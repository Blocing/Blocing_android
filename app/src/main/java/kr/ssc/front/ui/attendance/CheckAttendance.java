package kr.ssc.front.ui.attendance;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;

import kr.ssc.front.ui.main.MainFragment;

public class CheckAttendance extends Fragment {
    private IntentIntegrator qrScan;

    public void check(Activity activity) {
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "camera permission granted", Toast.LENGTH_SHORT).show();
        }
        else {
            requestCameraPermission(activity);
        }

        qrScan = new IntentIntegrator(activity);
        qrScan.setPrompt("QR코드 인식을 하세요");
        qrScan.setOrientationLocked(false);
        qrScan.setBeepEnabled(false);
        qrScan.initiateScan();
    }

    // 카메라 권한
    private void requestCameraPermission(Activity activity) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 100);
        }
        else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 100);
        }
    }
}
