package com.victor.app.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import com.victor.app.base.adapter.FinanceMonthAdapter
import com.victor.app.base.databinding.ActivityFlowLayoutmanagerBinding
import com.victor.app.base.databinding.ActivityPagerGridLayoutmanagerBinding
import com.victor.lib.base.BaseActivity
import com.victor.lib.widget.layoutmanager.FlowLayoutManager
import com.victor.lib.widget.pagerlayoutmanager.PagerGridLayoutManager
import com.victor.lib.widget.util.Loger

class PagerGridLayoutManagerActivity : BaseActivity<ActivityPagerGridLayoutmanagerBinding>(ActivityPagerGridLayoutmanagerBinding::inflate),
    OnClickListener, AdapterView.OnItemClickListener {

    companion object {
        fun intentStart (activity: Activity) {
            val intent = Intent(activity, PagerGridLayoutManagerActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private var mFinanceMonthAdapter: FinanceMonthAdapter? = null
    var mMonthPagerGridLayoutManager: PagerGridLayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarTextColorBlack = false
        super.onCreate(savedInstanceState)

        initView()
        initData()
    }

    fun initView() {
        mMonthPagerGridLayoutManager = PagerGridLayoutManager(3, 4,
            PagerGridLayoutManager.HORIZONTAL,false)
        mMonthPagerGridLayoutManager?.setHandlingSlidingConflictsEnabled(true)
        mMonthPagerGridLayoutManager?.setMaxScrollOnFlingDuration(200)
        mMonthPagerGridLayoutManager?.setPagerChangedListener(object :
            PagerGridLayoutManager.PagerChangedListener {
            override fun onPagerCountChanged(pagerCount: Int) {
            }

            override fun onPagerIndexSelected(
                prePagerIndex: Int,
                currentPagerIndex: Int
            ) {
                Loger.e(TAG,"onPagerIndexSelected-prePagerIndex = $prePagerIndex")
                Loger.e(TAG,"onPagerIndexSelected-currentPagerIndex = $currentPagerIndex")
                val total = mMonthPagerGridLayoutManager?.getTotalPageCount() ?: 0
                showPageIndex(currentPagerIndex, total)
            }
        })
        binding.mRvMessage.layoutManager = mMonthPagerGridLayoutManager

        mFinanceMonthAdapter = FinanceMonthAdapter(this,this)
        binding.mRvMessage.adapter = mFinanceMonthAdapter

        binding.mIvBack.setOnClickListener(this)
        binding.mBtnPrePage.setOnClickListener(this)
        binding.mBtnNextPage.setOnClickListener(this)
    }

    fun initData() {
        mFinanceMonthAdapter?.clear()
        for (i in 0 .. 180) {
            mFinanceMonthAdapter?.add("标签$i")
        }
        mFinanceMonthAdapter?.notifyDataSetChanged()
        val total = mFinanceMonthAdapter?.getContentItemCount() ?: 0
        val onePageCount = 3*4
        val totalPages = total / onePageCount
        showPageIndex(0,totalPages)
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.mIvBack -> {
                finish()
            }
            R.id.mBtnPrePage -> {
                mMonthPagerGridLayoutManager?.smoothScrollToPrePager()
            }
            R.id.mBtnNextPage -> {
                mMonthPagerGridLayoutManager?.smoothScrollToNextPager()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    fun showPageIndex(pagerIndex: Int,total: Int) {
        binding.mBtnPageIndex.text = "${pagerIndex + 1}/$total"
    }
}