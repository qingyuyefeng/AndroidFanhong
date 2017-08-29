package com.fanhong.cn.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.AgentWebActivity;
import com.fanhong.cn.App;
import com.fanhong.cn.CommStoreDetailsActivity;
import com.fanhong.cn.FaceRecognitionIntroductionActivity;
import com.fanhong.cn.GardenSelecterActivity;
import com.fanhong.cn.HomeNewsALLActivity;
import com.fanhong.cn.LoginActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.StoreActivity;
import com.fanhong.cn.adapters.HomelifeAdapter;
import com.fanhong.cn.adapters.HomenewsAdapter;
import com.fanhong.cn.expressage.ExpressHomeActivity;
import com.fanhong.cn.housekeeping.HouseKeepingActivity;
import com.fanhong.cn.models.BannerModel;
import com.fanhong.cn.models.HomeNews;
import com.fanhong.cn.models.Homelife;
import com.fanhong.cn.repair.RepairActivity;
import com.fanhong.cn.usedmarket.ShopActivity;
import com.fanhong.cn.verification.VerificationIndexActivity;
import com.sivin.Banner;
import com.sivin.BannerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/6.
 */

public class HomeView1 extends BaseFragment {

    private View homeView1;
    private SharedPreferences mSharedPref;
    private SampleConnection mSample;
    private ImageView chooseCell, noEsgoods;
    //    private ViewFlipper viewFlipper;
//    private GestureDetector gestureDetector;
//    private RadioGroup radioGroup;
//    private List<RadioButton> btnLists = new ArrayList<>();
    private Banner banner;
    private BannerAdapter bannerAdapter;
    private List<BannerModel> bannerModelList = new ArrayList<>();
    private TextView moreNotice, showCellName, moreEsgoods, moreNews;
    private TextView[] textViews = new TextView[4];
    private RelativeLayout repairLayout, kuaidiLayout, jiazhengLayout, storeLayout, daibanLayout;

    private List<String> strings = new ArrayList<>();

    private LinearLayout myGallery, homenewsLayout;
    private LayoutInflater inflater;

    private ListView newsListview;
    private List<HomeNews> newsList = new ArrayList<>();
    private HomenewsAdapter newsAdapter;

    private ListView lifeListview;
    private HomelifeAdapter lifeAdapter;
    private List<Homelife> lifeList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeView1 = inflater.inflate(R.layout.fragment_home, null);

        getGonggaoData();
        getnewsandlife();

        return homeView1;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //webView = (WebView)view.findViewById(R.id.WebView);
        // 启动activity时不自动弹出软键盘
//        this.getActivity().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mSharedPref = this.getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);
        chooseCell = (ImageView) view.findViewById(R.id.image_choosecell);
        chooseCell.setOnClickListener(ocl);


        moreNotice = (TextView) view.findViewById(R.id.show_notice);
        moreNotice.setOnClickListener(ocl);
        banner = (Banner) view.findViewById(R.id.home_banner);
        bannerAdapter = new BannerAdapter<BannerModel>(bannerModelList) {

            @Override
            protected void bindTips(TextView tv, BannerModel bannerModel) {

            }

            @Override
            public void bindImage(ImageView imageView, BannerModel bannerModel) {
                ImageOptions options = new ImageOptions.Builder().setCrop(true).setPlaceholderScaleType(ImageView.ScaleType.FIT_XY).setUseMemCache(true).build();
                x.image().bind(imageView, bannerModel.getImageUrl(), options);
            }
        };
        banner.setBannerAdapter(bannerAdapter);
        getBannerImg();
        banner.setOnBannerItemClickListener(new Banner.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        intent.setClass(HomeView1.this.getActivity(), FaceRecognitionIntroductionActivity.class);
                        intent.putExtra("position", 0);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(HomeView1.this.getActivity(), FaceRecognitionIntroductionActivity.class);
                        intent.putExtra("position", 1);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(HomeView1.this.getActivity(), StoreActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });


        textViews[0] = (TextView) view.findViewById(R.id.textView0);
        textViews[1] = (TextView) view.findViewById(R.id.textView1);
        textViews[2] = (TextView) view.findViewById(R.id.textView2);
        textViews[3] = (TextView) view.findViewById(R.id.textView3);

        for (int i = 0; i < textViews.length; i++) {
            textViews[i].setOnClickListener(ocl);
        }

        moreEsgoods = (TextView) view.findViewById(R.id.more_esgoods);
        moreEsgoods.setOnClickListener(ocl);

        moreNews = (TextView) view.findViewById(R.id.more_news);
        moreNews.setOnClickListener(ocl);


        repairLayout = (RelativeLayout) view.findViewById(R.id.repair_layout);
        repairLayout.setOnClickListener(ocl);
        kuaidiLayout = (RelativeLayout) view.findViewById(R.id.kuaidi_layout);
        kuaidiLayout.setOnClickListener(ocl);
        jiazhengLayout = (RelativeLayout) view.findViewById(R.id.jiazheng_layout);
        jiazhengLayout.setOnClickListener(ocl);
        storeLayout = (RelativeLayout) view.findViewById(R.id.store_layout);
        storeLayout.setOnClickListener(ocl);
        daibanLayout = (RelativeLayout) view.findViewById(R.id.daiban_layout);
        daibanLayout.setOnClickListener(ocl);

        myGallery = (LinearLayout) view.findViewById(R.id.my_gallery);
        noEsgoods = (ImageView) view.findViewById(R.id.iv_noesgoods);
        homenewsLayout = (LinearLayout) view.findViewById(R.id.home_news_layout);
        inflater = LayoutInflater.from(getActivity());
    }

    private void getBannerImg() {
        bannerModelList.add(new BannerModel().setImageUrl("assets://images/title3.jpg"));
        bannerModelList.add(new BannerModel().setImageUrl("assets://images/banner.jpg"));
        bannerModelList.add(new BannerModel().setImageUrl("assets://images/top_banner.jpg"));
        banner.notifyDataHasChanged();
    }

    private void getGonggaoData() {
        Map<String, Object> map = new HashMap<>();
        map.put("cmd", 43);
        if (mSample == null) {
            mSample = new SampleConnection(getBaseActivity(), 43);
        }
        mSample.connectService1(map);
    }

    private void setGoodsData(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray = jsonObject.optJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                final JSONObject object1 = object;
                View view = inflater.inflate(R.layout.myhorizontallistview_item, null);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeView1.this.getActivity(), CommStoreDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", (object1.optString("name")));
                        bundle.putString("img", (object1.optString("tupian")));
                        bundle.putString("detail", object1.optString("ms"));
                        bundle.putString("user", (object1.optString("user")));
                        bundle.putString("phone", (object1.optString("dh")));
                        bundle.putString("price", object1.optString("jg"));
                        bundle.putString("id", (object1.optInt("id") + ""));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                ImageView imageView = (ImageView) view.findViewById(R.id.myitempic);
                ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
                        .setFailureDrawableId(R.mipmap.picturefailedloading).setUseMemCache(true).build();
                x.image().bind(imageView, object.optString("tupian", ""), options);
                TextView textView = (TextView) view.findViewById(R.id.myitemtext);
                textView.setText(object.optString("name", ""));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getWidth() / 3 - 20, -1);
                params.setMargins(10, 5, 10, 5);
                view.setLayoutParams(params);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(getWidth() / 3 - 80,
                        getWidth() / 3 - 80);
                params1.setMargins(0, 20, 0, 5);
                imageView.setLayoutParams(params1);
                myGallery.addView(view);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //获取屏幕宽度
    private int getWidth() {
        WindowManager wm = HomeView1.this.getActivity().getWindowManager();
        return wm.getDefaultDisplay().getWidth();
    }

    // 移动效果
    int i = 0;

    public void translateImpl(final TextView tv) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
        //次数重复中停留的时间
        alphaAnimation.setStartOffset(2000);
        alphaAnimation.setDuration(1000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                tv.setText(strings.get(i));
                i++;
                if (i > strings.size() - 1) {
                    i = 0;
                }
            }
        });

        alphaAnimation.setRepeatCount(Animation.INFINITE); //循环显示
        tv.startAnimation(alphaAnimation);

    }

    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.show_notice:
                    getBaseActivity().setRadioButtonsChecked(3);
                    break;
                case R.id.image_choosecell:
                    if (isLogined() == 1) {
                        intent.setClass(HomeView1.this.getActivity(), GardenSelecterActivity.class);
                        HomeView1.this.getBaseActivity().startActivityForResult(intent, 112);
                    } else {
                        createDialog();
                    }
                    break;
                case R.id.textView0:
                    Toast.makeText(getActivity(), "正在努力开通", Toast.LENGTH_SHORT).show();
//                    intent.setClass(HomeView1.this.getActivity(), StoreActivity.class);
//                    startActivity(intent);
                    break;
                case R.id.textView1:
                    Toast.makeText(getActivity(), "正在努力开通", Toast.LENGTH_SHORT).show();
//                    intent.setClass(HomeView1.this.getActivity(), UrgentOpenDoorActivity.class);
//                    startActivity(intent);
                    break;
                case R.id.textView2:
                    Toast.makeText(getActivity(), "正在努力开通", Toast.LENGTH_SHORT).show();
//                    intent.setClass(HomeView1.this.getActivity(), TestViewFlipper.class);
//                    startActivity(intent);
                    break;
                case R.id.textView3:
                    Toast.makeText(getActivity(), "正在努力开通", Toast.LENGTH_SHORT).show();
//                    intent.setClass(HomeView1.this.getActivity(), ShopActivity.class);
//                    startActivity(intent);
                    break;
                case R.id.repair_layout:
                    intent.setClass(HomeView1.this.getActivity(), RepairActivity.class);
                    startActivity(intent);
//                    Toast.makeText(getActivity(), R.string.starting, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.kuaidi_layout:
                    intent.setClass(HomeView1.this.getActivity(), ExpressHomeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.jiazheng_layout:
//                    Toast.makeText(getActivity(), R.string.starting, Toast.LENGTH_SHORT).show();
                    intent.setClass(HomeView1.this.getActivity(), HouseKeepingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.store_layout:
                    intent.setClass(HomeView1.this.getActivity(), StoreActivity.class);
                    startActivity(intent);
                    break;
                case R.id.daiban_layout:
//                    Toast.makeText(getActivity(), R.string.starting, Toast.LENGTH_SHORT).show();
                    intent.setClass(HomeView1.this.getActivity(), VerificationIndexActivity.class);
                    startActivity(intent);
                    break;
                case R.id.more_esgoods:
                    intent.setClass(HomeView1.this.getActivity(), ShopActivity.class);
                    startActivity(intent);
                    break;
                case R.id.more_news:
                    intent.setClass(HomeView1.this.getActivity(), HomeNewsALLActivity.class);
                    startActivity(intent);
            }
        }
    };


    private int isLogined() {
        int result = 0;
        try {
            result = mSharedPref.getInt("Status", 0);
        } catch (Exception e) {
        }
        return result;
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("你还没有登录哦");
        builder.setMessage("是否立即登录？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(HomeView1.this.getActivity(), LoginActivity.class);
                startActivity(intent);
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


    public synchronized void setFragment(int type, String str) {
        switch (type) {
            case 11:
                showCellName.setText(str);
                break;
            case 33:
                myGallery.removeAllViews();
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    int cw = jsonObject.optInt("cw");
                    if (cw == 0) {
                        setGoodsData(str);
                    } else {
                        noEsgoods.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
//            case 333://接口数据获取失败（比如未联网）
//                noEsgoods.setImageResource(R.drawable.datafailed);
//                noEsgoods.setVisibility(View.VISIBLE);
//                break;
            case 49:
                newsList.clear();
                setnewsList(str);
                lifeList.clear();
                setlifeList(str);
                break;
            case 43:
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    String string = jsonObject.optString("data").replace("[", "").replace("]", "").replace("\"", "");
                    String[] strs = string.split(",");
                    if (strs.length > 0) {
                        for (int i = 0; i < strs.length; i++) {
                            strings.add(strs[i]);
                        }
                        moreNotice.setText(strings.get(0));
                        //公告内容动画显示
                        translateImpl(moreNotice);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setnewsList(String str) {
        Log.i("xq", "首页新闻data==>" + str);
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray = jsonObject.optJSONArray("data1");
            if (jsonArray.length() == 0) {
                homenewsLayout.setBackgroundResource(R.drawable.nodatas);
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    JSONArray logos = object.getJSONArray("logo");
                    String logo = (String) logos.get(0);
                    HomeNews homeNews = new HomeNews();
                    homeNews.setNewsImage(logo);
                    homeNews.setNewsTitle(object.optString("bt"));
                    homeNews.setNewsWhere(object.optString("zz"));
                    homeNews.setNewsTime(object.optString("time"));
                    homeNews.setNewsUrl(object.optString("url"));
                    newsList.add(homeNews);
                }
                handler.sendEmptyMessage(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setlifeList(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray = jsonObject.optJSONArray("data2");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Homelife homelife = new Homelife();
                JSONArray logos = object.getJSONArray("logo");
                List<String> list = new ArrayList<>();
                for (int j = 0; j < logos.length(); j++) {
                    list.add((String) logos.get(j));
                }
//                Log.i("xq","list==>"+list.toString());
                homelife.setStrings(list);
                if (list.size() == 3) {
                    homelife.setType(0);
                    homelife.setTitle(object.optString("bt"));
                    homelife.setPlace(object.optString("zz"));
                    homelife.setTime(object.optString("time"));
                    homelife.setUrl(object.optString("url"));
                } else if (list.size() == 1) {
                    homelife.setType(1);
                    homelife.setTitle(object.optString("bt"));
                    homelife.setPlace(object.optString("zz"));
                    homelife.setTime(object.optString("time"));
                    homelife.setUrl(object.optString("url"));
                }
                lifeList.add(homelife);
            }
            wait(2000);
            handler.sendEmptyMessage(0);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            getBaseActivity().setListViewHeight(newsListview);
            getBaseActivity().setListViewHeight(lifeListview);
            newsAdapter.notifyDataSetChanged();
            lifeAdapter.notifyDataSetChanged();
            return true;
        }
    });

    @Override
    public void onResume() {
        super.onResume();
        mSharedPref = this.getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);
        showCellName = (TextView) homeView1.findViewById(R.id.show_cellname);
        try {
            String str = mSharedPref.getString("gardenName", "");
            showCellName.setText(str);
        } catch (Exception e) {
        }

        newsListview = (ListView) homeView1.findViewById(R.id.home_news);
        lifeListview = (ListView) homeView1.findViewById(R.id.life_listview);
        getEsGoodsDatas();


        newsAdapter = new HomenewsAdapter(getActivity(), newsList);
        newsListview.setAdapter(newsAdapter);
        newsListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = newsList.get(position).getNewsUrl();
//                Intent intent = new Intent(HomeView1.this.getActivity(), WebViewActivity.class);
                Intent intent = new Intent(HomeView1.this.getActivity(), AgentWebActivity.class);
                intent.putExtra("url", url);
                startActivityForResult(intent, 1);
            }
        });

        lifeAdapter = new HomelifeAdapter(getActivity(), lifeList);
        lifeListview.setAdapter(lifeAdapter);
        lifeListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = lifeList.get(position).getUrl();
//                Intent intent = new Intent(HomeView1.this.getActivity(), WebViewActivity.class);
                Intent intent = new Intent(HomeView1.this.getActivity(), AgentWebActivity.class);
                intent.putExtra("url", url);
                startActivityForResult(intent, 1);
            }
        });


    }

    private void getEsGoodsDatas() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addParameter("cmd","33");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                setFragment(33,s);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
        Map<String, Object> map = new HashMap<>();
        map.put("cmd", "33");
        if (mSample == null) {
            mSample = new SampleConnection(getBaseActivity(), 33);
        }
        mSample.connectService1(map);
    }

    private void getnewsandlife() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addParameter("cmd","49");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                setFragment(49,s);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
