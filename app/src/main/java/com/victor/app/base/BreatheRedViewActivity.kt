package com.victor.app.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.victor.app.base.databinding.ActivityBreatheRedViewBinding
import com.victor.lib.base.BaseActivity

class BreatheRedViewActivity : BaseActivity<ActivityBreatheRedViewBinding>(ActivityBreatheRedViewBinding::inflate),
    OnClickListener {

    companion object {
        fun intentStart (activity: Activity) {
            val intent = Intent(activity, BreatheRedViewActivity::class.java)
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
    }

    fun initData() {
        binding.mBreatheRedView.startAlphaBreatheAnimation()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.mIvBack -> {
                finish()
            }
        }
    }

}