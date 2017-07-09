package com.fanhong.cn;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanhong.cn.adapters.LargeImageAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommentLargeImageActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private ViewPager vp_large_image;
    private LinearLayout ll_back, ll_remove;
    private TextView tv_current_index;

    private LargeImageAdapter adapter;
    private List<String> imgUrls;
    private int currentIndex;

    private final int RESULT_CODE_LARGE_IMAGE = 1;

    @Override
    public void setContentView() {
        Log.i("hu","*****CommentLargeImageActivity.java setContentView()");
        super.isNoTitle = true;
        setContentView(R.layout.activity_comment_large_image);
    }

    @Override
    public void initView() {
        vp_large_image = (ViewPager) findViewById(R.id.vp_large_image);
        tv_current_index = (TextView) findViewById(R.id.tv_current_index);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_remove = (LinearLayout) findViewById(R.id.ll_remove);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentIndex = bundle.getInt(AssessActivity.KEY_CURRENT_INDEX);

            try {
                int cut1 = bundle.getInt("cutremove");
                if (cut1 == 1)
                    ll_remove.setVisibility(View.GONE);
            }catch (Exception e) {}

            imgUrls = bundle.getStringArrayList(AssessActivity.KEY_IMAGE_LIST);
            vp_large_image.setAdapter(adapter = new LargeImageAdapter(this, imgUrls));//设置晒单图显示
            vp_large_image.setOffscreenPageLimit(imgUrls.size());//预加载的数量为图片集合的长度
            vp_large_image.setCurrentItem(currentIndex);
            tv_current_index.setText(currentIndex + 1 + " / " + imgUrls.size());
        }
    }

    @Override
    public void initListener() {
        ll_back.setOnClickListener(this);
        ll_remove.setOnClickListener(this);
        vp_large_image.addOnPageChangeListener(this);
    }

    @Override
    public void initData() {
    }

    @Override
    public void onBackPressed() {
        //回到晒单页时候把处理后的图片集合返回过去
        Intent intent = new Intent();
        intent.putStringArrayListExtra(AssessActivity.KEY_IMAGE_LIST, (ArrayList<String>) adapter.getData());
        setResult(RESULT_CODE_LARGE_IMAGE, intent);
        super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentIndex = position;
        tv_current_index.setText(++position + " / " + imgUrls.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;

            case R.id.ll_remove:
                //删除当前晒单图
                if (imgUrls.size() == 1) {
                    //删除最后一张时直接回到晒单评论页
                    imgUrls.clear();
                    onBackPressed();
                } else {
                    //删除指定索引的图片
                    removeImage(currentIndex);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 删除指定索引的晒单图
     * @param index
     */
    private void removeImage(int index) {
        imgUrls.remove(index);
        setImageTitle(index);
        vp_large_image.removeAllViews();//删除viewpager所有的子View
        vp_large_image.setAdapter(adapter = new LargeImageAdapter(this, imgUrls));//重新设置适配器数据显示
        vp_large_image.setCurrentItem(index == imgUrls.size() - 1 ? ++index : --index);//显示指定位置
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置标题显示
     * <p>比如3 / 4
     * @param index
     */
    private void setImageTitle(int index) {
        //删除图片路径后
        if (index == 0 || imgUrls.size() == 1) {
            // 索引 == 0 || 图片集合只剩一张图
            // 就把索引值固定为1
            index = 1;
        } else if (index == imgUrls.size() - 1) {
            // 当前索引 == 图片集合的最后一张
            // 就不做任何处理
        } else {
            //否则就把索引+1便于显示
            index += 1;
        }
        tv_current_index.setText(index + " / " + imgUrls.size());
    }
}
