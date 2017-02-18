package domain.interactor

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import domain.Domain
import domain.entity.Post
import domain.entity.TimeRange
import domain.exec.PostExecutionThread
import domain.repository.DomainTopPostsFacade
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import rx.Observable
import rx.Scheduler
import rx.observers.TestSubscriber
import rx.schedulers.Schedulers

/**
 * Tests for the all-time gaming top posts use case.
 * @see GamingAllTimeTopPostsUseCase
 */
@RunWith(JUnitPlatform::class)
internal class GamingAllTimeTopPostsUseCaseSpek : SubjectSpek<GamingAllTimeTopPostsUseCase>({
    subject { GamingAllTimeTopPostsUseCase(PAGE, EXECUTION_THREAD_SCHEDULE_IMMEDIATELY) }

    beforeEachTest {
        reset(MOCK_FACADE)
        Domain.inject(MOCK_FACADE)
        Domain.inject(SCHEDULER_IMMEDIATE)
    }

    it("should build its implementation as an observable") {
        val testSubscriber = TestSubscriber<Post>()
        // Cannot mock Post as it is a data class
        val values = arrayOf(Post("title", "sr", -8, "permalink"),
                Post("titfle", "eeesr", 9, ""),
                Post("titlea", "sr", 0, "pfaefaermalink"))
        whenever(MOCK_FACADE.getTop(any(), any(), any())) doReturn Observable.from(values)
        subject.execute(testSubscriber)
        testSubscriber.assertValues(*values)
        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
    }

    it("should unsubscribe on terminate") {
        val testSubscriber = TestSubscriber<Post>()
        whenever(MOCK_FACADE.getTop(any(), any(), any())) doReturn Observable.empty<Post>()
        subject.execute(testSubscriber)
        subject.terminate()
        testSubscriber.assertUnsubscribed()
        testSubscriber.assertNoErrors()
        testSubscriber.assertCompleted()
    }

    it("should delegate to the facade for execution") {
        val testSubscriber = TestSubscriber<Post>()
        val subreddit = "gaming"
        val timeRange = TimeRange.ALL_TIME
        val page = 0
        whenever(MOCK_FACADE.getTop(any(), any(), any())) doReturn Observable.empty<Post>()
        subject.execute(testSubscriber)
        verify(MOCK_FACADE).getTop(eq(subreddit), eq(timeRange), eq(page))
        verifyNoMoreInteractions(MOCK_FACADE)
    }
}) {
    private companion object {
        private const val PAGE = 0
        private val EXECUTION_THREAD_SCHEDULE_IMMEDIATELY = object : PostExecutionThread {
            override fun provideScheduler(): Scheduler = Schedulers.immediate()
        }
        private val SCHEDULER_IMMEDIATE = Schedulers.immediate()
        private val MOCK_FACADE = mock<DomainTopPostsFacade>()
    }
}
