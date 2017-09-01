package com.fanhong.cn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.fanhong.cn.database.Cartdb;
import com.fanhong.cn.view.PayMoney;
import com.fanhong.cn.view.PayPopupWindow;
import com.fanhong.cn.listviews.AmountView;
import com.fanhong.cn.listviews.ChangeListView;
import com.jauker.widget.BadgeView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.sivin.Banner;
import com.sivin.BannerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DescriptionActivity extends SampleActivity implements OnPageChangeListener, OnClickListener, PayMoney {
    private SharedPreferences mSettingPref;
    private SampleConnection mSafoneConnection;
    private Context context;
    private ImageView titleBackImageBtn;
    private Button btn_bay;
    private ImageView iv_cart;
    private LinearLayout ll_addcart;
    private ImageView iv_addcart;
    private PayPopupWindow menuWindow; // 自定义的立即支付编辑弹出框
    private AmountView mAmountView;
    private ChangeListView lv_list;
    private TextView tv_title, tv_str, tv_price;
    /**
     * ViewPager
     */
    private ViewPager viewPager;

    /**
     * 装点点的ImageView数组
     */
    private ImageView[] tips;

    /**
     * 装ImageView数组
     */
    private ImageView[] mImageViews;

    /**
     * 图片资源id
     */
    private int[] imgIdArray = {R.drawable.goods, R.drawable.top_banner, R.drawable.banner_02, R.drawable.banner_03};
    private int mImageViewArray[] = {R.drawable.banner_02, R.drawable.banner_03, R.drawable.goods};
    private TextView tv_detail, tv_discuss, tv_guige;
    private int selectedBtn = R.id.tv_detail;
    private String ID; //商品id
    private ImageView /*iv_logo, */iv_detail;
    String pictureurl = null;
    String name = null;
    String describe = null;
    String logourl = null;
    String price = null;
    String normal = null;
    String id = null;
    int number = 1;
    Cartdb _dbad;
    private ViewGroup anim_mask_layout;//动画层
    private BadgeView badgeCart;//购物车记录数图标
    private ImageView ball;// 小圆点
    private int discuss_count = 0; //评论总条数，10条为一页
    private int discuss_totalpages = 0; //评论总页数
    private int discuss_page = 0; //评论目前是第几页,从1开始
    List<String> tupian = new ArrayList<String>();
    List<String> nr = new ArrayList<String>();
    List<String> userid = new ArrayList<String>();
    List<String> date1 = new ArrayList<String>();
    private ProgressBar progressBar;
    private TextView tv_next;

    private Banner bannerLogo;
    private BannerAdapter<String> bannerAdapter;
    private List<String> bannerImages = new ArrayList<>();

    public synchronized void connectFail(int type) {
        Log.i("hu", "*******connectFail");
        SampleConnection.USER = "";
        SampleConnection.USER_STATE = 0;

        Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
    }

    public synchronized void connectSuccess(JSONObject json, int type) {
        int cmd = -1;
        int result = -1;
        String str;
        try {
            str = json.getString("cmd");
            cmd = Integer.parseInt(str);
            str = json.getString("cw");
            result = Integer.parseInt(str);
        } catch (Exception e) {
            connectFail(type);
            return;
        }
        if (cmd == 18) {
            if (result == 0) {
                setData(json);
            } else {

            }
        } else if (cmd == 20) {
            if (result == 0) {
                setDiscussData(json);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        } else {
            connectFail(type);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        // 启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        context = getApplicationContext();

        Bundle bundle = this.getIntent().getExtras();
        int num = 0;
        if (bundle != null)
            ID = bundle.getString("id");
        Log.i("hu", "*********商品id=" + ID);
        init();
        getProductDetails();
        _dbad = new Cartdb(this);
        updateCartCount();

        //getDiscussData(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSafoneConnection != null) {
            mSafoneConnection.close();
        }
    }

    private void init() {
        titleBackImageBtn = (ImageView) findViewById(R.id.titleBackImageBtn);
        titleBackImageBtn.setOnClickListener(mListener);
        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        btn_bay = (Button) findViewById(R.id.btn_bay);
        btn_bay.setOnClickListener(this);
        iv_cart = (ImageView) findViewById(R.id.iv_cart);
        iv_cart.setOnClickListener(this);
        ll_addcart = (LinearLayout) findViewById(R.id.ll_addcart);
        ll_addcart.setOnClickListener(this);
        iv_addcart = (ImageView) findViewById(R.id.iv_addcart);

//        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_detail = (ImageView) findViewById(R.id.iv_detail);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_str = (TextView) findViewById(R.id.tv_str);
        tv_price = (TextView) findViewById(R.id.tv_price);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_next.setOnClickListener(this);

        badgeCart = new BadgeView(this);
        badgeCart.setTargetView(iv_cart);
        //badgeCart.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
        badgeCart.setTextSize(6.0f);

        mAmountView = (AmountView) findViewById(R.id.amountview);
        mAmountView.setGoods_storage(50);
        mAmountView.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
            @Override
            public void onAmountChange(View view, int amount) {
                //  Toast.makeText(getApplicationContext(), "Amount=>  " + amount, Toast.LENGTH_SHORT).show();
                number = amount;
            }
        });

        lv_list = (ChangeListView) findViewById(R.id.lv_userlist);
        lv_list.setOnItemClickListener( //设置选项被单击的监听器
                new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                        //重写选项被单击事件的处理方法
                        lv_list.listItemAdapter.setSelectItem(position);
                        lv_list.listItemAdapter.notifyDataSetInvalidated();
                    }
                });
        // setList();

        tv_detail = (TextView) findViewById(R.id.tv_detail);
        tv_discuss = (TextView) findViewById(R.id.tv_discuss);
        tv_guige = (TextView) findViewById(R.id.tv_guige);
        tv_detail.setOnClickListener(this);
        tv_discuss.setOnClickListener(this);

        bannerLogo = (Banner) findViewById(R.id.banner_logo);
        bannerAdapter = new BannerAdapter<String>(bannerImages) {
            @Override
            protected void bindTips(TextView tv, String s) {

            }

            @Override
            public void bindImage(ImageView imageView, String s) {
                x.image().bind(imageView, s, new ImageOptions.Builder().setLoadingDrawableId(R.drawable.img_default).setFailureDrawableId(R.drawable.img_default).setUseMemCache(true).build());
            }
        };
        bannerLogo.setBannerAdapter(bannerAdapter);

    }

    private void getDiscussData(int number) { //从第1页开始
        Log.i("hu", "*******getDiscussData()");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cmd", "19");
        map.put("id", ID);  //商品id号
        map.put("page", String.valueOf(number));

        if (mSafoneConnection == null)
            mSafoneConnection = new SampleConnection(DescriptionActivity.this, 0);
        mSafoneConnection.connectService1(map);
    }

    /**
     * 更新购物车记录数
     */
    private void updateCartCount() {
        Log.i("hu", "*****updateCartCount()");
        _dbad.open();
        long unReadCount = _dbad.getCountItem();
        Log.i("hu", "****unReadCount=" + unReadCount);
        if (unReadCount > 0) {
            badgeCart.setBadgeCount((int) unReadCount);
            //badgeCart.setText(unReadCount+"");
            //badgeCart.show();
        } else {
            badgeCart.setBadgeCount(0);
        }
        _dbad.close();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.tv_detail:
                switchTwoButton(v.getId());
                break;
            case R.id.tv_discuss:
                switchTwoButton(v.getId());
                break;
            case R.id.btn_bay:
                if (isLogined() == 0) {
                    Intent intent3 = new Intent();
                    intent3.setClass(DescriptionActivity.this, LoginActivity.class);
                    startActivityForResult(intent3, 11);
                } else {
                    Intent intent1 = new Intent();
                    intent1.setClass(DescriptionActivity.this, ConfirmOrderActivity.class);
                    intent1.putExtra("iscart", 0);
                    intent1.putExtra("id", id);
                    intent1.putExtra("name", name);
                    intent1.putExtra("describe", describe);
                    intent1.putExtra("logourl", logourl);
                    intent1.putExtra("price", price);
                    intent1.putExtra("amount", number);
                    startActivityForResult(intent1, 2);
                }
                /*menuWindow = new PayPopupWindow(context, this);
                menuWindow.showAtLocation(findViewById(R.id.mainLayout),
						Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); */
                break;
            case R.id.iv_cart:
                if (isLogined() == 0) {
                    Intent intent3 = new Intent();
                    intent3.setClass(DescriptionActivity.this, LoginActivity.class);
                    startActivityForResult(intent3, 1);
                } else {
                    Intent intent1 = new Intent(this, ShoppingCartActivity.class);
                    startActivityForResult(intent1, 11);
                }
                break;
            case R.id.ll_addcart:
                if (isLogined() == 0) {
                    Toast.makeText(this, getString(R.string.pleaselogin), Toast.LENGTH_SHORT).show();
                } else {
                    _dbad.open();
                    _dbad.insertItem(id, name, describe, price, 1, logourl, 0);
                    _dbad.close();
                    //Intent intent2 = new Intent(this, ShoppingCartActivity.class);
                    //startActivityForResult(intent2,1);

                    int[] startLocation = new int[2];// 一个整型数组，用来存储按钮的在屏幕的X、Y坐标
                    iv_addcart.getLocationInWindow(startLocation);// 这是获取购买按钮的在屏幕的X、Y坐标（这也是动画开始的坐标）
                    Log.i("hu", "起点：startLocation[0]=" + startLocation[0] + " startLocation[1]=" + startLocation[1]);
                    ball = new ImageView(this);// buyImg是动画的图片，我的是一个小球（R.drawable.sign）
                    ball.setImageResource(R.drawable.sign);// 设置buyImg的图片
                    setAnim(ball, startLocation);// 开始执行动画
                }
                break;
            case R.id.tv_next:
                loadData(discuss_page + 1);
                break;
        }
    }

    //为弹出窗口实现监听类
    private OnClickListener itemsOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.takePhotoBtn:

                    break;
                default:
                    break;
            }
        }
    };

    private int isLogined() {
        int result = 0;
        try {
            result = mSettingPref.getInt("Status", 0);
        } catch (Exception e) {
        }
        return result;
    }

    private void switchTwoButton(int id) {
        if (id == R.id.tv_detail) {
            if (selectedBtn != R.id.tv_detail) {
                tv_detail.setBackgroundResource(R.drawable.qianse);
                tv_detail.setTextColor(this.getResources().getColor(R.color.lightblue));
                tv_discuss.setBackgroundColor(this.getResources().getColor(R.color.lightblue));
                tv_discuss.setTextColor(this.getResources().getColor(R.color.white));
                selectedBtn = R.id.tv_detail;
                //setList();
                lv_list.setVisibility(View.GONE);
                iv_detail.setVisibility(View.VISIBLE);
                tv_next.setVisibility(View.GONE);
            }
        } else if (id == R.id.tv_discuss) {
            if (selectedBtn != R.id.tv_discuss) {
                tv_detail.setBackgroundColor(this.getResources().getColor(R.color.lightblue));
                tv_detail.setTextColor(this.getResources().getColor(R.color.white));
                tv_discuss.setBackgroundResource(R.drawable.qianse);
                tv_discuss.setTextColor(this.getResources().getColor(R.color.lightblue));
                selectedBtn = R.id.tv_discuss;
                getDiscussData(1);
                progressBar.setVisibility(View.VISIBLE);
                //setList(userid,date1,nr,tupian);
                iv_detail.setVisibility(View.GONE);
                lv_list.setVisibility(View.VISIBLE);
                if (discuss_page < discuss_totalpages) {
                    tv_next.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setList(List<String> userid, List<String> date1, List<String> nr, List<String> tupian) {
        if (discuss_page == 1) {
            Log.i("hu", "*********RebulidDiscuss");
            lv_list.RebulidDiscuss();
        }
        int len = userid.size();
        for (int i = 0; i < len; i++) {
            lv_list.addItem2(R.drawable.login_user, userid.get(i), date1.get(i), nr.get(i), tupian.get(i));
        }

        lv_list.listItemAdapter.notifyDataSetInvalidated();

        setListViewHeightBasedOnChildren(lv_list);
    }

    private void setList2() {
        lv_list.RebulidDiscuss();
        //  for(int i=0;i<mImageViewArray.length;i++){
        //lv_list.addItem2(R.drawable.login_user,"游客111","2017-01-28","是正品的东北大米，专人送货上门，很方便，米吃起来很香，很满意，以后还会购买，好评。");
        //lv_list.addItem2(R.drawable.login_password,"游客22","2017-03-21","好！好！好！");
        //lv_list.addItem2(R.drawable.login_password,"游客333","2017-03-21","好！好！好！");
        //lv_list.addItem2(R.drawable.login_user,"游客44","2017-01-28","是正品的东北大米，专人送货上门，很方便，米吃起来很香，很满意，以后还会购买，好评。");
        //  }
        // if(mImageViewArray.length>0){
        //lv_carlist.listItemAdapter.setSelectItem(1);
        lv_list.listItemAdapter.notifyDataSetInvalidated();
        // }
        setListViewHeightBasedOnChildren(lv_list);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            Log.i("hu", "**********i=" + i + " listItem.getMeasuredHeight()=" + listItem.getMeasuredHeight());
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        Log.i("hu", "**********params.height=" + params.height);
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        public void onClick(View paramView) {
            if (paramView.getId() == R.id.titleBackImageBtn) {
                DescriptionActivity.this.finish();
            }

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == 11) {
                    Log.i("hu", "******购物车返回");
                    updateCartCount();
                }
                break;
            case 2:
                break;
            case 21:   //登录返回
                Log.i("hu", "******登录成功返回");
            /* menuWindow = new PayPopupWindow(context, this);
             menuWindow.showAtLocation(findViewById(R.id.mainLayout),
    			 	Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);*/
                if (requestCode == 11) {
                    Intent intent1 = new Intent();
                    intent1.setClass(DescriptionActivity.this, ConfirmOrderActivity.class);
                    intent1.putExtra("iscart", 0);
                    intent1.putExtra("id", id);
                    intent1.putExtra("name", name);
                    intent1.putExtra("logourl", logourl);
                    intent1.putExtra("price", price);
                    intent1.putExtra("amount", number);
                    startActivityForResult(intent1, 1);
                }
                break;
        }
    }

    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mImageViews[position % mImageViews.length]);

        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(mImageViews[position % mImageViews.length], 0);
            return mImageViews[position % mImageViews.length];
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        setImageBackground(arg0 % mImageViews.length);
    }

    /**
     * 设置选中的tip的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void OnPayMoney(String name, String phone, String address,
                           int payment) {
        // TODO Auto-generated method stub
        Log.e("hu", "*****pay*****name=" + name + " phone=" + phone + " address=" + address + " payment=" + payment);
    }

    private Map<String, Object> genProductDetails() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cmd", "17");
        map.put("id", ID);
        return map;
    }

    private void getProductDetails() {
        Log.i("hu", "*******genProductDetails()  id=" + ID);
        if (mSafoneConnection == null)
            mSafoneConnection = new SampleConnection(this, 0);
        mSafoneConnection.connectService1(genProductDetails());
    }

    private void setData(JSONObject json) {
        try {
            id = json.getString("id");
        } catch (Exception e) {
        }
        try {
            name = json.getString("name");
        } catch (Exception e) {
        }
        try {
            describe = json.getString("describe");
        } catch (Exception e) {
        }
        try {
            logourl = json.getString("logo");
        } catch (Exception e) {
        }
        try {
            price = json.getString("jg");
        } catch (Exception e) {
        }
        try {
            normal = json.getString("guige");
        } catch (Exception e) {
        }
        try {
            pictureurl = json.getString("tupian");
            // pictureurl = logourl;
        } catch (Exception e) {
        }
        Log.i("hu", "*********name=" + name + " describe=" + describe + " logourl="
                + logourl + " price=" + price + " normal=" + normal + " pictureurl=" + pictureurl);

        tv_guige.setText(normal);
        //用ImageLoader加载图片
//		ImageLoader.getInstance().displayImage(logourl, iv_logo
//				,new ImageLoaderPicture(this).getOptions(),new SimpleImageLoadingListener());
//        ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.img_default)
//                .setFailureDrawableId(R.drawable.img_default).setUseMemCache(true)
//                .setImageScaleType(ImageView.ScaleType.FIT_CENTER).build();
//        x.image().bind(iv_logo, logourl, options);
        bannerImages.add(logourl);
        bannerLogo.notifyDataHasChanged();
        //用ImageLoader加载图片
//        ImageLoader.getInstance().displayImage(pictureurl, iv_detail
//                , new ImageLoaderPicture(this).getOptions(), new SimpleImageLoadingListener());
        ImageOptions options1 = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.img_default)
                .setFailureDrawableId(R.drawable.img_default).setUseMemCache(true).build();
        x.image().loadDrawable(pictureurl, options1, new Callback.CommonCallback<Drawable>() {
            @Override
            public void onSuccess(Drawable drawable) {
                int MaxHeight = iv_detail.getWidth() * 10;
                int picheight = (int) ((float) iv_detail.getWidth() / drawable.getMinimumWidth() * drawable.getMinimumHeight());
                iv_detail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, picheight));
                iv_detail.setMaxHeight(MaxHeight);
                iv_detail.setImageDrawable(drawable);
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
        tv_title.setText(name);
        tv_str.setText(describe);
        tv_price.setText(price);

        switchTwoButton(R.id.tv_detail);
    }

    private void setDiscussData(JSONObject json) {
        Log.i("hu", "setDiscussData*********json=" + json.toString());
        tupian.clear();
        nr.clear();
        userid.clear();
        date1.clear();


        progressBar.setVisibility(View.GONE);
        String str, data;
        try {
            str = json.getString("page");
            discuss_page = Integer.parseInt(str);
        } catch (Exception e) {
        }
        try {
            str = json.getString("count");
            discuss_count = Integer.parseInt(str);
            discuss_totalpages = (discuss_count + 9) / 10;
            if (discuss_page < discuss_totalpages) {
                if (selectedBtn != R.id.tv_detail) {
                    tv_next.setVisibility(View.VISIBLE);
                }
            } else {
                tv_next.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }

        try {
            data = json.getString("data");
            JSONArray array = new JSONArray(data);
            int len = array.length();
            len = (len > 10) ? 10 : len;
            if (len < 1) {
                return;
            }
            Log.i("hu", "*******array.length()=" + array.length());
            for (int i = 0; i < len; i++) {
                JSONObject obj = array.getJSONObject(i);
                String str1 = "";
                try {
                    str1 = obj.getString("tupian");
                } catch (Exception e) {
                }
                Log.i("hu", "*******i=" + i + " tupian=" + str1);
                tupian.add(i, str1);
                try {
                    str1 = "";
                    str1 = obj.getString("nr");
                } catch (Exception e) {
                }
                Log.i("hu", "*******i=" + i + " nr=" + str1);
                nr.add(i, str1);
                try {
                    str1 = "";
                    str1 = obj.getString("uid");
                } catch (Exception e) {
                }
                Log.i("hu", "*******i=" + i + " userid=" + str1);
                userid.add(i, str1);
                try {
                    str1 = "";
                    str1 = obj.getString("time");
                } catch (Exception e) {
                }
                Log.i("hu", "*******i=" + i + " time=" + str1);
                date1.add(i, str1);
            }
            setList(userid, date1, nr, tupian);
        } catch (Exception e) {
        }

    }

    /**
     * @param
     * @return void
     * @throws
     * @Description: 创建动画层
     */
    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setId(Integer.MAX_VALUE);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    private View addViewToAnimLayout(final ViewGroup parent, final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }

    private void setAnim(final View v, int[] startLocation) {
        anim_mask_layout = null;
        anim_mask_layout = createAnimLayout();
        anim_mask_layout.addView(v);//把动画小球添加到动画层
        final View view = addViewToAnimLayout(anim_mask_layout, v,
                startLocation);
        int[] endLocation = new int[2];// 存储动画结束位置的X、Y坐标
        iv_cart.getLocationInWindow(endLocation);// shopCart是那个购物车
        Log.i("hu", "终点：endLocation[0]=" + endLocation[0] + " endLocation[1]=" + endLocation[1]);

        // 计算位移
        //int endX = 0 - startLocation[0] + 40;// 动画位移的X坐标
        int endX = endLocation[0] - startLocation[0];// 动画位移的X坐标
        int endY = endLocation[1] - startLocation[1];// 动画位移的y坐标
        TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(800);// 动画的执行时间
        view.startAnimation(set);
        // 动画监听事件
        set.setAnimationListener(new AnimationListener() {
            // 动画的开始
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
                //buyNum++;//让购买数量加1
                //buyNumView.setText(buyNum + "");//
                //buyNumView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                //buyNumView.show();
                updateCartCount();
            }
        });
    }

    private boolean loadData(int page) {
        Log.i("hu", "******loadData() page=" + page + " pageNum=" + discuss_totalpages);
        // new getNews().execute(page);
        if (page <= discuss_totalpages) {
            getDiscussData(page);
            progressBar.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }


   /* @Override
    public boolean onLoadNextPage() {
        Log.i("hu","******onLoadNextPage()");

        return loadData(++mEndPageNum);
    }*/

}
