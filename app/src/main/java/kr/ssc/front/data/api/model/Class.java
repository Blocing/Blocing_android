package kr.ssc.front.data.api.model;

import com.google.gson.annotations.SerializedName;

public class Class {
    @SerializedName("id")
    private Integer id;
    @SerializedName("name")
    private String Name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
