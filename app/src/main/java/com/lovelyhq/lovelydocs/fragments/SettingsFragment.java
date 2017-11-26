package com.lovelyhq.lovelydocs.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.lovelyhq.android.lovelydocs.R;
import com.lovelyhq.lovelydocs.helpers.PreferencesHelper;
import com.lovelyhq.lovelydocs.util.CacheDataManager;
import com.lovelyhq.lovelydocs.util.System;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;


public class SettingsFragment extends Fragment {

    private Context context;

    public static String APPID = "455eb6562cb6c6cf44d1f34f46baaaee";

    private System system=new System();

    private static boolean isnight=false ;
    private static boolean iswlan=false ;
    private static boolean isauto=false ;

    @BindView(R.id.information_update)
    TextView informationUpdate;
    @BindView(R.id.auto_update)
    TextView autoUpdate;
    @BindView(R.id.clear_cache)
    TextView clearCache;
    @BindView(R.id.dowm_local)
    TextView dowmLocal;
    @BindView(R.id.about_me)
    TextView aboutMe;
    @BindView(R.id.clear_cache_size)
    TextView clearCacheSize;
    @BindView(R.id.wlan_down)
    Switch wlanDown;
    @BindView(R.id.night_model)
    Switch nightModel;
    @BindView(R.id.logout)
    TextView logout;


/*
    CallBackValue callBackValue;
*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     * fragment与activity产生关联是  回调这个方法
     */
   /* @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        //当前fragment从activity重写了回调接口  得到接口的实例化对象
        callBackValue =(CallBackValue) getActivity();
    }*/

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
     // Bmob.initialize(getActivity(), APPID);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        try {
            clearCacheSize.setText(CacheDataManager.getTotalCacheSize(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        autoUpdate.setSaveEnabled(true);
        wlanDown.setSaveEnabled(true);
        nightModel.setSaveEnabled(true);

        return view;
    }

    public void autoData(){
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {

                Log.e("Setting", "onUpdateReturned: "+"safsdf");
                // TODO Auto-generated method stub
                if (updateStatus == UpdateStatus.Yes) {//版本有更新

                } else if (updateStatus == UpdateStatus.No) {
                    Toast.makeText(getActivity(), "版本无更新", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.EmptyField) {//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
                    Toast.makeText(getActivity(), "请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.IGNORED) {
                    Toast.makeText(getActivity(), "该版本已被忽略更新", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.ErrorSizeFormat) {
                    Toast.makeText(getActivity(), "请检查target_size填写的格式，请使用file.length()方法获取apk大小。", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.TimeOut) {
                    Toast.makeText(getActivity(), "查询出错或查询超时", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(context,"sada", Toast.LENGTH_SHORT).show();

            }
        });
        //发起自动更新
        BmobUpdateAgent.update(getActivity());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick({R.id.information_update, R.id.auto_update, R.id.wlan_down, R.id.clear_cache, R.id.night_model, R.id.dowm_local, R.id.about_me,R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.information_update:
                break;
            case R.id.auto_update:
                if (isauto) {
                    //关闭自动更新
                    Toast.makeText(getActivity(), getActivity().getApplicationContext().getString(R.string.NO_AUTO), Toast.LENGTH_SHORT).show();
                } else {
                    //开启自动更新
                    autoData();
                    Toast.makeText(getActivity(), getActivity().getApplicationContext().getString(R.string.YES_AUTO), Toast.LENGTH_SHORT).show();
                }
                isauto = !isauto;
                system.setIsauto(isauto);
                system.update("011016d342", new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            //修改成功后操作
                        }else{
                            //修改失败操作
                        }
                    }

                });
                break;
            case R.id.logout:
                BmobUser.logOut();
                break;
            case R.id.wlan_down:
                if (iswlan) {
                    BmobUpdateAgent.setUpdateOnlyWifi(false);
                    Toast.makeText(getActivity(), getActivity().getApplicationContext().getString(R.string.NO_WLAN), Toast.LENGTH_SHORT).show();
                } else {
                    BmobUpdateAgent.setUpdateOnlyWifi(true);
                    Toast.makeText(getActivity(), getActivity().getApplicationContext().getString(R.string.YES_WLAN), Toast.LENGTH_SHORT).show();
                }
                PreferencesHelper pre=new PreferencesHelper(getActivity());
                pre.setMobileEnabled(iswlan);
                iswlan = !iswlan;
                system.setIswlan(iswlan);
                system.update("011016d342", new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if(e==null){


                        }else{

                        }
                    }

                });
                break;
            case R.id.clear_cache:
                try {
                    clearCacheSize.setText(CacheDataManager.getTotalCacheSize(getActivity()));
                    new Thread(new clearCache()).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.night_model:
                isnight = !isnight;
                if (isnight) {
                    //callBackValue.SendMessageValue(true);
                    ((AppCompatActivity)getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(getActivity(),getActivity().getApplicationContext().getString(R.string.MODE_NIGHT_YES), Toast.LENGTH_SHORT).show();
                    getActivity().recreate();
                } else {
                    //callBackValue.SendMessageValue(false);
                    ((AppCompatActivity)getActivity()).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(getActivity(), getActivity().getApplicationContext().getString(R.string.MODE_NIGHT_NO), Toast.LENGTH_SHORT).show();
                    getActivity().recreate();
                }

                system.setIsnight(isnight);
                system.update("011016d342", new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if(e==null){

                        }else{

                        }
                    }

                });

                break;
            case R.id.dowm_local:
                break;
            case R.id.about_me:
                try {
                    String url = "https://wwww.baidu.com";
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), getActivity().getApplicationContext().getString(R.string.text_phone_has_no_browser), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    class clearCache implements Runnable {
        @RequiresApi(api = Build.VERSION_CODES.N)
        private Handler handler = new Handler() {

            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Toast.makeText(getActivity(), "清理完成", Toast.LENGTH_SHORT).show();
                        try {
                            clearCacheSize.setText(CacheDataManager.getTotalCacheSize(getActivity()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }

            ;

        };

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void run() {
            try {
                CacheDataManager.clearAllCache(getActivity());
                Thread.sleep(3000);
                if (CacheDataManager.getTotalCacheSize(getActivity()).startsWith("0")) {
                    handler.sendEmptyMessage(0);
                }
            } catch (Exception e) {
                return;
            }
        }
    }
    //定义一个回调接口
   /* public interface CallBackValue{
        public void SendMessageValue(boolean Value);
    }*/

}
