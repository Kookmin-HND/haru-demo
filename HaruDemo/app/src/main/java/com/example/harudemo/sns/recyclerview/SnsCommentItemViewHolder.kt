package com.example.harudemo.sns.recyclerview

import android.animation.ValueAnimator
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsComment
import com.example.harudemo.utils.getTimeDifference
import kotlinx.android.synthetic.main.sns_comment_item.view.*

//Sns 댓글 리사이클러뷰를 위한 뷰홀더
class SnsCommentItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val snsCommentWriterPhoto = itemView.sns_comment_writer_photo
    private val snsCommentWriterName = itemView.sns_comment_writer_name
    private val snsCommentCreatedAt = itemView.sns_comment_created_at
    private val snsCommentContent = itemView.sns_comment_content


    // 좋아요 여부
    private val snsCommentLottieHeart = itemView.sns_comment_lottie_heart
    var isLiked = false

    // 댓글 좋아요 개수
    private val snsCommentLikesNumberTv = itemView.sns_comment_likes_number_tv


    fun bindWidthView(snsCommentItem: SnsComment) {
        snsCommentWriterName.text = snsCommentItem.writer
        snsCommentCreatedAt.text = getTimeDifference(snsCommentItem.createdAt.toString())
        snsCommentContent.text = snsCommentItem.content
        snsCommentLikesNumberTv.text = ""

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
                //좋아요 개수 업데이트
                snsCommentLikesNumberTv.text = "1"
            }else{
                val animator = ValueAnimator.ofFloat(0.5f, 0f).setDuration(1000)
                animator.addUpdateListener { animation ->
                    snsCommentLottieHeart.setProgress(animation.getAnimatedValue() as Float)
                }
                animator.start()
                isLiked = false
                snsCommentLikesNumberTv.text = ""
            }
        }
        //이미지 생성
        Glide.with(App.instance)
            .load(snsCommentItem.writerPhoto)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(snsCommentWriterPhoto)
    }
}