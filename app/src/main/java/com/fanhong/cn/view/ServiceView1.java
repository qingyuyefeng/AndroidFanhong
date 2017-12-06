package com.fanhong.cn.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.AgentWebActivity;
import com.fanhong.cn.App;
import com.fanhong.cn.LoginActivity;
import com.fanhong.cn.R;
import com.fanhong.cn.StoreActivity;
import com.fanhong.cn.community.CommunityChatRoomActivity;
import com.fanhong.cn.fenxiao.FenXiaoActivity;
import com.fanhong.cn.listviews.MyGridView;
import com.fanhong.cn.party.ViewPagerActivity;
import com.fanhong.cn.repair.EmergencyUnlockActivity;
import com.fanhong.cn.repair.RepairActivity;
import com.fanhong.cn.usedmarket.ShopActivity;
import com.fanhong.cn.util.JsonSyncUtils;
import com.fanhong.cn.util.MySharedPrefUtils;
import com.fanhong.cn.util.TopBarUtil;
import com.fanhong.cn.verification.InputYuyueActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

public class ServiceView1 extends BaseFragment {
    public static final int PAGER_INDEX = 2;

    //定义数组来存放按钮图片
    private int mImageViewArray1[] = {
            R.drawable.service_store, R.drawable.service_es,
//			R.drawable.service_pay,
            R.drawable.service_dai,
            R.drawable.service_distribution,
            R.drawable.service_lock,
//          R.drawable.service_park
            R.drawable.service_fix,
//			,R.drawable.service_jd, R.drawable.service_kd, R.drawable.service_clean,
            R.drawable.service_talk,
            R.drawable.service_dang_1
    };

    //定义数组文字
    private int mTextviewArray1[] = {
            R.string.service_store, R.string.service_es,
//			R.string.service_pay,
            R.string.service_dai,
            R.string.service_zsdl,
            R.string.service_lock,
//          R.string.service_park
            R.string.service_fix,
//			,R.string.service_jd, R.string.service_kd, R.string.service_clean,
            R.string.service_talk,
            R.string.service_dang
    };

    private int mImageViewArray2[] = {R.drawable.service_mt, R.drawable.service_dz, R.drawable.service_tb,
            R.drawable.service_jds, R.drawable.service_wph, R.drawable.service_yms,
            R.drawable.service_xc, R.drawable.service_qne, R.drawable.service_tn,
            R.drawable.service_tc};
    private int mTextviewArray2[] = {R.string.service_mt, R.string.service_dz, R.string.service_tb,
            R.string.service_jds, R.string.service_wph, R.string.service_yms,
            R.string.service_xc, R.string.service_qne, R.string.service_tn,
            R.string.service_tc};
    private int mUrlArray2[] = {R.string.url_meituan, R.string.url_dianping, R.string.url_taobao,
            R.string.url_jingdong, R.string.url_weiping, R.string.url_yamaxun,
            R.string.url_xiecheng, R.string.url_quna, R.string.url_tuniu,
            R.string.url_tongcheng};

    private int mImageViewArray3[] = {R.drawable.service_hj, R.drawable.service_funhos, R.drawable.service_ks,
            R.drawable.service_gwy, R.drawable.service_zc, R.drawable.service_my};
    private int mTextviewArray3[] = {R.string.service_hj, R.string.service_funhos, R.string.service_ks,
            R.string.service_gwy, R.string.service_zc, R.string.service_my};
    private int mUrlArray3[] = {R.string.url_huzhang, R.string.url_quyy, R.string.url_teacher,
            R.string.url_gongwuyuan, R.string.url_zhichen, R.string.url_muying};
    MyGridView opergridview1, opergridview2, opergridview3;
    ImageAdapter typeAdapter1, typeAdapter2, typeAdapter3;

    private SharedPreferences mSettingPref;
    private View serView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        serView = inflater.inflate(R.layout.fragment_service, null);
        return serView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSettingPref = this.getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);

//		tv_choosecell = (TextView) view.findViewById(R.id.tv_choosecell);
//		ll_choosegarden = (LinearLayout) view.findViewById(R.id.ll_choosegarden);
//		ll_choosegarden.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//判断是否登录才允许跳转
//				if(isLogined() == 1){
//					Intent intent2 = new Intent(ServiceView1.this.getActivity(), GardenSelecterActivity.class);
//					ServiceView1.this.getBaseActivity().startActivityForResult(intent2,12);
//				}else {
//					createDialog();
//				}
//			}
//		});
//		try{
//			String str = mSettingPref.getString("gardenName", "");
//			if(str.length() > 0)
//				tv_choosecell.setText(str);
//			else
//				tv_choosecell.setText(getString(R.string.choosecell));
//		}catch (Exception e) {}

        opergridview1 = (MyGridView) view.findViewById(R.id.opergridview1);
        opergridview2 = (MyGridView) view.findViewById(R.id.opergridview2);
        opergridview3 = (MyGridView) view.findViewById(R.id.opergridview3);
        setGridView1();
        setGridView2();
        setGridView3();
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("你还没有登录哦");
        builder.setMessage("是否立即登录？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ServiceView1.this.getActivity(), LoginActivity.class);
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

    public void setGridView1() {
        typeAdapter1 = new ImageAdapter(this.getActivity(), mImageViewArray1, mTextviewArray1);
        opergridview1.setAdapter(typeAdapter1);
        opergridview1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                oper1(position);
                typeAdapter2.SetSelItem(-1);
                typeAdapter2.notifyDataSetChanged();
            }
        });

        opergridview1.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float currentXPosition = event.getX();
                    float currentYPosition = event.getY();
                    int position = opergridview1.pointToPosition((int) currentXPosition, (int) currentYPosition);
                    //m_gv_touch_down = true;
                    if (position != typeAdapter1.GetSelItem()) {
                        typeAdapter1.SetSelItem(position);
                        typeAdapter1.notifyDataSetChanged();
                    }

                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float currentXPosition = event.getX();
                    float currentYPosition = event.getY();
                    int position = opergridview1.pointToPosition((int) currentXPosition, (int) currentYPosition);
                    //m_gv_touch_down = true;
                    if (position != typeAdapter1.GetSelItem()) {
                        typeAdapter1.SetSelItem(-1);
                        typeAdapter1.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });
    }

    public void setGridView2() {
        typeAdapter2 = new ImageAdapter(this.getActivity(), mImageViewArray2, mTextviewArray2);
        opergridview2.setAdapter(typeAdapter2);
        opergridview2.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                oper2(position);
                typeAdapter2.SetSelItem(-1);
                typeAdapter2.notifyDataSetChanged();
            }
        });
    }

    public void setGridView3() {
        typeAdapter3 = new ImageAdapter(this.getActivity(), mImageViewArray3, mTextviewArray3);
        opergridview3.setAdapter(typeAdapter3);
        opergridview3.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                oper3(position);
                typeAdapter3.SetSelItem(-1);
                typeAdapter3.notifyDataSetChanged();
            }
        });
    }

    // 继承BaseAdapter
    public class ImageAdapter extends BaseAdapter {
        // 上下文
        private Context mContext;
        private int m_nSelItem = -1;
        private int[] imageid, textid;

        // 构造方法
        public ImageAdapter(Context c, int[] imageid1, int[] textid1) {
            mContext = c;
            imageid = imageid1;
            textid = textid1;
        }

        // 组件个数
        public int getCount() {
            return imageid.length;
        }

        // 当前组件
        public Object getItem(int position) {
            return null;
        }

        public void SetSelItem(int nItem) {
            m_nSelItem = nItem;
        }

        public int GetSelItem() {
            return m_nSelItem;
        }

        // 当前组件id
        public long getItemId(int position) {
            return 0;
        }

        // 获得当前视图
        public View getView(int position, View convertView, ViewGroup parent) {
            // 声明图片视图
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View v = null;
            ImageView imageView = null;
            TextView tv = null;
            if (convertView == null) {
                // 实例化图片视图
                v = inflater.inflate(R.layout.operatelistview, null);
                // 设置图片视图属性
                v.setPadding(4, 4, 4, 4);
            } else {
                v = (View) convertView;
            }
            // 获得ImageView对象
            imageView = (ImageView) v
                    .findViewById(R.id.ItemImage);
            // 设置图片视图属性
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(4, 4, 4, 4);
            //imageView.setImageResource(imageid[position]);
            Drawable im = ServiceView1.this.getResources().getDrawable(imageid[position]);
            imageView.setImageDrawable(im);
            // 获得TextView对象
            tv = (TextView) v.findViewById(R.id.ItemTitle);
            String str = ServiceView1.this.getString(textid[position]);
            // 为TextView设置操作命令
            tv.setText(str);


 		/*	if(position == m_nSelItem)
            {
 				if(!isadministrator)
 					imageView.setImageResource(mThumbIds[position]);
 				else
 					imageView.setImageResource(mThumbIds[position+1]);
 				tv.setTextColor(Color.argb(0xff, 0x32, 0x99, 0xfe));
    		}
    		else
    		{
    			if(!isadministrator)
    				imageView.setImageResource(mThumbIds_off[position]);
    			else
    				imageView.setImageResource(mThumbIds_off[position+1]);
				tv.setTextColor(Color.argb(0xff, 0x93, 0x93, 0x93));
    		}*/

            return v;
        }

    }

    //服务区便民服务的点击事件
    private void oper1(int arg) {
        switch (arg) {
            case 0:
                startActivity(new Intent(ServiceView1.this.getActivity(), StoreActivity.class));
                break;
            case 1:
                startActivity(new Intent(ServiceView1.this.getActivity(), ShopActivity.class));
                break;
            case 2:
                startActivity(new Intent(ServiceView1.this.getActivity(), InputYuyueActivity.class));
                break;
            case 3:
                startActivity(new Intent(ServiceView1.this.getActivity(), FenXiaoActivity.class));
                break;
            //管线维修
            case 4:
                startActivity(new Intent(ServiceView1.this.getActivity(), EmergencyUnlockActivity.class));
                break;
            case 5:
                startActivity(new Intent(ServiceView1.this.getActivity(), RepairActivity.class));
                break;
            case 6:
                if(MySharedPrefUtils.getStatus(getActivity())!=1){
                    TopBarUtil.createDialog(getActivity(),0);
                }else if(TextUtils.isEmpty(MySharedPrefUtils.getGardenName(getActivity()))){
                    TopBarUtil.createDialog(getActivity(),1);
                }else
                    startActivity(new Intent(ServiceView1.this.getActivity(), CommunityChatRoomActivity.class));
                break;
            case 7:
                if(MySharedPrefUtils.getStatus(getActivity())==1){
                    RequestParams params = new RequestParams(App.CMDURL);
                    params.addParameter("cmd","93");
                    params.addParameter("tel",MySharedPrefUtils.getPhone(getActivity()));
                    x.http().post(params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            if(JsonSyncUtils.getJsonValue(result,"cw").equals("0")){
                                startActivity(new Intent(ServiceView1.this.getActivity(), ViewPagerActivity.class));
                            }else {
                                Toast.makeText(getActivity(),"暂无进入权限...",Toast.LENGTH_SHORT).show();
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
                }else {
                    TopBarUtil.createDialog(getActivity(),0);
                }
                break;
        }
    }

    private void oper2(int arg) {
        String url = ServiceView1.this.getString(mUrlArray2[arg]);
        String str = getString(mTextviewArray2[arg]);
        Intent intent2 = new Intent(ServiceView1.this.getActivity(), AgentWebActivity.class);
        intent2.putExtra("url", url);
        intent2.putExtra("title", str);
        startActivity(intent2);
    }

    private void oper3(int arg) {
        String url = ServiceView1.this.getString(mUrlArray3[arg]);
        String str = getString(mTextviewArray3[arg]);
        Intent intent2 = new Intent(ServiceView1.this.getActivity(), AgentWebActivity.class);
        intent2.putExtra("url", url);
        intent2.putExtra("title", str);
        startActivity(intent2);
    }

}
