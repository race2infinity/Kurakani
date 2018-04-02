package com.kodery.pratz.notsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Events.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Events#newInstance} factory method to
 * create an instance of this fragment.
 */


public class Events extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        //setUserVisibleHint(true);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    Context context;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<Event> mEventList = new ArrayList<Event>();
    public String ip = MainActivity.ip;
    public String sid;

    public RecyclerView mEventRecycler;
    public EventAdapter mEventAdapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mEventList.add(new Event("Study", "OK", "Bye", "Chaitu", "Hyd", "19191"));
        SharedPreferences sharedPref = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String id = sharedPref.getString("userid","");
        String resultURL = ip + "/eventinvites/" + id;
        new RestOperation().execute(resultURL);


        /*
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        String id = sharedPref.getString("userid","");

                        String resultURL = ip + "/events/" + id;
                        new RestOperation().execute(resultURL);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 0);
            }
        });*/

        mEventRecycler = (RecyclerView) getView().findViewById(R.id.recycle_event);
        mEventAdapter = new EventAdapter(getContext(), mEventList);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        //llm.setStackFromEnd(true);
        llm.setReverseLayout(true);
        mEventRecycler.setLayoutManager(llm);
        mEventRecycler.getLayoutManager().setMeasurementCacheEnabled(false);
        mEventRecycler.setHasFixedSize(true);
        mEventRecycler.setAdapter(mEventAdapter);


        mEventRecycler.addOnItemTouchListener(
                new RecycleritemClickListener(getContext(),mEventRecycler  ,new RecycleritemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Log.d("TEsty","test");

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


/*
        mEventRecycler.addOnItemTouchListener(
            new RecycleritemClickListener(getContext(),mEventRecycler, new RecycleritemClickListener.OnItemClickListener() {
                @Override public void onItemClick(View view, int position) {
                    view.setOnClickListener(new View.OnClickListener(){
                        int flag;
                        //String temp=txtView.getText().toString();
                        //sid=temp;
                        @Override
                        public void onClick(View view){
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            flag=1;
                                            String resultURL = Invites.ip+"/events/yes";
                                            new PostData().execute(resultURL);
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            flag=0;
                                            resultURL = Invites.ip+"/events/no";
                                            new PostData().execute(resultURL);
                                            break;
                                    }
                                   // String temp=txtView.getText().toString();
                                    //Invites.getInstance().setme(flag,temp);
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("Be a part of this Event").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }

                    });
                }

                @Override public void onLongItemClick(View view, int position) {
                    // do whatever
                }
            })
        );*/


    }





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
                mEventList.clear();
                for(int i=0;i<arr.length();i++)
                {
                    jObj = arr.getJSONObject(i);
                    Log.d("printme",jObj.toString());
                    String name = jObj.getString("name"); //Event name
                    sid=jObj.getString("_id"); //Event id
                    String adminid=jObj.getString("creator"); //creator
                    String adminname=jObj.getString("creat_name"); //creat_name
                    String dstart = jObj.getString("starts_at");
                    String dend = jObj.getString("ends_at");
                    String date=jObj.getString("created_at");
                    Log.d("heygys", name+" "+sid);

                    /*if(temp.contains("T")){
                        temp= temp.substring(0, temp.indexOf("T"));
                    }*/
                    Log.d("print1",name+" "+sid+" "+date);
                    Event eve = new Event(name, dstart, dend, adminname, date, sid);
                    mEventList.add(eve);
                }
                mEventAdapter.notifyDataSetChanged();;
            }
            catch (Exception e)
            {
                Log.d("ERROR1",e.toString());

            }
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

            String id=Invites.id;

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
