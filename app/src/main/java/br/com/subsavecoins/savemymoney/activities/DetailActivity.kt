package br.com.subsavecoins.savemymoney.activities

import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.AppBarLayout
import android.support.transition.ChangeBounds
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.extensions.showByAnim
import br.com.subsavecoins.savemymoney.models.Data
import br.com.subsavecoins.savemymoney.models.Search
import br.com.subsavecoins.savemymoney.network.Api
import br.com.subsavecoins.savemymoney.utils.ResourseUtils
import com.android.volley.Response
import com.google.android.youtube.player.*
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URL

class DetailActivity : AppCompatActivity(), Response.Listener<Search?> {

    val mHandler = Handler()
    val mGson = Gson()
    var inExpanded = false

    override fun onResponse(response: Search?) {
        Log.DEBUG
        mData = response?.data
        if (mData != null) {
            val releaseDate = StringBuilder()
            val language = StringBuilder()
            val categorias = StringBuilder()
            val publicadoras = StringBuilder()
            demo_available.text = if (mData?.hasDemo == true) {
                "Sim"
            } else {
                "Não"
            }

            physical_media.text = if (mData?.retailRelease == true) {
                "Sim"
            } else {
                "Não"
            }

            mData?.releaseDates?.forEach {
                releaseDate.append("${it.region}: ${it.releaseDate} ")
            }

            mData?.languages?.forEach { r ->
                r.languages.forEach { l ->
                    language.append("${l.name} - ")
                }
            }

            mData?.categories?.forEach {
                categorias.append("${it} - ")
            }

            mData?.publishers?.forEach {
                publicadoras.append("${it.name} - ")
            }

            languages.text = language.toString().dropLast(3)
            release_date.text = releaseDate.toString()
            categories.text = categorias.toString().dropLast(3)
            publisher.text = publicadoras.dropLast(3)
            players.text = mData?.numberOfPlayers
            description.text = mData?.description
            best_price.text = mData?.price_info?.currentPrice
            country_price.text = mData?.price_info?.country?.name
            gold_points.text = mData?.price_info?.goldPoints.toString()
            status.text = mData?.price_info?.status

            list_info.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_fab_appear))
        }

        if (mData?.price_info?.hasDiscount == false) {

            if (verifySharedPreferences()) {
                fab.setImageResource(R.drawable.ic_notifications_active_white_24dp)
            } else {
                fab.setImageResource(R.drawable.ic_notifications_none_white_24dp)
            }

            Thread {
                mHandler.post { fab.showByAnim() }
            }.start()

            if (!mData?.youtubeId.isNullOrEmpty()) {
                initLoadYoutube()
            }
        }

        mInfoLoaderAsyncTask = null
    }


    private var mData: Data? = null
    private var mInfoLoaderAsyncTask: InfoLoaderAsyncTask? = null
    private var mYouTubePlayer: YouTubePlayer? = null
    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        supportPostponeEnterTransition()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mData = intent.getSerializableExtra("data") as Data
        loadImage()
//        setTitle()
        initView()
    }

    private fun initView() {
        val flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window?.decorView?.systemUiVisibility = flags

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            window?.statusBarColor = resources.getColor(R.color.dividerColor);
        } else {
            window?.statusBarColor = resources.getColor(android.R.color.white)
        }
        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
            val isTitleEnabled = i < ((i / 100) * 90) && i <= -200
            title = if (isTitleEnabled) {
                mData?.title
            } else {
                ""
            }
            toolbar_layout.isTitleEnabled = isTitleEnabled
        })

        fab.setOnClickListener {
            if (verifySharedPreferences()) {
                fab.setImageResource(R.drawable.ic_notifications_none_white_24dp)
                        .also(this::removeToAlert)
            } else {
                fab.setImageResource(R.drawable.ic_notifications_active_white_24dp)
                        .also(this::addToAlert)
            }
        }

        price_layout.setOnClickListener {
            expand()
        }

        unexpanded.setOnClickListener {
            unexpand()
        }

        //price_layout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse_animation))
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (inExpanded) {
            unexpand()
            return super.onOptionsItemSelected(item)
        }

        if (item == null)
            return super.onOptionsItemSelected(item)

        if (item.itemId == android.R.id.home) {
            youtube?.onDestroy()
            finishAfterTransition()
        }

        return when {
            item!!.itemId == R.id.action_desconto -> {
                super.onOptionsItemSelected(item)
            }
            item.itemId == R.id.action_lancamento -> {

                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        if (inExpanded) {
            unexpand()
        } else {
            if (isFullScreen) {
                mYouTubePlayer?.setFullscreen(false)
                isFullScreen = false
            } else {
                youtube?.onDestroy()
                super.onBackPressed()
            }
        }
    }

    private fun initDescription() {
//        desicription.text = mData?.description
    }

    private fun initLoadYoutube() {

        youtube_container.visibility = RelativeLayout.VISIBLE
        youtube_container.startAnimation(AnimationUtils.loadAnimation(this,
                R.anim.anim_fab_appear))

        val youtubeFragment = youtube as YouTubePlayerSupportFragment?

        if (mData?.youtubeId.isNullOrEmpty()) {
            youtube_container.visibility = View.GONE
            return
        }

        youtubeFragment?.initialize("AIzaSyAfcr0lyY94W5ekpN-M6AgmHBqdW4Tv2Ws",
                object : YouTubePlayer.OnInitializedListener {
                    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, p1: YouTubePlayer?,
                                                         p2: Boolean) {
                        mYouTubePlayer = p1

                        if (mData?.youtubeId.isNullOrEmpty()) {
                            youtube.onDestroy()
                        } else {
                            p1?.cueVideo(mData?.youtubeId)
                        }

                        p1?.setPlaybackEventListener(object : YouTubePlayer.PlaybackEventListener {
                            override fun onSeekTo(p0: Int) {

                            }

                            override fun onBuffering(p0: Boolean) {

                            }

                            override fun onPlaying() {
                                if (!isFullScreen)
                                    p1.setFullscreen(true)
                            }

                            override fun onStopped() {

                            }

                            override fun onPaused() {

                            }

                        })
                        p1?.setOnFullscreenListener {
                            isFullScreen = it
                        }
                    }

                    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {

                    }

                })
    }

    private fun loadImage() {
        Picasso.get()
                .load(mData?.imageUrl)
                .into(image, object : Callback {
                    override fun onSuccess() {
                        supportStartPostponedEnterTransition()
                        initLoaderInfo()
                    }

                    override fun onError(e: Exception?) {
                        supportStartPostponedEnterTransition()
                        initLoaderInfo()
                    }

                })
    }

    private fun initLoaderInfo() {
        if (mInfoLoaderAsyncTask == null) {
            mInfoLoaderAsyncTask = InfoLoaderAsyncTask()
            mInfoLoaderAsyncTask!!.mCallback = this
            mInfoLoaderAsyncTask!!.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                    Api.getUrl(Api.ApiSelection.OPEN_GAME, mData?.slug))
        }
    }

    private fun setTitle() {
        val sizeTitle = mData!!.title.length
        if (sizeTitle <= 23) {
            supportActionBar?.setSubtitle(mData?.title)
        } else {
            supportActionBar?.setSubtitle(mData?.title?.subSequence(0, 23))
        }
    }

    private fun addToAlert(v: Unit) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val discounts = sharedPreferences.getStringSet("discounts", mutableSetOf())
        sharedPreferences.edit().clear().apply()
        discounts.add(mData!!.slug)
        sharedPreferences.edit().putStringSet("discounts", discounts).apply()
        fab.setImageResource(R.drawable.ic_notifications_active_white_24dp)
    }

    private fun removeToAlert(v: Unit) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val discounts = sharedPreferences.getStringSet("discounts", mutableSetOf())
        sharedPreferences.edit().clear().apply()
        discounts.remove(mData?.slug)
        sharedPreferences.edit().putStringSet("discounts", discounts).apply()
        fab.setImageResource(R.drawable.ic_notifications_none_white_24dp)
    }

    private fun verifySharedPreferences(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val discounts = sharedPreferences.getStringSet("discounts", mutableSetOf())
        return discounts.contains(mData?.slug)
    }

    private fun expand() {
        if (inExpanded)
            return

        val changeBounds = ChangeBounds()
        changeBounds.duration = 600
        changeBounds.interpolator = OvershootInterpolator()

        changeBounds.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(p0: Transition) {

                unexpanded.startAnimation(AnimationUtils
                        .loadAnimation(this@DetailActivity, R.anim.anim_fab_appear))
                unexpanded.visibility = ImageView.VISIBLE

                info_price.visibility = RelativeLayout.VISIBLE
                info_price.startAnimation(AnimationUtils
                        .loadAnimation(this@DetailActivity, R.anim.anim_fab_appear))

                inExpanded = true
            }

            override fun onTransitionResume(p0: Transition) {
            }

            override fun onTransitionPause(p0: Transition) {
            }

            override fun onTransitionCancel(p0: Transition) {
            }

            override fun onTransitionStart(p0: Transition) {
//                price_layout.background = getDrawable(R.drawable.background_offer_expand)
            }

        })
        img_price.visibility = ImageView.INVISIBLE

        val params = price_layout.layoutParams as FrameLayout.LayoutParams
        val paramsListInfo = list_info.layoutParams as FrameLayout.LayoutParams
        params.height = ResourseUtils.dpToPx(200f, this@DetailActivity)
                .toInt()
        params.width = FrameLayout.LayoutParams.MATCH_PARENT
        params.topMargin = 0
        price_layout.layoutParams = params
        paramsListInfo.topMargin = ResourseUtils.dpToPx(216f, this@DetailActivity).toInt()

        list_info.layoutParams = paramsListInfo

        ViewCompat.setNestedScrollingEnabled(container, false)
        app_bar.setExpanded(false)

        TransitionManager.beginDelayedTransition(container, changeBounds)
    }

    private fun unexpand() {
        if (!inExpanded)
            return

        val changeBounds = ChangeBounds()
        val params = price_layout.layoutParams as FrameLayout.LayoutParams
        val paramsListInfo = list_info.layoutParams as FrameLayout.LayoutParams
        val size = ResourseUtils.dpToPx(50f, this@DetailActivity).toInt()
        changeBounds.duration = 450
        changeBounds.interpolator = OvershootInterpolator()

        changeBounds.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(p0: Transition) {
                inExpanded = false
            }

            override fun onTransitionResume(p0: Transition) {
            }

            override fun onTransitionPause(p0: Transition) {
            }

            override fun onTransitionCancel(p0: Transition) {
            }

            override fun onTransitionStart(p0: Transition) {

            }

        })

//        price_layout.background = getDrawable(R.drawable.background_offer)

        params.width = size
        params.height = size

        price_layout.layoutParams = params

        unexpanded.visibility = ImageView.GONE
        img_price.visibility = ImageView.VISIBLE
        info_price.visibility = RelativeLayout.GONE

        paramsListInfo.topMargin = ResourseUtils.dpToPx(16f, this@DetailActivity).toInt()

        list_info.layoutParams = paramsListInfo

        ViewCompat.setNestedScrollingEnabled(container, true)
        app_bar.setExpanded(true)

        TransitionManager.beginDelayedTransition(container, changeBounds)
    }

}

class InfoLoaderAsyncTask : AsyncTask<String, Search, Search>() {

    var mCallback: Response.Listener<Search?>? = null
    var data: Search? = null
    override fun doInBackground(vararg p0: String?): Search? {
        val url = URL(p0[0])
        val gson = Gson()
        val openConnection = url.openConnection()
        try {
            val stream = openConnection.getInputStream()
            val bufferedStream = BufferedInputStream(stream)
            val bufferedReader = BufferedReader(InputStreamReader(bufferedStream))
            val j = bufferedReader.readLine()
            data = gson.fromJson(j, Search::class.java)
        } catch (e: Exception) {

        } finally {
            return data
        }
    }

    override fun onPostExecute(result: Search?) {
        super.onPostExecute(result)
        mCallback?.onResponse(result)
    }
}
