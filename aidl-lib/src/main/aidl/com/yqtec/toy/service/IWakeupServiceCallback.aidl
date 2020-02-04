package com.yqtec.toy.service;

interface IWakeupServiceCallback{
    void onWakeupSpotKeyword(String keyword, int id);
    void onWakeupDetectPotentialKeyword(String keyword, int id, boolean discard);
    void onWakeupError(int code, int errType);
    void onWakeupMicLevelChange(float volume);
    void onWakeupRecordStart();
    void onWakeupRecordStop(String filename);
    void onWakeupSpeechBegin(long silenceMs);
    void onWakeupSpeechEnd();
}