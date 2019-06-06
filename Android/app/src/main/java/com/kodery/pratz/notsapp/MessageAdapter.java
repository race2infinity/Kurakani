package com.kodery.pratz.notsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
    Bitmap image;

    public Message(String text, User sender, String time,Bitmap image) {
        this.text = text;
        this.sender = sender;
        this.time = time;
        this.image=image;
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return sender;
    }

    public String getTime() {
        return time;
    }

    public Bitmap getImage(){return image;}
}

class User {
    String name;
    String desg;
    String dept;
    String profileurl;

    public User(String name, String desg, String dept, String profileurl) {
        this.name = name;
        this.desg = desg;
        this.dept = dept;
        this.profileurl = profileurl;
    }

    public String getName() {
        return name;
    }

    public String getDept() {
        return dept;
    }

    public String getDesg() {
        return desg;
    }

    public String getURL() {
        return profileurl;
    }

    public String print(){return name+" "+dept+" "+desg+" "+profileurl;}

}

public class MessageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Message> mMessageList;
    public String id;


    public MessageAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
        id=Chatroom.id;
        //Log.d(TAG, messageList.get(0).getText());
    }


    @Override
    public int getItemViewType(int position) {

        Message message = (Message) mMessageList.get(position);
       // Log.d("kklol",message.getImage().toString());
        if(message.getImage()==null && message.getUser()!=null)
        {
            if(message.getUser().getURL().equals(id)) {
                // If the current user is the sender of the message
                return 1;
            }
            else {
                // If some other user sent the message*/
                return 2;
            }
        }
        else{
            return 3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType)
        {
            case 1:
                return new SentMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent, parent, false));

            case 2:
                return new ReceivedMessageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false));

            default:
                return new ImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.image_sent, parent, false));

        }
        //Log.d(TAG, "onCreateViewHolder: ENTERED");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);

        switch(holder.getItemViewType())
        {

            case 1:
                ((SentMessageHolder) holder).bind(message);
                break;
            case 2:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case 3:
                ((ImageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount: ENTERED");
        return mMessageList.size();
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, desText, depText; //ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "ReceivedMessageHolder: ENTERED");
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            desText = (TextView) itemView.findViewById(R.id.text_message_post);
            //profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);

        }

        void bind(Message message) {

            Log.d(TAG, "bind: ENTERED");
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());

            nameText.setText(message.getUser().getName());

            desText.setText(message.getUser().getDesg()+", "+message.getUser().getDept());

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView imageView;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            imageView=(ImageView) itemView.findViewById(R.id.imageView4);
        }

        void bind(Message message) {
            messageText.setText(message.getText());
            timeText.setText(message.getTime());}

    }

    private class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageHolder(View itemView) {
            super(itemView);
            imageView=(ImageView) itemView.findViewById(R.id.imageView4);
        }

        void bind(Message message) {

                imageView.setImageBitmap(message.getImage());
        }
    }
}
