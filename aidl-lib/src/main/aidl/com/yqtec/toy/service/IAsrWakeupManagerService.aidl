package com.yqtec.toy.service;
import com.yqtec.toy.service.IAsrServiceCallback;
import com.yqtec.toy.service.IWakeupServiceCallback;
import com.yqtec.toy.service.IPersistWakeupServiceCallback;

interface IAsrWakeupManagerService{
    void initialize();
    void setParams(String params);
	boolean startAsr(int silenceTimeout, int smode);
	boolean startAsrGrade(String word);
	void stopRecord();
	void cancelAsr();
	boolean isInSpeech();
	int getEngineState();
	void setSkipInvalidEchoCancel(int ms);
	String getRecordFilename();
	void registerAsrRemoteCallback(IAsrServiceCallback cb);
	void unregisterAsrRemoteCallback(IAsrServiceCallback cb);
	void loadConfigFile(String configFileName);
	void setWakeupParam(int id, float value);
	void setWakeupRecordParam(String path, int vadTimeout);
	void startWakeup(boolean regExtraListener);
	void stopWakeup();
    void registerWakeupRemoteCallback(IWakeupServiceCallback cb);
    void unregisterWakeupRemoteCallback(IWakeupServiceCallback cb);
    String tts(String input);
   	boolean startAsrSmode(int silenceTimeout, String smode);
    void setPersistWakeupParam(int id, float value);
    void startPersistWakeup(String mode);
    void stopPersistWakeup();
    void registerPersistWakeupRemoteCallback(IPersistWakeupServiceCallback cb);
    void unregisterPersistWakeupRemoteCallback(IPersistWakeupServiceCallback cb);
}