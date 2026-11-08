package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.domain.CurrencyUtils
import com.example.domain.VietQrGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class ActiveQrSession(
    val amountVnd: Long,
    val bankBin: String,
    val bankShortName: String,
    val bankFullName: String,
    val accountNumber: String,
    val accountHolder: String,
    val transferMessage: String,
    val vietQrPayload: String,
    val crcHex: String,
    val qrBitmap: Bitmap,
    val createdAt: Long = System.currentTimeMillis()
)

object QrImageExporter {

    /**
     * Vẽ thẻ thanh toán VietQR độ phân giải cao (1080 x 1520 px) để lưu vào máy hoặc chia sẻ cho khách hàng
     */
    fun renderPaymentPosterBitmap(session: ActiveQrSession): Bitmap {
        val width = 1080
        val height = 1520
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Nền gradient xanh dương Royal Blue (#1769FF -> #4C8DFF) cao cấp
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                intArrayOf(
                    Color.parseColor("#0B4FD6"),
                    Color.parseColor("#1769FF"),
                    Color.parseColor("#4C8DFF")
                ),
                floatArrayOf(0f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Khung thẻ trắng trung tâm
        val cardRect = RectF(64f, 80f, width - 64f, height - 80f)
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            setShadowLayer(28f, 0f, 12f, Color.argb(70, 0, 0, 0))
        }
        canvas.drawRoundRect(cardRect, 48f, 48f, cardPaint)

        // 3. Header PHATPAY • VIETQR / NAPAS 247
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0A3D91")
            textSize = 46f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("PHATPAY • VIETQR", width / 2f, 162f, titlePaint)

        val subHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#12B981")
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("TẠO QR NHANH – THU TIỀN DỄ DÀNG • NAPAS 247", width / 2f, 206f, subHeaderPaint)

        // 4. Số tiền thật lớn ở trên QR
        val amountLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#475569")
            textSize = 26f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("SỐ TIỀN CẦN THANH TOÁN", width / 2f, 275f, amountLabelPaint)

        val amountValuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0057D9")
            textSize = 72f
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(CurrencyUtils.formatVnd(session.amountVnd), width / 2f, 355f, amountValuePaint)

        // 5. Khung viền xanh nhạt quanh mã QR
        val qrBoxSize = 640
        val qrLeft = (width - qrBoxSize) / 2f
        val qrTop = 400f
        val qrBorderRect = RectF(qrLeft - 20f, qrTop - 20f, qrLeft + qrBoxSize + 20f, qrTop + qrBoxSize + 20f)
        val qrBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E6F0FF")
            style = Paint.Style.STROKE
            strokeWidth = 6f
        }
        canvas.drawRoundRect(qrBorderRect, 32f, 32f, qrBorderPaint)

        // Vẽ Bitmap QR độ phân giải cao
        val scaledQr = Bitmap.createScaledBitmap(session.qrBitmap, qrBoxSize, qrBoxSize, false)
        canvas.drawBitmap(scaledQr, qrLeft, qrTop, null)

        // 6. Thông tin Ngân hàng & Chủ tài khoản bên dưới
        var currentY = qrTop + qrBoxSize + 85f

        val holderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0B1528")
            textSize = 40f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(session.accountHolder.uppercase(), width / 2f, currentY, holderPaint)

        currentY += 56f
        val bankPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#0A3D91")
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("${session.bankShortName} • ${session.accountNumber}", width / 2f, currentY, bankPaint)

        if (session.transferMessage.isNotBlank()) {
            currentY += 54f
            val msgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#475569")
                textSize = 28f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("Nội dung: ${session.transferMessage}", width / 2f, currentY, msgPaint)
        }

        // Footer CRC Checksum
        val footerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#94A3B8")
            textSize = 22f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "EMVCo VietQR • BIN ${session.bankBin} • CRC16: ${session.crcHex}",
            width / 2f,
            height - 115f,
            footerPaint
        )

        return bitmap
    }

    /**
     * Lưu ảnh thẻ VietQR vào thư viện ảnh trên điện thoại (MediaStore, không cần xin quyền bộ nhớ)
     */
    suspend fun saveQrPosterToGallery(context: Context, session: ActiveQrSession): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val posterBitmap = renderPaymentPosterBitmap(session)
                val fileName = "VietQR_${session.bankShortName}_${session.amountVnd}_${System.currentTimeMillis()}.png"
                val resolver = context.contentResolver

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES + "/SmartQRPayment"
                        )
                        put(MediaStore.MediaColumns.IS_PENDING, 1)
                    }
                }

                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IllegalStateException("Không thể tạo tệp ảnh trong thư viện MediaStore.")

                resolver.openOutputStream(imageUri)?.use { outputStream ->
                    if (!posterBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                        throw IllegalStateException("Không thể nén ảnh PNG.")
                    }
                } ?: throw IllegalStateException("Không thể mở luồng ghi ảnh.")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                }

                fileName
            }
        }

    /**
     * Chia sẻ ảnh mã QR qua các ứng dụng (Zalo, Messenger, Gmail, Bluetooth...) bằng FileProvider
     */
    suspend fun shareQrPosterImage(context: Context, session: ActiveQrSession): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val posterBitmap = renderPaymentPosterBitmap(session)
                val shareDir = File(context.cacheDir, "qr_shares").apply { mkdirs() }
                val file = File(shareDir, "VietQR_${session.amountVnd}.png")
                FileOutputStream(file).use { out ->
                    posterBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                val authority = "${context.packageName}.fileprovider"
                val contentUri = FileProvider.getUriForFile(context, authority, file)

                val shareText = buildString {
                    appendLine("Mã thanh toán VietQR - ${session.bankShortName}")
                    appendLine("Chủ TK: ${session.accountHolder}")
                    appendLine("Số TK: ${session.accountNumber}")
                    appendLine("Số tiền: ${CurrencyUtils.formatVnd(session.amountVnd)}")
                    if (session.transferMessage.isNotBlank()) {
                        appendLine("Nội dung: ${session.transferMessage}")
                    }
                }

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(shareIntent, "Chia sẻ mã VietQR").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                withContext(Dispatchers.Main) {
                    context.startActivity(chooser)
                }
            }
        }
}
