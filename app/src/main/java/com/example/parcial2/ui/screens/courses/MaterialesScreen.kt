package com.example.parcial2.ui.screens.courses

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.parcial2.domain.model.Archivo
import com.example.parcial2.ui.components.DialogConfirmacion
import com.example.parcial2.ui.components.TarjetaArchivo
import com.example.parcial2.ui.theme.Parcial2Theme

/**
 * Pantalla para ver, subir, descargar y eliminar material de estudio de una unidad.
 */
@Composable
fun MaterialesScreen(
    tituloUnidad: String,
    viewModel: MaterialesViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Control para diálogo de borrado
    var archivoABorrar by remember { mutableStateOf<Archivo?>(null) }

    // Control para descarga
    var archivoADescargar by remember { mutableStateOf<Archivo?>(null) }

    // 1. Selector SAF para SUBIR archivos (OpenDocument permite elegir cualquier tipo de archivo)
    val launcherSubir = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            viewModel.subirArchivo(context, it)
        }
    }

    // 2. Selector SAF para EXPORTAR / BAJAR archivo (CreateDocument sugiere nombre)
    val launcherBajar = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uriDestino ->
        uriDestino?.let { destino ->
            archivoADescargar?.let { archivo ->
                viewModel.descargarArchivo(archivo, destino)
            }
        }
        archivoADescargar = null
    }

    // Mostrar mensajes en el Snackbar
    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.mensajeConsumido()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Botón CTA dorado para subir archivo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        // Abrimos cualquier tipo de documento (PDF, imágenes, etc.)
                        launcherSubir.launch(arrayOf("*/*"))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    enabled = !uiState.cargando
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = if (uiState.cargando) "Procesando..." else "Subir archivo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValores ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValores)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Material de Estudio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = tituloUnidad,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.cargando && uiState.archivos.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                uiState.archivos.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Aún no hay archivos adjuntos.",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = "Puedes subir resúmenes, apuntes o PDFs (máx 10 MB)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.archivos, key = { it.id }) { archivo ->
                            TarjetaArchivo(
                                archivo = archivo,
                                onDescargar = {
                                    archivoADescargar = archivo
                                    launcherBajar.launch(archivo.nombre)
                                },
                                onBorrar = {
                                    archivoABorrar = archivo
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para borrar archivo (reutilizando DialogConfirmacion)
    archivoABorrar?.let { archivo ->
        DialogConfirmacion(
            titulo = "Eliminar archivo",
            mensaje = "¿Estás seguro de que deseas eliminar permanentemente '${archivo.nombre}'?",
            textoConfirmar = "Eliminar",
            onConfirmar = {
                viewModel.borrarArchivo(archivo)
                archivoABorrar = null
            },
            onCancelar = {
                archivoABorrar = null
            }
        )
    }
}

// -------------------------------------------------------------
// Preview sin ViewModel para Android Studio
// -------------------------------------------------------------
@Composable
fun MaterialesContentPreview(
    archivos: List<Archivo>,
    cargando: Boolean = false
) {
    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text("Subir archivo")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Material de Estudio",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Unidad 1: Marco constitucional",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(archivos) { arch ->
                    TarjetaArchivo(
                        archivo = arch,
                        onDescargar = {},
                        onBorrar = {}
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MaterialesScreenPreview() {
    Parcial2Theme {
        MaterialesContentPreview(
            archivos = listOf(
                Archivo(1, 1, "Resumen_Constitucion_Art101.pdf", "/ruta/1", 1024 * 350, System.currentTimeMillis()),
                Archivo(2, 1, "Esquema_Detencion_Flagrancia.png", "/ruta/2", 1024 * 1024 * 2, System.currentTimeMillis() - 86400000)
            )
        )
    }
}
