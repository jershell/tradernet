package ru.tradernet.core


data class AlertParams(
    val title: String = "",
    val description: String = "",
    val onOk: () -> Unit = {}
)