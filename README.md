# Quiz Carabineros

Aplicación móvil Android para estudiar con cuestionarios de contenido jurídico. Funciona 100 % sin conexión: los datos viven en una base de datos SQLite dentro del teléfono.

## Funcionalidades

- **Registro e inicio de sesión** con validaciones (correo institucional `@carabineros.cl`, contraseña segura).
- **Cursos → Unidades → Quizzes → Preguntas**, con datos precargados.
- **Quizzes** con alternativas A, B, C y D, porcentaje de aciertos y retroalimentación con justificación legal.
- **Perfil** con historial de resultados y estadísticas.
- **Cierre de sesión** con confirmación.

## Tecnologías

- Kotlin
- Jetpack Compose con Material 3
- Arquitectura MVVM con `StateFlow` y corrutinas
- SQLite nativo (`SQLiteOpenHelper`) con DAOs
- Navigation Compose

## Arquitectura

```
Pantalla (Compose) → ViewModel → Repositorio → DAO → SQLite
```

## Estructura de paquetes

```
com.example.parcial2
├── data
│   ├── local            SessionManager (sesión en memoria)
│   │   ├── db           DbHelper, DbContract, SeedData
│   │   └── dao          UserDao, StudyDao
│   └── repository       UserRepository, StudyRepository, Validaciones
├── domain.model         Modelos inmutables (Usuario, Curso, Quiz, Pregunta, ...)
└── ui
    ├── theme            Paleta institucional
    ├── components       Barras, diálogos y campos reutilizables
    ├── navigation       NavRoutes y AppNavGraph
    └── screens          auth, home, courses, quiz, profile
```

## Reglas de negocio

- Nombres y apellidos: mínimo 3 caracteres.
- Correo: debe terminar en `@carabineros.cl`.
- Contraseña: mínimo 6 caracteres, una mayúscula y un número; se guarda con hash SHA-256.
- Cerrar sesión siempre pide confirmación.

## Cómo ejecutar

1. Abrir el proyecto en Android Studio.
2. Esperar a que Gradle sincronice.
3. Ejecutar en un emulador o dispositivo con Android 13 (API 33) o superior.

## Nota sobre el contenido

Los cursos, preguntas y justificaciones legales son contenido de ejemplo. Las referencias legales deben verificarse contra el texto vigente.

## Autores

- Christian Villalobos
- Cristian Ferreira
