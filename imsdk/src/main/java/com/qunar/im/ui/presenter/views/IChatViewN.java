package com.qunar.im.ui.presenter.views;

import android.content.Context;

import com.qunar.im.base.jsonbean.NoticeBean;
import com.qunar.im.base.module.IMMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by xinbo.wang on 2015/1/30.
 */
public interface IChatViewN {
    String getInputMsg();
    String getRefenceString();
    void setInputMsg(String text);
    void setNewMsg2DialogueRegion(IMMessage newMsg);
    void showUnReadCount(int count);
    //真实Id
    String getAutoReply();
    String getOf();
    String getOt();
    String getRealJid();
    String getFromId();
    String getFullName();
    String getToId();
    String getChatType();
    String getUserId();
    String getUploadImg();

    int getUnreadMsgCount();
    List<IMMessage> getSelMessages();
    boolean isMessageExit(String msgId);
    void refreshDataset();
    void setCurrentStatus(String status);
    void setHistoryMessage(List<IMMessage> historyMessage, int unread);
    void addHistoryMessage(List<IMMessage> historyMessage);
    void addHistoryMessageLast(List<IMMessage> historyMessage);

    void showNoticePopupWindow(NoticeBean noticeBean);

    //删除小心重新显示
    void clearAndAddMsgs(List<IMMessage> historyMessage, int unread);
    void setTitle(String title);
    //获取Context对象
    Context getContext();
    //获取当前list数据量
    int getListSize();

    //发送编辑图片
    void sendEditPic(String path);

    //关闭会话窗口
    void closeActivity();

    //解析加密信令消息
    void parseEncryptSignal(IMMessage message);

    void updateUploadProgress(IMMessage message, int progress, boolean isDone);

    //添加表情
    void isEmotionAdd(boolean flag);

    //弹出pop通知
    void popNotice(NoticeBean noticeBean);

    //清空消息
    void clearMessage();

    void sendRobotMsg(String msg);

    String getBackupInfo();

    IMMessage getListFirstItem();

    IMMessage getListLastItem();

    void onRefreshComplete();

    boolean getSearching();

    void showToast(String str);

}
