package com.example.harudemo.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.auth.LoginActivity
import com.example.harudemo.retrofit.AuthRetrofitManager
import com.example.harudemo.retrofit.SnsRetrofitManager
import com.example.harudemo.utils.*
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.fragment_etc.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Paths

class EtcFragment : Fragment() {
    companion object {
        const val TAG: String = "로그"
        lateinit var prefs: PreferenceUtil
        lateinit var act: Activity
        fun newInstance(): EtcFragment {
            return EtcFragment()
        }
    }


    //어플리케이션 갤러리 접근 권한 확인
    private val permissionList = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val checkPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.forEach {
                if (!it.value) {
                    CustomToast.makeText(
                        App.instance,
                        "권한 동의후 사진을 업로드할 수 있습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    //프로필 이미지 리스트
    private var imagesList = ArrayList<Uri>()
    private val tmpFileList = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        act = context as Activity
        prefs = PreferenceUtil(act)
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Ranking - on Create() called")
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "Ranking - onAttach() called")
    }

    //뷰가 생성되었을 때
    //프래그먼트와 레이아웃을 연결시켜주는 부분이다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "Ranking - onCreateView() called")

        val view = inflater.inflate(R.layout.fragment_etc, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "더보기"


        //프로필 정보
        //----------

        //프로필 이미지 정보 불러오기
        SnsRetrofitManager.instance.getProfile(User.info!!.id, completion = { responseStatus, responseDataArrayList ->
            when (responseStatus) {
                //API 호출 성공
                RESPONSE_STATUS.OKAY -> {

                    //API를 통해 불러온 이미지로 프로필 이미지 표시
                    if (responseDataArrayList!!.isNotEmpty()) {
                        Glide.with(App.instance)
                            .load(responseDataArrayList[responseDataArrayList.size - 1])
                            .placeholder(R.drawable.ic_baseline_account_circle_24)
                            .circleCrop()
                            .into(etc_profile_image_view)
                    }
                }
                RESPONSE_STATUS.FAIL -> {
                    CustomToast.makeText(App.instance, "api 호출 에러입니다.", Toast.LENGTH_SHORT)
                        .show()
                }
                RESPONSE_STATUS.NO_CONTENT -> {
                    CustomToast.makeText(App.instance, "더이상 게시물이 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })



        etc_profile_image_view.setOnClickListener {
            CustomToast.makeText(App.instance, "프로필 이미지를 선택합니다.", Toast.LENGTH_SHORT).show()
            checkPermission.launch(permissionList)
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 200)
        }

        etc_profile_name_text_view.text = User.info!!.name;

        // ---------

        logoutBtn.setOnClickListener {
            Log.d(TAG, "Logout button Clicked")
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.d(TAG, "로그아웃 실패", error)
                }else{
                    Log.d(TAG, "kakao 로그아웃 성공")
                }
            }

            AuthRetrofitManager.instance.logoutUser { responseStatus, jsonElement ->
                when (responseStatus) {
                    RESPONSE_STATUS.OKAY -> {
                        Log.d(TAG, "success Logout")
                        Log.d(TAG, "${jsonElement}")
                        prefs.clearUser()
                        CustomToast.makeText(act, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                        val intent = Intent(act, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    RESPONSE_STATUS.FAIL -> {
                        Log.d(TAG, "fail Logout")
                        Log.d(TAG, "${jsonElement}")
                        CustomToast.makeText(act, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                    }
                    RESPONSE_STATUS.NO_CONTENT -> {
                        Log.d(TAG, "fail Logout")
                        Log.d(TAG, "${jsonElement}")
                        CustomToast.makeText(act, "로그아웃 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 200) {
            imagesList.clear()
            if (data?.clipData != null) { // 사진 여러개 선택한 경우
                val count = data.clipData!!.itemCount
                if (count > 10) {
                    CustomToast.makeText(App.instance, "사진은 10장까지 선택 가능합니다.", Toast.LENGTH_LONG)
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

            Glide.with(App.instance)
                .load(imagesList[0])
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .circleCrop()
                .into(etc_profile_image_view)


            val imagesMultipartBodyList = ArrayList<MultipartBody.Part>()

            for (imageUri in imagesList) {
                val realPath = createCopyAndReturnRealPath(imageUri)
                val file = File(realPath)

                //임시파일을 삭제하기 위해 리스트에 저장
                tmpFileList.add(realPath)
                Log.d(Constants.TAG, "SnsAddPostActivity ${realPath} - onCreate() called")
                val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                imagesMultipartBodyList.add(
                    MultipartBody.Part.createFormData(
                        "images",
                        file.name,
                        requestBody
                    )
                )
            }

            //프로필 사진 서버에 업로드
            SnsRetrofitManager.instance.postProfile(
                User.info!!.id,
                imagesMultipartBodyList,
                completion = { responseStatus, _ ->
                    when (responseStatus) {
                        //API 호출 성공
                        RESPONSE_STATUS.OKAY -> {
                            Log.d("로그", "SnsAddPostActivity - onCreate() called")
                            CustomToast.makeText(App.instance, "프로필 사진을 변경했습니다.", Toast.LENGTH_SHORT).show()
                            //사진을 업로드하면서 생기는 임시 캐시 파일 삭제
                            for (filePath in tmpFileList) {
                                try {
                                    Files.delete(Paths.get(filePath))
                                } catch (e: IOException) {
                                    CustomToast.makeText(App.instance, "cache delete Error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        RESPONSE_STATUS.FAIL -> {
                        }
                        RESPONSE_STATUS.NO_CONTENT -> {
                        }
                    }
                })
        }
    }

    // 이미지 uri를 절대 경로로 바꾸고 return
    private fun createCopyAndReturnRealPath(uri: Uri): String {
        val context = this.requireContext()
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