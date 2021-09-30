package com.percolate.mentions.sample.adapters

import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * A RecyclerArrayAdapter implementation very similar to an ArrayAdapter.  This was taken from the sample provided in the StickyHeadersRecyclerView library.
 * https://github.com/timehop/sticky-headers-recyclerview/blob/master/sample/src/main/java/com/timehop/stickyheadersrecyclerview/sample/RecyclerArrayAdapter.java
 */
abstract class RecyclerArrayAdapter<M, VH : RecyclerView.ViewHolder?> internal constructor() : RecyclerView.Adapter<VH>() {
    private val items = ArrayList<M>()
    fun add(`object`: M) {
        items.add(`object`)
        notifyDataSetChanged()
    }

    fun add(index: Int, `object`: M) {
        items.add(index, `object`)
        notifyDataSetChanged()
    }

    fun addAll(collection: Collection<M>?) {
        if (collection != null) {
            items.addAll(collection)
            notifyDataSetChanged()
        }
    }

    fun addAll(vararg items: M) {
        addAll(Arrays.asList(*items))
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun remove(`object`: M) {
        items.remove(`object`)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): M {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    init {
        setHasStableIds(true)
    }
}