package com.example.parcial2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.parcial2.data.local.dao.ArchivoDao
import com.example.parcial2.data.local.dao.StudyDao
import com.example.parcial2.data.local.db.DbHelper
import com.example.parcial2.data.repository.ArchivoRepository
import com.example.parcial2.data.repository.StudyRepository
import com.example.parcial2.data.repository.UserRepository
import com.example.parcial2.ui.navigation.AppNavGraph
import com.example.parcial2.ui.theme.Parcial2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Armamos las capas de datos: Helper -> DAO -> Repositorio
        val dbHelper = DbHelper.obtener(applicationContext)
        val userRepository = UserRepository()
        val studyRepository = StudyRepository(StudyDao(dbHelper))

        // Materiales: el repositorio necesita el Context para copiar archivos en filesDir
        val archivoRepository = ArchivoRepository(applicationContext, ArchivoDao(dbHelper))

        setContent {
            Parcial2Theme {
                AppNavGraph(
                    userRepository = userRepository,
                    studyRepository = studyRepository,
                    archivoRepository = archivoRepository
                )
            }
        }
    }
}