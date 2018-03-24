package com.kodery.calden.invites;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kodery.calden.invites.R;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;

import android.app.AlertDialog.Builder;
import android.app.AlertDialog;


public class fillerInvites extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> msglist=new ArrayList<String>();
    ArrayList<String> msglistadmin=new ArrayList<String>();
    ArrayList<String> msglistdate=new ArrayList<String>();

    public fillerInvites(Context context, Dictionary msg, Dictionary msgadmin, Dictionary msgdate){
        this.context=context;
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (Enumeration k = msg.keys(); k.hasMoreElements();)
        {
            String temp=k.nextElement().toString();
            msglist.add(temp+" "+msg.get(temp));
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
        final TextView txtView1=(TextView)view.findViewById(R.id.txt);
        final TextView txtView2=(TextView)view.findViewById(R.id.txtadmin);
        final TextView txtView3=(TextView)view.findViewById(R.id.txtdate);
        txtView1.setText(msglist.get(i));
        txtView2.setText(msglistadmin.get(i));
        txtView3.setText(msglistdate.get(i));

        view.setOnClickListener(new View.OnClickListener(){
            int flag;
            @Override
            public void onClick(View view){

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                flag=1;
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                flag=0;
                                break;
                        }
                        String temp=txtView1.getText().toString();
                        if(temp.contains(" ")){
                            temp= temp.substring(0, temp.indexOf(" "));
                        }
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



}