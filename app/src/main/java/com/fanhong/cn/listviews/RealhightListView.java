package com.fanhong.cn.listviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2017/7/4.
 */

public class RealhightListView extends ListView {
    public RealhightListView(Context context) {
        super(context);
    }

    public RealhightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RealhightListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mExpandSpec=MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, mExpandSpec);
    }
}
