package com.fanhong.cn.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.fanhong.cn.App;
import com.fanhong.cn.CommStoreDetailsActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.usedmarket.ShopActivity;
import com.fanhong.cn.usedmarket.ShopAdapter;
import com.fanhong.cn.usedmarket.ShopModel;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/7/6.
 */
@ContentView(R.layout.fragment_comm_store_shop)
public class CommStoreShopFragment extends Fragment {
    @ViewInject(R.id.layout_ms_sort)
    private AutoLinearLayout layout_sort;//排序页面
    @ViewInject(R.id.layout_ms_classify)
    private AutoLinearLayout layout_classify;//分类页面
    @ViewInject(R.id.rb_ms_sort)
    private RadioButton rb_sort;
    @ViewInject(R.id.rb_ms_classify)
    private RadioButton rb_classify;
    @ViewInject(R.id.rb_ms_sort_1)
    private RadioButton rb_sort1;
    @ViewInject(R.id.rb_ms_sort_2)
    private RadioButton rb_sort2;
    @ViewInject(R.id.rb_ms_sort_3)
    private RadioButton rb_sort3;
    @ViewInject(R.id.rb_ms_classify_1)
    private RadioButton rb_classify1;
    @ViewInject(R.id.rb_ms_classify_2)
    private RadioButton rb_classify2;
    @ViewInject(R.id.rb_ms_classify_3)
    private RadioButton rb_classify3;
    @ViewInject(R.id.rb_ms_classify_4)
    private RadioButton rb_classify4;
    @ViewInject(R.id.rb_ms_classify_5)
    private RadioButton rb_classify5;
    @ViewInject(R.id.rb_ms_classify_6)
    private RadioButton rb_classify6;
    @ViewInject(R.id.rb_ms_classify_7)
    private RadioButton rb_classify7;
    @ViewInject(R.id.rb_ms_classify_8)
    private RadioButton rb_classify8;
    @ViewInject(R.id.lv_ms_goods)
    private ListView lv_goods;
    private ShopAdapter shopAdapter;
    private List<ShopModel> shopModels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        layout_sort.setVisibility(View.GONE);
        layout_classify.setVisibility(View.GONE);
        initList();
        return view;
    }

    private void initList() {
        shopAdapter = new ShopAdapter(shopModels, getActivity());
        shopAdapter.setCallseller(new ShopAdapter.Callseller() {
            @Override
            public void onCall(String str) {
                showDialog(str);
            }
        });
        lv_goods.setAdapter(shopAdapter);

        lv_goods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), CommStoreDetailsActivity.class);
                ShopModel bean=shopModels.get(position);
                Bundle bundle=new Bundle();
                bundle.putString("title",bean.getGoodsName());
                bundle.putString("img",bean.getGoodsPicture());
                bundle.putString("detail",bean.getGoodsMessages());
                bundle.putString("user",bean.getOwnerName().replace("卖家姓名：",""));
                bundle.putString("phone",bean.getOwnerPhone().replace("卖家电话：",""));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        shopModels.clear();
        //加载数据
        new Thread() {
            @Override
            public void run() {
                seleteAllpostgoods();
            }
        }.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        shopAdapter = new ShopAdapter(shopModels, getActivity());
        shopAdapter.setCallseller(new ShopAdapter.Callseller() {
            @Override
            public void onCall(String str) {
                showDialog(str);
            }
        });
        lv_goods.setAdapter(shopAdapter);
        shopAdapter.notifyDataSetChanged();
    }

    @Event(value = {R.id.rb_ms_sort, R.id.rb_ms_classify, R.id.rb_ms_sort_1, R.id.rb_ms_sort_2, R.id.rb_ms_sort_3,
            R.id.rb_ms_classify_1, R.id.rb_ms_classify_2, R.id.rb_ms_classify_3, R.id.rb_ms_classify_4,
            R.id.rb_ms_classify_5, R.id.rb_ms_classify_6, R.id.rb_ms_classify_7, R.id.rb_ms_classify_8,
            R.id.tv_black_area1, R.id.tv_black_area2})
    private void onClicks(View view) {
        switch (view.getId()) {
            case R.id.rb_ms_sort:
                if (layout_sort.getVisibility() == View.VISIBLE) {
                    rb_sort.setChecked(false);
                    layout_sort.setVisibility(View.GONE);
                } else {
                    if (rb_classify.isChecked()) {
                        rb_classify.setChecked(false);
                        layout_classify.setVisibility(View.GONE);
                    }
                    rb_sort.setChecked(true);
                    layout_sort.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rb_ms_classify:
                if (layout_classify.getVisibility() == View.VISIBLE) {
                    rb_classify.setChecked(false);
                    layout_classify.setVisibility(View.GONE);
                } else {
                    if (rb_sort.isChecked()) {
                        rb_sort.setChecked(false);
                        layout_sort.setVisibility(View.GONE);
                    }
                    rb_classify.setChecked(true);
                    layout_classify.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_black_area1:
                onClicks(rb_sort);
                break;
            case R.id.tv_black_area2:
                onClicks(rb_classify);
                break;
            case R.id.rb_ms_sort_1:
//                Toast.makeText(getActivity(),rb_sort1.getText().toString(),Toast.LENGTH_SHORT).show();
                rb_sort1.setChecked(true);
                rb_sort2.setChecked(false);
                rb_sort3.setChecked(false);
                break;
            case R.id.rb_ms_sort_2:
                rb_sort1.setChecked(false);
                rb_sort2.setChecked(true);
                rb_sort3.setChecked(false);
                break;
            case R.id.rb_ms_sort_3:
                rb_sort1.setChecked(false);
                rb_sort2.setChecked(false);
                rb_sort3.setChecked(true);
                break;
            case R.id.rb_ms_classify_1:
                classify(1);
                break;
            case R.id.rb_ms_classify_2:
                classify(2);
                break;
            case R.id.rb_ms_classify_3:
                classify(3);
                break;
            case R.id.rb_ms_classify_4:
                classify(4);
                break;
            case R.id.rb_ms_classify_5:
                classify(5);
                break;
            case R.id.rb_ms_classify_6:
                classify(6);
                break;
            case R.id.rb_ms_classify_7:
                classify(7);
                break;
            case R.id.rb_ms_classify_8:
                classify(8);
                break;
        }
    }

    private void classify(int id) {
        rb_classify1.setChecked(false);
        rb_classify2.setChecked(false);
        rb_classify3.setChecked(false);
        rb_classify4.setChecked(false);
        rb_classify5.setChecked(false);
        rb_classify6.setChecked(false);
        rb_classify7.setChecked(false);
        rb_classify8.setChecked(false);
        switch (id) {
            case 1:
                rb_classify1.setChecked(true);
                break;
            case 2:
                rb_classify2.setChecked(true);
                break;
            case 3:
                rb_classify3.setChecked(true);
                break;
            case 4:
                rb_classify4.setChecked(true);
                break;
            case 5:
                rb_classify5.setChecked(true);
                break;
            case 6:
                rb_classify6.setChecked(true);
                break;
            case 7:
                rb_classify7.setChecked(true);
                break;
            case 8:
                rb_classify8.setChecked(true);
                break;
        }
    }

    private void showDialog(final String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("将要拨打" + str);
        builder.setMessage("是否立即拨打？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callNumber(str);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
//        builder.setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void callNumber(String num) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + num);
        intent.setData(data);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    public synchronized void seleteAllpostgoods() {
        String string = App.CMDURL; //接口地址，传入cmd参数，返回数据data
        OutputStream os = null;
        try {
            URL url = new URL(string);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);
            String content = "cmd=" + 33;
            os = http.getOutputStream();
            os.write(content.getBytes());
            os.flush();
            int res = http.getResponseCode();
            if (res == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(), "utf-8"));
                StringBuffer sb = new StringBuffer();
                String s;
                while ((s = br.readLine()) != null) {
                    sb.append(s);
                }
                JSONObject jsonObject = new JSONObject(sb.toString());

                JSONArray jsonArray = jsonObject.optJSONArray("data");
                if (jsonArray == null || jsonArray.length() == 0) {
//                        Message message0 = handler.obtainMessage();
//                        message0.what = 0;
//                        handler.sendMessage(message0);
                } else {
                    //name: tupian: ms: dh: dz: (uid)
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                        ShopModel shopModel = new ShopModel();
                        shopModel.setGoodsName(jsonObject1.optString("name"));
                        shopModel.setGoodsPicture(jsonObject1.optString("tupian"));
                        shopModel.setGoodsMessages(jsonObject1.optString("ms"));
                        shopModel.setOwnerPhone("卖家电话：" + jsonObject1.optString("dh"));
                        shopModel.setOwnerName("卖家地址：" + jsonObject1.optString("user"));
                        //商品有id,字符串类型
                        shopModel.setId(jsonObject1.optString("id"));
                        shopModels.add(shopModel);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            shopAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
