package com.victor.lib.widget.itemdecoration

import android.view.View

interface StickyView {
    /**
     * 是否是吸附view
     * @param view
     * @return
     */
    fun isStickyView(view: View?): Boolean

    /**
     * 得到吸附view的itemType
     * @return
     */
    fun getStickViewType(): Int
}