package br.com.subsavecoins.savemymoney.extensions

import android.support.design.widget.FloatingActionButton
import android.view.View
import android.view.animation.AnimationUtils
import br.com.subsavecoins.savemymoney.R

fun FloatingActionButton.showByAnim() {

    if (visibility != View.INVISIBLE)
        return

    startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fab_appear))
    visibility = View.VISIBLE
}

fun FloatingActionButton.hideByAnim() {

    if (visibility != View.VISIBLE)
        return


    startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_fab_disappear))
    visibility = View.INVISIBLE
}