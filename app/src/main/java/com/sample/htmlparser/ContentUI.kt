package com.sample.htmlparser

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.library.htmlparser.HtmlContent
import com.library.htmlparser.HtmlParserView
import com.library.htmlparser.codehighlight.CodeSyntaxTheme
import com.sample.htmlparser.article.ArticleContent
import com.sample.htmlparser.article.ArticleContentViewModel
import com.sample.htmlparser.test.STTestContentViewModel
import com.sample.htmlparser.tutorial.STTutorialContentViewModel

class ArticleContentActivity : AppCompatActivity(),
    HtmlParserView.OnParsingListener {

    companion object {
        fun getIntent(context : Context) : Intent =
                Intent(context, ArticleContentActivity :: class.java)
    }

    private lateinit var htmlParserView: HtmlParserView
    private lateinit var debugTextView : TextView
    private lateinit var articleContentViewModel: ArticleContentViewModel
    private lateinit var stTutorialContentViewModel: STTutorialContentViewModel
    private lateinit var stTestContentViewModel: STTestContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_content)
        htmlParserView = findViewById(R.id.html_parser_view)
        debugTextView = findViewById(R.id.debug_view)
        initHtmlParserView()

        // Studytonight tutorial
        // iframe example : https://www.studytonight.com/python/exception-handling-python
        // youtube example : https://www.studytonight.com/dbms/database-normalization

        val subject = "c"
        val tutorial = "pointers-to-structure-in-c.php"
        subscribeToSTTutorialModel(subject, tutorial)

        // Studytonight test
        val testSubject = "java"
        val testIndex = "1"
        //subscribeToSTTestModel(testSubject, testIndex)

        // Curious Article
        // 760, 289 : Text article
        // 148 : Video article
        val bid = "148";
        //subscribeToCuriousArticleModel(bid)
    }

    override fun onDestroy() {
        super.onDestroy()
        htmlParserView.unRegisterOnParsingListener(this)
    }

    private fun initHtmlParserView() {
        htmlParserView.setCodeSyntaxTheme(
            CodeSyntaxTheme.CodeSyntaxThemeBuilder(CodeSyntaxTheme.DARK)
                .withUnclassifiedColor(Color.RED)
                .build())
        val radioGroupClasses = HashSet<String>()
        radioGroupClasses.add("quiz")
        htmlParserView.radioGroupClasses = radioGroupClasses
    }

    private fun subscribeToSTTutorialModel(subject : String, tutorial : String) {
        htmlParserView.registerOnParsingListener(this)
        stTutorialContentViewModel = ViewModelProviders.of(this)
                .get(STTutorialContentViewModel:: class.java)
        stTutorialContentViewModel.getTutorialContent(subject, tutorial)
        stTutorialContentViewModel.tutorialContentLiveData.observe(this, Observer<String> {
            htmlParserView.clear()
            val htmlContent = HtmlContent.Builder(it)
                .withBaseUrl("https://www.studytonight.com/$subject")
                .withEndPoint(tutorial)
                .withInitialElementTagId("body-content")
                .build()
            htmlParserView.parseHTMLContent(htmlContent)
        })
        stTutorialContentViewModel.tutorialLoadErrorLiveData.observe(this, Observer<Boolean> {
            if (it) {
                Toast.makeText(this,
                        getString(R.string.content_load_error), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun subscribeToSTTestModel(subject : String, testIndex : String) {
        htmlParserView.registerOnParsingListener(this)
        stTestContentViewModel = ViewModelProviders.of(this)
            .get(STTestContentViewModel:: class.java)
        stTestContentViewModel.getTestContent(subject, testIndex)
        stTestContentViewModel.testContentLiveData.observe(this, Observer<String> {
            htmlParserView.clear()
            val htmlContent = HtmlContent.Builder(it)
                .withBaseUrl("https://www.studytonight.com/$subject/tests")
                .withEndPoint(testIndex)
                .withInitialElementTagId("quiz_container")
                .build()
            htmlParserView.parseHTMLContent(htmlContent)
        })
        stTestContentViewModel.testLoadErrorLiveData.observe(this, Observer<Boolean> {
            if (it) {
                Toast.makeText(this,
                    getString(R.string.content_load_error), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun subscribeToCuriousArticleModel(bid : String) {
        articleContentViewModel = ViewModelProviders.of(this)
            .get(ArticleContentViewModel:: class.java)
        articleContentViewModel.getArticleContent(bid)
        articleContentViewModel.articleContentLiveData
            .observe(this, Observer<ArticleContent> {
                val articleBody : String
                articleBody = if ("V" == it.articleType) {
                    val videoId = it.articleBody.replace("https://www.youtube.com/watch?v=", "")
                    val embedUrl = "https://www.youtube.com/embed/${videoId}"
                    "<iframe src=\"${embedUrl}\" width=\"100%\" height=\"500\">"
                } else {
                    it.articleBody
                }
                val htmlContent = HtmlContent.Builder(articleBody)
                    .build()
                htmlParserView.parseHTMLContent(htmlContent)

            })
        articleContentViewModel.contentLoadErrorLiveData
            .observe(this, Observer<Boolean> {
                if (it) {
                    Toast.makeText(this,
                        getString(R.string.content_load_error), Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onParsingStarted(parserView: HtmlParserView?) {
    }

    override fun onParsingFailed(parserView: HtmlParserView?, errorMessage: String?) {
    }

    override fun onParsingSuccessful(parserView: HtmlParserView?) {
        val imageUrls = parserView?.imageUrls
        if (imageUrls != null) {
            for (url in imageUrls) println("VIVAN VIVAN * $url")
        }
        val viewGroups = parserView?.findViewGroupsByHtmlTagClassName("quiz_answer_holder")
        if (viewGroups != null) {
            for (viewGroup in viewGroups) {
                val textView = TextView(parserView.context)
                textView.text = """Inserted text ${viewGroups.indexOf(viewGroup)}"""
                viewGroup.addView(textView)
            }
        }
    }

}