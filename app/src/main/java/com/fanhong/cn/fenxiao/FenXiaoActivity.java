package com.fanhong.cn.fenxiao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/8.
 */
@ContentView(R.layout.activity_fenxiao)
public class FenXiaoActivity extends Activity {
    @ViewInject(R.id.scroll_fenxiao)
    private ScrollView scrollView;
    @ViewInject(R.id.back_button)
    private ImageView backBtn;
    @ViewInject(R.id.iv_fenxiao1)
    private ImageView distribution1;
    @ViewInject(R.id.iv_fenxiao2)
    private ImageView distribution2;
    @ViewInject(R.id.tv_lijicanyu)
    private TextView joinIn;

//    float y,oldy;
//    boolean flag=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        setImage(distribution1, 720, 1139);
        setImage(distribution2, 540, 858);
//        scrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        oldy=event.getY();
//                        flag=true;
//                        Log.i("scroltest",y+","+flag+","+oldy);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        y=event.getY();
//                        if ((y-oldy)<-50){
//                            if (flag) {
//                                scrollView.smoothScrollTo(0, distribution2.getTop());
//                                flag=false;
//                            }
//                            Log.i("scroltest",y+","+flag+","+oldy);
//                        }
//                        break;
//                }
//                return false;
//            }
//        });
    }

    @Event(value = {R.id.back_button, R.id.iv_fenxiao1 ,R.id.iv_fenxiao2, R.id.tv_lijicanyu})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
            case R.id.iv_fenxiao1:
                scrollView.smoothScrollTo(0, distribution2.getTop());
                break;
            case R.id.iv_fenxiao2:
                scrollView.smoothScrollTo(0, joinIn.getTop());
                break;
            case R.id.tv_lijicanyu:
                startActivity(new Intent(this, InformationActivity.class));
                break;
        }
    }

//    @Event(value = R.id.scroll_fenxiao, type = View.OnTouchListener.class)
//    private boolean onTouch(View v, MotionEvent event) {
//        Toast.makeText(this,"",1000).show();
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                oldy=event.getY();
//                break;
//            case MotionEvent.ACTION_UP:
//                y=event.getY();
//                if ((y-oldy)<-50){
//                    scrollView.smoothScrollTo(0,distribution2.getTop());
//                }
//                break;
//        }
//        return false;
//    }

    private void setImage(ImageView iv, int wd, int ht) {
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int height = ht * screenWidth / wd;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, height);
        params.setMargins(0, 0, 0, 0);
        iv.setLayoutParams(params);
        iv.setPadding(0, 0, 0, 0);
    }
}
