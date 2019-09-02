package br.com.subsavecoins.savemymoney.network

import android.content.SharedPreferences
import android.os.AsyncTask
import android.text.TextUtils
import br.com.subsavecoins.savemymoney.models.GamedataModel
import com.google.gson.Gson
import java.net.URL

class GameLoadAsyncTask (var sharedPreferences: SharedPreferences) : AsyncTask<String, GamedataModel, GamedataModel>() {

    var mCallback: GameLoadCallback? = null

    override fun doInBackground(vararg p0: String?): GamedataModel? {
        val gson = Gson()
        val url = if (!TextUtils.isEmpty(p0[0]) || p0[0] != null) {
            URL(p0[0])
        } else {
            URL(Api.getUrl(Api.ApiSelection.GAMES, null, sharedPreferences.getString("platform", "")))
        }

        val urlConnection = url.openConnection()
        var gamedataModel: GamedataModel? = null
        try {
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

interface GameLoadCallback {
    fun onLoadResult(games: GamedataModel?)
}