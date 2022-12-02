package com.example.harudemo.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.harudemo.R
import com.example.harudemo.auth.LoginActivity
import com.example.harudemo.auth.LoginActivity.Companion.prefs
import com.example.harudemo.retrofit.AuthRetrofitManager
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.PreferenceUtil
import com.example.harudemo.utils.RESPONSE_STATUS
import com.example.harudemo.utils.User
import com.kakao.sdk.user.UserApiClient
import kotlinx.android.synthetic.main.fragment_etc.*

class EtcFragment : Fragment() {
    companion object {
        const val TAG: String = "로그"
        lateinit var prefs: PreferenceUtil
        lateinit var act: Activity
        fun newInstance(): EtcFragment {
            return EtcFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        act = context as Activity
        prefs = PreferenceUtil(act)
        super.onCreate(savedInstanceState)
        idView.text = "${User.info.email}"
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

        logoutBtn.setOnClickListener {
            Log.d(TAG, "Logout button Clicked")
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.d(TAG, "로그아웃 실패", error)
                } else {
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

}