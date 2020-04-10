package com.sample.htmlparser

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.library.htmlparser.HtmlContent
import com.library.htmlparser.HtmlParser
import com.library.htmlparser.codehighlight.CodeSyntaxTheme
import com.sample.htmlparser.article.ArticleContent
import com.sample.htmlparser.article.ArticleViewModel
import com.sample.htmlparser.test.TestViewModel
import com.sample.htmlparser.tutorial.TutorialViewModel

class ArticleContentActivity : AppCompatActivity(), HtmlParser.OnParsingListener {

    private lateinit var contentTypeSpinner : Spinner
    private lateinit var contentLayout: LinearLayout
    private lateinit var progressBar : ProgressBar

    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var tutorialViewModel: TutorialViewModel
    private lateinit var testViewModel: TestViewModel

    private lateinit var htmlParser: HtmlParser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_content)
        contentLayout = findViewById(R.id.content_layout)
        progressBar = findViewById(R.id.progressBar)

        htmlParser = HtmlParser(contentLayout)
        htmlParser.registerOnParsingListener(this)

        tutorialViewModel = ViewModelProvider(this).get(TutorialViewModel:: class.java)
        testViewModel = ViewModelProvider(this).get(TestViewModel:: class.java)
        articleViewModel = ViewModelProvider(this).get(ArticleViewModel:: class.java)

        // Studytonight tutorial
        // iframe example : https://www.studytonight.com/python/exception-handling-python
        // youtube example : https://www.studytonight.com/dbms/database-normalization
        val subject = "data-structures"
        val tutorial = "bubble-sort"
        //subscribeToSTTutorialModel(subject, tutorial)

        // Studytonight test
        val testSubject = "java"
        val testIndex = "1"
        //subscribeToSTTestModel(testSubject, testIndex)

        // Curious Article
        // 760, 289 : Text article .... 148 : Video article
        val bid = "760";
        //subscribeToCuriousArticleModel(bid)
        contentTypeSpinner = findViewById(R.id.contentTypeSpinner)
        contentTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                subscribeToSTTutorialModel(subject, tutorial)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                        position: Int, id: Long) {
                when (position) {
                    0 -> subscribeToSTTutorialModel(subject, tutorial)
                    1 -> subscribeToSTTestModel(testSubject, testIndex)
                    2 -> subscribeToCuriousArticleModel(bid)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        htmlParser.unRegisterOnParsingListener(this)
    }

    private fun subscribeToSTTutorialModel(subject : String, tutorial : String) {
        loadingInProgress(true)
        val codeSyntaxTheme = CodeSyntaxTheme.CodeSyntaxThemeBuilder(CodeSyntaxTheme.DARK)
            .withUnclassifiedColor(Color.RED)
            .build()
        tutorialViewModel.getTutorialContent(subject, tutorial)
        tutorialViewModel.tutorialContentLiveData.observe(this, Observer<String> {
            val htmlContent = HtmlContent.Builder(it)
                .withBaseUrl("https://www.studytonight.com/$subject")
                .withEndPoint(tutorial)
                .withInitialElementTagId("body-content")
                .withStyleToken("tutorial")
                .withCodeSyntaxTheme(codeSyntaxTheme)
                .build()
            htmlParser.parseHTMLContent(htmlContent)
        })
    }

    private fun subscribeToSTTestModel(subject : String, testIndex : String) {
        loadingInProgress(true)
        val codeSyntaxTheme = CodeSyntaxTheme.CodeSyntaxThemeBuilder(CodeSyntaxTheme.DARK)
            .withUnclassifiedColor(Color.RED)
            .build()
        val radioGroupClasses = HashSet<String>()
        radioGroupClasses.add("quiz")
        testViewModel.getTestContent(subject, testIndex)
        testViewModel.testContentLiveData.observe(this, Observer<String> {
            val htmlContent = HtmlContent.Builder(it)
                .withBaseUrl("https://www.studytonight.com/$subject/tests")
                .withEndPoint(testIndex)
                .withInitialElementTagId("quiz_container")
                .withStyleToken("test")
                .withCodeSyntaxTheme(codeSyntaxTheme)
                .withRadioGroupClasses(radioGroupClasses)
                .build()
            htmlParser.parseHTMLContent(htmlContent)
        })
    }

    private fun subscribeToCuriousArticleModel(bid : String) {
        loadingInProgress(true)
        val codeSyntaxTheme = CodeSyntaxTheme.CodeSyntaxThemeBuilder(CodeSyntaxTheme.DARK)
            .withUnclassifiedColor(Color.RED)
            .build()
        articleViewModel.getArticleContent(bid)
        articleViewModel.articleContentLiveData
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
                    .withStyleToken("article")
                    .withCodeSyntaxTheme(codeSyntaxTheme)
                    .build()
                htmlParser.parseHTMLContent(htmlContent)
            })
    }

    private fun loadingInProgress(loading : Boolean) {
        if (loading) {
            progressBar.visibility = View.VISIBLE
            contentLayout.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE
        }
    }

    override fun onParsingStarted(htmlParser: HtmlParser?) {

    }

    override fun onParsingFailed(htmlParser: HtmlParser?, errorMessage: String?) {
        loadingInProgress(false)
    }

    override fun onParsingSuccessful(htmlParser: HtmlParser?) {
        loadingInProgress(false)
        val imageUrls = htmlParser?.imageUrls
        if (imageUrls != null) {
            for (url in imageUrls) println("image url = * $url")
        }
        val viewGroups = htmlParser?.findViewGroupsByHtmlTagClassName("quiz_answer_holder")
        if (viewGroups != null) {
            for (viewGroup in viewGroups) {
                val textView = TextView(viewGroup.context)
                textView.text = """Inserted text ${viewGroups.indexOf(viewGroup)}"""
                viewGroup.addView(textView)
            }
        }
    }

}