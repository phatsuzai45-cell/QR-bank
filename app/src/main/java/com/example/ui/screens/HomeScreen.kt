package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BankAccountConfig
import com.example.domain.CurrencyUtils
import com.example.domain.VietnameseBanks
import com.example.ui.HomeUiState
import com.example.ui.components.BankLogoBadge
import com.example.ui.components.GradientPrimaryButton
import com.example.ui.components.VietQrVerifiedBadge
import com.example.ui.components.pressScaleEffect
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.SuccessEmerald

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    bankAccount: BankAccountConfig,
    hapticEnabled: Boolean,
    onAmountTextChanged: (String) -> Unit,
    onKeypadDigit: (String) -> Unit,
    onKeypadBackspace: () -> Unit,
    onClearAmount: () -> Unit,
    onQuickAmountSelected: (Long) -> Unit,
    onToggleQuickChipMode: () -> Unit,
    onCustomMessageChanged: (String) -> Unit,
    onGenerateQrClicked: () -> Unit,
    onEditBankClicked: () -> Unit,
    onQuickSettingsClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var showDirectInput by remember { mutableStateOf(false) }
    var isEditingTransferContent by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    fun triggerHaptic() {
        if (hapticEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    val matchedBank = remember(bankAccount.bankBin) {
        VietnameseBanks.findByBin(bankAccount.bankBin)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(240)) + scaleIn(initialScale = 0.98f, animationSpec = tween(240))
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 104.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. HEADER: Logo Smart QR Payment + Icon VietQR nhỏ + Nút cài đặt nhanh
                FintechCompactHeader(
                    onQuickSettingsClicked = onQuickSettingsClicked
                )

                // 2. BANK CARD: Gradient xanh dương + Hiệu ứng ánh sáng nhẹ + Logo NH + STK + Chủ TK + Nút chỉnh sửa
                PremiumBankGradientCard(
                    bankAccount = bankAccount,
                    brandColorHex = matchedBank?.brandColorHex ?: 0xFF1769FF,
                    onEditBankClicked = onEditBankClicked
                )

                // 3. AMOUNT CARD: Nổi bật nhất trên màn hình + Animation khi số tiền thay đổi
                HeroAmountCard(
                    amountVnd = uiState.amountVnd,
                    showDirectInput = showDirectInput,
                    onToggleDirectInput = { showDirectInput = !showDirectInput },
                    onAmountTextChanged = onAmountTextChanged,
                    onClearAmount = {
                        triggerHaptic()
                        onClearAmount()
                    }
                )

                // 4. QUICK AMOUNT: Các nút chọn nhanh 10K, 20K, 30K, 50K, 100K dạng Pill Button
                QuickAmountPillRow(
                    selectedAmount = uiState.amountVnd,
                    isAdditiveMode = uiState.isAdditiveQuickChipMode,
                    onToggleMode = {
                        triggerHaptic()
                        onToggleQuickChipMode()
                    },
                    onSelectQuickAmount = { amount ->
                        triggerHaptic()
                        onQuickAmountSelected(amount)
                    }
                )

                // 5. TRANSFER CONTENT CARD: Card nội dung chuyển khoản gọn đẹp, cho phép chỉnh sửa dễ dàng
                TransferContentCard(
                    customMessage = uiState.customMessage,
                    defaultMessage = bankAccount.defaultTransferMessage,
                    isEditing = isEditingTransferContent,
                    onToggleEditing = { isEditingTransferContent = !isEditingTransferContent },
                    onCustomMessageChanged = onCustomMessageChanged
                )

                // Thông báo lỗi hợp lệ nếu có
                AnimatedVisibility(
                    visible = uiState.validationError != null,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    uiState.validationError?.let { errorMsg ->
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Lỗi",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = errorMsg,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }

                // 6. KEYPAD: Bàn phím số 0–9 chuyên nghiệp, nút lớn đều nhau, nút 000 & nút xóa riêng
                FintechNumericKeypad(
                    onDigit = { digits ->
                        triggerHaptic()
                        onKeypadDigit(digits)
                    },
                    onBackspace = {
                        triggerHaptic()
                        onKeypadBackspace()
                    }
                )

                // 7. CREATE QR BUTTON: Nút tạo QR lớn, Gradient xanh dương, chữ trắng nổi bật, hiệu ứng nhấn
                GradientPrimaryButton(
                    text = "TẠO MÃ QR • ${CurrencyUtils.formatShortDong(uiState.amountVnd)}",
                    icon = Icons.Default.QrCode2,
                    isLoading = uiState.isGeneratingQr,
                    onClick = {
                        triggerHaptic()
                        onGenerateQrClicked()
                    },
                    modifier = Modifier.testTag("generate_qr_button")
                )
            }
        }
    }
}

@Composable
private fun FintechCompactHeader(
    onQuickSettingsClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = RoyalBluePrimary,
                shadowElevation = 4.dp,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Smart QR Payment Logo",
                        tint = Color.White,
                        modifier = Modifier.size(21.dp)
                    )
                }
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Smart QR Payment",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    VietQrVerifiedBadge(text = "VietQR")
                }
                Text(
                    text = "Thanh toán chuyển khoản chuẩn NAPAS 247",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Surface(
            onClick = onQuickSettingsClicked,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
            shadowElevation = 2.dp,
            modifier = Modifier
                .size(40.dp)
                .testTag("header_quick_settings_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Cài đặt nhanh",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun PremiumBankGradientCard(
    bankAccount: BankAccountConfig,
    brandColorHex: Long,
    onEditBankClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .pressScaleEffect(0.985f)
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = RoyalBluePrimary.copy(alpha = 0.28f),
                spotColor = RoyalBluePrimary.copy(alpha = 0.28f)
            )
            .clip(RoundedCornerShape(22.dp))
            .clickable { onEditBankClicked() }
            .testTag("bank_summary_card"),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            RoyalBlueDark,
                            RoyalBluePrimary,
                            SecondaryBlue
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(900f, 350f)
                    )
                )
                .drawBehind {
                    // Hiệu ứng ánh sáng nhẹ (Soft Ambient Light Glow)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.22f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.88f, size.height * 0.18f),
                            radius = size.width * 0.45f
                        ),
                        center = Offset(size.width * 0.88f, size.height * 0.18f),
                        radius = size.width * 0.45f
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.12f, size.height * 0.95f),
                            radius = size.width * 0.35f
                        ),
                        center = Offset(size.width * 0.12f, size.height * 0.95f),
                        radius = size.width * 0.35f
                    )
                }
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    BankLogoBadge(
                        bankShortName = bankAccount.bankShortName,
                        brandColorHex = brandColorHex,
                        size = 46.dp,
                        lightBackground = true
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = bankAccount.bankShortName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                            VietQrVerifiedBadge(
                                text = "NAPAS 247",
                                isLightOnBlue = true
                            )
                        }

                        Text(
                            text = bankAccount.accountNumber,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            ),
                            color = Color.White
                        )

                        Text(
                            text = bankAccount.accountHolder.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onEditBankClicked() }
                        .testTag("change_bank_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Chỉnh sửa tài khoản",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Sửa TK",
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
}

@Composable
private fun HeroAmountCard(
    amountVnd: Long,
    showDirectInput: Boolean,
    onToggleDirectInput: () -> Unit,
    onAmountTextChanged: (String) -> Unit,
    onClearAmount: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = RoyalBluePrimary.copy(alpha = 0.14f),
                spotColor = RoyalBluePrimary.copy(alpha = 0.14f)
            )
            .testTag("amount_display_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.5.dp, RoyalBluePrimary.copy(alpha = 0.22f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar inside Amount Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SỐ TIỀN CẦN NHẬN",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        onClick = onToggleDirectInput,
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.testTag("toggle_direct_input_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Keyboard,
                                contentDescription = "Nhập thủ công",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (showDirectInput) "Ẩn ô nhập" else "Nhập thủ công",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (amountVnd > 0L) {
                        IconButton(
                            onClick = onClearAmount,
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("clear_amount_top_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Xóa số tiền",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Animated Amount Display + VNĐ unit badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                AnimatedContent(
                    targetState = amountVnd,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInVertically { height -> height / 3 } + fadeIn(tween(150)))
                                .togetherWith(slideOutVertically { height -> -height / 3 } + fadeOut(tween(120)))
                        } else {
                            (slideInVertically { height -> -height / 3 } + fadeIn(tween(150)))
                                .togetherWith(slideOutVertically { height -> height / 3 } + fadeOut(tween(120)))
                        }
                    },
                    label = "amount_transition"
                ) { currentAmount ->
                    Text(
                        text = CurrencyUtils.formatNumberDots(currentAmount),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = when {
                                currentAmount >= 100_000_000L -> 30.sp
                                currentAmount >= 10_000_000L -> 34.sp
                                else -> 38.sp
                            },
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = if (currentAmount > 0L) {
                            RoyalBluePrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("formatted_amount_text")
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RoyalBluePrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "VNĐ",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = RoyalBluePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Đọc số tiền bằng chữ Tiếng Việt
            Text(
                text = CurrencyUtils.numberToVietnameseWords(amountVnd),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            AnimatedVisibility(visible = showDirectInput) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    OutlinedTextField(
                        value = if (amountVnd == 0L) "" else amountVnd.toString(),
                        onValueChange = onAmountTextChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("direct_amount_input"),
                        label = { Text("Nhập số tiền trực tiếp") },
                        placeholder = { Text("Ví dụ: 150000") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done
                        ),
                        shape = RoundedCornerShape(16.dp),
                        suffix = {
                            Text(
                                text = "VNĐ",
                                fontWeight = FontWeight.Bold,
                                color = RoyalBluePrimary
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickAmountPillRow(
    selectedAmount: Long,
    isAdditiveMode: Boolean,
    onToggleMode: () -> Unit,
    onSelectQuickAmount: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Chọn nhanh mệnh giá",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Surface(
                onClick = onToggleMode,
                shape = RoundedCornerShape(50),
                color = if (isAdditiveMode) {
                    RoyalBluePrimary.copy(alpha = 0.14f)
                } else {
                    MaterialTheme.colorScheme.surface
                },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.testTag("toggle_quick_mode_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isAdditiveMode) Icons.Default.AddCircleOutline else Icons.Default.SwapHoriz,
                        contentDescription = null,
                        tint = RoyalBluePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (isAdditiveMode) "Cộng dồn (+)" else "Chọn mức",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = RoyalBluePrimary
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CurrencyUtils.defaultQuickAmounts.forEach { amount ->
                val isSelected = !isAdditiveMode && selectedAmount == amount
                val pillBgColor by animateColorAsState(
                    targetValue = if (isSelected) RoyalBluePrimary else MaterialTheme.colorScheme.surface,
                    animationSpec = tween(180),
                    label = "pill_bg"
                )
                val pillTextColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    animationSpec = tween(180),
                    label = "pill_text"
                )
                val prefix = if (isAdditiveMode) "+" else ""

                Surface(
                    onClick = { onSelectQuickAmount(amount) },
                    shape = RoundedCornerShape(50),
                    color = pillBgColor,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) RoyalBluePrimary else MaterialTheme.colorScheme.outline
                    ),
                    shadowElevation = if (isSelected) 4.dp else 1.dp,
                    modifier = Modifier
                        .pressScaleEffect(0.94f)
                        .testTag("quick_chip_$amount")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$prefix${CurrencyUtils.formatCompactK(amount)}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = pillTextColor
                        )
                        Text(
                            text = CurrencyUtils.formatShortDong(amount),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = if (isSelected) {
                                Color.White.copy(alpha = 0.85f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransferContentCard(
    customMessage: String,
    defaultMessage: String,
    isEditing: Boolean,
    onToggleEditing: () -> Unit,
    onCustomMessageChanged: (String) -> Unit
) {
    val activeText = customMessage.ifBlank { defaultMessage }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "Nội dung chuyển khoản",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = activeText,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    onClick = onToggleEditing,
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (isEditing) "Xong" else "Chỉnh sửa",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = RoyalBluePrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isEditing) {
                OutlinedTextField(
                    value = customMessage,
                    onValueChange = onCustomMessageChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .testTag("transfer_message_input"),
                    placeholder = { Text("Mặc định: $defaultMessage") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalBluePrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    trailingIcon = {
                        if (customMessage.isNotEmpty()) {
                            IconButton(onClick = { onCustomMessageChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Xóa nội dung"
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FintechNumericKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("000", "0", "BACKSPACE")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowKeys.forEach { key ->
                    when (key) {
                        "BACKSPACE" -> {
                            Surface(
                                onClick = onBackspace,
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .pressScaleEffect(0.93f)
                                    .weight(1f)
                                    .height(54.dp)
                                    .testTag("keypad_backspace")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Xóa lùi chữ số",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        "000" -> {
                            Surface(
                                onClick = { onDigit("000") },
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = BorderStroke(1.dp, RoyalBluePrimary.copy(alpha = 0.3f)),
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .pressScaleEffect(0.93f)
                                    .weight(1f)
                                    .height(54.dp)
                                    .testTag("keypad_000")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "000",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = RoyalBluePrimary
                                    )
                                }
                            }
                        }

                        else -> {
                            Surface(
                                onClick = { onDigit(key) },
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                                shadowElevation = 1.5.dp,
                                modifier = Modifier
                                    .pressScaleEffect(0.93f)
                                    .weight(1f)
                                    .height(54.dp)
                                    .testTag("keypad_$key")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontFamily = JetBrainsMonoFontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
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
