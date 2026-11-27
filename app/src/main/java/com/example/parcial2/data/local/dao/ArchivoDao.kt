package com.example.parcial2.data.local.dao

import android.content.ContentValues
import com.example.parcial2.data.local.db.DbContract.Archivos
import com.example.parcial2.data.local.db.DbHelper
import com.example.parcial2.domain.model.Archivo

/**
 * Operaciones directas en la tabla 'archivos' de SQLite.
 * Sigue el mismo estilo simple de UserDao.
 */
class ArchivoDao(private val dbHelper: DbHelper) {

    // Inserta un archivo. Retorna el ID autogenerado o -1 si falla.
    fun insertar(archivo: Archivo): Long {
        val valores = ContentValues().apply {
            put(Archivos.UNIDAD_ID, archivo.unidadId)
            put(Archivos.NOMBRE, archivo.nombre)
            put(Archivos.RUTA_INTERNA, archivo.rutaInterna)
            put(Archivos.TAMANO_BYTES, archivo.tamanoBytes)
            put(Archivos.FECHA, archivo.fecha)
        }
        return dbHelper.writableDatabase.insert(Archivos.TABLA, null, valores)
    }

    // Lista todos los archivos asociados a una unidad, ordenados del más reciente al más antiguo.
    fun listarPorUnidad(unidadId: Int): List<Archivo> {
        val lista = mutableListOf<Archivo>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            Archivos.TABLA,
            null,
            "${Archivos.UNIDAD_ID} = ?",
            arrayOf(unidadId.toString()),
            null,
            null,
            "${Archivos.FECHA} DESC"
        )

        cursor.use { c ->
            val idxId = c.getColumnIndexOrThrow(Archivos.ID)
            val idxUnidad = c.getColumnIndexOrThrow(Archivos.UNIDAD_ID)
            val idxNombre = c.getColumnIndexOrThrow(Archivos.NOMBRE)
            val idxRuta = c.getColumnIndexOrThrow(Archivos.RUTA_INTERNA)
            val idxTamano = c.getColumnIndexOrThrow(Archivos.TAMANO_BYTES)
            val idxFecha = c.getColumnIndexOrThrow(Archivos.FECHA)

            while (c.moveToNext()) {
                lista.add(
                    Archivo(
                        id = c.getInt(idxId),
                        unidadId = c.getInt(idxUnidad),
                        nombre = c.getString(idxNombre),
                        rutaInterna = c.getString(idxRuta),
                        tamanoBytes = c.getLong(idxTamano),
                        fecha = c.getLong(idxFecha)
                    )
                )
            }
        }
        return lista
    }

    // Busca un archivo por su clave primaria ID.
    fun buscarPorId(id: Int): Archivo? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            Archivos.TABLA,
            null,
            "${Archivos.ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        return cursor.use { c ->
            if (c.moveToFirst()) {
                Archivo(
                    id = c.getInt(c.getColumnIndexOrThrow(Archivos.ID)),
                    unidadId = c.getInt(c.getColumnIndexOrThrow(Archivos.UNIDAD_ID)),
                    nombre = c.getString(c.getColumnIndexOrThrow(Archivos.NOMBRE)),
                    rutaInterna = c.getString(c.getColumnIndexOrThrow(Archivos.RUTA_INTERNA)),
                    tamanoBytes = c.getLong(c.getColumnIndexOrThrow(Archivos.TAMANO_BYTES)),
                    fecha = c.getLong(c.getColumnIndexOrThrow(Archivos.FECHA))
                )
            } else null
        }
    }

    // Elimina el registro por ID. Retorna true si borró al menos 1 fila.
    fun eliminar(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val filasAfectadas = db.delete(
            Archivos.TABLA,
            "${Archivos.ID} = ?",
            arrayOf(id.toString())
        )
        return filasAfectadas > 0
    }
}
