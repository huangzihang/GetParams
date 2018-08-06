package com.zeham.getparams;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.zeham.getparams.utils.GetSystemInfoUtil;
import com.zeham.getparams.utils.PhoneInfoUtils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LOCATION";
    private ToggleButton toButton;
    private WifiManager wifiManager;

    TextView mTextValue3;

    TextView mTvPhoneSn;
    TextView mTvPhoneMeid;
    TextView mTvPhoneImei;
    TextView mTvPhoneOtherImei;
    TextView mTvVersionNumber;
    TextView mTvPhoneModels;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化Button按钮,打开关闭WiFi开关
        init();
        //显示内网IP
        showIP();
        //显示安卓ID
        showAndroidID();
        //显示UserAgent
        showUserAgent();
        //获取IMEI1,IMEI2,MEID
        getSystemInfo();
        //获取手机号码等SIM卡信息
        TextView mPhoneInfo = (TextView) findViewById(R.id.txt_value9);
        mPhoneInfo.setText("PhoneInfo-1 " + PhoneInfoUtils.getPhoneInfo(getApplication()));

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getSystemInfo(){
        //mTvPhoneSn = (TextView) findViewById(R.id.tv_phone_sn);
        mTvPhoneMeid = (TextView) findViewById(R.id.txt_value6);
        mTvPhoneImei = (TextView) findViewById(R.id.txt_value7);
        mTvPhoneOtherImei = (TextView) findViewById(R.id.txt_value8);
        //mTvVersionNumber = (TextView) findViewById(R.id.tv_version_number);
        //mTvPhoneModels = (TextView) findViewById(R.id.tv_phone_models);


        if (Build.VERSION.SDK_INT < 21) {
            //如果获取系统的IMEI/MEID，14位代表meid 15位是imei
            if (GetSystemInfoUtil.getNumber(getApplication()) == 14) {
                mTvPhoneMeid.setText("MEID-1 " + GetSystemInfoUtil.getImeiOrMeid(getApplication()));//meid
                mTvPhoneImei.setText("IMEI-1 ");
                mTvPhoneOtherImei.setText("IMEI-2 ");

            } else if (GetSystemInfoUtil.getNumber(getApplication()) == 15) {
                mTvPhoneImei.setText("IMEI-1 " + GetSystemInfoUtil.getImeiOrMeid(getApplication()));//imei1
                mTvPhoneMeid.setText("MEID-1 ");
                mTvPhoneOtherImei.setText("IMEI-2 ");
            }
            // 21版本是5.0，判断是否是5.0以上的系统  5.0系统直接获取IMEI1,IMEI2,MEID
        } else if (Build.VERSION.SDK_INT >= 21) {
            Map<String, String> map = GetSystemInfoUtil.getIMEII(getApplication());
            Map<String, String> mapMeid = GetSystemInfoUtil.getImeiAndMeid(getApplication());
            mTvPhoneImei.setText(map.get("IMEI-1 "));//imei1
            if (map.get("IMEI2") == null || ("null").equals(map.get("IMEI2"))) {
                mTvPhoneOtherImei.setText("IMEI-2 ");//imei2
            } else {
                mTvPhoneOtherImei.setText("IMEI-2 " + map.get("IMEI2"));//imei2
            }
            mTvPhoneMeid.setText("MEID-1 " + mapMeid.get("meid"));//meid
        }
        //mTvPhoneSn.setText(GetSystemInfoUtil.getSn(getApplication()));//SN
        //mTvPhoneModels.setText(GetSystemInfoUtil.getSystemModel());//手机型号 PRO6
        //mTvVersionNumber.setText(GetSystemInfoUtil.getSystemVersion());//软件版本号  FLYME 6.02
    }

    public void showUserAgent(){
        WebView mWebView = (WebView) findViewById(R.id.id_wv_ua);
        String userAgent = mWebView.getSettings().getUserAgentString();

        TextView mTextValue5 = (TextView)findViewById(R.id.txt_value5);
        mTextValue5.setText("UserAgent-1 " + userAgent);
    }

    public void showAndroidID(){
        String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);
        Log.i("ANDROID_ID: ", ANDROID_ID);
/*        String SerialNumber = android.os.Build.SERIAL;
        Log.i("SerialNumber: ", SerialNumber);*/
        TextView mTextValue4 = (TextView)findViewById(R.id.txt_value4);
        mTextValue4.setText("安卓ID " + ANDROID_ID);
    }

    public void showIP(){
        TextView mTextValue3 = (TextView)findViewById(R.id.txt_value3);
        mTextValue3.setText(getHostIP());
    }

    /**
     * 获取ip地址
     * @return
     */
    public String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("IP", "SocketException");
            e.printStackTrace();
        }
        return ("内网IP " + hostIp);
    }


    public void init(){
        toButton = (ToggleButton) findViewById(R.id.toggleButton);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if ((wifiManager.isWifiEnabled())){
            toButton.setChecked(false);
        }else {
            toButton.setChecked(true);
        }
        toButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    wifiManager.setWifiEnabled(true);
                }else{
                    wifiManager.setWifiEnabled(false);
                }
            }
        });
    }
}
