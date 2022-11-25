package com.example.harudemo.retrofit

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.harudemo.App
import com.example.harudemo.utils.*
import com.example.harudemo.utils.Constants.TAG
import okhttp3.Interceptor
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.net.CookieManager
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private var retrofitClient: Retrofit? = null

    fun getClient(): Retrofit? {

        //okhttp 인스턴스 생성
        val client = OkHttpClient.Builder()

        val okHttpClient = client.cookieJar(JavaNetCookieJar(CookieManager()))


        // 로그를 찍기 위해 로깅 인터셉터 추가 - 테스트 용도
        val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
//                Log.d(TAG, "RetrofitClient - log() called / message: $message")
//
//                when {
//                    message.isJsonObject() ->
//                        Log.d(TAG, JSONObject(message).toString(4))
//                    message.isJsonArray() ->
//                        Log.d(TAG, JSONObject(message).toString(4))
//                    else -> {
//                        try {
//                            Log.d(TAG, JSONObject(message).toString(4))
//                        } catch (e: Exception) {
//                            Log.d(TAG, message)
//                        }
//                    }
//                }
            }
        })

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        // 위에서 설정한 로깅 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(loggingInterceptor)


        // 기본 파라미터 인터셉터 설정
        val baseParameterInterceptor: Interceptor = (Interceptor { chain ->
            Log.d(TAG, "RetrofitClient - intercept() called")
            // 오리지널 리퀘스트
            val originalRequest =
                chain.request()
                    .newBuilder().addHeader("token", "${User.info?.token}")
            //                // 쿼리 파라미터 추가하기 추후 API KET 추가 용도
            //                val addedUrl =
            //                    originalRequest.url.newBuilder().addQueryParameter("client_id", API.API_KEY)
            //                        .build()
            //
            //                val finalRequest = originalRequest.newBuilder()
            //                    .url(addedUrl)
            //                    .method(originalRequest.method, originalRequest.body)
            //                    .build()
            //
            //                val response = chain.proceed(finalRequest)
            Log.d("[debug]", originalRequest.toString())
            val response = chain.proceed(originalRequest.build())
            Log.d("[debug]", response.toString())
//            Log.d(TAG, "RetrofitClient - intercept() called response code : ${response.code}")
//            Log.d(TAG, "RetrofitClient - intercept() called response : ${response.body}")
//            if (response.code != 200) {
//                Handler(Looper.getMainLooper()).post {
//                    CustomToast.makeText(
//                        App.instance, "${response.code} 에러 입니다.", Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
            response
        })


        // 위에서 설정한 기본 파라미터 인터셉터를 okhttp 클라이언트에 추가한다.
        client.addInterceptor(baseParameterInterceptor)

        // 커넥션 타임 아웃
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)

        // 레트로핏 빌더를 통해 인스턴스 생성
        if (retrofitClient == null) {
            retrofitClient = Retrofit.Builder().baseUrl("http://192.168.0.11:8000/api/")
                .addConverterFactory(GsonConverterFactory.create())

                // 위에서 설정한 클라이언트로 레트로핏 클라이언트를 설정한다.
                .client(okHttpClient.build()).build()
//            client.build()
        }
        return retrofitClient
    }
}