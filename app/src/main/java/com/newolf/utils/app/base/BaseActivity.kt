package com.newolf.utils.app.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.newolf.utils.app.Init

/**
 * Created by v_anle on 2018/12/5.
 */
abstract class BaseActivity: AppCompatActivity(), Init {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        initView()
        initListener()
        loadData()
    }

    protected abstract fun getContentView():Int



}