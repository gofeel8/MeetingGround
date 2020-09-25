package com.mgmg.meetinground;

import android.view.View;

public interface OnRestaurantClickListener {
    public void OnItemClick(VoteAdapter.ViewHolder holder, View view, int position);
}
