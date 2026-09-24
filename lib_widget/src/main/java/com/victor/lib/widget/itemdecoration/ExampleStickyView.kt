package com.victor.lib.widget.itemdecoration

import android.view.View

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ExampleStickyView
 * Author: Victor
 * Date: 2022/4/1 12:13
 * Description: 
 * -----------------------------------------------------------------
 */

class ExampleStickyView : StickyView {
    override fun isStickyView(view: View?) = view?.tag as? Boolean ?: false

    override fun getStickViewType() = 11
}