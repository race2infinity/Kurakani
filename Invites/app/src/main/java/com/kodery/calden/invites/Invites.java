package com.kodery.calden.invites;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.*;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

public class Invites extends AppCompatActivity {

    private static Invites sInvites;
    int counter=1;
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

        lstnames.put(Integer.toString(counter),"Greater India");
        lstadmin.put(Integer.toString(counter),"Calden");
        lstdate.put(Integer.toString(counter),"25/03/2018");counter++;

        ListView lst_chat = (ListView) findViewById(R.id.lstdata);
        fillerInvites adapter=new fillerInvites(Invites.this,lstnames,lstadmin,lstdate);
        lst_chat.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        lstnames.put(Integer.toString(counter),"New India");
                        lstadmin.put(Integer.toString(counter),"Roshan");
                        lstdate.put(Integer.toString(counter),"26/03/2018");counter++;
                        ListView lst_chat = (ListView) findViewById(R.id.lstdata);
                        fillerInvites adapter=new fillerInvites(Invites.this,lstnames,lstadmin,lstdate);
                        lst_chat.setAdapter(adapter);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
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
        lstnames.remove(text);
        lstadmin.remove(text);
        lstdate.remove(text);
        ListView lst_chat = (ListView) findViewById(R.id.lstdata);
        fillerInvites adapter=new fillerInvites(Invites.this,lstnames,lstadmin,lstdate);
        lst_chat.setAdapter(adapter);
    }
}
