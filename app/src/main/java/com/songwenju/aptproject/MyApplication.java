package com.songwenju.aptproject;

import android.app.Application;

/**
 * Created by TheShy on 2019/2/19 10:41.
 * Email:406262584@qq.com
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        HookUtil2 hookUtil = new HookUtil2(this);
        hookUtil.hookSystemHandler();
        hookUtil.hookAms();
    }
}
