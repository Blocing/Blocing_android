package kr.ssc.front.ui.attendance;


import com.google.gson.annotations.SerializedName;

public class Attendance {
    @SerializedName("id")
    private String class_id;
    @SerializedName("time")
    private String attendance_time;
    @SerializedName("status")
    private String attendance_state;

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getAttendance_time() {
        return attendance_time;
    }

    public void setAttendance_time(String attendance_time) {
        this.attendance_time = attendance_time;
    }

    public String getAttendance_state() {
        return attendance_state;
    }

    public void setAttendance_state(String attendance_state) {
        this.attendance_state = attendance_state;
    }
}
