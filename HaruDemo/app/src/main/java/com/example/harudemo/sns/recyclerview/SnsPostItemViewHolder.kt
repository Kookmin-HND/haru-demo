package com.example.harudemo.sns.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.harudemo.App
import com.example.harudemo.R
import com.example.harudemo.model.SnsPost
import kotlinx.android.synthetic.main.layout_sns_post_item.view.*

//Sns 게시물 리사이클러뷰를 위한 뷰홀더
class SnsPostItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val snsPostWriterPhoto = itemView.sns_post_writer_photo
    private val snsPostWriterName = itemView.sns_post_writer_name
    private val snsPostCreatedAt = itemView.sns_post_created_at
    private val snsPostContent = itemView.sns_post_content
    private val snsPostPreviewImageOneCardView = itemView.sns_post_preview_image_one_cardView
    private val snsPostPreviewImageTwoCardView = itemView.sns_post_preview_image_two_cardView
    private val snsPostPreviewImageThreeCardView = itemView.sns_post_preview_image_three_cardView

    private val snsPostPreviewImageOne = itemView.sns_post_preview_image_one
    private val snsPostPreviewImageTwoFirst = itemView.sns_post_preview_image_two_first
    private val snsPostPreviewImageTwoSecond = itemView.sns_post_preview_image_two_second
    private val snsPostPreviewImageThreeFirst = itemView.sns_post_preview_image_three_first
    private val snsPostPreviewImageThreeSecond = itemView.sns_post_preview_image_three_second
    private val snsPostPreviewImageThreeThird = itemView.sns_post_preview_image_three_third

    fun bindWidthView(snsPostItem: SnsPost) {
        snsPostWriterName.text = snsPostItem.writer
        snsPostCreatedAt.text = snsPostItem.createdAt
        snsPostContent.text = snsPostItem.content

        //이미지의 개수에 따라 미리보기 다르게 표현
        if (snsPostItem.postImageList.isEmpty()) {//이미지가 없는 게시글인경우
            snsPostPreviewImageOneCardView.visibility = View.GONE
            snsPostPreviewImageTwoCardView.visibility = View.GONE
            snsPostPreviewImageThreeCardView.visibility = View.GONE
        } else if (snsPostItem.postImageList.size == 1) {
            snsPostPreviewImageOneCardView.visibility = View.VISIBLE
            snsPostPreviewImageTwoCardView.visibility = View.GONE
            snsPostPreviewImageThreeCardView.visibility = View.GONE

            Glide.with(App.instance)
                .load(snsPostItem.postImageList[0])
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(snsPostPreviewImageOne)
        } else if (snsPostItem.postImageList.size == 2) {
            snsPostPreviewImageOneCardView.visibility = View.GONE
            snsPostPreviewImageTwoCardView.visibility = View.VISIBLE
            snsPostPreviewImageThreeCardView.visibility = View.GONE

            Glide.with(App.instance)
                .load(snsPostItem.postImageList[0])
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(snsPostPreviewImageTwoFirst)

            Glide.with(App.instance)
                .load(snsPostItem.postImageList[1])
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(snsPostPreviewImageTwoSecond)


        } else if (snsPostItem.postImageList.size >= 3) {
            snsPostPreviewImageOneCardView.visibility = View.GONE
            snsPostPreviewImageTwoCardView.visibility = View.GONE
            snsPostPreviewImageThreeCardView.visibility = View.VISIBLE


            Glide.with(App.instance)
                .load(snsPostItem.postImageList[0])
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(snsPostPreviewImageThreeFirst)

            Glide.with(App.instance)
                .load(snsPostItem.postImageList[1])
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(snsPostPreviewImageThreeSecond)

            Glide.with(App.instance)
                .load(snsPostItem.postImageList[2])
                .placeholder(R.drawable.ic_baseline_account_circle_24)
                .into(snsPostPreviewImageThreeThird)
        }
        //프로필 이미지 생성
        Glide.with(App.instance)
            .load(snsPostItem.writerPhoto)
            .placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(snsPostWriterPhoto)
    }
}