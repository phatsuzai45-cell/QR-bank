package com.example.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Dialpad
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontStyle
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
import com.example.ui.components.PhatPayVectorLogo
import com.example.ui.components.UltraFintechBackdrop
import com.example.ui.components.pressScaleEffect
import com.example.ui.components.staggeredEntrance
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.RoyalBlueDark
import com.example.ui.theme.RoyalBluePrimary
import com.example.ui.theme.SecondaryBlue

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    bankAccount: BankAccountConfig,
    hapticEnabled: Boolean,
    darkTheme: Boolean = false,
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
    var isKeypadExpanded by remember { mutableStateOf(true) }

    fun triggerHaptic() {
        if (hapticEnabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    val matchedBank = remember(bankAccount.bankBin) {
        VietnameseBanks.findByBin(bankAccount.bankBin)
    }

    UltraFintechBackdrop(
        headerHeightDp = 250.dp,
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp, bottom = 108.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. ULTRA PREMIUM HEADER: Logo + Smart QR Payment + Pro Badge + Settings Gear + Tagline Pill
                UltraFintechHeader(
                    onQuickSettingsClicked = onQuickSettingsClicked,
                    modifier = Modifier.staggeredEntrance(0)
                )

                // 2. GLASSMORPHIC BANK CARD: Deep Navy-Blue Card + Shield Emblem + "Đổi TK" Pill
                UltraBankCard(
                    bankAccount = bankAccount,
                    brandColorHex = matchedBank?.brandColorHex ?: 0xFF00A86B,
                    onEditBankClicked = onEditBankClicked,
                    modifier = Modifier.staggeredEntrance(1)
                )

                // 3. UNIFIED HERO AMOUNT & QUICK AMOUNT PILLS CARD
                UltraAmountAndQuickPillsCard(
                    amountVnd = uiState.amountVnd,
                    isAdditiveMode = uiState.isAdditiveQuickChipMode,
                    showDirectInput = showDirectInput,
                    isKeypadExpanded = isKeypadExpanded,
                    onToggleDirectInput = { showDirectInput = !showDirectInput },
                    onToggleKeypad = { isKeypadExpanded = !isKeypadExpanded },
                    onAmountTextChanged = onAmountTextChanged,
                    onClearAmount = {
                        triggerHaptic()
                        onClearAmount()
                    },
                    onToggleMode = {
                        triggerHaptic()
                        onToggleQuickChipMode()
                    },
                    onSelectQuickAmount = { amount ->
                        triggerHaptic()
                        onQuickAmountSelected(amount)
                    },
                    modifier = Modifier.staggeredEntrance(2)
                )

                // 4. TRANSFER CONTENT CARD: "Nội dung chuyển khoản" + Chevron >
                UltraTransferContentCard(
                    customMessage = uiState.customMessage,
                    defaultMessage = bankAccount.defaultTransferMessage,
                    isEditing = isEditingTransferContent,
                    onToggleEditing = { isEditingTransferContent = !isEditingTransferContent },
                    onCustomMessageChanged = onCustomMessageChanged,
                    modifier = Modifier.staggeredEntrance(3)
                )

                // Hiển thị thông báo lỗi hợp lệ nếu có
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
                                    imageVector = Icons.Outlined.ErrorOutline,
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

                // 5. ERGONOMIC ONE-HANDED KEYPAD (Có hiệu ứng nhấn + xúc giác)
                AnimatedVisibility(visible = isKeypadExpanded) {
                    UltraNumericKeypad(
                        onDigit = { digits ->
                            triggerHaptic()
                            onKeypadDigit(digits)
                        },
                        onBackspace = {
                            triggerHaptic()
                            onKeypadBackspace()
                        },
                        modifier = Modifier.staggeredEntrance(4)
                    )
                }

                // 6. PRIMARY CTA BUTTON: "Tạo mã QR   >"
                GradientPrimaryButton(
                    text = "Tạo mã QR",
                    icon = Icons.Outlined.QrCode2,
                    isLoading = uiState.isGeneratingQr,
                    onClick = {
                        triggerHaptic()
                        onGenerateQrClicked()
                    },
                    modifier = Modifier
                        .staggeredEntrance(5)
                        .testTag("generate_qr_button")
                )
            }
        }
    }
}

@Composable
private fun UltraFintechHeader(
    onQuickSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PhatPayVectorLogo(
                size = 46.dp,
                isDarkBackground = true,
                showRoundedBox = false
            )

            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PHATPAY",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                            fontSize = 23.sp,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFFFB800).copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F).copy(alpha = 0.65f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.WorkspacePremium,
                                contentDescription = null,
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "VietQR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }

                Text(
                    text = "Tạo QR nhanh – Thu tiền dễ dàng",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    ),
                    color = Color(0xFFBCE6FF)
                )
            }
        }

        Surface(
            onClick = onQuickSettingsClicked,
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.16f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.32f)),
            modifier = Modifier
                .pressScaleEffect()
                .size(40.dp)
                .testTag("header_quick_settings_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Cài đặt nhanh",
                    tint = Color.White,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@Composable
private fun UltraBankCard(
    bankAccount: BankAccountConfig,
    brandColorHex: Long,
    onEditBankClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .pressScaleEffect(0.985f)
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = Color(0xFF04143A).copy(alpha = 0.45f),
                spotColor = Color(0xFF04143A).copy(alpha = 0.45f)
            )
            .clip(RoundedCornerShape(22.dp))
            .clickable { onEditBankClicked() }
            .testTag("bank_summary_card"),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF062360),
                            Color(0xFF0B3894),
                            Color(0xFF1557D6)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(900f, 300f)
                    )
                )
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF4C8DFF).copy(alpha = 0.28f),
                                Color.Transparent
                            ),
                            center = Offset(size.width * 0.85f, size.height * 0.2f),
                            radius = size.width * 0.45f
                        ),
                        center = Offset(size.width * 0.85f, size.height * 0.2f),
                        radius = size.width * 0.45f
                    )
                }
                .padding(horizontal = 16.dp, vertical = 15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    BankLogoBadge(
                        bankShortName = bankAccount.bankShortName,
                        brandColorHex = brandColorHex,
                        size = 46.dp,
                        lightBackground = true
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = bankAccount.bankShortName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = Color.White
                        )

                        Text(
                            text = bankAccount.accountNumber,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.7.sp
                            ),
                            color = Color.White.copy(alpha = 0.95f)
                        )

                        Text(
                            text = bankAccount.accountHolder.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color.White.copy(alpha = 0.78f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Glassmorphic "✏️ Đổi TK" pill button
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.32f)),
                    modifier = Modifier
                        .pressScaleEffect(0.94f)
                        .clip(RoundedCornerShape(50))
                        .clickable { onEditBankClicked() }
                        .testTag("change_bank_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Đổi tài khoản",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Đổi TK",
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
private fun UltraAmountAndQuickPillsCard(
    amountVnd: Long,
    isAdditiveMode: Boolean,
    showDirectInput: Boolean,
    isKeypadExpanded: Boolean,
    onToggleDirectInput: () -> Unit,
    onToggleKeypad: () -> Unit,
    onAmountTextChanged: (String) -> Unit,
    onClearAmount: () -> Unit,
    onToggleMode: () -> Unit,
    onSelectQuickAmount: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = RoyalBluePrimary.copy(alpha = 0.16f),
                spotColor = RoyalBluePrimary.copy(alpha = 0.16f)
            )
            .testTag("amount_display_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar: Icon túi tiền + "Số tiền cần nhận" + Nút nhập thủ công / Bàn phím + Nút Xóa (X)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = RoyalBluePrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Paid,
                                contentDescription = null,
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = "SỐ TIỀN CẦN NHẬN",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.4.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        onClick = onToggleDirectInput,
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.testTag("toggle_direct_input_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Keyboard,
                                contentDescription = "Nhập thủ công",
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (showDirectInput) "Ẩn ô nhập" else "Nhập tay",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = RoyalBluePrimary
                            )
                        }
                    }

                    Surface(
                        onClick = onToggleKeypad,
                        shape = RoundedCornerShape(50),
                        color = if (isKeypadExpanded) {
                            RoyalBluePrimary.copy(alpha = 0.12f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Dialpad,
                                contentDescription = "Ẩn hiện bàn phím số",
                                tint = RoyalBluePrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isKeypadExpanded) "Phím số" else "Mở phím",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = RoyalBluePrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = onClearAmount,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("clear_amount_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Xóa số tiền",
                            tint = RoyalBluePrimary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Hiển thị số tiền thật lớn + Animation khi thay đổi + Đơn vị VNĐ
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                AnimatedContent(
                    targetState = amountVnd,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInVertically { h -> h / 3 } + fadeIn(tween(150)))
                                .togetherWith(slideOutVertically { h -> -h / 3 } + fadeOut(tween(120)))
                        } else {
                            (slideInVertically { h -> -h / 3 } + fadeIn(tween(150)))
                                .togetherWith(slideOutVertically { h -> h / 3 } + fadeOut(tween(120)))
                        }
                    },
                    label = "amount_value_anim"
                ) { currentAmount ->
                    Text(
                        text = CurrencyUtils.formatNumberDots(currentAmount),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = when {
                                currentAmount >= 100_000_000L -> 30.sp
                                currentAmount >= 10_000_000L -> 35.sp
                                else -> 40.sp
                            },
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = if (currentAmount > 0L) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("formatted_amount_text")
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "VNĐ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = RoyalBluePrimary
                )
            }

            // Đọc số tiền bằng chữ Tiếng Việt
            Text(
                text = CurrencyUtils.numberToVietnameseWords(amountVnd),
                style = MaterialTheme.typography.labelSmall.copy(
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

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f),
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // QUICK AMOUNT PILLS (10K, 20K, 30K, 50K, 100K...) tích hợp gọn đẹp ngay dưới số tiền
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CurrencyUtils.defaultQuickAmounts.forEach { amount ->
                    val isSelected = !isAdditiveMode && amountVnd == amount
                    val pillBg by animateColorAsState(
                        targetValue = if (isSelected) {
                            RoyalBluePrimary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                        },
                        animationSpec = tween(160),
                        label = "quick_pill_bg"
                    )
                    val pillText by animateColorAsState(
                        targetValue = if (isSelected) {
                            Color.White
                        } else {
                            RoyalBluePrimary
                        },
                        animationSpec = tween(160),
                        label = "quick_pill_text"
                    )
                    val prefix = if (isAdditiveMode) "+" else ""

                    Surface(
                        onClick = { onSelectQuickAmount(amount) },
                        shape = RoundedCornerShape(50),
                        color = pillBg,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) {
                                RoyalBluePrimary
                            } else {
                                RoyalBluePrimary.copy(alpha = 0.22f)
                            }
                        ),
                        modifier = Modifier
                            .pressScaleEffect(0.92f)
                            .testTag("quick_chip_$amount")
                    ) {
                        Text(
                            text = "$prefix${CurrencyUtils.formatCompactK(amount)}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = pillText,
                            modifier = Modifier.padding(horizontal = 15.dp, vertical = 7.dp)
                        )
                    }
                }

                // Nút chuyển chế độ Chọn mức / Cộng dồn (+)
                Surface(
                    onClick = onToggleMode,
                    shape = RoundedCornerShape(50),
                    color = if (isAdditiveMode) {
                        RoyalBluePrimary.copy(alpha = 0.15f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .pressScaleEffect(0.94f)
                        .testTag("toggle_quick_mode_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isAdditiveMode) Icons.Outlined.AddCircleOutline else Icons.Outlined.SwapHoriz,
                            contentDescription = null,
                            tint = RoyalBluePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isAdditiveMode) "Cộng dồn" else "Chọn mức",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = RoyalBluePrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UltraTransferContentCard(
    customMessage: String,
    defaultMessage: String,
    isEditing: Boolean,
    onToggleEditing: () -> Unit,
    onCustomMessageChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeText = customMessage.ifBlank { defaultMessage }

    Card(
        modifier = modifier
            .pressScaleEffect(0.99f)
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = RoyalBluePrimary.copy(alpha = 0.10f),
                spotColor = RoyalBluePrimary.copy(alpha = 0.10f)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable { onToggleEditing() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.75f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Nội dung chuyển khoản",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = activeText,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Chỉnh sửa nội dung",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isEditing) {
                OutlinedTextField(
                    value = customMessage,
                    onValueChange = onCustomMessageChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
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
                                    imageVector = Icons.Outlined.Close,
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
private fun UltraNumericKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("000", "0", "BACKSPACE")
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        rows.forEach { rowKeys ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                rowKeys.forEach { key ->
                    when (key) {
                        "BACKSPACE" -> {
                            Surface(
                                onClick = onBackspace,
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.65f)),
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .pressScaleEffect(0.92f)
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("keypad_backspace")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Xóa lùi chữ số",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(21.dp)
                                    )
                                }
                            }
                        }

                        "000" -> {
                            Surface(
                                onClick = { onDigit("000") },
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                border = BorderStroke(1.dp, RoyalBluePrimary.copy(alpha = 0.28f)),
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .pressScaleEffect(0.92f)
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("keypad_000")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "000",
                                        style = MaterialTheme.typography.titleMedium.copy(
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
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)),
                                shadowElevation = 1.5.dp,
                                modifier = Modifier
                                    .pressScaleEffect(0.92f)
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("keypad_$key")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.titleLarge.copy(
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
