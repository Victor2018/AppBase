package com.victor.app.base

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import com.victor.app.base.databinding.ActivityMainBinding
import com.victor.lib.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        binding.mLLContent.mBtnLoadMore.setOnClickListener(this)
        binding.mLLContent.mBtnStickyItemDecoration.setOnClickListener(this)
        binding.mLLContent.mBtnBreatheRedView.setOnClickListener(this)
        binding.mLLContent.mExpandableLayout.setOnClickListener(this)
        binding.mLLContent.mFlowLayoutManager.setOnClickListener(this)
        binding.mLLContent.mPagerGridLayoutManager.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.mBtnLoadMore -> {
                LoadMoreActivity.intentStart(this)
            }
            R.id.mBtnStickyItemDecoration -> {
                StickyItemDecorationActivity.intentStart(this)
            }
            R.id.mBtnBreatheRedView -> {
                BreatheRedViewActivity.intentStart(this)
            }
            R.id.mExpandableLayout -> {
                ExpandableLayoutActivity.intentStart(this)
            }
            R.id.mFlowLayoutManager -> {
                FlowLayoutManagerActivity.intentStart(this)
            }
            R.id.mPagerGridLayoutManager -> {
                PagerGridLayoutManagerActivity.intentStart(this)
            }
        }
    }
}