package com.victor.app.base.holder

import android.view.View
import android.widget.AdapterView
import com.victor.app.base.data.StickyItemDecorationInfo
import com.victor.app.base.databinding.RvStickyItemDecorationCellBinding
import com.victor.lib.widget.holder.ContentViewHolder

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: StickyItemDecorationContentHolder
 * Author: Victor
 * Date: 2022/6/6 11:46
 * Description: 
 * -----------------------------------------------------------------
 */

class StickyItemDecorationContentHolder(private val binding: RvStickyItemDecorationCellBinding, listener: AdapterView.OnItemClickListener?) :
    ContentViewHolder<StickyItemDecorationInfo>(binding,listener) {

    override fun bindData(data: StickyItemDecorationInfo?) {
        itemView.tag = false
        binding.mTvTitle.text = data?.title ?: ""
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}