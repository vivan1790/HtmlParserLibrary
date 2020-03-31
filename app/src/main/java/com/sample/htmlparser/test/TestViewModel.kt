package com.sample.htmlparser.test

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Response

class TestViewModel : ViewModel() {
    lateinit var testContentLiveData : MutableLiveData<String>
    private val testRepository = STTestRepository()

    @SuppressLint("CheckResult")
    fun getTestContent(subject : String, testIndex : String) {
        testContentLiveData = testRepository.getTestContent(subject, testIndex)
    }
}