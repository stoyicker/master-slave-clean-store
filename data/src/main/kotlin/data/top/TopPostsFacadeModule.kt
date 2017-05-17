package data.top

import dagger.Component
import dagger.Module
import dagger.Provides
import data.common.DomainEntityMapper
import javax.inject.Singleton

/**
 * A component to inject instances that require access to data provided by TopRequestSourceModule.
 * @see TopRequestSourceModule
 */
@Component(modules = arrayOf(TopPostsFacadeModule::class))
@Singleton
internal interface TopPostsFacadeComponent {
    fun inject(target: TopPostsFacade)
}

/**
 * Module used to provide stuff required by TopRequestFacade objects.
 */
@Module
internal class TopPostsFacadeModule {
    @Provides
    @Singleton
    fun entityMapper() = DomainEntityMapper()

    @Provides
    @Singleton
    fun topRequestSource() = TopRequestSource()
}
