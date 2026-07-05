package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.*
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

                if (currentUser == null) {
                    LoginScreen(viewModel = viewModel, onLoginSuccess = {
                        // Handled automatically by Flow
                    })
                } else {
                    MainAppFrame(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppFrame(viewModel: AppViewModel) {
    var selectedTab by remember { mutableStateOf("home") }
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotificationsCount = notifications.count { it.status == "Unread" }
    
    var showNotifDialog by remember { mutableStateOf(false) }
    var showMenuDropdown by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ADINDO HR",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CorpBluePrimary
                        )
                        Text(
                            text = "OPERATIONS SYSTEM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CorpTextSecondary
                        )
                    }
                },
                actions = {
                    // Notification Bell with Badge
                    IconButton(onClick = { showNotifDialog = true }) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(containerColor = Color.Red) {
                                        Text(unreadNotificationsCount.toString(), color = Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifikasi",
                                tint = CorpBluePrimary
                            )
                        }
                    }

                    // Settings / Profile Dropdown
                    Box {
                        IconButton(onClick = { showMenuDropdown = !showMenuDropdown }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = CorpBluePrimary
                            )
                        }
                        DropdownMenu(
                            expanded = showMenuDropdown,
                            onDismissRequest = { showMenuDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Pengaturan & Sync") },
                                leadingIcon = { Icon(Icons.Default.Settings, "sync") },
                                onClick = {
                                    selectedTab = "settings"
                                    showMenuDropdown = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Log Aktivitas") },
                                leadingIcon = { Icon(Icons.Default.History, "logs") },
                                onClick = {
                                    selectedTab = "logs"
                                    showMenuDropdown = false
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Keluar (Logout)", color = Color.Red) },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, "logout", tint = Color.Red) },
                                onClick = {
                                    viewModel.logout()
                                    showMenuDropdown = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = selectedTab == "home",
                    onClick = { selectedTab = "home" },
                    icon = { Icon(Icons.Default.Dashboard, "Home") },
                    label = { Text("Beranda", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CorpBluePrimary,
                        selectedTextColor = CorpBluePrimary,
                        indicatorColor = CorpBluePrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == "perumahan",
                    onClick = { selectedTab = "perumahan" },
                    icon = { Icon(Icons.Default.HomeWork, "Housing") },
                    label = { Text("Rumah", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CorpBluePrimary,
                        selectedTextColor = CorpBluePrimary,
                        indicatorColor = CorpBluePrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == "guesthouse",
                    onClick = { selectedTab = "guesthouse" },
                    icon = { Icon(Icons.Default.Hotel, "Guest House") },
                    label = { Text("GH", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CorpBluePrimary,
                        selectedTextColor = CorpBluePrimary,
                        indicatorColor = CorpBluePrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == "asset",
                    onClick = { selectedTab = "asset" },
                    icon = { Icon(Icons.Default.Category, "Assets") },
                    label = { Text("Aset", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CorpBluePrimary,
                        selectedTextColor = CorpBluePrimary,
                        indicatorColor = CorpBluePrimary.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == "mobil",
                    onClick = { selectedTab = "mobil" },
                    icon = { Icon(Icons.Default.DirectionsCar, "Vehicle") },
                    label = { Text("Mobil", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CorpBluePrimary,
                        selectedTextColor = CorpBluePrimary,
                        indicatorColor = CorpBluePrimary.copy(alpha = 0.1f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                "home" -> DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToSection = { section -> selectedTab = section }
                )
                "perumahan" -> PerumahanScreen(viewModel = viewModel)
                "guesthouse" -> GuestHouseScreen(viewModel = viewModel)
                "asset" -> AssetScreen(viewModel = viewModel)
                "mobil" -> RequestMobilScreen(viewModel = viewModel)
                "settings" -> SyncSettingsScreen(viewModel = viewModel)
                "logs" -> ActivityLogsScreen(viewModel = viewModel)
            }
        }
    }

    // Notifications Dialog
    if (showNotifDialog) {
        Dialog(onDismissRequest = { showNotifDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notifikasi Sistem", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpBluePrimary)
                        IconButton(onClick = { showNotifDialog = false }) {
                            Icon(Icons.Default.Close, "close")
                        }
                    }

                    HorizontalDivider()

                    Box(modifier = Modifier.weight(1f)) {
                        if (notifications.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Tidak ada notifikasi", color = CorpTextSecondary)
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    notifications.forEach { notif ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    coroutineScope.launch {
                                                        viewModel.repository.markNotificationAsRead(notif.id)
                                                    }
                                                },
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (notif.status == "Unread") Color(0xFFEBF3FC) else Color(0xFFF9FAFB)
                                            ),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(notif.judul, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CorpTextPrimary)
                                                    Text(notif.tanggal, fontSize = 9.sp, color = CorpTextSecondary)
                                                }
                                                Text(notif.isi, fontSize = 11.sp, color = CorpTextSecondary)
                                            }
                                        }
                                    }
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
fun ActivityLogsScreen(viewModel: AppViewModel) {
    val logs by viewModel.activityLogs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorpLightBg)
            .padding(16.dp)
    ) {
        Text("Log Aktivitas Aplikasi", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpBluePrimary)
        Text("Catatan riwayat tindakan audit log yang terjadi di sistem.", fontSize = 11.sp, color = CorpTextSecondary)

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        if (logs.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Belum ada log aktivitas", color = CorpTextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(logs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("[${log.modul}]", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CorpGreenAccent)
                                Text("${log.tanggal} • ${log.jam}", fontSize = 9.sp, color = CorpTextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(log.aktivitas, fontSize = 12.sp, color = CorpTextPrimary)
                            Text("Oleh: ${log.nama}", fontSize = 10.sp, color = CorpTextSecondary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
