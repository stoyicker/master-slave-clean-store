package data.common

import dagger.Module
import dagger.Provides
import org.jorge.ms.data.BuildConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import javax.inject.Singleton

/**
 * Provides the network interface from which api descriptions will be created.
 */
@Module
internal class NetworkModule {
    @Provides
    @Singleton
    fun networkInterface(): Retrofit =
            Retrofit.Builder()
                    .baseUrl(BuildConfig.API_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .validateEagerly(true)
                    .build()
}
