package com.mgmg.meetinground;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.ViewHolder>{
    ArrayList<Restaurant> items = new ArrayList<Restaurant>();

    OnRestaurantClickListener listener;

    public void addItem(Restaurant item){
        items.add(item);
    }

    public void setItems(ArrayList<Restaurant> items){
        this.items = items;
    }

    public Restaurant getItem(int position){
        return items.get(position);
    }

    public void setItem(int position,Restaurant item){
        items.set(position,item);
    }

    public void setOnItemClickListener(OnRestaurantClickListener listener){
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView =  inflater.inflate(R.layout.restaurant_item,parent,false);

        return new ViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant item = items.get(position);
        holder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView agree;
        TextView disagree;
        TextView RestaurantName;


        public ViewHolder(@NonNull View itemView,final OnRestaurantClickListener listener) {
            super(itemView);
            agree= itemView.findViewById(R.id.agree);
            disagree= itemView.findViewById(R.id.disagree);
            RestaurantName= itemView.findViewById(R.id.RestaurantName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.OnItemClick(ViewHolder.this,v,position);
                    }
                }
            });
        }

        public void setItem (Restaurant item){
            RestaurantName.setText(item.getName());
            agree.setText(Integer.toString(item.getAgree()));
            disagree.setText(Integer.toString(item.getDisagree()));
        }
    }


}
