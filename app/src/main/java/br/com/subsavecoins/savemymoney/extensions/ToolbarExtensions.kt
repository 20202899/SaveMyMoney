package br.com.subsavecoins.savemymoney.extensions

import android.support.v7.widget.Toolbar
import android.widget.TextView


fun Toolbar.setTitleTransitionName (name: String) {
    val field = this::class.java.getDeclaredField("mTitleTextView")
    field.isAccessible = true
    val titleTextView = field.get(this) as TextView
    titleTextView.transitionName = name
}