package br.com.subsavecoins.savemymoney.activities

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.adapters.GameAdapter
import br.com.subsavecoins.savemymoney.adapters.SpotlightAdapter
import br.com.subsavecoins.savemymoney.extensions.hideByAnim
import br.com.subsavecoins.savemymoney.extensions.showByAnim
import br.com.subsavecoins.savemymoney.interfaces.EndlessRecyclerViewScrollListener
import br.com.subsavecoins.savemymoney.models.GamedataModel
import br.com.subsavecoins.savemymoney.network.GameLoadAsyncTask
import br.com.subsavecoins.savemymoney.network.GameLoadCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_more.*
import kotlinx.android.synthetic.main.fragment_content.*

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
        setTitle()
        initUrl()
        initRecyclerview()
    }

    private fun initUrl() {
        mUrl = intent.getStringExtra("url")
    }

    private fun initRecyclerview() {
        mAdapter = GameAdapter(this)
        mAdapter.setHasStableIds(true)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        recyclerview.adapter = mAdapter

        recyclerView.addOnScrollListener(object : EndlessRecyclerViewScrollListener(recyclerview.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                initLoad()
            }
        })

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
}