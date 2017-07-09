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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.fanhong.cn.CommStoreDetailsActivity;
import com.fanhong.cn.GardenSelecterActivity;
import com.fanhong.cn.LoginActivity;
import com.fanhong.cn.NoticeActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.StoreActivity;
import com.fanhong.cn.adapters.HomenewsAdapter;
import com.fanhong.cn.adapters.JchtAdapter;
import com.fanhong.cn.models.HomeNews;
import com.fanhong.cn.models.JchtModel;
import com.fanhong.cn.synctaskpicture.RepairLinesActivity;
import com.fanhong.cn.usedmarket.ShopActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;
import org.xutils.x;

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
    private ImageView chooseCell;
    private ViewFlipper viewFlipper;
    private GestureDetector gestureDetector;
    private RadioGroup radioGroup;
    private List<RadioButton> btnLists = new ArrayList<>();
    private List<Integer> images = new ArrayList<>();
    private TextView moreNotice, showCellName,moreEsgoods,moreNews;
    private TextView[] textViews = new TextView[4];
    private RelativeLayout repairLayout, kuaidiLayout, jiazhengLayout, storeLayout, daibanLayout;
//    private ListView jchtListView;
//    private JchtAdapter jchtAdapter;
//    private List<JchtModel> jchtModelList = new ArrayList<>();
    private String[] strings = new String[]{"2020年中国实现5G商用，2025年用户将破4亿",
            "直击茅台10万年薪招工体测：没人能躺着把钱赚了", "杭州7旬老太要把毕生积蓄送给警察 称比儿子还好"};

    private LinearLayout myGallery;
    private LayoutInflater inflater;

    private ListView newsListview;
    private List<HomeNews> newsList = new ArrayList<>();
    private HomenewsAdapter newsAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeView1 = inflater.inflate(R.layout.fragment_home, null);
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
        showCellName = (TextView) view.findViewById(R.id.show_cellname);
        try {
            String str = mSharedPref.getString("gardenName", "");
            showCellName.setText(str);
        } catch (Exception e) {
        }

        moreNotice = (TextView) view.findViewById(R.id.show_notice);
        moreNotice.setText(strings[0]);
        //公告内容动画显示
        translateImpl(moreNotice);

        moreNotice.setOnClickListener(ocl);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.my_flipper);
        radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);

        images.add(R.drawable.title3);
        images.add(R.drawable.banner);
        images.add(R.drawable.top_banner);

        textViews[0] = (TextView) view.findViewById(R.id.textView0);
        textViews[1] = (TextView) view.findViewById(R.id.textView1);
        textViews[2] = (TextView) view.findViewById(R.id.textView2);
        textViews[3] = (TextView) view.findViewById(R.id.textView3);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(14, 14);
        layoutParams.setMargins(12, 0, 12, 0);
        for (int i = 0; i < images.size(); i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
            imageView.setImageResource(images.get(i));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);

            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setLayoutParams(layoutParams);
            radioButton.setId(i);
            radioButton.setButtonDrawable(null);
            radioButton.setBackgroundResource(R.drawable.ciecle_radiobutton);
            btnLists.add(radioButton);
            radioGroup.addView(radioButton);
        }
        viewFlipper.setFlipInterval(3000); //设置切换间隔时间
        viewFlipper.startFlipping(); //开始切换
        viewFlipper.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                btnLists.get(viewFlipper.getDisplayedChild()).setChecked(true);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                viewFlipper.stopFlipping();  //停止切换
                viewFlipper.setDisplayedChild(checkedId);
                viewFlipper.startFlipping();  //开始切换
            }
        });

        for (int i = 0; i < textViews.length; i++) {
            textViews[i].setOnClickListener(ocl);
        }

        moreEsgoods = (TextView) view.findViewById(R.id.more_esgoods);
        moreEsgoods.setOnClickListener(ocl);
        moreNews = (TextView) view.findViewById(R.id.more_news);
        moreNews.setOnClickListener(ocl);

//        jchtListView = (ListView) view.findViewById(R.id.jcht_listview);
        newsListview = (ListView) view.findViewById(R.id.home_news);
        initData();
//        jchtAdapter = new JchtAdapter(getActivity(), jchtModelList);
//        jchtListView.setAdapter(jchtAdapter);
        newsAdapter = new HomenewsAdapter(getActivity(), newsList);
        newsListview.setAdapter(newsAdapter);
//        getBaseActivity().setListViewHeight(jchtListView);
        getBaseActivity().setListViewHeight(newsListview);


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
        inflater = LayoutInflater.from(getActivity());
    }
    private void initData(){
//        for (int i = 0; i < 2; i++) {
//            JchtModel jchtModel = new JchtModel();
//            jchtModelList.add(jchtModel);
//        }
        for(int i = 0;i<2;i++){
            HomeNews homeNews = new HomeNews();
            newsList.add(homeNews);
        }
        Message msg = handler.obtainMessage();
        msg.what = 0;
        handler.sendMessage(msg);

        Map<String,Object> map = new HashMap<>();
//        String uid = mSharedPref.getString("UserId","");
        map.put("cmd","33");
        if(mSample == null){
            mSample = new SampleConnection(getBaseActivity(),37);
        }
        mSample.connectService1(map);
    }
    private void getGoodsData(String str){
        Log.i("xq","es===>json："+str);
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jsonArray = jsonObject.optJSONArray("data");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);

                final JSONObject object1 = object;
                View view = inflater.inflate(R.layout.myhorizontallistview_item,null);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeView1.this.getActivity(), CommStoreDetailsActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putString("title",(object1.optString("name")));
                        bundle.putString("img",(object1.optString("tupian")));
                        bundle.putString("detail",object1.optString("ms"));
                        bundle.putString("user",(object1.optString("user")));
                        bundle.putString("phone",(object1.optString("dh")));
                        bundle.putString("price",object1.optString("jg"));
                        bundle.putString("id",(object1.optInt("id")+""));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                ImageView imageView = (ImageView) view.findViewById(R.id.myitempic);
//                LoadImage.Load(imageView,object.optString("tupian",""),getActivity());
                ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.pictureloading)
                        .setFailureDrawableId(R.mipmap.picturefailedloading).setUseMemCache(true).build();
                x.image().bind(imageView,object.optString("tupian",""),options);
                TextView textView = (TextView) view.findViewById(R.id.myitemtext);
                textView.setText(object.optString("name",""));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getWidth()/3-20,-1);
                params.setMargins(10,0,10,0);
                view.setLayoutParams(params);
                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(getWidth()/3-80,
                        getWidth()/3-80);
                params1.setMargins(0,20,0,0);
                imageView.setLayoutParams(params1);
                myGallery.addView(view);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //获取屏幕宽度
    private int getWidth(){
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
                tv.setText(strings[i]);
                i++;
                if (i > strings.length - 1) {
                    i = 0;
                }
            }
        });

        alphaAnimation.setRepeatCount(Animation.INFINITE); //循环显示
        tv.startAnimation(alphaAnimation);

    }

    //    private void getGoodsData(String string){
//        try {
//            JSONObject jsonObject = new JSONObject(string);
//            JSONArray jsonArray = jsonObject.getJSONArray("data");
//            for(int i=0;i<jsonArray.length();i++){
//                JSONObject object = jsonArray.optJSONObject(i);
//                HorizontalListviewModel model = new HorizontalListviewModel();
//                model.setImageUrl(object.optString("tupian"));
//                model.setPicName(object.optString("name"));
//                modelList.add(model);
//            }
//            Message msg = handler.obtainMessage();
//            msg.what = 1;
//            handler.sendMessage(msg);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.show_notice:
                    intent.setClass(HomeView1.this.getActivity(), NoticeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.image_choosecell:
                    if (isLogined() == 1) {
                        intent.setClass(HomeView1.this.getActivity(), GardenSelecterActivity.class);
                        HomeView1.this.getBaseActivity().startActivityForResult(intent, 12);
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
                    intent.setClass(HomeView1.this.getActivity(), RepairLinesActivity.class);
                    startActivity(intent);
                    break;
                case R.id.kuaidi_layout:
                    Toast.makeText(getActivity(), R.string.starting, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.jiazheng_layout:
                    Toast.makeText(getActivity(), R.string.starting, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.store_layout:
                    intent.setClass(HomeView1.this.getActivity(), StoreActivity.class);
                    startActivity(intent);
                    break;
                case R.id.daiban_layout:
                    Toast.makeText(getActivity(), R.string.starting, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.more_esgoods:
                    intent.setClass(HomeView1.this.getActivity(), ShopActivity.class);
                    startActivity(intent);
                    break;
                case R.id.more_news:
                    getBaseActivity().setRadioButtonsChecked(3);
                    break;
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
            case 37:
                myGallery.removeAllViews();
                getGoodsData(str);
                break;
        }
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
//                    jchtAdapter.notifyDataSetChanged();
                    newsAdapter.notifyDataSetChanged();
                    break;
            }
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

        Map<String,Object> map = new HashMap<>();
//        String uid = mSharedPref.getString("UserId","");
        map.put("cmd","33");
        if(mSample == null){
            mSample = new SampleConnection(getBaseActivity(),37);
        }
        mSample.connectService1(map);
    }
}
