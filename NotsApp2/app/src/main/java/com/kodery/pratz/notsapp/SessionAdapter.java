package com.kodery.pratz.notsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by pratz on 26/3/18.
 */

class Session {
    String name;
    String id;
    String time;
    String lmsg;
    String unread;

    public Session(String name, String id) {
    //public Session(String name, String id, String time,String lmsg,String unread) {
        this.name = name;
        this.id = id;
        //this.time = time;
        //this.lmsg= lmsg;
        //this.unread=unread;

    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getLmsg() {
        return lmsg;
    }

    public String getUnread() {
        return unread;
    }
}

public class SessionAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Session> mSessionList;


    public SessionAdapter(Context context, List<Session> SessionList) {
        mContext = context;
        mSessionList = SessionList;
        //Log.d("mytag", SessionList.get(0).getName());
    }

/*
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);

        if(message.getUser().getURL().equals(Chatroom.id)) {
            // If the current user is the sender of the message
            return 1;
        }
        else {
            // If some other user sent the message
            return 2;
        }
    }
    */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SessionAdapter.SessionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.session_filler, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Session session = (Session) mSessionList.get(position);
        ((SessionAdapter.SessionHolder) holder).bind(session);

    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount1111: ENTERED");
        return mSessionList.size();
    }

    private class SessionHolder extends RecyclerView.ViewHolder {
        TextView sessionName, sessionId;

        SessionHolder(View itemView) {
            super(itemView);
            sessionName= (TextView) itemView.findViewById(R.id.textView2);
            sessionId = (TextView) itemView.findViewById(R.id.textView3);
        }

        void bind(Session session) {
           sessionName.setText(session.getName());
           sessionId.setText(session.getId());
        }
    }







}
