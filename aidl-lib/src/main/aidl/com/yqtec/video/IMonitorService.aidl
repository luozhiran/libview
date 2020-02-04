// IMyAidlInterface.aidl
package com.yqtec.video;
import com.yqtec.video.IStopMonitorServiceCallback;
import com.yqtec.video.VideoCallInfo;
interface IMonitorService {
//  void startMonitorService(int roomId,boolean isCallingActive,String receiverFid,int receiverPid,
//  String cmdMessage,boolean isMonitorMode,boolean isNeedRotate,String receiveIdentifier,String mPortraitUrl,String name,int receiverSex);

  void startMonitorService(in VideoCallInfo info);
  void stopMonitorService();
  void setStopMonitorServiceCallback(IStopMonitorServiceCallback callback);
  String isActive();
  boolean isMonitorMode();
  void endIliveCall(String pid,String toyId);
  String serviceIsActive();
}
