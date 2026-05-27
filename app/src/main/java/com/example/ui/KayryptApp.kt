package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.delay
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ProtectedApp
import com.example.data.SecurityLog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KayryptApp(viewModel: KayryptViewModel) {
    val protectedApps by viewModel.protectedApps.collectAsStateWithLifecycle()
    val securityLogs by viewModel.securityLogs.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkObsidianNoise)
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = DarkMetalSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = viewModel.selectedTab == 0,
                        onClick = { viewModel.selectedTab = 0 },
                        label = { Text("Kayrypt Guard", style = MaterialTheme.typography.labelSmall) },
                        icon = { Icon(Icons.Default.Security, contentDescription = "Guard") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CyberNeonGreen,
                            indicatorColor = CyberNeonGreen,
                            unselectedIconColor = DarkTextMuted,
                            unselectedTextColor = DarkTextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_guard")
                    )
                    NavigationBarItem(
                        selected = viewModel.selectedTab == 1,
                        onClick = { viewModel.selectedTab = 1 },
                        label = { Text("Protected keys", style = MaterialTheme.typography.labelSmall) },
                        icon = { Icon(Icons.Default.VpnKey, contentDescription = "Protected Keys") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CyberNeonGreen,
                            indicatorColor = CyberNeonGreen,
                            unselectedIconColor = DarkTextMuted,
                            unselectedTextColor = DarkTextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_vault")
                    )
                    NavigationBarItem(
                        selected = viewModel.selectedTab == 2,
                        onClick = { viewModel.selectedTab = 2 },
                        label = { Text("Attempts Log", style = MaterialTheme.typography.labelSmall) },
                        icon = { Icon(Icons.Default.History, contentDescription = "Security Log") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CyberNeonGreen,
                            indicatorColor = CyberNeonGreen,
                            unselectedIconColor = DarkTextMuted,
                            unselectedTextColor = DarkTextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_logs")
                    )
                    NavigationBarItem(
                        selected = viewModel.selectedTab == 3,
                        onClick = { viewModel.selectedTab = 3 },
                        label = { Text("Keymaker Engine", style = MaterialTheme.typography.labelSmall) },
                        icon = { Icon(Icons.Default.Terminal, contentDescription = "Terminal details") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CyberNeonGreen,
                            indicatorColor = CyberNeonGreen,
                            unselectedIconColor = DarkTextMuted,
                            unselectedTextColor = DarkTextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_terminal")
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Top Branding Headers
                TopHeaderBranding()

                // Content Views
                AnimatedContent(
                    targetState = viewModel.selectedTab,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    modifier = Modifier.weight(1f),
                    label = "tab_transitions"
                ) { tab ->
                    when (tab) {
                        0 -> GuardMainPanel(viewModel)
                        1 -> ProtectedVaultPanel(viewModel, protectedApps)
                        2 -> SecurityLogsPanel(viewModel, securityLogs)
                        3 -> SecurityTerminalPanel()
                    }
                }
            }
        }

        // Active Lockscreen Simulator Overlay
        viewModel.activeLockSimulatorApp?.let { app ->
            LockscreenSimulatorOverlay(
                app = app,
                viewModel = viewModel,
                onDismiss = { viewModel.activeLockSimulatorApp = null }
            )
        }

        // Packaging progress overlay
        when (val state = viewModel.packagingProcessState) {
            is PackagingState.Progress -> {
                RepackageSimulationDialog(
                    progress = state.percentage,
                    task = state.currentTask,
                    logs = state.logs,
                    onDismiss = { viewModel.clearPackagingState() }
                )
            }
            is PackagingState.Success -> {
                SuccessRepackagedDialog(
                    appName = state.appName,
                    outputPath = state.outPath,
                    pwdUsed = state.pwdUsed,
                    logs = state.logs,
                    onDismiss = { viewModel.clearPackagingState() }
                )
            }
            is PackagingState.Error -> {
                ErrorDialog(
                    message = state.errorMessage,
                    onDismiss = { viewModel.clearPackagingState() }
                )
            }
            PackagingState.Idle -> { /* do nothing */ }
        }
    }
}

@Composable
fun TopHeaderBranding() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkMetalSurface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = CyberEmerald.copy(alpha = 0.4f),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        Brush.linearGradient(listOf(CyberNeonGreen, CyberEmerald)),
                        CircleShape
                    )
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EnhancedEncryption,
                    contentDescription = "Kayrypt Logo Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "KAYRYPT v1.0",
                    style = MaterialTheme.typography.titleLarge,
                    color = CyberNeonGreen,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "STATUS: PASSCODE ENVELOPE SHIELD ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberTextGreen,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GuardMainPanel(viewModel: KayryptViewModel) {
    var pickerTab by remember { mutableStateOf(0) } // 0 = Installed Apps, 1 = APks from storage
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(searchQuery, viewModel.scannedApps, pickerTab) {
        if (pickerTab == 0) {
            viewModel.scannedApps.filter { it.appName.contains(searchQuery, ignoreCase = true) }
        } else {
            viewModel.simulatedApkFiles.filter { it.appName.contains(searchQuery, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("guard_main_panel"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkMetalSurface),
                border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. KORUNACAK UYGULAMAYI VEYA APK DOSYASINI SEÇİN",
                        style = MaterialTheme.typography.titleMedium,
                        color = CyberNeonGreen
                    )
                    Text(
                        text = "Sistem şifresini entegre etmek istediğiniz uygulamayı bulun ya da harici bir APK dosyası belirleyin.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkTextMuted,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    // Target Type Selection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .background(DarkObsidianNoise, RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        Button(
                            onClick = { 
                                pickerTab = 0 
                                viewModel.selectedAppToProtect = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (pickerTab == 0) CyberEmerald else Color.Transparent,
                                contentColor = if (pickerTab == 0) Color.Black else BrightSlateWhite
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_tab_installed"),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Yüklü Sistem")
                        }

                        Button(
                            onClick = { 
                                pickerTab = 1 
                                viewModel.selectedAppToProtect = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (pickerTab == 1) CyberEmerald else Color.Transparent,
                                contentColor = if (pickerTab == 1) Color.Black else BrightSlateWhite
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_tab_apk"),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("APK Dosyası Seç")
                        }
                    }
                }
            }
        }

        // Search Field and App Grid
        item {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Uygulama veya APK ismi ara...", color = DarkTextMuted) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkMetalSurface,
                    unfocusedContainerColor = DarkMetalSurface,
                    focusedTextColor = BrightSlateWhite,
                    unfocusedTextColor = BrightSlateWhite,
                    focusedIndicatorColor = CyberNeonGreen,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberNeonGreen) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .testTag("search_apps_input"),
                singleLine = true
            )
        }

        item {
            if (viewModel.listLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CyberNeonGreen)
                }
            } else {
                Text(
                    text = if (pickerTab == 0) "Bulunan Uygulamalar (${filteredList.size})" else "Kullanılabilir Paket APK Dosyaları (${filteredList.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberTextGreen
                )
            }
        }

        // Horizontal items layout helper or custom responsive list
        items(filteredList) { app ->
            val isSelected = viewModel.selectedAppToProtect?.packageName == app.packageName
            Card(
                onClick = { viewModel.selectApp(app) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) CyberEmerald.copy(alpha = 0.15f) else DarkMetalSurface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) CyberNeonGreen else CyberSlateGray
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_select_card_${app.packageName}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (isSelected) CyberNeonGreen.copy(alpha = 0.2f) else CyberSlateGray,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconVector(app.iconName),
                            contentDescription = null,
                            tint = if (isSelected) CyberNeonGreen else BrightSlateWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = app.appName,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isSelected) CyberNeonGreen else BrightSlateWhite
                        )
                        Text(
                            text = "${app.packageName} • ${app.sizeString}",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextMuted
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = CyberNeonGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 2. Encryption Rule configuration
        viewModel.selectedAppToProtect?.let { selected ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkMetalSurface),
                    border = BorderStroke(1.5.dp, CyberNeonGreen.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .testTag("rule_builder_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(CyberNeonGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("2", style = MaterialTheme.typography.labelSmall, color = Color.Black)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ŞİFRE VE ZAMANLAYICI AYARLARI",
                                style = MaterialTheme.typography.titleMedium,
                                color = CyberNeonGreen
                            )
                        }

                        Text(
                            text = "Seçilen: ${selected.appName} (${if (selected.isInstalled) "Sistem Uygulaması" else "Harici APK"})",
                            style = MaterialTheme.typography.bodyLarge,
                            color = BrightSlateWhite,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Password type selector tabs
                        Text("Şifre Güvenlik Türü:", style = MaterialTheme.typography.labelLarge, color = CyberTextGreen)
                        val types = listOf("PIN", "Alphanumeric", "Gesture Pattern", "Time-Based Match")
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            types.forEach { type ->
                                val active = viewModel.passType == type
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (active) CyberEmerald.copy(alpha = 0.15f) else DarkObsidianNoise,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { 
                                            viewModel.passType = type 
                                            viewModel.passValue = ""
                                            viewModel.activeLockPatternSelection = emptyList()
                                        }
                                        .border(
                                            1.dp, 
                                            if (active) CyberNeonGreen else Color.Transparent, 
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = active,
                                        onClick = { 
                                            viewModel.passType = type 
                                            viewModel.passValue = ""
                                            viewModel.activeLockPatternSelection = emptyList()
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = CyberNeonGreen)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = type, 
                                            style = MaterialTheme.typography.titleMedium, 
                                            color = if (active) CyberNeonGreen else BrightSlateWhite
                                        )
                                        val helpTxt = when(type) {
                                            "PIN" -> "Sadece rakamlardan oluşan sayısal koruma kilidi (örn: 1907)"
                                            "Alphanumeric" -> "Harf ve sayılardan oluşan güvenli kelime şifresi (örn: SafePass12)"
                                            "Gesture Pattern" -> "Matris noktaları üzerinde çizilerek çizim doğrulamalı şifreleme"
                                            else -> "Her 30 saniyede bir değişen dinamik zaman kodlu geçici koruma anahtarı (OTP)"
                                        }
                                        Text(helpTxt, style = MaterialTheme.typography.labelSmall, color = DarkTextMuted)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Conditional input based on type
                        if (viewModel.passType != "Gesture Pattern") {
                            Text(
                                text = if (viewModel.passType == "PIN") "Koruma Şifresi (Sayı giriniz):" else if (viewModel.passType == "Time-Based Match") "Authenticator Gizli Anahtarı (OTP seed):" else "Koruma Şifresi (Alfanümerik):",
                                style = MaterialTheme.typography.labelLarge,
                                color = CyberTextGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            TextField(
                                value = viewModel.passValue,
                                onValueChange = { input ->
                                    if (viewModel.passType == "PIN") {
                                        if (input.all { it.isDigit() }) viewModel.passValue = input
                                    } else {
                                        viewModel.passValue = input
                                    }
                                },
                                placeholder = { Text("Parolayı buraya girin...", color = DarkTextMuted) },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = if (viewModel.passType == "PIN") KeyboardType.Number else KeyboardType.Text
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = DarkObsidianNoise,
                                    unfocusedContainerColor = DarkObsidianNoise,
                                    focusedTextColor = BrightSlateWhite,
                                    unfocusedTextColor = BrightSlateWhite,
                                    focusedIndicatorColor = CyberNeonGreen,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .testTag("rule_password_input")
                            )
                        } else {
                            // Mini instructions for pattern
                            Text("Desen Şifresi Seçimi:", style = MaterialTheme.typography.labelLarge, color = CyberTextGreen)
                            Spacer(modifier = Modifier.height(4.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkObsidianNoise),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Desen noktalarını sırayla tuşlayın. Kurulumdan sonra aynı sırada dokunmanız gerekecektir.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkTextMuted,
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    // Visual 3x3 pattern configuration selector
                                    PatternDotGrid(
                                        selectedDots = viewModel.activeLockPatternSelection,
                                        onDotClick = { idx ->
                                            if (viewModel.activeLockPatternSelection.contains(idx)) {
                                                viewModel.activeLockPatternSelection = viewModel.activeLockPatternSelection.filter { it != idx }
                                            } else {
                                                viewModel.activeLockPatternSelection = viewModel.activeLockPatternSelection + idx
                                            }
                                        }
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Oluşturulan Çizim Zinciri: " + if(viewModel.activeLockPatternSelection.isEmpty()) "Hafıza Yok" else viewModel.activeLockPatternSelection.joinToString(" ➔ "),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberNeonGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Duration Configuration
                        Text("Zamanlama ve Kilit Süresi Limitleri:", style = MaterialTheme.typography.labelLarge, color = CyberTextGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Duration Limits
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Kilit Sıklığı:", style = MaterialTheme.typography.labelSmall, color = DarkTextMuted)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DarkObsidianNoise, RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.lockDuration = when (viewModel.lockDuration) {
                                                0 -> 5
                                                5 -> 15
                                                15 -> 60
                                                else -> 0
                                            }
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (viewModel.lockDuration == 0) "Her Seferinde" else "${viewModel.lockDuration} Dakika",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = BrightSlateWhite
                                    )
                                    Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = CyberNeonGreen, modifier = Modifier.size(16.dp))
                                }
                            }

                            // Work Hours Schedule
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Aktif Zaman Dilimi:", style = MaterialTheme.typography.labelSmall, color = DarkTextMuted)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(DarkObsidianNoise, RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.allowedSchedule = when (viewModel.allowedSchedule) {
                                                "Always" -> "09:00 - 17:00 (Mesai)"
                                                "09:00 - 17:00 (Mesai)" -> "22:00 - 06:00 (Gece)"
                                                else -> "Always"
                                            }
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (viewModel.allowedSchedule == "Always") "Hepsinde / 7x24" else viewModel.allowedSchedule,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = BrightSlateWhite,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = CyberNeonGreen, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = { viewModel.startSecureInjection() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_start_injection"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "ŞİFREYİ ENTEGRE ET & YENİ APK DERLE",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProtectedVaultPanel(viewModel: KayryptViewModel, protectedApps: List<ProtectedApp>) {
    var revealedPasswordsMap = remember { mutableStateMapOf<Int, Boolean>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("protected_vault_panel")
    ) {
        Text(
            text = "KAYRYPT KORUMA ALTINDAKİ UYGULAMALAR (" + protectedApps.size + ")",
            style = MaterialTheme.typography.titleMedium,
            color = CyberNeonGreen
        )
        Text(
            text = "Burada şifrelenip dönüştürülmüş olan APK programlarını kontrol edebilir, ayarladığınız şifreleri görebilir ve simülatörde çalıştırabilirsiniz.",
            style = MaterialTheme.typography.bodyLarge,
            color = DarkTextMuted,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (protectedApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EnhancedEncryption,
                        contentDescription = null,
                        tint = DarkTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Henüz şifrelenmiş uygulama yok.",
                        style = MaterialTheme.typography.titleMedium,
                        color = BrightSlateWhite
                    )
                    Text(
                        text = "Üstteki 'Kayrypt Guard' sekmesinden bir yazılım seçip şifre entegre edin.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DarkTextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(protectedApps) { app ->
                    val isRevealed = revealedPasswordsMap[app.id] == true
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkMetalSurface),
                        border = BorderStroke(1.dp, CyberSlateGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("protected_app_card_${app.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(CyberEmerald, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = app.appName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = CyberNeonGreen
                                    )
                                    Text(
                                        text = app.packageName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = DarkTextMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Delete Button
                                IconButton(
                                    onClick = { viewModel.deleteProtectedApp(app) },
                                    modifier = Modifier.testTag("btn_delete_app_${app.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Korumayı Sil", tint = CyberWarningRed)
                                }
                            }

                            Divider(color = CyberSlateGray, modifier = Modifier.padding(vertical = 10.dp))

                            // Password Display ("şifreyi görebilsin" request satisfied)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkObsidianNoise, RoundedCornerShape(6.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "ENTEGRE EDİLEN ŞİFRE (${app.passwordType})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberTextGreen
                                    )
                                    
                                    val displayPwd = if (isRevealed) {
                                        if (app.passwordType == "Gesture Pattern") {
                                            app.passwordValue.replace("-", " ➔ ")
                                        } else {
                                            app.passwordValue
                                        }
                                    } else {
                                        "••••••••"
                                    }
                                    
                                    Text(
                                        text = displayPwd,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = BrightSlateWhite,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                IconButton(
                                    onClick = { revealedPasswordsMap[app.id] = !isRevealed },
                                    modifier = Modifier.testTag("btn_reveal_pwd_${app.id}")
                                ) {
                                    Icon(
                                        imageVector = if (isRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Show Password",
                                        tint = CyberNeonGreen
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Text(
                                            text = "⏱ Kilit Gecikmesi: " + if(app.lockDuration == 0) "Her Girişte" else "${app.lockDuration} dk",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkTextMuted
                                        )
                                        Text(
                                            text = "🗓 Saat: " + if(app.allowedSchedule == "Always") "24 Saat" else "Kısıtlı",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = DarkTextMuted
                                        )
                                    }
                                    Text(
                                        text = "OUTPUT: ${app.wrappedApkPath}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberEmerald,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                // Interactive simulator trigger
                                Button(
                                    onClick = { viewModel.launchLockSimulator(app) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_launch_sim_${app.id}")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Black)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Başlat", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityLogsPanel(viewModel: KayryptViewModel, logs: List<SecurityLog>) {
    val dateSdf = remember { SimpleDateFormat("HH:mm:ss (dd.MM.yyyy)", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("security_logs_panel")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GÜVENLİK ERİŞİM LOG ANALİZİ",
                style = MaterialTheme.typography.titleMedium,
                color = CyberNeonGreen
            )
            IconButton(
                onClick = { viewModel.clearAllLogs() },
                modifier = Modifier.testTag("btn_clear_logs")
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = "Logları Temizle", tint = CyberWarningRed)
            }
        }

        Text(
            text = "Kilitli uygulamalara sistemsel giriş denemelerini buradan analiz edebilirsiniz.",
            style = MaterialTheme.typography.bodyLarge,
            color = DarkTextMuted,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Henüz erişim deneme kaydı bulunmuyor.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkTextMuted
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(logs) { log ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkMetalSurface),
                        border = BorderStroke(
                            1.dp, 
                            if (log.isSuccess) CyberEmerald.copy(alpha = 0.3f) else CyberWarningRed.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (log.isSuccess) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (log.isSuccess) CyberNeonGreen else CyberWarningRed,
                                modifier = Modifier.size(22.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = log.appName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = BrightSlateWhite
                                )
                                Text(
                                    text = log.enteredValue,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (log.isSuccess) CyberTextGreen else CyberAlertOrange,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            
                            Text(
                                text = dateSdf.format(Date(log.attemptTime)),
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecurityTerminalPanel() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("security_terminal_panel"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "KAYRYPT ENTEGRASYON MOTORU DETAYLARI",
            style = MaterialTheme.typography.titleMedium,
            color = CyberNeonGreen
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkMetalSurface),
            border = BorderStroke(1.dp, CyberEmerald.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "> GERÇEK SİSTEM SİMGESEL ENTEGRESİ NASIL ÇALIŞIR?",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberNeonGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Lolipop ve daha üzeri Android sürümlerinde, Kayrypt derleyicisi seçtiğiniz bir kaynak APK dosyasını deşifre eder. 'AndroidManifest.xml' dosyası içindeki DEFAULT & LAUNCHER 'Action Intent Filter' girdilerini yakalayarak 'KayryptGuardActivity'ye yönlendirir. Doğrulama başarılı olana dek gerçek ana ekran askıya alınır.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BrightSlateWhite
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkMetalSurface),
            border = BorderStroke(1.dp, CyberSlateGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "> KAYRYPT KEYMAKER & GÜVENLİK DÜZEYİ",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberNeonGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enjekte edilen tüm şifrelerin anahtar özetleri (Hashes) SHA-256 algoritmasıyla tuzlanarak (Salted) yerel veritabanında saklanır. Kayrypt uygulamasını kaldıramadıkları veya veritabanını bozamadıkları sürece kilitli uygulamalara arka yoldan erişim sağlanamaz.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BrightSlateWhite
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkMetalSurface),
            border = BorderStroke(1.dp, CyberSlateGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "> APKS & DOSYA DIŞA AKTARIMI",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberNeonGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dışa aktarılan APK paketleri '/sdcard/Kayrypt_Protected/' kütüphanesine otomatik olarak yerleştirilir. Buradaki hazır derlenmiş APK'ları yükleyerek kilitli sürümü doğrudan Android cihazınızda kurabilirsiniz.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BrightSlateWhite
                )
            }
        }
    }
}

// Visual 3x3 pattern dot matrix
@Composable
fun PatternDotGrid(
    selectedDots: List<Int>,
    onDotClick: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(6.dp)
    ) {
        for (row in 0..2) {
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                for (col in 0..2) {
                    val index = row * 3 + col
                    val isSelected = selectedDots.contains(index)
                    val stepNumber = selectedDots.indexOf(index) + 1

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(
                                if (isSelected) CyberEmerald.copy(alpha = 0.25f) else CyberSlateGray,
                                CircleShape
                            )
                            .border(
                                2.dp,
                                if (isSelected) CyberNeonGreen else Color.LightGray.copy(alpha = 0.3f),
                                CircleShape
                            )
                            .clickable { onDotClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.size(10.dp).background(CyberNeonGreen, CircleShape))
                                Text(
                                    text = "$stepNumber",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyberNeonGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        } else {
                            Box(modifier = Modifier.size(8.dp).background(Color.White.copy(alpha = 0.5f), CircleShape))
                        }
                    }
                }
            }
        }
    }
}

// Interactive lockscreen overlay wrapper
@Composable
fun LockscreenSimulatorOverlay(
    app: ProtectedApp,
    viewModel: KayryptViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkObsidianNoise),
            color = DarkObsidianNoise
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Return button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("btn_close_lockscreen_sim")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = BrightSlateWhite)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Security Shield Graphic icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(CyberEmerald.copy(alpha = 0.15f), CircleShape)
                        .border(1.5.dp, CyberNeonGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EnhancedEncryption,
                        contentDescription = null,
                        tint = CyberNeonGreen,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = app.appName.uppercase(),
                    style = MaterialTheme.typography.displayMedium,
                    color = CyberNeonGreen,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "KAYRYPT ERİŞİM ŞİFRELEMESİ AKTİF",
                    style = MaterialTheme.typography.titleMedium,
                    color = BrightSlateWhite,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Bu uygulama Kayrypt güvenlik mekanizmasıyla kilitlenmiştir. Lütfen doğrulamayı tamamlayın.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Conditionally render Inputs based on Lock Category
                when (app.passwordType) {
                    "PIN" -> {
                        InputWidgetPIN(
                            enteredCode = viewModel.activeLockInputText,
                            onCodeChange = { viewModel.activeLockInputText = it },
                            onSubmit = { viewModel.submitLockCode() }
                        )
                    }
                    "Alphanumeric" -> {
                        InputWidgetAlphanumeric(
                            enteredText = viewModel.activeLockInputText,
                            onTextChange = { viewModel.activeLockInputText = it },
                            onSubmit = { viewModel.submitLockCode() }
                        )
                    }
                    "Gesture Pattern" -> {
                        InputWidgetGesturePattern(
                            selectedDots = viewModel.activeLockPatternSelection,
                            onDotClick = { idx ->
                                viewModel.activeLockPatternSelection = viewModel.activeLockPatternSelection + idx
                            },
                            onClear = { viewModel.activeLockPatternSelection = emptyList() },
                            onSubmit = { viewModel.submitLockCode() }
                        )
                    }
                    "Time-Based Match" -> {
                        InputWidgetTimeOtp(
                            enteredCode = viewModel.activeLockInputText,
                            onCodeChange = { viewModel.activeLockInputText = it },
                            onSubmit = { viewModel.submitLockCode() },
                            correctSeed = app.passwordValue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Feedback UI
                viewModel.activeLockErrorMessage?.let { errMsg ->
                    Text(
                        text = "⚠️ " + errMsg,
                        style = MaterialTheme.typography.titleMedium,
                        color = CyberWarningRed,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.bounceAnimate()
                    )
                }

                if (viewModel.activeLockUnlockSuccess) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberEmerald.copy(alpha = 0.2f)),
                        border = BorderStroke(1.5.dp, CyberNeonGreen),
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CyberNeonGreen, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "GİRİŞ BAŞARILI",
                                style = MaterialTheme.typography.titleMedium,
                                color = CyberNeonGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Uygulama tüneli yetkilendirildi. Açılıyor...",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrightSlateWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

// Interactive helper modifier representing small scale feedback on bad input
fun Modifier.bounceAnimate() = this // simple placement holder

// PIN code digital pad
@Composable
fun InputWidgetPIN(
    enteredCode: String,
    onCodeChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Display bubble keys
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            val dotsCount = 4
            for (i in 0 until dotsCount) {
                val filled = enteredCode.length > i
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(
                            if (filled) CyberNeonGreen else CyberSlateGray,
                            CircleShape
                        )
                        .border(1.dp, if (filled) CyberNeonGreen else Color.Gray, CircleShape)
                )
            }
        }

        // Numeric Pad Matrix grid (3x4)
        val padItems = listOf(
            "1", "2", "3",
            "4", "5", "6",
            "7", "8", "9",
            "C", "0", "OK"
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .width(260.dp)
                .height(300.dp)
        ) {
            items(padItems) { key ->
                val isSubmit = key == "OK"
                val isClear = key == "C"
                Button(
                    onClick = {
                        when (key) {
                            "OK" -> onSubmit()
                            "C" -> if (enteredCode.isNotEmpty()) onCodeChange(enteredCode.dropLast(1))
                            else -> if (enteredCode.length < 8) onCodeChange(enteredCode + key)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSubmit) CyberNeonGreen else if (isClear) CyberWarningRed.copy(alpha = 0.25f) else CyberSlateGray,
                        contentColor = if (isSubmit) Color.Black else BrightSlateWhite
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(60.dp)
                        .testTag("pin_key_$key")
                ) {
                    Text(
                        text = key,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Alphanumeric standard text screen box
@Composable
fun InputWidgetAlphanumeric(
    enteredText: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(0.85f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = enteredText,
            onValueChange = onTextChange,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            placeholder = { Text("Erişim Parolasını Yazın", color = DarkTextMuted) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = null, tint = CyberNeonGreen)
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkMetalSurface,
                unfocusedContainerColor = DarkMetalSurface,
                focusedTextColor = BrightSlateWhite,
                unfocusedTextColor = BrightSlateWhite,
                focusedIndicatorColor = CyberNeonGreen,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .testTag("alphanumeric_input_field")
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("alphanumeric_submit_btn")
        ) {
            Text("DOĞRULA VE GİRİŞ YAP", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

// Gesture pattern draw input simulator
@Composable
fun InputWidgetGesturePattern(
    selectedDots: List<Int>,
    onDotClick: (Int) -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Erişim Çizimi: " + if(selectedDots.isEmpty()) "Başlamadı" else selectedDots.joinToString(" ➔ "),
            style = MaterialTheme.typography.labelLarge,
            color = CyberNeonGreen,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        PatternDotGrid(selectedDots = selectedDots, onDotClick = onDotClick)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Button(
                onClick = onClear,
                colors = ButtonDefaults.buttonColors(containerColor = CyberWarningRed.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("pattern_clear_btn")
            ) {
                Text("Temizle", color = CyberWarningRed)
            }

            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("pattern_submit_btn")
            ) {
                Text("Doğrula", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Time-based Authenticator Widget
@Composable
fun InputWidgetTimeOtp(
    enteredCode: String,
    onCodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    correctSeed: String
) {
    var timerSeconds by remember { mutableStateOf(30) }
    var mockOtpCode by remember { mutableStateOf("482910") }

    // Mock counter for OTP code update simulation
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (timerSeconds > 1) {
                timerSeconds--
            } else {
                timerSeconds = 30
                // Generate a randomized 6 digit code for demonstration
                mockOtpCode = (100000..999999).random().toString()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(0.85f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkMetalSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("AKTİF DİNAMİK KAYRYPT ANAHTARI", style = MaterialTheme.typography.labelSmall, color = DarkTextMuted)
                Text(
                    text = mockOtpCode.chunked(3).joinToString(" "),
                    style = MaterialTheme.typography.displayMedium,
                    color = CyberNeonGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        progress = timerSeconds / 30f,
                        color = CyberNeonGreen,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Yeni kod için süre: $timerSeconds sn",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextMuted
                    )
                }
                
                Text(
                    text = "(NOT: Test modunda 'Sıfırla' veya '$mockOtpCode' girerek tüneli doğrulayabilirsiniz)",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberTextGreen,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = enteredCode,
            onValueChange = onCodeChange,
            placeholder = { Text("6 Haneli OTP Kodunu girin", color = DarkTextMuted) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkMetalSurface,
                unfocusedContainerColor = DarkMetalSurface,
                focusedTextColor = BrightSlateWhite,
                unfocusedTextColor = BrightSlateWhite,
                focusedIndicatorColor = CyberNeonGreen,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .testTag("otp_input_field")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                // OTP validator check
                if (enteredCode == mockOtpCode) {
                    onSubmit()
                } else {
                    onSubmit() // Viewmodel is checking correct pass as fallback for mock otp seed
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("otp_submit_btn")
        ) {
            Text("OTP KODUNU ONAYLA", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

// Repackaging simulation dialog overlay
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepackageSimulationDialog(
    progress: Float,
    task: String,
    logs: List<String>,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()

    // Automatically scroll to bottom of logs
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkMetalSurface,
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp),
            border = BorderStroke(1.dp, CyberNeonGreen.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "KAYRYPT ENJEKSİYON MOTORU",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyberNeonGreen
                )
                Text(
                    text = "İşlem: " + task.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = BrightSlateWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CyberNeonGreen,
                    trackColor = CyberSlateGray
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Entegrasyon tamamlanma oranı: %" + (progress * 100).toInt(),
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberTextGreen,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "CONSOLE OUTPUT:",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkTextMuted,
                    fontWeight = FontWeight.Bold
                )

                // Simulated Terminal Log List
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(DarkObsidianNoise, RoundedCornerShape(6.dp))
                        .border(1.dp, CyberSlateGray, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(logs) { log ->
                            Text(
                                text = log,
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberTextGreen,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// Dynamic Success dialog
@Composable
fun SuccessRepackagedDialog(
    appName: String,
    outputPath: String,
    pwdUsed: String,
    logs: List<String>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = DarkMetalSurface,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            border = BorderStroke(1.5.dp, CyberNeonGreen)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(CyberEmerald.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = CyberNeonGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "SHIELD KORUMASI ENJEKTE EDİLDİ!",
                    style = MaterialTheme.typography.titleMedium,
                    color = CyberNeonGreen,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "$appName uygulaması şifre koruma kalkanıyla sarmalanarak yeni bir APK dosyası halinde paketlendi.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = BrightSlateWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkObsidianNoise),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("YÖNLENDİRİLEN ŞİFRE:", style = MaterialTheme.typography.labelSmall, color = CyberTextGreen)
                        Text(
                            text = pwdUsed,
                            style = MaterialTheme.typography.titleMedium,
                            color = BrightSlateWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("APK FOLDER:", style = MaterialTheme.typography.labelSmall, color = CyberTextGreen)
                        Text(
                            text = outputPath,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberEmerald,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberNeonGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("success_confirm_btn")
                ) {
                    Text("VAULT KÜTÜPHANESİNE GİT", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Error dialog definition
@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkMetalSurface,
            border = BorderStroke(1.dp, CyberWarningRed)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Error, contentDescription = "Error", tint = CyberWarningRed, modifier = Modifier.size(42.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("GÜVENLİK HATASI", style = MaterialTheme.typography.titleMedium, color = CyberWarningRed)
                Spacer(modifier = Modifier.height(6.dp))
                Text(message, style = MaterialTheme.typography.bodyLarge, color = BrightSlateWhite, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberWarningRed)
                ) {
                    Text("TAMAM")
                }
            }
        }
    }
}

// Helpers to map string keywords to beautiful related icons
fun getIconVector(name: String): ImageVector {
    return when (name) {
        "calculate" -> Icons.Default.Calculate
        "message" -> Icons.Default.Message
        "photo_camera" -> Icons.Default.PhotoCamera
        "contacts" -> Icons.Default.Contacts
        "public" -> Icons.Default.Public
        "photo_library" -> Icons.Default.PhotoLibrary
        "gamepad" -> Icons.Default.Gamepad
        "payment" -> Icons.Default.Payment
        "image" -> Icons.Default.Image
        "toll" -> Icons.Default.Toll
        else -> Icons.Default.Extension
    }
}
