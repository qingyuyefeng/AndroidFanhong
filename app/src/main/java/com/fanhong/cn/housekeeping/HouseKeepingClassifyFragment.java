package com.fanhong.cn.housekeeping;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    protected final static int KEEP_CLEAN = 0;
    protected final static int FAMILY_SERVICE = 1;
    protected final static int TRANS_SERVICE = 2;
    protected final static int OTHER_SERVICE = 3;
    private int currentList = 0;

    private SimpleAdapter simpleAdapter;
    private List<HashMap<String, Object>> lists = new ArrayList<>();
    private Object[][][] objects = {{{R.drawable.ilon_keepclean, "家庭保洁", "家洁 心兴"},
            {R.drawable.ilon_empty_house_cleaning, "空房保洁", "新房就是要一尘不染"},
            {R.drawable.ilon_glass_clean, "玻璃保洁（按平方米）", "外面的世界很精彩"},
            {R.drawable.ilon_house_xiaodu, "房屋消毒", "安全 放心"},
            {R.drawable.ilon_kitchen, "厨房保养", "清洁 杀菌"},
            {R.drawable.ilon_toilet, "卫生间保养", "深度杀菌，用得放心"},
            {R.drawable.ilon_youyanji, "油烟机清洗", "不油腻，才有更美味的美食"},
            {R.drawable.ilon_kongtiao, "空调清洗", "清洁无菌，给家人更安心"}}
            , {{R.drawable.ilon_baomu, "保姆", "给你一个轻松愉快的家庭"},
            {R.drawable.ilon_yuyingshi, "育婴师", "让宝宝茁壮成长"},
            {R.drawable.ilon_cuirushi, "催乳师", "不会让宝宝饿到了"},
            {R.drawable.ilon_jiajiao, "上门家教", "妈妈再也不用担心孩子的学习了"}}
            , {{R.drawable.ilon_move, "搬家服务", "原封不动地让你换个环境"}}
            , {{R.drawable.ilon_a_care, "护工", "你安心上班，其他交给我"},
            {R.drawable.ilon_a_maid, "钟点工", "一点时间给你不一样的环境"}}
    };
    private String[] strings = {"picture", "text1", "text2"};
    private int[] ids = {R.id.housekeeping_item_iv, R.id.housekeeping_item_tv1, R.id.housekeeping_item_tv2};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        classifyRadiogroup.check(R.id.keepclean_radio);
        classifyListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), HouseKeepingClassifyServiceActivity.class);
                intent.putExtra("classify", objects[currentList][position][1].toString());
                startActivity(intent);
            }
        });
        return view;
    }

    @Event(value = R.id.classify_radiogroup, type = RadioGroup.OnCheckedChangeListener.class)
    private void onCheckchange(RadioGroup radioGroup, int checkId) {
        switch (checkId) {
            case R.id.keepclean_radio:
                lists.clear();
                simpleAdapter = new SimpleAdapter(getActivity(),
                        getData(0),
                        R.layout.housekeeping_classify_item,
                        strings,
                        ids);
                classifyListview.setAdapter(simpleAdapter);
                currentList = KEEP_CLEAN;
                break;
            case R.id.familyservice_radio:
                lists.clear();
                simpleAdapter = new SimpleAdapter(getActivity(),
                        getData(1),
                        R.layout.housekeeping_classify_item,
                        strings,
                        ids);
                classifyListview.setAdapter(simpleAdapter);
                currentList = FAMILY_SERVICE;
                break;
            case R.id.carryservice_radio:
                lists.clear();
                simpleAdapter = new SimpleAdapter(getActivity(),
                        getData(2),
                        R.layout.housekeeping_classify_item,
                        strings,
                        ids);
                classifyListview.setAdapter(simpleAdapter);
                currentList = TRANS_SERVICE;
                break;
            case R.id.otherservice_radio:
                lists.clear();
                simpleAdapter = new SimpleAdapter(getActivity(),
                        getData(3),
                        R.layout.housekeeping_classify_item,
                        strings,
                        ids);
                classifyListview.setAdapter(simpleAdapter);
                currentList = OTHER_SERVICE;
                break;
        }
    }

    private List<HashMap<String, Object>> getData(int x) {
        for (int i = 0; i < objects[x].length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            for (int j = 0; j < strings.length; j++) {
                map.put(strings[j], objects[x][i][j]);
            }
            lists.add(map);
        }
        return lists;
    }
}
