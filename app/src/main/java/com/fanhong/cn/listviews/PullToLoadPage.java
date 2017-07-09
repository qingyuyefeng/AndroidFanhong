package com.fanhong.cn.listviews;

import com.fanhong.cn.PullToLoadPageListView.PullToLoadCallback;
import com.fanhong.cn.R;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class PullToLoadPage implements PullToLoadCallback {
    private TextView mTxvMotion, mTxvCurrentPage;
    private ViewGroup mRootView;
    private View mPrbLoading;
    private int mMotionResStr;

    public PullToLoadPage(int motionResStr, View rootView) {
        mRootView = (ViewGroup) rootView;
        mMotionResStr = motionResStr;

        mTxvCurrentPage = (TextView) mRootView.findViewById(R.id.txvCurrentPage);
        mTxvMotion = (TextView) mRootView.findViewById(R.id.txvMotion);
        mPrbLoading = mRootView.findViewById(R.id.prbLoading);

        onEndLoading();
    }

    public ViewGroup getRootView() {
        return mRootView;
    }

    @Override
    public void onStartLoading() {
        mPrbLoading.setVisibility(View.VISIBLE);
       // mTxvMotion.setText(String.format(getLoadingPageNoteStr(), takePageNum()));
        mTxvMotion.setText(getLoadingPageNoteStr());
    }

    @Override
    public void onEndLoading() {
        mTxvMotion.setText(mMotionResStr);
        mPrbLoading.setVisibility(View.GONE);
        int pageNum = takePageNum();
        if (pageNum > 0) {
            mTxvCurrentPage.setText(String.format(getCurrentPageNoteStr(), takePageNum()));
           // mTxvCurrentPage.setVisibility(View.VISIBLE);
            mTxvCurrentPage.setVisibility(View.GONE);
        } else {
            mTxvCurrentPage.setVisibility(View.GONE);
        }
    }

    private String getCurrentPageNoteStr() {
        return mRootView.getResources().getString(R.string.current_page_note);
    }

    private String getLoadingPageNoteStr() {
        return mRootView.getResources().getString(R.string.loading_page_note);
    }

    public abstract int takePageNum();
}
