package br.com.subsavecoins.savemymoney.network

/**
 * Created by carlossilva on 26/04/2018.
 */
class Api {

    enum class ApiSelection { CURRENCIES, GAMES, SUMMARY, OPEN_GAME, SEARCH_GAME, ON_SALE,
        RECENT_RELEACE, COMING_SOON, PRE_SALE}

    companion object {
        fun getUrl(selection: ApiSelection, a: String? = null, platfom: String = ""): String {
            val url = "https://api-savecoins.nznweb.com.br/v1/%s"
            val open_game = "games/$a?currency=BRL&locale=pt"
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
            val onSale = "games?currency=USD&locale=pt&filter[${if (platfom.isEmpty()) {
                "nintendo"
            } else {
                platfom
            }}]=nintendo&filter[on_sale]=true"

            val recentRelease = "games?currency=USD&locale=pt&filter[${if (platfom.isEmpty()) {
                "nintendo"
            } else {
                platfom
            }}]=nintendo&filter[recent_release]=true"

            val comingSoon = "games?currency=USD&locale=pt&filter[${if (platfom.isEmpty()) {
                "nintendo"
            } else {
                platfom
            }}]=nintendo&filter[coming_soon]=true"

            val preSale = "games?currency=USD&locale=pt&filter[${if (platfom.isEmpty()) {
                "nintendo"
            } else {
                platfom
            }}]=nintendo&filter[preorder]=true"
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
                ApiSelection.ON_SALE -> {
                    String.format(url, onSale)
                }
                ApiSelection.RECENT_RELEACE -> {
                    String.format(url, recentRelease)
                }
                ApiSelection.COMING_SOON -> {
                    String.format(url, comingSoon)
                }
                ApiSelection.PRE_SALE -> {
                    String.format(url, preSale)
                }
            }
        }
    }
}