package br.com.subsavecoins.savemymoney.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.activities.DetailActivity
import br.com.subsavecoins.savemymoney.activities.MainActivity
import br.com.subsavecoins.savemymoney.models.Data
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
    object : AsyncTask<String, Search?, MutableList<Search>>() {
        override fun doInBackground(vararg p0: String?): MutableList<Search>? {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseContext)
            val discounts = sharedPreferences.getStringSet("discounts", mutableSetOf())
            if (discounts.size == 0) {
                jobFinished(mJobParameters, false)
                return null
            }
            val values = discounts.iterator()
            val gson = Gson()
            val list = mutableListOf<Search>()
            while (values.hasNext()) {
                val d = values.next()
                val url = URL(Api.getUrl(Api.ApiSelection.OPEN_GAME, d))
                val openConnection = url.openConnection()
                try {
                    val stream = openConnection.getInputStream()
                    list.add(gson.fromJson(String(stream.readBytes()), Search::class.java))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                Thread.sleep(3000)
            }

            list.forEach {
                if (it.data.price_info?.hasDiscount == true) {
                    publishProgress(it)
                }
            }

            return list
        }

        override fun onProgressUpdate(vararg values: Search?) {
            super.onProgressUpdate(*values)
            sendNotification(values[0])
            jobFinished(mJobParameters, true)
        }
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        mJobParameters = p0
        loader.execute()
        return true
    }

    private fun sendNotification(notification: Search?) {
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

        notification?.let {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val bundle = Bundle()
            bundle.putParcelable("notify", it.data)
            intent.putExtras(bundle)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT)
            val n = NotificationCompat.Builder(baseContext, "SubSaveCoinsJob")
            n.setContentTitle(it.data?.title)
            n.setContentText("De ${it.data?.price_info?.regularPrice?.regularPrice} por ${it.data?.price_info?.discountPrice?.discountPrice}")
            n.priority = NotificationCompat.PRIORITY_MAX
            n.setSmallIcon(R.drawable.ic_notifications_active_white_24dp)
            n.setContentIntent(pendingIntent)
            notificationManager.notify(if (it.data.id == -1) {
                it.data.newId.toInt()
            } else {
                it.data.id
            }, n.build())
        }
    }
}