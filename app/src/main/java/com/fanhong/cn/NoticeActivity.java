package com.fanhong.cn;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.adapters.NoticeAdapter;
import com.fanhong.cn.models.NoticeModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


@ContentView(R.layout.activity_notice)
public class NoticeActivity extends Activity{
	@ViewInject(R.id.tv_title)
	private TextView title;
	@ViewInject(R.id.notice_list)
	private RecyclerView recyclerView;

    private List<NoticeModel> list = new ArrayList<>();
    private NoticeAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		x.view().inject(this);
		title.setText("消息通知");
	}
	@Event({R.id.img_back})
	private void onClick(View v){
		finish();
	}
	private void getDatas(){
        for(int i=0;i<1;i++){
            list.add(new NoticeModel());
        }
    }

	@Override
	protected void onResume() {
		super.onResume();
		list.clear();
		getDatas();
		adapter = new NoticeAdapter(list,this);
		adapter.setLayoutClick(new NoticeAdapter.LayoutClick() {
			@Override
			public void click(int pos) {
				Toast.makeText(NoticeActivity.this,"现在真的没有活动。。。",Toast.LENGTH_SHORT).show();
			}
		});
		recyclerView.setAdapter(adapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
	}
}
	 