package kr.ssc.front.ui.attendance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.ssc.front.HolderDBHelper;
import kr.ssc.front.MainActivity;
import kr.ssc.front.OnBackPressedListener;
import kr.ssc.front.R;
import kr.ssc.front.data.api.ApiService;
import kr.ssc.front.data.api.model.Class;
import kr.ssc.front.databinding.FragmentListAttendanceBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ListAttendanceFragment extends Fragment implements OnBackPressedListener {

    private View root;
    private MainActivity activity;
    private ArrayList<String> subjectList;
    private ArrayList<Attendance> attendances;
    private Spinner spinner;

    private AttendanceAdapter attendanceAdapter;
    private ArrayAdapter<String> subjectAdapter;
    private ListView lvAttendance = null;

    private HolderDBHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private String holder_id;
    private String type;
    private int idx = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_list_attendance, container, false);
        activity = (MainActivity) getActivity();

        subjectList = new ArrayList<>();
        subjectList.add("수업을 선택해주세요.");

        attendances = new ArrayList<>();

        helper = new HolderDBHelper(getContext());

        // 메뉴 열려있으면 닫기
        activity.closeMenu();

        spinner = (Spinner)root.findViewById(R.id.spinner_subject);
        subjectAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, subjectList);
        spinner.setAdapter(subjectAdapter);

        type = "class";
        parsing();

        lvAttendance = root.findViewById(R.id.list_attendance);
        attendanceAdapter = new AttendanceAdapter(getActivity(), R.layout.listview_attendance, null);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                attendances.clear();

                idx = position;
                if (idx != 0) {
                    // 출석 이력 가져오기
                    type = "attendance";
                    parsing();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(this);
        MainActivity mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onBackPressed() {
        Navigation.findNavController(root).navigate(R.id.action_go_home);
    }

    // 수업 받아오기
    public void parsing() {
        try {
            if (type == "class") {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getResources().getString(R.string.apiaddress))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);
                Call<List<Class>> call = apiService.getClasses();
                call.enqueue(new Callback<List<Class>>() {
                    @Override
                    public void onResponse(Call<List<Class>> call, Response<List<Class>> response) {
                        if (response.isSuccessful()) {
                            List<Class> source = response.body();
                            for (Class src : source) subjectList.add(src.getName());
                            subjectAdapter.notifyDataSetChanged();

                            // QR코드 스캔으로 이력 조회 넘어온 경우
                            if(getArguments() != null) {
                                idx = Integer.parseInt(getArguments().getString("idx"));
                                if(idx != 0) spinner.setSelection(idx);
                            }
                        }
                        else System.out.println("ListAttendanceFragment - onResponse() called " + response.code());
                    }

                    @Override
                    public void onFailure(Call<List<Class>> call, Throwable t) {
                        System.out.println("ListAttendanceFragment - onFailure() called : " + t.getMessage());
                    }
                });
            }
            else if(type == "attendance") {
                db = helper.getReadableDatabase();

                cursor = db.rawQuery("select * from " + HolderDBHelper.getTableName(), null);
                cursor.moveToNext();

                String holder_id = cursor.getString(cursor.getColumnIndex("holder_id"));
                cursor.close();
                helper.close();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getResources().getString(R.string.apiaddress))
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);

                Call<ArrayList<Attendance>> call2 = apiService.getAttendanceList(holder_id, String.valueOf(idx));
                call2.enqueue(new Callback<ArrayList<Attendance>>() {
                    @Override
                    public void onResponse(Call<ArrayList<Attendance>> call, Response<ArrayList<Attendance>> response) {
                        if (response.isSuccessful()) {
                            ArrayList<Attendance> source = response.body();
                            attendanceAdapter.setList(source);
                            lvAttendance.setAdapter(attendanceAdapter);
                        }
                        else System.out.println("ListAttendanceFragment - onResponse() called " + response.code());
                    }

                    @Override
                    public void onFailure(Call<ArrayList<Attendance>> call, Throwable t) {
                        System.out.println("ListAttendanceFragment - onFailure() called : " + t.getMessage());
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}