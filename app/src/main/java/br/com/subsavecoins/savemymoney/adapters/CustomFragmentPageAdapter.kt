package br.com.subsavecoins.savemymoney.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by carlossilva on 26/04/2018.
 */
class CustomFragmentPageAdapter(fragmentManager: FragmentManager) :
        FragmentStatePagerAdapter(fragmentManager) {

    private var mItems = mutableListOf<Fragment>()

    override fun getItem(position: Int): Fragment {
        return mItems[position]
    }

    override fun getCount(): Int {
        return mItems.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return if (position == 0) {
            "Destaques"
        }else {
            "Todos Jogos"
        }
    }

    fun setItems(items: MutableList<Fragment>) {
        mItems = items
        notifyDataSetChanged()
    }

}