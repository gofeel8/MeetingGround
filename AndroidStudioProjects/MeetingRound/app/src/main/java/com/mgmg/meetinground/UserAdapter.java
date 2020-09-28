package com.mgmg.meetinground;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<UserDto> list;

    public UserAdapter(Context context, List<UserDto> list) {
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
        View view = layoutInflater.inflate(R.layout.list_user, null);

        ImageView ivProfile = view.findViewById(R.id.ivProfile2);
        TextView tvName = view.findViewById(R.id.tvName2);
        ImageView ivHost = view.findViewById(R.id.ivHost);

        TextView invest = view.findViewById(R.id.tvName3);

        Glide.with(context).load(list.get(position).getProfile()).into(ivProfile);
        tvName.setText(list.get(position).getName());
        invest.setText(Integer.toString(list.get(position).getInvest()));
        if (position == 0)
            ivHost.setVisibility(View.VISIBLE);

        return view;
    }
}
