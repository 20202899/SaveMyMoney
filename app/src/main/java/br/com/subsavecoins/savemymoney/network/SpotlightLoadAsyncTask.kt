package br.com.subsavecoins.savemymoney.network

import android.content.SharedPreferences
import android.os.AsyncTask
import android.text.TextUtils
import br.com.subsavecoins.savemymoney.models.SpotlightModel
import com.google.gson.Gson
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.UnknownHostException

class SpotlightLoadAsyncTask(var sharedPreferences: SharedPreferences) : AsyncTask<String, SpotlightModel, SpotlightModel>() {

    var mCallback: SpotlightLoadCallback? = null

    override fun doInBackground(vararg p0: String?): SpotlightModel? {
        val gson = Gson()
        val url = if (!TextUtils.isEmpty(p0[0]) || p0[0] != null) {
            URL(p0[0])
        } else {
            URL(Api.getUrl(Api.ApiSelection.GAMES, null, sharedPreferences.getString("platform", "")))
        }

        val urlConnection = url.openConnection()
        var gamedataModel: SpotlightModel? = null
        try {
            val input = urlConnection?.getInputStream()
            val bufferedStream = BufferedInputStream(input)
            val inputStrem = BufferedReader(InputStreamReader(bufferedStream))

            gamedataModel = gson.fromJson(inputStrem.readLine(), SpotlightModel::class.java)
        } catch (e: UnknownHostException) {
        } finally {
            return gamedataModel
        }
    }

    override fun onPostExecute(result: SpotlightModel?) {
        super.onPostExecute(result)
        mCallback?.onLoadResult(result)
    }

}

interface SpotlightLoadCallback {
    fun onLoadResult(games: SpotlightModel?)
}