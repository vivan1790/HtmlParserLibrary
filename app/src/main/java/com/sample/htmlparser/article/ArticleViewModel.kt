package com.sample.htmlparser.article

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Base64.encodeToString
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class ArticleViewModel : ViewModel() {

    lateinit var articleContentLiveData : MutableLiveData<ArticleContent>
    private val articleRepository = ArticleRepository()

    @SuppressLint("CheckResult")
    fun getArticleContent(bid : String) {
        val basicAuthHeader = "Basic " + encodeToString(
                "vivan.verma:blueGuitar3#".toByteArray(), Base64.NO_WRAP)
        val body = HashMap<String, Any>()
        body["bid"] = bid
        articleContentLiveData = articleRepository.getArticleContent(bid.toInt())
    }

}