package com.example.harudemo.sns

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.databinding.ActivitySnsAddPostBinding
import com.example.harudemo.retrofit.SnsRetrofitManager
import com.example.harudemo.sns.recyclerview.SnsPostImagesRecyclerViewAdapter
import com.example.harudemo.sns.recyclerview.SnsPostRecyclerViewAdapter
import com.example.harudemo.utils.Constants.TAG
import com.example.harudemo.utils.RESPONSE_STATUS
import kotlinx.android.synthetic.main.activity_sns_add_post.*
import kotlinx.android.synthetic.main.fragment_sns.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

import com.example.harudemo.utils.CustomToast
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths

// SNS 프래그먼트에서 게시물을 추가할 수 있는 액티비티
class SnsAddPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySnsAddPostBinding;


    //어플리케이션 갤러리 접근 권한 확인
    private val permissionList = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val checkPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.forEach {
                if (!it.value) {
                    Toast.makeText(
                        applicationContext,
                        "권한 동의후 사진을 업로드할 수 있습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }

    private var imagesList = ArrayList<Uri>()
    private val adapter = SnsPostImagesRecyclerViewAdapter(imagesList, this)
    private val tmpFileList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnsAddPostBinding.inflate(layoutInflater);
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.ivAddedMultipleImagesRecyclerview.layoutManager = layoutManager
        binding.ivAddedMultipleImagesRecyclerview.adapter = adapter

        //글 작성 취소 버튼 클릭 시
        binding.addCancel.setOnClickListener {
            CustomToast.makeText(applicationContext, "취소취소", Toast.LENGTH_SHORT).show();
            finish();
        }


        //이미지 추가 버전
        //글 작성 버튼 클릭시
        binding.addApply.setOnClickListener {
            val title =
                binding.addPostTitle.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val content =
                binding.addPostText.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val imagesMultipartBodyList = ArrayList<MultipartBody.Part>()

            for (imageUri in imagesList) {
                val realPath = createCopyAndReturnRealPath(imageUri)
                if(realPath.isEmpty()) return@setOnClickListener
                val file = File(realPath)

                //임시파일을 삭제하기 위해 리스트에 저장
                tmpFileList.add(realPath)
                Log.d(TAG, "SnsAddPostActivity ${realPath} - onCreate() called")
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                imagesMultipartBodyList.add(
                    MultipartBody.Part.createFormData(
                        "images",
                        file.name,
                        requestBody
                    )
                )
            }


            Log.d(TAG, "SnsAddPostActivity ${imagesMultipartBodyList} - onCreate() called")

            SnsRetrofitManager.instance.postPost(
                "LMJ",
                title,
                content,
                imagesMultipartBodyList,
                completion = { responseStatus, _ ->
                    when (responseStatus) {
                        //API 호출 성공
                        RESPONSE_STATUS.OKAY -> {
                            Log.d("로그", "SnsAddPostActivity - onCreate() called")
                            Toast.makeText(App.instance, "글 작성에 성공했습니다.", Toast.LENGTH_SHORT).show()

                            //사진을 업로드하면서 생기는 임시 캐시 파일 삭제
                            for(filePath in tmpFileList){
                                try{
                                    Files.delete(Paths.get(filePath))
                                } catch (e: IOException){
                                    Toast.makeText(this, "cache delete Error", Toast.LENGTH_SHORT).show()
                                }
                            }
                            finish()
                        }
                        RESPONSE_STATUS.FAIL -> {
                            Log.d(TAG, "SnsAddPostActivity - onCreate() ${responseStatus} called")
                            Toast.makeText(App.instance, "글 쓰기에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                        RESPONSE_STATUS.NO_CONTENT -> {
                            Log.d(TAG, "SnsAddPostActivity - onCreate() ${responseStatus} called")
                            Toast.makeText(App.instance, "더이상 게시물이 없습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })




        }

        binding.addPolicy.setOnClickListener {
            CustomToast.makeText(applicationContext, "전체전체", Toast.LENGTH_SHORT).show();
        }


        //갤러리에서 이미지 불러오기 선택
        binding.addimage.setOnClickListener {
            checkPermission.launch(permissionList)
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 200)
        }



        // 바텀 메뉴를 클릭해서 갤러리로 이동
        binding.snsAddBottomNav.setOnItemSelectedListener {
            if(it.itemId == R.id.sns_add_image_nav_menu){
                Toast.makeText(App.instance, "hello", Toast.LENGTH_SHORT).show()

                checkPermission.launch(permissionList)
                val intent = Intent(Intent.ACTION_PICK)
                intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent, 200)
            }
            true
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 200) {
            imagesList.clear()
            if (data?.clipData != null) { // 사진 여러개 선택한 경우
                val count = data.clipData!!.itemCount
                if (count > 10) {
                    Toast.makeText(applicationContext, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG)
                    return
                }
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    imagesList.add(imageUri)
                }
            } else { // 단일 선택
                data?.data?.let { uri ->
                    val imageUri: Uri? = data?.data
                    if (imageUri != null) {
                        imagesList.add(imageUri)
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    // 이미지 uri를 절대 경로로 바꾸고 return
    fun createCopyAndReturnRealPath(uri: Uri): String {
        val context = applicationContext
        val contentResolver = context.contentResolver ?: return ""

        // Create file path inside app's data dir
        val filePath = (context.applicationInfo.dataDir + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return ""
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()

            return ""
        }
        /*  절대 경로를 getGps()에 넘겨주기   */
        return file.getAbsolutePath()
    }
}