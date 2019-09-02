package br.com.subsavecoins.savemymoney.activities

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.adapters.FilterAdapter

import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.content_filter.*
import android.view.animation.AccelerateInterpolator
import android.view.ViewAnimationUtils
import android.view.View


class FilterActivity : AppCompatActivity() {

    private val mAdater = FilterAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        setSupportActionBar(toolbar)
        toolbar.transitionName = getString(R.string.transition_view)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initViews()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId

        return when (id) {
            android.R.id.home -> {
                finishAfterTransition()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initViews() {
        val flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window?.decorView?.systemUiVisibility = flags
        window?.clearFlags(flags)

        window?.statusBarColor = resources.getColor(R.color.colorAccentDark)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        recyclerview.adapter = mAdater

        mAdater.addFilterItems(getFilterItems())
        mAdater.notifyDataSetChanged()

    }

    private fun getFilterItems(): MutableList<String> {
        val sharedPreferences = getSharedPreferences("Filter", Context.MODE_PRIVATE)
        val categories = sharedPreferences.getStringSet("filter_categorie", mutableSetOf())
//        val languages = sharedPreferences.getStringSet("filter_language", mutableSetOf())
        return categories.toMutableList()
    }

    private fun animateRevealShow(viewRoot: View) {
        val cx = (viewRoot.left + viewRoot.right) / 2
        val cy = (viewRoot.top + viewRoot.bottom) / 2
        val finalRadius = Math.max(viewRoot.width, viewRoot.height)

        val anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0f, finalRadius.toFloat())
        viewRoot.visibility = View.VISIBLE
        anim.duration = 1000
        anim.interpolator = AccelerateInterpolator()
        anim.start()
    }

}
