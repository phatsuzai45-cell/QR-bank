package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.domain.VietnameseBanks
import com.example.ui.components.BankLogoBadge
import com.example.ui.components.GradientPrimaryButton
import com.example.ui.components.VietQrVerifiedBadge
import com.example.ui.components.pressScaleEffect
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SecondaryBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    historyItems: List<QrHistoryEntity>,
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
    var showConfirmClearAll by remember { mutableStateOf(false) }
    var selectedDetailItem by remember { mutableStateOf<QrHistoryEntity?>(null) }

    val filteredItems = remember(historyItems, searchQuery) {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) {
            historyItems
        } else {
            historyItems.filter {
                it.amountVnd.toString().contains(q) ||
                    CurrencyUtils.formatNumberDots(it.amountVnd).contains(q) ||
                    it.bankShortName.lowercase().contains(q) ||
                    it.accountNumber.lowercase().contains(q) ||
                    it.transferMessage.lowercase().contains(q)
            }
        }
    }

    val totalAmountCreated = remember(historyItems) {
        historyItems.sumOf { it.amountVnd }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Thẻ tổng quan lịch sử Gradient Xanh Dương
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(RoyalBlueDark, RoyalBluePrimary, SecondaryBlue)
                            )
                        )
                        .padding(horizontal = 18.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "LỊCH SỬ MÃ QR ĐÃ TẠO (${historyItems.size})",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.6.sp
                                    ),
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                            Text(
                                text = CurrencyUtils.formatVnd(totalAmountCreated),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = Color.White
                            )
                        }

                        if (historyItems.isNotEmpty()) {
                            Surface(
                                onClick = { showConfirmClearAll = true },
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                                modifier = Modifier.testTag("clear_all_history_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = "Xóa tất cả",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "Xóa hết",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Thanh tìm kiếm
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
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = RoyalBluePrimary
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )
            }

            // 3. Danh sách mã QR hoặc Empty State
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
                                modifier = Modifier.size(68.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = RoyalBluePrimary,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                            }

                            Text(
                                text = if (historyItems.isEmpty()) {
                                    "Chưa có lịch sử mã QR"
                                } else {
                                    "Không tìm thấy mã QR phù hợp"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Text(
                                text = "Các mã VietQR bạn tạo sẽ tự động lưu tại đây bằng Room Database để xem chi tiết hoặc mở lại chỉ với 1 chạm.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            if (historyItems.isEmpty()) {
                                GradientPrimaryButton(
                                    text = "TẠO MÃ QR ĐẦU TIÊN",
                                    icon = Icons.Default.AddCircleOutline,
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
                    items(filteredItems, key = { it.id }) { item ->
                        HistoryQrFintechCard(
                            item = item,
                            onOpenQr = { onOpenHistoryQr(item) },
                            onShowDetail = { selectedDetailItem = item },
                            onDelete = { onDeleteItem(item.id) }
                        )
                    }
                }
            }
        }
    }

    // MÀN HÌNH PHỤ: CHI TIẾT MÃ QR (QR Detail Modal Bottom Sheet)
    selectedDetailItem?.let { detailItem ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val previewBitmap = remember(detailItem.vietQrPayload) {
            VietQrGenerator.generateHighResQrBitmap(detailItem.vietQrPayload, sizePx = 512)
        }
        val formattedTime = remember(detailItem.createdAt) {
            SimpleDateFormat("HH:mm:ss • dd/MM/yyyy", Locale.getDefault()).format(Date(detailItem.createdAt))
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
                    VietQrVerifiedBadge(text = "CRC ${detailItem.crcHex}")
                }

                Text(
                    text = CurrencyUtils.formatVnd(detailItem.amountVnd),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = RoyalBluePrimary
                )

                // Xem nhanh QR trong bảng Chi tiết
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
                            imageVector = Icons.Default.Delete,
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
                            imageVector = Icons.Default.QrCode2,
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
    onDelete: () -> Unit
) {
    val formattedDate = remember(item.createdAt) {
        SimpleDateFormat("HH:mm • dd/MM/yyyy", Locale.getDefault()).format(Date(item.createdAt))
    }
    val bankColor = remember(item.bankBin) {
        VietnameseBanks.findByBin(item.bankBin)?.brandColorHex ?: 0xFF1769FF
    }

    Card(
        onClick = onOpenQr,
        modifier = Modifier
            .pressScaleEffect(0.985f)
            .fillMaxWidth()
            .testTag("history_item_${item.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                BankLogoBadge(
                    bankShortName = item.bankShortName,
                    brandColorHex = bankColor,
                    size = 44.dp
                )

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = CurrencyUtils.formatVnd(item.amountVnd),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = RoyalBluePrimary
                    )

                    Text(
                        text = "${item.bankShortName} • ${item.accountNumber}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (item.transferMessage.isNotBlank()) {
                        Text(
                            text = "ND: ${item.transferMessage}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onShowDetail,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Chi tiết mã QR",
                        tint = RoyalBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("delete_history_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa mục lịch sử",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
