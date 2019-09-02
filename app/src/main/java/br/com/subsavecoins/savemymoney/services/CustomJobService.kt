package br.com.subsavecoins.savemymoney.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.models.Search
import br.com.subsavecoins.savemymoney.network.Api
import com.google.gson.Gson
import java.lang.Exception
import java.net.URL

class CustomJobService : JobService() {

    private var mJobParameters: JobParameters? = null
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true
    }

    private val loader = @SuppressLint("StaticFieldLeak")
    object : AsyncTask<String, MutableList<Search>, MutableList<Search>>() {
        override fun doInBackground(vararg p0: String?): MutableList<Search>? {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
            val discounts = sharedPreferences.getStringSet("discounts", mutableSetOf()).iterator()
            val gson = Gson()
            val list = mutableListOf<Search>()
            while (discounts.hasNext()) {
                val d = discounts.next()
                val url = URL(Api.getUrl(Api.ApiSelection.OPEN_GAME, d))
                val openConnection = url.openConnection()
                try {
                    val stream = openConnection.getInputStream()
                    list.add(gson.fromJson(String(stream.readBytes()), Search::class.java))
                } catch (e: Exception) {
                }

                Thread.sleep(3000)
            }
            return list
        }

        override fun onPostExecute(result: MutableList<Search>?) {
            super.onPostExecute(result)
            sendNotification(result)
            jobFinished(mJobParameters, true)
        }
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        mJobParameters = p0
        loader.execute()
        return true
    }

    private fun sendNotification(notifications: MutableList<Search>?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var notificationChannel = notificationManager.getNotificationChannel("SubSaveCoinsJob")
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel("SubSaveCoinsJob", "Job",
                        NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableVibration(false)
                notificationChannel.setSound(null, null)
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        notifications?.forEach {

            if (it.data.price_info?.hasDiscount == true) {
                val notification = NotificationCompat.Builder(baseContext, "SubSaveCoinsJob")
                notification.setContentTitle(it.data?.title)
                notification.setContentText("De ${it.data?.price_info?.regularPrice?.regularPrice} por ${it.data?.price_info?.discountPrice?.discountPrice}")
                notification.priority = NotificationCompat.PRIORITY_MAX
                notification.setSmallIcon(R.drawable.ic_notifications_active_white_24dp)
                notificationManager.notify(if (it.data.id == -1) {
                    it.data.newId.toInt()
                } else {
                    it.data.id
                }, notification.build())
            }

            Thread.sleep(3000)
        }
    }
}