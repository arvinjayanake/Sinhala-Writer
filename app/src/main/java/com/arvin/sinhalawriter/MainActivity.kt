package com.arvin.sinhalawriter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.arvin.sinhalawriter.ui.theme.SinhalaWriterTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : ComponentActivity() {
    private lateinit var converter: SinhalaConverter
    private lateinit var draftStore: DraftStore
    private lateinit var ads: AdsManager

    private var english by mutableStateOf(TextFieldValue())
    private var sinhala by mutableStateOf(TextFieldValue())
    private var screen by mutableStateOf(Screen.Writer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        converter = SinhalaConverter.fromResources(resources)
        draftStore = DraftStore(this)
        val draft = savedInstanceState?.getString(STATE_ENGLISH) ?: draftStore.load()
        english = TextFieldValue(draft, selection = TextRange(draft.length))
        val sinhalaText = savedInstanceState?.getString(STATE_SINHALA) ?: converter.convert(draft)
        sinhala = TextFieldValue(sinhalaText, selection = TextRange(sinhalaText.length))
        screen = savedInstanceState?.getString(STATE_SCREEN)?.let { name ->
            Screen.entries.firstOrNull { it.name == name }
        } ?: Screen.Writer

        initializeFirebase()
        ads = AdsManager(this).also { it.initialize() }

        setContent {
            SinhalaWriterTheme {
                WriterApp(
                    screen = screen,
                    english = english,
                    sinhala = sinhala,
                    banner = ads.banner,
                    onEnglishChange = ::updateEnglish,
                    onSinhalaChange = { sinhala = it },
                    onCopy = ::copySinhala,
                    onPaste = ::pasteText,
                    onClear = { updateEnglish(TextFieldValue()) },
                    onShare = ::shareSinhala,
                    onNavigate = { destination ->
                        ads.showInterstitial()
                        screen = destination
                    },
                    onBack = { screen = Screen.Writer },
                    onShowAd = { ads.showInterstitial() },
                    onOpenLink = ::openLink,
                    onEmail = { openIntent(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:apps.arvin@gmail.com"))) },
                    onCall = { openIntent(Intent(Intent.ACTION_DIAL, Uri.parse("tel:+94771775727"))) },
                    version = packageManager.getPackageInfo(packageName, 0).versionName ?: "2.0.8",
                )
            }
        }
    }

    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
            FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = true
        } catch (error: Exception) {
            Log.e(TAG, "Firebase initialization failed", error)
        }
    }

    private fun updateEnglish(value: TextFieldValue) {
        english = value
        val converted = converter.convert(value.text)
        val outputCursor = if (value.selection.end == value.text.length) converted.length
            else sinhala.selection.end.coerceAtMost(converted.length)
        sinhala = TextFieldValue(converted, selection = TextRange(outputCursor))
        draftStore.save(value.text)
    }

    private fun copySinhala(): Boolean {
        if (sinhala.text.isEmpty()) return false
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Sinhala text", sinhala.text))
        return true
    }

    private fun pasteText(append: Boolean) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = clipboard.primaryClip?.takeIf { it.itemCount > 0 }
            ?.getItemAt(0)?.coerceToText(this)?.toString() ?: return
        val newText = if (append) "${english.text} $text" else text
        updateEnglish(TextFieldValue(newText, selection = TextRange(newText.length)))
    }

    private fun shareSinhala(): Boolean {
        if (sinhala.text.isEmpty()) return false
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, sinhala.text)
        }
        startActivity(Intent.createChooser(intent, null))
        return true
    }

    private fun openLink(url: String) {
        try {
            CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))
        } catch (error: Exception) {
            Log.w(TAG, "Could not open link: $url", error)
        }
    }

    private fun openIntent(intent: Intent) {
        try {
            startActivity(intent)
        } catch (error: Exception) {
            Log.w(TAG, "No app available for ${intent.action}", error)
        }
    }

    override fun onPause() {
        if (::draftStore.isInitialized) draftStore.save(english.text)
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(STATE_ENGLISH, english.text)
        outState.putString(STATE_SINHALA, sinhala.text)
        outState.putString(STATE_SCREEN, screen.name)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        if (::ads.isInitialized) ads.destroy()
        super.onDestroy()
    }

    private companion object {
        const val TAG = "SinhalaWriter"
        const val STATE_ENGLISH = "english"
        const val STATE_SINHALA = "sinhala"
        const val STATE_SCREEN = "screen"
    }
}
