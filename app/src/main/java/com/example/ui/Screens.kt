@file:Suppress("DEPRECATION")
package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.theme.*
import com.example.viewmodel.AppViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

// =========================================================================
// 1. LOGIN SCREEN
// =========================================================================
@Composable
fun LoginScreen(
    viewModel: AppViewModel,
    onLoginSuccess: () -> Unit
) {
    var emailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CorpLightBg, Color(0xFFE6EEF8))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Enterprise Logo Emblem
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(CorpBluePrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.BusinessCenter,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ADINDO HR OPERATIONS",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = CorpBluePrimary,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = "PT Adindo Hutani Lestari",
                fontSize = 13.sp,
                color = CorpTextSecondary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card Form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masuk Akun Perusahaan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CorpTextPrimary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "Silakan masuk menggunakan kredensial Google Sign-In Perusahaan Anda.",
                        fontSize = 12.sp,
                        color = CorpTextSecondary,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp, bottom = 20.dp)
                    )

                    // Email Field
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email Google Perusahaan") },
                        placeholder = { Text("nama@company.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CorpBluePrimary,
                            unfocusedBorderColor = Color(0xFFD0D5DD)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name Field
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nama Lengkap") },
                        placeholder = { Text("contoh: Adi Wijaya") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Name")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CorpBluePrimary,
                            unfocusedBorderColor = Color(0xFFD0D5DD)
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Simulate Google Sign In Button
                    Button(
                        onClick = {
                            if (emailInput.isEmpty()) {
                                errorMessage = "Mohon masukkan email Google Perusahaan Anda."
                                showErrorDialog = true
                                return@Button
                            }
                            viewModel.login(emailInput) { success, message ->
                                if (success) {
                                    Toast.makeText(context, "Selamat datang kembali!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                } else {
                                    errorMessage = message
                                    showErrorDialog = true
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Sign In Icon",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Google Sign-In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info text at bottom
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = "Secure",
                    tint = CorpGreenAccent,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Aman • Hanya diakses email @company.com / @perusahaan.com",
                    fontSize = 11.sp,
                    color = CorpTextSecondary
                )
            }
        }
    }

    // Access Denied Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK", color = CorpBluePrimary, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alert",
                        tint = Color.Red,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Akses Ditolak", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Text(errorMessage, fontSize = 14.sp, color = CorpTextPrimary)
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}

// =========================================================================
// 2. DASHBOARD SCREEN
// =========================================================================
@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    onNavigateToSection: (String) -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    
    // Read datasets
    val rumahList by viewModel.rumah.collectAsStateWithLifecycle()
    val reportsList by viewModel.laporanPerumahan.collectAsStateWithLifecycle()
    val assetsList by viewModel.assets.collectAsStateWithLifecycle()
    val transfersList by viewModel.transfers.collectAsStateWithLifecycle()
    val guesthousesList by viewModel.guestHouses.collectAsStateWithLifecycle()
    val bookingsList by viewModel.bookings.collectAsStateWithLifecycle()
    val requestsList by viewModel.carRequests.collectAsStateWithLifecycle()

    // Calculate counts
    val totalHouses = rumahList.size
    val damagedHouses = rumahList.count { it.statusRumah == "Rusak Ringan" || it.statusRumah == "Rusak Berat" }
    val totalAssets = assetsList.size
    val activeTransfers = transfersList.count { it.status == "Pending" }
    val occupiedGH = guesthousesList.count { it.status == "Terisi" }
    val vacantGH = guesthousesList.count { it.status == "Tersedia" }
    val totalCarRequests = requestsList.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CorpLightBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Profile Welcome Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CorpBluePrimary)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.nama ?: "U").take(1).uppercase(),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Halo, ${currentUser?.nama ?: "Rekan Kerja"}!",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${currentUser?.jabatan ?: "Staff"} • ${currentUser?.departemen ?: "Operations"}",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Estate: ${currentUser?.estate ?: "Pusat"}",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // --- GRID STATS ---
        item {
            Text(
                text = "Metrik Operasional",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CorpTextPrimary
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        title = "Jumlah Rumah",
                        value = totalHouses.toString(),
                        subtitle = "$damagedHouses Bermasalah",
                        icon = Icons.Default.Home,
                        color = CorpBluePrimary,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("perumahan") }
                    )
                    StatCard(
                        title = "Jumlah Aset",
                        value = totalAssets.toString(),
                        subtitle = "$activeTransfers Transfer pending",
                        icon = Icons.Default.Category,
                        color = CorpGreenAccent,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("asset") }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        title = "Guest House",
                        value = "$occupiedGH Terisi",
                        subtitle = "$vacantGH Kamar kosong",
                        icon = Icons.Default.Hotel,
                        color = Color(0xFFD97706),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("guesthouse") }
                    )
                    StatCard(
                        title = "Request Mobil",
                        value = totalCarRequests.toString(),
                        subtitle = "Total pengajuan",
                        icon = Icons.Default.DirectionsCar,
                        color = Color(0xFF7C3AED),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToSection("mobil") }
                    )
                }
            }
        }

        // --- CUSTOM CHARTS SECTION ---
        item {
            Text(
                text = "Grafik & Analisis Real-Time",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CorpTextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Chart 1: Bar Chart per Estate
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Statistik Rumah & Aset per Estate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CorpTextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    BarChartPerEstate(rumahList = rumahList, assetsList = assetsList)
                }
            }
        }

        // Chart 2: Line Chart Monthly Reports
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Grafik Bulanan Laporan Kerusakan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CorpTextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LineChartMonthly(reportsList = reportsList)
                }
            }
        }

        // Chart 3: Pie Chart of Report Category Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Breakdown Jenis Temuan Kerusakan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CorpTextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DonutChartReportType(reportsList = reportsList)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 12.sp, color = CorpTextSecondary, fontWeight = FontWeight.SemiBold)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(16.dp))
                }
            }

            Column {
                Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CorpTextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 10.sp, color = CorpTextSecondary)
            }
        }
    }
}

// =========================================================================
// 3. CUSTOM DRAWN CANVAS CHARTS
// =========================================================================

@Composable
fun BarChartPerEstate(
    rumahList: List<RumahEntity>,
    assetsList: List<AssetEntity>
) {
    val estates = listOf("Pandan", "Tamiang", "Sinar Riau", "Sentosa")
    
    // Group counts
    val housingCounts = estates.map { est ->
        rumahList.count { it.estate.contains(est, ignoreCase = true) }
    }
    val assetCounts = estates.map { est ->
        assetsList.count { it.estate.contains(est, ignoreCase = true) }
    }

    val maxVal = (housingCounts + assetCounts).maxOrNull()?.coerceAtLeast(1) ?: 10

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        val width = size.width
        val height = size.height
        val barGroupWidth = width / estates.size
        val barWidth = 14.dp.toPx()
        val spacing = 4.dp.toPx()

        // Draw background grid lines
        for (i in 0..4) {
            val y = height * (i / 4f)
            drawLine(
                color = Color(0xFFF0F0F0),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        estates.forEachIndexed { index, name ->
            val centerX = (barGroupWidth * index) + (barGroupWidth / 2)

            // 1. Housing Bar (Blue)
            val hValue = housingCounts[index]
            val barHeightH = height * (hValue.toFloat() / maxVal)
            drawRoundRect(
                color = CorpBlueSecondary,
                topLeft = Offset(centerX - barWidth - spacing, height - barHeightH),
                size = Size(barWidth, barHeightH),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // 2. Asset Bar (Green)
            val aValue = assetCounts[index]
            val barHeightA = height * (aValue.toFloat() / maxVal)
            drawRoundRect(
                color = CorpGreenAccent,
                topLeft = Offset(centerX + spacing, height - barHeightA),
                size = Size(barWidth, barHeightA),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )

            // Draw labels
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 10.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(name, centerX, height + 16.dp.toPx(), paint)

                // Value labels on bars
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 9.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
                if (hValue > 0) {
                    drawText(hValue.toString(), centerX - barWidth/2 - spacing, height - barHeightH - 4.dp.toPx(), textPaint)
                }
                if (aValue > 0) {
                    drawText(aValue.toString(), centerX + barWidth/2 + spacing, height - barHeightA - 4.dp.toPx(), textPaint)
                }
            }
        }
    }

    // Legend
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(10.dp).background(CorpBlueSecondary, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Perumahan", fontSize = 11.sp, color = CorpTextSecondary)
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.size(10.dp).background(CorpGreenAccent, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Aset Kantor", fontSize = 11.sp, color = CorpTextSecondary)
    }
}

@Composable
fun LineChartMonthly(reportsList: List<LaporanPerumahanEntity>) {
    // Simulated monthly reporting quantities
    val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul")
    val defaultCounts = listOf(2, 4, 3, 5, 8, reportsList.size.coerceAtLeast(3), reportsList.size + 1)
    val maxVal = defaultCounts.maxOrNull()?.coerceAtLeast(1) ?: 10

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        val width = size.width
        val height = size.height
        val stepX = width / (months.size - 1)

        val path = Path()
        val fillPath = Path()

        val points = defaultCounts.mapIndexed { index, count ->
            val x = stepX * index
            val y = height - (height * (count.toFloat() / maxVal))
            Offset(x, y)
        }

        // Generate line path
        points.forEachIndexed { index, offset ->
            if (index == 0) {
                path.moveTo(offset.x, offset.y)
                fillPath.moveTo(offset.x, height)
                fillPath.lineTo(offset.x, offset.y)
            } else {
                path.lineTo(offset.x, offset.y)
                fillPath.lineTo(offset.x, offset.y)
            }
        }
        fillPath.lineTo(points.last().x, height)
        fillPath.close()

        // Draw gradient area under the line
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(CorpBluePrimary.copy(alpha = 0.3f), Color.Transparent)
            )
        )

        // Draw line
        drawPath(
            path = path,
            color = CorpBluePrimary,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw dots & label texts
        points.forEachIndexed { index, offset ->
            drawCircle(
                color = CorpGreenAccent,
                radius = 4.dp.toPx(),
                center = offset
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = offset
            )

            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 10.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(months[index], offset.x, height + 16.dp.toPx(), paint)

                val valPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 9.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawText(defaultCounts[index].toString(), offset.x, offset.y - 6.dp.toPx(), valPaint)
            }
        }
    }
}

@Composable
fun DonutChartReportType(reportsList: List<LaporanPerumahanEntity>) {
    // Extract breakdown
    val types = listOf("Kebocoran Atap", "Keretakan Dinding", "Instalasi Air", "Kelistrikan", "Lain-lain")
    val mockWeights = listOf(40f, 25f, 15f, 10f, 10f)
    
    val colors = listOf(
        CorpBluePrimary,
        Color(0xFF2ECC71),
        Color(0xFFE74C3C),
        Color(0xFFF1C40F),
        Color(0xFF9B59B6)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Canvas(
            modifier = Modifier
                .size(130.dp)
                .weight(1f)
        ) {
            val strokeWidth = 18.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val rectSize = Size(diameter, diameter)
            val topLeftOffset = Offset(strokeWidth / 2, strokeWidth / 2)

            var currentAngle = 0f
            mockWeights.forEachIndexed { index, weight ->
                val sweepAngle = 360f * (weight / 100f)
                drawArc(
                    color = colors[index],
                    startAngle = currentAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeftOffset,
                    size = rectSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                currentAngle += sweepAngle
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1.2f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            types.forEachIndexed { index, title ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(colors[index], RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$title (${mockWeights[index].toInt()}%)",
                        fontSize = 11.sp,
                        color = CorpTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// =========================================================================
// 4. PERUMAHAN (HOUSING) MODULE
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerumahanScreen(
    viewModel: AppViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedEstate by remember { mutableStateOf("Semua") }
    var selectedStatus by remember { mutableStateOf("Semua") }
    
    val rumahList by viewModel.rumah.collectAsStateWithLifecycle()
    val reportsList by viewModel.laporanPerumahan.collectAsStateWithLifecycle()
    
    var showReportDialog by remember { mutableStateOf(false) }

    // Filtered lists
    val filteredRumah = rumahList.filter {
        (selectedEstate == "Semua" || it.estate == selectedEstate) &&
        (selectedStatus == "Semua" || it.statusRumah == selectedStatus) &&
        (searchQuery.isEmpty() || it.nomorRumah.contains(searchQuery, ignoreCase = true) || it.idRumah.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorpLightBg)
    ) {
        // --- FILTERS & SEARCH HEADER ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari ID Rumah / No. Rumah") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CorpBluePrimary,
                        unfocusedBorderColor = Color(0xFFE4E7EC)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Estate Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val estateOptions = listOf("Semua", "Pandan Estate", "Tamiang Estate", "Sinar Riau Estate", "Bukit Sentosa Estate")
                    var expandedEstate by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expandedEstate,
                        onExpandedChange = { expandedEstate = !expandedEstate },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedEstate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter Estate") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstate) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = TextStyle(fontSize = 12.sp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedEstate,
                            onDismissRequest = { expandedEstate = false }
                        ) {
                            estateOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt, fontSize = 12.sp) },
                                    onClick = {
                                        selectedEstate = opt
                                        expandedEstate = false
                                    }
                                )
                            }
                        }
                    }

                    // Status Filter
                    val statusOptions = listOf("Semua", "Baik", "Rusak Ringan", "Rusak Berat")
                    var expandedStatus by remember { mutableStateOf(false) }
                    
                    ExposedDropdownMenuBox(
                        expanded = expandedStatus,
                        onExpandedChange = { expandedStatus = !expandedStatus },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedStatus,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Filter Kondisi") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                            modifier = Modifier.menuAnchor(),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = TextStyle(fontSize = 12.sp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedStatus,
                            onDismissRequest = { expandedStatus = false }
                        ) {
                            statusOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt, fontSize = 12.sp) },
                                    onClick = {
                                        selectedStatus = opt
                                        expandedStatus = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- HOUSING LISTS ---
        Box(modifier = Modifier.weight(1f)) {
            if (filteredRumah.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.HomeWork, contentDescription = "Empty", modifier = Modifier.size(56.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Rumah tidak ditemukan", color = CorpTextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredRumah) { item ->
                        var showDetailsDialog by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDetailsDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Home Icon with Status Tint
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            when (item.statusRumah) {
                                                "Baik" -> CorpGreenAccent.copy(alpha = 0.15f)
                                                "Rusak Ringan" -> Color(0xFFF1C40F).copy(alpha = 0.15f)
                                                else -> Color(0xFFE74C3C).copy(alpha = 0.15f)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "House",
                                        tint = when (item.statusRumah) {
                                            "Baik" -> CorpGreenAccent
                                            "Rusak Ringan" -> Color(0xFFD97706)
                                            else -> Color(0xFFE74C3C)
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.idRumah} • ${item.nomorRumah}",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CorpTextPrimary
                                    )
                                    Text(
                                        text = "${item.estate} • ${item.jenisRumah}",
                                        fontSize = 12.sp,
                                        color = CorpTextSecondary
                                    )
                                    Text(
                                        text = "Penghuni: ${item.penghuni}",
                                        fontSize = 11.sp,
                                        color = CorpTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                // Status Indicator Pill
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (item.statusRumah) {
                                                "Baik" -> CorpGreenAccent.copy(alpha = 0.1f)
                                                "Rusak Ringan" -> Color(0xFFF1C40F).copy(alpha = 0.1f)
                                                else -> Color(0xFFE74C3C).copy(alpha = 0.1f)
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = item.statusRumah,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (item.statusRumah) {
                                            "Baik" -> CorpGreenAccent
                                            "Rusak Ringan" -> Color(0xFFD97706)
                                            else -> Color(0xFFE74C3C)
                                        }
                                    )
                                }
                            }
                        }

                        // House Detail and Reports History Dialog
                        if (showDetailsDialog) {
                            Dialog(onDismissRequest = { showDetailsDialog = false }) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Detail Perumahan",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = CorpBluePrimary
                                            )
                                            IconButton(onClick = { showDetailsDialog = false }) {
                                                Icon(Icons.Default.Close, contentDescription = "Close")
                                            }
                                        }

                                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                                        DetailRow("ID Rumah", item.idRumah)
                                        DetailRow("Estate", item.estate)
                                        DetailRow("Nomor Rumah", item.nomorRumah)
                                        DetailRow("Jenis/Tipe", item.jenisRumah)
                                        DetailRow("Nama Mess", item.namaMess)
                                        DetailRow("Penghuni", item.penghuni)
                                        DetailRow("Status Rumah", item.statusRumah)

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            text = "Riwayat Laporan Kerusakan",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = CorpTextPrimary
                                        )

                                        val houseReports = reportsList.filter { it.nomorRumah == item.nomorRumah && it.estate == item.estate }
                                        if (houseReports.isEmpty()) {
                                            Text(
                                                "Tidak ada laporan kerusakan untuk rumah ini.",
                                                fontSize = 11.sp,
                                                color = CorpTextSecondary,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            houseReports.forEach { rep ->
                                                Card(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 4.dp),
                                                    colors = CardDefaults.cardColors(containerColor = CorpLightBg),
                                                    shape = RoundedCornerShape(10.dp)
                                                ) {
                                                    Column(modifier = Modifier.padding(10.dp)) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Text(
                                                                rep.jenisTemuan,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 12.sp,
                                                                color = CorpTextPrimary
                                                            )
                                                            Text(
                                                                rep.status,
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = if (rep.status == "Open") Color.Red else CorpGreenAccent
                                                            )
                                                        }
                                                        Text(rep.deskripsi, fontSize = 11.sp, color = CorpTextSecondary)
                                                        Text(
                                                            "Pelapor: ${rep.pelapor} • Tanggal: ${rep.tanggal}",
                                                            fontSize = 9.sp,
                                                            color = CorpTextSecondary
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = {
                                                showDetailsDialog = false
                                                showReportDialog = true
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary)
                                        ) {
                                            Icon(Icons.Default.AddComment, contentDescription = "Lapor")
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Buat Laporan Kerusakan", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Floating Button to Report Damage
            FloatingActionButton(
                onClick = { showReportDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .navigationBarsPadding(),
                containerColor = CorpGreenAccent,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.AddComment, contentDescription = "Lapor Kerusakan")
            }
        }
    }

    // CREATE REPORT DIALOG
    if (showReportDialog) {
        Dialog(onDismissRequest = { showReportDialog = false }) {
            var inputEstate by remember { mutableStateOf("") }
            var inputNoRumah by remember { mutableStateOf("") }
            var inputPenghuni by remember { mutableStateOf("") }
            var inputJenis by remember { mutableStateOf("Kebocoran Atap") }
            var inputDeskripsi by remember { mutableStateOf("") }
            var isSubmitting by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Form Laporan Kerusakan Perumahan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CorpBluePrimary
                    )

                    Divider()

                    // Estate input
                    OutlinedTextField(
                        value = inputEstate,
                        onValueChange = { inputEstate = it },
                        label = { Text("Nama Estate") },
                        placeholder = { Text("contoh: Pandan Estate") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // House Number
                    OutlinedTextField(
                        value = inputNoRumah,
                        onValueChange = { inputNoRumah = it },
                        label = { Text("Nomor Rumah") },
                        placeholder = { Text("contoh: No. 12") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Occupant
                    OutlinedTextField(
                        value = inputPenghuni,
                        onValueChange = { inputPenghuni = it },
                        label = { Text("Nama Penghuni") },
                        placeholder = { Text("contoh: Sutrisno") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Issue dropdown (simplified)
                    val issueOptions = listOf("Kebocoran Atap", "Keretakan Dinding", "Instalasi Air", "Kelistrikan", "Lain-lain")
                    var expandedIssue by remember { mutableStateOf(false) }
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = inputJenis,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Jenis Kerusakan") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "dropdown") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedIssue = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = CorpTextPrimary,
                                disabledBorderColor = Color.Gray,
                                disabledLabelColor = CorpBluePrimary
                            )
                        )
                        DropdownMenu(
                            expanded = expandedIssue,
                            onDismissRequest = { expandedIssue = false }
                        ) {
                            issueOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt) },
                                    onClick = {
                                        inputJenis = opt
                                        expandedIssue = false
                                    }
                                )
                            }
                        }
                    }

                    // Description
                    OutlinedTextField(
                        value = inputDeskripsi,
                        onValueChange = { inputDeskripsi = it },
                        label = { Text("Deskripsi Kerusakan") },
                        placeholder = { Text("Ceritakan kondisi kerusakan...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    // Simulated Geo-Location Display
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CorpLightBg)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "GPS", tint = CorpGreenAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("GPS Terbaca Otomatis", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CorpTextPrimary)
                                Text("Lat: 0.4907 • Lng: 101.4478 (Riau Ops Office)", fontSize = 10.sp, color = CorpTextSecondary)
                            }
                        }
                    }

                    // Simulated Camera Capture Option
                    var simulatedPhotoBeforePath by remember { mutableStateOf("") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bukti Foto Kerusakan:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = {
                                // Simulate picture path creation
                                simulatedPhotoBeforePath = "simulated_photo_${Random().nextInt(10000)}.jpg"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CorpBlueSecondary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ambil Foto", fontSize = 11.sp)
                        }
                    }

                    if (simulatedPhotoBeforePath.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE8F8F5), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, "success", tint = CorpGreenAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Foto terlampir: $simulatedPhotoBeforePath", fontSize = 10.sp, color = CorpGreenAccent)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showReportDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                if (inputEstate.isEmpty() || inputNoRumah.isEmpty()) {
                                    return@Button
                                }
                                isSubmitting = true
                                viewModel.submitHousingReport(
                                    estate = inputEstate,
                                    nomorRumah = inputNoRumah,
                                    penghuni = inputPenghuni,
                                    jenisTemuan = inputJenis,
                                    deskripsi = inputDeskripsi,
                                    status = "Open",
                                    pic = "Staff Maintenance",
                                    fotoBefore = if (simulatedPhotoBeforePath.isEmpty()) "simulated_default_before.jpg" else simulatedPhotoBeforePath,
                                    fotoAfter = "",
                                    latitude = 0.4907,
                                    longitude = 101.4478
                                ) {
                                    isSubmitting = false
                                    showReportDialog = false
                                }
                            },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary)
                        ) {
                            Text("Kirim Laporan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = CorpTextSecondary, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 13.sp, color = CorpTextPrimary, fontWeight = FontWeight.Bold)
    }
}

// =========================================================================
// 5. GUEST HOUSE MODULE
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestHouseScreen(
    viewModel: AppViewModel
) {
    var selectedEstate by remember { mutableStateOf("Semua") }
    var searchQuery by remember { mutableStateOf("") }
    
    val guesthousesList by viewModel.guestHouses.collectAsStateWithLifecycle()
    val bookingsList by viewModel.bookings.collectAsStateWithLifecycle()
    
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedGHForBooking by remember { mutableStateOf<GuestHouseEntity?>(null) }

    val filteredGH = guesthousesList.filter {
        (selectedEstate == "Semua" || it.estate == selectedEstate) &&
        (searchQuery.isEmpty() || it.namaGH.contains(searchQuery, ignoreCase = true) || it.nomorKamar.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorpLightBg)
    ) {
        // --- SEARCH AND FILTER ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari Guest House / Nomor Kamar") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CorpBluePrimary,
                        unfocusedBorderColor = Color(0xFFE4E7EC)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                val estateOptions = listOf("Semua", "Pandan Estate", "Tamiang Estate", "Sinar Riau Estate", "Bukit Sentosa Estate")
                var expandedEstate by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expandedEstate,
                    onExpandedChange = { expandedEstate = !expandedEstate },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedEstate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filter Lokasi Estate") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstate) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEstate,
                        onDismissRequest = { expandedEstate = false }
                    ) {
                        estateOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    selectedEstate = opt
                                    expandedEstate = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- GUEST HOUSE ROOMS LIST ---
        Box(modifier = Modifier.weight(1f)) {
            if (filteredGH.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Guest House tidak ditemukan", color = CorpTextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredGH) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.namaGH, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpTextPrimary)
                                        Text("${item.estate} • ${item.nomorKamar}", fontSize = 12.sp, color = CorpTextSecondary)
                                    }

                                    // Room Status Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (item.status) {
                                                    "Tersedia" -> CorpGreenAccent.copy(alpha = 0.1f)
                                                    "Terisi" -> Color.Red.copy(alpha = 0.1f)
                                                    else -> Orange.copy(alpha = 0.1f)
                                                }
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = item.status,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = when (item.status) {
                                                "Tersedia" -> CorpGreenAccent
                                                "Terisi" -> Color.Red
                                                else -> Orange
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Operations Button (Book / Check-In / Check-Out)
                                if (item.status == "Tersedia") {
                                    Button(
                                        onClick = {
                                            selectedGHForBooking = item
                                            showBookingDialog = true
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.BookmarkAdd, contentDescription = "Book")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Booking Kamar ini", fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    // Check if there is an active booking for this room
                                    val activeBooking = bookingsList.find { 
                                        it.guestHouse == item.namaGH && it.nomorKamar == item.nomorKamar && it.status != "Checked Out" 
                                    }
                                    
                                    if (activeBooking != null) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            if (activeBooking.status == "Booked") {
                                                Button(
                                                    onClick = {
                                                        viewModel.checkInBooking(activeBooking, "simulated_checkin_photo.jpg")
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = CorpGreenAccent)
                                                ) {
                                                    Icon(Icons.Default.Login, contentDescription = "Check-In")
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Check-In (Wajib Foto)", fontSize = 11.sp)
                                                }
                                            } else if (activeBooking.status == "Checked In") {
                                                Button(
                                                    onClick = {
                                                        viewModel.checkOutBooking(activeBooking, "simulated_checkout_photo.jpg")
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                                ) {
                                                    Icon(Icons.Default.Logout, contentDescription = "Check-Out")
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Check-Out (Wajib Foto)", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Booking atas nama: ${activeBooking.nama} • ${activeBooking.lamaMenginap} malam",
                                            fontSize = 11.sp,
                                            color = CorpTextSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // GH BOOKING FORM DIALOG
    if (showBookingDialog && selectedGHForBooking != null) {
        val room = selectedGHForBooking!!
        Dialog(onDismissRequest = { showBookingDialog = false }) {
            var inputNama by remember { mutableStateOf("") }
            var inputNIK by remember { mutableStateOf("") }
            var inputAsal by remember { mutableStateOf("") }
            var inputLama by remember { mutableStateOf("3") }
            var isSubmitting by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Booking Guest House",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CorpBluePrimary
                    )
                    Text(
                        "${room.namaGH} • Room ${room.nomorKamar}",
                        fontSize = 12.sp,
                        color = CorpTextSecondary
                    )

                    Divider()

                    OutlinedTextField(
                        value = inputNama,
                        onValueChange = { inputNama = it },
                        label = { Text("Nama Pengunjung") },
                        placeholder = { Text("contoh: Budi Setiawan") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputNIK,
                        onValueChange = { inputNIK = it },
                        label = { Text("NIK Pengunjung") },
                        placeholder = { Text("contoh: 32012...") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = inputAsal,
                        onValueChange = { inputAsal = it },
                        label = { Text("Estate Asal") },
                        placeholder = { Text("contoh: Pandan Estate") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputLama,
                        onValueChange = { inputLama = it },
                        label = { Text("Lama Menginap (Malam)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    // Automated Expiry Notice H-7, H-3, H-1 Simulation Info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = "Reminder", tint = Color(0xFFD97706))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Pengingat booking dikirim otomatis H-7, H-3, dan H-1 ke Telegram/Notifikasi.",
                                fontSize = 10.sp,
                                color = Color(0xFF92400E),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showBookingDialog = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                if (inputNama.isEmpty() || inputNIK.isEmpty()) {
                                    return@Button
                                }
                                isSubmitting = true
                                val calendar = Calendar.getInstance()
                                val checkInStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                                calendar.add(Calendar.DAY_OF_YEAR, inputLama.toIntOrNull() ?: 3)
                                val checkOutStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

                                viewModel.bookGuestHouseRoom(
                                    guestHouse = room,
                                    nama = inputNama,
                                    nik = inputNIK,
                                    estateAsal = inputAsal,
                                    estateTujuan = room.estate,
                                    checkIn = checkInStr,
                                    checkOut = checkOutStr,
                                    lamaMenginap = inputLama.toIntOrNull() ?: 3,
                                    fotoCheckIn = "simulated_booking_photo.jpg"
                                ) {
                                    isSubmitting = false
                                    showBookingDialog = false
                                }
                            },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary)
                        ) {
                            Text("Pesan Sekarang")
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 6. ASSETS & TRANSFERS MODULE
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetScreen(
    viewModel: AppViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    val assetsList by viewModel.assets.collectAsStateWithLifecycle()
    val transfersList by viewModel.transfers.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = CorpBluePrimary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Daftar Aset") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Transfer Aset") }
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (selectedTab == 0) {
                AssetListTab(viewModel = viewModel, assetsList = assetsList)
            } else {
                TransferAssetTab(viewModel = viewModel, transfersList = transfersList, assetsList = assetsList)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetListTab(viewModel: AppViewModel, assetsList: List<AssetEntity>) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedEstate by remember { mutableStateOf("Semua") }
    var showAddAssetDialog by remember { mutableStateOf(false) }

    val filteredAssets = assetsList.filter {
        (selectedEstate == "Semua" || it.estate == selectedEstate) &&
        (searchQuery.isEmpty() || it.namaAsset.contains(searchQuery, ignoreCase = true) || it.nomorAsset.contains(searchQuery, ignoreCase = true))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search and Filters
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari Nama Aset / No. Seri Aset") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CorpBluePrimary,
                        unfocusedBorderColor = Color(0xFFE4E7EC)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                val estateOptions = listOf("Semua", "Pandan Estate", "Tamiang Estate", "Sinar Riau Estate", "Bukit Sentosa Estate")
                var expandedEstate by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expandedEstate,
                    onExpandedChange = { expandedEstate = !expandedEstate },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedEstate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Filter Lokasi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstate) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEstate,
                        onDismissRequest = { expandedEstate = false }
                    ) {
                        estateOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    selectedEstate = opt
                                    expandedEstate = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            if (filteredAssets.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aset tidak ditemukan", color = CorpTextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAssets) { item ->
                        var showDetailsDialog by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDetailsDialog = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mini QR Mockup on list
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CustomQRGridDrawing(text = item.nomorAsset, modifier = Modifier.fillMaxSize())
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.namaAsset, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CorpTextPrimary)
                                    Text("No. Aset: ${item.nomorAsset} • S/N: ${item.serialNumber}", fontSize = 11.sp, color = CorpTextSecondary)
                                    Text("Lokasi: ${item.estate} (${item.lokasi})", fontSize = 11.sp, color = CorpTextSecondary)
                                }

                                // Condition Indicator Light
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(if (item.kondisi == "Baik") CorpGreenAccent.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = item.kondisi,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.kondisi == "Baik") CorpGreenAccent else Color.Red
                                    )
                                }
                            }
                        }

                        // Asset Details with Large QR Code Display Dialog
                        if (showDetailsDialog) {
                            Dialog(onDismissRequest = { showDetailsDialog = false }) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(20.dp)
                                            .verticalScroll(rememberScrollState()),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Detail Aset Kantor", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpBluePrimary)
                                            IconButton(onClick = { showDetailsDialog = false }) {
                                                Icon(Icons.Default.Close, contentDescription = "Close")
                                            }
                                        }

                                        Divider()

                                        // Large Vector QR Code Representation
                                        Box(
                                            modifier = Modifier
                                                .size(160.dp)
                                                .border(2.dp, CorpBluePrimary, RoundedCornerShape(12.dp))
                                                .background(Color.White)
                                                .padding(16.dp)
                                        ) {
                                            CustomQRGridDrawing(text = item.nomorAsset, modifier = Modifier.fillMaxSize())
                                        }

                                        Text(
                                            text = "SMARTHR-AST-${item.nomorAsset}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = CorpTextSecondary
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        DetailRow("Nomor Asset", item.nomorAsset)
                                        DetailRow("Nama Asset", item.namaAsset)
                                        DetailRow("Kategori", item.kategori)
                                        DetailRow("Merk", item.merk)
                                        DetailRow("Serial Number", item.serialNumber)
                                        DetailRow("Lokasi Estate", item.estate)
                                        DetailRow("Penempatan", item.lokasi)
                                        DetailRow("Kondisi", item.kondisi)

                                        Spacer(modifier = Modifier.height(16.dp))

                                        OutlinedButton(
                                            onClick = { showDetailsDialog = false },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Tutup")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showAddAssetDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .navigationBarsPadding(),
                containerColor = CorpGreenAccent,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Asset")
            }
        }
    }

    // ADD ASSET DIALOG
    if (showAddAssetDialog) {
        Dialog(onDismissRequest = { showAddAssetDialog = false }) {
            var inputNo by remember { mutableStateOf("") }
            var inputNama by remember { mutableStateOf("") }
            var inputKategori by remember { mutableStateOf("IT Equipment") }
            var inputMerk by remember { mutableStateOf("") }
            var inputSN by remember { mutableStateOf("") }
            var inputEstate by remember { mutableStateOf("Pandan Estate") }
            var inputLokasi by remember { mutableStateOf("") }
            var inputKondisi by remember { mutableStateOf("Baik") }
            var isSubmitting by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Tambah Asset Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpBluePrimary)
                    Divider()

                    OutlinedTextField(value = inputNo, onValueChange = { inputNo = it }, label = { Text("Nomor Asset") }, placeholder = { Text("contoh: AST-005") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = inputNama, onValueChange = { inputNama = it }, label = { Text("Nama Asset") }, placeholder = { Text("contoh: Laptop Core i5") }, modifier = Modifier.fillMaxWidth())
                    
                    OutlinedTextField(value = inputMerk, onValueChange = { inputMerk = it }, label = { Text("Merk") }, placeholder = { Text("contoh: Lenovo") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = inputSN, onValueChange = { inputSN = it }, label = { Text("Serial Number") }, placeholder = { Text("contoh: SN-881A2") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = inputLokasi, onValueChange = { inputLokasi = it }, label = { Text("Detail Lokasi Penempatan") }, placeholder = { Text("contoh: Ruang Rapat Utama") }, modifier = Modifier.fillMaxWidth())

                    // Options for Estate
                    val estateOptions = listOf("Pandan Estate", "Tamiang Estate", "Sinar Riau Estate", "Bukit Sentosa Estate")
                    var expandedEstate by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = inputEstate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Estate") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "expand") },
                            modifier = Modifier.fillMaxWidth().clickable { expandedEstate = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(disabledTextColor = CorpTextPrimary, disabledBorderColor = Color.Gray, disabledLabelColor = CorpBluePrimary)
                        )
                        DropdownMenu(expanded = expandedEstate, onDismissRequest = { expandedEstate = false }) {
                            estateOptions.forEach { opt ->
                                DropdownMenuItem(text = { Text(opt) }, onClick = { inputEstate = opt; expandedEstate = false })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(onClick = { showAddAssetDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                if (inputNo.isEmpty() || inputNama.isEmpty()) return@Button
                                isSubmitting = true
                                viewModel.addAsset(
                                    nomorAsset = inputNo,
                                    namaAsset = inputNama,
                                    kategori = inputKategori,
                                    merk = inputMerk,
                                    serialNumber = inputSN,
                                    estate = inputEstate,
                                    lokasi = inputLokasi,
                                    kondisi = inputKondisi,
                                    fotoAsset = "simulated_asset_image.jpg"
                                ) { success, msg ->
                                    isSubmitting = false
                                    if (success) showAddAssetDialog = false
                                }
                            },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary)
                        ) {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferAssetTab(viewModel: AppViewModel, transfersList: List<TransferAssetEntity>, assetsList: List<AssetEntity>) {
    var showTransferFormDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            if (transfersList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada riwayat transfer aset", color = CorpTextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(transfersList) { item ->
                        var showDetails by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDetails = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.namaAsset, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CorpTextPrimary)
                                        Text("No. Aset: ${item.nomorAsset}", fontSize = 11.sp, color = CorpTextSecondary)
                                        Text("Dari: ${item.estateAsal} ➔ Ke: ${item.estateTujuan}", fontSize = 11.sp, color = CorpTextSecondary)
                                    }

                                    // Transfer Status Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (item.status == "Completed") CorpGreenAccent.copy(alpha = 0.1f) else Orange.copy(alpha = 0.1f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = item.status,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.status == "Completed") CorpGreenAccent else Orange
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Penyerah: ${item.penyerah} • Penerima: ${item.penerima}", fontSize = 11.sp, color = CorpTextSecondary)
                            }
                        }

                        // Transfer Details Dialog
                        if (showDetails) {
                            Dialog(onDismissRequest = { showDetails = false }) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(20.dp)
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Detail Transfer Asset", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpBluePrimary)
                                            IconButton(onClick = { showDetails = false }) {
                                                Icon(Icons.Default.Close, contentDescription = "Close")
                                            }
                                        }

                                        Divider()

                                        DetailRow("ID Transfer", item.idTransfer)
                                        DetailRow("Tanggal/Jam", "${item.tanggal} • ${item.jam}")
                                        DetailRow("Nomor Asset", item.nomorAsset)
                                        DetailRow("Nama Asset", item.namaAsset)
                                        DetailRow("Estate Asal", item.estateAsal)
                                        DetailRow("Estate Tujuan", item.estateTujuan)
                                        DetailRow("Nama Penyerah", item.penyerah)
                                        DetailRow("Nama Penerima", item.penerima)
                                        DetailRow("Status Transfer", item.status)

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Display signatures mock / pad
                                        Text("Tanda Tangan Digital:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Penyerah", fontSize = 10.sp, color = CorpTextSecondary)
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(60.dp)
                                                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                                        .background(Color(0xFFF9FAFB)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (item.ttdPenyerah.isNotEmpty()) {
                                                        Text("✓ Ter-ttd", color = CorpGreenAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    } else {
                                                        Text("Kosong", color = Color.Red, fontSize = 11.sp)
                                                    }
                                                }
                                            }
                                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Penerima", fontSize = 10.sp, color = CorpTextSecondary)
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(60.dp)
                                                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                                        .background(Color(0xFFF9FAFB)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    if (item.ttdPenerima.isNotEmpty()) {
                                                        Text("✓ Ter-ttd", color = CorpGreenAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                    } else {
                                                        Text("Kosong", color = Color.Red, fontSize = 11.sp)
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        OutlinedButton(onClick = { showDetails = false }, modifier = Modifier.fillMaxWidth()) {
                                            Text("Tutup")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showTransferFormDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .navigationBarsPadding(),
                containerColor = CorpBluePrimary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Transfer")
            }
        }
    }

    // FORM INITIATE TRANSFER DIALOG
    if (showTransferFormDialog) {
        Dialog(onDismissRequest = { showTransferFormDialog = false }) {
            var selectedAsset by remember { mutableStateOf<AssetEntity?>(null) }
            var destinationEstate by remember { mutableStateOf("Tamiang Estate") }
            var inputPenyerah by remember { mutableStateOf("") }
            var inputPenerima by remember { mutableStateOf("") }
            
            // Signature Sketchpad states
            var showPenyerahSignPad by remember { mutableStateOf(false) }
            var showPenerimaSignPad by remember { mutableStateOf(false) }
            var penyerahSignature by remember { mutableStateOf("") }
            var penerimaSignature by remember { mutableStateOf("") }

            val estateOptions = listOf("Pandan Estate", "Tamiang Estate", "Sinar Riau Estate", "Bukit Sentosa Estate")

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Form Transfer Aset Operasional", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpBluePrimary)
                    Divider()

                    // Select Asset Dropdown
                    var expandedAssetSelect by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedAsset?.namaAsset ?: "Pilih Aset untuk Ditransfer",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Aset") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "dropdown") },
                            modifier = Modifier.fillMaxWidth().clickable { expandedAssetSelect = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(disabledTextColor = CorpTextPrimary, disabledBorderColor = Color.Gray, disabledLabelColor = CorpBluePrimary)
                        )
                        DropdownMenu(expanded = expandedAssetSelect, onDismissRequest = { expandedAssetSelect = false }) {
                            assetsList.forEach { ast ->
                                DropdownMenuItem(
                                    text = { Text("${ast.namaAsset} (${ast.estate})") },
                                    onClick = {
                                        selectedAsset = ast
                                        expandedAssetSelect = false
                                    }
                                )
                            }
                        }
                    }

                    // Destination Estate
                    var expandedDestSelect by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = destinationEstate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Estate Tujuan") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "dropdown") },
                            modifier = Modifier.fillMaxWidth().clickable { expandedDestSelect = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(disabledTextColor = CorpTextPrimary, disabledBorderColor = Color.Gray, disabledLabelColor = CorpBluePrimary)
                        )
                        DropdownMenu(expanded = expandedDestSelect, onDismissRequest = { expandedDestSelect = false }) {
                            estateOptions.forEach { opt ->
                                DropdownMenuItem(text = { Text(opt) }, onClick = { destinationEstate = opt; expandedDestSelect = false })
                            }
                        }
                    }

                    OutlinedTextField(value = inputPenyerah, onValueChange = { inputPenyerah = it }, label = { Text("Nama Penyerah (PIC Asal)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = inputPenerima, onValueChange = { inputPenerima = it }, label = { Text("Nama Penerima (PIC Tujuan)") }, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(10.dp))

                    // Digital Signatures triggering buttons
                    Text("Tanda Tangan Digital (Wajib)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { showPenyerahSignPad = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (penyerahSignature.isNotEmpty()) CorpGreenAccent else CorpBlueSecondary)
                        ) {
                            Text(if (penyerahSignature.isNotEmpty()) "✓ Penyerah" else "TTD Penyerah", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { showPenerimaSignPad = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (penerimaSignature.isNotEmpty()) CorpGreenAccent else CorpBlueSecondary)
                        ) {
                            Text(if (penerimaSignature.isNotEmpty()) "✓ Penerima" else "TTD Penerima", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(onClick = { showTransferFormDialog = false }, modifier = Modifier.weight(1f)) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                if (selectedAsset == null || inputPenyerah.isEmpty() || inputPenerima.isEmpty()) return@Button
                                viewModel.initiateTransferAsset(
                                    asset = selectedAsset!!,
                                    estateTujuan = destinationEstate,
                                    penyerah = inputPenyerah,
                                    penerima = inputPenerima,
                                    ttdPenyerah = penyerahSignature,
                                    ttdPenerima = penerimaSignature,
                                    fotoAsset = "simulated_transfer_asset.jpg",
                                    fotoSerahTerima = "simulated_handover_ceremony.jpg",
									onComplete = {},
                                    latitude = 0.4907,
                                    longitude = 101.4478
                                )
                                showTransferFormDialog = false
                            },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary)
                        ) {
                            Text("Kirim Transfer")
                        }
                    }
                }
            }

            // Real drawing canvas popup for Penyerah Signature
            if (showPenyerahSignPad) {
                DigitalSignatureDialog(
                    title = "Tanda Tangan Penyerah",
                    onSignatureCaptured = {
                        penyerahSignature = it
                        showPenyerahSignPad = false
                    },
                    onDismiss = { showPenyerahSignPad = false }
                )
            }

            // Real drawing canvas popup for Penerima Signature
            if (showPenerimaSignPad) {
                DigitalSignatureDialog(
                    title = "Tanda Tangan Penerima",
                    onSignatureCaptured = {
                        penerimaSignature = it
                        showPenerimaSignPad = false
                    },
                    onDismiss = { showPenerimaSignPad = false }
                )
            }
        }
    }
}

// =========================================================================
// 7. DIGITAL SIGNATURE SKETCHPAD CANVAS DIALOG
// =========================================================================
@Composable
fun DigitalSignatureDialog(
    title: String,
    onSignatureCaptured: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CorpBluePrimary)
                Text("Goreskan tanda tangan Anda pada area putih di bawah.", fontSize = 11.sp, color = CorpTextSecondary)

                // Interactive Canvas Sketch Area
                var points = remember { mutableStateListOf<Offset>() }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset -> points.add(offset) },
                                onDrag = { change, _ -> points.add(change.position) }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        for (i in 0 until points.size - 1) {
                            // Ensure we don't draw lines between disconnected drags (simplified for mockup)
                            drawLine(
                                color = Color.Black,
                                start = points[i],
                                end = points[i + 1],
                                strokeWidth = 6f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                    
                    if (points.isEmpty()) {
                        Text(
                            "Area Tanda Tangan",
                            color = Color.LightGray,
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 12.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { points.clear() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bersihkan")
                    }
                    Button(
                        onClick = {
                            if (points.isNotEmpty()) {
                                onSignatureCaptured("captured-sig-points-${points.size}")
                            } else {
                                onSignatureCaptured("simulated-fallback-signature")
                            }
                        },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary)
                    ) {
                        Text("Simpan TTD")
                    }
                }
            }
        }
    }
}

// =========================================================================
// 8. CUSTOM QR CODE GRID DRAWING COMPONENT
// =========================================================================
@Composable
fun CustomQRGridDrawing(text: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val sizePx = size.minDimension
        val cells = 11
        val cellSize = sizePx / cells

        // Draw outer big squares (QR Finder patterns)
        // Top Left
        drawFinderPattern(0f, 0f, cellSize * 3)
        // Top Right
        drawFinderPattern(sizePx - cellSize * 3, 0f, cellSize * 3)
        // Bottom Left
        drawFinderPattern(0f, sizePx - cellSize * 3, cellSize * 3)

        // Draw randomized mockup binary QR pixels
        val pseudoRandom = Random(text.hashCode().toLong())
        for (row in 0 until cells) {
            for (col in 0 until cells) {
                // Skip finder patterns areas
                if ((row < 3 && col < 3) || (row < 3 && col >= cells - 3) || (row >= cells - 3 && col < 3)) {
                    continue
                }
                if (pseudoRandom.nextBoolean()) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(col * cellSize, row * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFinderPattern(x: Float, y: Float, size: Float) {
    // Outer black box
    drawRect(color = Color.Black, topLeft = Offset(x, y), size = Size(size, size))
    // Middle white box
    val inset = size / 5
    drawRect(color = Color.White, topLeft = Offset(x + inset, y + inset), size = Size(size - inset * 2, size - inset * 2))
    // Inner black box
    val innerInset = size * 2 / 5
    drawRect(color = Color.Black, topLeft = Offset(x + innerInset, y + innerInset), size = Size(size - innerInset * 2, size - innerInset * 2))
}

// =========================================================================
// 9. VEHICLE REQUEST (MOBIL) MODULE
// =========================================================================
@Composable
fun RequestMobilScreen(
    viewModel: AppViewModel
) {
    val requestsList by viewModel.carRequests.collectAsStateWithLifecycle()
    var showRequestForm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CorpLightBg)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            if (requestsList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada request kendaraan", color = CorpTextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requestsList) { item ->
                        var showDetails by remember { mutableStateOf(false) }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDetails = true },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.tujuan, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpTextPrimary)
                                        Text("Driver: ${item.driver} • ${item.kendaraan}", fontSize = 12.sp, color = CorpTextSecondary)
                                        Text("Tanggal berangkat: ${item.tanggal}", fontSize = 11.sp, color = CorpTextSecondary)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (item.status) {
                                                    "Completed" -> CorpGreenAccent.copy(alpha = 0.1f)
                                                    "Approved" -> CorpBlueSecondary.copy(alpha = 0.1f)
                                                    else -> Orange.copy(alpha = 0.1f)
                                                }
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = item.status,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = when (item.status) {
                                                "Completed" -> CorpGreenAccent
                                                "Approved" -> CorpBlueSecondary
                                                else -> Orange
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Car Request Details Dialog
                        if (showDetails) {
                            Dialog(onDismissRequest = { showDetails = false }) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(20.dp)
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Detail Request Mobil", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpBluePrimary)
                                            IconButton(onClick = { showDetails = false }) {
                                                Icon(Icons.Default.Close, contentDescription = "Close")
                                            }
                                        }

                                        Divider()

                                        DetailRow("ID Request", item.id)
                                        DetailRow("Nama Pemohon", item.nama)
                                        DetailRow("Asal Estate", item.estateAsal)
                                        DetailRow("Tujuan Perjalanan", item.tujuan)
                                        DetailRow("Keperluan", item.keperluan)
                                        DetailRow("Tanggal Perjalanan", item.tanggal)
                                        DetailRow("Jam Berangkat/Kembali", "${item.jamBerangkat} - ${item.jamKembali}")
                                        DetailRow("Driver", item.driver)
                                        DetailRow("Kendaraan / Plat", item.kendaraan)
                                        DetailRow("Status", item.status)

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Kilometer captures
                                        Text("Odometer Kilometer (Wajib):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(CorpLightBg, RoundedCornerShape(8.dp))
                                                .padding(12.dp)
                                        ) {
                                            Text("✓ Kilometer Awal terlampir", color = CorpGreenAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            if (item.status == "Completed") {
                                                Text("✓ Kilometer Akhir terlampir", color = CorpGreenAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            } else {
                                                Text("• Kilometer Akhir: Menunggu penyelesaian perjalanan", color = Color.Gray, fontSize = 11.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        if (item.status == "Pending" || item.status == "Approved") {
                                            Button(
                                                onClick = {
                                                    viewModel.completeCarRequest(item, "simulated_odometer_end.jpg") {
                                                        showDetails = false
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = ButtonDefaults.buttonColors(containerColor = CorpGreenAccent)
                                            ) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = "Complete")
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Selesaikan Perjalanan (Foto KM Akhir)", fontSize = 12.sp)
                                            }
                                        }

                                        OutlinedButton(onClick = { showDetails = false }, modifier = Modifier.fillMaxWidth()) {
                                            Text("Tutup")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showRequestForm = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .navigationBarsPadding(),
                containerColor = CorpGreenAccent,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Request Kendaraan")
            }
        }
    }

    // FORM REQUEST MOBIL DIALOG
    if (showRequestForm) {
        Dialog(onDismissRequest = { showRequestForm = false }) {
            var asal by remember { mutableStateOf("Pandan Estate") }
            var tujuan by remember { mutableStateOf("") }
            var keperluan by remember { mutableStateOf("") }
            var driver by remember { mutableStateOf("") }
            var plat by remember { mutableStateOf("") }
            var photoKM by remember { mutableStateOf("") }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Request Kendaraan Operasional", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpBluePrimary)
                    Divider()

                    OutlinedTextField(value = asal, onValueChange = { asal = it }, label = { Text("Asal Estate") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tujuan, onValueChange = { tujuan = it }, label = { Text("Tujuan") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = keperluan, onValueChange = { keperluan = it }, label = { Text("Keperluan") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = driver, onValueChange = { driver = it }, label = { Text("Nama Driver") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = plat, onValueChange = { plat = it }, label = { Text("Kendaraan & Plat Nomor") }, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Wajib Unggah Foto Kilometer Awal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { photoKM = "odometer_start_captured.jpg" },
                        colors = ButtonDefaults.buttonColors(containerColor = CorpBlueSecondary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (photoKM.isNotEmpty()) "✓ Foto KM Terlampir" else "Ambil Foto Odometer Awal", fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(onClick = { showRequestForm = false }, modifier = Modifier.weight(1f)) {
                            Text("Batal")
                        }
                        Button(
                            onClick = {
                                if (tujuan.isEmpty() || photoKM.isEmpty()) return@Button
                                viewModel.submitCarRequest(
                                    estateAsal = asal,
                                    tujuan = tujuan,
                                    keperluan = keperluan,
                                    tanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                                    jamBerangkat = "08:00",
                                    jamKembali = "17:00",
                                    driver = driver,
                                    kendaraan = plat,
                                    fotoKilometerAwal = photoKM,
									onComplete = {},
                                )
                                showRequestForm = false
                            },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary)
                        ) {
                            Text("Kirim Request")
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 10. CLOUD SPREADSHEET SYNC / SETTINGS SCREEN
// =========================================================================
@Composable
fun SyncSettingsScreen(
    viewModel: AppViewModel
) {
    val webAppUrl by viewModel.webAppUrl.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    
    var inputUrl by remember { mutableStateOf(webAppUrl) }
    var showGASCodeDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(CorpLightBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Sinkronisasi Google Spreadsheet", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpBluePrimary)
                    Text(
                        "Hubungkan aplikasi ADINDO HR langsung dengan Google Spreadsheet utama perusahaan melalui Google Apps Script API.",
                        fontSize = 12.sp,
                        color = CorpTextSecondary
                    )

                    Divider()

                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        label = { Text("URL Google Apps Script Web App") },
                        placeholder = { Text("https://script.google.com/macros/s/...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = { viewModel.saveWebAppUrl(inputUrl) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary)
                    ) {
                        Text("Simpan URL API", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- OPERATIONS CARDS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Aksi Sinkronisasi Data", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.testCloudConnection() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CorpBlueSecondary),
                            enabled = !isSyncing
                        ) {
                            Text("Tes Koneksi", fontSize = 11.sp)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { viewModel.exportToSpreadsheet() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CorpGreenAccent),
                            enabled = !isSyncing
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "upload")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export Data", fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.importFromSpreadsheet() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary),
                            enabled = !isSyncing
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = "download")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Import Data", fontSize = 12.sp)
                        }
                    }

                    if (isSyncing) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = CorpBluePrimary)
                    }

                    syncStatus?.let { status ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CorpLightBg)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = "info", tint = CorpBluePrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(status, fontSize = 11.sp, color = CorpTextPrimary, modifier = Modifier.weight(1f))
                                IconButton(onClick = { viewModel.clearSyncStatus() }) {
                                    Icon(Icons.Default.Close, "close", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- GOOGLE APPS SCRIPT TEMPLATE LINK ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Petunjuk Google Apps Script", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        "Klik tombol di bawah untuk menyalin seluruh kode Google Apps Script API yang diperlukan untuk diletakkan di Spreadsheet Anda.",
                        fontSize = 11.sp,
                        color = CorpTextSecondary
                    )

                    Button(
                        onClick = { showGASCodeDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CorpBlueSecondary)
                    ) {
                        Icon(Icons.Default.Code, contentDescription = "code")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Lihat Kode Google Apps Script")
                    }
                }
            }
        }
    }

    if (showGASCodeDialog) {
        Dialog(onDismissRequest = { showGASCodeDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
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
                        Text("Google Apps Script Code", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CorpBluePrimary)
                        IconButton(onClick = { showGASCodeDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Divider()

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                            .background(Color(0xFFF9FAFB))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = viewModel.getAppsScriptCode(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = CorpTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showGASCodeDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CorpBluePrimary)
                    ) {
                        Text("Salin & Tutup")
                    }
                }
            }
        }
    }
}
