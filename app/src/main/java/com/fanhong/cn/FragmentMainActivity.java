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
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fanhong.cn.listviews.MyFragmentPagerAdapter;
import com.fanhong.cn.view.AccesscontrolView1;
import com.fanhong.cn.view.FixedViewPager;
import com.fanhong.cn.view.HomeView1;
import com.fanhong.cn.view.CommunityIndexFragment;
import com.fanhong.cn.view.MineView1;
import com.fanhong.cn.view.ServiceView1;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fanhong.cn.R.id.mine_page;

public class FragmentMainActivity extends SampleActivity {
    private HomeView1 mTab01;
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
        wellcom();
        initViews();
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

    private void wellcom() {
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
        radioButtons[4] = (RadioButton) findViewById(mine_page);
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
                        break;
                    case R.id.door_page:
//                        showFragment(fragmentTransaction,2);
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
                                } else {
                                    createDialog(1);
                                    bottomRadioGroup.check(lastCheckedPage);
                                }
                            } catch (Exception e) {
                                createDialog(1);
                                bottomRadioGroup.check(lastCheckedPage);
                            }
                        } else {
                            createDialog(0);
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
                        break;
                }
            }
        });
    }


    private void initData() {
        mTab01 = new HomeView1();
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
                finish();
            } else {
                Toast.makeText(this, R.string.exit_app_tip, Toast.LENGTH_LONG).show();
                lastPressBackKeyTime = currentTime;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setRadioButtonsChecked(int number){
        if(number < radioButtons.length){
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
            Log.i("xq", "登录成功====>" + json.toString());
            SampleConnection.USER = name;
            SampleConnection.ALIAS = nick;
            SampleConnection.USER_STATE = 1;
            SampleConnection.LOGO_URL = logo;
            mSettingPref.edit().putInt("Status", 1).commit();
            mSettingPref.edit().putString("Name", name).commit();
            mSettingPref.edit().putString("Nick", nick).commit();
            mTab04.setFragment(1,1);
            getAccessControl();
        } else if (cmd == 42) {
            if (result == 0) {
//                setDoorsKey(json);
                mTab02.setFragment(21, json.toString());
            } else {
                Toast.makeText(this, "门禁钥匙数据异常", Toast.LENGTH_SHORT).show();
            }
        } else {
            connectFail(type);
        }
        if(type == 37){
            mTab01.setFragment(37,json.toString());
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
            Log.i("menjin", "userid===>" + userid);
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("cmd", "41");
            map.put("uid", userid);
            if (mSampleConnection == null)
                mSampleConnection = new SampleConnection(this, 0);
            mSampleConnection.connectService1(map);
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
                        mTab03.setFragment(11, name);
                        mTab05.setFragment(name);
                    }
                }
                break;
            case 24:   //添加小区门禁成功后返回
                getAccessControl();
                break;
            case 555:        //注销登录的回调
                mTab01.setFragment(11, mSettingPref.getString("gardenName", ""));
                mTab02.setFragment(21, "");
                mTab03.setFragment(11, mSettingPref.getString("gardenName", ""));
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
    protected void onResume() {
        super.onResume();
    }

    public void hiddenBottom(boolean hidden) {
        if (hidden) {
            this.bottomRadioGroup.setVisibility(View.GONE);
            if (this.imageViews[3].getVisibility() == View.VISIBLE)
                this.imageViews[3].setVisibility(View.GONE);
        } else {
            this.bottomRadioGroup.setVisibility(View.VISIBLE);
            if (this.imageViews[3].getVisibility() == View.GONE)
                this.imageViews[3].setVisibility(View.VISIBLE);
        }
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
