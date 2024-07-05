package com.rayman.ble.machine;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static com.rayman.ble.machine.NetWorkUtil.isLocationEnable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.rayman.ble.machine.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int DEVICE = 1024;

    private static final int REQUEST_ENABLE_BT = 10;
    private static final int REQUEST_ENABLE_LOCATION = 22;
    private static final int REQUEST_CODE_LOCATION_SETTINGS = 999;
    private BluetoothAdapter mBlueToothAdapter = BluetoothAdapter.getDefaultAdapter();

    private Toast mToast;
    private Intent intent = new Intent();

    private TextView mTv_one;
    public static Handler handler;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DEVICE:
                        mTv_one.append(msg.obj + "\n\n");
                        break;
                }
            }
        };
        mTv_one = (TextView) findViewById(R.id.tv_one);

        checkBlueToothPermission();

        mTv_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private List<String> mPermissionList = new ArrayList<>();

    private void checkBlueToothPermission() {
        /*
        if (Build.VERSION.SDK_INT >= 23) {
            Log.i(TAG, "系统版本为6.0");
            boolean hasLocationPermission =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            Log.i(TAG, "当前有Location权限吗？" + hasLocationPermission);
            if (!hasLocationPermission) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ENABLE_LOCATION);
            } else {
                if (isLocationEnable(this)) {
                    connectDevice();
                } else {
                    ShowToast("由于系统问题，请开启定位服务");
                    Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
                }
            }
        } else {
            connectDevice();
        }*
         */


        //动态申请是否有必要看sdk版本哈
        if (Build.VERSION.SDK_INT < 23){return;}

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            // Android 版本大于等于 Android12 时
            // 只包括蓝牙这部分的权限，其余的需要什么权限自己添加
            Log.i(TAG, "Build.VERSION_CODES.S");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                mPermissionList.add(Manifest.permission.BLUETOOTH_SCAN);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED)
                mPermissionList.add(Manifest.permission.BLUETOOTH_ADVERTISE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
                mPermissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            // Android 版本小于 Android12 及以下版本
            Log.i(TAG, "Build.VERSION_CODES.23");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                mPermissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                mPermissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            //if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED)
            //    mPermissionList.add(Manifest.permission.BLUETOOTH_ADVERTISE);
        }

        if(mPermissionList.size() > 0){
            Log.i(TAG, "Build.VERSION_CODES  need PERMISSION_GRANTED");
            ActivityCompat.requestPermissions(this,mPermissionList.toArray(new String[0]),REQUEST_ENABLE_LOCATION);
        }else{
            Log.i(TAG, "Build.VERSION_CODES  PERMISSION_GRANTED");
            connectDevice();
        }

    }

    private void connectDevice() {
        intent.setClass(MainActivity.this, BleService.class);

        Log.i(TAG, "connectDevice  mBlueToothAdapter="+mBlueToothAdapter);

        if (mBlueToothAdapter != null) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                if (mBlueToothAdapter.isEnabled()) {
                    startService(intent);
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            } else {
                ShowToast("该设备不支持BLE蓝牙");
            }
        } else {
            ShowToast("该设备不支持蓝牙");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == -1) {
                    //允许
                    ShowToast("蓝牙已启用");
                    //intent.putExtra("Order", Const.SCAN_START);
                    //startService(intent);
                } else {
                    ShowToast("蓝牙未启用");
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "onRequestPermissionsResult");
        if (requestCode == REQUEST_ENABLE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult 1"); //定位权限已经开启
                if (isLocationEnable(this)) {
                    connectDevice();
                } else {
                    ShowToast(" need location"); //由于系统问题，请开启定位服务
                    Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
                }
            } else {
                ShowToast("need location 2"); //请开启权限，否则无法使用开门功能
            }
        }
    }

    public void ShowToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}