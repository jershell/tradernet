package ru.tradernet.core

data class DialogWrongParams (val reason: String = "", val onOk: () -> Unit = {})