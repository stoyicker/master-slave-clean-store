package app.gaming

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jorge.ms.app.R

/**
 * An Activity that shows the top posts from /r/gaming.
 */
class TopGamingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_gaming)
    }

    internal companion object {
        /**
         * Safe way to provide an intent to route to this activity. More useful if it were to have
         * parameters for example, but a good idea to have nevertheless.
         * @param context The context to start this activity from.
         */
        fun getCallingIntent(context: Context) = Intent(context, TopGamingActivity::class.java)
    }
}
