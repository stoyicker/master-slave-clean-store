package data.network.common

import org.jorge.ms.data.BuildConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory

/**
 * Holds the network client from which interfaces will be created.
 */
internal object RxNetworkClient {
    internal val retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .validateEagerly(true)
                .build()
    }
}
