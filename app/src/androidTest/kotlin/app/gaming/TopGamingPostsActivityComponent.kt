package app.gaming

import dagger.Component
import javax.inject.Singleton

/**
 *
 */
@Component(modules = arrayOf(TopGamingPostsActivityModule::class))
@Singleton
internal interface TopGamingPostsActivityComponent {
    fun inject(target: TopGamingAllTimePostsActivity)
}
