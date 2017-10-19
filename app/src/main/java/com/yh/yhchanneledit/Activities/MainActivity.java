package com.yh.yhchanneledit.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yh.yhchanneledit.Application.YHApplication;
import com.yh.yhchanneledit.R;
import com.yh.yhchanneledit.Widgets.EditDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @OnClick(R.id.button_edit)
    void buttonAdd() {
        //  弹窗显示频道编辑页
        EditDialog dialog = new EditDialog(this);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //  设置main activity的window manager
        YHApplication application = YHApplication.getApplication();
        application.setWindowManager(this.getWindowManager());
    }
}
