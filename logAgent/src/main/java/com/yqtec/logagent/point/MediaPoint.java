package com.yqtec.logagent.point;

public class MediaPoint extends BasePoint{
    private String url;
    private String res_name;
    private String cate;
    private String subcat;
    private long total_time;
    private long exit_progress;
    private String sec_name;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRes_name() {
        return res_name;
    }

    public void setRes_name(String res_name) {
        this.res_name = res_name;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getSubcat() {
        return subcat;
    }

    public void setSubcat(String subcat) {
        this.subcat = subcat;
    }

    public long getTotal_time() {
        return total_time;
    }

    public void setTotal_time(long total_time) {
        this.total_time = total_time;
    }

    public long getExit_progress() {
        return exit_progress;
    }

    public void setExit_progress(long exit_progress) {
        this.exit_progress = exit_progress;
    }

    public String getSec_name() {
        return sec_name;
    }

    public void setSec_name(String sec_name) {
        this.sec_name = sec_name;
    }
}
