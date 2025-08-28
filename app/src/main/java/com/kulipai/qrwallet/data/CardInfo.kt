package com.kulipai.qrwallet.data

data class CardInfo(
    val id: String,                // 唯一标识
    val title: String? = null,     // 卡片标题
    val description: String? = null, // 说明
    val content: String? = null,           // 卡片内容（如二维码数据）
    val color: String? = null      // 颜色（存 hex 字符串）
)
