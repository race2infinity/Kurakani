package com.kodery.pratz.notsapp;

/**
 * Created by kyle on 18/3/18.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SignupActivity extends AppCompatActivity {
    //private static final String TAG = "SignupActivity";

    private EditText _inputName, _inputUserid, _inputAadharid, _inputMobile, _inputEmail, _inputLocation, _inputDesignation, _inputPassword, _inputRetypePassword;
    private TextInputLayout _inputLayoutName, _inputLayoutUserid, _inputLayoutAadharid, _inputLayoutMobile, _inputLayoutEmail, _inputLayoutLocation, _inputLayoutDesignation, _inputLayoutPassword, _inputLayoutRetypePassword;
    private Button _btnSignUp;
    private TextView _linkLogin;
    public static String ip=Sessions.ip;
    public static ArrayList<String> arraySpinner = new ArrayList<String>();
    public static ArrayList<String> deplist = new ArrayList<String>();
    public int flag=0;
    Spinner si;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(sharedPref.contains("abcd")) {
            if(sharedPref.getBoolean("abcd",true)) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        else
        {
            editor.putBoolean("abcd",false);

            editor.commit();
            //boolean check = sharedPref.getBoolean("abcd",false);
        }

        _inputLayoutName = findViewById(R.id.input_layout_signup_name);
        _inputLayoutUserid = findViewById(R.id.input_layout_signup_userid);
        _inputLayoutAadharid = findViewById(R.id.input_layout_signup_aadharid);
        _inputLayoutMobile = findViewById(R.id.input_layout_signup_mobile);
        _inputLayoutEmail = findViewById(R.id.input_layout_signup_email);
        _inputLayoutLocation = findViewById(R.id.input_layout_signup_location);
        _inputLayoutDesignation = findViewById(R.id.input_layout_signup_designation);
        _inputLayoutPassword = findViewById(R.id.input_layout_signup_password);
        _inputLayoutRetypePassword = findViewById(R.id.input_layout_signup_retypepassword);

        _inputName = findViewById(R.id.input_signup_name);
        _inputUserid = findViewById(R.id.input_signup_userid);
        _inputAadharid = findViewById(R.id.input_signup_aadharid);
        _inputMobile = findViewById(R.id.input_signup_mobile);
        _inputEmail = findViewById(R.id.input_signup_email);
        _inputLocation = findViewById(R.id.input_signup_location);
        _inputDesignation = findViewById(R.id.input_signup_designation);
        _inputPassword = findViewById(R.id.input_signup_password);
        _inputRetypePassword = findViewById(R.id.input_signup_retypepassword);

        _btnSignUp = findViewById(R.id.btn_signup);

        _linkLogin = findViewById(R.id.link_login);

        _inputName.addTextChangedListener(new MyTextWatcher(_inputName));
        _inputUserid.addTextChangedListener(new MyTextWatcher(_inputUserid));
        _inputAadharid.addTextChangedListener(new MyTextWatcher(_inputAadharid));
        _inputMobile.addTextChangedListener(new MyTextWatcher(_inputMobile));
        _inputEmail.addTextChangedListener(new MyTextWatcher(_inputEmail));
        _inputLocation.addTextChangedListener(new MyTextWatcher(_inputLocation));
        _inputDesignation.addTextChangedListener(new MyTextWatcher(_inputDesignation));
        _inputPassword.addTextChangedListener(new MyTextWatcher(_inputPassword));
        _inputRetypePassword.addTextChangedListener(new MyTextWatcher(_inputRetypePassword));

        String resultURL = ip+"/depdata/";
        new SignupActivity.GetData().execute(resultURL);

        Log.d("kyle1","hey");
        for(int i=0;i<arraySpinner.size();i++){
            Log.d("kyle1",arraySpinner.get(i));
        }

        si = findViewById(R.id.department_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arraySpinner);
        si.setAdapter(adapter);
        //sets(s);
        Toast.makeText(this, Integer.toString(si.getSelectedItemPosition()), Toast.LENGTH_LONG).show();
       // Toast.makeText(this, si.getSelectedItem().toString(), Toast.LENGTH_LONG).show();


        /*
        s = (Spinner) findViewById(R.id.department_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
*/
        //Log.d("hey",Integer.toString(s.getSelectedItemPosition()));

        _btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
                if(flag==0)
                    finish();
            }
        });

        _linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        //Log.d(TAG, "Signup");

        //s = findViewById(R.id.department_spinner);
        Toast.makeText(this, Integer.toString(si.getSelectedItemPosition()), Toast.LENGTH_LONG).show();

        if (!validate()) {
            flag=1;
            onSignupFailed();
            return;
        }
        flag=0;

        //get location for the selected department
        //String depid=s.getSelectedItem().toString();

        //Log.d("hi",Integer.toString(s.getSelectedItemPosition()));

//Post for the registration
       String resultUrl = "http://192.168.0.8:3020/userdata";
        new SignupActivity.PostData().execute(resultUrl);
        new SignupActivity.PostData().execute(resultUrl);


        // TODO: Implement your own signup logic here.

        onSignupSuccess();
    }


    public void onSignupSuccess() {
        setResult(RESULT_OK, null);
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userid",_inputUserid.getText().toString());
        editor.putBoolean("abcd",true);
        editor.commit();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle b = new Bundle();
        b.putString("id", _inputUserid.getText().toString()); //Your id
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();

        _btnSignUp.setEnabled(true);
    }

    public boolean validate() {

        boolean valid = true;

        String name = _inputName.getText().toString();
        String email = _inputEmail.getText().toString();
        String mobile = _inputMobile.getText().toString();
        String userid = _inputUserid.getText().toString();
        String aadharid = _inputAadharid.getText().toString();
        String location = _inputLocation.getText().toString();
        String designation = _inputDesignation.getText().toString();
        String password = _inputPassword.getText().toString();
        String retypepassword = _inputRetypePassword.getText().toString();

        if (name.trim().isEmpty()) {
            _inputLayoutName.setError("Enter your name.");
            valid = false;
        } else {
            _inputLayoutName.setError(null);
        }

        if (password.trim().isEmpty()) {
            _inputLayoutPassword.setError("Enter a valid password");
            valid = false;
        } else {
            _inputLayoutPassword.setError(null);
        }
        if (retypepassword.trim().isEmpty()) {
            _inputLayoutRetypePassword.setError("Passwords don't match");
            valid = false;
        } else {
            _inputLayoutRetypePassword.setError(null);
        }

        if (email.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _inputLayoutEmail.setError("Enter a valid email address.");
            valid = false;
        } else {
            _inputLayoutEmail.setError(null);
        }

        if (mobile.trim().isEmpty() || mobile.length() != 10) {
            _inputLayoutMobile.setError("Enter a valid phone number.");
            valid = false;
        } else {
            _inputLayoutMobile.setError(null);
        }

        if (userid.trim().isEmpty()) {
            _inputLayoutUserid.setError("Enter a valid User Id");
            valid = false;
        } else {
            _inputLayoutUserid.setError(null);
        }

        if (aadharid.trim().isEmpty()) {
            _inputLayoutAadharid.setError("Enter a valid Aadhar Id.");
            valid = false;
        } else {
            _inputLayoutAadharid.setError(null);
        }

        if (location.trim().isEmpty()) {
            _inputLayoutLocation.setError("Enter a valid location");
            valid = false;
        } else {
            _inputLayoutLocation.setError(null);
        }

        if (designation.trim().isEmpty()) {
            _inputLayoutDesignation.setError("Enter a valid designation");
            valid = false;
        } else {
            _inputLayoutDesignation.setError(null);
        }

        return valid;
    }

    public class PostData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                return postData(params[0]);
            }catch (IOException e) {
                return "Network Error";
            }catch (JSONException e) {
                return "Data Invalid";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }


        public String postData(String urlpath) throws IOException,JSONException {
            JSONObject datatosend = new JSONObject();
            Log.d("lols",deplist.get(si.getSelectedItemPosition()));
            datatosend.put("name", _inputName.getText().toString());
            datatosend.put("emailid", _inputEmail.getText().toString());
            datatosend.put("mobile_no", _inputMobile.getText().toString());
            datatosend.put("empid", _inputUserid.getText().toString());
            datatosend.put("aadhar", _inputAadharid.getText().toString());
            datatosend.put("designation", _inputDesignation.getText().toString());
            datatosend.put("password1",_inputPassword.getText().toString());
            datatosend.put("password2",_inputRetypePassword.getText().toString());
            datatosend.put("department",deplist.get(si.getSelectedItemPosition()));
            Log.d("shadrak",deplist.get(si.getSelectedItemPosition()));
            StringBuilder result=new StringBuilder();

            Log.d("lols",datatosend.toString());
            URL url = new URL(urlpath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.connect();

            OutputStream outputStream=urlConnection.getOutputStream();
            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter((outputStream)));
            bufferedWriter.write(datatosend.toString());
            bufferedWriter.flush();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(inputStream));
            String line;
            line = bufferedReader.readLine();
            result.append(line);
            Log.d("kyle",result.toString());
            return result.toString();
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
        }
    }

    class GetData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder rs = new StringBuilder();

            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                InputStream input = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                String Line;

                Line = br.readLine();
                rs.append(Line);

            }
            catch(IOException ex) {
                return ex.toString();
            }
            return rs.toString();


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                deplist.clear();
                arraySpinner.clear();
                Log.d("jp","hey");
                JSONArray jarr = new JSONArray(s);
                JSONObject main;
                for(int i=0; i<jarr.length(); i++) {
                   main=jarr.getJSONObject(i);
                   String temp=main.getString("name");
                   String temp1=main.getString("location");
                   String temp2=main.getString("id");
                   deplist.add(temp2);
                   arraySpinner.add(temp+" ("+temp1+")");
                    //deplist
                }

                for(int i=0;i<arraySpinner.size();i++)
                {
                    Log.d("bot",arraySpinner.get(i));
                }
                //stringbox.setText(name+"    "+message);
            }
            catch (Exception ex){
                Log.d("GG END MID", ex.toString());
            }

        }
    }


}