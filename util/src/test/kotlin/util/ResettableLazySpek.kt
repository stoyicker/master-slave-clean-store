package util

import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertEquals
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(JUnitPlatform::class)
internal class ResettableLazySpek : SubjectSpek<ResettableLazySpek.Companion.Helper>({
    subject { Helper() }

    it ("should not change") {
        val x1 = subject.x
        val y1 = subject.y
        val z1 = subject.z
        subject.seed++
        assertTrue(x1 === subject.x)
        assertTrue(y1 === subject.y)
        assertTrue(z1 === subject.z)
    }

    it ("should change") {
        val x1 = subject.x
        val y1 = subject.y
        val z1 = subject.z
        subject.seed++
        arrayOf(subject.x, subject.y, subject.z).forEach { ResettableLazyManager.reset(it) }
        val x2 = subject.x
        val y2 = subject.y
        val z2 = subject.z
        assertEquals(x2, subject.x)
        assertEquals(y2, subject.y)
        assertEquals(z2, subject.z)
        assertNotEquals(x1, x2)
        assertNotEquals(y1, y2)
        assertNotEquals(z1, z2)
    }
}) {
    companion object {
        internal class Helper {
            val x by resettableLazy { "x $seed" }
            val y by resettableLazy { "y $seed" }
            val z by resettableLazy { "z $x $y" }
            var seed = 1
        }
    }
}
