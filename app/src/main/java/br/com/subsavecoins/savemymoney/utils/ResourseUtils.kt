package br.com.subsavecoins.savemymoney.utils

import android.content.Context
import android.util.TypedValue

class ResourseUtils {
    companion object {
        fun dpToPx(d: Float, context: Context): Float {
            return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    d,
                    context.resources.displayMetrics)
        }
    }
}