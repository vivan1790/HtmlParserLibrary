package com.sample.htmlparser.tutorial

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TutorialViewModel : ViewModel() {

    lateinit var tutorialContentLiveData : MutableLiveData<String>
    private var tutorialRepository = STTutorialRepository()

    @SuppressLint("CheckResult")
    fun getTutorialContent(subject : String, tutorial : String) {
        if (subject.contains('/')) {
            val sub = subject.split("/")
            tutorialContentLiveData = tutorialRepository.getTutorialContent(sub[0], sub[1], tutorial)
        } else {
            tutorialContentLiveData = tutorialRepository.getTutorialContent(subject, tutorial)
        }
    }
}