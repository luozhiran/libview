package com.yqtec.services;
import com.yqtec.services.BaiduTtsListenerCallback;

interface BaiduTtsService {
    void speak(String text);
    void stop();
    void pause();
    void resume();
    void batchSpeak();
    void setParam(String speaker,String volum,String speed,String pitch);
    void setBaiduTtsListener(BaiduTtsListenerCallback callback);
}
