package com.yqtec.logagent.point;

public class BasePoint {
    private   String tid;
    private  String[] enter_mode;
    private  String[] exit_mode;
    private  long enter_time;
    private  long exit_time;
    private  String log_type;
    private  long spent_time;
    private String action;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String[] getEnter_mode() {
        return enter_mode;
    }

    public void setEnter_mode(String[] enter_mode) {
        this.enter_mode = enter_mode;
    }

    public String[] getExit_mode() {
        return exit_mode;
    }

    public void setExit_mode(String[] exit_mode) {
        this.exit_mode = exit_mode;
    }

    public long getEnter_time() {
        return enter_time;
    }

    public void setEnter_time(long enter_time) {
        this.enter_time = enter_time;
    }

    public long getExit_time() {
        return exit_time;
    }

    public void setExit_time(long exit_time) {
        this.exit_time = exit_time;
    }

    public String getLog_type() {
        return log_type;
    }

    public void setLog_type(String log_type) {
        this.log_type = log_type;
    }

    public long getSpent_time() {
        return spent_time;
    }

    public void setSpent_time(long spent_time) {
        this.spent_time = spent_time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
