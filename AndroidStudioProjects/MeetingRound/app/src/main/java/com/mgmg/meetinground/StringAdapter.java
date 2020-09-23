package com.mgmg.meetinground;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;

public class StringAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<String> list;
    private int height, divider;
    private HashSet<String> set;

    public StringAdapter(Context context, List<String> list, int height, int divider, HashSet<String> set) {
        this.context = context;
        this.list = list;
        this.layoutInflater = LayoutInflater.from(context);
        this.height = height;
        this.divider = divider;
        this.set = set;
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
        View view = layoutInflater.inflate(R.layout.list_string, null);

        TextView tvString = view.findViewById(R.id.tvString);
        tvString.setText(list.get(position));
        tvString.setHeight(height / list.size() - divider);

        if (set.contains(list.get(position))) {
            tvString.setTextColor(Color.BLUE);
        }

        return view;
    }
}
