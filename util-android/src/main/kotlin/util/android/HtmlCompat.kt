package util.android

import android.os.Build
import android.text.Html

class HtmlCompat {
    companion object {
        /**
         * A compat version of Html#fromHtml to reduce verbosity.
         * @see
         */
        fun fromHtml(source: String, flags: Int = 0) =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(source, flags)
                } else {
                    @Suppress("DEPRECATION") // The replacement can only be used on API >= 24
                    Html.fromHtml(source)
        }
    }
}
