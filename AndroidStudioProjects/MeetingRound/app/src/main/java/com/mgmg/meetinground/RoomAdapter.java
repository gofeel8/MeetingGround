package com.mgmg.meetinground;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.Calendar;
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
        LinearLayout roomLayout = view.findViewById(R.id.roomLayout);


        LinearLayout dateLayout = view.findViewById(R.id.dateLayout);
//        dateLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.share_round_item));

        if(list.get(position).getCalendar().before(Calendar.getInstance())){
            dateLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.share_round_item_gray));
            roomLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.share_round_list_gray));
        }else{
            dateLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.share_round_item));
            roomLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.share_round_list));
        }

        TextView tvRoomId = view.findViewById(R.id.tvRoomId);
        tvRoomId.setText(list.get(position).getRoomName());
        TextView tvDate = view.findViewById(R.id.tvDate);
        tvDate.setText(list.get(position).getDate());
        TextView tvTime = view.findViewById(R.id.tvTime);
        tvTime.setText(list.get(position).getTime());
        return view;
    }
}
