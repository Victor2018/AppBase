package com.victor.lib.widget.holder

import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.victor.lib.widget.holder.ContentViewHolder.Companion.ONITEM_CLICK
import com.victor.lib.widget.holder.ContentViewHolder.Companion.ONITEM_LONG_CLICK

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: HeaderViewHolder.java
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

open class HeaderViewHolder<T>
    (private val binding: ViewBinding, val listener: AdapterView.OnItemClickListener?)
    : RecyclerView.ViewHolder((binding.root)),View.OnClickListener, View.OnLongClickListener {

    init {
        binding.root.setOnClickListener(this)
        binding.root.setOnLongClickListener(this)
    }

    open fun bindData (data: T?) {

    }
    override fun onClick(view: View) {
        listener?.onItemClick(null, view, adapterPosition, ONITEM_CLICK)
    }

    override fun onLongClick(v: View): Boolean {
        listener?.onItemClick(null, v, adapterPosition, ONITEM_LONG_CLICK)
        return false
    }
}