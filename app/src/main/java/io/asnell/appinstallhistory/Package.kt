package io.asnell.appinstallhistory

import android.graphics.drawable.Drawable

data class Package(val label: String, val name: String, val lastUpdated: Long, val icon: Drawable?)
