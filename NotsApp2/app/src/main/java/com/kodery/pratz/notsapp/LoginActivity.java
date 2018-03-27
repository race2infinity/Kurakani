package com.kodery.pratz.notsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextWatcher;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;
    private EditText _inputPasswordText, _inputUseridText;
    private TextInputLayout _inputLayoutPasswordText, _inputLayoutUseridText;
    private Button _btnLogin;
    private TextView _linkSignup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(sharedPref.contains("abcd")) {
            if(sharedPref.getBoolean("abcd",true)) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        }
        else
        {
            editor.putBoolean("abcd",false);
            editor.commit();
            boolean check = sharedPref.getBoolean("abcd",false);
        }
        _inputLayoutUseridText = findViewById(R.id.input_layout_login_userid);
        _inputLayoutPasswordText = findViewById(R.id.input_layout_login_password);

        _inputUseridText = findViewById(R.id.input_login_userid);
        _inputPasswordText = findViewById(R.id.input_login_password);

        _btnLogin = findViewById(R.id.btn_login);
        _linkSignup = findViewById(R.id.link_signup);

        _inputUseridText.addTextChangedListener(new LoginActivity.MyTextWatcher(_inputUseridText));
        _inputPasswordText.addTextChangedListener(new LoginActivity.MyTextWatcher(_inputPasswordText));

        _btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                login();

            }
        });

        _linkSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });


    }

    public void login() {

        _btnLogin.setEnabled(false);

        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userid",_inputUseridText.getText().toString());

        if (!validate()) {
            onLoginFailed();
            return;
        }

        editor.putBoolean("abcd", true);
        editor.commit();
        boolean check = sharedPref.getBoolean("abcd",false);
        //Log.d("hello", String.valueOf(check));

        String resultUrl = "http://192.168.0.8:3020/login/app";
        new PostData().execute(resultUrl);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP);
        // TODO: Implement authentication logic here.

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _btnLogin.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _btnLogin.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        /*String login_userid = _inputUseridText.getText().toString();
        String login_password = _inputPasswordText.getText().toString();*/

        if (_inputUseridText.getText().toString().trim().isEmpty()) {
            _inputLayoutUseridText.setError("Invalid User ID.");
            //requestFocus(_inputUseridText);
            valid = false;
        } else {
            _inputLayoutUseridText.setError(null);
        }

        if (_inputPasswordText.getText().toString().trim().isEmpty()) {
            _inputLayoutPasswordText.setError("Invalid password.");
            //requestFocus(_inputPasswordText);
            valid = false;
        } else {
            _inputLayoutPasswordText.setError(null);
        }

        return valid;
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
            datatosend.put("id", _inputUseridText.getText().toString());
            datatosend.put("password", _inputPasswordText.getText().toString());

            StringBuilder result = new StringBuilder();

            URL url = new URL(urlpath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.connect();

            OutputStream outputStream=urlConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter((outputStream)));
            bufferedWriter.write(datatosend.toString());
            bufferedWriter.flush();

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            line = bufferedReader.readLine();
            result.append(line);
            Log.d("kyle",result.toString());
            return result.toString();
        }
    }
}