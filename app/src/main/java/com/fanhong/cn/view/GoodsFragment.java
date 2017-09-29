package com.fanhong.cn.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;

import com.fanhong.cn.DescriptionActivity;
import com.fanhong.cn.PullToLoadPageListView.OnLoadingPageListener;
import com.fanhong.cn.R;
import com.fanhong.cn.listviews.ListViewgoodslist;
import com.fanhong.cn.listviews.PullToLoadPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class GoodsFragment extends BaseFragment implements OnItemClickListener, OnLoadingPageListener{
//	private final String cellNameStr[]={"双百上等东北大米","金龙鱼优质东北大米","双百上等东北大米","金龙鱼优质东北大米"};
//	private final String cellDetailStr[]={"2016新米 十月稻田长粒香大米5kg 东北大米 香米","金龙鱼 大米 优质东北大米 5kg/包 三江平原贡米 长粒香醇","2016新米 十月稻田长粒香大米5kg 东北大米 香米","金龙鱼 大米 优质东北大米 5kg/包 三江平原贡米 长粒香醇"};
//	private final String cellPriceStr[]={"￥50.8","￥61.5","￥50.8","￥61.7"};
	ListViewgoodslist lv_carlist;
	int type = 0;     //1：米，2：油，3：面，4：酒
	private ProgressBar mProgressBar;
	private int mEndPageNum = 0;
	private LoadData interface_loaddata;  //interface
	int count = 0;//数据总条数. 10条为一页
	int pageNum = 0; //数据总页数

	public void initData(int type, LoadData loadData){
    /*	if(type == 1){
    		cellNameStr[0] = "大米";
    	}else if(type == 2){
    		cellNameStr[0] = "食用油";
    	}else if(type == 3){
    		cellNameStr[0] = "面食";
    	}*/
		this.type = type;
		interface_loaddata = loadData;
	}

	private boolean loadData(int page)
	{
		Log.i("hu","******loadData() page="+page+" pageNum="+pageNum);
		// new getNews().execute(page);
		if(page < pageNum){
			interface_loaddata.OnLoadData(type, page+1);
			mProgressBar.setVisibility(View.VISIBLE);
			return true;
		}
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_goods, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		lv_carlist = (ListViewgoodslist)view.findViewById(R.id.lv_carlist);

     /*   lv_carlist.setOnItemClickListener( //设置选项被单击的监听器
        new OnItemClickListener(){
      	   public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
      		   //重写选项被单击事件的处理方法
      		//lv_carlist.listItemAdapter.setSelectItem(position);
      		//lv_carlist.listItemAdapter.notifyDataSetInvalidated();
      		 Intent intent2 = new Intent(GoodsFragment.this.getActivity(), DescriptionActivity.class);
      		 startActivity(intent2);
      	   }
        });*/
		// setList();
		View rootView = this.getActivity().getLayoutInflater().inflate(R.layout.list_pull_to_load, null);
		lv_carlist.setPullToLoadNextPageView(new PullToLoadPage(R.string.pull_to_load_next_page, rootView) {
			@Override
			public int takePageNum() {
				return mEndPageNum;
			}
		});
		lv_carlist.setOnItemClickListener(this);
		lv_carlist.setOnLoadingPageListener(this);
		//  lv_carlist.triggerLoadNextPage();

		mProgressBar = (ProgressBar)view.findViewById(R.id.progressBar);
		mProgressBar.setVisibility(View.VISIBLE);
		//  interface_loaddata.OnLoadData(type, 0);
	}

  /*  private void setList(){
        lv_carlist.Bulid();
        for(int i=0;i<cellNameStr.length;i++){
        	lv_carlist.addItem(cellNameStr[i] ,cellDetailStr[i],cellPriceStr[i]);

        }
        if(cellNameStr.length>0){
        	//lv_carlist.listItemAdapter.setSelectItem(1);
  			lv_carlist.listItemAdapter.notifyDataSetInvalidated();
        }

      //  setListViewHeightBasedOnChildren(lv_carlist);
    }*/

	public synchronized void connectResult(int cmd,String str,int cout,int page) {
		switch(cmd)
		{
			case 16:
				setList(str,cout,page);
				break;
		}
	}

	private void setList(String str,int cout,int page){
		count = cout;
//		Log.i("hu","收到商品总条数："+count);
		pageNum = count/10;
		if(count%10 > 0)
			pageNum++;

		lv_carlist.finishLoadNextPage();
		mProgressBar.setVisibility(View.GONE);
		if(page == 1){
			lv_carlist.Bulid();
			mEndPageNum = 0;
		}
		try {
			JSONArray array = new JSONArray(str);
			if(array.length()<1){
				return;
			}
			for(int i=0;i<array.length();i++){
				JSONObject obj = array.getJSONObject(i);
				String name = obj.getString("name");
				String describe = obj.getString("describe");
				String price = obj.getString("jg");
				String logo_url = obj.getString("logo");
				String id2 = obj.getString("id");
				lv_carlist.addItem(name ,describe,price,logo_url,id2);
			}

    		/*	JSONObject obj =  new JSONObject(str);
    			String name = obj.getString("name");
    			String describe = obj.getString("describe");
    			String price = "￥"+obj.getString("jg");
    			String logo_url = obj.getString("logo");
            	lv_carlist.addItem(name ,describe,price,logo_url);      	
    		*/
			if(page == 1)
				lv_carlist.listItemAdapter.notifyDataSetInvalidated();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onLoadPrevPage() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLoadNextPage() {
//		Log.i("hu","******onLoadNextPage()");
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
		Map<String, Object> item = (Map<String, Object>)parent.getItemAtPosition(position);
		if (item != null) {
			Intent intent = new Intent();
			String id1 = (String) item.get("id");
			intent.putExtra("id", id1);
			//  intent.setClass(AdList.this, NewsWeb.class);
			//   startActivity(intent);
			intent.setClass(GoodsFragment.this.getActivity(), DescriptionActivity.class);
			startActivity(intent);
		}
	}
}
