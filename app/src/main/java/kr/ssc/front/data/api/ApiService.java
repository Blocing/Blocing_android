package kr.ssc.front.data.api;

import java.util.ArrayList;
import java.util.List;

import kr.ssc.front.data.api.model.Class;
import kr.ssc.front.data.api.model.Student;
import kr.ssc.front.ui.attendance.Attendance;
import kr.ssc.front.ui.attendance.AttendanceModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    
    @GET("/idcard/{holder_id}")
    Call<Student> getStudent(@Path("holder_id") String holder_id);

    @POST("/attendance")
    Call<Integer> addAttendance(@Body AttendanceModel attendanceModel);

    @PUT("/idcard/activate/{holder_id}")
    Call<Student> updateStudent(@Path("holder_id") String holder_id);

    // /attendance/classes
    @GET("/attendance/classes")
    Call<List<Class>> getClasses();

    @GET("/attendance/list/{holder_id}/{class_id}")
    Call<ArrayList<Attendance>> getAttendanceList(@Path("holder_id") String holder_id, @Path("class_id") String class_id);
}
