package br.com.subsavecoins.savemymoney.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import br.com.subsavecoins.savemymoney.R

class HeaderItem : RelativeLayout {

    var type = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, deff: Int) : super(context, attrs, deff)

    init {
        View.inflate(context, R.layout.header_spotlight, this)
    }
}