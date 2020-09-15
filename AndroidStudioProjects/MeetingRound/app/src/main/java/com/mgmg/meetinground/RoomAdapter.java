package com.mgmg.meetinground;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class RoomAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<RoomDto> list;

    public RoomAdapter(Context context, List<RoomDto> list) {
        this.context = context;
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.list_room, null);

        TextView tvRoomId = view.findViewById(R.id.tvRoomId);
        tvRoomId.setText(list.get(position).getRoomName());

        return view;
    }
}
