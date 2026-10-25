package com.victor.app.base.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.victor.app.base.databinding.RvMarketPrintUnitCellBinding
import com.victor.app.base.holder.MarkPrintUnitContentHolder
import com.victor.lib.widget.adapter.BaseRecycleAdapter

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MarkPrintUnitAdapter
 * Author: Victor
 * Date: 2025/12/16 15:51
 * Description: 
 * -----------------------------------------------------------------
 */

class MarkPrintUnitAdapter(context: Context?, listener: AdapterView.OnItemClickListener?) :
    BaseRecycleAdapter<String, RecyclerView.ViewHolder>(context, listener) {

    var checkPosition: Int = 0

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: String?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MarkPrintUnitContentHolder(inflateVB(RvMarketPrintUnitCellBinding::inflate, parent), listener)
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: String?, position: Int) {
        when(viewHolder) {
            is MarkPrintUnitContentHolder -> {
                viewHolder.checkPosition = checkPosition
                viewHolder.bindData(data)
            }
        }
    }
}