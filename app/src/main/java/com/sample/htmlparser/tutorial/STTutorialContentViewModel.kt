package com.sample.htmlparser.tutorial

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.htmlparser.tutorial.STTutorialService
import retrofit2.Call
import retrofit2.Response

class STTutorialContentViewModel : ViewModel() {
    val tutorialContentLiveData : MutableLiveData<String> = MutableLiveData()
    val tutorialLoadErrorLiveData : MutableLiveData<Boolean> = MutableLiveData()
    private val tutorialContentService = STTutorialService()

    @SuppressLint("CheckResult")
    fun getTutorialContent(subject : String, tutorial : String) {
        tutorialContentService.getTutorialContent2(subject, tutorial)
                .enqueue(object : retrofit2.Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        //println("VIVAN error is ${t.localizedMessage} ... ${t.message}")
                        tutorialLoadErrorLiveData.value = true
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        //println("VIVAN message is ${response.message()} body is ${response.body()?.length}  ... ${response.isSuccessful}")
                        tutorialContentLiveData.value = response.body()
                        tutorialLoadErrorLiveData.value = false
                    }
                })
    }
}