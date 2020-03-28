package com.sample.htmlparser.article

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Base64.encodeToString
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*

class ArticleContentViewModel : ViewModel() {

    val articleContentLiveData : MutableLiveData<ArticleContent> = MutableLiveData()
    val contentLoadErrorLiveData : MutableLiveData<Boolean> = MutableLiveData()
    private val articleContentService =
        ArticleContentService()

    @SuppressLint("CheckResult")
    fun getArticleContent(bid : String) {
        val basicAuthHeader = "Basic " + encodeToString(
                "vivan.verma:blueGuitar3#".toByteArray(), Base64.NO_WRAP)
        val body = HashMap<String, Any>()
        body["bid"] = bid
        articleContentService.getArticleContent(bid.toInt())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ArticleContent>() {
                    override fun onSuccess(articleContent : ArticleContent) {
                        articleContentLiveData.value = articleContent
                        contentLoadErrorLiveData.value = false
                    }

                    override fun onError(e: Throwable) {
                        contentLoadErrorLiveData.value = true
                    }

                })
    }

}