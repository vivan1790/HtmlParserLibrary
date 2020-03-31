package com.sample.htmlparser.article

import com.google.gson.annotations.SerializedName

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