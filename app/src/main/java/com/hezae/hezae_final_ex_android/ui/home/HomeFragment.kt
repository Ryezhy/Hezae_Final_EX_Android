package com.hezae.hezae_final_ex_android.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hezae.hezae_final_ex_android.databinding.FragmentHomeBinding
import android.content.ContentResolver
import android.os.Environment
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var latestTmpUri: Uri? = null // 存储最新拍摄图片的 Uri

    private val REQUEST_CODE_IMAGE_PICKER = 100
    private val REQUEST_CODE_VIDEO_PICKER = 200
    private val REQUEST_CODE_PERMISSIONS = 101

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val openGalleryButton: Button = binding.openGalleryButton

        val imageView = binding.imageView

        val openCameraButton = binding.openCameraButton


        openCameraButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                val photoFile = createImageFile()
                latestTmpUri = FileProvider.getUriForFile(requireContext(), "com.hezae.hezae_final_ex_android.fileprovider", photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, latestTmpUri)
                takePicture.launch(latestTmpUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }






        openGalleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        val openFileManagerButton: Button = binding.openFileManagerButton
        openFileManagerButton.setOnClickListener {
            openFilePicker()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                // 使用 ImageView 显示图片
                binding.imageView.setImageURI(imageUri)
            }
        }
    }
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*" // 允许选择所有文件类型
        }
        filePickerLauncher.launch(intent)
    }
    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri: Uri? = result.data?.data
            if (uri != null) {
                // 获取文件的 MIME 类型
                val mimeType = requireContext().contentResolver.getType(uri)
                val fileName = getFileName(uri)
                // 创建 Intent 来打开文件
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // 查询能够处理该 Intent 的 Activity
                if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                } else {
                    // 没有找到合适的应用
                    Toast.makeText(requireContext(), "没有找到能够打开此类文件的应用", Toast.LENGTH_SHORT).show()
                }
                val editText: EditText = binding.editText
                editText.setText("文件名:$fileName")
            }
        }
    }


    // 获取文件的显示名称
    private fun getFileName(uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        return it.getString(nameIndex)
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.plus(1)?.let { uri.path?.substring(it) }
    }


    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                // 处理拍摄的图片，例如显示在 ImageView 中
                val imageView = binding.imageView
                imageView.setImageURI(uri)
            }
        } else {
            // 处理拍摄失败的情况
        }
    }

    // 创建用于存储图片的临时文件
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", // 文件名前缀
            ".jpg", // 文件后缀
            storageDir // 文件夹
        )
    }

    // 获取临时文件的 Uri
    private fun getTmpFileUri(): Uri? {
        return try {
            val tmpFile = createImageFile()
            latestTmpUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                tmpFile
            )
            latestTmpUri
        } catch (e: IOException) {
            null
        }
    }
}
