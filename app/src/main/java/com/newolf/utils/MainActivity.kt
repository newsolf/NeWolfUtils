package com.newolf.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.blankj.utilcode.util.ToastUtils
import com.newolf.utils.app.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    internal var mLastClick: Long = 0
    override fun getContentView(): Int {
        return R.layout.activity_main
    }


    override fun initView() {

    }

    override fun initListener() {
        btnSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }

        btnClean.setOnClickListener {
            clearMem()
            ToastUtils.showShort("清除完成")
            loadData()
        }
        btnNetTest.setOnClickListener {

        }
    }

    override fun loadData() {
        tvShow.setText(getCurrentMeminfo())
    }

    private fun getCurrentMeminfo(): String {
        val sb = StringBuffer()
        val memoryInfo: ActivityManager.MemoryInfo = ActivityManager.MemoryInfo()
        val activityManager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)

        sb.append("剩余内存：" + (memoryInfo.availMem / 1024 / 1024) + "MB\n");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            sb.append("总内存： " + (memoryInfo.totalMem / 1024 / 1024) + "MB\n");
        }
        sb.append("内存是否过低：" + memoryInfo.lowMemory);



        return sb.toString()
    }

    fun clearMem() {
        var cleanName : String = ""
        val activityManager: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses
        for (runningApp in runningAppProcesses) {
            if (runningApp.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (appProcess in runningAppProcesses) {
                    cleanName += (appProcess.processName + "\n")
                    activityManager.killBackgroundProcesses(appProcess.processName)
                }

            }
        }
        tvCleanName.setText(cleanName)

    }

    override fun onBackPressed() {
        val currentTimeMillis = System.currentTimeMillis()
        val l = currentTimeMillis - mLastClick
        if (l > 0 && l < 1000 * 10) {
            super.onBackPressed()
        } else {
            ToastUtils.showShort("再点击一次退出")
            mLastClick = currentTimeMillis
        }
    }


}
