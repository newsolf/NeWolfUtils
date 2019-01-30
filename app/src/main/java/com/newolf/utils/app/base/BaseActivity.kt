package com.newolf.utils.app.base

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.newolf.utils.app.Init

/**
 * Created by v_anle on 2018/12/5.
 */
abstract class BaseActivity: AppCompatActivity(), Init {
    protected lateinit var mContext : Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        mContext = this
        initView()
        initListener()
        loadData()
    }

    protected abstract fun getContentView():Int



}