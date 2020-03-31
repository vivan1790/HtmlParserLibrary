package com.sample.htmlparser.test

import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface STTestContentApi {

    @GET("{subject}/tests/{testIndex}")
    fun getTestContent(@Path("subject") subject : String,
                           @Path("testIndex") testIndex : String) : Call<String>
}

class STTestRepository {
    private val BASE_URL = "https://www.studytonight.com/"
    private val testContentApi : STTestContentApi

    init {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        testContentApi = retrofit.create(STTestContentApi:: class.java)
    }

    fun getTestContent(subject : String, testIndex : String) : MutableLiveData<String> {
        val contentLiveData : MutableLiveData<String> = MutableLiveData()
        testContentApi.getTestContent(subject, testIndex)
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