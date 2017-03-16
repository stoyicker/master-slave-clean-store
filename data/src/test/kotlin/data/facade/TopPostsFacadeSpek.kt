package data.facade

import com.nhaarman.mockito_kotlin.anyVararg
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nytimes.android.external.store.base.impl.Store
import data.network.common.DataPost
import data.network.top.TopDataPostContainer
import data.network.top.TopRequestData
import data.network.top.TopRequestDataContainer
import data.network.top.TopRequestEntityMapper
import data.network.top.TopRequestParameters
import data.network.top.TopRequestSource
import domain.entity.Post
import domain.entity.TimeRange
import util.ResettableLazyManager
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import rx.Observable
import rx.observers.TestSubscriber
import java.net.UnknownHostException
import kotlin.test.assertEquals

/**
 * Unit tests for TopPostsFacade.
 * @see TopPostsFacade
 */
@RunWith(JUnitPlatform::class)
internal class TopPostsFacadeSpek : SubjectSpek<TopPostsFacade>({
    subject { TopPostsFacade } // <- TopPostsFacade singleton instance as test subject

    it ("should schedule an observable of domain posts when calling fetch") {
        val expectedAfter = "a random after"
        // Mocking data classes is not possible directly without using some trick, so we will
        // instantiate them instead
        val expectedValues = listOf(
                TopDataPostContainer(DataPost("post2", "subreddit", 100, "permalink")),
                TopDataPostContainer(DataPost("post87", "", -9, "another permalink")),
                TopDataPostContainer(DataPost("143141", "r", 0, "some other permalink")))
        val mockResult = Observable.just(TopRequestDataContainer(
                TopRequestData(expectedValues, expectedAfter)))
        val mockStore = mock<Store<TopRequestDataContainer, TopRequestParameters>> {
            on { fetch(anyVararg()) } doReturn mockResult
        }
        // Now we schedule our mock into the data source. You could do this with Dagger, but it is
        // an overkill from my point of view, or you could also write a testing flavor for the
        // module, but it will cause issues when being referenced from other modules
        ResettableLazyManager.reset(TopRequestSource.store)
        TopRequestSource.Provide.storeGenerator = { mockStore }
        val testSubscriber = TestSubscriber<Post>()
        // Parameters do not matter because of the mocked method on the provided delegate
        subject.fetchTop("", TimeRange.ALL_TIME, 0)
                .subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()
        assertEquals(expectedAfter, TopRequestSource.pageMap[1], "Last item not saved.")
        testSubscriber.assertNoErrors()
        testSubscriber.assertValues(
                *(expectedValues.map { TopRequestEntityMapper.transform(it.data) }).toTypedArray())
        testSubscriber.assertCompleted()
    }

    it ("should propagate the error on failed fetch") {
        val expectedError = mock<UnknownHostException>()
        val mockResult = Observable.error<TopRequestDataContainer>(expectedError)
        val mockStore = mock<Store<TopRequestDataContainer, TopRequestParameters>> {
            on { fetch(anyVararg()) } doReturn mockResult
        }
        // Now we schedule our mock into the data source. You could do this with Dagger, but it is
        // an overkill from my point of view, or you could also write a testing flavor for the
        // module, but it will cause issues when being referenced from other modules
        ResettableLazyManager.reset(TopRequestSource.store)
        TopRequestSource.Provide.storeGenerator = { mockStore }
        val testSubscriber = TestSubscriber<Post>()
        // Parameters do not matter because of the mocked method on the provided delegate
        subject.fetchTop("", TimeRange.ALL_TIME, 0)
                .subscribe(testSubscriber)
        testSubscriber.assertError(expectedError)
        testSubscriber.assertNoValues()
        testSubscriber.assertNotCompleted()
        testSubscriber.assertTerminalEvent()
    }
})
