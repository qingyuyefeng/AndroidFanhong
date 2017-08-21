package com.fanhong.cn.verification;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.fanhong.cn.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by Administrator on 2017/8/18.
 */
@ContentView(R.layout.activity_verification_car_notice)
public class CarVerificationNotice extends Activity {
    @ViewInject(R.id.tv_title)
    private TextView tv_title;
    @ViewInject(R.id.tv_notice)
    private TextView tv_notice;

    private String notice_html="<font color='#333333' size='5'>" +
            "<b>一、确认上面资料的真实性和完整性，如因资料导致的损失由本人承担。" +
            "<br />二、在用户上传资料后我司将于两个小时之内安排工作人员跟你联系确定取车时间。" +
            "<br />三、所需资料：</b></font><font color='#666666' size='3'>" +
            "<br />　　（1）《机动车行驶证》原件。" +
            "<br />　　（2）机动车交通事故强制保险凭证第三联原件（原件丢失的，提交其他任一联复印件" +
            "并加盖保险公司印章或由保险公司提供的补办凭证，原件因上次核发检验合格标志时已收存的，提供" +
            "任一联原件及复印件）。" +
            "<br />　　（3）车船税纳税或者免税证明原件和复印件（渝警号牌车辆、核定载客人数在9人以下" +
            "（含9人）以下没有发动机排量的纯电动车不属于车船税征税范围，不需要提供相关证明）" +
            "<br />　　（4）代理人申请核发机动车检验合格标志业务的，应当提交代理人" +
            "的身份证明和机动车所有人的书面委托</font><font color='#333333' size='5'>" +
            "<br /><b>四、核发检验合格标志条件：</b></font><font color='#666666' size='3'>" +
            "<br />　　机动车检验合格且排气污染物检测报告结论为“合格”的，直接在承检安检机构机动车" +
            "检验合格标志远程核发窗口申领机动车检验合格标志。机动车所有人为单位的，由本单位的被委托人" +
            "或代理人持身份证明办理。" +
            "<br />　　除以上须提交的证明、凭证外，运输危险化学品常压罐式汽车须持重庆市特种承压设备检测" +
            "研究院出具的有效期为一年的《常压槽罐车结论报告》正本和复印件；运输危险化学品承压罐式汽车" +
            "须持重庆市质量技术监督局核发的有效期内的《移动式压力容器使用登记证》正本及复印件；使用" +
            "警报器和标志灯具的非警用特种车辆须持省公安厅核发的《特种车辆警报器和标志灯具使用证》正" +
            "本和复印件。</font>";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        tv_title.setText("须知");
        tv_notice.setText(Html.fromHtml(notice_html));
        setResult(11);
    }

    @Event(R.id.img_back)
    private void onBackk(View v) {
        finish();
    }
}
