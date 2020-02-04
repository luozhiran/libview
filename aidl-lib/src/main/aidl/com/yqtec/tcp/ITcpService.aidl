package com.yqtec.tcp;
import com.yqtec.tcp.ITcpServiceCallback;

interface ITcpService{
    void getToyid(String deviceId);
    void login(String info);
    void logout();
    void sendMsg(int mt, int st, String desc, in byte[] content, String tag);
    void getOfflineMsg(int ecode, String desc);
    void setCallback(ITcpServiceCallback cb);
    void registerCallback(ITcpServiceCallback cb);
    void unregisterCallback(ITcpServiceCallback cb);
    void shutdown();
}
