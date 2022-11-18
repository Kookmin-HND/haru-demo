package com.example.harudemo.sns

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.harudemo.App
import com.example.harudemo.databinding.ActivitySnsPostDetailBinding
import com.example.harudemo.model.SnsComment
import com.example.harudemo.model.SnsImage
import com.example.harudemo.retrofit.SnsRetrofitManager
import com.example.harudemo.sns.recyclerview.SnsCommentRecyclerViewAdapter
import com.example.harudemo.sns.recyclerview.SnsPostImageViewPagerAdpater
import com.example.harudemo.utils.Constants.TAG
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.RESPONSE_STATUS

class SnsPostDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySnsPostDetailBinding;

    // 코멘트 데이터
    private var snsCommentList = ArrayList<SnsComment>()

    // 이미지 데이터
    private var snsImagesList = ArrayList<SnsImage>()


    private val MIN_SCALE = 1f // 뷰가 몇퍼센트로 줄어들 것인지
    private val MIN_ALPHA = 0.97f // 어두워지는 정도

    //lottie heart
    private var isLiked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySnsPostDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.snsPostDetailCancelButton.setOnClickListener {
            finish()
        }

        val intent = getIntent();
        val snsPostId = intent.getIntExtra("sns_post_id", -1)
        val snsPostCategory = intent.getStringExtra("sns_post_category")
        val snsPostWriter = intent.getStringExtra("sns_post_writer")
        val snsPostContent = intent.getStringExtra("sns_post_content")

        binding.snsPostDetailWriter.text = snsPostWriter
        binding.snsPostDetailBody.text = snsPostContent
        binding.snsPostDetailCategoryTextview.text = snsPostCategory


        //comment adapter 연결
        binding.snsPostCommentsRecyclerview.adapter = SnsCommentRecyclerViewAdapter()
        (binding.snsPostCommentsRecyclerview.adapter as SnsCommentRecyclerViewAdapter).submitList(
            this.snsCommentList
        )
        //댓글 불러오기
        commentApiCall(snsPostId)

        binding.snsPostCommentsRecyclerview.layoutManager =
            GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)


        //image viewpager apdater 연결
        binding.snsImageViewpager.adapter = SnsPostImageViewPagerAdpater()
        (binding.snsImageViewpager.adapter as SnsPostImageViewPagerAdpater).submitList(this.snsImagesList)
        //이미지 불러오기
        imageApiCall(snsPostId)

        //indicator 지정
        binding.snsImageIndicator.setViewPager(binding.snsImageViewpager)

        binding.snsImageViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.snsImageViewpager.setPageTransformer(ZoomOutPageTransformer()) //애니메이션 적용



        binding.snsPostDetailCancelButton.setOnClickListener {
            finish()
        }


        //post heart click event
        // heart click event listener
        binding.snsPostLottieHeart.setOnClickListener {
            if (!isLiked) {
                // Custom animation speed or duration.
                val animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(1000)
                animator.addUpdateListener { animation ->
                    binding.snsPostLottieHeart.setProgress(animation.getAnimatedValue() as Float)
                }
                animator.start()
                isLiked = true
                //좋아요 개수 업데이트
                binding.snsPostDetailLikeNumber.text = "1"
            }else{
                val animator = ValueAnimator.ofFloat(0.5f, 0f).setDuration(1000)
                animator.addUpdateListener { animation ->
                    binding.snsPostLottieHeart.setProgress(animation.getAnimatedValue() as Float)
                }
                animator.start()
                isLiked = false
                binding.snsPostDetailLikeNumber.text = ""
            }
        }




        //일반 댓글 입력
        binding.btnSendComment.setOnClickListener {
            if (snsPostId == -1) return@setOnClickListener

            val comment = binding.etWriteComment.text.trim().toString()
            SnsRetrofitManager.instance.postComment(
                "LMJ",
                snsPostId,
                comment,
                -1,
                completion = { responseStatus, responseDataArrayList ->
                    when (responseStatus) {
                        //API 호출 성공
                        RESPONSE_STATUS.OKAY -> {
                            CustomToast.makeText(App.instance, "댓글 입력에 성공했습니다.", Toast.LENGTH_SHORT)
                                .show()

                            //API 재호출
                            commentApiCall(snsPostId)
                            binding.etWriteComment.setText("")
                            binding.snsPostCommentsRecyclerview.smoothScrollToPosition(100)
                        }
                        RESPONSE_STATUS.FAIL -> {
                            CustomToast.makeText(App.instance, "api 호출 에러입니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                        RESPONSE_STATUS.NO_CONTENT -> {
                            CustomToast.makeText(App.instance, "댓글이 없습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                })
        }
    }

    private fun commentApiCall(postId: Int) {
        SnsRetrofitManager.instance.getComments(
            postId,
            completion = { responseStatus, responseDataArrayList ->
                when (responseStatus) {
                    //API 호출 성공
                    RESPONSE_STATUS.OKAY -> {
                        this.snsCommentList.clear()
                        responseDataArrayList!!.forEach {
                            this.snsCommentList.add(it)
                        }
                        binding.snsPostCommentsRecyclerview.adapter?.notifyDataSetChanged()
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
            }
        )
    }

    private fun imageApiCall(postId: Int) {
        SnsRetrofitManager.instance.getImages(
            postId,
            completion = { responseStatus, responseDataArrayList ->
                when (responseStatus) {
                    RESPONSE_STATUS.OKAY -> {
                        responseDataArrayList!!.forEach {
                            this.snsImagesList.add(it)
                        }
                        binding.snsImageViewpager.adapter?.notifyItemInserted(this.snsImagesList.size)
                        // 사진 개수가 2개 이상이면 indicator 지정
                        if (this.snsImagesList.size >= 2)
                            binding.snsImageIndicator.createIndicators(this.snsImagesList.size, 0)
                        else if (this.snsImagesList.size == 0) {
                            //이미지가 없으면 frame layout가 안보이게 함
                            binding.snsPostDetailImagesFrameLayout.visibility = View.GONE
                        }

                    }
                    RESPONSE_STATUS.FAIL -> {
                        CustomToast.makeText(App.instance, "api 호출 에러입니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                    RESPONSE_STATUS.NO_CONTENT -> {
//                        CustomToast.makeText(App.instance, "더이상 게시물이 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                Log.d(TAG, "SnsPostDetailActivity ${this.snsImagesList} - imageApiCall() called")
            }
        )
    }


    inner class ZoomOutPageTransformer : ViewPager2.PageTransformer {
        override fun transformPage(view: View, position: Float) {
            view.apply {
                val pageWidth = width
                val pageHeight = height
                when {
                    position < -1 -> { // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        alpha = 0f
                    }
                    position <= 1 -> { // [-1,1]
                        // Modify the default slide transition to shrink the page as well
                        val scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position))
                        val vertMargin = pageHeight * (1 - scaleFactor) / 2
                        val horzMargin = pageWidth * (1 - scaleFactor) / 2
                        translationX = if (position < 0) {
                            horzMargin - vertMargin / 2
                        } else {
                            horzMargin + vertMargin / 2
                        }

                        // Scale the page down (between MIN_SCALE and 1)
                        scaleX = scaleFactor
                        scaleY = scaleFactor

                        // Fade the page relative to its size.
                        alpha = (MIN_ALPHA +
                                (((scaleFactor - MIN_SCALE) / (1 - MIN_SCALE)) * (1 - MIN_ALPHA)))
                    }
                    else -> { // (1,+Infinity]
                        // This page is way off-screen to the right.
                        alpha = 0f
                    }
                }
            }
        }
    }
}