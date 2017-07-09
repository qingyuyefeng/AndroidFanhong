package com.fanhong.cn.view;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.fanhong.cn.R;
import com.fanhong.cn.SampleConnection;
import com.fanhong.cn.adapters.CommunityChatAdapter;
import com.fanhong.cn.bean.CommunityMessageBean;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * Created by Administrator on 2017/6/30.
 */
@ContentView(R.layout.fragment_comim_chatroom)
public class CommunityIMFragment extends Fragment {
    @ViewInject(R.id.lv_chatroom_msg_content)
    ListView lv_msg_content;
    @ViewInject(R.id.edt_chat_input)
    EditText edt_chat_input;
    @ViewInject(R.id.btn_msg_send)
    Button btn_msg_send;

    List<CommunityMessageBean> list;
    CommunityChatAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        connectRongCloud(SampleConnection.TOKEN, SampleConnection.CHATROOM);

        btn_msg_send.setEnabled(false);
        edt_chat_input.addTextChangedListener(watcher);

        return view;
    }

    private List<CommunityMessageBean> initData() {
        list = new ArrayList<>();
        list.add(new CommunityMessageBean("http://imgsrc.baidu.com/imgad/pic/item/b8014a90f603738dc01baa06b91bb051f819ec45.jpg",
                "春风与湖", "欢迎加入我们的聊天室", 1499126400000l, CommunityMessageBean.TYPE_LEFT));
        return list;
    }

    TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(edt_chat_input.getText().toString())) {
                btn_msg_send.setEnabled(false);
            } else {
                btn_msg_send.setEnabled(true);
            }
        }
    };

    /**
     * 获取当前用户的信息
     * @return userInfo：包含用户ID、昵称、头像地址（地址为URI，需要处理）
     */
    private UserInfo getCurrentInfo() {
        SharedPreferences pref = getActivity().getSharedPreferences("Setting", Context.MODE_PRIVATE);
        String uId = pref.getString("UserId", "");
        String nick = pref.getString("Nick", "");
        String headUrl = SampleConnection.LOGO_URL;
        Uri portraitUri = Uri.parse(headUrl);
//        Toast.makeText(getActivity(), headUrl, Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(), portraitUri.getPath(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(), SampleConnection.getUrlFromUri(portraitUri), Toast.LENGTH_SHORT).show();
        return new UserInfo(uId, nick, portraitUri);
    }

    /**
     * 发送按钮点击事件
     * @param v
     */
    @Event(R.id.btn_msg_send)
    private void onSendClick(View v) {
        String input_msg = edt_chat_input.getText().toString().trim();
        edt_chat_input.setText("");
        TextMessage textMessage = TextMessage.obtain(input_msg);
        textMessage.setUserInfo(getCurrentInfo());//在消息体中附加用户信息，以便于接收时使用
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.CHATROOM, SampleConnection.TOKEN, textMessage, null, null,
                new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {

                    }
                }, new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {
                        CommunityMessageBean bean = new CommunityMessageBean();
                        UserInfo info = getCurrentInfo();//获取当前用户信息
                        TextMessage msg = (TextMessage) message.getContent();
                        //获取消息的内容
                        bean.setMessage(msg.getContent());
                        bean.setMsgTime(message.getSentTime());
                        bean.setUserName(info.getName());
                        bean.setHeadUrl(SampleConnection.getUrlFromUri(info.getPortraitUri()));
                        bean.setType(CommunityMessageBean.TYPE_RIGHT);//发送的消息显示在右边
                        list.add(bean);
                        getActivity().runOnUiThread(new Runnable() {//在UI线程更新
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
    }

    /**
     * 连接融云服务器并加入指定聊天室
     * @param token 用户的token
     * @param chatRoomId 所选小区的聊天室ID
     */
    private void connectRongCloud(String token, final String chatRoomId) {
        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                //token失效
                edt_chat_input.setEnabled(false);
                edt_chat_input.setHint("加入聊天室失败:31004");
            }

            @Override
            public void onSuccess(String s) {
                /**
                 * 加入聊天室
                 */
                RongIMClient.getInstance().joinChatRoom(chatRoomId, 50, new RongIMClient.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        edt_chat_input.setEnabled(true);
                        edt_chat_input.setHint("");
                        //初始化聊天室
                        initConversation();
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {
                        edt_chat_input.setEnabled(false);
                        edt_chat_input.setHint("加入聊天室失败:" + errorCode.getValue());
                    }
                });
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                edt_chat_input.setEnabled(false);
                edt_chat_input.setHint("加入聊天室失败:" + errorCode.getValue());
            }
        });
    }

    private void initConversation() {
        initData();
        adapter = new CommunityChatAdapter(getActivity(), list);
        lv_msg_content.setAdapter(adapter);
//        /**RongIMClient.getInstance().getChatroomHistoryMessages
//         * 聊天室中该方法是付费功能，需要开通才能使用
//         * targetId - 目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
//         * recordTime - 起始的消息发送时间戳，单位: 毫秒。
//         * count - 要获取的消息数量，count 大于 0 ，小于等于 200。
//         * order - 拉取顺序: 降序, 按照时间戳从大到小; 升序, 按照时间戳从小到大。
//         */
//        RongIMClient.getInstance().getChatroomHistoryMessages(chatroomId, 0, 20, RongIMClient.TimestampOrder.RC_TIMESTAMP_DESC, new IRongCallback.IChatRoomHistoryMessageCallback() {
//            @Override
//            public void onSuccess(List<Message> list, long l) {
//
//            }
//
//            @Override
//            public void onError(RongIMClient.ErrorCode errorCode) {
//
//            }
//        });
        /**
         * 设置消息接收监听
         */
        RongIMClient.setOnReceiveMessageListener(new RongIMClient.OnReceiveMessageListener() {
            /**
             * 收到消息的处理。
             * @param message 收到的消息实体。
             * @param i       剩余未拉取消息数目。
             * @return 是否接收
             */
            @Override
            public boolean onReceived(final Message message, int i) {
                if (message != null) {
                    CommunityMessageBean bean = new CommunityMessageBean();
                    TextMessage msg = (TextMessage) message.getContent();
                    UserInfo info = message.getContent().getUserInfo();//获取消息体中所附带的用户信息
                    //获取消息内容
                    bean.setMessage(msg.getContent());
                    bean.setMsgTime(message.getSentTime());
                    bean.setUserName(info.getName());
                    bean.setHeadUrl(SampleConnection.getUrlFromUri(info.getPortraitUri()));
                    //对比消息的用户是否是当前用户，是则显示在右边，否则显示在左边
                    if (getCurrentInfo().getUserId().equals(info.getUserId()))
                        bean.setType(CommunityMessageBean.TYPE_RIGHT);
                    else
                        bean.setType(CommunityMessageBean.TYPE_LEFT);
                    list.add(bean);//在UI线程中更新
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                return true;
            }
        });
    }

}
