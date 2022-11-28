package com.example.harudemo.sns

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.databinding.ActivitySnsPostDetailBinding
import com.example.harudemo.model.SnsComment
import com.example.harudemo.model.SnsImage
import com.example.harudemo.retrofit.SnsRetrofitManager
import com.example.harudemo.sns.recyclerview.SnsCommentRecyclerViewAdapter
import com.example.harudemo.sns.recyclerview.SnsPostImageViewPagerAdpater
import com.example.harudemo.utils.Constants.TAG
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.RESPONSE_STATUS
import com.example.harudemo.utils.User

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
        val snsPostContent = intent.getStringExtra("sns_post_content")
        val snsPostLikeList = intent.getStringArrayListExtra("sns_post_like_list")

        //writer 정보
        val snsPostWriter = intent.getStringExtra("sns_post_writer")
        val snsPostWriterId = intent.getIntExtra("sns_post_writer_id", 0)
        val snsPostWriterPhoto = intent.getStringExtra("sns_post_writer_photo")

        //user profile image 연결
        //프로필 이미지 생성
        Glide.with(App.instance)
            .load(snsPostWriterPhoto)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .circleCrop()
            .into(binding.snsPostWriterPhotoImageView)

        binding.snsPostDetailWriter.text = snsPostWriter
        binding.snsPostDetailBody.text = snsPostContent
        binding.snsPostDetailCategoryTextview.text = snsPostCategory
        binding.snsPostDetailLikeNumber.text = snsPostLikeList?.size.toString()

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


        //좋아요가 이미 있다면
        if (snsPostLikeList!!.contains(User.info!!.name)) {
            isLiked = true
            binding.snsPostLottieHeart.progress = 0.5f
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
                binding.snsPostDetailLikeNumber.text =
                    (Integer.parseInt(binding.snsPostDetailLikeNumber.text.toString()) + 1).toString()

                //좋아요 api 호출
                SnsRetrofitManager.instance.postPostLike(
                    snsPostId,
                    User.info!!.name,
                    completion = { responseStatus, responseDataArrayList ->
                    })

            } else {
                val animator = ValueAnimator.ofFloat(0.5f, 0f).setDuration(1000)
                animator.addUpdateListener { animation ->
                    binding.snsPostLottieHeart.setProgress(animation.getAnimatedValue() as Float)
                }
                animator.start()
                isLiked = false
                binding.snsPostDetailLikeNumber.text = (Integer.parseInt(binding.snsPostDetailLikeNumber.text.toString()) - 1).toString()


                //좋아요 api 호출
                SnsRetrofitManager.instance.deletePostLike(
                    snsPostId,
                    User.info!!.name,
                    completion = { responseStatus, responseDataArrayList ->
                    })
            }
        }


        //일반 댓글 입력
        binding.btnSendComment.setOnClickListener {
            if (snsPostId == -1) return@setOnClickListener

            val comment = binding.etWriteComment.text.trim().toString()
            SnsRetrofitManager.instance.postComment(
                User.info!!.id,
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
                            binding.snsPostDetailNestedScrollView.fullScroll(View.FOCUS_DOWN)
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


        //상단의 etc 버튼 클릭 할때 pop up menu 띄어주기
        binding.snsPostDetailEtcBtn.setOnClickListener {
            CustomToast.makeText(App.instance, "버튼 클릭", Toast.LENGTH_SHORT)
                .show()

            //자신이 글 작성자인 경우
            if (snsPostWriterId == User.info!!.id) {
                val inflateMenu = R.menu.sns_post_detail_etc_writer_menu
                val popupMenu = PopupMenu(this, it)

                menuInflater?.inflate(inflateMenu, popupMenu.menu)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.sns_post_detail_etc_redirect_writer_menu -> {
                            CustomToast.makeText(App.instance, "새로고침", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnMenuItemClickListener true
                        }
                        R.id.sns_post_detail_etc_update_writer_menu -> {
                            CustomToast.makeText(App.instance, "수정", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnMenuItemClickListener true
                        }
                        R.id.sns_post_detail_etc_delete_writer_menu -> {
                            CustomToast.makeText(App.instance, "삭제", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnMenuItemClickListener true
                        }
                        else -> {
                            return@setOnMenuItemClickListener false
                        }
                    }
                }
            } else {//다른사람이 쓴 글인 경우
                val inflateMenu = R.menu.sns_post_detail_etc_menu
                val popupMenu = PopupMenu(this, it)

                menuInflater?.inflate(inflateMenu, popupMenu.menu)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.sns_post_detail_etc_redirect_menu -> {
                            CustomToast.makeText(App.instance, "새로고침", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnMenuItemClickListener true
                        }
                        R.id.sns_post_detail_etc_police_menu -> {
                            CustomToast.makeText(App.instance, "신고", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnMenuItemClickListener true
                        }
                        R.id.sns_post_detail_etc_message_menu -> {
                            CustomToast.makeText(App.instance, "신고", Toast.LENGTH_SHORT)
                                .show()
                            return@setOnMenuItemClickListener true
                        }
                        else -> {
                            return@setOnMenuItemClickListener false
                        }
                    }
                }
            }
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

                        binding.snsPostDetailCommentNumber.text =
                            this.snsCommentList.size.toString()
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