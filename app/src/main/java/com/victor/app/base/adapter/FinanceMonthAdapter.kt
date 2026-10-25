package com.victor.app.base.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.victor.app.base.databinding.RvFinanceMonthCellBinding
import com.victor.app.base.holder.FinanceMonthContentHolder
import com.victor.lib.widget.adapter.BaseRecycleAdapter

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FinanceMonthAdapter
 * Author: Victor
 * Date: 2026/2/4 15:50
 * Description: 
 * -----------------------------------------------------------------
 */

class FinanceMonthAdapter(context: Context?, listener: AdapterView.OnItemClickListener?) :
    BaseRecycleAdapter<String, RecyclerView.ViewHolder>(context, listener) {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: String?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FinanceMonthContentHolder(inflateVB(RvFinanceMonthCellBinding::inflate,parent),listener)
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: String?, position: Int) {
        when(viewHolder) {
            is FinanceMonthContentHolder -> {
                viewHolder.bindData(data)
            }
        }
    }
}