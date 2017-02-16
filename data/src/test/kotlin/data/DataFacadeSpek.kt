package data

import com.nhaarman.mockito_kotlin.anyVararg
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
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
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import rx.Observable
import rx.observers.TestSubscriber
import java.net.UnknownHostException

// TODO If time: there seems to be some duplicated code here

/**
 * Unit tests for DataFacade.
 * @see DataFacade
 */
@RunWith(JUnitPlatform::class)
internal class DataFacadeSpek : SubjectSpek<DataFacade>({
    subject { DataFacade } // <- DataFacade singleton instance as test subject

    it("should provide an observable of domain posts when calling fetch") {
        // Mocking data classes is not possible directly without using some trick, so we will
        // instantiate them instead
        val expectedValues = listOf(
                TopDataPostContainer(DataPost("post0", "sr", 1)),
                TopDataPostContainer(DataPost("post1", "sr", -1)),
                TopDataPostContainer(DataPost("post1", "sr", 3)))
        val mockResult = Observable.just(TopRequestDataContainer(TopRequestData(expectedValues)))
        val mockStore = mock<Store<TopRequestDataContainer, TopRequestParameters>>()
        // Now we inject our mock into the data source. You could do this with Dagger, but it is
        // an overkill from my point of view, or you could also write a testing flavor for the
        // module, but it will cause issues when being referenced from other modules
        TopRequestSource.delegate = mockStore
        val testSubscriber = TestSubscriber<Post>()
        whenever(mockStore.fetch(anyVararg())) doReturn mockResult
        // Parameters do not matter because of the mocked method on the injected delegate
        subject.fetchTop("", TimeRange.ALL_TIME, "", 0)
                .subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValues(
                *(expectedValues.map { TopRequestEntityMapper.transform(it.data) }).toTypedArray())
        testSubscriber.assertCompleted()
    }

    it("should propagate the error on failed fetch") {
        val expectedError = mock<UnknownHostException>()
        val mockResult = Observable.error<TopRequestDataContainer>(expectedError)
        val mockStore = mock<Store<TopRequestDataContainer, TopRequestParameters>>()
        // Now we inject our mock into the data source. You could do this with Dagger, but it is
        // an overkill from my point of view, or you could also write a testing flavor for the
        // module, but it will cause issues when being referenced from other modules
        TopRequestSource.delegate = mockStore
        val testSubscriber = TestSubscriber<Post>()
        whenever(mockStore.fetch(anyVararg())) doReturn mockResult
        // Parameters do not matter because of the mocked method on the injected delegate
        subject.fetchTop("", TimeRange.ALL_TIME, "", 0)  // Parameters do not matter because of the injected delegate
                .subscribe(testSubscriber)
        testSubscriber.assertError(expectedError)
        testSubscriber.assertNoValues()
        testSubscriber.assertNotCompleted()
        testSubscriber.assertTerminalEvent()
    }

    it("should provide an observable of domain posts when calling get") {
        // Mocking data classes is not possible directly without using some trick, so we will
        // instantiate them instead
        val expectedValues = listOf(
                TopDataPostContainer(DataPost("post2", "subreddit", 100)),
                TopDataPostContainer(DataPost("post87", "", -9)),
                TopDataPostContainer(DataPost("143141", "r", 0)))
        val mockResult = Observable.just(TopRequestDataContainer(TopRequestData(expectedValues)))
        val mockStore = mock<Store<TopRequestDataContainer, TopRequestParameters>>()
        // Now we inject our mock into the data source. You could do this with Dagger, but it is
        // an overkill from my point of view, or you could also write a testing flavor for the
        // module, but it will cause issues when being referenced from other modules
        TopRequestSource.delegate = mockStore
        val testSubscriber = TestSubscriber<Post>()
        whenever(mockStore.get(anyVararg())) doReturn mockResult
        // Parameters do not matter because of the mocked method on the injected delegate
        subject.getTop("", TimeRange.ALL_TIME, "", 0)  // Parameters do not matter because of the injected delegate
                .subscribe(testSubscriber)
        testSubscriber.assertNoErrors()
        testSubscriber.assertValues(
                *(expectedValues.map { TopRequestEntityMapper.transform(it.data) }).toTypedArray())
        testSubscriber.assertCompleted()
    }

    it("should propagate the error on failed get") {
        val expectedError = mock<UnknownHostException>()
        val mockResult = Observable.error<TopRequestDataContainer>(expectedError)
        val mockStore = mock<Store<TopRequestDataContainer, TopRequestParameters>>()
        // Now we inject our mock into the data source. You could do this with Dagger, but it is
        // an overkill from my point of view, or you could also write a testing flavor for the
        // module, but it will cause issues when being referenced from other modules
        TopRequestSource.delegate = mockStore
        val testSubscriber = TestSubscriber<Post>()
        whenever(mockStore.get(anyVararg())) doReturn mockResult
        // Parameters do not matter because of the mocked method on the injected delegate
        subject.getTop("", TimeRange.ALL_TIME, "", 0)  // Parameters do not matter because of the injected delegate
                .subscribe(testSubscriber)
        testSubscriber.assertError(expectedError)
        testSubscriber.assertNoValues()
        testSubscriber.assertNotCompleted()
        testSubscriber.assertTerminalEvent()
    }
})
