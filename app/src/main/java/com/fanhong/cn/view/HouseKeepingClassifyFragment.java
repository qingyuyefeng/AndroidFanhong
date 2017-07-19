package com.fanhong.cn.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Administrator on 2017/7/19.
 */
@ContentView(R.layout.fragment_housekeeping_classify)
public class HouseKeepingClassifyFragment extends Fragment {
    @ViewInject(R.id.classify_radiogroup)
    private RadioGroup classifyRadiogroup;
    @ViewInject(R.id.housekeeping_classify_listview)
    private ListView classifyListview;

    private SimpleAdapter simpleAdapter;
    private List<HashMap<String, Object>> lists = new ArrayList<>();
    private Object[][] objects1 = {{R.drawable.ilon_keepclean, "家庭保洁", "家洁 心兴"},
            {R.drawable.ilon_empty_house_cleaning, "空房保洁", "新房就是要一尘不染"},
            {R.drawable.ilon_glass_clean, "玻璃保洁（按平方米）", "外面的世界很精彩"},
            {R.drawable.ilon_house_xiaodu, "房屋消毒", "安全 放心"},
            {R.drawable.ilon_kitchen, "厨房保养", "清洁 杀菌"},
            {R.drawable.ilon_toilet, "卫生间保养", "深度杀菌，用得放心"},
            {R.drawable.ilon_youyanji, "油烟机清洗", "不油腻，才有更美味的美食"},
            {R.drawable.ilon_kongtiao, "空调清洗", "清洁无菌，给家人更安心"}};
    private String[] strings = {"picture", "text1", "text2"};
    private int[] ids = {R.id.housekeeping_item_iv, R.id.housekeeping_item_tv1, R.id.housekeeping_item_tv2};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        return view;
    }

    @Event(value = R.id.classify_radiogroup, type = RadioGroup.OnCheckedChangeListener.class)
    private void onCheckchange(RadioGroup radioGroup, int checkId) {
        switch (checkId) {
            case R.id.keepclean_radio:
                lists.clear();
                simpleAdapter = new SimpleAdapter(getActivity(),
                        getData1(),
                        R.layout.housekeeping_classify_item,
                        strings,
                        ids);
                classifyListview.setAdapter(simpleAdapter);
                break;
            case R.id.familyservice_radio:
                break;
            case R.id.carryservice_radio:
                break;
            case R.id.otherservice_radio:
                break;
        }
    }

    private List<HashMap<String, Object>> getData1() {
        for (int i = 0; i < objects1.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("picture", objects1[i][0]);
            map.put("text1", objects1[i][1]);
            map.put("text2", objects1[i][2]);
            lists.add(map);
        }
        return lists;
    }
}
