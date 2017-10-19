package com.yh.yhchanneledit.Widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.GridView;
import android.widget.TextView;

import com.yh.yhchanneledit.Adapters.GridAdapter;
import com.yh.yhchanneledit.Utils.ListHolder;
import com.yh.yhchanneledit.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by YH on 2017/10/13.
 *
 * 频道编辑页弹窗
 */

public class EditDialog extends Dialog {
    private Activity activity;

    //  ListHolder 实例用于保存 我的频道 及 频道推荐 列表内容
    private ListHolder listHolder = ListHolder.getInstance();
    //  我的频道 adapter
    private GridAdapter mineAdapter;
    //  频道推荐 adapter
    private GridAdapter recommendAdapter;

    public EditDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        this.activity = (Activity) context;
    }

    @BindView(R.id.text_edit)
    TextView textEdit;
    @BindView(R.id.grid_mine)
    ChannelGridView gridMine;
    @BindView(R.id.grid_recommend)
    GridView gridRecommend;

    @OnClick(R.id.text_cancel)
    void textCancel() {
        this.dismiss();
    }

    @OnClick(R.id.text_edit) void textEdit() {
        if (mineAdapter.isEditable()) {
            textEdit.setText("编辑");
            mineAdapter.setEditable(false);
            mineAdapter.moveNotifyDataSetChanged(false, -1);
        } else {
            textEdit.setText("完成");
            mineAdapter.setEditable(true);
            mineAdapter.notifyDataSetChanged();
        }
    }

    @OnItemClick(R.id.grid_recommend) void gridRecommend(int position) {
        String string = listHolder.getRecommendList().get(position);
        //  我的频道中增加标签
        listHolder.getMineList().add(string);
        //  频道推荐中删除标签
        listHolder.getRecommendList().remove(position);

        //  更新各频道数据
        mineAdapter.moveNotifyDataSetChanged(false, -1);
        recommendAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit);

        ButterKnife.bind(this);

        //  测试数据
        List<String> mineList = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            mineList.add("ITEM" + i);
        }
        List<String> recommendList = new ArrayList<>();
        for (int i = 10; i < 30; ++i) {
            recommendList.add("ITEM" + i);
        }

        //  listHolder 设置相关列表内容
        listHolder.setMineList(mineList);
        listHolder.setRecommendList(recommendList);

        mineAdapter = new GridAdapter(activity, listHolder.getMineList());
        recommendAdapter = new GridAdapter(activity, listHolder.getRecommendList());

        mineAdapter.setParams(mineAdapter, recommendAdapter);

        gridMine.setAdapter(mineAdapter);
        gridMine.setParams(this, textEdit, mineAdapter);
        gridRecommend.setAdapter(recommendAdapter);
    }
}
