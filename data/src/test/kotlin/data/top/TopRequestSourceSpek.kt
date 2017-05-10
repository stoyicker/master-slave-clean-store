package data.top

import com.nhaarman.mockito_kotlin.anyVararg
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import com.nytimes.android.external.store.base.impl.Store
import dagger.Component
import dagger.Module
import dagger.Provides
import data.ComponentHolder
import domain.entity.TimeRange
import domain.interactor.TopGamingAllTimePostsUseCase
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import retrofit2.Retrofit
import rx.Observable
import rx.observers.TestSubscriber
import util.android.IndexedPersistedByDiskStore
import java.io.File
import javax.inject.Singleton
import kotlin.test.assertEquals

/**
 * Unit tests for cache cleanup.
 */
@RunWith(JUnitPlatform::class)
internal class TopRequestSourceSpek : SubjectSpek<TopRequestSource>({
    subject { TopRequestSource() }

    beforeEachTest {
        ComponentHolder.topRequestSourceComponent = DaggerTopRequestSourceSpekDataComponent
                .builder()
                .topRequestSourceSpekModule(TopRequestSourceSpekModule(CACHE_DIR, MOCK_STORE))
                .build()
    }

    afterEachTest {
        CACHE_DIR.deleteRecursively()
        reset(MOCK_STORE)
    }

    it ("should fall back to the cache on failed fetch and propagate the error when the cache is empty") {
        val fetchError = mock<Exception>()
        whenever(MOCK_STORE.fetch(anyVararg())) doReturn
                Observable.error<TopRequestDataContainer>(fetchError)
        whenever(MOCK_STORE.get(anyVararg())) doReturn Observable.empty()
        val testSubscriber = TestSubscriber<TopRequestDataContainer>()
        // Parameters do not matter because of the mocked method on the provided store
        subject.fetch(TopRequestParameters("", TimeRange.ALL_TIME, 0)).subscribe(testSubscriber)
        verify(MOCK_STORE).fetch(anyVararg())
        verify(MOCK_STORE).get(anyVararg())
        testSubscriber.assertError(fetchError)
        testSubscriber.assertNoValues()
        testSubscriber.assertNotCompleted()
        testSubscriber.assertTerminalEvent()
    }

    it ("should fall back to the cache on failed fetch without propagating the error when the cache is not empty") {
        val requestData = mock<TopRequestData> {
            on { after } doReturn "a random after"
        }
        val cachedValue = mock<TopRequestDataContainer> {
            on { data } doReturn requestData
        }
        val fetchError = mock<Exception>()
        whenever(MOCK_STORE.fetch(anyVararg())) doReturn
                Observable.error<TopRequestDataContainer>(fetchError)
        whenever(MOCK_STORE.get(anyVararg())) doReturn Observable.just(cachedValue)
        val testSubscriber = TestSubscriber<TopRequestDataContainer>()
        // Parameters do not matter because of the mocked method on the provided store
        subject.fetch(TopRequestParameters("", TimeRange.ALL_TIME, 0)).subscribe(testSubscriber)
        verify(MOCK_STORE).fetch(anyVararg())
        verify(MOCK_STORE).get(anyVararg())
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(cachedValue)
        testSubscriber.assertCompleted()
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
        val CACHE_DIR = File("build/test-generated/")
        val MOCK_STORE = mock<Store<TopRequestDataContainer, TopRequestParameters>>()
        fun assertCacheClean(subject: TopRequestSource, fromPage: Int) {
            val safePage = Math.max(0, fromPage)
            (0..100).forEach {
                // Just filling up the page map in order to check that it empties when requested
                subject.pageMap.put(it, "$it")
            }
            val size = subject.pageMap.size
            subject.clearCacheFromPage(fromPage)
            verify(subject.store, times(size - safePage))
                    .clear(eq(TopRequestParameters(
                            TopGamingAllTimePostsUseCase.Companion.SUBREDDIT,
                            TopGamingAllTimePostsUseCase.Companion.TIME_RANGE,
                            0)))
            verifyNoMoreInteractions(subject.store)
            assertEquals(safePage, subject.pageMap.size)
        }
    }
}

/**
 * Module used to provide stuff required by this spek.
 */
@Module
internal class TopRequestSourceSpekModule(
        private val cacheDir: File,
        private val store: Store<TopRequestDataContainer, TopRequestParameters>) {
    @Provides
    fun networkInterface() = mock<Retrofit>()

    @Provides
    fun cacheDir() = cacheDir

    @Provides
    @Singleton
    fun pageMapAccessor(cacheDir: File): IndexedPersistedByDiskStore<String> {
        val value = IndexedPersistedByDiskStore(cacheDir.resolve("pageMap"),
                object : IndexedPersistedByDiskStore.ValueStringifier<String> {
                    override fun fromString(source: String) = source

                    override fun toString(source: String) = source
                }, mutableMapOf(0 to ""))
        value.restore()
        return value
    }

    @Provides
    @Singleton
    fun store() = store
}

/**
 * The reason why we use a replacement component instead of inheritance in the module structure
 * is that such a solution could have some potentially bad consequences.
 * @see <a href="https://google.github.io/dagger/testing.html">Testing with Dagger</a>
 */
@Component(modules = arrayOf(TopRequestSourceSpekModule::class))
@Singleton
internal interface TopRequestSourceSpekDataComponent : TopRequestSourceComponent
