package com.kodery.pratz.notsapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;

public class SessionUsers extends AppCompatActivity {
    public static ArrayList<User> userlist =new ArrayList<User>();
    public static String ip=Sessions.ip;
    public String id,name;
    RecyclerView mSessionRecycler;
    SessionUser mSessionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_users2);


        Intent myintent=getIntent();
        id=myintent.getStringExtra("sid");
        name=myintent.getStringExtra("name");

        User temp=new User("Calden","GOD","Godology","666");
        userlist.add(temp);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String resultURL = ip+"/sessions/"+id;
        new SessionUsers.GetData().execute(resultURL);

        mSessionRecycler = (RecyclerView) findViewById(R.id.userlist);
        mSessionAdapter = new SessionUser(this, userlist);
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mSessionRecycler.setLayoutManager(llm);
        mSessionRecycler.getLayoutManager().setMeasurementCacheEnabled(false);
        mSessionRecycler.setHasFixedSize(true);
        mSessionRecycler.setAdapter(mSessionAdapter);

        FloatingActionButton myFab = (FloatingActionButton)findViewById(R.id.adduser);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //post request to add user
                Intent intent = new Intent(SessionUsers.this, AddUser.class);
                intent.putExtra("sesid",id);
                startActivity(intent);

            }
        });

    }
    @Override
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
                JSONObject jobj=new JSONObject(s);
                String depname=jobj.getString("name");
                JSONArray arr=jobj.getJSONArray("emps");
                userlist.clear();
                for(int i=0;i<arr.length();i++){
                    JSONObject jobj1 = arr.getJSONObject(i);
                    String name=jobj1.getString("name");
                    String id=jobj1.getString("empid");
                    String des=jobj1.getString("designation");
                    User user=new User(name,des,"aaa",id);
                    userlist.add(user);
                    Log.d("lulz",user.print());
                }
                mSessionAdapter.notifyDataSetChanged();
            }

            catch (Exception ex){
                Log.d("Error in SessionUsers", ex.toString());
            }

        }
    }

    public class PostData extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                return postData(params[0]);
            }
            catch (Exception e){
                Log.d("prat", e.toString());
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        public String postData(String urlpath) throws IOException,JSONException {


            JSONObject datatosend=new JSONObject();
            JSONArray temp=new JSONArray();
            //datatosend.put("id",);
            datatosend.put("sid",id);

            Log.d("cal",datatosend.toString());

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
            BufferedWriter bufferedWriter=new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(datatosend.toString());
            bufferedWriter.flush();

            InputStream inputStream= urlConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            String line;
            line=bufferedReader.readLine();
            result.append(line);




            return result.toString();
        }


    }


}

class SessionUser extends RecyclerView.Adapter {
    private Context mContext;
    private List<User> mUserList;


    public SessionUser(Context context, List<User> SessionList) {
        mContext = context;
        mUserList = SessionList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SessionUser.SessionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filler_session_users, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = (User) mUserList.get(position);
        ((SessionUser.SessionHolder) holder).bind(user);

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    private class SessionHolder extends RecyclerView.ViewHolder {
        TextView userName,userDes,userDep,userLoc,userId;

        SessionHolder(View itemView) {
            super(itemView);
            userName= (TextView) itemView.findViewById(R.id.txtuser);
            //userId = (TextView) itemView.findViewById(R.id.textView3);
            userDes=(TextView)itemView.findViewById(R.id.txtdes);
        }

        void bind(User session) {
            userName.setText(session.getName());
            userDes.setText(session.getDesg());
        }
    }
}





