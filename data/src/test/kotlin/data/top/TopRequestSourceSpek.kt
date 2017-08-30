package data.top

import com.nhaarman.mockito_kotlin.anyVararg
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.nytimes.android.external.store3.base.impl.Store
import dagger.Component
import dagger.Module
import dagger.Provides
import data.ComponentHolder
import domain.entity.Post
import domain.entity.TimeRange
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import retrofit2.Retrofit
import util.android.IndexedPersistedByDiskStore
import java.io.File
import javax.inject.Singleton

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

    it ("should fall back to the cache on failed fetch") {
        val value = TopRequestDataContainer.EMPTY
        whenever(MOCK_STORE.fetch(anyVararg())) doReturn Single.error(mock<Exception>())
        whenever(MOCK_STORE.get(anyVararg())) doReturn Single.just(value)
        val testSubscriber = TestObserver<TopRequestDataContainer>()
        // Parameters do not matter because of the mocked method on the provided store
        subject.fetch(TopRequestParameters("", TimeRange.ALL_TIME, 0))
                .subscribe(testSubscriber)
        verify(MOCK_STORE).fetch(anyVararg())
        testSubscriber.assertValue(value)
        testSubscriber.assertComplete()
    }

    it ("should fall back to the cache on failed fetch without propagating the error when the cache is not empty") {
        val requestData = mock<TopRequestData> {
            on { after } doReturn "a random after"
        }
        val cachedValue = mock<TopRequestDataContainer> {
            on { data } doReturn requestData
        }
        val fetchError = mock<Exception>()
        whenever(MOCK_STORE.fetch(anyVararg())) doReturn Single.error(fetchError)
        whenever(MOCK_STORE.get(anyVararg())) doReturn Single.just(cachedValue)
        val testSubscriber = TestObserver<TopRequestDataContainer>()
        // Parameters do not matter because of the mocked method on the provided store
        subject.fetch(TopRequestParameters("", TimeRange.ALL_TIME, 0))
                .subscribe(testSubscriber)
        verify(MOCK_STORE).fetch(anyVararg())
        verify(MOCK_STORE).get(anyVararg())
        testSubscriber.assertNoErrors()
        testSubscriber.assertValue(cachedValue)
        testSubscriber.assertComplete()
    }
}) {
    private companion object {
        val CACHE_DIR = File("build/test-generated/")
        val MOCK_STORE = mock<Store<TopRequestDataContainer, TopRequestParameters>>()
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
    fun pageMap(cacheDir: File) = IndexedPersistedByDiskStore(
            cacheDir.resolve("pageMap"),
            object : IndexedPersistedByDiskStore.ValueStringifier<String> {
                override fun fromString(source: String) = if (source == "null") null else source

                override fun toString(source: String?) = source ?: "null"
            }, mutableMapOf(0 to null as String?)).also { it.restore() }

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
