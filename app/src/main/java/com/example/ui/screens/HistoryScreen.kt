package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QrHistoryEntity
import com.example.domain.CurrencyUtils
import com.example.domain.VietQrGenerator
import com.example.ui.components.GradientPrimaryButton
import com.example.ui.components.UltraFintechBackdrop
import com.example.ui.components.VietQrVerifiedBadge
import com.example.ui.components.pressScaleEffect
import com.example.ui.components.staggeredEntrance
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.SuccessEmerald
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private enum class HistoryTimeFilter(val label: String) {
    ALL("Tất cả"),
    TODAY("Hôm nay"),
    THIS_WEEK("Tuần này"),
    THIS_MONTH("Tháng này")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyItems: List<QrHistoryEntity>,
    darkTheme: Boolean = false,
    onOpenHistoryQr: (QrHistoryEntity) -> Unit,
    onDeleteItem: (Long) -> Unit,
    onClearAllHistory: () -> Unit,
    onNavigateBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBackHome()
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(HistoryTimeFilter.ALL) }
    var showAnalyticsPanel by remember { mutableStateOf(true) }
    var showConfirmClearAll by remember { mutableStateOf(false) }
    var selectedDetailItem by remember { mutableStateOf<QrHistoryEntity?>(null) }

    val timeFilteredItems = remember(historyItems, selectedFilter) {
        val now = Calendar.getInstance()
        historyItems.filter { item ->
            when (selectedFilter) {
                HistoryTimeFilter.ALL -> true
                HistoryTimeFilter.TODAY -> {
                    val cal = Calendar.getInstance().apply { timeInMillis = item.createdAt }
                    cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        cal.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                }
                HistoryTimeFilter.THIS_WEEK -> {
                    val diffMs = now.timeInMillis - item.createdAt
                    diffMs <= 7L * 24 * 3600 * 1000L
                }
                HistoryTimeFilter.THIS_MONTH -> {
                    val cal = Calendar.getInstance().apply { timeInMillis = item.createdAt }
                    cal.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        cal.get(Calendar.MONTH) == now.get(Calendar.MONTH)
                }
            }
        }
    }

    val filteredItems = remember(timeFilteredItems, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) {
            timeFilteredItems
        } else {
            timeFilteredItems.filter {
                it.amountVnd.toString().contains(q) ||
                    CurrencyUtils.formatNumberDots(it.amountVnd).contains(q) ||
                    it.bankShortName.lowercase().contains(q) ||
                    it.accountNumber.lowercase().contains(q) ||
                    it.transferMessage.lowercase().contains(q)
            }
        }
    }

    UltraFintechBackdrop(
        headerHeightDp = 165.dp,
        darkTheme = darkTheme
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp, bottom = 104.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. TOP HEADER BAR TRÊN NỀN SÓNG XANH
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(0),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Lịch sử mã QR",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            onClick = { showAnalyticsPanel = !showAnalyticsPanel },
                            shape = RoundedCornerShape(50),
                            color = if (showAnalyticsPanel) {
                                Color.White.copy(alpha = 0.25f)
                            } else {
                                Color.White.copy(alpha = 0.14f)
                            },
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.32f)),
                            modifier = Modifier.pressScaleEffect()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.BarChart,
                                    contentDescription = "Thống kê",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Thống kê",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                            }
                        }

                        if (historyItems.isNotEmpty()) {
                            Surface(
                                onClick = { showConfirmClearAll = true },
                                shape = RoundedCornerShape(50),
                                color = Color.White.copy(alpha = 0.14f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.28f)),
                                modifier = Modifier
                                    .pressScaleEffect()
                                    .testTag("clear_all_history_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteSweep,
                                        contentDescription = "Xóa hết",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Xóa",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. THANH CHỌN THỜI GIAN DẠNG PILL: Tất cả | Hôm nay | Tuần này | Tháng này
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .staggeredEntrance(1)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HistoryTimeFilter.entries.forEach { filter ->
                            val isSelected = selectedFilter == filter
                            val bg by animateColorAsState(
                                targetValue = if (isSelected) {
                                    RoyalBluePrimary.copy(alpha = 0.14f)
                                } else {
                                    Color.Transparent
                                },
                                animationSpec = tween(160),
                                label = "filter_bg"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) {
                                    RoyalBluePrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                animationSpec = tween(160),
                                label = "filter_text"
                            )

                            Surface(
                                onClick = { selectedFilter = filter },
                                shape = RoundedCornerShape(50),
                                color = bg,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = filter.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                    ),
                                    color = textColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                // 3. BẢNG THỐNG KÊ THỰC TẾ (Tính toán 100% từ dữ liệu thật trong Room Database)
                AnimatedVisibility(
                    visible = showAnalyticsPanel && timeFilteredItems.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    RealQrAnalyticsDashboard(
                        items = timeFilteredItems
                    )
                }

                // 4. THANH TÌM KIẾM
                if (historyItems.isNotEmpty()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("history_search_input"),
                        placeholder = { Text("Tìm theo số tiền, ngân hàng, nội dung...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = null,
                                tint = RoyalBluePrimary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = RoyalBluePrimary
                        )
                    )
                }

                // 5. DANH SÁCH LỊCH SỬ MÃ QR HOẶC EMPTY STATE
                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(28.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(66.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Outlined.History,
                                            contentDescription = null,
                                            tint = RoyalBluePrimary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = if (historyItems.isEmpty()) {
                                        "Chưa có lịch sử mã QR"
                                    } else {
                                        "Không có mã QR trong khoảng lọc này"
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                Text(
                                    text = "Mọi mã VietQR bạn khởi tạo sẽ được lưu tự động vào Room Database trên thiết bị để xem thống kê và mở lại nhanh.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )

                                if (historyItems.isEmpty()) {
                                    GradientPrimaryButton(
                                        text = "Tạo mã QR đầu tiên",
                                        icon = Icons.Outlined.AddCircleOutline,
                                        onClick = onNavigateBackHome,
                                        modifier = Modifier.testTag("empty_history_create_button")
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(filteredItems, key = { _, item -> item.id }) { idx, item ->
                            HistoryQrFintechCard(
                                item = item,
                                onOpenQr = { onOpenHistoryQr(item) },
                                onShowDetail = { selectedDetailItem = item },
                                onDelete = { onDeleteItem(item.id) },
                                modifier = Modifier.staggeredEntrance(idx.coerceAtMost(6), baseDelayMs = 25)
                            )
                        }
                    }
                }
            }
        }
    }

    // MÀN HÌNH CHI TIẾT MÃ QR (Modal Bottom Sheet)
    selectedDetailItem?.let { detailItem ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val previewBitmap = remember(detailItem.vietQrPayload) {
            VietQrGenerator.generateHighResQrBitmap(detailItem.vietQrPayload, sizePx = 512)
        }
        val formattedTime = remember(detailItem.createdAt) {
            SimpleDateFormat("dd/MM/yyyy • HH:mm:ss", Locale.getDefault()).format(Date(detailItem.createdAt))
        }

        ModalBottomSheet(
            onDismissRequest = { selectedDetailItem = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Chi tiết mã QR #${detailItem.id}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    VietQrVerifiedBadge(text = "Đã tạo")
                }

                Text(
                    text = CurrencyUtils.formatVnd(detailItem.amountVnd),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = RoyalBluePrimary
                )

                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(2.dp, Color(0xFFD6E4FF), RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = previewBitmap.asImageBitmap(),
                        contentDescription = "Chi tiết mã QR",
                        contentScale = ContentScale.Fit,
                        filterQuality = FilterQuality.None,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DetailInfoRow("Ngân hàng", "${detailItem.bankShortName} (BIN ${detailItem.bankBin})")
                        DetailInfoRow("Số tài khoản", detailItem.accountNumber)
                        DetailInfoRow("Chủ tài khoản", detailItem.accountHolder)
                        if (detailItem.transferMessage.isNotBlank()) {
                            DetailInfoRow("Nội dung CK", detailItem.transferMessage)
                        }
                        DetailInfoRow("Thời gian tạo", formattedTime)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val idToDelete = detailItem.id
                            selectedDetailItem = null
                            onDeleteItem(idToDelete)
                        },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Xóa mã này", color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = {
                            val itemToOpen = detailItem
                            selectedDetailItem = null
                            onOpenHistoryQr(itemToOpen)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBluePrimary),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCode2,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mở màn hình QR", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showConfirmClearAll) {
        AlertDialog(
            onDismissRequest = { showConfirmClearAll = false },
            title = { Text("Xóa toàn bộ lịch sử?") },
            text = {
                Text("Bạn có chắc chắn muốn xóa tất cả ${historyItems.size} mã QR đã lưu trong lịch sử không?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmClearAll = false
                        onClearAllHistory()
                    }
                ) {
                    Text(
                        text = "Xóa tất cả",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmClearAll = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

/**
 * Bảng thống kê thực tế được tính toán trực tiếp từ danh sách lịch sử Room Database (Không tạo dữ liệu giả)
 */
@Composable
private fun RealQrAnalyticsDashboard(
    items: List<QrHistoryEntity>
) {
    val totalAmount = remember(items) { items.sumOf { it.amountVnd } }
    val count = items.size
    val averageAmount = remember(totalAmount, count) {
        if (count > 0) totalAmount / count else 0L
    }

    // Thống kê theo thứ trong tuần (T2..CN) từ dữ liệu thật
    val dayBuckets = remember(items) {
        val labels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
        val sums = LongArray(7)
        val cal = Calendar.getInstance()
        items.forEach { item ->
            cal.timeInMillis = item.createdAt
            val dow = cal.get(Calendar.DAY_OF_WEEK) // SUNDAY=1 .. SATURDAY=7
            val idx = if (dow == Calendar.SUNDAY) 6 else dow - 2
            sums[idx.coerceIn(0, 6)] += item.amountVnd
        }
        val maxVal = (sums.maxOrNull() ?: 1L).coerceAtLeast(1L)
        labels.mapIndexed { i, label ->
            Triple(label, sums[i], (sums[i].toFloat() / maxVal.toFloat()).coerceIn(0.12f, 1f))
        }
    }

    // Mệnh giá phổ biến nhất (Top mệnh giá)
    val topDenomination = remember(items) {
        val grouped = items.groupBy { it.amountVnd }
        val topEntry = grouped.maxByOrNull { it.value.size }
        if (topEntry != null && items.isNotEmpty()) {
            val pct = (topEntry.value.size.toFloat() / items.size.toFloat()).coerceIn(0f, 1f)
            Pair(topEntry.key, pct)
        } else {
            null
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Card Tổng tiền + Biểu đồ cột T2..CN
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(RoyalBlueDark, RoyalBluePrimary, SecondaryBlue)
                        )
                    )
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Tổng tiền đã tạo QR",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                text = CurrencyUtils.formatShortDong(totalAmount),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = SuccessEmerald.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, Color(0xFF34D399).copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "$count mã QR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Biểu đồ 7 cột T2..CN gọn gàng
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        dayBuckets.forEach { (label, sum, ratio) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .weight(1f, fill = false)
                                        .fillMaxHeight(ratio)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            if (sum > 0L) {
                                                Color(0xFF60A5FA)
                                            } else {
                                                Color.White.copy(alpha = 0.18f)
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hàng 2 thẻ: Lượt tạo QR & Trung bình/lần
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                shadowElevation = 2.dp,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = RoyalBluePrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.QrCode2,
                                contentDescription = null,
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Lượt tạo QR",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                shadowElevation = 2.dp,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Paid,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = CurrencyUtils.formatShortDong(averageAmount),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Trung bình/lần",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Top mệnh giá
        topDenomination?.let { (topAmount, ratio) ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Top: ${CurrencyUtils.formatShortDong(topAmount)}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LinearProgressIndicator(
                        progress = { ratio },
                        color = RoyalBluePrimary,
                        trackColor = RoyalBluePrimary.copy(alpha = 0.14f),
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                    )
                    Text(
                        text = "${(ratio * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = RoyalBluePrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun HistoryQrFintechCard(
    item: QrHistoryEntity,
    onOpenQr: () -> Unit,
    onShowDetail: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember(item.createdAt) {
        SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault()).format(Date(item.createdAt))
    }

    Card(
        onClick = onOpenQr,
        modifier = modifier
            .pressScaleEffect(0.985f)
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = RoyalBluePrimary.copy(alpha = 0.10f),
                spotColor = RoyalBluePrimary.copy(alpha = 0.10f)
            )
            .testTag("history_item_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.65f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Icon QR trong hộp xanh nhạt như thiết kế mẫu
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = RoyalBluePrimary.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, RoyalBluePrimary.copy(alpha = 0.22f)),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.QrCode2,
                            contentDescription = null,
                            tint = RoyalBluePrimary,
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = CurrencyUtils.formatShortDong(item.amountVnd),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "$formattedDate • ${item.bankShortName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (item.transferMessage.isNotBlank()) {
                        Text(
                            text = item.transferMessage,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Huy hiệu xanh ngọc "Đã tạo"
                Surface(
                    shape = RoundedCornerShape(50),
                    color = SuccessEmerald.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = "Đã tạo",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = SuccessEmerald,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                IconButton(
                    onClick = onShowDetail,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Chi tiết mã QR",
                        tint = RoyalBluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_history_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Xóa mục lịch sử",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
