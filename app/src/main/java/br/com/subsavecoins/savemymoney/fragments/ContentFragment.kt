package br.com.subsavecoins.savemymoney.fragments


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.adapters.GameAdapter
import br.com.subsavecoins.savemymoney.models.GamedataModel
import kotlinx.android.synthetic.main.fragment_content.*
import br.com.subsavecoins.savemymoney.activities.MainActivity
import br.com.subsavecoins.savemymoney.extensions.hideByAnim
import br.com.subsavecoins.savemymoney.extensions.showByAnim
import br.com.subsavecoins.savemymoney.interfaces.EndlessRecyclerViewScrollListener
import br.com.subsavecoins.savemymoney.network.GameLoadAsyncTask
import br.com.subsavecoins.savemymoney.network.GameLoadCallback
import kotlinx.android.synthetic.main.activity_main.*


class ContentFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, GameLoadCallback {

    private var isFilterSaved = false
    private var isSearch = false

    override fun onLoadResult(games: GamedataModel?) {
        refreshLayout.isRefreshing = false
        refreshLayout.isEnabled = false

        if (games == null) {
            refreshLayout.isEnabled = true
            return
        }

        bundle.putSerializable("gamedataModel", games)
        gameAdapter?.addModel(games)

        if (!isFilterSaved) {

            val filterSetCategorie = HashSet<String>(games.filters?.categories?.map { it.name })
            val filterSetLanguage = HashSet<String>(games.filters?.languages?.map { it.name })

            activity?.getSharedPreferences("Filter", Context.MODE_PRIVATE)
                    ?.edit()
                    ?.putStringSet("filter_categorie", filterSetCategorie)
                    ?.apply()

            activity?.getSharedPreferences("Filter", Context.MODE_PRIVATE)
                    ?.edit()
                    ?.putStringSet("filter_language", filterSetLanguage)
                    ?.apply()

            isFilterSaved = !isFilterSaved
        }

        mGameLoadAsyncTask = null
    }

    override fun onRefresh() {
        initLoad()
    }

    var gameAdapter: GameAdapter? = null
    var mGameLoadAsyncTask: GameLoadAsyncTask? = null
    private val bundle = Bundle()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameAdapter = GameAdapter(activity as AppCompatActivity)
        gameAdapter?.setHasStableIds(true)
        refreshLayout?.isRefreshing = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        bundle.putSerializable("gamedataModel", gameAdapter?.lastModel())
        super.onSaveInstanceState(bundle)
    }

    private fun initViews() {
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = linearLayoutManager

        recyclerView.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (!isSearch)
                    initLoad()
            }

            override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(view, dx, dy)
                if (activity is MainActivity) {
                    val activity = activity as MainActivity
                    if (activity.fab.visibility == View.VISIBLE && dy > 0) {
                        activity.fab.hideByAnim()
                    }else if (dy < 0 && activity.fab.visibility != View.VISIBLE) {
                        activity.fab.showByAnim()
                    }
                }
            }
        })

        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = gameAdapter

        (recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

        refreshLayout.setOnRefreshListener(this)
        refreshLayout.isEnabled = true
        refreshLayout.isRefreshing = true

        initLoad()
    }

    fun search(games: GamedataModel?) {
        isSearch = true
        gameAdapter?.isFilter = isSearch
        gameAdapter?.gamedataModel?.data?.clear()
        gameAdapter?.gamedataModel?.data?.addAll(games?.data!!)
        gameAdapter?.notifyDataSetChanged()
    }

    fun listReturn() {
        isSearch = false
        gameAdapter?.isFilter = isSearch
        gameAdapter?.gamedataModel?.data?.clear()
        gameAdapter?.reloadDefault()
    }

    private fun initLoad() {
        mGameLoadAsyncTask = GameLoadAsyncTask(activity!!.getSharedPreferences("Platform", Context.MODE_PRIVATE))
        mGameLoadAsyncTask?.mCallback = this
        mGameLoadAsyncTask?.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                gameAdapter!!.lastModel()?.links?.next)
    }

    companion object {

        val CONTEXT_FRAGMENT = "context"
        const val CONTENT_TYPE = 1

        fun newInstance(fragmentContext: Int): ContentFragment {
            val fragment = ContentFragment()
            val bundle = Bundle()
            bundle.putInt(CONTEXT_FRAGMENT, fragmentContext)
            fragment.arguments = bundle
            return fragment
        }
    }

}
