package util.android.test

import android.support.test.espresso.IdlingResource
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(JUnitPlatform::class)
internal class BinaryIdlingResourceSpek : SubjectSpek<BinaryIdlingResource>({
    subject { BinaryIdlingResource(SUBJECT_NAME) }

    it ("should be named SUT") {
        assertEquals(SUBJECT_NAME, subject.name, "Name was not $SUBJECT_NAME")
    }

    it ("should be idle by default") {
        assertTrue { subject.isIdleNow }
    }

    it ("should change idle status") {
        assertTrue { subject.isIdleNow }
        subject.setIdleState(false)
        assertFalse { subject.isIdleNow }
    }

    it ("should notify the given callback when it becomes idle") {
        val registeredCallback = mock<IdlingResource.ResourceCallback>()
        subject.registerIdleTransitionCallback(registeredCallback)
        subject.setIdleState(false)
        subject.setIdleState(true)
        verify(registeredCallback).onTransitionToIdle()
    }
}) {
    private companion object {
        val SUBJECT_NAME = "SUT"
    }
}
