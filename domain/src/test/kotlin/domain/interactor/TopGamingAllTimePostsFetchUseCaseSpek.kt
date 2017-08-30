package domain.interactor

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.whenever
import domain.Domain
import domain.entity.Post
import domain.exec.PostExecutionThread
import domain.repository.DomainTopPostsFacade
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.fail

/**
 * Tests for the all-time gaming fetch top posts use case.
 * @see TopGamingAllTimePostsUseCase
 */
@RunWith(JUnitPlatform::class)
internal class TopGamingAllTimePostsFetchUseCaseSpek : SubjectSpek<TopGamingAllTimeFetchPostsUseCase>({
    subject { TopGamingAllTimeFetchPostsUseCase(PAGE, POST_EXECUTION_THREAD_SCHEDULE_IMMEDIATELY) }

    beforeEachTest {
        reset(MOCK_FACADE)
        Domain.topPostsFacade(MOCK_FACADE)
    }

    it ("should build its implementation as an observable") {
        // Cannot mock Post as it is a data class
        val values = setOf(Post("", "title", "sr", -8, "a", "a"),
                Post("rafe", "titfle", "eeesr", 9, "", "a"),
                Post("123", "titlea", "sr", 0, "a", "a"))
        val testSubscriber = object : DisposableSingleObserver<Iterable<Post>>() {
            override fun onSuccess(payload: Iterable<Post>) {
                assertEquals(payload, values, "Values not as expected")
            }

            override fun onError(error: Throwable) {
                fail("An error occurred: $error")
            }
        }
        whenever(MOCK_FACADE.fetchTop(any(), any(), any())) doReturn Single.just<Iterable<Post>>(values)
        subject.execute(testSubscriber)
    }
}) {
    private companion object {
        private const val PAGE = 0
        private val POST_EXECUTION_THREAD_SCHEDULE_IMMEDIATELY = object : PostExecutionThread {
            override fun scheduler(): Scheduler = Schedulers.trampoline()
        }
        private val MOCK_FACADE = mock<DomainTopPostsFacade>()
    }
}
