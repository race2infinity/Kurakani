package com.kodery.calden.newsession;

import android.content.Context;
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

import java.util.ArrayList;

public class filler extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<String> msglist=new ArrayList<String>();
    ArrayList<String> listofemp=new ArrayList<String>();

    public filler(Context context, ArrayList<String> msg){
        this.context=context;
        layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for(int i=0;i<msg.size();i++)
        {
            msglist.add(msg.get(i));
        }
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
        view=layoutInflater.inflate(R.layout.activity_filler,null);
        final TextView txtView=(TextView)view.findViewById(R.id.txtView);
        txtView.setText(msglist.get(i));

        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String temp=txtView.getText().toString();
                if(listofemp.contains(temp))
                {
                    listofemp.remove(temp);
                }
                else
                {
                    listofemp.add(temp);
                }
                newSession.getInstance().setme(listofemp);
            }
        });



        return view;

    }



}
