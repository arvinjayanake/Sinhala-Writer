package com.arvin.sinhalawriter

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.mediation.banner.LevelPlayBannerAdView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Screen { Writer, Help, About }

@Composable
private fun QuillIcon(modifier: Modifier = Modifier) {
    // The project's launcher foreground contains the original feather and a dark shadow.
    // Use the feather's alpha mask with the current theme's foreground color.
    val iconColor = MaterialTheme.colorScheme.onSurface
    val featherOnly = remember(iconColor) {
        ColorFilter.colorMatrix(ColorMatrix(floatArrayOf(
            0f, 0f, 0f, 0f, iconColor.red * 255f,
            0f, 0f, 0f, 0f, iconColor.green * 255f,
            0f, 0f, 0f, 0f, iconColor.blue * 255f,
            2.5f, 0f, 0f, 0f, -380f,
        )))
    }
    Image(
        painter = painterResource(R.mipmap.ic_launcher_foreground),
        contentDescription = null,
        modifier = modifier,
        colorFilter = featherOnly,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriterApp(
    screen: Screen,
    english: TextFieldValue,
    sinhala: TextFieldValue,
    banner: LevelPlayBannerAdView?,
    bannerSlotVisible: Boolean,
    onEnglishChange: (TextFieldValue) -> Unit,
    onSinhalaChange: (TextFieldValue) -> Unit,
    onCopy: () -> Boolean,
    onPaste: (Boolean) -> Unit,
    onClear: () -> Unit,
    onShare: () -> Boolean,
    onNavigate: (Screen) -> Unit,
    onBack: () -> Unit,
    onShowAd: () -> Unit,
    onOpenLink: (String) -> Unit,
    onEmail: () -> Unit,
    onCall: () -> Unit,
    version: String,
) {
    var menuOpen by remember { mutableStateOf(false) }
    var pasteDialog by remember { mutableStateOf(false) }
    var clearDialog by remember { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val keyboard = LocalSoftwareKeyboardController.current
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    BackHandler(screen != Screen.Writer) { onBack() }

    Scaffold(
        modifier = Modifier.navigationBarsPadding().imePadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Text(when (screen) {
                        Screen.Writer -> "Sinhala Writer"
                        Screen.Help -> "Help"
                        Screen.About -> "About"
                    }, modifier = Modifier.padding(start = 6.dp).offset(y = 5.dp))
                },
                navigationIcon = {
                    if (screen == Screen.Writer) {
                        QuillIcon(Modifier.size(56.dp).offset(x = (-3).dp, y = 5.dp))
                    } else {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                    }
                },
                actions = {
                    if (screen == Screen.Writer) {
                        IconButton(modifier = Modifier.offset(y = 5.dp), onClick = {
                            keyboard?.hide()
                            if (!onCopy()) scope.launch { snackbar.showSnackbar("Text is empty!") }
                            onShowAd()
                        }) { Icon(Icons.Default.ContentCopy, contentDescription = "Copy Sinhala Text") }
                        IconButton(modifier = Modifier.offset(y = 5.dp), onClick = {
                            if (sinhala.text.isEmpty()) {
                                onPaste(false)
                                onShowAd()
                            } else {
                                pasteDialog = true
                            }
                        }) { Icon(Icons.Default.ContentPaste, contentDescription = "Paste English Text") }
                        Box {
                            IconButton(modifier = Modifier.offset(y = 5.dp), onClick = { menuOpen = true }) { Icon(Icons.Default.MoreVert, contentDescription = "More options") }
                            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                                listOf("Clear All", "Share Text", "Help", "About").forEach { item ->
                                    DropdownMenuItem(text = { Text(item, fontSize = 18.sp) }, onClick = {
                                        menuOpen = false
                                        when (item) {
                                            "Clear All" -> clearDialog = true
                                            "Share Text" -> {
                                                keyboard?.hide()
                                                if (!onShare()) scope.launch { snackbar.showSnackbar("Text is empty!") }
                                                onShowAd()
                                            }
                                            "Help" -> onNavigate(Screen.Help)
                                            "About" -> onNavigate(Screen.About)
                                        }
                                    })
                                }
                            }
                        }
                    }
                },
                expandedHeight = 56.dp,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
            )
        },
        bottomBar = {
            if (bannerSlotVisible && !imeVisible) {
                Box(
                    Modifier.fillMaxWidth().height(58.dp).padding(top = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (banner != null) {
                        AndroidView(
                            factory = { banner },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                        )
                    }
                }
            }
        },
    ) { padding ->
        when (screen) {
            Screen.Writer -> WriterScreen(
                english, sinhala, onEnglishChange, onSinhalaChange,
                Modifier.fillMaxSize().padding(padding),
            )
            Screen.Help -> HelpScreen(Modifier.fillMaxSize().padding(padding))
            Screen.About -> AboutScreen(
                version, onOpenLink, onEmail, onCall,
                Modifier.fillMaxSize().padding(padding),
            )
        }
    }

    if (pasteDialog) {
        AlertDialog(
            onDismissRequest = { pasteDialog = false; onShowAd() },
            title = { Text("Sinhala Writer") },
            text = { Text("Choose an action for pasting the clipboard text.", fontSize = 18.sp) },
            confirmButton = {
                TextButton(onClick = { pasteDialog = false; onPaste(false); onShowAd() }) {
                    Text("Clear & Paste")
                }
            },
            dismissButton = {
                TextButton(onClick = { pasteDialog = false; onPaste(true); onShowAd() }) {
                    Text("Append")
                }
            },
        )
    }

    if (clearDialog) {
        AlertDialog(
            onDismissRequest = { clearDialog = false; onShowAd() },
            title = { Text("Sinhala Writer") },
            text = { Text("Are you sure, you want to clear all? This action cannot be undone.", fontSize = 18.sp) },
            confirmButton = {
                TextButton(onClick = { clearDialog = false; onClear(); onShowAd() }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { clearDialog = false; onShowAd() }) { Text("No") }
            },
        )
    }
}

@Composable
private fun WriterScreen(
    english: TextFieldValue,
    sinhala: TextFieldValue,
    onEnglishChange: (TextFieldValue) -> Unit,
    onSinhalaChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    val englishFocus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    var sinhalaFocused by remember { mutableStateOf(false) }
    val floatingSinhalaLabel = sinhala.text.isNotEmpty() || sinhalaFocused

    LaunchedEffect(Unit) {
        englishFocus.requestFocus()
        delay(250)
        keyboard?.hide()
    }

    Column(modifier) {
        Box(Modifier.fillMaxWidth().weight(1f).padding(
            start = 8.dp,
            top = if (floatingSinhalaLabel) 8.dp else 16.dp,
            end = 8.dp,
        )) {
            OutlinedTextField(
                value = sinhala,
                onValueChange = onSinhalaChange,
                label = if (floatingSinhalaLabel) {
                    { Text("Sinhala") }
                } else null,
                modifier = Modifier.fillMaxSize().onFocusChanged { sinhalaFocused = it.hasFocus },
                textStyle = TextStyle(fontSize = 20.sp, lineHeight = 28.sp),
                shape = RoundedCornerShape(8.dp),
            )
            if (!floatingSinhalaLabel) {
                Text(
                    "Sinhala",
                    Modifier.align(Alignment.CenterStart).padding(start = 12.dp),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        OutlinedTextField(
            value = english,
            onValueChange = onEnglishChange,
            label = { Text("Type in English") },
            modifier = Modifier.fillMaxWidth().padding(8.dp).height(160.dp).focusRequester(englishFocus),
            textStyle = TextStyle(fontSize = 20.sp, lineHeight = 28.sp),
            keyboardOptions = KeyboardOptions(autoCorrectEnabled = false),
            minLines = 4,
            maxLines = 4,
            shape = RoundedCornerShape(8.dp),
        )
    }
}

@Composable
private fun HelpScreen(modifier: Modifier = Modifier) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        HelpData.sections.forEach { section ->
            var expanded by rememberSaveable(section.title) { mutableStateOf(false) }
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    QuillIcon(Modifier.size(48.dp))
                    Text(section.title, modifier = Modifier.weight(1f).padding(start = 32.dp), fontSize = 16.sp)
                    Text(if (expanded) "⌃" else "⌄", fontSize = 20.sp)
                }
                if (expanded) {
                    section.entries.forEach { (sin, eng) ->
                        Text("$sin - $eng", Modifier.fillMaxWidth().padding(start = 80.dp, top = 4.dp, end = 24.dp, bottom = 4.dp), fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AboutScreen(
    version: String,
    onOpenLink: (String) -> Unit,
    onEmail: () -> Unit,
    onCall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        Image(
            painter = painterResource(R.drawable.arvin_apps),
            contentDescription = "Arvin Apps",
            modifier = Modifier.size(100.dp).align(Alignment.CenterHorizontally),
        )
        Spacer(Modifier.height(16.dp))
        Text("Arvin Apps", Modifier.fillMaxWidth(), fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("Mobile Specialist", Modifier.fillMaxWidth(), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Text("Sinhala Writer (${version}v)", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(
            "The ultimate app for effortless Sinhala typing on your mobile device! Designed to make writing in Sinhala as simple as sending an message.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(24.dp))
        Text("Contact Information", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Email, contentDescription = null)
            TextButton(onClick = onEmail) { Text("apps.arvin@gmail.com") }
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Phone, contentDescription = null)
            TextButton(onClick = onCall) { Text("+9477 177 5727") }
        }
        Spacer(Modifier.height(24.dp))
        Text("Connect with me:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Language, contentDescription = null)
            TextButton(onClick = { onOpenLink("https://arvinapps.com/") }) { Text("Website") }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Link, contentDescription = null)
            TextButton(onClick = { onOpenLink("https://www.linkedin.com/in/arvin-jayanake-45a86478/") }) { Text("LinkedIn") }
        }
    }
}
