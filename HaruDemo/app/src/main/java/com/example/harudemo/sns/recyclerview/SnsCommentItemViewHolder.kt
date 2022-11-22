package com.example.harudemo.sns.recyclerview

import android.animation.ValueAnimator
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsComment
import com.example.harudemo.retrofit.SnsRetrofitManager
import com.example.harudemo.utils.CustomToast
import com.example.harudemo.utils.getTimeDifference
import kotlinx.android.synthetic.main.sns_comment_item.view.*

//Sns 댓글 리사이클러뷰를 위한 뷰홀더
class SnsCommentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val snsCommentConstraintLayout = itemView.sns_comment_constraint_layout

    private val snsCommentWriterPhoto = itemView.sns_comment_writer_photo
    private val snsCommentWriterName = itemView.sns_comment_writer_name
    private val snsCommentCreatedAt = itemView.sns_comment_created_at
    private val snsCommentContent = itemView.sns_comment_content

    private val snsCommentReCommentTextView = itemView.sns_comment_recomment_tv


    private var snsCommentLikeNumber = 0


    // 좋아요 여부
    private val snsCommentLottieHeart = itemView.sns_comment_lottie_heart
    var isLiked = false

    // 댓글 좋아요 개수
    private val snsCommentLikesNumberTv = itemView.sns_comment_likes_number_tv


    fun bindWidthView(snsCommentItem: SnsComment) {
        snsCommentWriterName.text = snsCommentItem.writer
        snsCommentCreatedAt.text = getTimeDifference(snsCommentItem.createdAt.toString())
        snsCommentContent.text = snsCommentItem.content


        //댓글 좋아요 갯수 지정
        snsCommentLikeNumber = snsCommentItem.commentLikeList!!.size

        if (snsCommentItem.commentLikeList!!.contains("LMJ")) {
            isLiked = true
            snsCommentLottieHeart.progress = 0.5f
        }

        //좋아요 개수 업데이트
        if (snsCommentLikeNumber == 0) {
            snsCommentLikesNumberTv.text = ""
        } else {
            snsCommentLikesNumberTv.text = snsCommentLikeNumber.toString()
        }


        // heart click event listener
        snsCommentLottieHeart.setOnClickListener {
            if (!isLiked) {
                // Custom animation speed or duration.
                val animator = ValueAnimator.ofFloat(0f, 0.5f).setDuration(1000)
                animator.addUpdateListener { animation ->
                    snsCommentLottieHeart.setProgress(animation.getAnimatedValue() as Float)
                }
                animator.start()
                isLiked = true


                snsCommentLikeNumber += 1

                //좋아요 개수 업데이트
                if (snsCommentLikeNumber == 0) {
                    snsCommentLikesNumberTv.text = ""
                } else {
                    snsCommentLikesNumberTv.text = snsCommentLikeNumber.toString()
                }


                //좋아요 api 호출
                SnsRetrofitManager.instance.postCommentLike(
                    snsCommentItem.id,
                    "LMJ",
                    completion = { responseStatus, responseDataArrayList ->
                    })


            } else {
                val animator = ValueAnimator.ofFloat(0.5f, 0f).setDuration(1000)
                animator.addUpdateListener { animation ->
                    snsCommentLottieHeart.setProgress(animation.getAnimatedValue() as Float)
                }
                animator.start()
                isLiked = false
                snsCommentLikesNumberTv.text = ""


                snsCommentLikeNumber += -1

                //좋아요 개수 업데이트
                if (snsCommentLikeNumber == 0) {
                    snsCommentLikesNumberTv.text = ""
                } else {
                    snsCommentLikesNumberTv.text = snsCommentLikeNumber.toString()
                }

                //좋아요 api 호출
                SnsRetrofitManager.instance.deleteCommentLike(
                    snsCommentItem.id,
                    "LMJ",
                    completion = { responseStatus, responseDataArrayList ->
                    })
            }
        }


        snsCommentReCommentTextView.setOnClickListener {
            CustomToast.makeText(App.instance, "${snsCommentItem.content}", Toast.LENGTH_SHORT)
                .show()
            snsCommentConstraintLayout.setBackgroundColor(Color.parseColor("#EBF9FF"));
        }


        //이미지 생성
        Glide.with(App.instance)
            .load(snsCommentItem.writerPhoto)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(snsCommentWriterPhoto)
    }
}