package ru.tradernet.data.repository

import android.content.Context


class ResourceRepository (val appCtx: Context) {
    fun getString(stringId: Int): String {
        return appCtx.getString(stringId)
    }
}