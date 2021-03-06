package com.fanhong.cn.party.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.R;
import com.fanhong.cn.party.adapters.CommentAdapter;
import com.fanhong.cn.party.models.CommentModel;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.MySharedPrefUtils;
import com.fanhong.cn.util.TopBarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/4.
 */

@ContentView(R.layout.activity_details)
public class DetailsActivity extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView title;
    @ViewInject(R.id.img_fx)
    private ImageView imgfx;
    @ViewInject(R.id.detail_content)
    private TextView content;
    @ViewInject(R.id.detail_edit)
    private EditText edit;
    @ViewInject(R.id.detail_publish)
    private TextView publish;
    @ViewInject(R.id.detail_list)
    private ListView listView;

    private int shareid;
    private List<CommentModel> list = new ArrayList<>();
    private CommentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initView();
        adapter = new CommentAdapter(this, list);
        listView.setAdapter(adapter);
    }

    private void initView() {
        title.setText("内容详情");
        Intent intent = getIntent();
        content.setText(intent.getStringExtra("content"));
        shareid = intent.getIntExtra("id", 0);

        String url = intent.getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            imgfx.setVisibility(View.GONE);
        } else {
            ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
                    .setFailureDrawableId(R.mipmap.picturefailedloading).setUseMemCache(true).build();
//            x.image().bind(imgfx,url,options);
            x.image().loadDrawable(url, options, new Callback.CommonCallback<Drawable>() {
                @Override
                public void onSuccess(Drawable drawable) {
                    //获取屏幕宽度
                    WindowManager wm = DetailsActivity.this.getWindowManager();
                    int screenWidth = wm.getDefaultDisplay().getWidth();

                    int MaxHeight = screenWidth * 10;
                    int picheight = (int) ((float) screenWidth / drawable.getMinimumWidth() * drawable.getMinimumHeight());
                    imgfx.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, picheight));
                    imgfx.setMaxHeight(MaxHeight);
                    imgfx.setImageDrawable(drawable);

//                    Log.i("xq","分享详情图片宽高==>"+imgfx.getWidth()+","+imgfx.getHeight());
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

    @Event({R.id.img_back, R.id.detail_publish})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.detail_publish:
                String str = edit.getText().toString();
                if (TextUtils.isEmpty(str)) {
                    Toast.makeText(this, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    addData(str);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        getData();
    }

    //评论展示
    private void getData() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "121");
        params.addBodyParameter("shareid", shareid + "");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "cmd").equals("122")) {
                    try {
                        JSONArray jsonArray = new JSONObject(result).getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            CommentModel model = new CommentModel();
                            model.setPhoto(object.optString("logo", ""));
                            model.setName(object.optString("name", ""));
                            model.setTime(object.optString("times", ""));
                            model.setContent(object.optString("comment", ""));
                            list.add(model);
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TopBarUtil.setListViewHeight(listView);
                                adapter.notifyDataSetChanged();
                            }
                        });
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

    //评论添加
    private void addData(String string) {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "119");
        params.addBodyParameter("shareid", shareid + "");
        params.addBodyParameter("uid", MySharedPrefUtils.getUserId(this));
        params.addBodyParameter("comment", string);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (JsonSyncUtils.getJsonValue(result, "cw").equals("0")) {
                    Toast.makeText(DetailsActivity.this, "评论发表成功~", Toast.LENGTH_SHORT).show();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            edit.setText("");
//                            adapter.notifyDataSetChanged();
                            DetailsActivity.this.onResume();
                        }
                    });
                } else {
                    Toast.makeText(DetailsActivity.this, "评论发表失败~", Toast.LENGTH_SHORT).show();
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
}
