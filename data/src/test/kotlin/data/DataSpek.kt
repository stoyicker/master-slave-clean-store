package data

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import domain.callback.Urgency
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

/**
 * Unit tests for Data.
 */
@RunWith(JUnitPlatform::class)
internal class DataSpek : SubjectSpek<Data>({
    subject { Data } // <- The singleton instance as test subject

    beforeEachTest {
        Data.cacheablePagedSources = arrayOf(mock())
    }

    it ("should call its sources with page ${Data.PAGE_KEPT_ON_MEMORY_TRIM_LOW}") {
        subject.onTrimMemory(Urgency.LOW)
        verifySourceCalledWithPage(Data.PAGE_KEPT_ON_MEMORY_TRIM_LOW)
    }

    it ("should call its sources with page ${Data.PAGE_KEPT_ON_MEMORY_TRIM_MEDIUM}") {
        subject.onTrimMemory(Urgency.MEDIUM)
        verifySourceCalledWithPage(Data.PAGE_KEPT_ON_MEMORY_TRIM_MEDIUM)
    }

    it ("should call its sources with page ${Data.PAGE_KEPT_ON_MEMORY_TRIM_HIGH}") {
        subject.onTrimMemory(Urgency.HIGH)
        verifySourceCalledWithPage(Data.PAGE_KEPT_ON_MEMORY_TRIM_HIGH)
    }
}) {
    private companion object {
        private fun verifySourceCalledWithPage(expectedPage: Int) {
            Data.cacheablePagedSources.forEach {
                verify(it).clearCacheFromPage(expectedPage)
                verifyNoMoreInteractions(it)
            }
        }
    }
}
