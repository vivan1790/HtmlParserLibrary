package com.sample.htmlparser.tutorial

import com.google.gson.GsonBuilder
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface STTutorialContentApi {
    @GET("{subject}/{tutorial}")
    fun getTutorialContent(@Path("subject") subject : String,
                          @Path("tutorial") tutorial : String) : Single<String>

    @GET("{subject}/{tutorial}")
    fun getTutorialContent2(@Path("subject") subject : String,
                           @Path("tutorial") tutorial : String) : Call<String>
}

class STTutorialService {
    private val BASE_URL = "https://www.studytonight.com/"
    private val tutorialContentApi : STTutorialContentApi

    init {
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create())
                //.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        tutorialContentApi = retrofit.create(STTutorialContentApi:: class.java)
    }

    fun getTutorialContent(subject : String, tutorial : String) : Single<String>
            = tutorialContentApi.getTutorialContent(subject, tutorial)

    fun getTutorialContent2(subject : String, tutorial : String) : Call<String>
            = tutorialContentApi.getTutorialContent2(subject, tutorial)
}