package br.com.subsavecoins.savemymoney.adapters

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.activities.DetailActivity
import br.com.subsavecoins.savemymoney.models.Data
import br.com.subsavecoins.savemymoney.models.GamedataModel
import com.squareup.picasso.Picasso

/**
 * Created by carlossilva on 26/04/2018.
 */
class GameAdapter(var context: AppCompatActivity) : RecyclerView.Adapter<GameAdapter.CustomViewHolder>() {
    override fun onBindViewHolder(p0: CustomViewHolder, p1: Int) {
        val models = gamedataModel?.data
        setProperties(models!![p1], p0)
        setAnimation(p0.itemView, p1)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(p0.context)
                .inflate(R.layout.item_recyclerview, p0, false), context)
    }

    var isFilter = false
    var gamedataModel: GamedataModel? = null
    val picasso = Picasso.Builder(context).build()
    var filterList = mutableListOf<Data>()
    private var lastPosition = -1

    override fun getItemCount(): Int {
        return if (gamedataModel == null) {
            0
        } else {
            gamedataModel?.data!!.size
        }
    }

    override fun getItemId(position: Int): Long {
        return if (gamedataModel == null) {
            super.getItemId(position)
        } else {
            val model = gamedataModel!!.data!![position]
            model.newId
        }
    }

    fun addModel(gamedataModel: GamedataModel) {
        this.gamedataModel = if (this.gamedataModel == null) {
            if (!isFilter)
                filterList.addAll(gamedataModel.data!!)
            gamedataModel
        } else {
            this.gamedataModel?.data!!.addAll(gamedataModel.data!!)
            if (!isFilter)
                filterList.addAll(gamedataModel.data!!)
            this.gamedataModel?.links = gamedataModel.links
            this.gamedataModel?.meta = gamedataModel.meta
            this.gamedataModel?.filters = gamedataModel.filters
            this.gamedataModel
        }

        notifyDataSetChanged()
    }

    fun reloadDefault() {
        gamedataModel?.data?.clear()
        gamedataModel?.data?.addAll(filterList)
        notifyDataSetChanged()
    }

    fun lastModel(): GamedataModel? {
        return gamedataModel
    }

    fun setProperties(data: Data?, holder: CustomViewHolder?) {
        holder?.title?.text = data?.title
        val prince = data?.price_info
        if (prince?.discountPrice != null) {
            holder?.price?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder?.price?.text = prince.regularPrice?.regularPrice
            holder?.priceOffer?.visibility = View.VISIBLE
            holder?.priceOffer?.text = prince.discountPrice?.discountPrice
            holder?.offerView?.visibility = View.VISIBLE
            holder?.offerPercent?.text = String.format("%s%s", prince.discountPrice?.percentOff, "%")

        } else {
            holder?.price?.paintFlags = Paint.ANTI_ALIAS_FLAG
            holder?.priceOffer?.visibility = View.GONE
            holder?.offerView?.visibility = View.GONE
            holder?.price?.text = prince?.currentPrice
        }

        picasso.load(data?.imageUrl)
                .resize(90, 90)
                .centerCrop()
                .into(holder?.image)
    }

    private fun setAnimation(view: View?, position: Int) {
        if (view == null)
            return

        if (position > lastPosition) {
            view.startAnimation(AnimationUtils.loadAnimation(view.context, android.R.anim.slide_in_left))
            lastPosition = position
        }
    }

    inner class CustomViewHolder(view: View, var context: AppCompatActivity) : RecyclerView.ViewHolder(view) {
        var title = view.findViewById<TextView>(R.id.title)
        var image = view.findViewById<ImageView>(R.id.image)
        var price = view.findViewById<TextView>(R.id.release_date)
        var priceOffer = view.findViewById<TextView>(R.id.priceOffert)
        var offerView = view.findViewById<RelativeLayout>(R.id.offerView)
        var offerPercent = offerView.findViewById<TextView>(R.id.offerPercent)

        init {
            view.setOnClickListener {
                val models = gamedataModel!!.data
                val intent = Intent(context, DetailActivity::class.java)
                val pair = Pair.create(image as View, context.getString(R.string.transition_image))
//                val pair1 = Pair.create(title as View, context.getString(R.string.transition_text))
                val bundle = Bundle()
                bundle.putSerializable("data", models!![adapterPosition])
                intent.putExtras(bundle)
                context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context,
                        pair).toBundle())
            }
        }
    }

}