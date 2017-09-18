package com.fanhong.cn.fenxiao;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.fanhong.cn.R;
import com.fanhong.cn.expressage.CheckExpressActivity;
import com.fanhong.cn.listviews.SpinerPopWindow;
import com.fanhong.cn.util.TopBarUtil;
import com.fanhong.cn.util.ZYStatusBarUtil;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/16.
 */
@ContentView(R.layout.activity_fenxiao_joined)
public class CheckJoinedActivity extends Activity{
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.tv_year)
    private TextView tvYear;
    @ViewInject(R.id.tv_month)
    private TextView tvMonth;
    @ViewInject(R.id.tv_this_month)
    private TextView thisMonth;
    @ViewInject(R.id.hetong_number)
    private TextView hNumber;
    @ViewInject(R.id.true_number)
    private TextView tNumber;
    @ViewInject(R.id.money_amount)
    private TextView mAmount;

    private SpinerPopWindow<Integer> ssp;
    private List<Integer> years = new ArrayList<>();
    private List<Integer> months = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
//        ZYStatusBarUtil.setStatusBarColor(this,getColor(R.color.colorPrimary));
//        ZYStatusBarUtil.translucentStatusBar(this,true);
//        TopBarUtil.initStatusBar(this);
        title.setText("招募统计");
    }
    @Event({R.id.img_back,R.id.tv_year,R.id.tv_month})
    private void onClick(View v){
        switch (v.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.tv_year:
                ssp = new SpinerPopWindow<>(CheckJoinedActivity.this, getYears(), new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        tvYear.setText(years.get(position)+"年");
                        ssp.dismiss();
                    }
                },"年");
                ssp.setWidth(tvYear.getWidth());
                ssp.showAsDropDown(tvYear,0,0);
                break;
            case R.id.tv_month:
                ssp = new SpinerPopWindow<>(CheckJoinedActivity.this, getMonths(), new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        tvMonth.setText(months.get(position)+"月");
                        thisMonth.setText(months.get(position)+"月");
                        ssp.dismiss();
                    }
                },"月");
                ssp.setWidth(tvMonth.getWidth());
                ssp.showAsDropDown(tvMonth,0,0);
                break;
        }
    }
    private List<Integer> getMonths(){
        months.clear();
        for(int i=0;i<12;i++){
            months.add(i+1);
        }
        return months;
    }
    private List<Integer> getYears(){
        years.clear();
        for(int i=2017;i<2031;i++){
            years.add(i);
        }
        return years;
    }
}
