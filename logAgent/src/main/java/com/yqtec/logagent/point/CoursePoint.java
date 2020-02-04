package com.yqtec.logagent.point;

public class CoursePoint extends BasePoint{
    private String class_id;
    private String class_name;
    private String unit_name;
    private String wrongtries;
    private String score;
    private String task_id;

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getWrongtries() {
        return wrongtries;
    }

    public void setWrongtries(String wrongtries) {
        this.wrongtries = wrongtries;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }
}
