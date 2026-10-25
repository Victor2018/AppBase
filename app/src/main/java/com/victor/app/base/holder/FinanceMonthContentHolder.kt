package com.victor.app.base.holder

import android.view.View
import android.widget.AdapterView
import com.victor.app.base.databinding.RvFinanceMonthCellBinding
import com.victor.lib.widget.holder.ContentViewHolder

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: FinanceMonthContentHolder
 * Author: Victor
 * Date: 2026/2/4 15:50
 * Description: 
 * -----------------------------------------------------------------
 */

class FinanceMonthContentHolder(private val binding: RvFinanceMonthCellBinding, listener: AdapterView.OnItemClickListener?) :
    ContentViewHolder<String>(binding,listener) {

    override fun bindData(data: String?) {
        binding.mTvMonth.text = data ?: ""
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}