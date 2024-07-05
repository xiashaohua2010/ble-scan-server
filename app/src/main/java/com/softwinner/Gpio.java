package com.softwinner;

public class Gpio
{
	public static final String TAG = "GPIO";
    private Gpio()
    {
    }

	static {
		System.loadLibrary("gpio_jni");
		nativeInit();
	}

	private static native void nativeInit();
    private static native int nativeWriteGpio(String path, String value);
	private static native int nativeReadGpio(String path);

	private static final String  mPathstr      = "/sys/class/gpio_sw/P";
	private static final String  mDataName     = "/data";
	private static final String  mPullName     = "/pull";
	private static final String  mDrvLevelName = "/drv_level";
	private static final String  mMulSelName   = "/mul_sel";

	public static int writeGpio(char group, int num, int value){
	    String dataPath = composePinPath(group, num).concat(mDataName);

		return nativeWriteGpio(dataPath, Integer.toString(value));
	}

	public static int readGpio(char group, int num){
	    String dataPath = composePinPath(group, num).concat(mDataName);

		return nativeReadGpio(dataPath);
	}

	public static int setPull(char group, int num, int value){
	    String dataPath = composePinPath(group, num).concat(mPullName);

		return nativeWriteGpio(dataPath, Integer.toString(value));
	}

	public static int getPull(char group, int num){
	    String dataPath = composePinPath(group, num).concat(mPullName);

		return nativeReadGpio(dataPath);
	}

	public static int setDrvLevel(char group, int num, int value){
	    String dataPath = composePinPath(group, num).concat(mDrvLevelName);

		return nativeWriteGpio(dataPath, Integer.toString(value));
	}

	public static int getDrvLevel(char group, int num){
	    String dataPath = composePinPath(group, num).concat(mDrvLevelName);

		return nativeReadGpio(dataPath);
	}

	public static int setMulSel(char group, int num, int value){
	    String dataPath = composePinPath(group, num).concat(mMulSelName);

		return nativeWriteGpio(dataPath, Integer.toString(value));
	}

	public static int getMulSel(char group, int num){
	    String dataPath = composePinPath(group, num).concat(mMulSelName);

		return nativeReadGpio(dataPath);
	}

	private static String composePinPath(char group, int num){
		String  numstr;
		String  groupstr;

		groupstr = String.valueOf(group).toUpperCase();
		numstr = Integer.toString(num);
        return mPathstr.concat(groupstr).concat(numstr);
    }

}

