package com.arvin.sinhalawriter

// Kept in the same order as Flutter AppConst help tables.
internal object HelpData {
    data class Section(val title: String, val entries: List<Pair<String, String>>)
    val sections: List<Section> = listOf(
        Section("අ, ආ, ඇ, ඈ, ඉ, ඊ, උ, ඌ, සෘ, සෲ, එ, ඒ, ඓ, ඔ, ඕ, ඖ", listOf(
            "අ" to "a",
            "ආ" to "aa",
            "ඇ" to "ae",
            "ඈ" to "aea",
            "ඉ" to "i",
            "ඊ" to "ii",
            "උ" to "u",
            "ඌ" to "uu",
            "සෘ" to "sru",
            "සෲ" to "sruu",
            "එ" to "e",
            "ඒ" to "ea",
            "ඓ" to "ee",
            "ඔ" to "o",
            "ඕ" to "oa",
            "ඖ" to "au"
        )),
        Section("ක, ග, ච, ජ, ට, ඩ, ත, ද, න, ප, බ, ම, ය, ර, ල, ව, ශ, ස, හ, ෆ", listOf(
            "ක" to "ka",
            "ග" to "ga",
            "ච" to "cha",
            "ජ" to "ja",
            "ට" to "ta",
            "ඩ" to "da",
            "ත" to "tha",
            "ද" to "dha",
            "න" to "na",
            "ප" to "pa",
            "බ" to "ba",
            "ම" to "ma",
            "ය" to "ya",
            "ර" to "ra",
            "ල" to "la",
            "ව" to "va, wa",
            "ශ" to "sha",
            "ස" to "sa",
            "හ" to "ha",
            "ෆ" to "fa"
        )),
        Section("ඛ, ඝ, ඡ, ඣ, ඨ, ඪ, ණ, ථ, ධ, ළ, ෂ, ඵ, භ", listOf(
            "ඛ" to "Ka",
            "ඝ" to "Ga",
            "ඡ" to "Cha",
            "ඣ" to "Ja",
            "ඨ" to "Ta",
            "ඪ" to "Da",
            "ණ" to "Na",
            "ථ" to "Tha",
            "ධ" to "Dha",
            "ළ" to "La",
            "ෂ" to "Sha",
            "ඵ" to "Pa",
            "භ" to "Ba"
        )),
        Section("ඟ, ඤ, ඬ, ඳ, ඥ, ඹ", listOf(
            "ඟ" to "nnga",
            "ඤ" to "knna",
            "ඬ" to "nnda",
            "ඳ" to "nndha",
            "ඥ" to "gnna",
            "ඹ" to "Ma"
        )),
        Section("ක, කා, කැ, කෑ, කි, කී, කු, කූ, කෘ, කෙ, කේ, කෛ, කො, කෝ, කෞ, කෲ, කං, කඞ, කඃ, ක්‍ය, ක්‍ර", listOf(
            "ක" to "ka",
            "කා" to "kaa",
            "කැ" to "kae",
            "කෑ" to "kaea",
            "කි" to "ki",
            "කී" to "kii",
            "කු" to "ku",
            "කූ" to "kuu",
            "කෘ" to "kru",
            "කෙ" to "ke",
            "කේ" to "kea",
            "කෛ" to "kai",
            "කො" to "ko",
            "කෝ" to "koa",
            "කෞ" to "kau",
            "කෲ" to "kruu",
            "කං" to "kax",
            "කඞ" to "kaX",
            "කඃ" to "kaH",
            "ක්‍ය" to "kaYa",
            "ක්‍ර" to "kra"
        )),
        Section("උදාහරණ", listOf(
            "අම්මා" to "ammaa",
            "තාත්තා" to "thaaththaa",
            "සත්‍යය" to "sathaYaya",
            "ව්‍යුහය" to "vaYuhaya",
            "ඊජිප්තුව" to "iijipthuva",
            "ළදරුවා" to "Ladharuvaa"
        ))
    )
}
