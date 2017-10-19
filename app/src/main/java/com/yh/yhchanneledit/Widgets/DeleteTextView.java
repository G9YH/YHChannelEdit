package com.yh.yhchanneledit.Widgets;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import com.yh.yhchanneledit.Adapters.GridAdapter;
import com.yh.yhchanneledit.Utils.ListHolder;
import com.yh.yhchanneledit.R;

/**
 * Created by YH on 2017/10/14.
 */

public class DeleteTextView extends android.support.v7.widget.AppCompatTextView {
    private ListHolder listHolder = ListHolder.getInstance();
    private DeleteTextView instance;

    private boolean adapter = false;

    private GridAdapter mineAdapter;
    private GridAdapter recommendAdapter;

    public DeleteTextView(Context context) {
        super(context);
        init();
    }

    public DeleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.instance = this;
        this.setGravity(Gravity.CENTER);
        this.setText("X");
        this.setTextColor(Color.parseColor("#FFFFFF"));
        this.setTextSize(8);
        this.setBackground(
                ContextCompat.getDrawable(getContext(), R.drawable.shape_dialog_edit_item_delete));
        this.setVisibility(GONE);
    }

    public boolean isAdapter() {
        return adapter;
    }

    public void setAdapter(boolean adapter) {
        this.adapter = adapter;
    }

    public void setAdapter(GridAdapter mineAdapter, GridAdapter recommendAdapter) {
        this.mineAdapter = mineAdapter;
        this.recommendAdapter = recommendAdapter;
        this.adapter = true;
    }

    public void setPosition(int position) {
        this.instance.setOnClickListener(new DeleteClickListener(position));
    }

    private class DeleteClickListener implements OnClickListener {
        private int position;

        private DeleteClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            String string = listHolder.getMineList().get(position);
            listHolder.getMineList().remove(position);
            listHolder.getRecommendList().add(0, string);

            mineAdapter.moveNotifyDataSetChanged(false, -1);
            recommendAdapter.notifyDataSetChanged();
        }
    }
}
