package kr.ssc.front;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

public class JoinActivity extends AppCompatActivity {

    private OnBackPressedListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
    }

    public void setOnBackPressedListener(OnBackPressedListener listener){
        this.listener = listener;
    }

    // back 버튼 클릭 처리
    @Override
    public void onBackPressed(){
        if(listener!=null) listener.onBackPressed();
        else super.onBackPressed();
    }
}