package com.example.harudemo.utils

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.example.harudemo.databinding.CustomToastBinding

object CustomToast {
    fun makeText(context: Context, message: String, duration: Int): Toast {
        val binding: CustomToastBinding = CustomToastBinding.inflate(LayoutInflater.from(context))

        binding.tvToastMessage.text = message

        return Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 20.toDP())
            duration
            view = binding.root
        }
    }

    private fun Int.toDP(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPX(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
}