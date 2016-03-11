package com.ggalia84.ggalia84.todos;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<TodoItem> list;
    private final LayoutInflater layputInflater;

    public CustomListAdapter(Context context, ArrayList listData) {
        this.context = context;
        this.list = listData;
        layputInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = layputInflater.inflate(R.layout.todolistitem, null);
        } else {

        }

        final TextView tv = (TextView) convertView.findViewById(R.id.todolistitemtext);
        tv.setText(list.get(position).getName());

        final CheckBox taskDone = (CheckBox) convertView.findViewById(R.id.taskdone);


        String priorityText = "";

        if(list.get(position).getPriority() == 1){
            priorityText = "Baja";
        }
        if (list.get(position).getPriority() == 2){
            priorityText = "Normal";
        }
        if (list.get(position).getPriority() == 3){
            priorityText = "Alta";
        }

        taskDone.setChecked(list.get(position).isDone());

        tv.setText(tv.getText() +
                " [" + priorityText + "]");

               // +" d:" +list.get(position).isDone());

        showDone(tv,taskDone, position);

        taskDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDone(tv,taskDone, position);
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).editTask(position);
            }
        });

        return convertView;
    }

    private void showDone(TextView tv, CheckBox done, int position)
    {
        if (!done.isChecked()) {
            done.setChecked(false);
            list.get(position).setDone(false);
            tv.setPaintFlags(0);
        } else {
            done.setChecked(true);
            list.get(position).setDone(true);
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}