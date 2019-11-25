package br.com.subsavecoins.savemymoney.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.adapters.GameAdapter
import br.com.subsavecoins.savemymoney.interfaces.EndlessRecyclerViewScrollListener
import br.com.subsavecoins.savemymoney.models.GamedataModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.content_more.*
import java.net.URL

class MoreActivity : AppCompatActivity(), GameLoadCallback {

    private lateinit var mAdapter: GameAdapter
    private var mUrl = ""
    var mGameLoadAsyncTask: GameLoadAsyncTask? = null

    companion object {
        const val ON_SALE_TYPE = 0
        const val RECENT_RELEACE_TYPE = 1
        const val COMING_SOON_TYPE = 2
        const val PRE_SALE_TYPE = 3
    }

    override fun onLoadResult(games: GamedataModel?) {
        refreshLayout.isRefreshing = false
        refreshLayout.isEnabled = false

        if (games == null) {
            return
        }

        mAdapter.addModel(games)

        mGameLoadAsyncTask = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle()
        initUrl()
        initViews()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val itemId = item?.itemId

        if (itemId == android.R.id.home) {
            finishAfterTransition()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initUrl() {
        mUrl = intent.getStringExtra("url")
    }

    private fun initViews() {
        val flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window?.decorView?.systemUiVisibility = flags

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window?.statusBarColor = resources.getColor(R.color.dividerColor);
        }else {
            window?.statusBarColor = resources.getColor(android.R.color.white)
        }
        mAdapter = GameAdapter(this)
        mAdapter.setHasStableIds(true)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        recyclerview.adapter = mAdapter

        recyclerview.addOnScrollListener(object : EndlessRecyclerViewScrollListener(recyclerview.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                initLoad()
            }
        })

        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        refreshLayout.isEnabled = true
        refreshLayout.isRefreshing = true

        initLoad()
    }

    private fun setTitle() {
        val type = intent.getIntExtra("type", -1)
        when(type) {
            ON_SALE_TYPE -> {
                title = "Em Promoção"
            }
            RECENT_RELEACE_TYPE -> {
                title = "Recém-lançados"
            }
            COMING_SOON_TYPE -> {
                title = "Em Breve"
            }
            PRE_SALE_TYPE -> {
                title = "Pré-venda"
            }
        }
    }

    private fun initLoad() {
        mGameLoadAsyncTask = GameLoadAsyncTask(getSharedPreferences("Platform", Context.MODE_PRIVATE))
        mGameLoadAsyncTask?.mCallback = this
        mGameLoadAsyncTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, if (mUrl.isNullOrEmpty()) {
            mAdapter.lastModel()?.links?.next
        }else {
            val nowUrl = mUrl
            mUrl = ""
            nowUrl
        })
    }

   @SuppressLint("StaticFieldLeak")
   inner open class GameLoadAsyncTask (var sharedPreferences: SharedPreferences) : AsyncTask<String, GamedataModel, GamedataModel>() {

        var mCallback: GameLoadCallback? = null

        override fun doInBackground(vararg p0: String?): GamedataModel? {
            val gson = Gson()
            var gamedataModel: GamedataModel? = null
            try {
                val url = URL(p0[0])
                val urlConnection = url.openConnection()
                val input = urlConnection?.getInputStream()
                gamedataModel = gson.fromJson(String(input!!.readBytes()), GamedataModel::class.java)
            } catch (e: Exception) {
                print("TESTE")
            } finally {
                return gamedataModel
            }
        }

        override fun onPostExecute(result: GamedataModel?) {
            super.onPostExecute(result)
            mCallback?.onLoadResult(result)
        }

    }
}

interface GameLoadCallback {
    fun onLoadResult(games: GamedataModel?)
}