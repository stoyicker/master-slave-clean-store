package data.network.top

import com.google.gson.Gson
import data.network.common.ApiService
import domain.entity.TimeRange
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.jorge.ms.data.BuildConfig
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.observers.TestSubscriber
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration test to guarantee validity of endpoint, request formation and model.
 */
@RunWith(JUnitPlatform::class)
internal class TopRequestSourceIntegrationSpek : SubjectSpek<TopRequestSource>({
    subject { TopRequestSource } // <- Specify singleton instance as test subject

    it ("should always fetch models with non-empty values for the attributes kept") {
        val retrofit: ApiService = Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .validateEagerly(true)
                .build()
                .create(ApiService::class.java)
        val expectedSubreddit = "gaming"
        val expectedSize = 25
        val testSubscriber = TestSubscriber<TopRequestDataContainer>()
        val gson = Gson()
        retrofit.top(expectedSubreddit, TimeRange.ALL_TIME.value, null, expectedSize)
                .map { gson.fromJson(it.string(), TopRequestDataContainer::class.java) }
                .subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.onNextEvents.forEach {
            assertTrue { it.data.after.isNotEmpty() }
            it.data.children.let {
                children ->
                    assertEquals(expectedSize, children.size, "Amount of posts not as expected")
                    children.forEach {
                        it.data.let { post ->
                            assertTrue { post.title.isNotEmpty() }
                            assertEquals(expectedSubreddit, post.subreddit, "Subreddit not as expected")
                            assertTrue { post.score > 0 }
                            assertTrue { post.permalink.isNotEmpty() }
                        }
                    }
            }
        }
        testSubscriber.assertCompleted()
    }
})
