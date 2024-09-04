package com.hezae.hezae_final_ex_android.ui.home

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _openFileButtonText = MutableLiveData<String>().apply {
        value = "打开文件"
    }
    public val openFileButtonText: LiveData<String> = _openFileButtonText

    // 打开文件按钮点击事件
    fun onOpenFileButtonClicked() {
        if(openFileButtonText.value == "文件已打开"){
            _openFileButtonText.value = "文件已关闭"
        }else{
            _openFileButtonText.value = "文件已打开"
        }
    }
}