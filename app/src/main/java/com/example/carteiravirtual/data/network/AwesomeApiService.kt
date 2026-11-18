package com.example.carteiravirtual.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface AwesomeApiService {
    @GET("last/{par}")
    suspend fun getCotacao(@Path("par") par: String): Map<String, CotacaoMoeda>

    companion object {
        private const val BASE_URL = "https://economia.awesomeapi.com.br/"

        fun create(): AwesomeApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(AwesomeApiService::class.java)
        }
    }
}

