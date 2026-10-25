package com.victor.app.base.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.victor.app.base.data.StickyItemDecorationInfo
import com.victor.app.base.data.StickyItemDecorationType
import com.victor.app.base.databinding.RvMessageCellBinding
import com.victor.app.base.databinding.RvStickyItemDecorationCellBinding
import com.victor.app.base.databinding.RvStickyItemDecorationTitleCellBinding
import com.victor.app.base.holder.MessageContentHolder
import com.victor.app.base.holder.StickyItemDecorationContentHolder
import com.victor.app.base.holder.StickyItemDecorationTitleContentHolder
import com.victor.lib.widget.adapter.BaseRecycleAdapter

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageAdapter
 * Author: Victor
 * Date: 2022/6/6 11:45
 * Description: 
 * -----------------------------------------------------------------
 */

class StickyItemDecorationAdapter(context: Context?, listener: AdapterView.OnItemClickListener?) :
    BaseRecycleAdapter<StickyItemDecorationInfo, RecyclerView.ViewHolder>(context, listener) {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: StickyItemDecorationInfo?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            StickyItemDecorationType.TYPE_TITLE -> {
                return StickyItemDecorationTitleContentHolder(inflateVB(RvStickyItemDecorationTitleCellBinding::inflate,parent),listener)
            }
            else -> {
                StickyItemDecorationContentHolder(inflateVB(RvStickyItemDecorationCellBinding::inflate,parent),listener)
            }
        }
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: StickyItemDecorationInfo?, position: Int) {
        when(viewHolder) {
            is StickyItemDecorationTitleContentHolder -> viewHolder.bindData(data)
            is StickyItemDecorationContentHolder -> viewHolder.bindData(data)
        }
    }

    override fun getItemViewType(position: Int): Int {
        var viewType = super.getItemViewType(position)
        if (viewType == ITEM_TYPE_CONTENT) {
            val data = getItem(position)
            viewType = data?.itemType ?: 0
        }
        return viewType
    }
}