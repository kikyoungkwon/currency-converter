package com.kikyoung.currency.data

import androidx.test.platform.app.InstrumentationRegistry
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset

class TestDataLoader {

    fun loadString(path: String): String {
        try {
            val inputStream = InstrumentationRegistry.getInstrumentation().context.assets.open(path)
            val reader = BufferedReader(InputStreamReader(inputStream, Charset.defaultCharset()) as Reader?)
            val buffer = StringBuilder()
            var line = reader.readLine()
            while (line != null) {
                buffer.append(line)
                line = reader.readLine()
            }
            return buffer.toString()
        } catch (e: Exception) {
            throw IllegalStateException(
                "IOException reading file: $path. File exists? ${File(path).exists()}",
                e
            )
        }
    }
}