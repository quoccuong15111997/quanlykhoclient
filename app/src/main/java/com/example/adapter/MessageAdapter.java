package com.example.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firebase.ChatMessage;
import com.example.quanlykho.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<ChatMessage> {
    Activity context = null;
    List<ChatMessage> objects;
    int resource;

    public MessageAdapter(Context context, int resource, ArrayList<ChatMessage> objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        this.resource = resource;
        this.objects = objects;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        LayoutInflater layoutInflater = this.context.getLayoutInflater();
        if(convertView==null){
            viewHolder= new ViewHolder();
            convertView=layoutInflater.inflate(this.resource,null);
            viewHolder.messageText=convertView.findViewById(R.id.message_text);
            viewHolder.messageUser=convertView.findViewById(R.id.message_user);
            viewHolder.messageTime=convertView.findViewById(R.id.message_time);
            viewHolder.imgAvarta=convertView.findViewById(R.id.iv_avatar);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        ChatMessage chatMessage=objects.get(position);
        viewHolder.messageText.setText(chatMessage.getMessageText());
        viewHolder.messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                chatMessage.getMessageTime()));
        viewHolder.messageUser.setText(chatMessage.getMessageUser());
        Picasso.with(convertView.getContext()).load(chatMessage.getUrlImage()).into(viewHolder.imgAvarta);

        return convertView;
    }
    public static class ViewHolder{
        TextView messageText;
        TextView messageUser;
        TextView messageTime;
        ImageView imgAvarta;
    }
}