package com.victor.app.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import com.victor.app.base.adapter.MarkPrintUnitAdapter
import com.victor.app.base.databinding.ActivityFlowLayoutmanagerBinding
import com.victor.lib.base.BaseActivity
import com.victor.lib.widget.layoutmanager.FlowLayoutManager

class FlowLayoutManagerActivity : BaseActivity<ActivityFlowLayoutmanagerBinding>(ActivityFlowLayoutmanagerBinding::inflate),
    OnClickListener, AdapterView.OnItemClickListener {

    companion object {
        fun intentStart (activity: Activity) {
            val intent = Intent(activity, FlowLayoutManagerActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private var mMarkPrintUnitAdapter: MarkPrintUnitAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarTextColorBlack = false
        super.onCreate(savedInstanceState)

        initView()
        initData()
    }

    fun initView() {
        mMarkPrintUnitAdapter = MarkPrintUnitAdapter(this,this)
        binding.mRvMessage.layoutManager = FlowLayoutManager()
        binding.mRvMessage.adapter = mMarkPrintUnitAdapter

        binding.mIvBack.setOnClickListener(this)
    }

    fun initData() {
        mMarkPrintUnitAdapter?.clear()
        for (i in 0 .. 10) {
            mMarkPrintUnitAdapter?.add("测试标签$i")
        }
        mMarkPrintUnitAdapter?.notifyDataSetChanged()
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.mIvBack -> {
                finish()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mMarkPrintUnitAdapter?.checkPosition = position
        mMarkPrintUnitAdapter?.notifyDataSetChanged()
    }
}