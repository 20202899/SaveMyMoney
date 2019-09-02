package br.com.subsavecoins.savemymoney.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.com.subsavecoins.savemymoney.R
import br.com.subsavecoins.savemymoney.models.Data
import com.squareup.picasso.Picasso
import android.view.animation.AnimationUtils


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
            p0.textView.text = item.title
            picasso?.load(item.imageUrl)
                    ?.resize(90, 90)
                    ?.centerCrop()
                    ?.into(p0.image)
        } else if (p0 is MyViewHolderHeader) {
            val item = mData[p1] as String
            p0.textView.text = item

        }

        setAnimation(p0.itemView, p1)
    }

    private var mData = mutableListOf<Any>()
    private var picasso: Picasso? = null
    private var lastPosition = -1

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
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    inner class MyViewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView = itemView.findViewById<TextView>(R.id.text1)
    }

    inner class MyViewHolderItem(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.image)
        val textView = itemView.findViewById<TextView>(R.id.text1)
    }

}