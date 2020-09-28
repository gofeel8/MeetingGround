package com.mgmg.meetinground;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StringAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private String[] keys;
    private boolean[] checked;

    public StringAdapter(Context context, String[] keys, boolean[] checked) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.keys = keys;
        this.checked = checked;
    }

    @Override
    public int getCount() {
        return keys.length;
    }

    @Override
    public Object getItem(int position) {
        return keys[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.list_string, null);

        TextView tvString = view.findViewById(R.id.tvString);
        tvString.setText(keys[position]);

        if (checked[position]) {
            tvString.setTextColor(Color.BLUE);
        }

        return view;
    }
}
