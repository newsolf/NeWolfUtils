package com.newolf.utils.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;
import com.newolf.utils.MainActivity;
import com.newolf.utils.R;

import java.util.*;

/**
 * Created by lyl on 2017/8/23.
 */

public class WidgetProvider extends AppWidgetProvider {

    // 更新 widget 的广播对应的action
    private final String ACTION_UPDATE_ALL = "com.lyl.widget.UPDATE_ALL";
    // 保存 widget 的id的HashSet，每新建一个 widget 都会为该 widget 分配一个 id。
    private static Set idsSet = new HashSet();

    public static int mIndex;

    /**
     * 接收窗口小部件点击时发送的广播
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();

        if (ACTION_UPDATE_ALL.equals(action)) {
            // “更新”广播
            updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
        } else if (intent.hasCategory(Intent.CATEGORY_ALTERNATIVE)) {
            // “按钮点击”广播
            mIndex = 0;
            updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
        }
    }

    // 更新所有的 widget
    private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, Set set) {
        // widget 的id
        int appID;
        // 迭代器，用于遍历所有保存的widget的id
        Iterator it = set.iterator();

        // 要显示的那个数字，每更新一次 + 1
        mIndex++; // TODO:可以在这里做更多的逻辑操作，比如：数据处理、网络请求等。然后去显示数据

        while (it.hasNext()) {
            appID = ((Integer) it.next()).intValue();

            // 获取 example_appwidget.xml 对应的RemoteViews
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.app_widget);

            // 设置显示数字
            remoteView.setTextViewText(R.id.widget_txt, String.valueOf(mIndex));

            // 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
            remoteView.setOnClickPendingIntent(R.id.widget_btn_reset, getResetPendingIntent(context));
            remoteView.setOnClickPendingIntent(R.id.widget_btn_open, getOpenPendingIntent(context));

            // 更新 widget
            appWidgetManager.updateAppWidget(appID, remoteView);
        }
    }

    /**
     * 获取 重置数字的广播
     */
    private PendingIntent getResetPendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, WidgetProvider.class);
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }

    /**
     * 获取 打开 MainActivity 的 PendingIntent
     */
    private PendingIntent getOpenPendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.putExtra("main", "这句话是我从桌面点开传过去的。");
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        return pi;
    }

    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法，可添加多次但只第一次调用
     */
    @Override
    public void onEnabled(Context context) {
        // 在第一个 widget 被创建时，开启服务
        Intent intent = new Intent(context, WidgetService.class);
        context.startService(intent);
        Toast.makeText(context, "开始计数", Toast.LENGTH_SHORT).show();
        super.onEnabled(context);
    }

    // 当 widget 被初次添加 或者 当 widget 的大小被改变时，被调用
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle
            newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    /**
     * 当小部件从备份恢复时调用该方法
     */
    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    /**
     * 每次窗口小部件被点击更新都调用一次该方法
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // 每次 widget 被创建时，对应的将widget的id添加到set中
        for (int appWidgetId : appWidgetIds) {
            idsSet.add(Integer.valueOf(appWidgetId));
        }
    }

    /**
     * 每删除一次窗口小部件就调用一次
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // 当 widget 被删除时，对应的删除set中保存的widget的id
        for (int appWidgetId : appWidgetIds) {
            idsSet.remove(Integer.valueOf(appWidgetId));
        }
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 当最后一个该窗口小部件删除时调用该方法，注意是最后一个
     */
    @Override
    public void onDisabled(Context context) {
        // 在最后一个 widget 被删除时，终止服务
        Intent intent = new Intent(context, WidgetService.class);
        context.stopService(intent);
        super.onDisabled(context);
    }

    /**
     * 控制 桌面小部件 更新
     * Created by lyl on 2017/8/23.
     */
    public static class WidgetService extends Service {

        // 更新 widget 的广播对应的 action
        private final String ACTION_UPDATE_ALL = "com.lyl.widget.UPDATE_ALL";
        // 周期性更新 widget 的周期
        private static final int UPDATE_TIME = 1000;

        private Timer mTimer;
        private TimerTask mTimerTask;


        @Override
        public void onCreate() {
            super.onCreate();

            // 每经过指定时间，发送一次广播
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Intent updateIntent = new Intent(ACTION_UPDATE_ALL);
                    sendBroadcast(updateIntent);
                }
            };
            mTimer.schedule(mTimerTask, 1000, UPDATE_TIME);
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mTimerTask.cancel();
            mTimer.cancel();
        }

        /*
         *  服务开始时，即调用startService()时，onStartCommand()被执行。
         *
         *  这个整形可以有四个返回值：start_sticky、start_no_sticky、START_REDELIVER_INTENT、START_STICKY_COMPATIBILITY。
         *  它们的含义分别是：
         *  1):START_STICKY：如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象。随后系统会尝试重新创建service，
         *     由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null;
         *  2):START_NOT_STICKY：“非粘性的”。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统不会自动重启该服务;
         *  3):START_REDELIVER_INTENT：重传Intent。使用这个返回值时，如果在执行完onStartCommand后，服务被异常kill掉，系统会自动重启该服务，并将Intent的值传入;
         *  4):START_STICKY_COMPATIBILITY：START_STICKY的兼容版本，但不保证服务被kill后一定能重启。
         */
        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            super.onStartCommand(intent, flags, startId);
            return START_STICKY;
        }
    }
}
