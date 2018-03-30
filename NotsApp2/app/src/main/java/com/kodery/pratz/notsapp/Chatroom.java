package com.kodery.pratz.notsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.*;


public class Chatroom extends AppCompatActivity {

    public static String ip=Sessions.ip;
    //public TextView stringbox;
    private RecyclerView mMessageRecycler;
    private MessageAdapter mMessageAdapter;
    public ArrayList<Message> messageList=new ArrayList<Message>();
    public String text,value="",valuename;
    public static String id="";

    private Socket socket;

    {
        try {
            socket = IO.socket(ip);

        } catch (URISyntaxException e) {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        id = sharedPref.getString("userid","");

        Bundle b = getIntent().getExtras();
        //String value = ""; // or other values
        if(b != null)
        { value = b.getString("id");
        valuename=b.getString("name");
        }



        User usr = (User) new User("Lord Kaldon", "Desg2", "Dept2", id);
        Message temp =(Message) new Message("MESSAGE CONTENT", usr, "9pm");
        messageList.add(temp);

        setContentView(R.layout.activity_chatroom);

        //stringbox=(TextView)findViewById(R.id.textView2);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar6);
        //ActionBar actionbar=getSupportActionBar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(valuename);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Chatroom.this, SessionUsers.class);
                intent.putExtra("sid",value);
                intent.putExtra("name",valuename);
                startActivity(intent);
            }
        });



        mMessageRecycler = (RecyclerView) findViewById(R.id.recyclerview_message_list);

        socket.connect();
        socket.on("chat", handleIncomingMessages);


        String resultURL = ip+"/messages/"+value;
        new GetData().execute(resultURL);


        mMessageAdapter = new MessageAdapter(this, messageList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        //llm.setStackFromEnd(true);
        //llm.setReverseLayout(true);
        mMessageRecycler.setLayoutManager(llm);
        mMessageRecycler.setAdapter(mMessageAdapter);

        Button btn = (Button) findViewById(R.id.button_chatbox_send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txt = (EditText) findViewById(R.id.edittext_chatbox);
                text = txt.getText().toString();
                txt.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                txt.setText("");
                if(!text.equals("")) {
                    String resultURL = ip+"/messages/";
                    new Chatroom.PostData().execute(resultURL, text);
                    mMessageAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();

    }

//Socket listenr
    private Emitter.Listener handleIncomingMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Chatroom.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject mainObject = (JSONObject) args[0];
                    Log.d("chai",mainObject.toString());
                    Log.d("uu","uu");
                    try {
                        String id = mainObject.getString("sender");
                        String message = mainObject.getString("body");
                        String ses=mainObject.getString("sess_id");
                        String des=mainObject.getString("send_des");
                        String dep=mainObject.getString("send_dep");
                        String name=mainObject.getString("send_name");
                        String time=mainObject.getString("created_at");

                        SimpleDateFormat gg=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        //gg.setTimeZone(Calendar.getInstance().getTimeZone());
                        SimpleDateFormat fd=new SimpleDateFormat("HH:mm");
                        Date dateobj;
                        String ttime;
                        dateobj =gg.parse(time);
                        ttime=fd.format(dateobj);

                        User usr = (User) new User(name, des, dep, id);
                        Message temp =(Message) new Message(message, usr, ttime);
                        messageList.add(temp);
                        mMessageAdapter.notifyDataSetChanged();
                        mMessageRecycler.scrollToPosition(messageList.size()-1);

                    } catch (Exception e) {
                        Log.d("yomom",e.toString());
                    }

                }
            });
        }
    };


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
                JSONArray jarr = new JSONArray(s);
                JSONObject mainObject;
                messageList.clear();
                for(int i=0; i<jarr.length(); i++) {
                    mainObject = jarr.getJSONObject(i);
                    String id = mainObject.getString("sender");
                    String message = mainObject.getString("body");
                    String ses=mainObject.getString("sess_id");
                    String des=mainObject.getString("send_des");
                    String dep=mainObject.getString("send_dep");
                    String name=mainObject.getString("send_name");
                    String time=mainObject.getString("created_at");

                    SimpleDateFormat gg=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    //gg.setTimeZone(Calendar.getInstance().getTimeZone());
                    SimpleDateFormat fd=new SimpleDateFormat("HH:mm");
                    Date dateobj;
                    String ttime;
                    dateobj =gg.parse(time);
                    ttime=fd.format(dateobj);

                    User usr = (User) new User(name, des, dep, id);
                    Message temp =(Message) new Message(message, usr, ttime);
                    messageList.add(temp);
                    mMessageAdapter.notifyDataSetChanged();
                    mMessageRecycler.scrollToPosition(messageList.size()-1);
                    //Log.d("hi",messageList.get(1).getText());
                }

                //stringbox.setText(name+"    "+message);
            }
            catch (Exception ex){
                Log.d("GG END MID", ex.toString());
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

        public String postData(String urlpath) throws IOException,JSONException{


            JSONObject datatosend=new JSONObject();
            JSONArray temp=new JSONArray();
            datatosend.put("sender",id);
            datatosend.put("sess_id",value);
            datatosend.put("body",text);

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


