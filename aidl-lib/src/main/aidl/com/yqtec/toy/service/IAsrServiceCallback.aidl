package com.yqtec.toy.service;

interface IAsrServiceCallback{
	void onAsrResult(String result, boolean isLast);
	void onAsrMicLevelChange(float volume);
	void onAsrRecordStart();
	void onAsrRecordStop();
	void onAsrError(int code, int errType);
}