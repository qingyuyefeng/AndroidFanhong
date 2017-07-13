package com.fanhong.cn;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.fanhong.cn.adapters.CommunityChatAdapter;
import com.fanhong.cn.bean.CommunityMessageBean;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import io.rong.message.TextMessage;

/**
 * Created by Administrator on 2017/6/30.
 */
@ContentView(R.layout.activity_comim_chatroom)
public class CommunityChatRoomActivity extends Activity {
    @ViewInject(R.id.lv_chatroom_msg_content)
    ListView lv_msg_content;
    @ViewInject(R.id.edt_chat_input)
    EditText edt_chat_input;
    @ViewInject(R.id.btn_msg_send)
    Button btn_msg_send;
    @ViewInject(R.id.tv_chat_title)
    TextView tv_title;

    List<CommunityMessageBean> mMessagelist = new ArrayList<>();

    CommunityChatAdapter adapter;
    SharedPreferences pref;

    private boolean ATCHATROOM = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initData();
//        if (!ATCHATROOM)
        connectRongCloud(SampleConnection.TOKEN, SampleConnection.CHATROOM);
        btn_msg_send.setEnabled(false);
        edt_chat_input.setEnabled(false);
        edt_chat_input.addTextChangedListener(watcher);
    }

    private void initData() {
        pref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        SampleConnection.TOKEN = pref.getString("token", "");
        SampleConnection.CHATROOM = pref.getString("gardenId", "");
//        Log.i("IMFragment","token="+SampleConnection.TOKEN+"chatroomId="+SampleConnection.CHATROOM);
        tv_title.setText(pref.getString("gardenName",""));
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
     *
     * @return userInfo：包含用户ID、昵称、头像地址（地址为URI，需要处理）
     */
    private UserInfo getCurrentInfo() {
        String uId = pref.getString("UserId", "");
        String nick = pref.getString("Nick", "");
        if (TextUtils.isEmpty(nick)) nick = "匿名用户";
        String headUrl = SampleConnection.LOGO_URL;
        Uri portraitUri = Uri.parse(headUrl);
        return new UserInfo(uId, nick, portraitUri);
    }

    /**
     * 发送按钮点击事件
     *
     * @param v
     */
    @Event(R.id.btn_msg_send)
    private void onSendClick(View v) {
        String input_msg = edt_chat_input.getText().toString().trim();
        edt_chat_input.setText("");
        TextMessage textMessage = TextMessage.obtain(input_msg);
        textMessage.setUserInfo(getCurrentInfo());//在消息体中附加用户信息，以便于接收时使用
        UserInfo info = getCurrentInfo();
        Log.i("IMFragment", "Info=:" + info.getUserId() + "," + info.getPortraitUri() + "," + info.getName());
        RongIMClient.getInstance().sendMessage(Conversation.ConversationType.CHATROOM, SampleConnection.CHATROOM, textMessage, null, null,
                new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                        Log.i("IMFragment", "发送失败" + errorCode.getMessage());
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.i("IMFragment", "发送成功");
                    }
                }, new RongIMClient.ResultCallback<Message>() {
                    @Override
                    public void onSuccess(Message message) {
                        final CommunityMessageBean bean = new CommunityMessageBean();
                        UserInfo info = getCurrentInfo();//获取当前用户信息
                        TextMessage msg = (TextMessage) message.getContent();
                        //获取消息的内容
                        bean.setMessage(msg.getContent());
                        bean.setMsgTime(message.getSentTime());
                        bean.setUserName(info.getName());
                        bean.setHeadUrl(SampleConnection.getUrlFromUri(info.getPortraitUri()));
                        bean.setType(CommunityMessageBean.TYPE_RIGHT);//发送的消息显示在右边

                        runOnUiThread(new Runnable() {//在UI线程更新
                            @Override
                            public void run() {
                                mMessagelist.add(bean);
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
     *
     * @param token      用户的token
     * @param chatRoomId 所选小区的聊天室ID
     */
    private void connectRongCloud(String token, final String chatRoomId) {
        Log.i("IMFragment", "token=" + token + "chatroomId=" + chatRoomId);
//        edt_chat_input.setEnabled(false);
        RongIMClient.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                //token失效
                edt_chat_input.setEnabled(false);
                edt_chat_input.setHint("加入聊天室失败:31004");
                Log.i("IMFragment", "onTokenIncorrect()加入聊天室失败:31004");
            }

            @Override
            public void onSuccess(String s) {
                Log.i("IMFragment", "链接聊天服务器成功");
                joinChatRoom(chatRoomId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                edt_chat_input.setEnabled(false);
                edt_chat_input.setHint("加入聊天室失败:" + errorCode.getValue());
                Log.i("IMFragment", "onError连接聊天服务器失败:" + errorCode.getValue());
            }
        });
    }

    private void joinChatRoom(String chatRoomId) {
        /**
         * 加入聊天室
         */
        RongIMClient.getInstance().joinChatRoom(chatRoomId, 50, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                //初始化聊天室
                initConversation();
                ATCHATROOM = true;
                Log.i("IMFragment", "加入聊天室成功");
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                edt_chat_input.setEnabled(false);
                edt_chat_input.setHint("加入聊天室失败:" + errorCode.getValue());
                Log.i("IMFragment", "onError连接聊天服务器失败:" + errorCode.getValue());
            }
        });
    }

    private void initConversation() {
        mMessagelist.add(new CommunityMessageBean("assets://welcome.gif", pref.getString("gardenName", "帆社区"),
                "欢迎加入我们的聊天室", System.currentTimeMillis(), CommunityMessageBean.TYPE_LEFT));
        adapter = new CommunityChatAdapter(this, mMessagelist);
        lv_msg_content.setAdapter(adapter);
        edt_chat_input.setEnabled(true);
        edt_chat_input.setHint("");
        /**RongIMClient.getInstance().getChatroomHistoryMessages
         * 聊天室中该方法是付费功能，需要开通才能使用
         * targetId - 目标 Id。根据不同的 conversationType，可能是用户 Id、讨论组 Id、群组 Id。
         * recordTime - 起始的消息发送时间戳，单位: 毫秒。
         * count - 要获取的消息数量，count 大于 0 ，小于等于 200。
         * order - 拉取顺序: 降序, 按照时间戳从大到小; 升序, 按照时间戳从小到大。
         */
//        RongIMClient.getInstance().getChatroomHistoryMessages(SampleConnection.CHATROOM, 0, 50, RongIMClient.TimestampOrder.RC_TIMESTAMP_DESC, new IRongCallback.IChatRoomHistoryMessageCallback() {
//            @Override
//            public void onSuccess(List<Message> list, long l) {
//                for (Message message : list) {
//                    if (message != null) {
//                        CommunityMessageBean bean = new CommunityMessageBean();
//                        TextMessage msg = (TextMessage) message.getContent();
//                        UserInfo info = message.getContent().getUserInfo();//获取消息体中所附带的用户信息
//                        //获取消息内容
//                        bean.setMessage(msg.getContent());
//                        bean.setMsgTime(message.getSentTime());
//                        bean.setUserName(info.getName());
//                        bean.setHeadUrl(SampleConnection.getUrlFromUri(info.getPortraitUri()));
//                        //对比消息的用户是否是当前用户，是则显示在右边，否则显示在左边
//                        if (getCurrentInfo().getUserId().equals(info.getUserId()))
//                            bean.setType(CommunityMessageBean.TYPE_RIGHT);
//                        else
//                            bean.setType(CommunityMessageBean.TYPE_LEFT);
//                        mMessagelist.add(bean);//在UI线程中更新
//                        parentActivity.runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                adapter.notifyDataSetChanged();
//                            }
//                        });
//                    }
//                }
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
                    final CommunityMessageBean bean = new CommunityMessageBean();
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
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mMessagelist.add(bean);//在UI线程中更新
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        RongIMClient.getInstance().quitChatRoom(SampleConnection.CHATROOM, new RongIMClient.OperationCallback() {
            @Override
            public void onSuccess() {
                ATCHATROOM = false;
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!ATCHATROOM)
            joinChatRoom(SampleConnection.CHATROOM);
    }

    @Override
    public void onStop() {
        super.onStop();
        //断开连接
        RongIMClient.getInstance().disconnect();
//        ATCHATROOM = false;
    }

    @Event(R.id.tv_back)
    private void onBackClick(View v) {
        finish();
    }
}
