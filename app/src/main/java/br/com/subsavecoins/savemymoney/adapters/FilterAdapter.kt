package br.com.subsavecoins.savemymoney.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import br.com.subsavecoins.savemymoney.R

class FilterAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(p0.context)
                .inflate(R.layout.item_filter_recyclerview, p0, false))
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if (p0 is MyViewHolder) {

            val item = mItems[p1]

            p0.text1.text = item

            p0.itemView.setOnClickListener (null)

            p0.itemView.setOnClickListener {

            }

            setAnimation(p0.itemView, p1)
        }
    }

    private var mItems = mutableListOf<String>()

    private var lastPosition = -1

    override fun getItemCount() = mItems.size

    fun addFilterItems(items: MutableList<String>) {
        mItems.addAll(items)
    }

    private fun setAnimation(view: View?, position: Int) {
        if (view == null)
            return

        if (position > lastPosition) {
            view.startAnimation(AnimationUtils.loadAnimation(view.context, android.R.anim.slide_in_left))
            lastPosition = position
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text1 = itemView.findViewById<TextView>(R.id.text1)
    }

}