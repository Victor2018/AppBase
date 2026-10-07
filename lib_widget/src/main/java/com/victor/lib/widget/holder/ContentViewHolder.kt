package com.victor.lib.widget.holder

import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ContentViewHolder.java
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

open class ContentViewHolder<T>
    (private val binding: ViewBinding, val listener: AdapterView.OnItemClickListener?)
    : RecyclerView.ViewHolder(binding.root),View.OnClickListener,View.OnLongClickListener {
    companion object {
        const val ONITEM_LONG_CLICK: Long = -1
        const val ONITEM_CLICK: Long = 0
    }

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