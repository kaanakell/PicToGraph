package com.example.cameraeae

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST


interface ApiInterface {

    @Multipart
    @POST("upload")
    @Headers(
        "Accept: multipart/form-data",
        "User-Agent: Thunder Client (https://www.thunderclient.com)",
        "X-EAE-Auth: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiTWluZERyYXdlciIsImRldmljZW1hYyI6IlRlc3QiLCJleHAiOjE2NjQ0Mjk4OTR9.Rs-vzf6vFsJSvmw22YcP-jS3jtlg__R7qIYOgXjRUPw")
    fun uploadImage(
        @Field("image") image: String?
    ): Call<ApiResponse>


    operator fun invoke(): ApiInterface{
        return Retrofit.Builder()
            .baseUrl("http://ec2-52-57-246-10.eu-central-1.compute.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
    }

