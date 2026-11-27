package com.example.parcial2.data.repository

import android.content.Context
import android.net.Uri
import com.example.parcial2.data.local.dao.ArchivoDao
import com.example.parcial2.domain.model.Archivo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Repositorio de materiales de estudio:
 * Administra la copia física en filesDir/materiales y la persistencia en BD.
 */
class ArchivoRepository(
    private val context: Context,
    private val archivoDao: ArchivoDao
) {

    companion object {
        // Límite máximo: 10 Megabytes
        const val MAX_BYTES = 10L * 1024 * 1024
    }

    // Carpeta privada interna: /data/user/0/com.example.parcial2/files/materiales
    private val carpetaMateriales: File by lazy {
        File(context.filesDir, "materiales").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Lista los archivos de una unidad desde la base de datos en segundo plano.
     */
    suspend fun listarPorUnidad(unidadId: Int): List<Archivo> = withContext(Dispatchers.IO) {
        archivoDao.listarPorUnidad(unidadId)
    }

    /**
     * Copia el archivo desde el Uri seleccionado a la carpeta privada y guarda el registro.
     */
    suspend fun subir(
        unidadId: Int,
        uriOrigen: Uri,
        nombreOriginal: String,
        tamanoBytes: Long
    ): Result<Archivo> = withContext(Dispatchers.IO) {
        try {
            // 1. Validación de tamaño
            if (tamanoBytes > MAX_BYTES) {
                return@withContext Result.failure(Exception("El archivo supera el límite de 10 MB"))
            }

            // 2. Generar nombre de archivo único para evitar colisiones en disco
            val timestamp = System.currentTimeMillis()
            val extension = nombreOriginal.substringAfterLast('.', "")
            val nombreBase = nombreOriginal.substringBeforeLast('.')
                .replace("[^a-zA-Z0-9_-]".toRegex(), "_")
            val nombreGuardado = if (extension.isNotBlank()) {
                "${nombreBase}_$timestamp.$extension"
            } else {
                "${nombreBase}_$timestamp"
            }

            val destinoFisico = File(carpetaMateriales, nombreGuardado)

            // 3. Copiar bytes desde el SAF al almacenamiento interno
            val contentResolver = context.contentResolver
            contentResolver.openInputStream(uriOrigen)?.use { entrada ->
                FileOutputStream(destinoFisico).use { salida ->
                    entrada.copyTo(salida)
                }
            } ?: return@withContext Result.failure(Exception("No se pudo leer el archivo seleccionado"))

            // 4. Verificar tamaño real en disco tras la copia
            val tamanoReal = destinoFisico.length()
            if (tamanoReal > MAX_BYTES) {
                destinoFisico.delete()
                return@withContext Result.failure(Exception("El archivo supera el límite de 10 MB"))
            }

            // 5. Guardar en SQLite
            val nuevoArchivo = Archivo(
                id = 0,
                unidadId = unidadId,
                nombre = nombreOriginal,
                rutaInterna = destinoFisico.absolutePath,
                tamanoBytes = tamanoReal,
                fecha = timestamp
            )

            val idGenerado = archivoDao.insertar(nuevoArchivo)
            if (idGenerado == -1L) {
                // Si la BD falla, limpiamos el archivo físico para no dejar basura
                destinoFisico.delete()
                return@withContext Result.failure(Exception("Error al registrar el archivo en la base de datos"))
            }

            Result.success(nuevoArchivo.copy(id = idGenerado.toInt()))
        } catch (e: Exception) {
            Result.failure(Exception("Error al guardar archivo: ${e.message}"))
        }
    }

    /**
     * Exporta el archivo interno hacia el Uri destino elegido por el usuario (SAF CreateDocument).
     */
    suspend fun bajar(archivo: Archivo, uriDestino: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val origenFisico = File(archivo.rutaInterna)
            if (!origenFisico.exists()) {
                return@withContext Result.failure(Exception("El archivo no se encuentra en el dispositivo"))
            }

            val contentResolver = context.contentResolver
            contentResolver.openOutputStream(uriDestino)?.use { salida ->
                origenFisico.inputStream().use { entrada ->
                    entrada.copyTo(salida)
                }
            } ?: return@withContext Result.failure(Exception("No se pudo escribir en el destino seleccionado"))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al exportar archivo: ${e.message}"))
        }
    }

    /**
     * Borrado seguro: primero borra el archivo en disco, luego el registro en SQLite.
     */
    suspend fun borrar(archivo: Archivo): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Borramos el archivo físico
            val archivoFisico = File(archivo.rutaInterna)
            if (archivoFisico.exists()) {
                archivoFisico.delete()
            }

            // 2. Borramos de la BD
            val eliminado = archivoDao.eliminar(archivo.id)
            if (eliminado) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo eliminar el registro de la base de datos"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al borrar archivo: ${e.message}"))
        }
    }
}
