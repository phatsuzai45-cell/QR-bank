package com.example.domain

data class VietnameseBank(
    val bin: String,
    val shortName: String,
    val fullName: String,
    val swiftCode: String,
    val brandColorHex: Long
)

object VietnameseBanks {
    val supportedBanks: List<VietnameseBank> = listOf(
        VietnameseBank(
            bin = "970436",
            shortName = "Vietcombank",
            fullName = "Ngân hàng TMCP Ngoại thương Việt Nam",
            swiftCode = "BFTVVNVX",
            brandColorHex = 0xFF006B3F
        ),
        VietnameseBank(
            bin = "970407",
            shortName = "Techcombank",
            fullName = "Ngân hàng TMCP Kỹ thương Việt Nam",
            swiftCode = "VTCBVNVX",
            brandColorHex = 0xFFE31B23
        ),
        VietnameseBank(
            bin = "970422",
            shortName = "MB Bank",
            fullName = "Ngân hàng TMCP Quân đội",
            swiftCode = "MSCBVNVX",
            brandColorHex = 0xFF141ED2
        ),
        VietnameseBank(
            bin = "970415",
            shortName = "VietinBank",
            fullName = "Ngân hàng TMCP Công thương Việt Nam",
            swiftCode = "ICBVVNVX",
            brandColorHex = 0xFF005993
        ),
        VietnameseBank(
            bin = "970418",
            shortName = "BIDV",
            fullName = "Ngân hàng TMCP Đầu tư và Phát triển Việt Nam",
            swiftCode = "BIDVVNVX",
            brandColorHex = 0xFF006B68
        ),
        VietnameseBank(
            bin = "970405",
            shortName = "Agribank",
            fullName = "Ngân hàng Nông nghiệp và Phát triển Nông thôn VN",
            swiftCode = "VBAAVNVX",
            brandColorHex = 0xFFA61C3C
        ),
        VietnameseBank(
            bin = "970416",
            shortName = "ACB",
            fullName = "Ngân hàng TMCP Á Châu",
            swiftCode = "ASCBVNVX",
            brandColorHex = 0xFF004B9B
        ),
        VietnameseBank(
            bin = "970432",
            shortName = "VPBank",
            fullName = "Ngân hàng TMCP Việt Nam Thịnh Vượng",
            swiftCode = "VPBKVNVX",
            brandColorHex = 0xFF009B4D
        ),
        VietnameseBank(
            bin = "970423",
            shortName = "TPBank",
            fullName = "Ngân hàng TMCP Tiên Phong",
            swiftCode = "TPBVVNVX",
            brandColorHex = 0xFF652D86
        ),
        VietnameseBank(
            bin = "970403",
            shortName = "Sacombank",
            fullName = "Ngân hàng TMCP Sài Gòn Thương Tín",
            swiftCode = "SGTTVNVX",
            brandColorHex = 0xFF0056A4
        ),
        VietnameseBank(
            bin = "970437",
            shortName = "HDBank",
            fullName = "Ngân hàng TMCP Phát triển TP.HCM",
            swiftCode = "HDBCVNVX",
            brandColorHex = 0xFFDA2128
        ),
        VietnameseBank(
            bin = "970441",
            shortName = "VIB",
            fullName = "Ngân hàng TMCP Quốc tế Việt Nam",
            swiftCode = "VNIBVNVX",
            brandColorHex = 0xFF0066B3
        ),
        VietnameseBank(
            bin = "970443",
            shortName = "SHB",
            fullName = "Ngân hàng TMCP Sài Gòn - Hà Nội",
            swiftCode = "SHBAVNVX",
            brandColorHex = 0xFFF37021
        ),
        VietnameseBank(
            bin = "970440",
            shortName = "SeABank",
            fullName = "Ngân hàng TMCP Đông Nam Á",
            swiftCode = "SEAVVNVX",
            brandColorHex = 0xFFD71920
        ),
        VietnameseBank(
            bin = "970426",
            shortName = "MSB",
            fullName = "Ngân hàng TMCP Hàng Hải Việt Nam",
            swiftCode = "MCOBVNVX",
            brandColorHex = 0xFFF05A22
        ),
        VietnameseBank(
            bin = "970448",
            shortName = "OCB",
            fullName = "Ngân hàng TMCP Phương Đông",
            swiftCode = "ORCOVNVX",
            brandColorHex = 0xFF007A33
        ),
        VietnameseBank(
            bin = "970449",
            shortName = "LPBank",
            fullName = "Ngân hàng TMCP Lộc Phát Việt Nam",
            swiftCode = "LVBKVNVX",
            brandColorHex = 0xFF542E1B
        ),
        VietnameseBank(
            bin = "970431",
            shortName = "Eximbank",
            fullName = "Ngân hàng TMCP Xuất Nhập khẩu Việt Nam",
            swiftCode = "EBVIVNVX",
            brandColorHex = 0xFF0088CE
        ),
        VietnameseBank(
            bin = "970428",
            shortName = "Nam A Bank",
            fullName = "Ngân hàng TMCP Nam Á",
            swiftCode = "NAMAVNVX",
            brandColorHex = 0xFF00529B
        ),
        VietnameseBank(
            bin = "970414",
            shortName = "OceanBank / MBV",
            fullName = "Ngân hàng TNHH MTV Việt Nam Hiện Đại",
            swiftCode = "OJBAVNVX",
            brandColorHex = 0xFF0066B3
        ),
        VietnameseBank(
            bin = "970412",
            shortName = "PVcomBank",
            fullName = "Ngân hàng TMCP Đại Chúng Việt Nam",
            swiftCode = "WBVNVNVX",
            brandColorHex = 0xFF005A9C
        ),
        VietnameseBank(
            bin = "970429",
            shortName = "SCB",
            fullName = "Ngân hàng TMCP Sài Gòn",
            swiftCode = "SACLVNVX",
            brandColorHex = 0xFF004C97
        ),
        VietnameseBank(
            bin = "970425",
            shortName = "ABBank",
            fullName = "Ngân hàng TMCP An Bình",
            swiftCode = "ABBKVNVX",
            brandColorHex = 0xFF009B90
        ),
        VietnameseBank(
            bin = "970409",
            shortName = "Bac A Bank",
            fullName = "Ngân hàng TMCP Bắc Á",
            swiftCode = "NASCVNVX",
            brandColorHex = 0xFF6B7732
        ),
        VietnameseBank(
            bin = "970454",
            shortName = "BVBank (Timo)",
            fullName = "Ngân hàng TMCP Bản Việt / Timo Digital Bank",
            swiftCode = "VCBCVNVX",
            brandColorHex = 0xFF4F2D7F
        ),
        VietnameseBank(
            bin = "546034",
            shortName = "CAKE by VPBank",
            fullName = "Ngân hàng số CAKE by VPBank",
            swiftCode = "VPBKVNVX",
            brandColorHex = 0xFFE91E63
        ),
        VietnameseBank(
            bin = "546035",
            shortName = "Ubank by VPBank",
            fullName = "Ngân hàng số Ubank by VPBank",
            swiftCode = "VPBKVNVX",
            brandColorHex = 0xFFFF6F00
        ),
        VietnameseBank(
            bin = "970433",
            shortName = "VietBank",
            fullName = "Ngân hàng TMCP Việt Nam Thương Tín",
            swiftCode = "VNTTVNVX",
            brandColorHex = 0xFF005596
        ),
        VietnameseBank(
            bin = "970427",
            shortName = "VietABank",
            fullName = "Ngân hàng TMCP Việt Á",
            swiftCode = "VNACVNVX",
            brandColorHex = 0xFF004A8F
        ),
        VietnameseBank(
            bin = "970452",
            shortName = "KienlongBank",
            fullName = "Ngân hàng TMCP Kiên Long",
            swiftCode = "KLBKVNVX",
            brandColorHex = 0xFF0072BC
        ),
        VietnameseBank(
            bin = "970419",
            shortName = "NCB",
            fullName = "Ngân hàng TMCP Quốc Dân",
            swiftCode = "NVBAVNVX",
            brandColorHex = 0xFF0054A6
        ),
        VietnameseBank(
            bin = "970438",
            shortName = "BaoViet Bank",
            fullName = "Ngân hàng TMCP Bảo Việt",
            swiftCode = "BVBVVNVX",
            brandColorHex = 0xFF0066B3
        ),
        VietnameseBank(
            bin = "970430",
            shortName = "PGBank",
            fullName = "Ngân hàng TMCP Thịnh vượng và Phát triển",
            swiftCode = "PGBLVNVX",
            brandColorHex = 0xFF00529C
        ),
        VietnameseBank(
            bin = "970400",
            shortName = "SaigonBank",
            fullName = "Ngân hàng TMCP Sài Gòn Công Thương",
            swiftCode = "SBITVNVX",
            brandColorHex = 0xFF0066B3
        ),
        VietnameseBank(
            bin = "970457",
            shortName = "Woori Bank VN",
            fullName = "Ngân hàng TNHH MTV Woori Việt Nam",
            swiftCode = "HVBKVNVX",
            brandColorHex = 0xFF0067AC
        ),
        VietnameseBank(
            bin = "970424",
            shortName = "Shinhan Bank",
            fullName = "Ngân hàng TNHH MTV Shinhan Việt Nam",
            swiftCode = "SHBKVNVX",
            brandColorHex = 0xFF0046FF
        ),
        VietnameseBank(
            bin = "971005",
            shortName = "Viettel Money",
            fullName = "Hệ sinh thái tài chính số Viettel Money",
            swiftCode = "VTMNVNVX",
            brandColorHex = 0xFFEE0033
        ),
        VietnameseBank(
            bin = "971011",
            shortName = "VNPT Money",
            fullName = "Trung tâm Dịch vụ Tài chính số VNPT",
            swiftCode = "VNPTVNVX",
            brandColorHex = 0xFF0066CC
        )
    )

    fun findByBin(bin: String): VietnameseBank? {
        return supportedBanks.firstOrNull { it.bin == bin }
    }

    fun search(query: String): List<VietnameseBank> {
        val trimmed = query.trim().lowercase()
        if (trimmed.isEmpty()) return supportedBanks
        return supportedBanks.filter {
            it.shortName.lowercase().contains(trimmed) ||
                it.fullName.lowercase().contains(trimmed) ||
                it.bin.contains(trimmed)
        }
    }
}
