package com.example.parcial2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.parcial2.data.local.dao.UserDao
import com.example.parcial2.data.local.db.DbHelper
import com.example.parcial2.data.repository.UserRepository
import com.example.parcial2.ui.navigation.AppNavGraph
import com.example.parcial2.ui.theme.Parcial2Theme
import com.example.parcial2.data.local.dao.StudyDao
import com.example.parcial2.data.repository.StudyRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Armamos las capas de datos: Helper -> DAO -> Repositorio
        val dbHelper = DbHelper.obtener(applicationContext)
        val userRepository = UserRepository(UserDao(dbHelper))
        val studyRepository = StudyRepository(StudyDao(dbHelper))


        setContent {
            Parcial2Theme {
                AppNavGraph(
                    userRepository = userRepository,
                    studyRepository = studyRepository
                )
            }
        }
    }
}