package kr.ssc.front.ui.attendance;

import com.google.gson.annotations.SerializedName;

public class AttendanceModel {
    @SerializedName("idx")
    private String idx;

    public AttendanceModel(String idx) {
        this.idx = idx;
    }

    public String getIdx() {
        return idx;
    }
}
