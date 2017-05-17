package data.common

import domain.entity.Post
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.jorge.ms.data.BuildConfig
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertEquals

/**
 * Unit tests for the top request entity mapper.
 * @see DomainEntityMapper
 */
@RunWith(JUnitPlatform::class)
internal class DomainEntityMapperSpek : SubjectSpek<DomainEntityMapper>({
    subject { DomainEntityMapper() } // <- Specify the test subject

    it ("should transform a happy case") {
        val source = DataPost("r54553", "random title", "random subreddit", 23, "some permalink", "https://media.giphy.com/media/3o6ZtdtckQKDQWAet2/giphy.gif")
        assertEquivalent(source, subject.transform(source))
    }

    it ("should transform an all empty/0s case") {
        val source = DataPost("", "", "", 0, "", "")
        assertEquivalent(source, subject.transform(source))
    }

    it ("should transform a mixed case") {
        val source = DataPost("aa", "", "another subreddit", 0, "another permalink", "self")
        assertEquivalent(source, subject.transform(source))
    }

    it ("should transform when score is negative") {
        val source = DataPost("87", "a title", "yet another subreddit", -7, "one more permalink", "feafea")
        assertEquivalent(source, subject.transform(source))
    }
}) {
    private companion object {
        private fun assertEquivalent(dataPost: DataPost, post: Post) {
            assertEquals(dataPost.id, post.id)
            assertEquals(dataPost.title, post.title)
            assertEquals(dataPost.subreddit, post.subreddit)
            assertEquals(dataPost.score, post.score)
            assertEquals("${BuildConfig.API_URL}${dataPost.permalink.drop(1)}", post.detailLink)
            assertEquals(dataPost.thumbnailLink, post.thumbnailLink)
        }
    }
}
