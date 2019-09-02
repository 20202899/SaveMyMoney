package br.com.subsavecoins.savemymoney.models

import java.io.Serializable

class SpotlightModel (var data: Model) : Serializable

class Model (var gamesCount: Int, var pricesCount: Int,
             var recentReleases: MutableList<Data>, var onSale: MutableList<Data>,
             var comingSoon: MutableList<Data>, var newlyAdded: MutableList<Data>,
             var preorder: MutableList<Data>) : Serializable