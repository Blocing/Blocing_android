package kr.ssc.front.data.api.model;

import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("name")
    private String name;
    @SerializedName("studentId")
    private String student_id;
    @SerializedName("university")
    private String university;
    @SerializedName("department")
    private String department;
    @SerializedName("expireDate")
    private String expireDate;
    @SerializedName("holder_id")
    private String holder_id;
    @SerializedName("status")
    private String status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getHolder_id() {
        return holder_id;
    }

    public void setHolder_id(String holder_id) {
        this.holder_id = holder_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", student_id='" + student_id + '\'' +
                ", university='" + university + '\'' +
                ", department='" + department + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", holder_id='" + holder_id + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
