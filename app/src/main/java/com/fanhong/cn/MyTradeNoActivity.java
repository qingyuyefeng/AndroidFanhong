package com.fanhong.cn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.PullToLoadPageListView.OnLoadingPageListener;
import com.fanhong.cn.listviews.PullToLoadPage;
import com.fanhong.cn.listviews.TradeNoListView;
import com.fanhong.cn.util.TopBarUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

public class MyTradeNoActivity extends SampleActivity implements OnItemClickListener, OnLoadingPageListener {
	private SharedPreferences mSettingPref;
	private SampleConnection mSafoneConnection;
	private TradeNoListView lv_list;
	private Context mcontext;
	private String userid;
	private int mEndPageNum = 0;
	int count = 0;//数据总条数. 10条为一页
	int pageNum = 0; //数据总页数
	int pageid = 0; //当前下载页数
	private ProgressBar mProgressBar;

	public synchronized void connectFail(int type) {

		SampleConnection.USER = "";
		SampleConnection.USER_STATE = 0;

		Toast.makeText(this, getString(R.string.getdatafailed), Toast.LENGTH_SHORT).show();
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
			Log.i("xqTradeNo","cw==>"+result);
		} catch (Exception e) {
			Log.i("xqTradeNo1","cw==>"+result);
			connectFail(type);
			return;
		}
		if(cmd == 26){
			if(result == 0){
				String data = null;
				try {
					data = json.getString("data");
					str = json.getString("page");
					pageid = Integer.parseInt(str);
					if(pageid == 1){
						str = json.getString("count");
						count = Integer.parseInt(str);
						Log.i("hu","收到订单总条数："+count);
						pageNum = count/10;
						if(count%10 > 0)
							pageNum++;
					}
				} catch (Exception e) {
					return;
				}
				setData(data,pageid);
			}else{
				lv_list.finishLoadNextPage();
				mProgressBar.setVisibility(View.GONE);
			}
		} else {
			connectFail(type);
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mytradeno);
		TopBarUtil.initStatusBar(this);
		mcontext = getApplicationContext();
		mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
		((TextView)findViewById(R.id.tv_title)).setText("我的订单");
		findViewById(R.id.img_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		lv_list = (TradeNoListView) findViewById(R.id.lv_list);
 		/*lv_list.setOnItemClickListener( //设置选项被单击的监听器
        new OnItemClickListener(){
      	   public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
      		   //重写选项被单击事件的处理方法
      		   lv_list.listItemAdapter.setSelectItem(position);
      		   lv_list.listItemAdapter.notifyDataSetInvalidated();
      		   Intent intent1 = new Intent();
      		   intent1.setClass(MyTradeNoActivity.this, OrderDetailActivity.class);
      		   intent1.putExtra("uid", userid);
      		   String orderid = (String) lv_list.listItems.get(position).get("orderid");
      		   intent1.putExtra("iid", orderid);
      		   startActivityForResult(intent1, 1);
      	   }
        });*/
		View rootView = this.getLayoutInflater().inflate(R.layout.list_pull_to_load, null);
		lv_list.setPullToLoadNextPageView(new PullToLoadPage(R.string.pull_to_load_next_page, rootView) {
			@Override
			public int takePageNum() {
				return mEndPageNum;
			}
		});
		lv_list.setOnItemClickListener(this);
		lv_list.setOnLoadingPageListener(this);
		mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
		mProgressBar.setVisibility(View.VISIBLE);

		getTradeNO(1);
	}

	private void getTradeNO(int number){
		Map<String, Object> map = new HashMap<String, Object>();
		userid = mSettingPref.getString("UserId", "");
		map.put("cmd", "25");
		map.put("uid", userid);  //下订单用户ID
		map.put("page",number+"");

		if(mSafoneConnection == null)
			mSafoneConnection = new SampleConnection(MyTradeNoActivity.this, 0);
		mSafoneConnection.connectService1(map);

//		userid = mSettingPref.getString("UserId", "");
//		RequestParams params = new RequestParams(App.CMDURL);
//		params.addBodyParameter("cmd","25");
//		params.addBodyParameter("uid",userid);
//		params.addBodyParameter("page",number+"");
//		x.http().post(params, new Callback.CommonCallback<String>() {
//			@Override
//			public void onSuccess(String result) {
//
//			}
//
//			@Override
//			public void onError(Throwable ex, boolean isOnCallback) {
//
//			}
//
//			@Override
//			public void onCancelled(CancelledException cex) {
//
//			}
//
//			@Override
//			public void onFinished() {
//
//			}
//		});
	}


	private void setData(String data ,int page_id){
		lv_list.finishLoadNextPage();
		mProgressBar.setVisibility(View.GONE);
		try {
			JSONArray array = new JSONArray(data);
			if(array.length()<1){
				return;
			}
			if(page_id == 1){
				lv_list.Bulid();
				mEndPageNum = 0;
			}
			for(int i=0;i<array.length();i++){
				JSONObject obj = array.getJSONObject(i);
				String orderid = obj.getString("id");
				String ordernum = obj.getString("ddh");
				String ordertime = obj.getString("time");
				String goods = obj.getString("goods");
				String price = obj.getString("zjje");
				lv_list.addItem(orderid,ordernum,ordertime,goods,price);
			}
			if(page_id == 1)
				lv_list.listItemAdapter.notifyDataSetInvalidated();

		} catch (Exception e) {}

	}

	private boolean loadData(int page){
		// new getNews().execute(page);
		if(page < pageNum){
			getTradeNO(page+1);
			mProgressBar.setVisibility(View.VISIBLE);
			return true;
		}
		return false;
	}

	@Override
	public boolean onLoadPrevPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLoadNextPage() {

		return loadData(++mEndPageNum);
	}

	@Override
	public boolean hasPrevPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasNextPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		// TODO Auto-generated method stub
		Intent intent1 = new Intent();
		intent1.setClass(MyTradeNoActivity.this, OrderDetailActivity.class);
		intent1.putExtra("uid", userid);
		String orderid = (String) lv_list.listItems.get(position).get("orderid");
		intent1.putExtra("iid", orderid);
		startActivityForResult(intent1, 1);
	}
}
