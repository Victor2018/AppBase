package com.victor.app.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import com.bao.groupitemdecoration.widget.DividerItemDecoration
import com.victor.app.base.adapter.StickyItemDecorationAdapter
import com.victor.app.base.data.StickyItemDecorationInfo
import com.victor.app.base.data.StickyItemDecorationType
import com.victor.app.base.databinding.ActivityStickyItemDecorationBinding
import com.victor.lib.base.BaseActivity
import com.victor.lib.widget.itemdecoration.StickyItemDecoration

class StickyItemDecorationActivity : BaseActivity<ActivityStickyItemDecorationBinding>(ActivityStickyItemDecorationBinding::inflate),
    OnClickListener, AdapterView.OnItemClickListener {

    companion object {
        fun intentStart (activity: Activity) {
            val intent = Intent(activity, StickyItemDecorationActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private var mStickyItemDecorationAdapter: StickyItemDecorationAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarTextColorBlack = false
        super.onCreate(savedInstanceState)

        initView()
        initData()
    }

    fun initView() {
        mStickyItemDecorationAdapter = StickyItemDecorationAdapter(this,this)
        binding.mRvMessage.adapter = mStickyItemDecorationAdapter

        binding.mRvMessage.addItemDecoration(StickyItemDecoration())
        binding.mRvMessage.addItemDecoration(DividerItemDecoration())

        binding.mIvBack.setOnClickListener(this)
    }

    fun initData() {
        val datas = ArrayList<StickyItemDecorationInfo>()
        for (i in 0 .. 5) {
            val item = StickyItemDecorationInfo()
            item.itemType = StickyItemDecorationType.TYPE_TITLE
            item.title = "Group---$i"
            datas.add(item)
            for (j in 0 .. 10) {
                val child = StickyItemDecorationInfo()
                child.itemType = StickyItemDecorationType.TYPE_NORMAL
                child.title = "Item---$i-$j"
                datas.add(child)
            }
        }
        mStickyItemDecorationAdapter?.showData(datas)
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.mIvBack -> {
                finish()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }
}