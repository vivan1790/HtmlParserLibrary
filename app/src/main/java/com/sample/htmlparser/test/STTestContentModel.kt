package com.sample.htmlparser.test

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface STTestContentApi {

    @GET("{subject}/tests/{testIndex}")
    fun getTestContent(@Path("subject") subject : String,
                           @Path("testIndex") testIndex : String) : Call<String>
}

class STTestService {
    private val BASE_URL = "https://www.studytonight.com/"
    private val testContentApi : STTestContentApi

    init {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        testContentApi = retrofit.create(STTestContentApi:: class.java)
    }

    fun getTestContent(subject : String, testIndex : String) : Call<String>
            = testContentApi.getTestContent(subject, testIndex)
}