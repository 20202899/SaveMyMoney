package br.com.subsavecoins.savemymoney.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.activities.MainActivity
import br.com.subsavecoins.savemymoney.adapters.SpotlightAdapter
import br.com.subsavecoins.savemymoney.models.SpotlightModel
import br.com.subsavecoins.savemymoney.network.Api
import br.com.subsavecoins.savemymoney.network.SpotlightLoadAsyncTask
import br.com.subsavecoins.savemymoney.network.SpotlightLoadCallback
import kotlinx.android.synthetic.main.fragment_spotlight.*

class SpotlightFragment : Fragment(), SpotlightLoadCallback {

    override fun onLoadResult(games: SpotlightModel?) {

        swiperefresh.isRefreshing = false
        swiperefresh.isEnabled = true

        if (games != null) {
            val list = mutableListOf<Any>()
            list.add("Em Promoção")
            list.addAll(games.data.onSale)
            list.add("Recém-lançados")
            list.addAll(games.data.recentReleases)
            list.add("Em Breve")
            list.addAll(games.data.comingSoon)
            list.add("Pré-venda")
            list.addAll(games.data.preorder)
            mAdaper.addAll(list)
        }

        mSpotlightLoadAsyncTask = null
    }

    private val mAdaper = SpotlightAdapter()
    private var mSpotlightLoadAsyncTask: SpotlightLoadAsyncTask? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_spotlight, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdaper.activity = activity as MainActivity

        swiperefresh.setOnRefreshListener {
            initLoader()
        }

        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false)
//        recyclerview.addItemDecoration(DividerItemDecoration(context,
//                DividerItemDecoration.VERTICAL))

        recyclerview.adapter = mAdaper

        swiperefresh.isEnabled = true
        swiperefresh.isRefreshing = true

        initLoader()
    }

    private fun initLoader() {
        if (mSpotlightLoadAsyncTask == null) {

            mSpotlightLoadAsyncTask = SpotlightLoadAsyncTask(activity!!.getSharedPreferences("Platform", Context.MODE_PRIVATE))
            mSpotlightLoadAsyncTask!!.mCallback = this
            mSpotlightLoadAsyncTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, Api.getUrl(Api.ApiSelection.SUMMARY))
        }
    }

    private fun getListOrdened() {

    }


    companion object {
        const val SPOTLIGHT_TYPE = 0
        fun newInstance() = SpotlightFragment()
    }

}