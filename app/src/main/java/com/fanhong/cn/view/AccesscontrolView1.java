package com.fanhong.cn.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.adapters.DoorApplyAdapter;
import com.fanhong.cn.applydoors.AddGuardActivity;
import com.fanhong.cn.applydoors.OpenDoorActivity;
import com.fanhong.cn.models.DoorcheckModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

public class AccesscontrolView1 extends BaseFragment {

    private View accView;
    private ListView doorapplyListview;
    private DoorApplyAdapter doorApplyAdapter;
    private List<DoorcheckModel> list = new ArrayList<>();

    private ImageView btn_reflush;
    private Button btn_add;
    private SharedPreferences mSettingPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        accView = inflater.inflate(R.layout.fragment_accesscontrol, null);
        return accView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // 启动activity时隐藏软键盘
        this.getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

         mSettingPref = this.getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);

         btn_add = (Button)view.findViewById(R.id.btn_add);
         btn_add.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
                    if(isLogined() == 1){
                        Intent intent2 = new Intent(AccesscontrolView1.this.getActivity(), AddGuardActivity.class);
                        AccesscontrolView1.this.getBaseActivity().startActivityForResult(intent2,1);
                    }else{
                        Toast.makeText(AccesscontrolView1.this.getActivity(), getString(R.string.pleaselogin), Toast.LENGTH_SHORT).show();
                    }
				}	        		
     	});
        btn_reflush = (ImageView)view.findViewById(R.id.btn_reflush);
        btn_reflush.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                //刷新显示钥匙
                if(isLogined() == 1){
                    AccesscontrolView1.this.getBaseActivity().getAccessControl();
                }else{
                    Toast.makeText(AccesscontrolView1.this.getActivity(), getString(R.string.pleaselogin), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        doorapplyListview = (ListView) accView.findViewById(R.id.applydoors_listview);
        doorApplyAdapter = new DoorApplyAdapter(getActivity(),list);
        doorApplyAdapter.setOpenDoor(new DoorApplyAdapter.OpenDoor() {
            @Override
            public void openBtnClick(String str1,String str2,String str3) {
                Intent intent = new Intent();
                intent.putExtra("cellName",str1);
                intent.putExtra("louNumber",str2);
                intent.putExtra("miyue",str3);
                intent.setClass(AccesscontrolView1.this.getActivity(), OpenDoorActivity.class);
                startActivity(intent);
            }
        });

        handler.sendEmptyMessage(0);
        doorapplyListview.setAdapter(doorApplyAdapter);

    }

    private void setList(String string){
        try {
            JSONObject jsonObject = new JSONObject(string);
            String data = jsonObject.optString("data","");
            Log.i("xq",data);
            String data1 = jsonObject.optString("data1","");
            Log.i("xq",data1);
            if(!data.equals("0")){
                String[] array1 = data.split(",");
                for (int i = 0; i < array1.length; i++) {
                    String[] arr = array1[i].split("<");
                    DoorcheckModel doorcheckModel = new DoorcheckModel();
                    doorcheckModel.setCellName(arr[0]);
                    doorcheckModel.setLouNumber(arr[1]);
                    doorcheckModel.setMiyue(arr[2]);
                    doorcheckModel.setStatus(0);
                    list.add(doorcheckModel);
                }
            }
            if(!data1.equals("0")){
                String[] array2 = data1.split(",");
                for (int i = 0; i < array2.length; i++) {
                    DoorcheckModel doorcheckModel = new DoorcheckModel();
                    doorcheckModel.setCellName(array2[i]);
                    doorcheckModel.setStatus(1);
                    list.add(doorcheckModel);
                }
            }
            handler.sendEmptyMessage(0);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e){
            Log.i("xq","数组越界==>"+list.size());
        }
    }

    private int isLogined(){
        int result = 0;
        try{
            result = mSettingPref.getInt("Status", 0);
        }catch (Exception e) {}
        return result;
    }

    public synchronized void setFragment(int type,String string) {
        switch(type)
        {
            case 21:
                list.clear();
                setList(string);
                break;
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            doorApplyAdapter.notifyDataSetChanged();
            return true;
        }
    });
}
