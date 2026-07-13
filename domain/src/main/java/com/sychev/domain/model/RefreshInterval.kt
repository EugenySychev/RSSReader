package com.sychev.domain.model

enum class RefreshInterval(val minutes: Long) {
    MIN_15(15),
    MIN_30(30),
    HOUR_1(60),
    HOUR_2(120),
    HOUR_6(360),
    HOUR_12(720),
    HOUR_24(1440),
}
