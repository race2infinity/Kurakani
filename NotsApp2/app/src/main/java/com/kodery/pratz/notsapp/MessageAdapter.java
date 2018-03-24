package com.kodery.pratz.notsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by pratz on 23/3/18.
 */
class Message {
    String text;
    User sender;
    String time;

    public Message(String text, User sender, String time) {
        this.text = text;
        this.sender = sender;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return sender.getName();
    }

    public String getTime() {
        return time;
    }
}

class User {
    String name;
    String profileurl;

    public User(String name, String profileurl) {
        this.name = name;
        this.profileurl = profileurl;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return profileurl;
    }

}

public class MessageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Message> mMessageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
        Log.d(TAG, messageList.get(0).getText());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        Log.d(TAG, "onCreateViewHolder: ENTERED");
        v=LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false);
        return new ReceivedMessageHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);

        ((ReceivedMessageHolder) holder).bind(message);

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ENTERED");
        return mMessageList.size();
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        //ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "ReceivedMessageHolder: ENTERED");
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            //profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Message message) {

            Log.d(TAG, "bind: ENTERED");
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());

            nameText.setText(message.getUser());

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

}
