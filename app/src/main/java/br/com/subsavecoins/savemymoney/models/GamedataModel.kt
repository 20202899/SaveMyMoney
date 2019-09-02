package br.com.subsavecoins.savemymoney.models

import android.annotation.SuppressLint
import android.content.Context
import java.io.Serializable

/**
 * Created by carlossilva on 26/04/2018.
 */

class GamedataModel: Serializable {
    var links: Link? = null
    var meta: Meta? = null
    var filters: Filters? = null
    var data: MutableList<Data> ? = null
}

class Link: Serializable {
    var self = ""
    var next = ""
    var last = ""
}

class Meta: Serializable {
    var pageTotal = 0
    var total = 0
}

class Filters: Serializable {

    var languages = mutableListOf<Language>()
    var categories = mutableListOf<Categorie>()

    class Language: Serializable {
        var id = 0
        var code = ""
        var name = ""
    }

    class Categorie: Serializable {
        var id = 0
        var name = ""
    }
}

open class Data: Serializable {
    var id = -1
    var newId: Long = -1
    var slug = ""
    var url = ""
    var title = ""
    var imageUrl = ""
    var releaseDateDisplay = ""
    var releaseDateOrde = ""
    var price_info: PriceInfo? = null
    var releaseDates = mutableListOf<RelaseDate>()
    var publishers = mutableListOf<Publisher>()
    var developers = mutableListOf<Any?>()
    var categories = mutableListOf<String>()
    var youtubeId = ""
    var description = ""
    var hasDemo = false
    var languages = mutableListOf<Language>()
    var numberOfPlayers = ""
    var retailRelease = false
    init {
        newId = hashCode().toLong()
    }

    @SuppressLint("WrongConstant")
    fun save(context: Context) {
        synchronized(this) {

        }
    }

    class Language{
        var region = ""
        var languages = mutableListOf<Filters.Language>()
    }
}

class PriceInfo: Serializable {
    var currentPrice = ""
    var rawCurrentPrice = 0f
    var hasDiscount: Boolean = false
    var status = ""
    var goldPoints = 0
    var country: Country? = null
    var regularPrice: RegularPrice? = null
    var discountPrice: DiscountPrice? = null

    class Country: Serializable {
        var code = ""
        var name = ""
    }

    class RegularPrice: Serializable {
        var rawOriginalRegularPrice = 0f
        var originalRegularPrice = ""
        var rawRegularPrice = 0f
        var regularPrice = ""
    }

    class DiscountPrice: Serializable {
        var rawOriginalDiscountPrice = 0f
        var originalDiscountPrice = ""
        var rawDiscountPrice = 0f
        var discountPrice = ""
        var discountBeginsAt = ""
        var discountEndsAt = ""
        var percentOff = ""
    }
}

class RelaseDate : Serializable {
    var region = ""
    var releaseDate = ""
}

class Publisher : Serializable {
    var name: String? = null
    var japaneseName: String? = null
}