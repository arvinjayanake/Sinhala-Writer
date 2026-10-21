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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.unity3d.mediation.banner.LevelPlayBannerAdView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class Screen { Writer, Help, About }

private const val WEBSITE_URL = "https://sinhala-writer.web.app/"
private const val PRIVACY_URL = "https://sinhala-writer.web.app/privacy.html"
private const val TERMS_URL = "https://sinhala-writer.web.app/terms.html"

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
                                listOf("Clear All", "Share Text", "Help", "Privacy Policy", "Terms", "About").forEach { item ->
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
                                            "Privacy Policy" -> onOpenLink(PRIVACY_URL)
                                            "Terms" -> onOpenLink(TERMS_URL)
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
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp),
    ) {
        AndroidView(
            factory = { viewContext ->
                android.widget.ImageView(viewContext).apply {
                    setImageResource(R.mipmap.ic_launcher)
                    contentDescription = "Sinhala Writer"
                    scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
                }
            },
            modifier = Modifier.padding(bottom = 16.dp).size(92.dp),
        )
        Text(
            "Sinhala Writer",
            color = colors.primary,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            "Write Sinhala naturally, using an English keyboard.",
            modifier = Modifier.padding(top = 8.dp),
            color = colors.onSurfaceVariant,
            fontSize = 16.sp,
            lineHeight = 24.sp,
        )
        Spacer(Modifier.height(28.dp))
        HorizontalDivider(color = colors.outlineVariant)

        AboutSection(
            "About the app",
            "Sinhala Writer turns words typed with an English keyboard into Sinhala as you write. It makes everyday Sinhala messages and notes easy to compose on your phone.",
        )
        AboutSection(
            "Type and edit",
            "See the Sinhala result while you type. You can also edit the converted text directly, then copy or share it when it is ready.",
        )
        AboutSection(
            "Keep your work",
            "Your English draft is saved on this device so you can continue where you left off. Paste text to convert it, or clear both fields to start again.",
        )

        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = colors.outlineVariant)
        Text(
            "Developer",
            modifier = Modifier.padding(top = 24.dp),
            color = colors.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        Image(
            painter = painterResource(R.drawable.arvin_apps),
            contentDescription = "Arvin Apps",
            modifier = Modifier.padding(top = 16.dp).size(width = 104.dp, height = 78.dp),
            colorFilter = ColorFilter.tint(colors.onSurface),
        )
        DeveloperDetail("Arvin Apps", Icons.Filled.Business, topPadding = 14.dp, fontWeight = FontWeight.SemiBold)
        DeveloperDetail("Arvin Jayanake", Icons.Filled.Person)
        DeveloperDetail("apps.arvin@gmail.com", Icons.Filled.Email, accent = true, onClick = onEmail)
        DeveloperDetail("+94 77 177 5727", Icons.Filled.Phone, accent = true, onClick = onCall)

        Spacer(Modifier.height(28.dp))
        HorizontalDivider(color = colors.outlineVariant)
        Text(
            "Links",
            modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            color = colors.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        DeveloperDetail("sinhala-writer.web.app", Icons.Filled.Language, topPadding = 14.dp, accent = true, onClick = { onOpenLink(WEBSITE_URL) })
        DeveloperDetail("Privacy Policy", Icons.Filled.Link, accent = true, onClick = { onOpenLink(PRIVACY_URL) })
        DeveloperDetail("Terms", Icons.Filled.Link, accent = true, onClick = { onOpenLink(TERMS_URL) })

        Text(
            "Version $version",
            modifier = Modifier.padding(top = 28.dp, bottom = 16.dp),
            color = colors.onSurfaceVariant,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun AboutSection(title: String, body: String) {
    val colors = MaterialTheme.colorScheme
    Text(
        title,
        modifier = Modifier.padding(top = 24.dp),
        color = colors.onSurface,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
    Text(
        body,
        modifier = Modifier.padding(top = 8.dp),
        color = colors.onSurfaceVariant,
        fontSize = 15.sp,
        lineHeight = 23.sp,
    )
}

@Composable
private fun DeveloperDetail(
    text: String,
    icon: ImageVector,
    topPadding: Dp = 4.dp,
    fontWeight: FontWeight = FontWeight.Normal,
    accent: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val color = if (accent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier.padding(top = topPadding).then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = color)
        Text(
            text,
            modifier = Modifier.padding(start = 8.dp),
            color = color,
            fontSize = 16.sp,
            fontWeight = fontWeight,
            lineHeight = 24.sp,
        )
    }
}
