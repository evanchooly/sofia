package com.antwerkz.sofia

import org.testng.Assert
import org.testng.annotations.Test
import java.text.MessageFormat

class MessageFormatTest {
    @Test
    fun argCount() {
        count("Today''s date is {0,date,full} and now a number {1,number}")
    }

    private fun count(pattern: String) {
        val messageFormat = MessageFormat(pattern)
        val formats = messageFormat.getFormats()
        Assert.assertEquals(formats.size, 2)
    }
}
