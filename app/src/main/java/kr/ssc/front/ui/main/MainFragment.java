package kr.ssc.front.ui.main;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import kr.ssc.front.HolderDBHelper;
import kr.ssc.front.MainActivity;
import kr.ssc.front.R;
import kr.ssc.front.data.api.ApiService;
//import kr.ssc.front.ui.join.Student;
import kr.ssc.front.data.api.model.Student;
import kr.ssc.front.databinding.FragmentMainBinding;
import kr.ssc.front.ui.attendance.CheckAttendance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment extends Fragment {

    private static final String TAG = "로그";
//    private View root;
    private MainActivity activity;
    private View layoutStudentCard;
    private View layoutCamera;
    private View layoutInfo;
    Dialog dialog;
    String idx;

    HolderDBHelper helper;
    SQLiteDatabase db;
    Cursor cursor;
    private String type;
    String holder_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        root = inflater.inflate(R.layout.fragment_main, container, false);
        FragmentMainBinding root = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        activity = (MainActivity) getActivity();
        dialog = new Dialog(activity);
        helper = new HolderDBHelper(getContext());
//        layoutStudentCard = root.findViewById(R.id.layoutStudentCard);
        root.layoutStudentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "학생증을 눌렀습니다.", Snackbar.LENGTH_SHORT).show();
//                Navigation.findNavController(root).navigate(R.id.action_nav_main_to_nav_student);
                openStudentCard();
            }
        });

//        layoutCamera = root.findViewById(R.id.layoutCamera);
        root.layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "출석체크를 눌렀습니다.", Snackbar.LENGTH_SHORT).show();
                CheckAttendance checkAttendance = new CheckAttendance();
                checkAttendance.check(activity);
                Log.d(TAG, "scan FINISH!!!");
            }
        });

//        layoutInfo = root.findViewById(R.id.layoutInfo);
        root.layoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "개발자정보를 눌렀습니다.", Snackbar.LENGTH_SHORT).show();
            }
        });

        return root.getRoot();
    }

    private void openStudentCard() {
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvUniversity = dialog.findViewById(R.id.text_main_university);
        TextView tvName = dialog.findViewById(R.id.text_main_name);
        TextView tvDepartment = dialog.findViewById(R.id.text_main_department);
        TextView tvNum = dialog.findViewById(R.id.text_main_num);
        TextView tvDate = dialog.findViewById(R.id.text_main_date);
        Button reissue = dialog.findViewById(R.id.btn_main_reissue);
        Button back_btn = dialog.findViewById(R.id.back_btn);


        Log.d(TAG, "TEST 1");
        parsingIssue();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Snackbar.make(v, "뒤로가기 버튼을 눌렀습니다.", Snackbar.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void parsingIssue() {
        final kr.ssc.front.data.api.model.Student st = new kr.ssc.front.data.api.model.Student();
        try {
            // 휴대폰 내장에 저장된 holder_id 가져오기
            Log.d(TAG, HolderDBHelper.getTableName());
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("select * from " + HolderDBHelper.getTableName(), null);
            cursor.moveToNext();
            holder_id = cursor.getString(cursor.getColumnIndex("holder_id"));

            cursor.close();
            helper.close();
            Log.d(TAG, holder_id);
            type = "issue";
//             http:localhost:6464/idcard/{holderId}
//            new RestAPITask().execute(getResources().getString(R.string.apiaddress)+getResources().getString(R.string.idcard)+holder_id);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.apiaddress))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiService = retrofit.create(ApiService.class);
            Call<kr.ssc.front.data.api.model.Student> call = apiService.getStudent(holder_id);

            call.enqueue(new Callback<kr.ssc.front.data.api.model.Student>() {
                @Override
                public void onResponse(Call<kr.ssc.front.data.api.model.Student> call, Response<kr.ssc.front.data.api.model.Student> response) {
                    if (response.isSuccessful()) {
                        kr.ssc.front.data.api.model.Student student = response.body();

                        TextView tvUniversity = dialog.findViewById(R.id.text_main_university);
                        TextView tvName = dialog.findViewById(R.id.text_main_name);
                        TextView tvDepartment = dialog.findViewById(R.id.text_main_department);
                        TextView tvNum = dialog.findViewById(R.id.text_main_num);
                        TextView tvDate = dialog.findViewById(R.id.text_main_date);
                        Button reissue = dialog.findViewById(R.id.btn_main_reissue);
                        Button back_btn = dialog.findViewById(R.id.back_btn);

                        tvUniversity.setText(student.getUniversity());
                        tvName.setText(student.getName());
                        tvDepartment.setText(student.getDepartment());
                        tvNum.setText(student.getStudent_id());
                        tvDate.setText(student.getExpireDate());

                        if (student.getStatus() != "ACTIVATE") {
                            reissue.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d(TAG, "MainFragment - onResponse() called ");
                    }
                }

                @Override
                public void onFailure(Call<kr.ssc.front.data.api.model.Student> call, Throwable t) {
                    Log.d(TAG, "MainFragment - onFailure() called " + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parsingReissue() {
        try {
            type = "reissue";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}