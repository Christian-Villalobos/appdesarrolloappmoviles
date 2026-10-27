package com.example.parcial2.ui.screens.courses

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial2.data.repository.ArchivoRepository
import com.example.parcial2.domain.model.Archivo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado inmutable de la pantalla de materiales de estudio.
 */
data class MaterialesUiState(
    val archivos: List<Archivo> = emptyList(),
    val cargando: Boolean = false,
    val mensaje: String? = null
)

/**
 * ViewModel para gestionar el material de estudio de una unidad.
 */
class MaterialesViewModel(
    private val unidadId: Int,
    private val archivoRepository: ArchivoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MaterialesUiState(cargando = true))
    val uiState: StateFlow<MaterialesUiState> = _uiState.asStateFlow()

    init {
        cargarArchivos()
    }

    fun cargarArchivos() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            val lista = archivoRepository.listarPorUnidad(unidadId)
            _uiState.update { it.copy(archivos = lista, cargando = false) }
        }
    }

    /**
     * Sube un archivo seleccionado con el selector SAF.
     */
    fun subirArchivo(context: Context, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }

            // Obtenemos el nombre y tamaño originales usando el ContentResolver
            var nombreOriginal = "archivo_${System.currentTimeMillis()}"
            var tamano = 0L

            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val idxNombre = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val idxTamano = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst()) {
                    if (idxNombre != -1) nombreOriginal = cursor.getString(idxNombre)
                    if (idxTamano != -1) tamano = cursor.getLong(idxTamano)
                }
            }

            val resultado = archivoRepository.subir(
                unidadId = unidadId,
                uriOrigen = uri,
                nombreOriginal = nombreOriginal,
                tamanoBytes = tamano
            )

            resultado.fold(
                onSuccess = {
                    val listaActualizada = archivoRepository.listarPorUnidad(unidadId)
                    _uiState.update {
                        it.copy(
                            archivos = listaActualizada,
                            cargando = false,
                            mensaje = "Archivo subido correctamente"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensaje = error.message ?: "Error al subir el archivo"
                        )
                    }
                }
            )
        }
    }

    /**
     * Exporta el archivo interno al destino elegido por el usuario.
     */
    fun descargarArchivo(archivo: Archivo, uriDestino: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            val resultado = archivoRepository.bajar(archivo, uriDestino)

            resultado.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensaje = "Archivo guardado exitosamente"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensaje = error.message ?: "Error al exportar el archivo"
                        )
                    }
                }
            )
        }
    }

    /**
     * Elimina el archivo físico y su registro en BD.
     */
    fun borrarArchivo(archivo: Archivo) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            val resultado = archivoRepository.borrar(archivo)

            resultado.fold(
                onSuccess = {
                    val listaActualizada = archivoRepository.listarPorUnidad(unidadId)
                    _uiState.update {
                        it.copy(
                            archivos = listaActualizada,
                            cargando = false,
                            mensaje = "Archivo eliminado"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensaje = error.message ?: "Error al eliminar el archivo"
                        )
                    }
                }
            )
        }
    }

    /**
     * Limpia el mensaje mostrado en el Snackbar.
     */
    fun mensajeConsumido() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
