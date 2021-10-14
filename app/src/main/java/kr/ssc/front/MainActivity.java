package kr.ssc.front;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kr.ssc.front.data.api.ApiService;
import kr.ssc.front.data.api.model.Student;
import kr.ssc.front.database.Holder;
import kr.ssc.front.database.HolderDao;
import kr.ssc.front.database.holder_db;
import kr.ssc.front.ui.attendance.AttendanceModel;
import kr.ssc.front.ui.attendance.CheckAttendance;
import kr.ssc.front.ui.attendance.ListAttendanceFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private String idx;
    private OnBackPressedListener listener;
    private DrawerLayout drawer;
    private AlertDialog.Builder builder;
    HolderDBHelper helper;
    SQLiteDatabase db;
    Cursor cursor;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        helper = new HolderDBHelper(getApplicationContext());


        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.getMenu().findItem(R.id.nav_attendance_check).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CheckAttendance checkAttendance = new CheckAttendance();
                checkAttendance.check(MainActivity.this);
                return true;
            }
        });


        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_main, R.id.nav_attendance, R.id.nav_attendance_list, R.id.nav_attendance_check)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setOnBackPressedListener(OnBackPressedListener listener){
        this.listener = listener;
    }

    @Override
    public void onBackPressed() {
        if(this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawers();
        } else {
            if(listener!=null) {
                listener.onBackPressed();
            } else {
                super.onBackPressed();
            }
        }
    }

    public void closeMenu() {
        if(this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawers();
        }
    }

    // QRcode SCAN reulst
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            // 내용이 없는 경우
            if(result.getContents() == null) {
                Toast.makeText(this, "취소!", Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
            // 내용이 있는 경우
            else {
                try{
                    // data -> json
                    JSONObject obj = new JSONObject(result.getContents());
                    // qr코드 스캔 잘 된 경우
                    if(obj.has("idx")) {
                        idx = obj.getString("idx");
                        parsing();
                    }
                    // 다른 qr코드를 스캔한 경우
                    else {
                        Toast.makeText(this, "수업의 QR코드를 스캔해주세요.", Toast.LENGTH_SHORT)
                                .show();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    String temp = result.getContents();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 출석체크하기
    public void parsing() throws JSONException {
        try{
            // 휴대폰 내장에서 개인 고유번호가져오기
            db = helper.getReadableDatabase();

            cursor = db.rawQuery("select * from " + HolderDBHelper.getTableName(), null);
            cursor.moveToNext();

            String holder_id = cursor.getString(cursor.getColumnIndex("holder_id"));
            JSONObject json = new JSONObject();
            json.put("class_id", idx);
            json.put("holder_id", holder_id);
            String body = json.toString();

            cursor.close();
            helper.close();


//            // 블록체인에 출석현황을 저장합시다~~!
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.apiaddress))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            AttendanceModel attendanceModel = new AttendanceModel(body);

            Call<Integer> call = apiService.addAttendance(attendanceModel);
            final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            call.enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if(response.isSuccessful()) {
                        builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("출석 체크 완료")
                                .setCancelable(false)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        bundle = new Bundle();
                                        bundle.putString("idx", idx);
                                        navController.navigate(R.id.action_check_to_list, bundle);

                                    }
                                }).show();
                    }
                    else System.out.println("MainActivity - onResponse() called " + response.code());

                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    System.out.println("MainActivity - onFailure() called" + t.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

