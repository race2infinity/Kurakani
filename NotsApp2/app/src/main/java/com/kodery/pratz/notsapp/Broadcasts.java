package com.kodery.pratz.notsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Broadcasts.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Broadcasts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Broadcasts extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView mSessionRecycler;
    BroadcastAdapter mSessionAdapter;

    public static String id;
    public static String ip=Sessions.ip;
    public static ArrayList<Broad> broadlist=new ArrayList<Broad>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Broadcasts() {
        // Required empty public constructor
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Broadcasts.
     */
    // TODO: Rename and change types and number of parameters
    public static Broadcasts newInstance(String param1, String param2) {
        Broadcasts fragment = new Broadcasts();
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
        return inflater.inflate(R.layout.fragment_broadcasts, container, false);
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

        SharedPreferences sharedPref = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        id = sharedPref.getString("userid","");

        Broad broad=new Broad("Hello World","Calden","9:00","","");
        //broadlist.clear();
       // broadlist.add(broad);

        String resultURL = ip+"/broadcast";
        new Broadcasts.GetData().execute(resultURL);

        mSessionRecycler = (RecyclerView) getActivity().findViewById(R.id.broadlist);
        mSessionAdapter = new BroadcastAdapter(getContext(), broadlist);
        LinearLayoutManager llm = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        mSessionRecycler.setLayoutManager(llm);
        mSessionRecycler.getLayoutManager().setMeasurementCacheEnabled(false);
        mSessionRecycler.setHasFixedSize(true);
        mSessionRecycler.setAdapter(mSessionAdapter);

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
                Log.d("Broadcast",s);
                JSONArray arr=new JSONArray(s);
                JSONObject jobj1;
                broadlist.clear();
                for(int i=0;i<arr.length();i++){
                    jobj1 = arr.getJSONObject(i);
                    String bname=jobj1.getString("body");
                    String sname=jobj1.getString("send_name");
                    String time=jobj1.getString("created_at");
                    String dep=jobj1.getString("send_dep");
                    String des=jobj1.getString("send_des");
                    Broad broad=new Broad(bname,sname,time,dep,des);
                    broadlist.add(broad);
                    Log.d("lulz",broad.print());
                }
                mSessionAdapter.notifyDataSetChanged();
            }

            catch (Exception ex){
                Log.d("Error in Broadcast", ex.toString());
            }

        }
    }


}

class Broad {
    String bname;
    String sname;
    String time;
    String des;
    String dep;


    public Broad(String bname, String sname, String time,String dep,String des) {
        this.bname = bname;
        this.sname = sname;
        this.time = time;
        this.des=des;
        this.dep=dep;
    }

    public String getBname() {
        return bname;
    }

    public String getSname() {
        return sname;
    }

    public String getTime() {
        return time;
    }

    public String getDep() {
        return dep;
    }

    public String getDes() {
        return des;
    }

    public String print() {
        return bname + " " + sname + " " + time + " "+des+" "+dep;
    }
}


class BroadcastAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Broad> mBroadList;


    public BroadcastAdapter(Context context, List<Broad> mbroadList) {
        mContext = context;
        mBroadList = mbroadList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BroadcastAdapter.SessionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.filler_broadcast, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Broad broad = (Broad) mBroadList.get(position);
        ((BroadcastAdapter.SessionHolder) holder).bind(broad);

    }

    @Override
    public int getItemCount() {
        return mBroadList.size();
    }

    private class SessionHolder extends RecyclerView.ViewHolder {
        TextView userBname,userSname,userTime,userDep,userDes;

        SessionHolder(View itemView) {
            super(itemView);
            userSname= (TextView) itemView.findViewById(R.id.txtsname);
            userBname=(TextView)itemView.findViewById(R.id.txtbname);
            userTime=(TextView)itemView.findViewById(R.id.txttime);
            userDep=(TextView)itemView.findViewById(R.id.txtdept);
            userDes=(TextView)itemView.findViewById(R.id.txtdes);
        }

        void bind(Broad session) {
            userBname.setText(session.getBname());
            userSname.setText(session.getSname());
            userTime.setText(session.getTime());
            userDep.setText(session.getDep());
            userDes.setText(session.getDes());
        }
    }
}