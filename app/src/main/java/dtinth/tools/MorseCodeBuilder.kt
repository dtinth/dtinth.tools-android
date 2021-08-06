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
    private var addedChars = 0

    fun add(text: String) {
        for (element in text) {
            add(element)
        }
    }

    private fun add(c: Char) {
        if (c in 'a'..'z') {
            addCode(chars[c - 'a'])
            pause()
        }
        if (c in 'A'..'Z') {
            addCode(chars[c - 'A'])
            pause()
        }
    }

    private fun addCode(code: String) {
        if (addedChars >= maxChars) {
            return
        }
        for (c in code) {
            if (c == '.') dit()
            if (c == '-') dah()
        }
        addedChars++
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
        Log.d("MorseCodeBuilder", addedCode.toString())
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
}