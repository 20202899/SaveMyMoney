package br.com.subsavecoins.savemymoney.activities

import android.app.ActivityOptions
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Pair
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.adapters.CustomFragmentPageAdapter
import br.com.subsavecoins.savemymoney.adapters.GameAdapter
import br.com.subsavecoins.savemymoney.extensions.hideByAnim
import br.com.subsavecoins.savemymoney.extensions.showByAnim
import br.com.subsavecoins.savemymoney.fragments.ContentFragment
import br.com.subsavecoins.savemymoney.fragments.SpotlightFragment
import br.com.subsavecoins.savemymoney.models.Data
import br.com.subsavecoins.savemymoney.models.GamedataModel
import br.com.subsavecoins.savemymoney.network.Api
import br.com.subsavecoins.savemymoney.network.GameLoadAsyncTask
import br.com.subsavecoins.savemymoney.network.GameLoadCallback
import br.com.subsavecoins.savemymoney.services.CustomJobService

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_content.*

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener, SearchView.OnQueryTextListener, GameLoadCallback {
    override fun onLoadResult(games: GamedataModel?) {
        val fragment = customFragmentPageAdapter.getItem(1) as ContentFragment
        fragment.refreshLayout?.isRefreshing = false
        fragment.refreshLayout?.isEnabled = false
        fragment.search(games)
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        val sharedPreferences = getSharedPreferences("Platform",
                Context.MODE_PRIVATE)

        val refreshLayout = (customFragmentPageAdapter.getItem(1) as ContentFragment?)?.refreshLayout
        refreshLayout?.isEnabled = true
        refreshLayout?.isRefreshing = true

        GameLoadAsyncTask(sharedPreferences).apply {
            mCallback = null
            mCallback = this@MainActivity
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    Api.getUrl(Api.ApiSelection.SEARCH_GAME, p0,
                            sharedPreferences.getString("platform", "")))
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        return true
    }

    private val customFragmentPageAdapter = CustomFragmentPageAdapter(supportFragmentManager)

    private lateinit var mSearchView: SearchView
    private val mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupAppbarAnimation()
        title = ""
        showPlatform()
        initViews()
        initNotify()
        registreDiscountAlarmManager()
    }

    private fun initNotify() {
        val data = intent.getParcelableExtra<Data>("notify")
        if (data != null) {
            mHandler.postDelayed({startActivity(Intent(this, DetailActivity::class.java).apply {
                putExtra("data", data)
            })}, 600)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val menuItem = menu.findItem(R.id.search)
        mSearchView = menuItem.actionView as SearchView
        mSearchView.queryHint = "Nome do game..."
        mSearchView.maxWidth = Int.MAX_VALUE
        mSearchView.setOnQueryTextListener (this)
        mSearchView.setOnCloseListener {
            val fragment = customFragmentPageAdapter.getItem(1) as ContentFragment?
            fragment?.listReturn()
            return@setOnCloseListener false
        }

        mSearchView.setOnSearchClickListener {
            viewPager.currentItem = 1
        }

        return true
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {

        if (position == SpotlightFragment.SPOTLIGHT_TYPE) {
            fab.hideByAnim()
        } else {
            fab.showByAnim()
        }
    }

    private fun showPlatform() {

        if (!getSharedPreferences("Platform", Context.MODE_PRIVATE)
                        .getString("platform", "").isNullOrEmpty()) {
            initTabLayoutViewPager()
            return
        }

        val items = arrayOf("Nintendo", "Xbox One")
        AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Selecione sua plataforma")
                .setItems(items) { dialog, which ->
                    getSharedPreferences("Platform", Context.MODE_PRIVATE)
                            .edit()
                            .putString("platform", items[which].toLowerCase())
                            .apply()
                    initTabLayoutViewPager()

                }.show()
    }

    private fun initTabLayoutViewPager() {
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = customFragmentPageAdapter
        tabLayout.setupWithViewPager(viewPager)
        customFragmentPageAdapter.setItems(mutableListOf(SpotlightFragment.newInstance(),
                ContentFragment.newInstance(0)))
        viewPager.addOnPageChangeListener(this)
    }

    private fun setupAppbarAnimation() {
        app_bar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_fab_appear))
        app_bar.visibility = View.VISIBLE
    }

    private fun initViews() {
        val flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window?.decorView?.systemUiVisibility = flags

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window?.statusBarColor = resources.getColor(R.color.dividerColor);
        }else {
            window?.statusBarColor = resources.getColor(android.R.color.white)
        }

        fab.setOnClickListener {
            val intent = Intent(this, FilterActivity::class.java)
            val pair = Pair.create(it, getString(R.string.transition_view))
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, pair)
                    .toBundle())
        }
    }

    private fun registreDiscountAlarmManager() {
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val job = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobScheduler.getPendingJob(1)
        } else {
            jobScheduler.allPendingJobs.find { it.id == 10 }
        }

        if (job == null) {
            val componentName = ComponentName(packageName, CustomJobService::class.java.name)
            val jobinfoBuilder = JobInfo.Builder(10, componentName)
            jobinfoBuilder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//            jobinfoBuilder.setBackoffCriteria(1800000, JobInfo.BACKOFF_POLICY_LINEAR)
            jobinfoBuilder.setOverrideDeadline(1800000)
            jobinfoBuilder.setMinimumLatency(1800000)
            jobinfoBuilder.setRequiresBatteryNotLow(true)
            jobinfoBuilder.setPersisted(true)
            jobScheduler.schedule(jobinfoBuilder.build())
        }
    }

    override fun onBackPressed() {

        if (viewPager.currentItem == ContentFragment.CONTENT_TYPE) {
            viewPager.currentItem = SpotlightFragment.SPOTLIGHT_TYPE
        }else {
            super.onBackPressed()
        }
    }
}
