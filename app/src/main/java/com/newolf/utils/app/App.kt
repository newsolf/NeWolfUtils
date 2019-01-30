package com.newolf.utils.app

import android.app.Application
import com.blankj.utilcode.util.Utils

/**
 * Created by v_anle on 2018/12/5.
 */
class App:Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}