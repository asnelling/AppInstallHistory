package io.asnell.appinstallhistory

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PackageListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val labelView: TextView = view.findViewById(R.id.labelView)
    val updatedView: TextView = view.findViewById(R.id.updatedView)
    val iconView: ImageView = view.findViewById(R.id.iconView)
}
