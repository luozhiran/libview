package com.yqtec.logagent.point;

import android.util.Log;

import com.google.gson.Gson;
import com.sup.itg.netlib.ItgNetSend;
import com.sup.itg.netlib.okhttpLib.interfaces.ItgCallback;
import com.yqtec.logagent.LogAgent;
public class DetailPointControl {
    public static final String ENTEROPE = "ENTEROPE";
    public static final String ENTERSOU = "ENTERSOU";
    public static final String EXITMOPE = "EXITMOPE";
    public static final String EXITMSOU = "EXITMSOU";
    public static final String EXITMFNI = "EXITMFNI";
    public static final String TOUCH = "touch";
    public static final String SPEECH = "speech";
    public static final String ACTION_ENTER = "enter";
    public static final String ACTION_task = "task";
    public static final String ACTION_robot = "robot";
    public static final String ACTION_next = "next";
    public static final String ACTION_prev = "prev";
    public static final String ACTION_retry = "retry";
    public static final String ACTION_parentcmd = "parentcmd";

    public static final String TYPE_MEDIA = "";


    public static volatile DetailPointControl instance;
    private BasePoint basePoint;


    // log类型 目前已有的类型有：
    // chat 闲聊
    // media 音视频
    // course 课程
    // videolog 视频通话
    // appmarket 百宝箱
    // amuse 游乐场
    public enum BehaviorType {
        chat, media, course, videolog, appmkt, amuse
    }

    public enum EnterOperate {
        speech, touch
    }

    public enum ExitSource {
        close, next, prev, retry, parentcmd,
    }

    public enum ExitOperate {
        touch, speech
    }

    public enum ExitFinish {
        finish, abort
    }


    public void addMediaEnterRecord(String toyId, String[] enterMode, String action, String url, String cate, String resName, String subcat, String secName) {
        if (basePoint == null) {
            basePoint = new MediaPoint();
            basePoint.setLog_type(BehaviorType.media.toString());
        }
        if (basePoint instanceof MediaPoint) {
            MediaPoint m = (MediaPoint) basePoint;
            m.setTid(toyId);
            m.setEnter_mode(enterMode);
            m.setAction(action);
            m.setUrl(url);
            m.setCate(cate);
            m.setRes_name(resName);
            m.setSubcat(subcat);
            m.setSec_name(secName);
            m.setEnter_time(System.currentTimeMillis());
        }
    }

    public void addMediaEnterRecord(String toyId, String action, String url, String cate, String resName, String subcat, String secName, long total) {
        if (basePoint == null) {
            basePoint = new MediaPoint();
            basePoint.setLog_type(BehaviorType.media.toString());
        }
        if (basePoint instanceof MediaPoint) {
            MediaPoint m = (MediaPoint) basePoint;
            m.setTid(toyId);
            m.setAction(action);
            m.setUrl(url);
            m.setCate(cate);
            m.setRes_name(resName);
            m.setSubcat(subcat);
            m.setSec_name(secName);
            m.setTotal_time(total);
            m.setEnter_time(System.currentTimeMillis());
        }
    }

    public void updateMediaRecord(long total, String action) {
        if (basePoint != null) {
            MediaPoint m = (MediaPoint) basePoint;
            m.setTotal_time(total);
            m.setAction(action);
            m.setEnter_time(System.currentTimeMillis());
        }
    }

    public void updateEnterMode(String[] enter) {
        if (basePoint == null) {
            basePoint = new MediaPoint();
            basePoint.setLog_type(BehaviorType.media.toString());
        }

        basePoint.setEnter_mode(enter);

    }

    public void addMediaExitRecord(String[] exitMode, String action, long progress) {
        if (basePoint != null) {
            if (basePoint instanceof MediaPoint) {
                MediaPoint m = (MediaPoint) basePoint;
                m.setExit_mode(exitMode);
                m.setExit_time(System.currentTimeMillis());
                m.setExit_progress(progress);
                m.setAction(action);
                printPoint();
            }
        }
    }


    public void addCourseEnterRecord(String toyId, String[] enterMode, String action, String classId, String className, String unitName, String wrongtries, String score, String taskId) {
        if (basePoint == null) {
            basePoint = new CoursePoint();
            basePoint.setLog_type(BehaviorType.course.toString());
        }
        if (basePoint instanceof CoursePoint) {
            CoursePoint c = (CoursePoint) basePoint;
            c.setTid(toyId);
            c.setEnter_mode(enterMode);
            c.setAction(action);
            c.setEnter_time(System.currentTimeMillis());
            c.setClass_id(classId);
            c.setUnit_name(unitName);
            c.setWrongtries(wrongtries);
            c.setScore(score);
            c.setTask_id(taskId);
        }

    }

    public void addCourseExitRecord(String[] exitMode, String action) {
        if (basePoint != null) {
            if (basePoint instanceof CoursePoint) {
                CoursePoint c = (CoursePoint) basePoint;
                c.setExit_mode(exitMode);
                c.setEnter_time(System.currentTimeMillis());
                c.setAction(action);
                printPoint();
            }
        }
    }


    public void addVideoEnterRecord(String toyId, String[] enterMode, String action, String releation, String sender, String receiver, String type) {
        if (basePoint == null) {
            basePoint = new VideoPoint();
            basePoint.setLog_type(BehaviorType.videolog.toString());
        }
        if (basePoint instanceof VideoPoint) {
            VideoPoint v = (VideoPoint) basePoint;
            v.setTid(toyId);
            v.setEnter_mode(enterMode);
            v.setAction(action);
            v.setEnter_time(System.currentTimeMillis());
            v.setRelation(receiver);
            v.setSender(sender);
            v.setReceiver(receiver);
            v.setType(type);
        }
    }

    public void addVideoExitRecord(String[] exitMode, String action) {
        if (basePoint != null) {
            if (basePoint instanceof VideoPoint) {
                VideoPoint v = (VideoPoint) basePoint;
                v.setExit_mode(exitMode);
                v.setEnter_time(System.currentTimeMillis());
                v.setAction(action);
                printPoint();
            }
        }
    }

    private void clear() {
        if (basePoint instanceof CoursePoint) {
            basePoint = new CoursePoint();
            basePoint.setLog_type(BehaviorType.course.toString());
        } else if (basePoint instanceof MediaPoint) {
            basePoint = new MediaPoint();
            basePoint.setLog_type(BehaviorType.media.toString());
        } else if (basePoint instanceof VideoPoint) {
            basePoint = new VideoPoint();
            basePoint.setLog_type(BehaviorType.videolog.toString());
        }
    }

    public void printPoint() {
        Gson gson = new Gson();
        String json = "";
        json = gson.toJson(basePoint);
        clear();
        ItgNetSend.itg()
                .builder(ItgNetSend.POST)
                .url("http://tickmq.yuanqutech.com:9800/action?")
                .addContent(json, ItgNetSend.MEDIA_JSON)
                .send(new ItgCallback() {
                    @Override
                    public void onFailure(String er) {
                        Log.e("DetailPointControl", er);


                    }

                    @Override
                    public void onResponse(String result, int code) {
                        Log.e("DetailPointControl", "成功");
                    }
                });
        LogAgent.onEvent("action", json);
    }
}
