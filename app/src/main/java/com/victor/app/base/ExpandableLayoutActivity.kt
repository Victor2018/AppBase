package com.victor.app.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.victor.app.base.databinding.ActivityExpandableLayoutBinding
import com.victor.lib.base.BaseActivity
import com.victor.lib.widget.util.TextViewBoundsUtil

class ExpandableLayoutActivity : BaseActivity<ActivityExpandableLayoutBinding>(ActivityExpandableLayoutBinding::inflate),
    OnClickListener {

    companion object {
        fun intentStart (activity: Activity) {
            val intent = Intent(activity, ExpandableLayoutActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarTextColorBlack = false
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    fun initView() {
        binding.mIvBack.setOnClickListener(this)
        binding.mTvExpand.setOnClickListener(this)
    }

    fun initData() {
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.mIvBack -> {
                finish()
            }
            R.id.mTvExpand -> {
                binding.mElReceiptPoint.toggle()

                val isExpanded = binding.mElReceiptPoint.isExpanded()
                if (isExpanded) {
                    binding.mTvExpand.text = "收起"
                    TextViewBoundsUtil.setTvDrawableRight(this,binding.mTvExpand,
                        R.mipmap.order_ic_receipt_point_up)
                } else {
                    binding.mTvExpand.text = "展开"
                    TextViewBoundsUtil.setTvDrawableRight(this,binding.mTvExpand,
                        R.mipmap.order_ic_trust_entry_down)
                }
            }
        }
    }

}