package com.sample.htmlparser.tutorial

import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface STTutorialContentApi {

    @GET("{subject}/{tutorial}")
    fun getTutorialContent(@Path("subject") subject : String,
                           @Path("tutorial") tutorial : String) : Call<String>
}

class STTutorialRepository {
    private val BASE_URL = "https://www.studytonight.com/"
    private val tutorialContentApi : STTutorialContentApi

    init {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        tutorialContentApi = retrofit.create(STTutorialContentApi:: class.java)
    }

    fun getTutorialContent(subject : String, tutorial : String) : MutableLiveData<String> {
        val contentLiveData : MutableLiveData<String> = MutableLiveData()
        tutorialContentApi.getTutorialContent(subject, tutorial)
            .enqueue(object : retrofit2.Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    contentLiveData.value = null
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    contentLiveData.value = response.body()
                }
            })
        return contentLiveData
    }
}