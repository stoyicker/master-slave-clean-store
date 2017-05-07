package domain.interactor

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.verifyZeroInteractions
import com.nhaarman.mockito_kotlin.whenever
import domain.Domain
import domain.exec.PostExecutionThread
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertSame
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import rx.Observable
import rx.Scheduler
import rx.Subscriber
import rx.Subscription

/**
 * Unit tests for the UseCase abstraction.
 * @see UseCase
 */
@RunWith(JUnitPlatform::class)
internal class UseCaseSpek : SubjectSpek<UseCase<Nothing>>({
    // The subject is just a UseCase<Nothing> that always returns a mock observable and is observed
    // onto in a mock Scheduler
    subject {
        object : UseCase<Nothing>(object : PostExecutionThread {
            override fun provideScheduler() = MOCK_SCHEDULER
        }) {
            override fun buildUseCaseObservable() = MOCK_OBSERVABLE
        }
    }

    afterEachTest {
        reset(MOCK_SCHEDULER, MOCK_OBSERVABLE)
    }

    it ("should build the observable and subscribe to it") {
        val expectedSubscription = mock<Subscription>()
        val subscriber = mock<Subscriber<Nothing>>()
        val subscribedOn = mock<Observable<Nothing>>()
        val observedOn = mock<Observable<Nothing>>()
        val inOrder = inOrder(MOCK_OBSERVABLE, subscribedOn, observedOn)
        whenever(MOCK_OBSERVABLE.subscribeOn(Domain.useCaseScheduler)) doReturn subscribedOn
        whenever(subscribedOn.observeOn(eq(MOCK_SCHEDULER))) doReturn observedOn
        whenever(observedOn.subscribe(any<Subscriber<Any>>())) doReturn expectedSubscription
        subject.execute(subscriber)
        inOrder.verify(MOCK_OBSERVABLE).subscribeOn(eq(Domain.useCaseScheduler))
        inOrder.verify(subscribedOn).observeOn(eq(MOCK_SCHEDULER))
        inOrder.verify(observedOn).subscribe(eq(subscriber))
        assertSame("Subscription was not same instance.", expectedSubscription,
                subject.subscription)
    }

    it ("should do nothing with the observable") {
        subject.execute(null)
        verifyZeroInteractions(MOCK_OBSERVABLE)
    }

    it ("should unsubscribe its subscription if not yet unsubscribed") {
        subject.subscription = mock { on { isUnsubscribed } doReturn false }
        subject.terminate()
        verify(subject.subscription).isUnsubscribed
        verify(subject.subscription).unsubscribe()
    }

    it ("should do nothing to its subscription if already unsubscribed") {
        subject.subscription = mock { on { isUnsubscribed } doReturn true }
        subject.terminate()
        verify(subject.subscription).isUnsubscribed
        verifyNoMoreInteractions(subject.subscription)
    }
}) {
    private companion object {
        val MOCK_OBSERVABLE = mock<Observable<Nothing>>()
        val MOCK_SCHEDULER = mock<Scheduler>()
    }
}
