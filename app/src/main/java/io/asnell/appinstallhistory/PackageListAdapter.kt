package io.asnell.appinstallhistory

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

enum class SortBy {
    NAME, LAST_UPDATE
}

class PackageListAdapter(
    private var packages: List<Package>,
    private val onClickListener: View.OnClickListener
) : RecyclerView.Adapter<PackageListViewHolder>() {

    private var listedPackages = packages
    private var searchQuery = ""
    private var orderBy = SortBy.LAST_UPDATE

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PackageListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_package, parent, false)
        return PackageListViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PackageListViewHolder,
        position: Int
    ) {
        val item = listedPackages[position]
        holder.labelView.text = item.label
        holder.iconView.setImageDrawable(item.icon)
        holder.updatedView.text = DateUtils.formatDateTime(
            holder.itemView.context,
            item.lastUpdated,
            DateUtils.FORMAT_SHOW_TIME
                    or DateUtils.FORMAT_SHOW_DATE
                    or DateUtils.FORMAT_NUMERIC_DATE
        )

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = listedPackages.size

    private fun refresh() {
        listedPackages = packages.filter { p ->
            p.label.contains(searchQuery, true)
        }
        listedPackages = when (orderBy) {
            SortBy.NAME -> listedPackages.sortedBy { it.label }
            SortBy.LAST_UPDATE -> listedPackages.sortedByDescending {
                it.lastUpdated
            }
        }
        notifyDataSetChanged()
    }

    fun sortBy(order: SortBy) {
        orderBy = order
        refresh()
    }

    fun search(query: String) {
        searchQuery = query
        refresh()
    }
}