package com.fanhong.cn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fanhong.cn.adapters.SortAdapter;
import com.fanhong.cn.models.RegionInfo;
import com.fanhong.cn.models.SortModel;
import com.fanhong.cn.util.CharacterParser;
import com.fanhong.cn.util.ClearEditText;
import com.fanhong.cn.util.PinyinComparator;
import com.fanhong.cn.listviews.SideBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GardenSelecterActivity extends SampleActivity {
    private SharedPreferences mSettingPref;
    private SampleConnection mSafoneConnection;
    private Context mcontext;
    private Button titleBackImageBtn;
    private List<RegionInfo> provinceList;
    private List<RegionInfo> citysList;
    private List<String> provinces;
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    public synchronized void connectFail(int type) {
        //SampleConnection.USER = "";
        //SampleConnection.USER_STATE = 0;

        //Toast.makeText(this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
    }

    public synchronized void connectSuccess(JSONObject json, int type) {
        int cmd = -1;
        int result = -1;
        int state = 0;
        String str;
        String name = "";
        String phonenumber = "";
        String alias = "";
        try {
            str = json.getString("cmd");
            cmd = Integer.parseInt(str);
            str = json.getString("cw");
            result = Integer.parseInt(str);
        } catch (Exception e) {
            connectFail(type);
            return;
        }
        if (cmd == 30) {
            if (result == 0) {
                try {
                    str = json.getString("data");
                } catch (Exception e) {
                    connectFail(type);
                    return;
                }
                initData(str);
            } else {
            }
        } else {
            connectFail(type);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_city_selecter);
        mcontext = getApplicationContext();
        // 启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSettingPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        titleBackImageBtn = (Button) findViewById(R.id.btn_back);
        titleBackImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //initData();
        //initViews();
        getGardenName();
    }

    private void getGardenName() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cmd", "29");

        if (mSafoneConnection == null)
            mSafoneConnection = new SampleConnection(GardenSelecterActivity.this, 0);
        mSafoneConnection.connectService1(map);
    }

    private void initData(String str) {
        provinceList = getProvencesOrCity(str);
        citysList = new ArrayList<RegionInfo>();
        provinces = new ArrayList<String>();
        for (RegionInfo info : provinceList) {
            provinces.add(info.getName().trim());
        }
        initViews();
    }

    private void initViews() {
        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        //sortListView.addHeaderView(view);
        sortListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 这里要利用adapter.getItem(position)来获取当前position所对应的对象
                //Toast.makeText(getApplication(), ((SortModel) adapter.getItem(position - 1)).getName(), Toast.LENGTH_SHORT).show();
                //hideSoftInput(mClearEditText.getWindowToken());
                //String name = ((SortModel) adapter.getItem(position - 1)).getName();
                String name = ((SortModel) adapter.getItem(position)).getName();
                RegionInfo info = getReginFromName(name);
                Intent data = new Intent();
                data.putExtra("gardenName", name);
                if (info != null) {
                    data.putExtra("gardenId", info.getId());
                    data.putExtra("gardenProperty", info.getProperty());
                }
                setResult(51, data);
                GardenSelecterActivity.this.finish();
            }
        });

        SourceDateList = filledData(provinceList);

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SortAdapter(this, SourceDateList);
        sortListView.setAdapter(adapter);

        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public List<RegionInfo> getProvencesOrCity(String data) {
        List<RegionInfo> regionInfos = new ArrayList<RegionInfo>();
        try {
            JSONArray array = new JSONArray(data);
            int len = array.length();
            if (data == null || data.length() < 1) {
                return regionInfos;
            }
            for (int i = 0; i < len; i++) {
                RegionInfo regionInfo = new RegionInfo();
                JSONObject obj = array.getJSONObject(i);
                String id = "";
                String property = "";
                String name = "";
                try {
                    id = obj.getString("id");
                } catch (Exception e) {
                }
                try {
                    property = obj.getString("property");
                } catch (Exception e) {
                }
                try {
                    name = obj.getString("name");
                } catch (Exception e) {
                }
                regionInfo.setId(id);
                regionInfo.setProperty(property);
                regionInfo.setName(name);
                regionInfos.add(regionInfo);
            }
        } catch (Exception e) {
        }
        return regionInfos;
    }

    private RegionInfo getReginFromName(String name) {
        int len = provinceList.size();
        for (int i = 0; i < len; i++) {
            RegionInfo regionInfo = provinceList.get(i);
            if (regionInfo.getName().equals(name)) {
                return regionInfo;
            }
        }
        return null;
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(List<RegionInfo> date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.size(); i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date.get(i).getName());
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date.get(i).getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            if (!provinces.contains(filterStr)) {
                filterDateList.clear();
                for (SortModel sortModel : SourceDateList) {
                    String name = sortModel.getName();
                    if (name.indexOf(filterStr.toString()) != -1
                            || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                        filterDateList.add(sortModel);
                    }
                }
            } else {
                filterDateList.clear();
                for (int i = 0; i < provinceList.size(); i++) {
                    String name = provinceList.get(i).getName();
                    if (name.equals(filterStr)) {
                        //filterDateList.addAll(filledData(RegionDAO.getProvencesOrCityOnParent(provinceList.get(i).getId())));
                    }
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    protected void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
