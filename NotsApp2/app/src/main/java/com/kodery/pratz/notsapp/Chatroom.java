package com.kodery.pratz.notsapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import java.net.*;


public class Chatroom extends AppCompatActivity {

    public static String id=Sessions.id;
    public static String ip=Sessions.ip;
    //public TextView stringbox;
    private RecyclerView mMessageRecycler;
    private MessageAdapter mMessageAdapter;
    public ArrayList<Message> messageList=new ArrayList<Message>();
    public String text,value="";

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

        Bundle b = getIntent().getExtras();
        //String value = ""; // or other values
        if(b != null)
            value = b.getString("id");

        User usr = (User) new User("Lord Kaldon", "Desg2", "Dept2", id);
        Message temp =(Message) new Message("MESSAGE CONTENT", usr, "9pm");
        messageList.add(temp);

        setContentView(R.layout.activity_chatroom);

        //stringbox=(TextView)findViewById(R.id.textView2);

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
                    JSONObject data = (JSONObject) args[0];
                    Log.d("uu","uu");
                    try {
                        String id1 = data.getString("sender");
                        String message = data.getString("body");

                        User usr = (User) new User(id1, "Designation1", "Department1", id1);
                        Message temp =(Message) new Message(message, usr, "9pm");

                        messageList.add(temp);
                        mMessageAdapter.notifyDataSetChanged();
                        mMessageRecycler.scrollToPosition(messageList.size()-1);
                    } catch (JSONException e) {
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
                for(int i=0; i<jarr.length(); i++) {
                    mainObject = jarr.getJSONObject(i);
                    String name = mainObject.getString("sender");
                    String message = mainObject.getString("body");

                    User usr = (User) new User(name, "Designation1", "Department1", name);
                    Message temp =(Message) new Message(message, usr, "9pm");
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


