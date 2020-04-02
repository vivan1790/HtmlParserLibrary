package com.sample.htmlparser

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.library.htmlparser.HtmlContent
import com.library.htmlparser.HtmlParserView
import com.library.htmlparser.codehighlight.CodeSyntaxTheme
import com.sample.htmlparser.article.ArticleContent
import com.sample.htmlparser.article.ArticleViewModel
import com.sample.htmlparser.test.TestViewModel
import com.sample.htmlparser.tutorial.TutorialViewModel

class ArticleContentActivity : AppCompatActivity(),
    HtmlParserView.OnParsingListener {

    private lateinit var htmlParserView: HtmlParserView
    private lateinit var debugTextView : TextView
    private lateinit var scaleTextButton : View
    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var tutorialViewModel: TutorialViewModel
    private lateinit var testViewModel: TestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_content)
        htmlParserView = findViewById(R.id.html_parser_view)
        htmlParserView.registerOnParsingListener(this)
        debugTextView = findViewById(R.id.debug_view)
        scaleTextButton = findViewById(R.id.scaleTextButton)
        scaleTextButton.setOnClickListener {
            htmlParserView.scaleTextSize(1.5f)
        }
        tutorialViewModel = ViewModelProvider(this).get(TutorialViewModel:: class.java)
        testViewModel = ViewModelProvider(this).get(TestViewModel:: class.java)
        articleViewModel = ViewModelProvider(this).get(ArticleViewModel:: class.java)

        // Studytonight tutorial
        // iframe example : https://www.studytonight.com/python/exception-handling-python
        // youtube example : https://www.studytonight.com/dbms/database-normalization

        val subject = "data-structures"
        val tutorial = "bubble-sort"
        subscribeToSTTutorialModel(subject, tutorial)

        // Studytonight test
//        val testSubject = "java"
//        val testIndex = "1"
//        subscribeToSTTestModel(testSubject, testIndex)

        // Curious Article
        // 760, 289 : Text article
        // 148 : Video article
        //val bid = "148";
        //subscribeToCuriousArticleModel(bid)
    }

    override fun onDestroy() {
        super.onDestroy()
        htmlParserView.unRegisterOnParsingListener(this)
    }

    private fun subscribeToSTTutorialModel(subject : String, tutorial : String) {
        val codeSyntaxTheme = CodeSyntaxTheme.CodeSyntaxThemeBuilder(CodeSyntaxTheme.DARK)
            .withUnclassifiedColor(Color.RED)
            .build()
        tutorialViewModel.getTutorialContent(subject, tutorial)
        tutorialViewModel.tutorialContentLiveData.observe(this, Observer<String> {
            htmlParserView.clear()
            val htmlContent = HtmlContent.Builder(it)
                .withBaseUrl("https://www.studytonight.com/$subject")
                .withEndPoint(tutorial)
                .withInitialElementTagId("body-content")
                .withStyleToken("tutorial")
                .withCodeSyntaxTheme(codeSyntaxTheme)
                .build()
            htmlParserView.parseHTMLContent(htmlContent)
        })
    }

    private fun subscribeToSTTestModel(subject : String, testIndex : String) {
        val codeSyntaxTheme = CodeSyntaxTheme.CodeSyntaxThemeBuilder(CodeSyntaxTheme.DARK)
            .withUnclassifiedColor(Color.RED)
            .build()
        val radioGroupClasses = HashSet<String>()
        radioGroupClasses.add("quiz")
        testViewModel.getTestContent(subject, testIndex)
        testViewModel.testContentLiveData.observe(this, Observer<String> {
            htmlParserView.clear()
            val htmlContent = HtmlContent.Builder(it)
                .withBaseUrl("https://www.studytonight.com/$subject/tests")
                .withEndPoint(testIndex)
                .withInitialElementTagId("quiz_container")
                .withStyleToken("test")
                .withCodeSyntaxTheme(codeSyntaxTheme)
                .withRadioGroupClasses(radioGroupClasses)
                .build()
            htmlParserView.parseHTMLContent(htmlContent)
        })
    }

    private fun subscribeToCuriousArticleModel(bid : String) {
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
                htmlParserView.parseHTMLContent(htmlContent)
            })
    }

    override fun onParsingStarted(parserView: HtmlParserView?) {
    }

    override fun onParsingFailed(parserView: HtmlParserView?, errorMessage: String?) {
    }

    override fun onParsingSuccessful(parserView: HtmlParserView?) {
        val imageUrls = parserView?.imageUrls
        if (imageUrls != null) {
            for (url in imageUrls) println("image url = * $url")
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