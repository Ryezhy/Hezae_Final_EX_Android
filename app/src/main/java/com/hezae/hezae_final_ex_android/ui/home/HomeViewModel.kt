package com.hezae.hezae_final_ex_android.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class HomeViewModel : ViewModel() {

    val AUTHORITY = "com.cs.camerademo.fileProvider" //FileProvider的签名(后面会介绍)
    val REQUEST_CODE_CAPTURE_RAW = 6 //startActivityForResult时的请求码
    var imageFile: File? = null     //拍照后保存的照片
    var imgUri: Uri? = null         //拍照后保存的照片的uri

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}