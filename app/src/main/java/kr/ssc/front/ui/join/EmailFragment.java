//회원가입 가장 처음 메일 인증 fragment

package kr.ssc.front.ui.join;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import kr.ssc.front.JoinActivity;
import kr.ssc.front.OnBackPressedListener;
import kr.ssc.front.R;


public class EmailFragment extends Fragment implements OnBackPressedListener {

    private EditText etEmail;
    private EditText etCheck;
    private TextView tvCheck;
    private Button btnSend;
    private Button btnCheck;
    private Button btnNext;
    private View root;
    private AlertDialog.Builder builder;
    private JoinActivity activity;
    private boolean first = true;

    private String rand;

    private boolean check = false;
    private String body;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_join_email, container, false);

        activity = (JoinActivity)getActivity();

        etEmail = root.findViewById(R.id.edit_email);
        etCheck = root.findViewById(R.id.edit_email_check);
        tvCheck = root.findViewById(R.id.text_email_check);
        btnSend = root.findViewById(R.id.btn_email_send);// 보내기
        btnCheck = root.findViewById(R.id.btn_email_check);// 인증(확인)
        btnNext = root.findViewById(R.id.btn_join_email_next); // 다음

        //보내기
        btnSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stEmail = etEmail.getText().toString();

                if(stEmail.equals("")){
                    Toast.makeText(getContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    //이메일 전송
                    parsing();
                    Toast.makeText(getContext(), "이메일을 전송합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //인증
        btnCheck.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stCheck = etCheck.getText().toString();

                if(stCheck.equals("")){
                    Toast.makeText(getContext(), "인증번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    builder = new AlertDialog.Builder(getContext());

                    //인증 번호 비교
                    if(stCheck.equals(rand)){
                        check = true;
                        tvCheck.setText("인증 성공");
                        builder.setMessage("인증이 확인되었습니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                    } else{
                        Toast.makeText(getContext(), "인증번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //다음
        btnNext.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                //인증 여부 확인
                if(check){
                    Navigation.findNavController(root).navigate(R.id.action_nav_join_to_nav_join_password);
                }
                else {
                    Toast.makeText(getContext(), "이메일 인증을 먼저 해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(this);

        if(check){
            tvCheck.setText("인증 성공");
        }
    }


    @Override
    public void onBackPressed() {
        if(first){
            first = false;
            Toast.makeText(getActivity(), "한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
        else{
            activity.finishAffinity();//종료
            System.runFinalization();
            System.exit(0);
        }
    }

    //이메일 인증 전송
    public void parsing() {
        try {
            new RestAPITask().execute(getResources().getString(R.string.apiaddress)+getResources().getString(R.string.email_send));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class RestAPITask extends AsyncTask<String, Void, String> {
        //수행 전
        @Override
        protected void onPreExecute() {
            try {
                JSONObject json = new JSONObject();
                json.put("phoneNumber", etEmail.getText().toString());
                body = json.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        protected String doInBackground(String... Strings) {
            String result = null;

            try {
                result = downloadContents(Strings[0]);
            }
            catch (Exception e) {
                Log.e("REST_API", "POST method failed: " + e.getMessage());
                e.printStackTrace();
            }
            return result;
        }
        //작업 완료
        @Override
        protected void onPostExecute(String result) {
            parse(result);
            builder = new AlertDialog.Builder(getContext());

            builder.setMessage("인증 메일이 전송되었습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }
    }

    /* 주소(address)에 접속하여 문자열 데이터를 수신한 후 반환 */
    protected String downloadContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToString(stream);
            if (stream != null)
                stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

//    // URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        // 클라이언트 아이디 및 시크릿 그리고 요청 URL 선언
        conn.setRequestMethod("POST");
        conn.setRequestProperty("content-type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setDefaultUseCaches(false);
        conn.connect();
        writeStream(conn);

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_CREATED) {
            throw new IOException("HTTP error code: " + conn.getResponseCode());
        }
        return conn.getInputStream();
    }

    protected void writeStream(HttpURLConnection conn) {
        try {
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(body); //json 형식의 메세지 전달
            wr.flush();
            wr.close();
            System.out.println("body"+body);
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }
//    /* InputStream을 전달받아 문자열로 변환 후 반환 */
    protected String readStreamToString(InputStream stream){
        StringBuilder result = new StringBuilder();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String readLine = bufferedReader.readLine();

            while (readLine != null) {
                result.append(readLine + "\n");
                readLine = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
//    //json parsing
    public void parse(String json){
        try{
            JSONObject object = new JSONObject(json);
            //인증번호
            rand = object.getString("authCode");
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}