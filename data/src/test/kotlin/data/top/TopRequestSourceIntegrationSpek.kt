package data.top

import com.squareup.moshi.Moshi
import data.common.ApiService
import domain.entity.TimeRange
import io.reactivex.observers.TestObserver
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.jorge.ms.data.BuildConfig
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration test to guarantee validity of endpoint, request formation and model.
 */
@RunWith(JUnitPlatform::class)
internal class TopRequestSourceIntegrationSpek : SubjectSpek<TopRequestSource>({
    subject { TopRequestSource() }

    it ("should always fetch models with non-empty values for the attributes kept") {
        val retrofit: ApiService = Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .validateEagerly(true)
                .build()
                .create(ApiService::class.java)
        val expectedSubreddit = "gaming"
        val expectedSize = 25
        val testSubscriber = TestObserver<TopRequestDataContainer>()
        val moshi = Moshi.Builder().build()
        retrofit.top("gaming", TimeRange.ALL_TIME.value, null, expectedSize)
                .map { moshi.adapter(TopRequestDataContainer::class.java).fromJson(it.string()) }
                .subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        @Suppress("UNCHECKED_CAST")
        (testSubscriber.events.first() as Iterable<TopRequestDataContainer>)
                .forEach {
                    assertTrue { it.data.after?.isNotEmpty() ?: false }
                    it.data.children.let {
                        children ->
                        assertEquals(expectedSize, children.size, "Amount of posts not as expected")
                        children.forEach {
                            it.data.let { (id, title, subreddit, score, permalink) ->
                                assertTrue { id.isNotEmpty() }
                                assertTrue { title.isNotEmpty() }
                                assertEquals(expectedSubreddit, subreddit, "Subreddit not as expected")
                                assertTrue { score > 0 }
                                assertTrue { permalink?.isNotEmpty() ?: true }
                            }
                        }
                    }
                }
        testSubscriber.assertComplete()
    }
})
