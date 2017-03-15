package domain

import com.nhaarman.mockito_kotlin.mock
import domain.repository.DomainTopPostsFacade
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import rx.Scheduler
import kotlin.test.assertEquals

/**
 * Unit tests for the Domain holder
 */
@RunWith(JUnitPlatform::class)
internal class DomainSpek : SubjectSpek<Domain>({
    subject { Domain } // <- Test subject is the singleton instance

    it("should hold the provided top posts facade") {
        val expectedFacade = mock<DomainTopPostsFacade>()
        Domain.topPostsFacade(expectedFacade)
        assertEquals(expectedFacade, Domain.topPostsFacade, "Top posts facade not held.")
    }

    it("should hold the provided scheduler") {
        val expectedScheduler = mock<Scheduler>()
        Domain.Provide.useCaseSchedulerGenerator = { expectedScheduler }
        assertEquals(expectedScheduler, Domain.useCaseScheduler, "Scheduler not held.")
    }
})
