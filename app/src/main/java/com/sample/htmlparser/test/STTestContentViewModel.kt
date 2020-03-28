package com.sample.htmlparser.test

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sample.htmlparser.test.STTestService
import retrofit2.Call
import retrofit2.Response

class STTestContentViewModel : ViewModel() {
    val testContentLiveData : MutableLiveData<String> = MutableLiveData()
    val testLoadErrorLiveData : MutableLiveData<Boolean> = MutableLiveData()
    private val testContentService = STTestService()

    @SuppressLint("CheckResult")
    fun getTestContent(subject : String, testIndex : String) {
        testContentService.getTestContent(subject, testIndex)
                .enqueue(object : retrofit2.Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        //println("VIVAN error is ${t.localizedMessage} ... ${t.message}")
                        testLoadErrorLiveData.value = true
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        //println("VIVAN message is ${response.message()} body is ${response.body()?.length}  ... ${response.isSuccessful}")
                        testContentLiveData.value = response?.body()
                        testLoadErrorLiveData.value = false
                    }

                })
    }
}