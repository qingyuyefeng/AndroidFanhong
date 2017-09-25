package com.fanhong.cn.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.AgentWebActivity;
import com.fanhong.cn.App;
import com.fanhong.cn.FaceRecognitionIntroductionActivity;
import com.fanhong.cn.GardenSelecterActivity;
import com.fanhong.cn.LoginActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.StoreActivity;
import com.fanhong.cn.expressage.ExpressHomeActivity;
import com.fanhong.cn.fenxiao.CheckJoinedActivity;
import com.fanhong.cn.fenxiao.FenXiaoActivity;
import com.fanhong.cn.models.BannerModel;
import com.fanhong.cn.repair.EmergencyUnlockActivity;
import com.fanhong.cn.repair.RepairActivity;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.MySharedPrefUtils;
import com.fanhong.cn.verification.InputYuyueActivity;
import com.sivin.Banner;
import com.sivin.BannerAdapter;

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
 * Created by Administrator on 2017/9/1.
 */
@ContentView(R.layout.fragment_home1)
public class HomeView2 extends BaseFragment {
    @ViewInject(R.id.show_cellname)
    private TextView cellName;
    @ViewInject(R.id.show_notice)
    private TextView showNotice;
    @ViewInject(R.id.home_banner)
    private Banner banner;
    @ViewInject(R.id.ylgh)
    private TextView ylgh;
    @ViewInject(R.id.xxyl)
    private TextView xxyl;
    @ViewInject(R.id.mydy)
    private TextView mydy;
    @ViewInject(R.id.hwwl)
    private TextView hwwl;

    private TextView[] textViews;
    private String[] urls = {"https://m.quyiyuan.com",
            "http://i.meituan.com/chongqing?cid=2&stid_b=1&cateType=poi",
            "https://m.maoyan.com",
            "http://m.ctrip.com/webapp/attractions/index.html#!/index?from=http%3A%2F%2Fm.ctrip.com%2Fhtml5%2F"};

    private List<String> strings = new ArrayList<>();

    private List<BannerModel> bannerModelList = new ArrayList<>();
    private BannerAdapter bannerAdapter;
    private SharedPreferences mSharedPref;
    private String uid = "";
    private String managerName = "", managerTel = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        mSharedPref = MySharedPrefUtils.getSharedPref(getActivity());
        getGonggaoData();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
                        intent.setClass(getActivity(), FaceRecognitionIntroductionActivity.class);
                        intent.putExtra("position", 0);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(getActivity(), FaceRecognitionIntroductionActivity.class);
                        intent.putExtra("position", 1);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(getActivity(), StoreActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        textViews = new TextView[]{ylgh, xxyl, mydy, hwwl};
        for (int i = 0; i < textViews.length; i++) {
            final int finalI = i;
            textViews[finalI].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AgentWebActivity.class);
                    intent.putExtra("ishome", true);
                    intent.putExtra("url", urls[finalI]);
                    startActivity(intent);
                }
            });
        }
    }

    @Event({R.id.image_choosecell, R.id.textView0, R.id.textView1, R.id.textView2,
            R.id.tv_homestore, R.id.tv_homeexpressage, R.id.tv_homedaiban, R.id.tv_homerepair, R.id.tv_homeunlock})
    private void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.image_choosecell: //选择小区
                if (isLogined() == 1) {
                    intent.setClass(getActivity(), GardenSelecterActivity.class);
                    getActivity().startActivityForResult(intent, 112);
                } else {
                    createDialog(0);
                }
                break;
            case R.id.textView0: //社区公告
                if (isLogined() == 1) {
                    if (!TextUtils.isEmpty(mSharedPref.getString("gardenName", ""))) {
                        getBaseActivity().setRadioButtonsChecked(3);
                    } else {
                        createDialog(1);
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.pleaselogin, Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.textView1: //物业之星
                if (isLogined() == 1) {
                    if (!TextUtils.isEmpty(mSharedPref.getString("gardenName", ""))) {
                        RequestParams param = new RequestParams(App.CMDURL);
                        param.addParameter("cmd", "81");
                        param.addParameter("id", mSharedPref.getString("gardenId", ""));
                        x.http().post(param, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                if (JsonSyncUtils.getJsonValue(result, "cw").equals("0")) {
                                    managerTel = JsonSyncUtils.getJsonValue(result, "tel");
                                    managerName = mSharedPref.getString("gardenName", "");
                                    new AlertDialog.Builder(getActivity()).setTitle(managerName).setMessage("联系电话:" + managerTel)
                                            .setPositiveButton("立即拨打", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //判断Android版本是否大于23
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                        int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

                                                        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                                                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},
                                                                    11);
                                                            return;
                                                        }
                                                    }
                                                    callManager();
                                                }
                                            }).setNegativeButton("取消", null).create().show();
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
                    } else {
                        createDialog(1);
                    }
                } else {
                    createDialog(0);
                }
                break;
            case R.id.textView2: //招商代理
                //TODO  判断登录状态是否需要改在下一层
                if (isLogined() == 1) {
                    checkIfjoined(uid);
                } else {
//                    createDialog(0);
                    startActivity(new Intent(getActivity(), FenXiaoActivity.class));
                }
                break;
            case R.id.tv_homestore: //社区卖场
                intent.setClass(getActivity(), StoreActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_homeexpressage://代发快递
                intent.setClass(getActivity(), ExpressHomeActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_homedaiban://代办年审
                intent.setClass(getActivity(), InputYuyueActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_homerepair://上门维修
                intent.setClass(getActivity(), RepairActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_homeunlock://紧急开锁
                intent.putExtra("gardenName", mSharedPref.getString("gardenName", ""));
                intent.putExtra("gardenId", mSharedPref.getString("gardenId", ""));
                intent.setClass(getActivity(), EmergencyUnlockActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void callManager() {
        Intent i = new Intent(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:" + managerTel);
        i.setData(uri);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public synchronized void setFragment(int type, String str) {
        switch (type) {
            case 11:
                cellName.setText(str);//选择小区回调
                break;
        }
    }

    private void getBannerImg() {
        bannerModelList.add(new BannerModel().setImageUrl("assets://images/title3.jpg"));
        bannerModelList.add(new BannerModel().setImageUrl("assets://images/banner.jpg"));
        bannerModelList.add(new BannerModel().setImageUrl("assets://images/top_banner.jpg"));
        banner.notifyDataHasChanged();
    }

    private void getGonggaoData() {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "43");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
//                    Log.i("xq","首页公告==>"+s);
                    String string = jsonObject.optString("data").replace("[", "").replace("]", "").replace("\"", "");
                    String[] strs = string.split(",");
                    if (strs.length > 0) {
                        for (int i = 0; i < strs.length; i++) {
                            strings.add(strs[i]);
                        }
                        showNotice.setText(strings.get(0));
                        //公告内容动画显示
                        translateImpl(showNotice);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private int isLogined() {
        int result = 0;
        try {
            result = mSharedPref.getInt("Status", 0);
        } catch (Exception e) {
        }
        return result;
    }

    private void checkIfjoined(String str) {
        RequestParams params = new RequestParams(App.CMDURL);
        params.addBodyParameter("cmd", "69");
        params.addBodyParameter("uid", str);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                String data = JsonSyncUtils.getJsonValue(s, "data");
                Log.i("xq", "data==>" + data);
                if (data.equals("0")) {
                    startActivity(new Intent(getActivity(), FenXiaoActivity.class));
                } else if (data.equals("1")) {
//                    createDialog(2);
                    Intent intent = new Intent(getActivity(), CheckJoinedActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "登录状态异常", Toast.LENGTH_SHORT).show();
                }
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

    private void createDialog(int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (i) {
            case 0:
                builder.setTitle("你还没有登录哦");
                builder.setMessage("是否立即登录？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent,100);
                    }
                });
                break;
            case 1:
                builder.setTitle("你还没选择小区");
                builder.setMessage("是否立即去选择小区？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), GardenSelecterActivity.class);
                        getActivity().startActivityForResult(intent, 112);
                    }
                });
                break;
//            case 2:
//                builder.setTitle("你已注册过此系统");
//                builder.setMessage("是否立即查询？");
//                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(getActivity(), CheckJoinedActivity.class);
//                        startActivity(intent);
//                    }
//                });
//                break;
        }
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        uid = mSharedPref.getString("UserId", "");
        String str = mSharedPref.getString("gardenName", "");
        cellName.setText(str);
    }
}
