package util.android

import com.nhaarman.mockito_kotlin.*
import org.jetbrains.spek.api.SubjectSpek
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(JUnitPlatform::class)
internal class IndexedPersistedByDiskStoreSpek : SubjectSpek<IndexedPersistedByDiskStore<Any>>({
    subject {
        IndexedPersistedByDiskStore(SOURCE_FILE, MOCK_VALUE_STRINGIFIER, INITIAL_MAP)
    }

    beforeEachTest {
        SOURCE_FILE.parentFile.mkdirs()
    }

    afterEachTest {
        SOURCE_FILE.delete()
        reset(MOCK_VALUE_STRINGIFIER)
        INITIAL_MAP.clear()
    }

    it ("should not touch the initial map if the source file does not exist") {
        val dummyInt = 0
        val dummyString = "abc"
        SOURCE_FILE.delete()
        subject.restore()
        verifyZeroInteractions(spy(INITIAL_MAP))
        arrayOf(INITIAL_MAP, subject).forEach {
            assertTrue { it.isEmpty() }
        }
        INITIAL_MAP.put(dummyInt, dummyString)
        subject.restore()
        verifyZeroInteractions(spy(INITIAL_MAP))
        arrayOf(INITIAL_MAP, subject).forEach {
            assertEquals(1, it.size)
            assertEquals(dummyString, it[dummyInt])
        }
    }

    it ("should not restore anything is the file exists but has bad content") {
        SOURCE_FILE.writeText(";")
        subject.restore()
        verifyZeroInteractions(spy(INITIAL_MAP))
        assertTrue { subject.isEmpty() }
    }

    it ("should read the source and restore the map contents") {
        val indexes = arrayOf(1, 2, 4)
        val values = arrayOf("a", "b", "c")
        SOURCE_FILE.writeText(
                indexes.mapIndexed { index, value ->
                  "$value=${values[index]}"
                }.joinToString(separator = "\n"))
        whenever(MOCK_VALUE_STRINGIFIER.fromString(any())) doAnswer { it.arguments[0] }
        subject.restore()
        values.forEachIndexed { index, value ->
            verify(MOCK_VALUE_STRINGIFIER).fromString(eq(values[index]))
            assertEquals(value, subject[indexes[index]], "Expected value did not match actual")
        }
    }

    it ("should save the map contents to the source if it does not exist") {
        val newIndex = -7
        val newValue = "feafeaf"
        SOURCE_FILE.delete()
        whenever(MOCK_VALUE_STRINGIFIER.toString(eq(newValue))) doReturn newValue
        subject.put(newIndex, newValue)
        assertEquals("$newIndex=$newValue",
                SOURCE_FILE.readText(IndexedPersistedByDiskStore.CHARSET))
    }

    it ("should override the source file when saving the map contents if it already exists") {
        val indexes = arrayOf(-1, -2, -4)
        val values = arrayOf("a", "b", "c")
        val newIndex = 0
        val newValue = "abc"
        SOURCE_FILE.writeText(
                indexes.mapIndexed { index, value ->
                    "$value=${values[index]}"
                }.joinToString(separator = "\n"))
        whenever(MOCK_VALUE_STRINGIFIER.toString(eq(newValue))) doReturn newValue
        subject.put(newIndex, newValue)
        assertEquals("$newIndex=$newValue",
                SOURCE_FILE.readText(IndexedPersistedByDiskStore.CHARSET))
    }
}) {
    companion object {
        private val INITIAL_MAP = mutableMapOf<Int, Any>()
        private val SOURCE_FILE
                = File("build/test-generated/${IndexedPersistedByDiskStore::class.java.simpleName}")
        private val MOCK_VALUE_STRINGIFIER
                = mock<IndexedPersistedByDiskStore.ValueStringifier<Any>>()
    }
}
