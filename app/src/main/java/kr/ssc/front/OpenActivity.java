package kr.ssc.front;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;

public class OpenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @SuppressLint("HandlerLeak")
            public  void handleMessage(Message message){
                super.handleMessage(message);

                Intent intent = new Intent(OpenActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0, 3000);

    }
}