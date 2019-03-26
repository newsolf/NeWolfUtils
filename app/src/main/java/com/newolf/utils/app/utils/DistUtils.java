package com.newolf.utils.app.utils;

import android.content.Context;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.lang.reflect.Method;

/**
 * Created by v_anle on 2019/2/13.
 */
public class DistUtils {
    /**
     * 由于从Android 4.4版本开始，APP只能写外置SD卡的指定目录，故：
     * <li>对Android 4.0以上4.4以下的版本，所有存储路径都通过StorageManager.getVolumeList()获取；
     * <li>对Android 4.4及以上的版本，内置SD卡路径通过StorageManager.getVolumeList()获取，外置SD卡路径通过Context.getExternalFilesDirs()获取；
     * <li>对Android 4.4及以上的版本，还需要检测当前是否首选使用外置SD卡根目录（如/storage/extSdCard）且该目录有数据文件，如是则需要将其移动至新的目录（如/storage/extSdCard/Android/data/com.baidu.BaiduMap/files）。
     *
     * @param context
     */
//    @SuppressLint("NewApi")
//    @TargetApi(14)
    public static void getAllStoragesV14(Context context) {
        try {
            StorageManager manager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            LogUtils.e("manager = "+manager);
            Method getVolumeList = manager.getClass().getMethod("getVolumeList");
            LogUtils.e("getVolumeList = "+getVolumeList);
            Method getVolumeState = manager.getClass().getMethod("getVolumeState", String.class);
            LogUtils.e("getVolumeState = "+getVolumeState);
            Class<?> storageVolume = Class.forName("android.os.storage.StorageVolume");
            LogUtils.e("storageVolume = "+storageVolume);
            Method isRemovable = storageVolume.getMethod("isRemovable");
            LogUtils.e("isRemovable = "+isRemovable);
            Method getPath = storageVolume.getMethod("getPath");
            LogUtils.e("isRemovable = "+isRemovable);
            Object[] volumes = (Object[]) getVolumeList.invoke(manager);
            LogUtils.e("volumes = "+volumes);
          if (volumes!=null){
              for (Object volume : volumes) {
                  LogUtils.e("volume = "+volume);
                  String path = (String) getPath.invoke(volume);
                  LogUtils.e("path = "+path);
                  if(!TextUtils.isEmpty(path)){
                      File file = new File(path);
                      if (file.isDirectory()){
                          File[] files = file.listFiles();
                          String allNames = "";
                          for (File item : files) {
                              allNames = item.getName() + "\n";
                              LogUtils.e("item = " + item.getName() + "\tpath " +item.getAbsolutePath());
                          }
                          ToastUtils.showShort("item = " + allNames);
                      }
                  }

                  String volumeState = (String) getVolumeState.invoke(manager, path);
                  LogUtils.e("volumeState = "+volumeState);
              }
          }

        } catch (Exception ex) {
            LogUtils.e( "exception", ex);
        }
    }


    private static boolean isWritable(String path) {
        boolean isCanWrite = false;
        try {
            File testFolder = new File(path);
            if (!testFolder.exists()) {
                testFolder.mkdirs();
            }
            File testFile = new File(path + "/test.0");
            if (testFile.exists()) {
                testFile.delete();
            }
            isCanWrite = testFile.createNewFile();
            if (testFile.exists()) {
                testFile.delete();
            }
        } catch (Exception ex) {
            LogUtils.e("exception", ex);
        }
        return isCanWrite;
    }
}
