package kr.ssc.front.ui.join;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.annotation.RequiresApi;
import androidx.navigation.Navigation;

import com.beautycoder.pflockscreen.PFFLockScreenConfiguration;
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment;
import com.beautycoder.pflockscreen.security.PFResult;
import com.beautycoder.pflockscreen.security.PFSecurityManager;
import com.beautycoder.pflockscreen.security.callbacks.PFPinCodeHelperCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import kr.ssc.front.HolderDBHelper;
import kr.ssc.front.JoinActivity;
import kr.ssc.front.OnBackPressedListener;
import kr.ssc.front.PreferencesSettings;
import kr.ssc.front.R;
import kr.ssc.front.ui.join.Student;

public class PasswordFragment extends Fragment implements OnBackPressedListener {
    private View root;

    private PFLockScreenFragment fragment;
    private PFFLockScreenConfiguration.Builder builder;

    private JoinActivity activity;

    private Student student;
    private String body;
    private Integer id;
    private String did, name, studentId, university, department;
    private HolderDBHelper helper;
    private SQLiteDatabase db;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_password, container, false);

        activity = (JoinActivity) getActivity();

        //?????? ?????? ????????????
        student = (Student) getArguments().get("student");

        helper = new HolderDBHelper(getContext());

        fragment = new PFLockScreenFragment();
        builder = new PFFLockScreenConfiguration.Builder(getContext())
                .setMode(PFFLockScreenConfiguration.MODE_CREATE)
                .setTitle("???????????? 6????????? ?????????????????????")
                .setNewCodeValidation(true)
                .setNewCodeValidationTitle("??????????????? ?????? ?????????????????????")
                .setUseFingerprint(false)
                .setCodeLength(6);

        fragment.setConfiguration(builder.build());
        fragment.setCodeCreateListener(new PFLockScreenFragment.OnPFLockScreenCodeCreateListener() {
            @Override
            public void onCodeCreated(String encodedCode) {
                //TODO: save somewhere;
                PreferencesSettings.saveToPref(getActivity(), encodedCode);
                //?????????????????? ?????? ?????? ??? ?????? ???
                parsing();
            }

            @Override
            public void onNewCodeValidationFailed() {
                Toast.makeText(getContext(), "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
            }
        });

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.pincode_container, fragment).commit();
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(null);
    }

    @Override
    public void onBackPressed() {

    }

    //?????? ?????? + ????????? ??????
    public void parsing() {
        try {
            new RestAPITask().execute(getResources().getString(R.string.apiaddress)+getResources().getString(R.string.signup));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class RestAPITask extends AsyncTask<String, Void, String> {

        //?????? ???
        @Override
        protected void onPreExecute() {
            try {
                JSONObject json = new JSONObject();

                json.put("name", student.getName());
                json.put("studentId", student.getStudent_id());
                json.put("university", student.getUniversity());
                json.put("department", student.getDepartment());

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
                // Error calling the rest api
                Log.e("REST_API", "POST method failed: " + e.getMessage());
                e.printStackTrace();
            }
            return result;
        }

        //?????? ??????
        @Override
        protected void onPostExecute(String result) {
            int holderId = parse(result);
            if(holderId == -1) {
                Toast.makeText(getContext(), "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                PreferencesSettings.deletePref(getActivity());
                PFSecurityManager.getInstance().getPinCodeHelper().delete(
                        new PFPinCodeHelperCallback<Boolean>() {
                            @Override
                            public void onResult(PFResult<Boolean> result) {
                                Navigation.findNavController(root).navigate(R.id.action_password_to_nav_join_name);
                            }
                        }
                );
            }
            else {

                //???????????? ?????? db??? holder id ??????
                // did,name,university,department
                db = helper.getWritableDatabase();
                ContentValues row = new ContentValues();
                row.put(HolderDBHelper.getColHolderId(), holderId);
                row.put(HolderDBHelper.getColHolderDid(), did);
                row.put(HolderDBHelper.getColHolderName(), name);
                row.put(HolderDBHelper.getColHolderUniversity(), university);
                row.put(HolderDBHelper.getColHolderDepartment(), department);
                db.insert(HolderDBHelper.getTableName(), null, row);
                helper.close();

                Navigation.findNavController(root).navigate(R.id.action_nav_join_password_to_nav_join_success);
            }
        }
    }

    /* ??????(address)??? ???????????? ????????? ???????????? ????????? ??? ?????? */
    protected String downloadContents(String address) {
        HttpURLConnection conn = null;
        InputStream stream = null;
        String result = null;

        try {
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection();
            stream = getNetworkConnection(conn);
            result = readStreamToString(stream);
            if (stream != null) stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return result;
    }

    // URLConnection ??? ???????????? ???????????? ?????? ??? ??????, ?????? ??? ????????? InputStream ??????
    private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
        // ??????????????? ????????? ??? ????????? ????????? ?????? URL ??????
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("content-type", "application/json");

        writeStream(conn);

        if (conn.getResponseCode() != HttpsURLConnection.HTTP_CREATED) {
            throw new IOException("HTTP error code: " + conn.getResponseCode());
        }
        return conn.getInputStream();
    }

    protected void writeStream(HttpURLConnection conn) {

        try {
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(body); //json ????????? ????????? ??????
            wr.flush();
            wr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* InputStream??? ???????????? ???????????? ?????? ??? ?????? */
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
    //json parsing
    public Integer parse(String json){
        try{
            JSONObject object = new JSONObject(json);
            String success = object.getString("success");
            if(success.equals("failed")) id = -1;
            else {
                id = object.getInt("HolderId");
                did = object.getString("CardDid");
                name = object.getString("name");
                studentId = object.getString("studentId");
                university = object.getString("university");
                department = object.getString("department");
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return id;
    }

}