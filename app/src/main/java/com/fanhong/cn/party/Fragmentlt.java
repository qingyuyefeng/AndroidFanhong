package com.fanhong.cn.party;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.party.adapters.LtAdapter;
import com.fanhong.cn.party.models.LtItemModel;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.MySharedPrefUtils;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/3.
 */
@ContentView(R.layout.fragment_lt)
public class Fragmentlt extends Fragment {
    @ViewInject(R.id.lt_layout)
    private AutoLinearLayout ltlayout;
    @ViewInject(R.id.lt_recyclerview)
    private RecyclerView recyclerView;
    @ViewInject(R.id.lt_get_more)
    private ImageView getMore;
    @ViewInject(R.id.lt_empty)
    private TextView ltEmpty;
    @ViewInject(R.id.add_layout)
    private AutoLinearLayout addLayout;
    @ViewInject(R.id.lt_edit)
    private EditText ltEdit;
    @ViewInject(R.id.lt_submit)
    private TextView submit;

    private List<LtItemModel> list = new ArrayList<>();
    private LtAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        return view;
    }

    @Event({R.id.lt_get_more, R.id.lt_submit})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.lt_get_more:
                list.clear();
                RequestParams params1 = new RequestParams(App.CMDURL);
                params1.addBodyParameter("cmd","117");
                x.http().post(params1, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if(JsonSyncUtils.getJsonValue(result,"cmd").equals("118")){
                            try {
                                JSONArray jsonArray = new JSONObject(result).getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    LtItemModel model = new LtItemModel();
                                    model.setPhoto(object.optString("logo", ""));
                                    model.setName(object.optString("name", ""));
                                    model.setContent(object.optString("content", ""));
                                    list.add(model);
                                }
                                handler.sendEmptyMessage(3);
                                handler.sendEmptyMessage(4);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
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
                break;
            case R.id.lt_submit:
                RequestParams params = new RequestParams(App.CMDURL);
                params.addBodyParameter("cmd","113");
                params.addBodyParameter("uid", MySharedPrefUtils.getUserId(getActivity()));
                params.addBodyParameter("content",ltEdit.getText().toString());
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if(JsonSyncUtils.getJsonValue(result,"cw").equals("0")){
                            Toast.makeText(getActivity(),"发表成功！",Toast.LENGTH_SHORT).show();
                            ltEdit.setText("");
                            hideSoftinputyer(submit);
                            list.clear();
                            getDatas();
                        }else {
                            Toast.makeText(getActivity(),"发表失败！",Toast.LENGTH_SHORT).show();
                        }
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
                break;
        }
    }

    //隐藏软键盘的方法
    private void hideSoftinputyer(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //展示3条数据
    private void getDatas() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd","115");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if(JsonSyncUtils.getJsonValue(result,"cmd").equals("116")){
                    try {
                        JSONArray jsonArray = new JSONObject(result).getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            LtItemModel model = new LtItemModel();
                            model.setPhoto(object.optString("logo", ""));
                            model.setName(object.optString("name", ""));
                            model.setContent(object.optString("content", ""));
                            list.add(model);
                        }
                        handler.sendEmptyMessage(3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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

    @Override
    public void onResume() {
        super.onResume();
        getMore.setVisibility(View.VISIBLE);
        addLayout.setVisibility(View.VISIBLE);
        list.clear();
        getDatas();
        adapter = new LtAdapter(getActivity(), list);
        //留回复接口和详情页面接口
//        adapter.setLtInterface();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new MyDecoration());
//        if (list.size() > 0) {
//            handler.sendEmptyMessage(2);
//        } else
//            handler.sendEmptyMessage(1);
    }

    private  Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //没有数据时
                    ltlayout.setVisibility(View.GONE);
                    ltEmpty.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    ltlayout.setVisibility(View.VISIBLE);
                    ltEmpty.setVisibility(View.GONE);
                    break;
                case 3:
                    adapter.notifyDataSetChanged();
                    break;
                case 4:
                    getMore.setVisibility(View.GONE);
                    addLayout.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    });

    class MyDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int d = getResources().getDimensionPixelSize(R.dimen.item_decoration);
            outRect.set(0,d,0,d);
        }
    }
}
