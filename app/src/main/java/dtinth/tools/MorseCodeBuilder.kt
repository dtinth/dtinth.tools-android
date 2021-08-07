package dtinth.tools

import android.util.Log
import java.util.*

class MorseCodeBuilder(private val period: Int = 80, private val maxChars: Int = 999) {
    private val chars =
        ".-/-.../-.-./-.././..-./--./..../../.---/-.-/.-../--/-./---/.--./--.-/.-./.../-/..-/...-/.--/-..-/-.--/--..".split(
            "/"
        ).toTypedArray()
    private val list: MutableList<Boolean> = LinkedList()
    private val addedCode = StringBuilder()
    private val addedChars = StringBuilder()

    fun add(text: String) {
        for (element in text) {
            add(element)
        }
    }

    private fun add(c: Char) {
        if (c in 'a'..'z') {
            addCode(chars[c - 'a'], c)
            pause()
        }
        if (c in 'A'..'Z') {
            addCode(chars[c - 'A'], c)
            pause()
        }
    }

    private fun addCode(code: String, char: Char) {
        if (addedChars.length >= maxChars) {
            return
        }
        for (c in code) {
            if (c == '.') dit()
            if (c == '-') dah()
        }
        addedChars.append(char)
    }

    private fun dit() {
        list.add(true)
        list.add(false)
        addedCode.append(".")
    }

    private fun dah() {
        list.add(true)
        list.add(true)
        list.add(true)
        list.add(false)
        addedCode.append("-")
    }

    private fun pause() {
        list.add(false)
        list.add(false)
        addedCode.append(" ")
    }

    fun build(): MutableList<Long> {
        var now: Long = 0
        var state = false
        val durations: MutableList<Long> = LinkedList()
        for (status in list) {
            if (state != status) {
                durations.add(now)
                state = status
                now = 0
            }
            now += period.toLong()
        }
        return durations
    }

    override fun toString(): String {
        return "${addedChars.toString()} (${addedCode.toString()})"
    }
}