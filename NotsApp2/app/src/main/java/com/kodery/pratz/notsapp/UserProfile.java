package com.kodery.pratz.notsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserProfile extends AppCompatActivity {

    private TextView _inputStatusText, _department, _designation, _location, _status,_inputName, _locationMain, _inputDepartment, _inputDesignation, _inputLocation;
    private FloatingActionButton _floatButton;

    public static String ip=MainActivity.ip;

    private void showAddItemDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c).setTitle("Add status").setMessage("What's on your mind?").setView(taskEditText).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String task = String.valueOf(taskEditText.getText());
                _inputStatusText.setText(task);
            }
        })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String id;
        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        id = sharedPref.getString("userid","");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar3);
        //ActionBar actionbar=getSupportActionBar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String resultURL = ip+"/userdata/"+id;
        new UserProfile.GetData().execute(resultURL);

        _status=findViewById(R.id.textview_status);
        _inputName = findViewById(R.id._inputName);
        _inputStatusText = findViewById(R.id.textview_status_input);
        _department = findViewById(R.id.textview_department);
        _department = findViewById(R.id.textview_department_input);

        _designation = findViewById(R.id.textview_designation_input);
        _inputDesignation = findViewById(R.id.textview_designation);

        _locationMain = findViewById(R.id.input_location_main);
        _location = findViewById(R.id.textview_location_input);

        _floatButton = findViewById(R.id.floatingActionButton);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog(UserProfile.this);
            }
        });


    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
                Log.d("UserProfile",s);
                JSONObject obj=new JSONObject(s);
                String name=obj.getString("name");
                String des=obj.getString("designation");
                String dep=obj.getString("dname");
                //String status=obj.getString("");
                //String loc=obj.getString("");

                _status.setText("My status");
                _department.setText(dep);
                _designation.setText(des);
                _location.setText("Some location");
                _inputName.setText(name);

            }

            catch (Exception ex){
                Log.d("myapp", Log.getStackTraceString(ex));
            }

        }
    }



}
