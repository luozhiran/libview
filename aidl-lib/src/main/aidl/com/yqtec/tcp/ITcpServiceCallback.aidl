package com.yqtec.tcp;

interface ITcpServiceCallback{
    void onReceive(int mt, int st, String desc, in byte[] content, String tag, boolean bPush);
    void onNetworkError(int errno, String emsg);
}
