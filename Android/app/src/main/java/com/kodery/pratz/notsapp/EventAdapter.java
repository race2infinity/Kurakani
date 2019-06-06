package com.kodery.pratz.notsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kodery.pratz.notsapp.Events;

import java.util.ArrayList;
import java.util.List;

class Event {
    //name, start_at, end_at, creator, creator+invited (JSONarray)
    String name;
    String start_at;
    String end_at;
    String creator;
    String created_at;
    String id;


    public Event(String name, String start_at, String end_at, String creator, String created_at, String id) {
        this.name = name;
        this.start_at = start_at;
        this.end_at = end_at;
        this.creator = creator;
        this.created_at = created_at;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getStart_at() {
        return start_at;
    }

    public String getEnd_at() {
        return end_at;
    }

    public String getCreator() {
        return creator;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getId() {
        return id;
    }
}


public class EventAdapter extends RecyclerView.Adapter {
    private Context mContext;
    //public final PositionClickListener listener;
    ArrayList<Event> mEventList = new ArrayList<Event>();
   // LayoutInflater layoutInflater;


    public EventAdapter(Context mContext, ArrayList<Event> mEventList) {
        this.mContext = mContext;
        this.mEventList = mEventList;
       // layoutInflater=(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventAdapter.EventHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_fillerinvites, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Event event = (Event) mEventList.get(position);
        ((EventAdapter.EventHolder) holder).bind(event);

    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount1111: ENTERED");
        return mEventList.size();
    }

    private class EventHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventId, eventTime, eventAdmin;

        EventHolder(View itemView) {
            super(itemView);
            eventName= (TextView) itemView.findViewById(R.id.txt);
            eventAdmin = (TextView) itemView.findViewById(R.id.txtadmin) ;
            eventId = (TextView) itemView.findViewById(R.id.sid);
            eventTime=(TextView)itemView.findViewById(R.id.txtdate);
        }

        void bind(Event event) {
            eventName.setText(event.getName());
            eventId.setText(event.getId());
            eventTime.setText(event.getCreated_at());
            eventAdmin.setText(event.getCreator());


        }
    }
}