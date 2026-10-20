package com.arvin.sinhalawriter

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Test
import org.w3c.dom.Element

class SinhalaConverterTest {
    private val converter: SinhalaConverter by lazy {
        val resource = File("src/main/res/values/conversion_arrays.xml")
        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(resource)
        val arrays = (0 until document.getElementsByTagName("string-array").length)
            .map { document.getElementsByTagName("string-array").item(it) as Element }
            .associate { array ->
                array.getAttribute("name") to (0 until array.getElementsByTagName("item").length)
                    .map { array.getElementsByTagName("item").item(it).textContent }
            }
        fun table(name: String) = requireNotNull(arrays[name])
        SinhalaConverter.fromTables(
            table("vowels_en"), table("vowels_sin"),
            table("consonants_eng"), table("consonants_sin"),
            table("punctuation_eng"), table("punctuation_sin"),
            table("special_char_eng"), table("special_char_sin"),
        )
    }

    @Test fun flutterHelpExamplesMatchNativeConversion() {
        val examples = mapOf(
            "ammaa" to "අම්මා",
            "thaaththaa" to "තාත්තා",
            "sathaYaya" to "සත්‍යය",
            "vaYuhaya" to "ව්‍යුහය",
            "iijipthuva" to "ඊජිප්තුව",
            "Ladharuvaa" to "ළදරුවා",
        )
        examples.forEach { (english, expected) -> assertEquals(english, expected, converter.convert(english)) }
    }

    @Test fun replacementOrderAndSpecialCharactersMatchNativeConversion() {
        val examples = mapOf(
            "kruu" to "කෲ",
            "kru" to "කෘ",
            "kra" to "ක්‍ර",
            "kaea" to "කෑ",
            "kax" to "කං",
            "kaX" to "කඞ",
            "kaH" to "කඃ",
            "iru iruu" to "ඉරු ඉරූ",
            "hello world" to "හෙල්ලො වොර්ල්ඩ්",
        )
        examples.forEach { (english, expected) -> assertEquals(english, expected, converter.convert(english)) }
    }

    @Test fun emptyInputRemainsEmpty() {
        assertEquals("", converter.convert(""))
    }
}
