package br.com.subsavecoins.savemymoney.adapters

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.models.Data
import com.squareup.picasso.Picasso
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import br.com.subsavecoins.savemymoney.activities.DetailActivity
import br.com.subsavecoins.savemymoney.activities.MainActivity


class SpotlightAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        if (picasso == null) {
            picasso = Picasso.Builder(p0.context).build()
        }

        val item = mData[p1]

        return if (item is String) {
            MyViewHolderHeader(LayoutInflater.from(p0.context)
                    .inflate(R.layout.header_spotlight, p0, false))
        } else {
            MyViewHolderItem(LayoutInflater.from(p0.context)
                    .inflate(R.layout.item_spotlight, p0, false))
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if (p0 is MyViewHolderItem) {
            val item = mData[p1] as Data
//            p0.title.text = item.title
//            picasso?.load(item.imageUrl)
//                    ?.resize(90, 90)
//                    ?.centerCrop()
//                    ?.into(p0.image)
            setProperties(item, p0)
        } else if (p0 is MyViewHolderHeader) {
            val item = mData[p1] as String
            p0.textView.text = item

        }

        setAnimation(p0.itemView, p1)
    }

    private var mData = mutableListOf<Any>()
    private var picasso: Picasso? = null
    private var lastPosition = -1
    lateinit var activity: MainActivity
    override fun getItemCount() = mData.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addAll(data: MutableList<Any>) {
        mData.clear()
        mData.addAll(data)
        notifyDataSetChanged()
    }

    private fun setAnimation(viewToAnimate: View?, position: Int) {

        if (viewToAnimate == null)
            return

        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            viewToAnimate.startAnimation(AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left))
            lastPosition = position
        }
    }

    inner class MyViewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.text1)
    }

    inner class MyViewHolderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.title)
        var image = itemView.findViewById<ImageView>(R.id.image)
        var price = itemView.findViewById<TextView>(R.id.release_date)
        var priceOffer = itemView.findViewById<TextView>(R.id.priceOffert)
        var offerView = itemView.findViewById<RelativeLayout>(R.id.offerView)
        var offerPercent = offerView.findViewById<TextView>(R.id.offerPercent)

        init {
            itemView.setOnClickListener {
                val models = mData[adapterPosition] as Data
                val intent = Intent(it.context, DetailActivity::class.java)
                val pair = Pair.create(image as View, it.context.getString(R.string.transition_image))
//                val pair1 = Pair.create(title as View, context.getString(R.string.transition_text))
                val bundle = Bundle()
                bundle.putParcelable("data", models)
                intent.putExtras(bundle)
                it.context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity,
                        pair).toBundle())
            }
        }
    }

    fun setProperties(data: Data?, holder: MyViewHolderItem) {
        holder.title?.text = data?.title
        val prince = data?.price_info
        if (prince?.discountPrice != null) {
            holder.price?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.price?.text = prince.regularPrice?.regularPrice
            holder.priceOffer?.visibility = View.VISIBLE
            holder.priceOffer?.text = prince.discountPrice?.discountPrice
            holder.offerView?.visibility = View.VISIBLE
            holder.offerPercent?.text = String.format("%s%s", prince.discountPrice?.percentOff, "%")

        } else {
            holder.price?.paintFlags = Paint.ANTI_ALIAS_FLAG
            holder.priceOffer?.visibility = View.GONE
            holder.offerView?.visibility = View.GONE
            holder.price?.text = prince?.currentPrice
        }

        picasso?.load(data?.imageUrl)
                ?.resize(90, 90)
                ?.centerCrop()
                ?.into(holder.image)
    }

}