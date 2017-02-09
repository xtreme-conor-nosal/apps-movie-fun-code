package org.superbiz.moviefun.restsupport

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import kotlin.reflect.KClass

class RestTemplate {

    val client = OkHttpClient()
    val objectMapper = ObjectMapper()
    val JSON = MediaType.parse("application/json; charset=utf-8")!!

    fun get(url: String): RestResult<String> {
        return get(url, { response ->
            response.body().string()
        })
    }

    fun <T : Any> get(url: String, klass: KClass<T>): RestResult<T> {
        return get(url, { response ->
            objectMapper.readValue(response.body().byteStream(), klass.java)
        })
    }

    fun <T : Any, U : Any> post(url: String, body: U, klass: KClass<T>): String {
        val jsonBody = objectMapper.writeValueAsString(body)
        return post(url, jsonBody)
    }

    private fun <T : Any> get(url: String, successHandler: (Response) -> T): RestResult<T> {
        val request = Request.Builder()
            .get()
            .url(url)
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            return RestResult.Success(successHandler(response))
        }

        return RestResult.Error(response.message())
    }

    private fun post(url: String, json: String): String {
        val body = RequestBody.create(JSON, json)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        return response.body().string()
    }

    sealed class RestResult<T> {
        class Success<T>(val value: T) : RestResult<T>()
        class Error<T>(val error: String) : RestResult<T>()
    }
}