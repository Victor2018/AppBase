package com.victor.app.base.holder

import android.view.View
import android.widget.AdapterView
import com.victor.app.base.databinding.RvMessageCellBinding
import com.victor.lib.widget.holder.ContentViewHolder

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MessageContentHolder
 * Author: Victor
 * Date: 2022/6/6 11:46
 * Description: 
 * -----------------------------------------------------------------
 */

class MessageContentHolder(private val binding: RvMessageCellBinding, listener: AdapterView.OnItemClickListener?) :
    ContentViewHolder<String>(binding,listener) {

    override fun bindData(data: String?) {
        binding.mTvTitle.text = data ?: ""
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}