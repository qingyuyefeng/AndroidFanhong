package com.fanhong.cn.util;

import com.fanhong.cn.bean.CommunityNewsBean;
import com.fanhong.cn.models.RegionInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/9.
 */

public class JsonSyncUtils {
    /*
     *通用方法
     */

    /**
     *
     * @param jsonObject
     * @param key
     * @return
     */
    public synchronized static String getJsonValue(String jsonObject, String key) {
        String value = "";
        try {
            JSONObject obj = new JSONObject(jsonObject);
            value = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     *
     * @param jsonArray
     * @param key
     * @return
     */
    public synchronized static String getJsonValueInAray(String jsonArray, String key) {
        String value = "";
        try {
            JSONArray array=new JSONArray(jsonArray);
            JSONObject obj = (JSONObject) array.opt(0);
            value = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     *
     * @param data
     * @param key
     * @return
     */
    public synchronized static List<String> getStringList(String data,String key){
        List<String> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.optJSONObject(i);
                list.add(object.optString(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /*
     * 专用方法
     */
    /**
     * @param list
     * @param json
     * @param type
     * @return
     */
    public synchronized static List<CommunityNewsBean> addNews(List<CommunityNewsBean> list, String json, int type) {
        try {
            JSONObject obj = new JSONObject(json);
            String datagroup="";
            if (type == CommunityNewsBean.TYPE_NEWS)
                datagroup = "data1";
            else if (type == CommunityNewsBean.TYPE_INFORM)
                datagroup = "data2";
            else if (type == CommunityNewsBean.TYPE_NOTICE)
                datagroup = "data3";
            else if (type == CommunityNewsBean.TYPE_ACTIVE)
                datagroup = "data4";
            String dataJ = obj.getString(datagroup);
            JSONArray array = new JSONArray(dataJ);
            CommunityNewsBean bean;
            for (int i = 0; i < array.length(); i++) {
                JSONObject objbean = (JSONObject) array.opt(i);
                bean = new CommunityNewsBean();
                bean.setNewsId(objbean.getString("id"));
                bean.setNews_photo(objbean.getString("logo"));
                bean.setTv_news_title(objbean.getString("bt"));
                bean.setTv_news_from(objbean.getString("zz"));
                bean.setTv_news_time(objbean.getString("time"));
                bean.setNews_flag(type);
                list.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    public synchronized static List<RegionInfo> getRegionList(String data){
        List<RegionInfo> infoList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.optJSONObject(i);
                infoList.add(new RegionInfo(object.optString("id"),object.optString("property"),object.optString("name")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return infoList;
    }
}
