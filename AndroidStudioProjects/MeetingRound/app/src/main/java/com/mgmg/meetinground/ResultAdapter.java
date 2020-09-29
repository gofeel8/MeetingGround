package com.mgmg.meetinground;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ResultAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<Restaurant> list;

    public ResultAdapter(Context context, List<Restaurant> list) {
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
        View view = layoutInflater.inflate(R.layout.list_result, null);

        TextView tvResultName = view.findViewById(R.id.tvResultName);
        TextView tvResultTag = view.findViewById(R.id.tvRtag);

        tvResultName.setText(list.get(position).getName());

        StringBuilder sb = new StringBuilder();

        for (String c : list.get(position).getCategory_list()) {
            sb.append("#");
            sb.append(c);
            sb.append("  ");
        }

        for (String tag : list.get(position).getTags()) {
            sb.append("#");
            sb.append(tag);
            sb.append("  ");
        }

        tvResultTag.setText(sb.toString());

        return view;
    }
}
