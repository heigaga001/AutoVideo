package com.liu.autovideo;


import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

public class AutoVideoService extends AccessibilityService {

    static final String TAG = "AutoVideo";

    Handler handler = new Handler();

    String nickname = null;
    int time = 30;
    long lastTime = 0l;
    long action = 0l;

    /** 微信的包名*/
    static final String WECHAT_PACKAGENAME = "com.tencent.mm";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        long current = System.currentTimeMillis();
        Log.d(TAG,(current-lastTime)+"");
        String className1 = (String)event.getClassName();
        Log.d(TAG,className1);
        if(nickname==null||current-lastTime<time*60*1000){
            return;
        }
        final int eventType = event.getEventType();
        if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String className = (String)event.getClassName();
            Log.d(TAG,className);
            if("com.tencent.mm.ui.chatting.ChattingUI".equals(className)||"com.tencent.mm.ui.LauncherUI".equals(className)){
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ko");
                List<AccessibilityNodeInfo> more = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/amo");
                List<AccessibilityNodeInfo> last = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/l_");
                Log.d(TAG,"====="+list.size()+"::"+more.size()+":"+last.size());
                if(last.size()>3){
                    last.get(2).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    action = System.currentTimeMillis();
                    return;
                }
                if(list.size()==0){
                    return;
                }
                for(AccessibilityNodeInfo n : list) {
                    Log.d(TAG,"====="+n.getText().toString());
                    if(nickname.equals(n.getText().toString())&&more.size()>0){
                        more.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AccessibilityNodeInfo nodeInfo = AutoVideoService.this.getRootInActiveWindow();
                                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ko");
                                List<AccessibilityNodeInfo> more = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/amo");
                                List<AccessibilityNodeInfo> video = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/xi");
                                List<AccessibilityNodeInfo> last = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/xh");
                                Log.d(TAG,"====="+list.size()+"::"+more.size()+":"+video.size()+":"+last.size());
                                if(last.size()>3){
                                    last.get(2).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    action = System.currentTimeMillis();
                                }
                            }
                        },300);
                        break;
                    }
                }
            }else if("android.support.design.widget.c".equals(className)){
                if(System.currentTimeMillis()-action<30*1000){
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
//                    List<AccessibilityNodeInfo> last = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/jb");
                    List<AccessibilityNodeInfo> last = nodeInfo.findAccessibilityNodeInfosByText("视频通话");
                    Log.d(TAG,"====="+":"+last.size());
                    if(last.size()>0){
                        lastTime = System.currentTimeMillis();
                        SharedPreferences sp = getSharedPreferences("com.liu",Context.MODE_PRIVATE);
                        sp.edit().putLong("last",lastTime).commit();
                        last.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);


                    }
                }

            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sp = getSharedPreferences("com.liu",Context.MODE_PRIVATE);
        nickname = sp.getString("nickname",null);
        time = sp.getInt("time",30);
        lastTime = sp.getLong("last",0l);
        Log.d(TAG,"oncreate:"+nickname+":::"+time);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences sp = getSharedPreferences("com.liu",Context.MODE_PRIVATE);
        nickname = sp.getString("nickname",null);
        time = sp.getInt("time",30);
        Log.d(TAG,"onStart::"+nickname+":::"+time);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onInterrupt() {
        Toast.makeText(this, "中断自动视频服务", Toast.LENGTH_SHORT).show();
    }
}
