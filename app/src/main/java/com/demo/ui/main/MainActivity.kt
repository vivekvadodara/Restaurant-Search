package com.demo.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.demo.R
import com.demo.databinding.ActivityMainBinding
import com.demo.model.Restaurant
import com.demo.ui.base.BaseActivity
import com.demo.ui.main.adapter.RestaurantListAdapter
import com.demo.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val mViewModel: MainViewModel by viewModels()


    private val mRestaurantAdapter = RestaurantListAdapter(this::onItemClicked)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        initView()
    }

    override fun onStart() {
        super.onStart()
        observeRestaurants()
    }

    private fun observeSearchedRestaurants() {
        lifecycleScope.launchWhenStarted {
            mViewModel.searchedRestaurants.collect { data ->
                when {
                    data.isNotEmpty() -> {
                        val adapterData: MutableList<Restaurant> = mutableListOf()
                        data.entries.forEach {
                            val key = it.key
                            adapterData.addAll(it.value.map { it.copy(match = "Matched: $key") })
                        }
                        mRestaurantAdapter.submitList(adapterData)
                        showLoading(false)
                    }
                    else -> {
                        showToast("No search result found")
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun observeMenuForRest() {
        lifecycleScope.launch {
            mViewModel.menuForRest.collect { data ->
                when {
                    data != null -> {
                        val builder = AlertDialog.Builder(this@MainActivity)
                        builder.setTitle("Menu Details")
                        builder.setMessage(data.toString())
                        builder.setPositiveButton("OK") { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        val alertDialog: AlertDialog = builder.create()
                        alertDialog.setCancelable(true)
                        alertDialog.show()

                        showLoading(false)
                    }
                    else -> {
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun initView() {
        mViewBinding.run {
            recyclerView.adapter = mRestaurantAdapter
            getRestaurants()

            swipeRefreshLayout.setOnRefreshListener {
                mViewBinding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun observeRestaurants() {
        lifecycleScope.launchWhenStarted {
            mViewModel.restaurants.collect { data ->
                if (data.isNotEmpty()) {
                    mRestaurantAdapter.submitList(data.toMutableList())
                    showLoading(false)
                } else {
                    showLoading(false)
                }
            }
        }
    }

    private fun getRestaurants() {
        mRestaurantAdapter.submitList(emptyList())
        mViewModel.getRestaurants()
    }

    private fun showLoading(isLoading: Boolean) {
        mViewBinding.swipeRefreshLayout.isRefreshing = isLoading
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                showAlertDialog()
                true
            }
            R.id.action_show_all -> {
                getRestaurants()
                true
            }

            else -> true
        }
    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(this@MainActivity)
        val customLayout: View = layoutInflater.inflate(R.layout.custom, null)
        alertDialog.setView(customLayout)
        alertDialog.setPositiveButton(
            "SEARCH"
        ) { dialog, which ->
            val editText: EditText = customLayout.findViewById(R.id.searchEditText)
            val text = editText.text.toString()
            if (text.isEmpty() || text.isBlank() || text.length < 3) {
                showToast("search with at least 3 characters")
            } else {
                search(text)
            }

        }
        val alert = alertDialog.create()
        alert.setCanceledOnTouchOutside(true)
        alert.show()
    }

    private fun search(text: String) {
        mRestaurantAdapter.submitList(emptyList())
        mViewModel.getSearchResult(text)
        observeSearchedRestaurants()
    }

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)


    private fun onItemClicked(restaurant: Restaurant, imageView: ImageView) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            imageView,
            imageView.transitionName
        )

        mViewModel.getMenus(restaurant.id)
        observeMenuForRest()
    }
}
