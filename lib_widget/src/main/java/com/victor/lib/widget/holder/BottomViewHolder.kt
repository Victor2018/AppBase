package com.victor.lib.widget.holder

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.victor.lib.widget.R

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: BottomViewHolder.java
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

class BottomViewHolder : RecyclerView.ViewHolder {
    var progressBar: ProgressBar? = null
    var mTvTitle: TextView? = null
    var mTvLoadEnd: TextView? = null

    constructor(itemView: View) : super(itemView) {
        progressBar = itemView.findViewById(R.id.progressBar)
        mTvTitle = itemView.findViewById(R.id.tv_title)
        mTvLoadEnd = itemView.findViewById(R.id.mTvLoadEnd)
    }
}