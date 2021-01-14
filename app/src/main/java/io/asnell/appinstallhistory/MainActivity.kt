package io.asnell.appinstallhistory

import android.content.Intent
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var listAdapter: PackageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val packages: MutableList<Package> = mutableListOf()
        for (p in packageManager.getInstalledPackages(0)) {
            var label = p.packageName
            var icon: Drawable? = null
            if (p.applicationInfo != null) {
                label = packageManager.getApplicationLabel(p.applicationInfo)
                    .toString()
                icon = packageManager.getApplicationIcon(p.applicationInfo)
            }
            packages.add(Package(label, p.packageName, p.lastUpdateTime, icon))
        }

        packages.sortByDescending { it.lastUpdated }

        val clickListener = View.OnClickListener { view ->
            val item = view.tag as Package
            val uri = Uri.Builder()
                .scheme("package")
                .opaquePart(item.name)
                .build()
            val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS, uri)
            val activities = packageManager
                .queryIntentActivities(intent, MATCH_DEFAULT_ONLY)
            val isIntentSafe = activities.isNotEmpty()
            if (isIntentSafe) {
                startActivity(intent)
            } else {
                Log.w(TAG, "No details activity for package: ${item.name}")
                Toast.makeText(
                    this,
                    R.string.unable_open_details,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        listAdapter = PackageListAdapter(packages, clickListener)

        findViewById<RecyclerView>(R.id.packageList).apply {
            adapter = listAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.app_bar, menu)

        val searchView =
            menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                listAdapter.search(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listAdapter.search(newText!!)
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_sort_name -> {
            listAdapter.sortBy(SortBy.NAME)
            true
        }
        R.id.action_sort_last_updated -> {
            listAdapter.sortBy(SortBy.LAST_UPDATE)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}