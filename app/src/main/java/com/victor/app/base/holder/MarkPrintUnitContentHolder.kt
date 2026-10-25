package com.victor.app.base.holder

import android.view.View
import android.widget.AdapterView
import com.victor.app.base.R
import com.victor.app.base.databinding.RvMarketPrintUnitCellBinding
import com.victor.lib.widget.holder.ContentViewHolder
import com.victor.lib.widget.util.ResUtils

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2025-2035, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MarkPrintUnitContentHolder
 * Author: Victor
 * Date: 2025/12/16 15:50
 * Description: 
 * -----------------------------------------------------------------
 */

class MarkPrintUnitContentHolder(private val binding: RvMarketPrintUnitCellBinding, listener: AdapterView.OnItemClickListener?) :
    ContentViewHolder<String>(binding,listener) {

    var checkPosition = 0

    override fun bindData(data: String?) {
        binding.mTvUnit.text = data ?: "全部单位"
        if (checkPosition == adapterPosition) {
            binding.mTvUnit.setTextColor(ResUtils.getColorRes(itemView.context,R.color.color_0084FF))
            binding.mTvUnit.background = ResUtils.getDrawableRes(itemView.context,R.drawable.shape_e0f0ff_radius_12)
        } else {
            binding.mTvUnit.setTextColor(ResUtils.getColorRes(itemView.context,R.color.color_1A1A1A))
            binding.mTvUnit.background = ResUtils.getDrawableRes(itemView.context,R.drawable.shape_f9f9fb_radius_12)
        }
        if (adapterPosition == 0) {
            binding.mTvUnit.setPadding(
                ResUtils.getDimenPixRes(itemView.context,com.victor.screen.match.library.R.dimen.dp_44),
                ResUtils.getDimenPixRes(itemView.context,com.victor.screen.match.library.R.dimen.dp_18),
                ResUtils.getDimenPixRes(itemView.context,com.victor.screen.match.library.R.dimen.dp_44),
                ResUtils.getDimenPixRes(itemView.context,com.victor.screen.match.library.R.dimen.dp_18)
            )
        } else {
            binding.mTvUnit.setPadding(
                ResUtils.getDimenPixRes(itemView.context,com.victor.screen.match.library.R.dimen.dp_26),
                ResUtils.getDimenPixRes(itemView.context,com.victor.screen.match.library.R.dimen.dp_18),
                ResUtils.getDimenPixRes(itemView.context,com.victor.screen.match.library.R.dimen.dp_26),
                ResUtils.getDimenPixRes(itemView.context,com.victor.screen.match.library.R.dimen.dp_18)
            )
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }

}