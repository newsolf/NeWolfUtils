package com.newolf.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.newolf.utils.app.base.BaseActivity
import com.newolf.utils.app.utils.DistUtils
import com.newolf.utils.app.utils.NetUtils
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
            netTest()
        }
        btnDiskTest.setOnClickListener {
           diskTest()
        }

        btnOpenNavi.setOnClickListener {
            openNavi()
        }

        btnOpenHardwareInfo.setOnClickListener {
            openHardwareInfo()
        }
    }

    private fun openHardwareInfo() {
        val launchIntent = packageManager.getLaunchIntentForPackage("com.chehejia.car.factorymode")
        startActivity(launchIntent)
    }

    private fun openNavi() {
        val launchIntent = packageManager.getLaunchIntentForPackage("com.navi.tracker")
        startActivity(launchIntent)
    }

    private fun diskTest() {
        DistUtils.getAllStoragesV14(mContext)
    }

    private fun netTest() {
        val wifiState: String = if (NetUtils.isWifiConnected(mContext)) {
            "已连接"
        } else {
            "未连接"
        }

        val isConnect: String = if (NetUtils.isNetworkAvailable(mContext)) {
            "已连接"
        } else {
            "未连接"
        }
        val netInfo: String = String.format(
            "WIFI状态: %s \n 数据状态: %s \n 网络连接状态: %s",
            wifiState,
            NetUtils.getCurrentNetMode(mContext),
            isConnect
        )

        tvShow.setText(netInfo)
        tvCleanName.setText(R.string.app_name)

        var pingResult: String = NetUtils.pingBaidu()
        if (TextUtils.isEmpty(pingResult)) {
            pingResult = "ping faild"
        }

        tvCleanName.setText(pingResult)
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
            sb.append("总内存： " + (memoryInfo.totalMem / 1024 / 1024) + "MB\n")
        }
        sb.append("内存是否过低：" + memoryInfo.lowMemory);

            KeyboardUtils.hideSoftInput(this)

        return sb.toString()
    }

    fun clearMem() {
        var cleanName: String = ""
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
