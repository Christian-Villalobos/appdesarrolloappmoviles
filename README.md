# Quiz Carabineros

Aplicación móvil Android desarrollada en Kotlin con Jetpack Compose para el estudio y preparación de contenido jurídico institucional mediante cuestionarios, soporte híbrido (remoto con microservicio Spring Boot y respaldo local con SQLite), gestión de materiales de estudio y consulta de feriados legales.

---

## Funcionalidades Principales

- **Autenticación y Registro**:
  - Validación de correo institucional con dominio exclusivo `@carabineros.cl`.
  - Verificación de contraseña robusta (longitud mínima, mayúscula y número).
  - Manejo de sesión persistente mediante `SessionManager` (`EncryptedSharedPreferences`) y autenticación basada en tokens JWT (con expiración de 24 h y redirección al Login ante respuestas `401`).
- **Cursos, Unidades y Quizzes**:
  - Navegación estructurada: Cursos → Unidades → Quizzes.
  - Cuestionarios interactivos de selección múltiple (alternativas A, B, C y D).
  - Retroalimentación inmediata tras cada respuesta con justificación legal asociada.
  - Resumen al finalizar el quiz con porcentaje de aciertos y guardado de resultados.
- **Materiales de Estudio**:
  - Consulta, subida local y descarga de archivos de apoyo por unidad formativa (con límite de hasta 10 MB vía Storage Access Framework y persistencia interna).
- **Inicio (Home)**:
  - Saludo al usuario en sesión y consulta de los próximos feriados nacionales en Chile consumiendo la API externa de Nager.Date.
- **Perfil y Estadísticas**:
  - Visualización del historial de intentos realizados y cálculo de promedio general de rendimiento.
  - Cierre de sesión seguro con diálogo de confirmación.

---

## Arquitectura y Tecnologías

- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose con Material Design 3
- **Patrón de Arquitectura**: MVVM (Model-View-ViewModel) con `StateFlow`, corrutinas (`kotlinx.coroutines`) y ciclo de vida (`androidx.lifecycle`).
- **Navegación**: Jetpack Navigation Compose con definición tipada/cerrada en `NavRoutes`.
- **Capa de Red**:
  - Retrofit 2 + Gson Converter + OkHttp Logging Interceptor.
  - Interceptor OkHttp que inyecta automáticamente el token Bearer en los endpoints del microservicio backend.
- **Persistencia Local**:
  - SQLite nativo mediante `SQLiteOpenHelper` (`DbHelper`, `DbContract`, `SeedData`).
  - Patrón DAO (`UserDao`, `StudyDao`, `ArchivoDao`).
- **Estrategia Híbrida / Respaldo**:
  - Los repositorios intentan sincronizar y consumir datos desde el microservicio backend REST, recurriendo a la base de datos local SQLite en caso de fallos de red.

---

## Estructura de Paquetes (`app/src/main/java/com/example/parcial2`)

```text
com.example.parcial2
├── MainActivity.kt
├── data
│   ├── local
│   │   ├── SessionManager.kt             // Gestión de sesión y token en EncryptedSharedPreferences
│   │   ├── dao                           // Interfaces y queries SQLite nativas
│   │   │   ├── ArchivoDao.kt
│   │   │   ├── StudyDao.kt
│   │   │   └── UserDao.kt
│   │   └── db                            // Contrato, helper y precarga de SQLite
│   │       ├── DbContract.kt
│   │       ├── DbHelper.kt
│   │       └── SeedData.kt
│   ├── remote
│   │   ├── BackendApiService.kt          // Contrato REST del backend propio (Spring Boot)
│   │   ├── FeriadosApiService.kt         // API REST externa (date.nager.at)
│   │   ├── RetrofitClient.kt             // Cliente Retrofit con interceptor Bearer Token
│   │   └── dto                           // Data Transfer Objects para requests y responses
│   │       ├── BackendDtos.kt
│   │       └── FeriadoDto.kt
│   └── repository                        // Coordinación entre API remota, DAOs locales y validaciones
│       ├── ArchivoRepository.kt
│       ├── ResultadoAuth.kt
│       ├── StudyRepository.kt
│       ├── UserRepository.kt
│       └── Validaciones.kt
├── domain
│   └── model                             // Modelos de dominio
│       ├── Archivo.kt
│       ├── Curso.kt
│       ├── HistorialItem.kt
│       ├── Pregunta.kt
│       ├── Quiz.kt
│       ├── ResultadoQuiz.kt
│       ├── Unidad.kt
│       ├── UnidadConQuizzes.kt
│       └── Usuario.kt
└── ui
    ├── components                        // Componentes Compose comunes (TopBar, BottomBar, Campos, etc.)
    │   ├── AppBottomBar.kt
    │   ├── AppTopBar.kt
    │   ├── BotonPrincipal.kt
    │   ├── Campos.kt
    │   ├── DialogConfirmacion.kt
    │   ├── TarjetaArchivo.kt
    │   └── VistaError.kt
    ├── navigation                        // Grafo y rutas de navegación
    │   ├── AppNavGraph.kt
    │   └── NavRoutes.kt
    ├── screens                           // Pantallas y ViewModels por módulo
    │   ├── auth                          // Login y Registro
    │   ├── courses                       // Lista de Cursos, Detalle y Materiales
    │   ├── home                          // Vista inicial y feriados
    │   ├── profile                       // Perfil e historial
    │   └── quiz                          // Ejecución y resultado del cuestionario
    └── theme                             // Tema visual, tipografía y paleta de colores
```

---

## Pruebas Unitarias (`app/src/test/java/com/example/parcial2`)

El proyecto incluye tests unitarios para repositorios, validaciones, modelos y ViewModels utilizando JUnit 4, corrutinas de prueba y fakes para desacoplar el acceso a red:

- `ValidacionesTest`: Reglas de validación de nombres, correo institucional, contraseña y confirmación.
- `UserRepositoryTest`: Casos de éxito y error en registro e inicio de sesión frente a respuestas simuladas de la API.
- `ResultadoQuizTest`: Cálculo de propiedades como porcentaje de aciertos.
- `LoginViewModelTest`: Comportamiento reactivo, estados UI y validaciones del login.
- `HomeViewModelTest`: Carga de feriados y manejo de errores en Home.
- `QuizViewModelTest`: Flujo completo de respuesta a preguntas y finalización del quiz.
- `PerfilUiStateTest`: Estadísticas calculadas (total de intentos y promedio).
- `fakes/`: Implementaciones de prueba (`FakeBackendApi`, `FakeFeriadosApi`, `ErrorHttpFalso`).

Para ejecutar los tests unitarios:

```bash
./gradlew testDebugUnitTest
```

---

## Requisitos y Ejecución

- **Android SDK**: `minSdk 33`, `targetSdk 37`, `compileSdk 37`.
- **Java**: Versión 11.
- **Configuración local (`local.properties`)**:
  - Permite definir la URL base del microservicio `quiz-carabineros-backend` (puerto `8080`):
    - **Emulador Android:** `BASE_URL=http://10.0.2.2:8080/`
    - **Dispositivo físico (misma red Wi-Fi):** `BASE_URL=http://<IP_LOCAL_DE_TU_PC>:8080/`
  - **Alternativa si `10.0.2.2` da timeout (firewalls/antivirus o PCs de laboratorio):**
    1. Con el emulador abierto, ejecutar:
       ```bash
       adb reverse tcp:8080 tcp:8080
       ```
    2. Configurar en `local.properties`:
       ```properties
       BASE_URL=http://localhost:8080/
       ```
    *(Debe re-ejecutarse `adb reverse` cada vez que se reinicie el emulador).*
  - `AndroidManifest.xml` incluye `android:usesCleartextTraffic="true"` para permitir HTTP en desarrollo local.

---

## Autores

- Christian Villalobos
- Cristian Ferreira
