package com.victor.app.base.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.victor.app.base.databinding.RvMessageCellBinding
import com.victor.app.base.holder.MessageContentHolder
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

class MessageAdapter(context: Context?, listener: AdapterView.OnItemClickListener?) :
    BaseRecycleAdapter<String, RecyclerView.ViewHolder>(context, listener) {

    override fun onCreateHeadVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindHeadVHolder(viewHolder: RecyclerView.ViewHolder, data: String?, position: Int) {
    }

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MessageContentHolder(inflateVB(RvMessageCellBinding::inflate,parent),listener)
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: String?, position: Int) {
        if (viewHolder is MessageContentHolder) {
            viewHolder.bindData(data)
        }
    }
}