package com.victor.app.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.victor.app.base.adapter.MessageAdapter
import com.victor.app.base.databinding.ActivityLoadMoreBinding
import com.victor.lib.base.BaseActivity
import com.victor.lib.widget.LMRecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadMoreActivity : BaseActivity<ActivityLoadMoreBinding>(ActivityLoadMoreBinding::inflate),
    OnClickListener, SwipeRefreshLayout.OnRefreshListener, LMRecyclerView.OnLoadMoreListener,
    AdapterView.OnItemClickListener {

    companion object {
        fun intentStart (activity: Activity) {
            val intent = Intent(activity, LoadMoreActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private var mMessageAdapter: MessageAdapter? = null
    var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarTextColorBlack = false
        super.onCreate(savedInstanceState)

        initView()
        initData()
    }

    fun initView() {
        mMessageAdapter = MessageAdapter(this,this)
        binding.mRvMessage.adapter = mMessageAdapter

        binding.mSrlRefresh.setOnRefreshListener(this)
        binding.mRvMessage.setLoadMoreListener(this)

        binding.mIvBack.setOnClickListener(this)
    }

    fun initData() {
        requestData()
    }

    fun requestData() {
        lifecycleScope.launch {
            if (currentPage == 1) {
                mMessageAdapter?.clear()
                binding.mSrlRefresh.isRefreshing = true
            }
            delay(1000)
            binding.mSrlRefresh.isRefreshing = false

            val datas = ArrayList<String>()
            val start = mMessageAdapter?.getContentItemCount() ?: 0
            for (i in start .. start + 20) {
                datas.add("Item $i")
            }
            mMessageAdapter?.showData(datas, listOf(binding.mTvNoData),binding.mRvMessage,currentPage,20)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.mIvBack -> {
                finish()
            }
            R.id.fab -> {
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setAnchorView(R.id.fab).show()
            }
            R.id.mBtnLoadMore -> {

            }
        }
    }

    override fun onRefresh() {
        currentPage = 1
        requestData()
    }

    override fun OnLoadMore() {
        currentPage++
        requestData()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }
}