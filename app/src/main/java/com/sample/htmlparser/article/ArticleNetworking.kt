package com.sample.htmlparser.article

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ArticleContentApi {

    @POST("curious-post")
    fun getArticleContent(@Header("Authorization") authHeader : String,
                          @Body body : HashMap<String, Any>) : Single<ArticleContent>


    @GET("curious-post/{bid}")
    fun getArticleContent(@Path("bid") bid : Int) : Single<ArticleContent>

}

class ArticleRepository {

    private val BASE_URL = "https://www.studytonight.com/core/api/"
    private val articleContentApi : ArticleContentApi

    init {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        articleContentApi = retrofit.create(ArticleContentApi:: class.java)
    }

    @SuppressLint("CheckResult")
    fun getArticleContent(authHeader : String, body : HashMap<String, Any>)
            : MutableLiveData<ArticleContent> {
        val contentLiveData : MutableLiveData<ArticleContent> = MutableLiveData()
        articleContentApi.getArticleContent(authHeader, body)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<ArticleContent>() {
                override fun onSuccess(articleContent : ArticleContent) {
                    contentLiveData.value = articleContent
                }

                override fun onError(e: Throwable) {
                    contentLiveData.value = null
                }
            })
        return contentLiveData
    }

    @SuppressLint("CheckResult")
    fun getArticleContent(bid : Int) : MutableLiveData<ArticleContent> {
        val contentLiveData : MutableLiveData<ArticleContent> = MutableLiveData()
        articleContentApi.getArticleContent(bid)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<ArticleContent>() {
                override fun onSuccess(articleContent : ArticleContent) {
                    contentLiveData.value = articleContent
                }

                override fun onError(e: Throwable) {
                    contentLiveData.value = null
                }
            })
        return contentLiveData
    }

}