package com.softwinner;

import android.util.Log;

public class Lock {
    private static boolean isOpen=false;
    private static int point=14;    //初始端口
    private static int status=1;    //初始闭锁状态
    private static int close=0;    //开锁状态

    public static void initlock(){
        Gpio.writeGpio('H',point,status);
    }

    public static void initlock( int p,int s ,int c){
        point=p;
        status=s;
        close=c;
        Gpio.writeGpio('H',point,status);
    }

    public static void openLock(){
        if(!isOpen){
            new Thread(){
                @Override
                public void run() {
                    try {
                        Gpio.writeGpio('H', point, close);
                        Log.e("control","开锁。");
                        isOpen = true;
                        Thread.sleep(10000);
                        Gpio.writeGpio('H',point,status);
                        Log.e("control","关锁。");
                        isOpen=false;
                    }catch (Exception e){
                        Log.e("unlock error","开锁失败。");
                        isOpen=false;
                    }
                }
            }.start();
        }
    }
}
