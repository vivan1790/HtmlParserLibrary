package com.sample.htmlparser.article

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class ArticleContent {

    @SerializedName("bid")
    lateinit var articleId : String

    @SerializedName("user_id_fk")
    lateinit var userId : String

    @SerializedName("title")
    lateinit var articleTitle : String

    @SerializedName("body")
    lateinit var articleBody : String

    @SerializedName("category")
    lateinit var articleCategory : String

    @SerializedName("subcategory")
    lateinit var articleSubCategory : String

    @SerializedName("tags")
    lateinit var articleTags : String

    @SerializedName("url")
    lateinit var articleUrl : String

    @SerializedName("type")
    lateinit var articleType : String

    @SerializedName("featured_pic")
    lateinit var articleFeaturedPicUrl : String

    @SerializedName("ctc")
    lateinit var articleCTC : String

    @SerializedName("read_time")
    lateinit var articleReadTime : String

    @SerializedName("sponsored")
    lateinit var articleSponsored : String

    @SerializedName("created")
    lateinit var articleCreateTime : String

    @SerializedName("meta_desc")
    lateinit var authorMetaDesc : String

}

interface ArticleContentApi {

    @POST("curious-post")
    fun getArticleContent(@Header("Authorization") authHeader : String,
                          @Body body : HashMap<String, Any>) : Single<ArticleContent>


    @GET("curious-post/{bid}")
    fun getArticleContent(@Path("bid") bid : Int) : Single<ArticleContent>

}

class ArticleContentService {

    private val BASE_URL = "https://www.studytonight.com/core/api/"
    private val articleContentApi : ArticleContentApi

    init {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        articleContentApi = retrofit.create(ArticleContentApi:: class.java)
    }

    fun getArticleContent(authHeader : String, body : HashMap<String, Any>) : Single<ArticleContent>
            = articleContentApi.getArticleContent(authHeader, body)

    fun getArticleContent(bid : Int) : Single<ArticleContent>
            = articleContentApi.getArticleContent(bid)

}