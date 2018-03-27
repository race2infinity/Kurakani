package com.kodery.pratz.notsapp;

/**
 * Created by kyle on 18/3/18.
 */
import android.app.ProgressDialog;
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

public class SignupActivity extends AppCompatActivity {
    //private static final String TAG = "SignupActivity";

    private EditText _inputName, _inputUserid, _inputAadharid, _inputMobile, _inputEmail, _inputLocation, _inputDesignation;
    private TextInputLayout _inputLayoutName, _inputLayoutUserid, _inputLayoutAadharid, _inputLayoutMobile, _inputLayoutEmail, _inputLayoutLocation, _inputLayoutDesignation;
    private Button _btnSignUp;
    private TextView _linkLogin;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        _inputLayoutName = findViewById(R.id.input_layout_signup_name);
        _inputLayoutUserid = findViewById(R.id.input_layout_signup_userid);
        _inputLayoutAadharid = findViewById(R.id.input_layout_signup_aadharid);
        _inputLayoutMobile = findViewById(R.id.input_layout_signup_mobile);
        _inputLayoutEmail = findViewById(R.id.input_layout_signup_email);
        _inputLayoutLocation = findViewById(R.id.input_layout_signup_location);
        _inputLayoutDesignation = findViewById(R.id.input_layout_signup_designation);

        _inputName = findViewById(R.id.input_signup_name);
        _inputUserid = findViewById(R.id.input_signup_userid);
        _inputAadharid = findViewById(R.id.input_signup_aadharid);
        _inputMobile = findViewById(R.id.input_signup_mobile);
        _inputEmail = findViewById(R.id.input_signup_email);
        _inputLocation = findViewById(R.id.input_signup_location);
        _inputDesignation = findViewById(R.id.input_signup_designation);

        _btnSignUp = findViewById(R.id.btn_signup);

        _linkLogin = findViewById(R.id.link_login);

        _inputName.addTextChangedListener(new MyTextWatcher(_inputName));
        _inputUserid.addTextChangedListener(new MyTextWatcher(_inputUserid));
        _inputAadharid.addTextChangedListener(new MyTextWatcher(_inputAadharid));
        _inputMobile.addTextChangedListener(new MyTextWatcher(_inputMobile));
        _inputEmail.addTextChangedListener(new MyTextWatcher(_inputEmail));
        _inputLocation.addTextChangedListener(new MyTextWatcher(_inputLocation));
        _inputDesignation.addTextChangedListener(new MyTextWatcher(_inputDesignation));

        Spinner spinner = findViewById(R.id.department_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.department_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        _btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
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

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _btnSignUp.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account..");
        progressDialog.show();

        String resultUrl = "http://192.168.0.8:3020/userdata";
        new SignupActivity.PostData().execute(resultUrl);

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _btnSignUp.setEnabled(true);
        setResult(RESULT_OK, null);
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

        if (name.trim().isEmpty()) {
            _inputLayoutName.setError("Enter your name.");
            valid = false;
        } else {
            _inputLayoutName.setError(null);
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

            datatosend.put("name", _inputName.getText().toString());
            datatosend.put("email", _inputEmail.getText().toString());
            datatosend.put("mobile", _inputMobile.getText().toString());
            datatosend.put("userid", _inputUserid.getText().toString());
            datatosend.put("aadharid", _inputAadharid.getText().toString());
            datatosend.put("location", _inputLocation.getText().toString());
            datatosend.put("designation", _inputDesignation.getText().toString());

            StringBuilder result=new StringBuilder();

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

}