package com.yh.yhchanneledit.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yh.yhchanneledit.R;
import com.yh.yhchanneledit.Widgets.DeleteTextView;

import java.util.List;

/**
 * Created by YH on 2017/10/13.
 */

public class GridAdapter extends BaseAdapter {
    private Activity activity;
    private List<String> list;

    //  当前移动的频道
    private int moveChannel = -1;
    //  是否可移动
    private boolean movable = false;
    //  是否可编辑
    private boolean editable = false;

    //  灰色字体
    private int textGrey;
    //  黑色字体
    private int textBlack;

    private GridAdapter mineAdapter;
    private GridAdapter recommendAdapter;

    public GridAdapter(Activity activity, List<String> list) {
        this.activity = activity;
        this.list = list;
        this.textGrey = Color.parseColor("#999999");
        this.textBlack = Color.parseColor("#000000");
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
        String string = list.get(position);

        // 初始化layout
        convertView = activity.getLayoutInflater().inflate(R.layout.dialog_edit_item, null);
        TextView textContent = (TextView) convertView.findViewById(R.id.text_content);
        DeleteTextView textDelete = (DeleteTextView) convertView.findViewById(R.id.text_delete);

        textContent.setText(string);
        if (!textDelete.isAdapter()) {
            textDelete.setAdapter(mineAdapter, recommendAdapter);
        }
        textDelete.setPosition(position);

        if (editable) {
            if (position == 0) {
                textContent.setTextColor(textGrey);
                textDelete.setVisibility(View.GONE);
            } else {
                textContent.setTextColor(textBlack);
                textDelete.setVisibility(View.VISIBLE);
            }
        } else {
            textDelete.setVisibility(View.GONE);
        }

        //  移动item
        if (movable && position == moveChannel && position != 0) {
            convertView.setVisibility(View.GONE);
        }

        return convertView;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * 设置相关参数
     *
     * @param mineAdapter      我的频道
     * @param recommendAdapter 频道推荐
     */
    public void setParams(GridAdapter mineAdapter, GridAdapter recommendAdapter) {
        this.mineAdapter = mineAdapter;
        this.recommendAdapter = recommendAdapter;
    }

    /**
     * 给item交换位置
     *
     * @param movable  item是否可移动
     * @param position item现在位置
     */
    public void moveNotifyDataSetChanged(boolean movable, int position) {
        this.movable = movable;
        this.moveChannel = position;
        notifyDataSetChanged();
    }
}
