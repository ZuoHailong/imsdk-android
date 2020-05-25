package com.qunar.im.ui.activity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.qunar.im.base.common.ConversitionType;
import com.qunar.im.base.jsonbean.CapabilityResult;
import com.qunar.im.base.jsonbean.ExtendMessageEntity;
import com.qunar.im.base.jsonbean.ImgVideoBean;
import com.qunar.im.base.jsonbean.NoticeBean;
import com.qunar.im.base.jsonbean.RbtMsgBackupInfo;
import com.qunar.im.base.jsonbean.VideoMessageResult;
import com.qunar.im.base.module.IMMessage;
import com.qunar.im.base.module.ImageItem;
import com.qunar.im.base.module.UserConfigData;
import com.qunar.im.base.structs.FuncButtonDesc;
import com.qunar.im.base.structs.MessageStatus;
import com.qunar.im.base.structs.MessageType;
import com.qunar.im.base.util.ChatTextHelper;
import com.qunar.im.base.util.Constants;
import com.qunar.im.base.util.DataUtils;
import com.qunar.im.base.util.EventBusEvent;
import com.qunar.im.base.util.IMUserDefaults;
import com.qunar.im.base.util.InternDatas;
import com.qunar.im.base.util.JsonUtils;
import com.qunar.im.base.util.ListUtil;
import com.qunar.im.base.util.LogUtil;
import com.qunar.im.base.util.Utils;
import com.qunar.im.base.view.faceGridView.EmoticionMap;
import com.qunar.im.base.view.faceGridView.EmoticonEntity;
import com.qunar.im.common.CommonConfig;
import com.qunar.im.common.CurrentPreference;
import com.qunar.im.core.services.QtalkNavicationService;
import com.qunar.im.other.CacheDataType;
import com.qunar.im.permission.PermissionCallback;
import com.qunar.im.permission.PermissionDispatcher;
import com.qunar.im.permission.PermissionUtils;
import com.qunar.im.protobuf.dispatch.DispatchHelper;
import com.qunar.im.ui.R;
import com.qunar.im.ui.adapter.ExtendChatViewAdapter;
import com.qunar.im.ui.broadcastreceivers.ShareReceiver;
import com.qunar.im.ui.imagepicker.ImageDataSourceForRecommend;
import com.qunar.im.ui.imagepicker.ImagePicker;
import com.qunar.im.ui.presenter.IChatingPresenter;
import com.qunar.im.ui.presenter.ICloudRecordPresenter;
import com.qunar.im.ui.presenter.impl.SingleSessionPresenter;
import com.qunar.im.ui.presenter.views.IChatView;
import com.qunar.im.ui.util.EmotionUtils;
import com.qunar.im.ui.util.ImageSelectUtil;
import com.qunar.im.ui.util.WaterMarkTextUtil;
import com.qunar.im.ui.util.atmanager.AtManager;
import com.qunar.im.ui.util.easyphoto.easyphotos.callback.SelectCallback;
import com.qunar.im.ui.util.easyphoto.easyphotos.models.album.entity.Photo;
import com.qunar.im.ui.view.CommonDialog;
import com.qunar.im.ui.view.IconView;
import com.qunar.im.ui.view.QtNewActionBar;
import com.qunar.im.ui.view.RecommendPhotoPop;
import com.qunar.im.ui.view.baseView.ViewPool;
import com.qunar.im.ui.view.camera.CameraActivity;
import com.qunar.im.ui.view.chatExtFunc.FuncItem;
import com.qunar.im.ui.view.chatExtFunc.FuncMap;
import com.qunar.im.ui.view.chatExtFunc.OperationView;
import com.qunar.im.ui.view.emojiconEditView.EmojiconEditText;
import com.qunar.im.ui.view.emoticonRain.EmoticonRainUtil;
import com.qunar.im.ui.view.emoticonRain.EmoticonRainView;
import com.qunar.im.ui.view.faceGridView.EmotionLayout;
import com.qunar.im.ui.view.faceGridView.FaceGridView;
import com.qunar.im.ui.view.kpswitch.util.KPSwitchConflictUtil;
import com.qunar.im.ui.view.kpswitch.util.KeyboardUtil;
import com.qunar.im.ui.view.kpswitch.widget.KPSwitchPanelLinearLayout;
import com.qunar.im.ui.view.kpswitch.widget.KPSwitchRootLinearLayout;
import com.qunar.im.ui.view.medias.play.MediaPlayerImpl;
import com.qunar.im.ui.view.swipBackLayout.SwipeBackActivity;
import com.qunar.im.utils.CapabilityUtil;
import com.qunar.im.utils.ConnectionUtil;
import com.qunar.im.utils.HttpUtil;
import com.qunar.im.utils.QtalkStringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import de.greenrobot.event.EventBus;

/**
 * Created by hubin on 2017/8/13.
 */

public class PbChatActivity extends SwipeBackActivity implements AtManager.AtTextChangeListener, View.OnFocusChangeListener, View.OnClickListener, ImageDataSourceForRecommend.OnImagesLoadedListener,
        IChatView, PermissionCallback {
    public static final String TAG = "PbChatActivity";

    /**************其他地方在用，不可删  开始 **************/
    public static final int TRANSFER_CONVERSATION_REQUEST_CODE = 0x15;//会话转移
    protected static final int MENU4 = 0x04;
    protected static final int MENU5 = 0x05;
    public static final int AT_MEMBER = 0x13;//forresult的key 应该为开启所在群成员列表,方便@
    public static final String KEY_IS_CHATROOM = "isFromChatRoom";
    public static final String KEY_RIGHTBUTTON = "right_button_type";
    public static final String KEY_IS_REMIND = "is_remind";
    public static final String KEY_ENCRYPT_BODY = "encryptBody";
    public static final String KEY_ATMSG_INDEX = "atmsg_index";

    //是不是群
    protected boolean isFromChatRoom;

    public boolean searching;//是否处于搜索结果中

    @Override
    public String getTransferId() {
        return null;
    }

    @Override
    public void initActionBar() {

    }

    @Override
    public Map<String, String> getAtList() {
        return null;
    }

    @Override
    public void setTitleState(String stats) {

    }

    @Override
    public void revokeItem(IMMessage imMessage) {

    }

    @Override
    public void deleteItem(IMMessage imMessage) {

    }

    @Override
    public boolean isFromChatRoom() {
        return false;
    }

    @Override
    public void replaceItem(IMMessage imMessage) {

    }

    @Override
    public void payAuth(String authInfo) {

    }

    @Override
    public void payOrder(String orderInfo) {

    }

    @Override
    public void payRedEnvelopChioce(String type, String rid) {

    }

    //设置长按对话框都有什么功能
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void parseEncryptSignal(IMMessage message) {

    }

    @Override
    public boolean getSearching() {
        return false;
    }

    @Override
    public void isEmotionAdd(boolean flag) {
    }

    @Override
    public String getRefenceString() {
        return null;
    }

    @Override
    public List<IMMessage> getSelMessages() {
        return null;
    }

    @Override
    public String getAutoReply() {
        return null;
    }

    /**************其他地方在用，不可删  结束**************/

    public static final int ACTIVITY_GET_CAMERA_IMAGE = 1;//拍照
    public static final int ACTIVITY_SELECT_PHOTO = 2;//图库选图
    //权限
    protected final int SHOW_CAMERA = PermissionDispatcher.getRequestCode();
    protected final int SELECT_PIC = PermissionDispatcher.getRequestCode();
    //连接核心类
    private ConnectionUtil connectionUtil;
    //获取intent数据中所用key
    public static final String KEY_JID = "jid";
    public static final String KEY_REAL_JID = "realJid";
    public static final String KEY_CHAT_TYPE = "chatType";
    public static final String KEY_INPUTTYPE = "input_type";
    public static final String KEY_BUSI_NAME = "busi_name";
    public static final String KEY_SUPPLIER_ID = "supplier_id";
    public static final String KEY_SHOW_READSTATE = "show_read_state";

    public static final String KEY_UNREAD_MSG_COUNT = "unread_msg_count";

    private static final String TAG_UNREAD_VIEW = "tag_unread_view";


    public int atMsgIndex = 0;
    //基础数据
    //应理解为消息列表左侧显示的人或群的id
    protected String jid;
    protected String realJid;
    protected String busiName;
    protected String supplierId;
    protected String chatType;
    protected String of;
    protected String ot;
    protected boolean input_type;
    protected FuncMap funcMap = new FuncMap();

    //声明主持类两个
    protected IChatingPresenter chatingPresenter;
    //是否是第一次打开 默认先设置为true

    //第一次打开时,应该进行的操作有 加载历史记录 20条,把所有这个会话id的消息的readStats设置为1 已读,
    boolean isFirstInit = true;

    //临时图片地址
    private String imageUrl;
    //
    private WaterMarkTextUtil waterMarkTextUtil;

    //界面View
    protected LinearLayout edit_region, linearlayout_tab2, outter_msg_prompt, atom_bottom_more;
    protected OperationView linearlayout_tab;
    protected PullToRefreshListView chat_region;
    protected KPSwitchPanelLinearLayout mPanelRoot;
    //listview速度监听
//    private VelocityTracker velocityTracker = null;
    //发送，新消息，按住语音说话，键盘切换，语音切换，表情，功能加号
    protected TextView send_btn, new_msg_prompt, outter_msg, no_prompt, close_prompt;
    protected IconView left_btn, tv_options_btn, emotion_btn;
    protected EmojiconEditText edit_msg;
    protected RelativeLayout atom_bottom_frame;
    protected LinearLayout input_container;
    protected LinearLayout total_bottom_layout;
    protected KPSwitchRootLinearLayout relativeLayout;
    protected RelativeLayout chating_view;
    protected EmotionLayout faceView;
    protected EmoticonRainView emoticonRainView;
    protected QtNewActionBar qtNewActionBar;
//    private View line;

    protected ExtendChatViewAdapter pbChatViewAdapter;
    //未读消息
    AtomicInteger unreadMsgCount = new AtomicInteger(0);
    //展示当前界面时,有新消息记录下来,如果滑动到底部,重设为0
    protected int newMsgCount = 0;
    //文本改变相关boolean 具体用法未知
    protected boolean canShowAtActivity = true;
    //选中的消息列表
    private AtManager mAtManager;

    HandleChatEvent handleChatEvent = new HandleChatEvent();

    private ViewTreeObserver.OnGlobalLayoutListener keyBordOnGlobalLayoutListener;

    public PullToRefreshBase.OnRefreshListener onRefresh = new PullToRefreshBase.OnRefreshListener<ListView>() {
        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {
            //加载更多的历史消息:
            loadMoreHistory();
        }

    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override //文本改变前
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mAtManager.beforeTextChanged(s, start, count, after);
            //这一套操作的意思不明确 应该为在删除状态时,不让@界面出现
            if (count > 0 && after == 0) {
                canShowAtActivity = false;
            }
        }

        @Override //文本改变
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //如果当前文本没有值
            if (TextUtils.isEmpty(s) || s.toString().trim().length() == 0) {
                //设置发送按钮不可见
                send_btn.setVisibility(View.GONE);
                //设置更多功能按钮可见
                tv_options_btn.setVisibility(View.VISIBLE);
                return;
            }
            mAtManager.onTextChanged(s, start, before, count);
            //设置更多功能按钮不可见
            tv_options_btn.setVisibility(View.GONE);
            //设置发送按钮可见
            send_btn.setVisibility(View.VISIBLE);
        }

        @Override //文本改变后
        public void afterTextChanged(Editable s) {
            //todo:向对方发送当前正在输入的状态
            //判断字段长度大于0  防止刚发送消息后,清空msg时调用了本方法
            if (s.length() > 0) {
                chatingPresenter.sendTypingStatus();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atom_ui_activity_chat);
        connectionUtil = ConnectionUtil.getInstance();
        waterMarkTextUtil = new WaterMarkTextUtil();
        //处理一些额外的数据
        handleExtraData(savedInstanceState);
        //获取intent数据
        injectExtras(getIntent());

        //@消息管理
        mAtManager = new AtManager(this, jid);
        mAtManager.setTextChangeListener(this);
        //初始化view
        bindViews();
        initViews();

        EventBus.getDefault().register(handleChatEvent);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        isFirstInit = true;
        injectExtras(intent);
        pbChatViewAdapter.setShowReadState(isShowReadStateView());

        handleExtraData(null);
        initViews();
        //重新初始化扩展键盘
        initGridView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Logger.i("分享:isFirstInit" + isFirstInit);
        if (isFirstInit) {
            clearMessage();
            isFirstInit = false;
            initHistoryMsg();
        }
        setReadState();

    }

    public void setReadState() {
        //通知服务器消息已读 临时解决 先异步处理
        DispatchHelper.Async("sendAllRead", false, new Runnable() {
            @Override
            public void run() {
                //home回来未读文件设置为已读
                if ("5".equals(chatType)) {
                    connectionUtil.sendSingleAllRead(jid, realJid, MessageStatus.STATUS_SINGLE_READED + "");
                } else {
                    connectionUtil.sendSingleAllRead(jid, jid, MessageStatus.STATUS_SINGLE_READED + "");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //下发同步会话presence
        chatingPresenter.sendSyncConversation();
    }

    @Override
    protected void onDestroy() {
        if (vibrator != null) {
            vibrator.cancel();
        }
        ViewPool.clear();

        if (keyBordOnGlobalLayoutListener != null) {
            KeyboardUtil.detach(this, keyBordOnGlobalLayoutListener);
        }
        CommonConfig.isPlayVoice = false;
        releaseResource();
        MediaPlayerImpl.getInstance().release();
        String draft = edit_msg.getText().toString();
        if (!TextUtils.isEmpty(draft)) {
            InternDatas.putDraft(QtalkStringUtils.parseBareJid(jid) + QtalkStringUtils.parseBareJid(realJid), draft);
        } else {
            InternDatas.removeDraft(QtalkStringUtils.parseBareJid(jid) + QtalkStringUtils.parseBareJid(realJid));
        }
        super.onDestroy();
    }


    /**
     * 页面finish后释放相关资源
     */
    private void releaseResource() {
        if (pbChatViewAdapter != null) {
            pbChatViewAdapter.releaseViews();
        }
        getHandler().removeCallbacksAndMessages(null);
        if (chatingPresenter != null) {
            chatingPresenter.close();
        }
        if (waterMarkTextUtil != null) {
            waterMarkTextUtil.recyleBitmap();
        }
        EventBus.getDefault().unregister(handleChatEvent);
    }


    //第一次进入界面时,去加载一批历史消息
    protected void initHistoryMsg() {
        chatingPresenter.propose();
    }

    //获取intent传递数据
    protected void injectExtras(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey(KEY_JID)) {
                //这个做法是如果没有传入realjid 默认认为jid和realjid相同

                jid = extras.getString(KEY_JID);
                realJid = extras.getString(KEY_JID);
            }
            if (!TextUtils.isEmpty(extras.getString(KEY_REAL_JID))) {//realJid不是空 取出真正的realJid
                realJid = extras.getString(KEY_REAL_JID);
            }
            if (!TextUtils.isEmpty(extras.getString(KEY_BUSI_NAME))) {//bu
                busiName = extras.getString(KEY_BUSI_NAME);
            }
            if (!TextUtils.isEmpty(extras.getString(KEY_SUPPLIER_ID))) {//供应商id
                supplierId = extras.getString(KEY_SUPPLIER_ID);
            }

            if (extras.containsKey(KEY_CHAT_TYPE)) {
                chatType = extras.getString(KEY_CHAT_TYPE);
            }
            if (extras.containsKey(KEY_INPUTTYPE)) {
                input_type = extras.getBoolean(KEY_INPUTTYPE);
            }
            if (extras.containsKey(KEY_UNREAD_MSG_COUNT)) {
                unreadMsgCount.set(extras.getInt(KEY_UNREAD_MSG_COUNT, -1));
            }
        }

        if (chatingPresenter != null) {
            chatingPresenter.close();
        }

        chatingPresenter = new SingleSessionPresenter();

        chatingPresenter.setView(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_JID, jid);
        outState.putString(KEY_REAL_JID, realJid);
        outState.putString(KEY_CHAT_TYPE, chatType);
        outState.putBoolean(KEY_SHOW_READSTATE, isShowReadStateView());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        handleExtraData(savedInstanceState);
    }

    //处理一些额外的数据
    protected void handleExtraData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            jid = savedInstanceState.getString(KEY_JID);
            realJid = savedInstanceState.getString(KEY_REAL_JID);
            chatType = savedInstanceState.getString(KEY_CHAT_TYPE);
        }
        if (jid != null) {
            jid = QtalkStringUtils.parseBareJid(jid);
            jid = QtalkStringUtils.userId2Jid(jid);
        }

    }

    /**
     * 设置本class的events挂载,所有挂载消息应都在本方法执行
     */
    public void addEvents() {
        chat_region.getRefreshableView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        switchStatus(CHAT_STATUS_DEFAULT);
                        KPSwitchConflictUtil.hidePanelAndKeyboard(mPanelRoot);
                        break;
                }
                //设置ej edittext 消除焦点
                edit_msg.clearFocus();
                //对+号功能的layout设置不可见
                if (linearlayout_tab2.getVisibility() == View.VISIBLE)
                    linearlayout_tab2.setVisibility(View.GONE);
                if (linearlayout_tab.getVisibility() == View.VISIBLE)
                    linearlayout_tab.setVisibility(View.GONE);
                return false;
            }
        });
    }

    /**
     * 编辑图片后发送
     *
     * @param edit
     */
    public void onEventMainThread(EventBusEvent.NewPictureEdit edit) {
        if (!TextUtils.isEmpty(edit.mPicturePath)) {
            imageUrl = edit.mPicturePath;
            chatingPresenter.sendImage();
        }
    }

    /**
     * 初始化View
     */
    protected void initViews() {
        if (input_type) {
            edit_region.setVisibility(View.GONE);
        }
        //初始化输入区域
        initInputRegion();

        //初始化键盘切换事件
        initKpswitch();

        //初始化聊天消息list
        initPbChatRegion();

    }


    private void initKpswitch() {
        keyBordOnGlobalLayoutListener = KeyboardUtil.attach(this, mPanelRoot,
                // Add keyboard showing state callback, do like this when you want to listen in the
                // keyboard's show/hide change.
                new KeyboardUtil.OnKeyboardShowingListener() {
                    @Override
                    public void onKeyboardShowing(boolean isShowing) {
                        setTranscriptMode(isShowing);
                        if (isShowing) {
                            switchStatus(CHAT_STATUS_DEFAULT);
                        }

                    }
                });
        // If there are several sub-panels in this activity ( e.p. function-panel, emoji-panel).
        KPSwitchConflictUtil.attach(mPanelRoot, edit_msg,
                new KPSwitchConflictUtil.SwitchClickListener() {
                    @Override
                    public void onClickSwitch(View view, boolean switchToPanel) {
                        setTranscriptMode(switchToPanel);
                        if (switchToPanel) {
                            edit_msg.clearFocus();
                            if (view == emotion_btn) {
                                edit_msg.requestFocus();
                                switchStatus(CHAT_STATUS_EMOTION);
                            } else if (view == tv_options_btn) {
                                switchStatus(CHAT_STATUS_DEFAULT);
                                showRecommendPop();
                            }
                        }
                    }

                    @Override
                    public boolean beforeClick(View view) {
                        if (view.getId() == R.id.voice_switch_btn) {
                            boolean isGranted = PermissionUtils.checkPermissionGranted(PbChatActivity.this
                                    , PermissionDispatcher.permissions.get(PermissionDispatcher.REQUEST_RECORD_AUDIO));
                            if (isGranted) {
                                return true;
                            }
                            return false;
                        }
                        return true;
                    }
                },
                new KPSwitchConflictUtil.SubPanelAndTrigger(linearlayout_tab, tv_options_btn),
                new KPSwitchConflictUtil.SubPanelAndTrigger(linearlayout_tab2, emotion_btn));
    }


    private static final int CHAT_STATUS_DEFAULT = 0;
    private static final int CHAT_STATUS_EMOTION = 1;

    private void switchStatus(int status) {
        switch (status) {
            case CHAT_STATUS_DEFAULT:
                left_btn.setVisibility(View.GONE);
                input_container.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(edit_msg.getText()) || edit_msg.getText().toString().trim().length() == 0) {
                    tv_options_btn.setVisibility(View.VISIBLE);
                    //设置发送按钮可见
                    send_btn.setVisibility(View.GONE);
                } else {
                    tv_options_btn.setVisibility(View.GONE);
                    //设置发送按钮可见
                    send_btn.setVisibility(View.VISIBLE);
                }

                emotion_btn.setText(R.string.atom_ui_new_chat_input_emoji);
                break;
            case CHAT_STATUS_EMOTION:
                left_btn.setVisibility(View.GONE);
                input_container.setVisibility(View.VISIBLE);
                emotion_btn.setText(R.string.atom_ui_new_chat_input_keybord);
                break;
        }
    }

    /**
     * 初始化聊天消息列表
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void initPbChatRegion() {
        if (pbChatViewAdapter == null) {
            pbChatViewAdapter = new ExtendChatViewAdapter(this, jid, getHandler(), false);
        }

        pbChatViewAdapter.setShowReadState(isShowReadStateView());


        pbChatViewAdapter.setRightSendFailureClickHandler(message -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(PbChatActivity.this);
            builder.setTitle(R.string.atom_ui_title_resend_message);

            builder.setPositiveButton(R.string.atom_ui_menu_resend, (dialog, which) -> {
                dialog.dismiss();
                chatingPresenter.resendMessage();
            });

            builder.setNegativeButton(R.string.atom_ui_common_cancel, (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        //对自定义listview设置adapter
        chat_region.setAdapter(pbChatViewAdapter);
        chat_region.getRefreshableView().setFastScrollEnabled(false);
        //设置滑动监听,
        chat_region.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (unreadMsgCount.intValue() > 0 &&
                            chat_region.getRefreshableView().getFirstVisiblePosition() <= pbChatViewAdapter.getCount() - unreadMsgCount.intValue() - 1) {
                        //todo:清除未读数据
                        clearUnread();
                    }
                } else if (scrollState == SCROLL_STATE_FLING) {
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //有新消息提醒 listview到最底部时隐藏提醒textview
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    new_msg_prompt.setVisibility(View.GONE);
                }

            }
        });

        //设置刷新监听器
        chat_region.setOnRefreshListener(onRefresh);

        waterMarkTextUtil.setWaterMarkTextBg(chat_region, this);

    }

    //初始化输入区域
    public void initInputRegion() {
        //设置语音部分不可见
        //输入框外层layout可见
        input_container.setVisibility(View.VISIBLE);
        //左侧键盘小图标按钮不可见
        left_btn.setVisibility(View.GONE);
        //草稿
        String draft = InternDatas.getDraft(QtalkStringUtils.parseBareJid(jid) + QtalkStringUtils.parseBareJid(realJid));
        if (!TextUtils.isEmpty(draft)) {
            edit_msg.setText(draft);
        }
        //edtext清空焦点
        edit_msg.clearFocus();
        //edtext 设置 当输入完文字,点击软键盘上回车触发
        edit_msg.setOnEditorActionListener((v, actionId, event) -> {
            //当id等于发送时
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                //发送消息
                sendMessage();
                return true;//事件消耗
            }
            return false;
        });
        //添加文字改变事件
//        edit_msg.removeTextChangedListener();
        edit_msg.removeTextChangedListener(textWatcher);
        edit_msg.addTextChangedListener(textWatcher);
        //todo: 方法下部还有很多东西,先进行文本阶段测试,暂时不考虑
        //todo: 开始继续
        //初始化功能列表
        initGridView();
        //初始化表情
        initEmoticon();
    }

    /**
     * 初始化表情
     */
    private void initEmoticon() {
        if (!faceView.isInitialize()) {
            faceView.setDefaultOnEmoticionsClickListener(new DefaultOnEmoticionsClickListener());
            faceView.setDeleteImageViewOnClickListener(v -> {
                int keyCode = KeyEvent.KEYCODE_DEL;  //这里是退格键s
                KeyEvent keyEventDown = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
                KeyEvent keyEventUp = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
                edit_msg.onKeyDown(keyCode, keyEventDown);
                edit_msg.onKeyUp(keyCode, keyEventUp);
            });
            faceView.initFaceGridView(EmotionUtils.getExtEmotionsMap(this, false), EmotionUtils.getDefaultEmotion(this), getFavoriteMap());
        }

        //加载表情
        EmotionUtils.getDefaultEmotion(this.getApplicationContext());
//        EmotionUtils.getExtEmotionsMap(this.getApplicationContext(), false);
    }

    public static EmoticionMap getFavoriteMap() {
        UserConfigData userConfigData = new UserConfigData();
        userConfigData.setKey(CacheDataType.kCollectionCacheKey);
        List<UserConfigData> list = ConnectionUtil.getInstance().selectUserConfigValueInString(userConfigData);
        EmoticionMap map = new EmoticionMap("0", list.size() + 1, 0, 0, "");
        EmoticonEntity entity = new EmoticonEntity();
        map.pusEntity(entity.id, entity);
        try {
            for (UserConfigData f : list) {
                EmoticonEntity tmpEntity = new EmoticonEntity();
                tmpEntity.fileFiexd = f.getValue();
                tmpEntity.fileOrg = f.getValue();
                map.pusEntity(f.getSubkey(), tmpEntity);
            }
        } catch (Exception e) {
            Logger.e("getFavoriteMap:" + e.getMessage());
        }
        //收藏表情默认 8*2
        map.showAll = 0;
        return map;
    }

    /**
     * 初始化功能模块
     *
     * @return 1
     */
    private FuncMap initGridData() {
        FuncMap funcMap = new FuncMap();

        FuncItem item = new FuncItem();
        item.id = FuncMap.PHOTO;
        item.icon = "res:///" + R.drawable.atom_ui_sharemore_picture;
        item.textId = getString(R.string.atom_ui_function_photo);
        item.hanlder = this::checkShowGallary;
        funcMap.regisger(item);

        item = new FuncItem();
        item.id = FuncMap.CAMERA;
        item.icon = "res:///" + R.drawable.atom_ui_sharemore_camera;
        item.textId = getString(R.string.atom_ui_user_camera);
        item.hanlder = this::checkShowCamera;
        funcMap.regisger(item);

        return funcMap;
    }

    /**
     * 初始化功能界面
     */
    protected void initGridView() {
        FuncMap defaultFunMap = initGridData();
        int SUPPORT = 0;
        if (chatType == null) {
            chatType = String.valueOf(ConversitionType.MSG_TYPE_CHAT);
        }
        switch (chatType) {
            case ConversitionType.MSG_TYPE_CHAT + "":
                SUPPORT = 1;
                break;
            case ConversitionType.MSG_TYPE_GROUP + "":
                SUPPORT = 2;
                break;
            case ConversitionType.MSG_TYPE_CONSULT + "":
                SUPPORT = 8;
                break;
            case ConversitionType.MSG_TYPE_CONSULT_SERVER + "":
                SUPPORT = 16;
                break;
            case ConversitionType.MSG_TYPE_SUBSCRIPT + "":
                SUPPORT = 32;
                break;
        }
        if (isRobot() || isOtherManager() || isSelf()) {
            SUPPORT = 4;
        }
        int SCOPE = CurrentPreference.getInstance().isMerchants() ? 2 : 1;
        CapabilityResult capabilityResult = CapabilityUtil.getInstance().getCurrentCapabilityData();
        Logger.i("initGridView:" + SUPPORT + SCOPE + JsonUtils.getGson().toJson(capabilityResult));
        if (capabilityResult != null && capabilityResult.trdextendmsg != null) {
            funcMap.clear();
            for (final FuncButtonDesc funcButtonDesc : capabilityResult.trdextendmsg) {
                if ((funcButtonDesc.support & SUPPORT) == SUPPORT
                        && (funcButtonDesc.scope & SCOPE) == SCOPE) {

                    final FuncItem item = new FuncItem();
                    item.id = funcButtonDesc.trdextendId;//tmap.genNewId();
                    item.icon = funcButtonDesc.icon;
                    item.textId = funcButtonDesc.title;
                    if (funcButtonDesc.linkType == 0) {//本地处理的点击
                        if (defaultFunMap.getItem(item.id) != null) {
                            item.hanlder = defaultFunMap.getItem(item.id).hanlder;
                        }
                    } else if (funcButtonDesc.linkType == 1) {
                        item.hanlder = () -> {
                            if (TextUtils.isEmpty(CurrentPreference.getInstance().getVerifyKey()))
                                return;
                            StringBuilder builder = new StringBuilder(funcButtonDesc.linkurl);
                            if (builder.indexOf("?") > -1) {
                                builder.append("&");
                            } else {
                                builder.append("?");
                            }
                            builder.append("username=");
                            builder.append(CurrentPreference.getInstance().getUserid());
                            builder.append("&rk=");
                            builder.append(CurrentPreference.getInstance().getVerifyKey());
                            if (chatType.equals(String.valueOf(ConversitionType.MSG_TYPE_CONSULT_SERVER))) {
                                builder.append("&qchatid=5&type=consult");
                                builder.append("&user_id=");
                                builder.append(getToId());
                                builder.append("&realfrom=");
                                builder.append(CurrentPreference.getInstance().getPreferenceUserId());
                                builder.append("&realto=");
                                builder.append(getRealJid());
                            } else if (chatType.equals(String.valueOf(ConversitionType.MSG_TYPE_GROUP))) {
                                builder.append("&group_id=");
                                builder.append(getToId());
                                builder.append("&type=groupchat");
                            } else {
                                builder.append("&user_id=");
                                builder.append(getToId());
                                builder.append("&type=chat");
                            }
                            builder.append("&company=");
                            builder.append(QtalkNavicationService.COMPANY);
                            builder.append("&domain=");
                            builder.append(QtalkNavicationService.getInstance().getXmppdomain());
                            builder.append("&q_d=");
                            builder.append(QtalkNavicationService.getInstance().getXmppdomain());
                            Intent intent = new Intent(PbChatActivity.this, QunarWebActvity.class);
                            intent.setData(Uri.parse(builder.toString()));
                            intent.putExtra(WebMsgActivity.IS_HIDE_BAR, true);
                            startActivity(intent);
                        };
                    } else if (funcButtonDesc.linkType == 2) {//request
                        item.hanlder = () -> {
                            try {
                                JSONObject requestBody = new JSONObject();
                                requestBody.put("from", getFromId());
                                requestBody.put("to", getToId());
                                requestBody.put(KEY_REAL_JID, getRealJid());
                                requestBody.put(KEY_CHAT_TYPE, chatType);
                                HttpUtil.sendCapabilityRequest(funcButtonDesc.linkurl, requestBody);
                            } catch (JSONException e) {
                                Logger.i(e.getMessage());
                            }
                        };
                    } else if (funcButtonDesc.linkType == 4) {//schema
                        item.hanlder = () -> {
                            Intent i = new Intent("android.intent.action.VIEW", Uri.parse(funcButtonDesc.linkurl));
                            startActivity(i);
                        };
                    }
                    funcMap.regisger(item);
                }
            }
        } else {
            funcMap = defaultFunMap;
        }
        if (linearlayout_tab != null) {
            linearlayout_tab.init(this, funcMap);
        }
    }

    private void setTranscriptMode(boolean hasFocus) {
        chat_region.getRefreshableView().setTranscriptMode(hasFocus ? AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL : AbsListView.TRANSCRIPT_MODE_NORMAL);
    }


    //发送消息
    public void sendMessage() {
        //获取消息正文
        String str = edit_msg.getText().toString();
        //判断当前文本信息,
        if (TextUtils.isEmpty(str)) {
            //不符合规则不发
            return;
        }

        //表情雨
        String emop = EmoticonRainUtil.getEmoPath(str.toLowerCase());
        if (!TextUtils.isEmpty(emop))
            EmoticonRainUtil.startRain(emoticonRainView, emop);

        chatingPresenter.sendMsg();

        //方便下条信息
        mAtManager.reset();
        edit_msg.setText("");

    }

    //获取消息文本框中的内容
    public String getInputMsg() {
        return edit_msg.getText().toString();
    }

    @Override
    public void setInputMsg(String text) {
        edit_msg.setText(text);
        edit_msg.requestFocus();
        edit_msg.setSelection(text.length());
        KPSwitchConflictUtil.showKeyboard(mPanelRoot, edit_msg);
    }

    //加载更多的历史消息
    public void loadMoreHistory() {
        //todo:这里去加载更多的历史消息,pb理论上应该在这里面挂载一个添加历史消息event
        ((ICloudRecordPresenter) chatingPresenter).showMoreOldMsg(false);
    }


    //重设消息提示textview
    private void resetNewMsgCount() {
        if (chat_region.getRefreshableView().getCount() > 0) {
            //mListView.getRefreshableView().setSelection(mListView.getRefreshableView().getCount() - 1); //在vivo手机上会导致粘贴功能不正常
            //移动到最后一条消息
            chat_region.getRefreshableView().smoothScrollToPosition(chat_region.getRefreshableView().getCount() - 1);
        }
        //消息提示textview设置不可见
        new_msg_prompt.setVisibility(View.GONE);
        //未读新消息初始化  设置为0
        newMsgCount = 0;
    }

    /**
     * 绑定View
     */
    protected void bindViews() {
        edit_region = (LinearLayout) findViewById(R.id.edit_region);
        left_btn = (IconView) findViewById(R.id.left_btn);
        input_container = (LinearLayout) findViewById(R.id.input_container);
        total_bottom_layout = (LinearLayout) findViewById(R.id.total_bottom_layout);
        atom_bottom_frame = (RelativeLayout) findViewById(R.id.atom_bottom_frame);
        edit_msg = (EmojiconEditText) findViewById(R.id.edit_msg);
        tv_options_btn = (IconView) findViewById(R.id.tv_options_btn);
        send_btn = (TextView) findViewById(R.id.send_btn);

        chating_view = (RelativeLayout) findViewById(R.id.chating_view);
        chat_region = (com.handmark.pulltorefresh.library.PullToRefreshListView) findViewById(R.id.chat_region);
        new_msg_prompt = (TextView) findViewById(R.id.new_msg_prompt);//更新消息条目数有关
        emotion_btn = (IconView) findViewById(R.id.tv_emojicon);
        outter_msg_prompt = (LinearLayout) findViewById(R.id.outter_msg_prompt);
        outter_msg = (TextView) findViewById(R.id.outter_msg);

        emoticonRainView = (EmoticonRainView) findViewById(R.id.emoticonRainView);


        no_prompt = (TextView) findViewById(R.id.no_prompt);
        close_prompt = (TextView) findViewById(R.id.close_prompt);
        relativeLayout = (KPSwitchRootLinearLayout) findViewById(R.id.resizelayout);
//        line = findViewById(line);
        new_msg_prompt.setOnClickListener(this);

        tv_options_btn.setOnClickListener(this);
        left_btn.setOnClickListener(this);
        send_btn.setOnClickListener(this);
        emotion_btn.setOnClickListener(this);
        edit_msg.setOnFocusChangeListener(this);
        no_prompt.setOnClickListener(this);
        close_prompt.setOnClickListener(this);
        outter_msg.setOnClickListener(this);
        chating_view.setOnClickListener(this);
        qtNewActionBar = (QtNewActionBar) this.findViewById(R.id.my_action_bar);
        setNewActionBar(qtNewActionBar);

        mPanelRoot = (KPSwitchPanelLinearLayout) findViewById(R.id.panel_root);
        linearlayout_tab = (OperationView) findViewById(R.id.linearlayout_tab);
        linearlayout_tab2 = (LinearLayout) findViewById(R.id.linearlayout_tab2);
        atom_bottom_more = (LinearLayout) findViewById(R.id.atom_bottom_more);
        faceView = (EmotionLayout) findViewById(R.id.faceView);

        //+号图标下更多更能设置为不可见
        linearlayout_tab.setVisibility(View.GONE);
        //+号下疑似视频功能设置为不可见
        linearlayout_tab2.setVisibility(View.GONE);

        addEvents();


    }

    /**
     * 单击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        //点击消息提示textview
        if (i == R.id.new_msg_prompt) {
            resetNewMsgCount();
            //点击发送按钮 发送消息
        } else if (i == R.id.send_btn) {
            sendMessage();
            //点击了屏幕,强行关闭软键盘
        } else if (i == R.id.chating_view) {
            KPSwitchConflictUtil.hidePanelAndKeyboard(mPanelRoot);
        } else if (i == R.id.tv_options_btn) {
//            //加号+功能按钮
            switchStatus(CHAT_STATUS_DEFAULT);
        } else if (i == R.id.left_btn) {
            //切换键盘
            switchStatus(CHAT_STATUS_DEFAULT);
            KPSwitchConflictUtil.showKeyboard(mPanelRoot, edit_msg);
        } else if (i == R.id.tv_emojicon) {
            //表情
            switchStatus(CHAT_STATUS_EMOTION);
        }
    }

    /**
     * 焦点改变事件
     *
     * @param v
     * @param hasFocus
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    /**
     * 是否展示已读未读
     *
     * @return
     */
    protected boolean isShowReadStateView() {
        return !isRobot() && !isOtherManager();
    }

    protected boolean isRobot() {
        String[] configs = getResources().getStringArray(R.array.atom_ui_robot_config);
        List<String> readStateConfig = Arrays.asList(configs);
        return readStateConfig != null && readStateConfig.contains(QtalkStringUtils.parseId(jid));
    }

    protected boolean isOtherManager() {
        String[] configs = getResources().getStringArray(R.array.atom_ui_otehr_manager);
        List<String> readStateConfig = Arrays.asList(configs);
        return readStateConfig != null && readStateConfig.contains(QtalkStringUtils.parseId(jid));
    }

    protected boolean isSelf() {
        return CurrentPreference.getInstance().getPreferenceUserId().equals(jid);
    }

    @Override
    public void setNewMsg2DialogueRegion(final IMMessage newMsg) {

        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
            if (unreadMsgCount.intValue() > 0) {
                unreadMsgCount.incrementAndGet();
            }
            //向列表中添加一条新数据
            pbChatViewAdapter.addNewMsg(newMsg);
            //当前为普通消息时,记录新消息
            if (newMsg.getDirection() == IMMessage.DIRECTION_RECV || newMsg.getDirection() == IMMessage.DIRECTION_SEND) {
                newMsgCount++;
            }
            //当前listview中有消息展示
            if (chat_region.getRefreshableView().getCount() > 0) {
                //如果消息时自己发出
                if (newMsg.getDirection() == IMMessage.DIRECTION_SEND ||
                        //如果输入框获得焦点
                        edit_msg.isFocused() ||
                        //如果更多功能View显示时
                        (linearlayout_tab != null && linearlayout_tab.getVisibility() == View.VISIBLE) ||
                        //如果现在展示的数据下滑了5条
                        chat_region.getRefreshableView().getLastVisiblePosition() >= chat_region.getRefreshableView().getCount() - 5) {
                    //把当前listview拉倒最底部
//                        chat_region.getRefreshableView().setSelection(chat_region.getRefreshableView().getCount() - 1);
                    chat_region.getRefreshableView().smoothScrollToPosition(chat_region.getRefreshableView().getCount() - 1);
                    //提示新消息textview 设置为不可见
                    new_msg_prompt.setVisibility(View.GONE);
                    //把新消息数据重设为0
                    newMsgCount = 0;
                } else {
                    //如果当前是普通消息,即可以显示在左侧或右侧
                    if (newMsg.getDirection() == IMMessage.DIRECTION_RECV || newMsg.getDirection() == IMMessage.DIRECTION_SEND) {
                        //拼接 收到新消息几条 类似字段
//                            showUnreadView(newMsgCount,true);
                        //提示新消息textview设置为文本
                        String msg = MessageFormat.format(getString(R.string.atom_ui_tip_new_msg_prompt), newMsgCount);
                        new_msg_prompt.setText(msg);
                        //提示新消息textview设置为可见
                        new_msg_prompt.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void showUnReadCount(final int count) {
        if (!isFinishing())
            runOnUiThread(() -> setActionBarLeftCount(count));

    }

    @Override
    public String getOf() {
        return of;
    }

    @Override
    public String getOt() {
        return ot;
    }

    @Override
    public String getRealJid() {
        return realJid;
    }

    //获取消息来自于谁的id
    @Override
    public String getFromId() {
        return CurrentPreference.getInstance().getPreferenceUserId();
    }

    @Override
    public String getFullName() {
        return null;
    }

    @Override
    public String getToId() {
        return jid;
    }

    @Override
    public String getChatType() {
        return chatType;
    }

    @Override
    public String getUserId() {
        return CurrentPreference.getInstance().getPreferenceUserId();
    }

    @Override
    public String getUploadImg() {
        return imageUrl;
    }

    @Override
    public boolean isMessageExit(String msgId) {
        List<IMMessage> messages = pbChatViewAdapter.getMessages();
        if (messages == null || messages.size() == 0) {
            return false;
        } else {
            for (IMMessage imMessage : messages) {
                if (msgId.equals(imMessage.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void refreshDataset() {
        getHandler().post(() -> pbChatViewAdapter.notifyDataSetChanged());
    }

    @Override
    public void setCurrentStatus(String status) {

    }

    @Override
    public void setHistoryMessage(final List<IMMessage> historyMessage, final int unread) {
        runOnUiThread(() -> {
            if (isFinishing()) {
                return;
            }
            unreadMsgCount.set(unread);
            chat_region.onRefreshComplete();
            if (historyMessage != null && historyMessage.size() > 0) {

                pbChatViewAdapter.setMessages(historyMessage);
                if (chat_region.getRefreshableView().getCount() > 0)
                    chat_region.getRefreshableView().setSelection(chat_region.getRefreshableView().getCount() - 1);
            }
            if (unread > 5) {
                showUnreadView(unread);
            }
        });
    }

    /**
     * 未读消息数提示
     * isNew 是否是新消息
     */
    @SuppressLint("ObjectAnimatorBinding")
    public void showUnreadView(int unread) {
        if (unread <= 0 || isFinishing()) {
            return;
        }

        ImageView tipImage = new ImageView(PbChatActivity.this);
        tipImage.setImageResource(R.drawable.atom_ui_chat_unread_tip);

        final TextView textView = new TextView(PbChatActivity.this);
        int padding = Utils.dipToPixels(PbChatActivity.this, 4);
        int size = Utils.dipToPixels(PbChatActivity.this, 30);
        int topMargin = Utils.dipToPixels(PbChatActivity.this, 30);
        final LinearLayout linearLayout = new LinearLayout(PbChatActivity.this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        linearLayout.setBackgroundResource(R.drawable.atom_ui_float_tab);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                size);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(0, topMargin, 0, 0);
        linearLayout.setPadding(padding * 2, padding, padding * 2, padding);
        linearLayout.setLayoutParams(layoutParams);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        textView.setTextColor(Color.parseColor("#666666"));

        String msg = unread + (String) getText(R.string.atom_ui_tip_unread_message);
        textView.setText(msg);
        textView.setPadding(padding * 2, 0, 0, 0);
        textView.setOnClickListener((view) -> {
            chat_region.getRefreshableView().smoothScrollToPosition(pbChatViewAdapter.getCount() - unreadMsgCount.intValue() - 1);
            clearUnread();
        });
        linearLayout.addView(tipImage);
        linearLayout.addView(textView);
        linearLayout.setTag(TAG_UNREAD_VIEW);
        final LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.setAnimator(LayoutTransition.APPEARING, ObjectAnimator.ofFloat(this, "scaleX", 0, 1));
        getHandler().postDelayed(() -> {
            if (!isFinishing()) {
                chating_view.setLayoutTransition(layoutTransition);
                chating_view.addView(linearLayout);
            }
        }, 500);
    }

    protected void clearUnread() {
        unreadMsgCount.set(0);
        chating_view.setLayoutTransition(null);
        if (chating_view != null && chating_view.getChildCount() > 0) {
            for (int i = 0; i <= chating_view.getChildCount() - 1; i++) {
                View v = chating_view.getChildAt(i);
                if (TAG_UNREAD_VIEW.equals(v.getTag())) {
                    chating_view.removeView(v);
                    break;
                }
            }
        }
    }

    @Override
    public void addHistoryMessage(final List<IMMessage> historyMessage) {
        getHandler().post(() -> {
            chat_region.onRefreshComplete();
            if (historyMessage == null || historyMessage.size() == 0) {
                return;
            }
            if (historyMessage.size() == 1) {
                if (TextUtils.isEmpty(historyMessage.get(0).getBody())) {
                    historyMessage.get(0).setBody(getString(R.string.atom_ui_cloud_record_prompt));
                    pbChatViewAdapter.addOldMsg(historyMessage);
                    return;
                }
                //已经拉取不到消息则不重复提示
                IMMessage firstMessage = pbChatViewAdapter.getFirstMessage();
                if (firstMessage != null && firstMessage.getMsgType() == MessageType.MSG_TYPE_NO_MORE_MESSAGE &&
                        historyMessage.get(0).getMsgType() == MessageType.MSG_TYPE_NO_MORE_MESSAGE) {
                    return;
                }
            }
            pbChatViewAdapter.addOldMsg(historyMessage);
            chat_region.getRefreshableView().setSelection(historyMessage.size());
        });
    }

    @Override
    public void addHistoryMessageLast(final List<IMMessage> historyMessage) {

    }

    @Override
    public void showNoticePopupWindow(NoticeBean noticeBean) {
        if (isFinishing()) {
            return;
        }
        super.showNoticePopupWindow(noticeBean);
    }

    @Override
    public void sendEditPic(String path) {
        imageUrl = path;
//        imageUrl = edit.mPicturePath;
        chatingPresenter.sendImage();

    }

    @Override
    public void clearAndAddMsgs(final List<IMMessage> historyMessage, final int unread) {
        Logger.i("分享:clearAndAddMsgs");
        getHandler().post(() -> {
            if (isFinishing()) {
                return;
            }
            unreadMsgCount.set(unread);
            chat_region.onRefreshComplete();
            if (historyMessage != null) {

                pbChatViewAdapter.clearAndAddMsgs(historyMessage);
                if (chat_region.getRefreshableView().getCount() > 0)
                    chat_region.getRefreshableView().setSelection(chat_region.getRefreshableView().getCount() - 1);
            }
            if (unread > 5) {
                showUnreadView(unread);
            }
        });

        setReadState();

    }

    @Override
    public void closeActivity() {
//        this.onBackPressed();
        //调用backPreseed可能会引起Can not perform this action after onSaveInstanceState异常 所以直接调用finish
        finish();
    }

    @Override
    public void setTitle(final String title) {
        runOnUiThread(() -> setActionBarTitle(title));
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public int getListSize() {
        return pbChatViewAdapter.getCount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ACTIVITY_GET_CAMERA_IMAGE:
                getCameraImageResult(data);
                break;
            case ACTIVITY_SELECT_PHOTO:
                selectPhotoResult(data);
                break;
            default:
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getCameraImageResult(Intent data) {
        if (data != null) {
            //自定义相机 带录制视频版本 2018-01-23
            String videopath = data.getStringExtra("videopath");
            if (!TextUtils.isEmpty(videopath)) {
                File file = new File(videopath);
                if (file.exists()) {
                    chatingPresenter.sendVideo(videopath);
                } else {
                    Toast.makeText(this, R.string.atom_ui_file_not_exist, Toast.LENGTH_SHORT).show();
                }
            } else {
                imageUrl = data.getStringExtra("path");
                if (!TextUtils.isEmpty(imageUrl)) {
                    chatingPresenter.sendImage();
                }
            }
        }
    }

    public void selectPhotoResult(Intent data) {
        try {
            if (data != null) {
                //新版图片选择器
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images.size() > 0) {
                    for (ImageItem image : images) {
                        imageUrl = image.path;
                        chatingPresenter.sendImage();
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "ERROR", e);
        }
    }

    public void checkShowCamera() {
        PermissionDispatcher.
                requestPermissionWithCheck(PbChatActivity.this, new int[]{PermissionDispatcher.REQUEST_CAMERA,
                                PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE, PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE, PermissionDispatcher.REQUEST_RECORD_AUDIO}, PbChatActivity.this,
                        SHOW_CAMERA);


    }

    public void checkShowGallary() {
        PermissionDispatcher.
                requestPermissionWithCheck(PbChatActivity.this, new int[]{PermissionDispatcher.REQUEST_READ_EXTERNAL_STORAGE,
                        PermissionDispatcher.REQUEST_WRITE_EXTERNAL_STORAGE}, PbChatActivity.this, SELECT_PIC);

    }

    void selectPic() {
        //新版图片选择器
        ImageSelectUtil.startSelectPhotos(this, new SelectCallback() {
            @Override
            public void onResult(ArrayList<Photo> photos, ArrayList<String> paths, boolean isOriginal) {
//                new String();
                try {
                    if (photos != null) {
                        //新版图片选择器
//                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                        if (paths.size() > 0) {
                            for (Photo image : photos) {
                                if (image.type.startsWith("image")) {
                                    imageUrl = image.path;
                                    chatingPresenter.sendImage();
                                } else if (image.type.startsWith("video")) {


                                    String time = IMUserDefaults.getStandardUserDefaults().getStringValue(CommonConfig.globalContext,
                                            CurrentPreference.getInstance().getUserid()
                                                    + QtalkNavicationService.getInstance().getXmppdomain()
                                                    + CommonConfig.isDebug
                                                    + "videoMaxTime");
                                    if (TextUtils.isEmpty(time) || time.equals("0")) {
                                        time = (300 * 1000) + "";
                                    }
                                    if (image.duration > Long.parseLong(time)) {
                                        boolean flag = DataUtils.getInstance(PbChatActivity.this).getPreferences("notRemindVideoToFile", false);
                                        if (flag) {
                                            chatingPresenter.sendFile(image.path);
                                        } else {
                                            commonDialog = new CommonDialog.Builder(PbChatActivity.this);
                                            commonDialog.setMessage("视频超过限制,只可转为文件发送!");
                                            commonDialog.setPositiveButton(getString(R.string.atom_ui_common_confirm), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    chatingPresenter.sendFile(image.path);

                                                }
                                            });
                                            commonDialog.setNegativeButton(getString(R.string.atom_ui_common_cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialog, int which) {
                                                    dialog.dismiss();
//                            finish();

                                                }
                                            });
                                            commonDialog.setNeutralButton(getString(R.string.atom_ui_btn_not_remind), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int i) {
                                                    dialog.dismiss();
                                                    DataUtils.getInstance(PbChatActivity.this).putPreferences("notRemindVideoToFile", true);
                                                    chatingPresenter.sendFile(image.path);
                                                }
                                            });
                                            commonDialog.create().show();
                                        }

                                    } else {
                                        chatingPresenter.sendVideo(image.path);
                                    }


                                } else {
                                    Toast.makeText(PbChatActivity.this, "未知消息,不可发送", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "ERROR", e);
                }
            }
//        ImagePicker.getInstance().setSelectLimit(9);
//        Intent intent1 = new Intent(this, ImageGridActivity.class);
//        /* 如果需要进入选择的时候显示已经选中的图片，
//         * 详情请查看ImagePickerActivity
//         * */
////                                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
//        startActivityForResult(intent1, ACTIVITY_SELECT_PHOTO);
        });
    }

    void showCamera() {
        Logger.i("相机拍照");
        //自定义相机 带录制视频版本
        startActivityForResult(new Intent(this, CameraActivity.class), ACTIVITY_GET_CAMERA_IMAGE);
    }


    RecommendPhotoPop photoPop;

    void showRecommendPop() {
        atom_bottom_frame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isFinishing()) {
                    return;
                }
                if (photoPop != null) {
                    return;
                }
                if (linearlayout_tab.getVisibility() == View.GONE)
                    return;
                if (latestImage != null && (System.currentTimeMillis() - latestImage.addTime * 1000) > 30000) {
                    return;
                }
                if (latestImage == null || TextUtils.isEmpty(latestImage.path)) {
                    return;
                }
                photoPop = RecommendPhotoPop.recommendPhoto(PbChatActivity.this, total_bottom_layout, latestImage.path, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageUrl = latestImage.path;
                        chatingPresenter.sendImage();
                        if (photoPop.isShowing())
                            photoPop.dismiss();
                    }
                });
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
                            return;
                        }
                        if (photoPop != null && photoPop.isShowing()) {
                            photoPop.dismiss();
                        }
                    }
                }, 15 * 1000);
            }
        });
    }

    /**
     * 申请相机权限提示
     */
    private void showCameraPermissionDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        commonDialog.setTitle(getString(R.string.atom_ui_open_permission_title));
        commonDialog.setMessage(getString(R.string.atom_ui_open_permission_camera_message));
        commonDialog.setPositiveButton(getString(R.string.atom_ui_setting_title), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                Uri packageURI = Uri.parse("package:" + PbChatActivity.this.getApplicationInfo().packageName);
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                startActivity(intent);

            }
        });
        commonDialog.setNegativeButton(getString(R.string.atom_ui_common_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        commonDialog.create().show();
    }

    @Override
    public void responsePermission(int requestCode, boolean granted) {
        if (!granted) {
            if (requestCode == SHOW_CAMERA) {
                showCameraPermissionDialog();
            }
            return;
        }
        if (requestCode == SHOW_CAMERA) {
            showCamera();
        } else if (requestCode == SELECT_PIC) {
            selectPic();
        }
    }

    @Override
    public void onTextAdd(String content, int start, int length) {
        Editable edit = edit_msg.getEditableText();
        edit.insert(start, content);
    }

    @Override
    public void onTextDelete(int start, int length) {
        Editable edit = edit_msg.getEditableText();
        edit.delete(start, start + length);
    }

    private final class DefaultOnEmoticionsClickListener implements FaceGridView.OnEmoticionsClickListener {
        @Override
        public void onEmoticonClick(EmoticonEntity entity, String pkgId) {
            StringBuilder text = new StringBuilder();
            text.append((char) 0).
                    append(pkgId).
                    append((char) 1).
                    append(entity.shortCut).append((char) 255);
            int index = edit_msg.getSelectionStart();
            Editable edit = edit_msg.getEditableText();
            if (index < 0 || index > edit.length()) {
                edit.append(text);
            } else {
                edit.insert(index, text);
            }
        }
    }

    protected class HandleChatEvent {
        /**
         * 表情下载完成更新
         *
         * @param downEmojComplete
         */
        public void onEventMainThread(EventBusEvent.DownEmojComplete downEmojComplete) {
            faceView.resetTabLayout();
        }

    }

    @Override
    public void updateUploadProgress(final IMMessage message, final int progress,
                                     final boolean isDone) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbChatViewAdapter.notifyDataSetChanged();
                LogUtil.i("lex pbactivity  updateUploadProgress  progress = " + message.getProgress() + "   status = " + message.getReadState());
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (photoPop != null && photoPop.isShowing()) {
            photoPop.dismiss();
        }
        if (mPanelRoot != null && mPanelRoot.getVisibility() == View.VISIBLE) {
            KPSwitchConflictUtil.hidePanelAndKeyboard(mPanelRoot);
            return;
        }
        if (pbChatViewAdapter != null && pbChatViewAdapter.isShareStatus()) {
            atom_bottom_frame.setVisibility(View.VISIBLE);
            atom_bottom_more.setVisibility(View.GONE);
            pbChatViewAdapter.changeShareStatus(false);
            pbChatViewAdapter.notifyDataSetChanged();
            return;
        }
        super.onBackPressed();
    }


    ImageItem latestImage;

    @Override
    public void onImagesLoaded(ImageItem imageItem) {
        latestImage = imageItem;
    }

    @Override
    public void popNotice(NoticeBean noticeBean) {
        showNoticePopupWindow(noticeBean);
    }

    @Override
    public void clearMessage() {
        pbChatViewAdapter.clearAndAddMsgs(new ArrayList<IMMessage>());
    }

    @Override
    public void sendRobotMsg(String msg) {
        chatingPresenter.sendRobotMsg(msg);
    }

    @Override
    public String getBackupInfo() {
        List<RbtMsgBackupInfo> list = new ArrayList<>();

        RbtMsgBackupInfo info = new RbtMsgBackupInfo();
        info.type = 5 * 10000 + 10;
        RbtMsgBackupInfo.Data data = new RbtMsgBackupInfo.Data();
//        if(params != null){
        if (!TextUtils.isEmpty(busiName)) {
            data.bu = busiName;//params.bu;
        }
        if (!TextUtils.isEmpty(supplierId)) {
            data.bsid = supplierId;//sId
        }
        info.data = data;
        RbtMsgBackupInfo info1 = new RbtMsgBackupInfo();
        info1.type = 10002;
        RbtMsgBackupInfo.Data data1 = new RbtMsgBackupInfo.Data();
        data1.rbtMsg = 1;
        info1.data = data1;

        list.add(info);
        list.add(info1);
        return JsonUtils.getGson().toJson(list);
    }

    @Override
    public IMMessage getListFirstItem() {
        if (pbChatViewAdapter.getFirstMessage().getMsgType() == -99) {
            return pbChatViewAdapter.getItem(1);
        } else {
            return pbChatViewAdapter.getFirstMessage();
        }
    }

    @Override
    public IMMessage getListLastItem() {
        return pbChatViewAdapter.getLastMessage();
    }

    @Override
    public void onRefreshComplete() {
        chat_region.onRefreshComplete();
    }

    @Override
    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    @Override
    public int getUnreadMsgCount() {
        return unreadMsgCount.get();
    }

}
