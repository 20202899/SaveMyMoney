package br.com.subsavecoins.savemymoney.network

/**
 * Created by carlossilva on 26/04/2018.
 */
class Api {

    enum class ApiSelection { CURRENCIES, GAMES, SUMMARY, OPEN_GAME, SEARCH_GAME }

    companion object {
        fun getUrl(selection: ApiSelection, a: String? = null, platfom: String = ""): String {
            val url = "https://api-savecoins.nznweb.com.br/v1/%s"
            val currencies = "currencies"
            val search = "games?currency=BRL&locale=pt&filter[platform]=" + if (platfom.isEmpty()) {
                "nintendo"
            } else {
                platfom
            } + "&filter[title]=$a"

            val games = "games?currency=BRL&locale=pt&filter[platform]=" + if (platfom.isEmpty()) {
                "nintendo"
            } else {
                platfom
            }
            val summary = "summary?currency=BRL&locale=pt&filter[platform]=" + if (platfom.isEmpty()) {
                "nintendo"
            } else {
                platfom
            }
            val open_game = "games/$a?currency=BRL&locale=pt"
            return when (selection) {
                ApiSelection.CURRENCIES -> {
                    String.format(url, currencies)
                }
                ApiSelection.GAMES -> {
                    String.format(url, games)
                }
                ApiSelection.SUMMARY -> {
                    String.format(url, summary)
                }
                ApiSelection.OPEN_GAME -> {
                    String.format(url, open_game)
                }
                ApiSelection.SEARCH_GAME -> {
                    String.format(url, search)
                }
            }
        }
    }
}