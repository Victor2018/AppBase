package com.victor.app.base.holder

import android.view.View
import android.widget.AdapterView
import com.victor.app.base.data.StickyItemDecorationInfo
import com.victor.app.base.databinding.RvStickyItemDecorationTitleCellBinding
import com.victor.lib.widget.holder.ContentViewHolder

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: StickyItemDecorationTitleContentHolder
 * Author: Victor
 * Date: 2022/6/6 11:46
 * Description: 
 * -----------------------------------------------------------------
 */

class StickyItemDecorationTitleContentHolder(private val binding: RvStickyItemDecorationTitleCellBinding, listener: AdapterView.OnItemClickListener?) :
    ContentViewHolder<StickyItemDecorationInfo>(binding,listener) {

    override fun bindData(data: StickyItemDecorationInfo?) {
        itemView.tag = true
        binding.mTvTitle.text = data?.title ?: ""
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}