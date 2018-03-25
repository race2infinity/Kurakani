package com.kodery.calden.invites;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.*;
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
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Invites extends AppCompatActivity {

    private static Invites sInvites;

    int counter=1,flag=0;
    String adminname;
    String sid;
    //public ArrayList<String> lstnames=new ArrayList<String>();
    Dictionary lstnames=new Hashtable<String, String>();
    Dictionary lstadmin=new Hashtable<String, String>();
    Dictionary lstdate=new Hashtable<String, String>();
    //public ArrayList<String> lstadmin=new ArrayList<String>();
    //public ArrayList<String> lstdate=new ArrayList<String>();
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sInvites=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invites);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        Log.d("lol","lol");
                        String resultURL = "http://192.168.0.8:3020/findinvites/"+"7712";
                        new RestOperation().execute(resultURL);
                        ListView lst_chat = (ListView) findViewById(R.id.lstdata);
                        fillerInvites adapter=new fillerInvites(Invites.this,lstnames,lstadmin,lstdate);
                        lst_chat.setAdapter(adapter);
                        flag=1;
                    }
                });
            }
        }, 0, 1000);


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        String resultURL = "http://192.168.0.8:3020/findinvites/"+"7712";
                        new RestOperation().execute(resultURL);
                        ListView lst_chat = (ListView) findViewById(R.id.lstdata);
                        fillerInvites adapter=new fillerInvites(Invites.this,lstnames,lstadmin,lstdate);
                        lst_chat.setAdapter(adapter);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 0);
            }
        });
    }
    public static Invites getInstance() {
        return sInvites;
    }

    public void setme(int flag,String text){
        if(flag==1){
            Toast.makeText(Invites.this, "You are now a part of this Session", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(Invites.this, "You have declined the session request", Toast.LENGTH_SHORT).show();
        }
        setsid(text);
        lstnames.remove(text);
        lstadmin.remove(text);
        lstdate.remove(text);
        ListView lst_chat = (ListView) findViewById(R.id.lstdata);
        fillerInvites adapter=new fillerInvites(Invites.this,lstnames,lstadmin,lstdate);
        lst_chat.setAdapter(adapter);
    }

    public void callme()
    {
        ListView lst_chat = (ListView) findViewById(R.id.lstdata);
        fillerInvites adapter=new fillerInvites(Invites.this,lstnames,lstadmin,lstdate);
        lst_chat.setAdapter(adapter);
    }

    public void setsid(String temp){
        sid=temp;
    }

    public class RestOperation extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params)
        {
            StringBuilder result= new StringBuilder();
            try{

                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                line=bufferedReader.readLine();

                result.append(line).append("\n");


            }catch(IOException ex){
                return ex.toString();
            }
            return result.toString();
        }


        @Override
        protected void onPreExecute()
        {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                super.onPostExecute(result);

                Log.d("calden",result);
                JSONArray arr=new JSONArray(result);
                JSONObject jObj;
                for(int i=0;i<arr.length();i++)
                {
                    jObj = arr.getJSONObject(i);
                    String name = jObj.getString("name");
                    String id=jObj.getString("_id");
                    String adminid=jObj.getString("admin");
                    String date=jObj.getString("created_at");
                    String temp=date;
                    if(temp.contains("T")){
                        temp= temp.substring(0, temp.indexOf("T"));
                    }

                    String resultURL = "http://192.168.0.8:3020/userdata/"+adminid;
                    new RestOperation2().execute(resultURL);
                    if(lstnames.get(id)==null && flag==1)
                    {
                            Log.d("change","its new");
                        NotificationCompat.Builder b = new NotificationCompat.Builder(Invites.this);
                        b.setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setTicker("Hearty365")
                                .setContentTitle("Invites Notification")
                                .setContentText("You have Pending invites")
                                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                                .setContentInfo("Info");
                        NotificationManager notificationManager = (NotificationManager) Invites.this.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.notify(1, b.build());

                    }

                    lstnames.put(id,name);
                    lstadmin.put(id,adminname);
                    lstdate.put(id,temp);
                }
            }
            catch (Exception e)
            {
                Log.d("ERROR",e.toString());

            }
        }
    }

    public void setAdmin(String temp)
    {
        adminname=temp;
    }

    public class RestOperation2 extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params)
        {
            StringBuilder result= new StringBuilder();
            try{

                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                line=bufferedReader.readLine();

                result.append(line).append("\n");


            }catch(IOException ex){
                return ex.toString();
            }
            return result.toString();
        }


        @Override
        protected void onPreExecute()
        {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result)
        {
            try {
                super.onPostExecute(result);
                //Log.d("hello",result);
                JSONArray a=new JSONArray(result);
                JSONObject j=a.getJSONObject(0);
                String temp=j.getString("name");
                setAdmin(temp);
            }
            catch (Exception e)
            {
                Log.d("ERROR",e.toString());

            }
        }
    }

}
