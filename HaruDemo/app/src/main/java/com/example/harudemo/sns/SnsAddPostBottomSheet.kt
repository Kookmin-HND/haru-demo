package com.example.harudemo.sns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.harudemo.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


//sns에서 새로운 게시물을 작성할 때 태그를 선택하면 나타내기 위한 bottom sheet dialog
class SnsAddPostBottomSheet(val itemClick: (Int)->Unit) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.sns_post_bottomsheetdialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<Button>(R.id.sns_post_bottom_sheet_dialog_health_btn)?.setOnClickListener {
            itemClick(0)
            dismiss()
        }

        view?.findViewById<Button>(R.id.sns_post_bottom_sheet_dialog_study_btn)?.setOnClickListener {
            itemClick(1)
            dismiss()
        }
        view?.findViewById<Button>(R.id.sns_post_bottom_sheet_dialog_coding_btn)?.setOnClickListener {
            itemClick(2)
            dismiss()
        }
        view?.findViewById<Button>(R.id.sns_post_bottom_sheet_dialog_hobby_btn)?.setOnClickListener {
            itemClick(3)
            dismiss()
        }
        view?.findViewById<Button>(R.id.sns_post_bottom_sheet_dialog_diary_btn)?.setOnClickListener {
            itemClick(4)
            dismiss()
        }

    }
}