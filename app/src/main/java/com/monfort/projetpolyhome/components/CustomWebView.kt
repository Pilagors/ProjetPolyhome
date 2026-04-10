package com.monfort.projetpolyhome.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView

class CustomWebView(context: Context, attrs: AttributeSet?) : WebView(context, attrs) {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(event)
    }

}