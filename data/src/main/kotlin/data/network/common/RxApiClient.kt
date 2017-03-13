package data.network.common

import org.jorge.ms.data.BuildConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.Single
import rx.schedulers.Schedulers

/**
 * Holds the network client from which interfaces will be created.
 */
internal object RxApiClient {
    private val scheduler = Schedulers.io()
    internal val retrofit by lazy {
        Single.just(Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .validateEagerly(true)
                .build())
                .observeOn(scheduler)
                .subscribeOn(scheduler)
                .toBlocking()
                .value()
    }
}
