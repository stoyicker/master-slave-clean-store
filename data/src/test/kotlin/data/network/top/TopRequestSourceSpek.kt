package data.network.top

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import data.Data
import domain.interactor.TopGamingAllTimePostsUseCase
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import util.ResettableLazyManager
import java.io.File
import kotlin.test.assertEquals

/**
 * Unit tests for cache cleanup.
 */
@RunWith(JUnitPlatform::class)
internal class TopRequestSourceSpek : SubjectSpek<TopRequestSource>({
    subject {
        TopRequestSource.Provide.storeGenerator = { mock() }
        TopRequestSource // <- Specify singleton instance as test subject
    }

    beforeEachTest {
        Data.Provide.cacheDirGenerator = { File("build/test-generated/") }
        ResettableLazyManager.reset(TopRequestSource.store)
        TopRequestSource.pageMap.clear()
    }

    afterEachTest {
        File("build/test-generated/").deleteRecursively()
    }

    it ("should clean up the page dictionary and cache completely when given page 0") {
        assertCacheClean(subject, 0)
    }

    it ("should clean up the page dictionary and cache completely when given a negative page") {
        assertCacheClean(subject, -7)
    }

    it ("should clean up the page dictionary and cache partially when given a positive page") {
        assertCacheClean(subject, 82)
    }
}) {
    private companion object {
        private fun assertCacheClean(subject: TopRequestSource, fromPage: Int) {
            val safePage = Math.max(0, fromPage)
            (0..100).forEach {
                // Just filling up the page map in order to check that it empties when requested
                TopRequestSource.pageMap.put(it, "$it")
            }
            val size = TopRequestSource.pageMap.size
            subject.clearCacheFromPage(fromPage)
            verify(TopRequestSource.store, times(size - safePage))
                    .clear(eq(TopRequestParameters(
                            TopGamingAllTimePostsUseCase.SUBREDDIT,
                            TopGamingAllTimePostsUseCase.TIME_RANGE,
                            0)))
            verifyNoMoreInteractions(TopRequestSource.store)
            assertEquals(safePage, TopRequestSource.pageMap.size)
        }
    }
}
