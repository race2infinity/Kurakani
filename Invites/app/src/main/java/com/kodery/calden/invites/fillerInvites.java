package com.kodery.calden.invites;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kodery.calden.invites.R;

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
import java.util.Dictionary;
import java.util.Enumeration;

import android.app.AlertDialog.Builder;
import android.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;


public class fillerInvites extends BaseAdapter {

    Context context;
    String sid;
    LayoutInflater layoutInflater;
    ArrayList<String> msglist=new ArrayList<String>();
    ArrayList<String> msglistadmin=new ArrayList<String>();
    ArrayList<String> msglistdate=new ArrayList<String>();
    ArrayList<String> msgid=new ArrayList<String>();

    public fillerInvites(Context context, Dictionary msg, Dictionary msgadmin, Dictionary msgdate){
        this.context=context;
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (Enumeration k = msg.keys(); k.hasMoreElements();)
        {
            String temp=k.nextElement().toString();
            msgid.add(temp);
            msglist.add(msg.get(temp)+"");
            msglistadmin.add(msgadmin.get(temp)+"");
            msglistdate.add(msgdate.get(temp)+"");
        }
    /*
    for(int i=0;i<msg.size();i++){
        msglist.add(msg.get(i));
        msglistadmin.add(msgadmin.get(i));
        msglistdate.add(msgdate.get(i));
    }*/
    }

    @Override
    public int getCount() {
        return msglist.size();
    }

    @Override
    public Object getItem(int i) {
        return msglist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=layoutInflater.inflate(R.layout.activity_fillerinvites,null);
        final TextView txtView=(TextView)view.findViewById(R.id.sid);
        final TextView txtView1=(TextView)view.findViewById(R.id.txt);
        final TextView txtView2=(TextView)view.findViewById(R.id.txtadmin);
        final TextView txtView3=(TextView)view.findViewById(R.id.txtdate);
        txtView.setText(msgid.get(i));
        //sid=(msgid.get(i));
        txtView1.setText(msglist.get(i));
        txtView2.setText(msglistadmin.get(i));
        txtView3.setText(msglistdate.get(i));

        view.setOnClickListener(new View.OnClickListener(){
            int flag;
            @Override
            public void onClick(View view){
                String temp=txtView.getText().toString();
                sid=temp;
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                flag=1;
                                String resultURL = "http://192.168.0.8:3020/sessions/yes";
                                new PostData().execute(resultURL);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                flag=0;
                                resultURL = "http://192.168.0.8:3020/sessions/no";
                                new PostData().execute(resultURL);
                                break;
                        }
                        String temp=txtView.getText().toString();
                        Invites.getInstance().setme(flag,temp);
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Be a part of this Session").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }

        });

        return view;

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
            }catch (IOException e){
                return "Network Error";
            }catch (JSONException e){
                return "Data Invalid";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        public String postData(String urlpath) throws IOException,JSONException {

            JSONObject datatosend=new JSONObject();
            //JSONArray temp=new JSONArray();
            //JSONArray mem=new JSONArray();
            datatosend.put("id","7712");

            datatosend.put("sid",sid);

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