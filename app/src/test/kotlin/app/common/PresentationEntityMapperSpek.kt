package app.common

import domain.entity.Post
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals

/**
 * Unit tests for the top request entity mapper.
 * @see PresentationEntityMapper
 */
@RunWith(JUnitPlatform::class)
internal class PresentationEntityMapperSpek : SubjectSpek<PresentationEntityMapper>({
    subject { PresentationEntityMapper() } // <- Specify the test subject

    it ("should transform a happy case") {
        val source = Post("r54553", "random title", "r/abc", 23,
                "https://media.giphy.com/media/3o6ZtdtckQKDQWAet2/giphy.gif",
                "http://www.google.com/")
        assertEquivalent(source, subject.transform(source))
    }

    it ("should transform an all empty/0s case") {
        val source = Post("", "", "", 0, "", "")
        assertEquivalent(source, subject.transform(source))
    }

    it ("should transform a mixed case") {
        val source = Post("aa", "", "another subreddit", 0, "self", "aefaefaf")
        assertEquivalent(source, subject.transform(source))
    }

    it ("should transform when score is negative") {
        val source = Post("87", "a title", "yet another subreddit", -7, "feafea", "feafeafe")
        assertEquivalent(source, subject.transform(source))
    }
}) {
    private companion object {
        private fun assertEquivalent(expected: Post, actual: PresentationPost) {
            assertEquals(expected.id, actual.id)
            assertEquals(expected.title, actual.title)
            assertEquals(expected.subreddit, actual.subreddit)
            assertEquals(expected.score, actual.score)
            assertEquals(expected.thumbnailLink, actual.thumbnailLink)
        }
    }
}
