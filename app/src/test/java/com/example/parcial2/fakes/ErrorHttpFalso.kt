package com.example.parcial2.fakes

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

// Fabrica el error que Retrofit lanza cuando el servidor responde 401, 404, 500, etc.
fun crearErrorHttp(codigo: Int, cuerpo: String = ""): HttpException {
    val body = cuerpo.toResponseBody("application/json".toMediaType())
    return HttpException(Response.error<Any>(codigo, body))
}