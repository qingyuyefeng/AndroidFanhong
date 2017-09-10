package com.fanhong.cn;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fanhong.cn.applydoors.AddGuardActivity;
import com.fanhong.cn.listviews.MyFragmentPagerAdapter;
import com.fanhong.cn.view.AccesscontrolView1;
import com.fanhong.cn.community.CommunityIndexFragment;
import com.fanhong.cn.view.FixedViewPager;
import com.fanhong.cn.view.HomeView1;
import com.fanhong.cn.view.HomeView2;
import com.fanhong.cn.view.MineView1;
import com.fanhong.cn.view.ServiceView1;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imlib.RongIMClient;

public class FragmentMainActivity extends SampleActivity {
    private HomeView2 mTab01;
    private AccesscontrolView1 mTab02;
    private ServiceView1 mTab03;
    private MineView1 mTab04;
    private CommunityIndexFragment mTab05;
//    private LinearLayout welcome;
    /**
     * 底部五个按钮
     */
    private RadioGroup bottomRadioGroup;
    private RadioButton[] radioButtons = new RadioButton[5];
    private ImageView[] imageViews = new ImageView[5];
    /**
     * 用于对Fragment进行管理
     */
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private SampleConnection mSampleConnection;

    private SharedPreferences mSettingPref;
    //viewpager禁止滑动
    private FixedViewPager viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private int lastCheckedPage = R.id.home_page;

    public final static int FRAGMENT_HOMEVIEW = 1;
    public final static int FRAGMENT_SERVICE = 2;
    public final static int FRAGMENT_OPENDOOR = 3;
    public final static int FRAGMENT_COMMUNITY = 4;
    public final static int FRAGMENT_MINEVIEW = 6;
    public static int FORCE_FRAGMENT = 0;//当前选中的Fragment

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			//透明状态栏
//	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//	        //透明导航栏
//	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}
        welcome();
        initViews();

        if (!isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.nonetwork, Toast.LENGTH_SHORT).show();
        }

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        //setTabSelection(0);
        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        mSettingPref.edit().putInt("Status", 0).commit();
        int login = 0;
        try {
            login = mSettingPref.getInt("Remember", 0);
        } catch (Exception e) {
        }
        if (login == 1) {
            String name = mSettingPref.getString("Name", "");
            String psw = mSettingPref.getString("Password", "");
            if (name.length() > 0 && psw.length() > 0) {
                login(name, psw);
            }
        }
    }


    /* 判断联网状态
         * 连上之后判断是流量还是wifi
         */
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        } else {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    private void welcome() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.wellcome).setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void initViews() {
        bottomRadioGroup = (RadioGroup) findViewById(R.id.bottom_radiogroup);
        radioButtons[0] = (RadioButton) findViewById(R.id.home_page);
        radioButtons[1] = (RadioButton) findViewById(R.id.service_page);
        radioButtons[2] = (RadioButton) findViewById(R.id.door_page);
        radioButtons[3] = (RadioButton) findViewById(R.id.interact_page);
        radioButtons[4] = (RadioButton) findViewById(R.id.mine_page);
        imageViews[0] = (ImageView) findViewById(R.id.click_home);
        imageViews[1] = (ImageView) findViewById(R.id.click_serve);
        imageViews[2] = (ImageView) findViewById(R.id.click_entrance);
        imageViews[3] = (ImageView) findViewById(R.id.click_interaction);
        imageViews[4] = (ImageView) findViewById(R.id.click_user);


        viewPager = (FixedViewPager) findViewById(R.id.viewPager);
        initData();
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        //禁止滑动，没有滑动监听
        viewPager.setOffscreenPageLimit(4); //设置向左和向右都缓存limit个页面

        bottomRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.home_page:
//                        showFragment(fragmentTransaction,0);
                        viewPager.setCurrentItem(0);
                        radioButtons[0].setTextColor(getResources().getColor(R.color.skyblue));
                        radioButtons[1].setTextColor(getResources().getColor(R.color.notchecked));
                        radioButtons[2].setTextColor(getResources().getColor(R.color.notchecked));
                        radioButtons[3].setTextColor(getResources().getColor(R.color.notchecked));
                        radioButtons[4].setTextColor(getResources().getColor(R.color.notchecked));
                        imageViews[0].setVisibility(View.VISIBLE);
                        imageViews[1].setVisibility(View.INVISIBLE);
                        imageViews[2].setVisibility(View.INVISIBLE);
                        imageViews[3].setVisibility(View.INVISIBLE);
                        imageViews[4].setVisibility(View.INVISIBLE);
                        lastCheckedPage = R.id.home_page;
                        FORCE_FRAGMENT = FRAGMENT_HOMEVIEW;
                        break;
                    case R.id.service_page:
//                        showFragment(fragmentTransaction,1);
                        viewPager.setCurrentItem(1);
                        radioButtons[0].setTextColor(getResources().getColor(R.color.notchecked));
                        radioButtons[1].setTextColor(getResources().getColor(R.color.skyblue));
                        radioButtons[2].setTextColor(getResources().getColor(R.color.notchecked));
                        radioButtons[3].setTextColor(getResources().getColor(R.color.notchecked));
                        radioButtons[4].setTextColor(getResources().getColor(R.color.notchecked));
                        imageViews[0].setVisibility(View.INVISIBLE);
                        imageViews[1].setVisibility(View.VISIBLE);
                        imageViews[2].setVisibility(View.INVISIBLE);
                        imageViews[3].setVisibility(View.INVISIBLE);
                        imageViews[4].setVisibility(View.INVISIBLE);
                        lastCheckedPage = R.id.service_page;
                        FORCE_FRAGMENT = FRAGMENT_SERVICE;
                        break;
                    case R.id.door_page:
//                        showFragment(fragmentTransaction,2);
                        if (isLogined() == 1) {
                            mTab02.onResume();
                            Log.i("xq", "门禁list.size()==>" + mTab02.list.size());
                            if (mTab02.list.size() == 0) {
                                createDialog(2);
                            }
                            viewPager.setCurrentItem(2);
                            radioButtons[0].setTextColor(getResources().getColor(R.color.notchecked));
                            radioButtons[1].setTextColor(getResources().getColor(R.color.notchecked));
                            radioButtons[2].setTextColor(getResources().getColor(R.color.skyblue));
                            radioButtons[3].setTextColor(getResources().getColor(R.color.notchecked));
                            radioButtons[4].setTextColor(getResources().getColor(R.color.notchecked));
                            imageViews[0].setVisibility(View.INVISIBLE);
                            imageViews[1].setVisibility(View.INVISIBLE);
                            imageViews[2].setVisibility(View.VISIBLE);
                            imageViews[3].setVisibility(View.INVISIBLE);
                            imageViews[4].setVisibility(View.INVISIBLE);
                            lastCheckedPage = R.id.door_page;
                            FORCE_FRAGMENT = FRAGMENT_OPENDOOR;
                        } else {
                            createDialog(0);
                            bottomRadioGroup.check(lastCheckedPage);
                        }

                        break;
                    case R.id.interact_page:
//                        showFragment(fragmentTransaction,3);
                        if (isLogined() == 1) {
                            try {
                                if (!TextUtils.isEmpty(mSettingPref.getString("gardenName", ""))) {
                                    viewPager.setCurrentItem(3);
                                    radioButtons[0].setTextColor(getResources().getColor(R.color.notchecked));
                                    radioButtons[1].setTextColor(getResources().getColor(R.color.notchecked));
                                    radioButtons[2].setTextColor(getResources().getColor(R.color.notchecked));
                                    radioButtons[3].setTextColor(getResources().getColor(R.color.skyblue));
                                    radioButtons[4].setTextColor(getResources().getColor(R.color.notchecked));
                                    imageViews[0].setVisibility(View.INVISIBLE);
                                    imageViews[1].setVisibility(View.INVISIBLE);
                                    imageViews[2].setVisibility(View.INVISIBLE);
                                    imageViews[3].setVisibility(View.VISIBLE);
                                    imageViews[4].setVisibility(View.INVISIBLE);
                                    lastCheckedPage = R.id.interact_page;
                                    FORCE_FRAGMENT = FRAGMENT_COMMUNITY;
                                } else {
                                    createDialog(1);
                                    bottomRadioGroup.check(lastCheckedPage);
                                }
                            } catch (Exception e) {
                                createDialog(1);
                                bottomRadioGroup.check(lastCheckedPage);
                            }
                        } else {
//                            createDialog(0);
                            Toast.makeText(FragmentMainActivity.this, R.string.pleaselogin, Toast.LENGTH_SHORT).show();
                            bottomRadioGroup.check(lastCheckedPage);
                        }
                        break;
                    case R.id.mine_page:
                        viewPager.setCurrentItem(4);
                        radioButtons[0].setTextColor(getResources().getColor(R.color.notchecked));
                        radioButtons[1].setTextColor(getResources().getColor(R.color.notchecked));
                        radioButtons[2].setTextColor(getResources().getColor(R.color.notchecked));
                        radioButtons[3].setTextColor(getResources().getColor(R.color.notchecked));
                        radioButtons[4].setTextColor(getResources().getColor(R.color.skyblue));
                        imageViews[0].setVisibility(View.INVISIBLE);
                        imageViews[1].setVisibility(View.INVISIBLE);
                        imageViews[2].setVisibility(View.INVISIBLE);
                        imageViews[3].setVisibility(View.INVISIBLE);
                        imageViews[4].setVisibility(View.VISIBLE);
                        lastCheckedPage = R.id.mine_page;
                        FORCE_FRAGMENT = FRAGMENT_MINEVIEW;
                        break;
                }
            }
        });
    }


    private void initData() {
        mTab01 = new HomeView2();
        mTab02 = new AccesscontrolView1();
        mTab03 = new ServiceView1();
        mTab04 = new MineView1();
        mTab05 = new CommunityIndexFragment();

        fragments.add(mTab01);
        fragments.add(mTab03);
        fragments.add(mTab02);
        fragments.add(mTab05);
        fragments.add(mTab04);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSampleConnection != null) {
            mSampleConnection.close();
        }
    }

    private long lastPressBackKeyTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPressBackKeyTime < 2000) {
                RongIMClient.getInstance().disconnect();
                finish();
            } else {
                Toast.makeText(this, R.string.exit_app_tip, Toast.LENGTH_LONG).show();
                lastPressBackKeyTime = currentTime;
            }
            return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    public void setRadioButtonsChecked(int number) {
        if (number < radioButtons.length) {
            radioButtons[number].setChecked(true);
        }
    }

    public synchronized void connectFail(int type) {
        SampleConnection.USER = "";
        SampleConnection.USER_STATE = 0;
    }

    public synchronized void connectSuccess(JSONObject json, int type) {
        int cmd = -1;
        int result = -1;
        String name = "";
        String str;
        String nick = "";
        String logo = "";
        try {
            str = json.getString("cmd");
            cmd = Integer.parseInt(str);
            str = json.getString("cw");
            result = Integer.parseInt(str);
            try {
                name = json.getString("name");
            } catch (Exception e) {
            }
            try {
                nick = json.getString("user");
            } catch (Exception e) {
            }
            try {
                logo = json.getString("logo");
            } catch (Exception e) {
            }
        } catch (Exception e) {
            connectFail(type);
            return;
        }
        if (cmd == 6 && result == 0) {
            SampleConnection.USER = name;
            SampleConnection.ALIAS = nick;
            SampleConnection.USER_STATE = 1;
            SampleConnection.LOGO_URL = logo;
            mSettingPref.edit().putInt("Status", 1).commit();
            mSettingPref.edit().putString("Name", name).commit();
            mSettingPref.edit().putString("Nick", nick).commit();
            mTab04.setFragment(1, 1);
            getAccessControl();
        } else if (cmd == 44) {//主页最新公告展示
            mTab01.setFragment(43, json.toString());
        } else {
            connectFail(type);
        }
    }

    private void login(String name, String psw) {
        SampleConnection.PASSWORD = psw;
        SampleConnection.USER = name;
        if (mSampleConnection == null)
            mSampleConnection = new SampleConnection(this, 0);
        mSampleConnection.connectService1(genAccount(name, psw));
    }

    private Map<String, Object> genAccount(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cmd", "5");
        map.put("name", username);
        map.put("password", password);
        return map;
    }

    private int isLogined() {
        int result = 0;
        try {
            result = mSettingPref.getInt("Status", 0);
        } catch (Exception e) {
        }
        return result;
    }

    public void getAccessControl() {
        if (isLogined() == 1) {
            String userid = mSettingPref.getString("UserId", "");
            RequestParams params = new RequestParams(App.CMDURL);
            params.addBodyParameter("cmd", "41");
            params.addBodyParameter("uid", userid);
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String s) {
                    mTab02.setFragment(21, s);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:          /* 确认返回 */
                Intent intent = new Intent();
                this.setResult(RESULT_OK, intent);
                this.finish();
                break;
            case 21:   //登录返回
                if (mTab04 != null) {
                    int status = mSettingPref.getInt("Status", 0);
                    mTab04.setFragment(1, status);
                }
                break;
            case 51:  //选择小区返回
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String name = bundle.getString("gardenName");
                    String id = bundle.getString("gardenId");
                    String property = bundle.getString("gardenProperty");
                    // tv_garden.setText(name);
                    if (name != null && name.length() > 0) {
                        mSettingPref.edit().putString("gardenName", name).commit();
                        mSettingPref.edit().putString("gardenId", id).commit();
                        mSettingPref.edit().putString("gardenProperty", property).commit();
                        mTab01.setFragment(11, name);
                        mTab05.setFragment(name);
                    }
                }
                break;
//            case 112:
//                String location = data.getStringExtra("location");
//                Toast.makeText(this,location+" ",Toast.LENGTH_SHORT).show();
//                if(location!=null && location.length()>0){
//                    mTab01.setFragment(11, location);
//                }
//                break;
            case 555:        //注销登录的回调
                mTab01.setFragment(11, mSettingPref.getString("gardenName", ""));
                mTab05.setFragment(mSettingPref.getString("gardenName", ""));
                mTab02.setFragment(21, "");
                mTab04.setFragment(1, 0);
        }
    }

    //设定listview的高度
    public void setListViewHeight(ListView listView) {
        BaseAdapter listAdapter = (BaseAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mTab01.callManager();
            } else
                Toast.makeText(this, "需要通话权限", Toast.LENGTH_SHORT).show();
    }

    private void createDialog(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (i) {
            case 0:
                builder.setTitle("你还没有登录哦");
                builder.setMessage("是否立即登录？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(FragmentMainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
                break;
            case 1:
                builder.setTitle("你还没选择小区");
                builder.setMessage("是否立即去选择小区？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(FragmentMainActivity.this, GardenSelecterActivity.class);
                        startActivityForResult(intent, 12);
                    }
                });
                break;
            case 2:
                builder.setTitle("提示");
                builder.setMessage("你还没有绑定钥匙，\n快去绑定吧！");
                builder.setPositiveButton("去绑定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(FragmentMainActivity.this, AddGuardActivity.class);
                        startActivity(intent);
                    }
                });
                break;
        }
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
