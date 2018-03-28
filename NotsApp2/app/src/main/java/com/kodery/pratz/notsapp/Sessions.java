package com.kodery.pratz.notsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Sessions.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Sessions#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sessions extends Fragment {
    public static Sessions mSessions;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //setUserVisibleHint(true);
    }

    public static Sessions getInstance() {
        return mSessions;
    }

    //public static SharedPreferences sharedPref = getSharedPreferences("userinfo", Context.MODE_PRIVATE);
    //SharedPreferences.Editor editor = sharedPref.edit();
    //public static String id= Pref.getString("userid", null);
    //public static String id= "4234";
   public static String id="";
    public static String ip="http://192.168.0.8:3020";

    public RecyclerView mSessionRecycler;
    public SessionAdapter mSessionAdapter;
    public static ArrayList<Session> sessionList=new ArrayList<Session>();

    private Socket socket;

    {
        try {
            socket = IO.socket(ip);

        } catch (URISyntaxException e) {

        }
    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Sessions() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Sessions.
     */
    // TODO: Rename and change types and number of parameters
    public static Sessions newInstance(String param1, String param2) {
        Sessions fragment = new Sessions();
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
        mSessions=this;
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_sessions, container, false);

        View rootView = inflater.inflate(R.layout.fragment_sessions, container, false);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*Button button = (Button) getView().findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Chatroom.class);
                startActivity(intent);

            }
        });*/

        socket.connect();
        socket.on("refresh", handleIncomingMessages);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        id = sharedPref.getString("userid","");

        Session temp=(Session)new Session("name","5ab732b6a173133b1a6c481a","9:00pm");
        sessionList.add(temp);


        String resultURL = ip+"/findsessions/"+id;
        new Sessions.GetData().execute(resultURL);

        mSessionRecycler = (RecyclerView) getView().findViewById(R.id.session_recycler);
        mSessionAdapter = new SessionAdapter(getContext(), sessionList);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        //llm.setStackFromEnd(true);
        //llm.setReverseLayout(true);
        mSessionRecycler.setLayoutManager(llm);
        mSessionRecycler.getLayoutManager().setMeasurementCacheEnabled(false);
        mSessionRecycler.setHasFixedSize(true);


        mSessionRecycler.addOnItemTouchListener(
                new RecycleritemClickListener(getContext(),mSessionRecycler  ,new RecycleritemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        String id,name;
                        TextView txtView3=view.findViewById(R.id.textView3);
                        TextView txtView4=view.findViewById(R.id.textView2);
                        id=txtView3.getText().toString();
                        name=txtView4.getText().toString();

                        Intent intent = new Intent(getActivity(),Chatroom.class);
                        Bundle b = new Bundle();
                        b.putString("id", id); //Your id
                        b.putString("name",name);
                        intent.putExtras(b);
                        startActivity(intent);

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        mSessionRecycler.setAdapter(mSessionAdapter);
    }


    private Emitter.Listener handleIncomingMessages = new Emitter.Listener() {

            @Override
            public void call ( final Object...args){

            if(getActivity()!=null){
            getActivity().runOnUiThread(new Runnable() {



                    public void run () {

                    Log.d("uu", "uu");
                    try {
                        sort();
                        mSessionRecycler = (RecyclerView) getView().findViewById(R.id.session_recycler);
                        mSessionAdapter = new SessionAdapter(getContext(), sessionList);
                        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        //llm.setStackFromEnd(true);
                        //llm.setReverseLayout(true);
                        mSessionRecycler.setLayoutManager(llm);
                        mSessionRecycler.getLayoutManager().setMeasurementCacheEnabled(false);
                        mSessionRecycler.setHasFixedSize(true);

                    } catch (Exception e) {
                        Log.d("yomom", e.toString());
                    }

                }

            });
            }

    }};


    public void sort()
    {

        Collections.sort(sessionList, new Comparator<Session>() {
            @Override
            public int compare(Session o1, Session o2) {
                return o1.time.compareTo(o2.time);
            }
        });
        Collections.reverse(sessionList);
    }


    public class GetData extends AsyncTask<String, Void, String> {
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
                sessionList.clear();
                Log.d("bis",s);
                JSONArray jarr = new JSONArray(s);
                JSONObject main;
                for(int i=0;i<jarr.length();i++){

                    main=jarr.getJSONObject(i);
                    String name=main.getString("name");
                    String id=main.getString("_id");
                    String time=main.getString("LastMT");

                    SimpleDateFormat gg=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    //gg.setTimeZone(Calendar.getInstance().getTimeZone());
                    SimpleDateFormat fd=new SimpleDateFormat("HH:mm");
                    Date dateobj;
                    String ttime;
                    dateobj =gg.parse(time);
                    ttime=fd.format(dateobj);

                    Session temp = (Session) new Session(name,id,ttime);
                    sessionList.add(temp);
                    //Log.d("jo",sessionList.get(i+1).getName());
                }
                sort();
                mSessionAdapter.notifyDataSetChanged();

            }
            catch (Exception ex){
                Log.d("GG END MID", ex.toString());
            }

        }
    }





}
