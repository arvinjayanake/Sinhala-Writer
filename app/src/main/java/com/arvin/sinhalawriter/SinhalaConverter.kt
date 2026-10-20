package com.arvin.sinhalawriter

import android.content.res.Resources

/** The Android Flutter app's replacement table and ordering, kept byte-for-byte in resources. */
class SinhalaConverter private constructor(
    private val replacements: List<Pair<String, String>>,
) {
    fun convert(input: String): String = replacements.fold(input) { text, (english, sinhala) ->
        text.replace(english, sinhala)
    }

    companion object {
        fun fromResources(resources: Resources): SinhalaConverter = fromTables(
            vowelsEnglish = resources.getStringArray(R.array.vowels_en).toList(),
            vowelsSinhala = resources.getStringArray(R.array.vowels_sin).toList(),
            consonantsEnglish = resources.getStringArray(R.array.consonants_eng).toList(),
            consonantsSinhala = resources.getStringArray(R.array.consonants_sin).toList(),
            punctuationEnglish = resources.getStringArray(R.array.punctuation_eng).toList(),
            punctuationSinhala = resources.getStringArray(R.array.punctuation_sin).toList(),
            specialEnglish = resources.getStringArray(R.array.special_char_eng).toList(),
            specialSinhala = resources.getStringArray(R.array.special_char_sin).toList(),
        )

        internal fun fromTables(
            vowelsEnglish: List<String>, vowelsSinhala: List<String>,
            consonantsEnglish: List<String>, consonantsSinhala: List<String>,
            punctuationEnglish: List<String>, punctuationSinhala: List<String>,
            specialEnglish: List<String>, specialSinhala: List<String>,
        ): SinhalaConverter {
            require(vowelsEnglish.size == vowelsSinhala.size)
            require(consonantsEnglish.size == consonantsSinhala.size)
            require(punctuationEnglish.size == punctuationSinhala.size)
            require(specialEnglish.size == specialSinhala.size)

            val characters = mutableListOf<Pair<String, String>>()
            vowelsEnglish.indices.forEach { i ->
                characters += vowelsEnglish[i] to vowelsSinhala[i]
            }
            consonantsEnglish.indices.forEach { i ->
                characters += "${consonantsEnglish[i]}a" to consonantsSinhala[i]
            }
            consonantsEnglish.indices.forEach { i ->
                characters += consonantsEnglish[i] to "${consonantsSinhala[i]}්"
            }
            consonantsEnglish.indices.forEach { i ->
                characters += "${consonantsEnglish[i]}ea" to "${consonantsSinhala[i]}ේ"
            }
            consonantsEnglish.indices.forEach { i ->
                characters += "${consonantsEnglish[i]}aea" to "${consonantsSinhala[i]}ෑ"
            }
            consonantsEnglish.indices.forEach { i ->
                punctuationEnglish.indices.forEach { j ->
                    characters += "${consonantsEnglish[i]}${punctuationEnglish[j]}" to
                        "${consonantsSinhala[i]}${punctuationSinhala[j]}"
                }
            }
            specialEnglish.indices.forEach { i ->
                characters += specialEnglish[i] to specialSinhala[i]
            }

            // Java's stable sort was used in the Flutter app's native MainActivity.
            characters.sortWith(compareByDescending { it.first.length })
            val lastCharacters = characters.filter { it.first.contains("iru") }
            lastCharacters.forEach { character ->
                characters.removeAt(characters.indexOfFirst { it.first == character.first })
                characters += character
            }
            return SinhalaConverter(characters)
        }
    }
}
