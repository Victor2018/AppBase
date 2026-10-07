package com.victor.app.base

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.victor.app.base.databinding.ActivityMainBinding
import com.victor.lib.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate),OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        binding.mLLContent.mBtnLoadMore.setOnClickListener(this)
        binding.fab.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.fab -> {
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .setAnchorView(R.id.fab).show()
            }
            R.id.mBtnLoadMore -> {
                LoadMoreActivity.intentStart(this)
            }
        }
    }
}