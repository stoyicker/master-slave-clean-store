package util.android.test.matchers

import android.view.View
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
internal class WithIndexSpek : Spek({
    given ("a Matcher") {
        val subject = withIndex(MOCK_MATCHER, INDEX)

        it ("should describe itself") {
            val description = mock<Description>()
            val inOrder = inOrder(description, MOCK_MATCHER)
            subject.describeTo(description)
            inOrder.verify(description).appendText(eq("with index: "))
            inOrder.verify(description).appendValue(eq(INDEX))
            inOrder.verify(MOCK_MATCHER).describeTo(description)
        }
    }
}) {
    private companion object {
        private val MOCK_MATCHER = mock<Matcher<View>>()
        private val INDEX = 0
    }
}
