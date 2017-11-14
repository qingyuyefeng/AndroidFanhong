package com.fanhong.cn.party;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.R;
import com.fanhong.cn.listviews.MyFragmentPagerAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/3.
 */

@ContentView(R.layout.activity_party_main)
public class ViewPagerActivity extends FragmentActivity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.top_extra)
    private TextView extra;
    @ViewInject(R.id.party_viewpager)
    private ViewPager viewPager;

    private List<Fragment> fragmentList = new ArrayList<>();

    /**
     * 四个Fragment
     */
    private Fragmentfx fragmentfx; //分享
    private Fragmentlt fragmentlt; //论坛
    private Fragmentcy fragmentcy; //参与
    private Fragmentwd fragmentwd; //我的

    /**
     * 底部四个radiobutton
     */
    private RadioGroup radioGroup;
    private RadioButton[] radioButtons = new RadioButton[4];
    private TextView[] textViews = new TextView[4];
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        title.setText(R.string.fx);
        extra.setVisibility(View.VISIBLE);
        initViews();
    }
    @SuppressLint("WrongViewCast")
    private void initViews(){
//        extra.setVisibility(View.VISIBLE);
        radioGroup = findViewById(R.id.bottom_group);
        radioButtons[0] = findViewById(R.id.radiogroup_fx);
        radioButtons[1] = findViewById(R.id.radiogroup_lt);
        radioButtons[2] = findViewById(R.id.radiogroup_cy);
        radioButtons[3] = findViewById(R.id.radiogroup_wd);
        textViews[0] = findViewById(R.id.bottom_fx);
        textViews[1] = findViewById(R.id.bottom_lt);
        textViews[2] = findViewById(R.id.bottom_cy);
        textViews[3] = findViewById(R.id.bottom_wd);
        radioButtons[0].setChecked(true);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                extra.setVisibility(View.INVISIBLE);
                textViews[0].setBackgroundColor(getResources().getColor(R.color.white));
                textViews[1].setBackgroundColor(getResources().getColor(R.color.white));
                textViews[2].setBackgroundColor(getResources().getColor(R.color.white));
                textViews[3].setBackgroundColor(getResources().getColor(R.color.white));
                switch (checkedId){
                    case R.id.radiogroup_fx:
                        extra.setVisibility(View.VISIBLE);
                        title.setText(R.string.fx);
                        textViews[0].setBackgroundColor(getResources().getColor(R.color.skyblue));
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.radiogroup_lt:
                        title.setText(R.string.lt);
                        textViews[1].setBackgroundColor(getResources().getColor(R.color.skyblue));
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.radiogroup_cy:
                        title.setText(R.string.cy);
                        textViews[2].setBackgroundColor(getResources().getColor(R.color.skyblue));
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.radiogroup_wd:
                        title.setText(R.string.wd);
                        textViews[3].setBackgroundColor(getResources().getColor(R.color.skyblue));
                        viewPager.setCurrentItem(3);
                        break;
                }
            }
        });
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getFragmentManager(),getFragList());
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                /**
                 * 滚动中  位置偏移  像素位置偏移
                 */
            }

            @Override
            public void onPageSelected(int position) {
                /**
                 * 选定页面
                 */
                radioButtons[position].setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                /**
                 * 页面滚动状态改变
                 */
            }
        });
        viewPager.setOffscreenPageLimit(3);
    }
    private List<Fragment> getFragList(){
        fragmentfx = new Fragmentfx();
        fragmentList.add(fragmentfx);
        fragmentlt = new Fragmentlt();
        fragmentList.add(fragmentlt);
        fragmentcy = new Fragmentcy();
        fragmentList.add(fragmentcy);
        fragmentwd = new Fragmentwd();
        fragmentList.add(fragmentwd);

        return fragmentList;
    }
    @Event({R.id.img_back,R.id.top_extra})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.top_extra:
                Toast.makeText(this,"签到成功!",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}