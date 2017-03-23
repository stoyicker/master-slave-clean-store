package app.gaming

import android.support.v7.widget.RecyclerView
import android.view.View
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import domain.entity.Post
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

/**
 * Tests for the all-time top in r/gaming view
 * @see TopGamingAllTimePostsView
 */
@RunWith(JUnitPlatform::class)
internal class TopGamingAllTimePostsViewSpek : SubjectSpek<TopGamingAllTimePostsView>({
    subject { TopGamingAllTimePostsView(MOCK_RECYCLER_VIEW, MOCK_ERROR_VIEW, MOCK_PROGRESS_VIEW) }

    afterEachTest { reset(MOCK_RECYCLER_VIEW, MOCK_ERROR_VIEW, MOCK_PROGRESS_VIEW) }

    it ("should make the progress view visible") {
        subject.showLoadingLayout()
        verify(MOCK_PROGRESS_VIEW).visibility = eq(View.VISIBLE)
        verifyNoMoreInteractions(MOCK_RECYCLER_VIEW, MOCK_ERROR_VIEW, MOCK_PROGRESS_VIEW)
    }

    it ("should make the progress view go away") {
        subject.hideLoadingLayout()
        verify(MOCK_PROGRESS_VIEW).visibility = eq(View.GONE)
        verifyNoMoreInteractions(MOCK_RECYCLER_VIEW, MOCK_ERROR_VIEW, MOCK_PROGRESS_VIEW)
    }

    it ("should update the content") {
        val mockList = mock<List<Post>>()
        val mockAdapter = mock<Adapter> {
            onGeneric { addItems(eq(mockList)) } doAnswer { }
        }
        whenever (MOCK_RECYCLER_VIEW.adapter) doReturn mockAdapter
        subject.updateContent(mockList)
        verify(MOCK_RECYCLER_VIEW).adapter
        verify(mockAdapter).addItems(eq(mockList))
        verifyNoMoreInteractions(MOCK_RECYCLER_VIEW, MOCK_ERROR_VIEW, MOCK_PROGRESS_VIEW)
    }

    it ("should make the error view visible") {
        subject.showErrorLayout()
        verify(MOCK_ERROR_VIEW).visibility = eq(View.VISIBLE)
        verifyNoMoreInteractions(MOCK_RECYCLER_VIEW, MOCK_ERROR_VIEW, MOCK_PROGRESS_VIEW)
    }

    it ("should make the error view go away") {
        subject.hideErrorLayout()
        verify(MOCK_ERROR_VIEW).visibility = eq(View.GONE)
        verifyNoMoreInteractions(MOCK_RECYCLER_VIEW, MOCK_ERROR_VIEW, MOCK_PROGRESS_VIEW)
    }
}) {
    companion object {
        private val MOCK_RECYCLER_VIEW = mock<RecyclerView>()
        private val MOCK_ERROR_VIEW = mock<View>()
        private val MOCK_PROGRESS_VIEW = mock<View>()
    }
}
