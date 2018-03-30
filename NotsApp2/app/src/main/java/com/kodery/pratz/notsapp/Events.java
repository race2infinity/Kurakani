package com.kodery.pratz.notsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Dictionary;
import java.util.Hashtable;
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
import com.kodery.pratz.notsapp.Sessions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Events.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Events#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Events extends Fragment {
   // public static String id=Sessions.id;

    public static  String ip=Sessions.ip;
    public static String id;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //KALDON's VARIABLES:
    private static Events sInvites;

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


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


    public Events() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Events.
     */
    // TODO: Rename and change types and number of parameters
    public static Events newInstance(String param1, String param2) {
        Events fragment = new Events();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //KALDON's SHIT BEGINS:


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sInvites = this;

        SharedPreferences sharedPref = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        id = sharedPref.getString("userid","");
/*
        try {
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    getActivity().runOnUiThread(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                SharedPreferences sharedPref = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                String id = sharedPref.getString("userid","");


                                Log.d("lol", "lol");
                                String resultURL = ip + "/findinvites/" + id;
                                new RestOperation().execute(resultURL);
                                ListView lst_chat = (ListView) getView().findViewById(R.id.lstdata);
                                fillerInvites adapter = new fillerInvites(getActivity(), lstnames, lstadmin, lstdate);
                                lst_chat.setAdapter(adapter);
                                flag = 1;
                            } catch (Exception e) {
                                Log.d("hello", e.toString());
                            }
                        }
                    });

                }
            }, 0, 1000 * 60);
        } catch (Exception e) {
            Log.d("YAYAYA", e.toString());


        }
*/

        Log.d("lol", "lol");
        String resultURL = ip + "/findinvites/" + id;
        new RestOperation().execute(resultURL);
        ListView lst_chat = (ListView) getView().findViewById(R.id.lstdata);
        fillerInvites adapter = new fillerInvites(getActivity(), lstnames, lstadmin, lstdate);
        lst_chat.setAdapter(adapter);


        mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        String id = sharedPref.getString("userid","");

                        String resultURL = ip + "/findinvites/" + id;
                        new RestOperation().execute(resultURL);
                        ListView lst_chat = (ListView) getView().findViewById(R.id.lstdata);
                        fillerInvites adapter = new fillerInvites(getActivity(), lstnames, lstadmin, lstdate);
                        lst_chat.setAdapter(adapter);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 0);
            }
        });
    }


    public static Events getInstance() {
        return sInvites;
    }

    public void setme(int flag,String text){
        if(flag==1){
            SharedPreferences sharedPref = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            String id = sharedPref.getString("userid","");

            Toast.makeText(getActivity(), "You are now a part of this Session", Toast.LENGTH_SHORT).show();
            String resultURL = ip+"/findsessions/"+id;
            Sessions.GetData innerObject = new Sessions().new GetData();
            innerObject.execute(resultURL);
        }
        else{
            Toast.makeText(getActivity(), "You have declined the session request", Toast.LENGTH_SHORT).show();
        }
        setsid(text);
        lstnames.remove(text);
        lstadmin.remove(text);
        lstdate.remove(text);
        ListView lst_chat = (ListView) getView().findViewById(R.id.lstdata);
        fillerInvites adapter=new fillerInvites(getActivity(),lstnames,lstadmin,lstdate);
        lst_chat.setAdapter(adapter);
    }

    public void callme()
    {
        ListView lst_chat = (ListView) getView().findViewById(R.id.lstdata);
        fillerInvites adapter=new fillerInvites(getActivity(),lstnames,lstadmin,lstdate);
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
                    Log.d("printme",jObj.toString());
                    String name = jObj.getString("name");
                    String id=jObj.getString("_id");
                    String adminid=jObj.getString("admin");
                    String adminname=jObj.getString("admin_name");
                    String date=jObj.getString("created_at");
                    String temp=date;
                    if(temp.contains("T")){
                        temp= temp.substring(0, temp.indexOf("T"));
                    }
                    Log.d("print1",name+" "+id+" "+date);
                    String resultURL = ip+"/userdata/"+adminid;
                    new RestOperation2().execute(resultURL);

                    //TimeUnit.SECONDS.sleep(3);

                    /*
                    if(lstnames.get(id)==null && flag==1)
                    {
                        Log.d("change","its new");
                        NotificationCompat.Builder b = new NotificationCompat.Builder(getActivity());
                        b.setAutoCancel(true)
                                .setDefaults(Notification.DEFAULT_ALL)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.ic_launcher_foreground)
                                .setTicker("Hearty365")
                                .setContentTitle("Invites Notification")
                                .setContentText("You have Pending invites")
                                .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                                .setContentInfo("Info");
                        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                        //notificationManager.notify(1, b.build());

                    }
*/
                    lstnames.put(id,name);
                    lstadmin.put(id,adminname);
                    lstdate.put(id,temp);
                }
            }
            catch (Exception e)
            {
                Log.d("ERROR1",e.toString());

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
                Log.d("hello",result);
                JSONObject a=new JSONObject(result);
                //JSONObject j=a.getJSONObject(0);
                String temp=a.getString("name");
                setAdmin(temp);
            }
            catch (Exception e)
            {
                Log.d("ERROR2",e.toString());

            }
        }
    }


}


class fillerInvites extends BaseAdapter {

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
                                String resultURL = Events.ip+"/sessions/yes";
                                new PostData().execute(resultURL);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                flag=0;
                                resultURL = Events.ip+"/sessions/no";
                                new PostData().execute(resultURL);
                                break;
                        }
                        String temp=txtView.getText().toString();
                        Events.getInstance().setme(flag,temp);
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

            String id=Events.id;

            //SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            //String id=sharedPref.getString(MainActivity."userid","");

            //JSONArray temp=new JSONArray();
            //JSONArray mem=new JSONArray();
            datatosend.put("id",id);

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
