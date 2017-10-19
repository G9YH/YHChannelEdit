package com.yh.yhchanneledit.Widgets;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.yh.yhchanneledit.Adapters.GridAdapter;
import com.yh.yhchanneledit.Application.YHApplication;
import com.yh.yhchanneledit.R;
import com.yh.yhchanneledit.Utils.ListHolder;
import com.yh.yhchanneledit.Utils.PermissionGetter;

/**
 * Created by YH on 2017/10/13.
 */

public class ChannelGridView extends GridView
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final int MODE_EDIT = 1;
    private static final int MODE_NORMAL = 2;

    private int dragPosition;
    private int originPosition;
    private int mode = MODE_NORMAL;
    private float x;
    private float y;
    private float screenX;
    private float screenY;

    private View dragView;
    private View originView;
    private TextView textEdit;
    private EditDialog editDialog;
    private GridAdapter mineAdapter;
    private WindowManager windowManager;
    private PermissionGetter permissionGetter;
    private WindowManager.LayoutParams layoutParams;
    private ChannelAnimationListener animationListener;
    private ListHolder listHolder = ListHolder.getInstance();

    public ChannelGridView(Context context) {
        super(context);
        init();
    }

    public ChannelGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        YHApplication application = YHApplication.getApplication();
        this.windowManager = application.getWindowManager();
        this.animationListener = new ChannelAnimationListener();
        this.permissionGetter = new PermissionGetter(getContext());
        this.setOnItemClickListener(this);
        this.setOnItemLongClickListener(this);
    }

    public void setParams(EditDialog addDialog, TextView textEdit, GridAdapter mineAdapter) {
        this.editDialog = addDialog;
        this.textEdit = textEdit;
        this.mineAdapter = mineAdapter;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (!mineAdapter.isEditable()) {
            editDialog.dismiss();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        //  已处于移动模式
        if (mode == MODE_EDIT) {
            return false;
        }

        textEdit.setText("完成");
        mineAdapter.setEditable(true);
        mineAdapter.moveNotifyDataSetChanged(true, i);

        //  推荐标签无法移动或删除
        if (i == 0) {
            return false;
        }

        //  判断并获取弹窗权限
        permissionGetter.alertWindowPermission();

        originView = view;
        dragPosition = i;
        originPosition = i;
        x = screenX - view.getLeft() - this.getLeft();
        y = screenY - view.getTop() - this.getTop();

        //  初始化弹窗
        initWindow();

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == MODE_EDIT) {
                    updateWindow(motionEvent);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mode == MODE_EDIT) {
                    closeWindow();
                }
                break;
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                screenX = motionEvent.getRawX();
                screenY = motionEvent.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    /**
     * 初始化window
     */
    private void initWindow() {
        if (dragView == null) {
            dragView = View.inflate(getContext(), R.layout.dialog_edit_item, null);
            TextView textView = (TextView) dragView.findViewById(R.id.text_content);
            textView.setText(((TextView) originView.findViewById(R.id.text_content)).getText());
        }

        if (layoutParams == null) {
            layoutParams = new WindowManager.LayoutParams();
            layoutParams.width = originView.getWidth();
            layoutParams.height = originView.getHeight();
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity = Gravity.TOP | Gravity.START;
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            layoutParams.flags =
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.x = originView.getLeft() + this.getLeft();
            layoutParams.y = originView.getTop() + this.getTop();
            originView.setVisibility(GONE);
        }

        windowManager.addView(dragView, layoutParams);
        mode = MODE_EDIT;
    }

    /**
     * 触摸移动时，更新window
     */
    private void updateWindow(MotionEvent motionEvent) {
        if (mode == MODE_EDIT) {
            float x = motionEvent.getRawX() - this.x;
            float y = motionEvent.getRawY() - this.y;

            if (layoutParams != null) {
                layoutParams.x = (int) x;
                layoutParams.y = (int) y;
                windowManager.updateViewLayout(dragView, layoutParams);
            }

            float mx = motionEvent.getX();
            float my = motionEvent.getY();
            int dropPosition = pointToPosition((int) mx, (int) my);
            if (dropPosition == dragPosition || dropPosition == GridView.INVALID_POSITION) {
                return;
            }

            //  推荐标签无法移动
            if (dropPosition != 0) {
                itemMove(dropPosition);
            }
        }
    }

    /**
     * 关闭window
     */
    private void closeWindow() {
        if (dragView != null) {
            windowManager.removeView(dragView);
            dragView = null;
            layoutParams = null;
        }
        itemDown();
    }

    /**
     * 判断item移动
     *
     * @param dropPosition 移动位置
     */
    private void itemMove(int dropPosition) {
        TranslateAnimation translateAnimation;
        if (dropPosition < dragPosition) {
            for (int i = dropPosition; i < dragPosition; i++) {
                View view = getChildAt(i);
                View nextView = getChildAt(i + 1);
                float xValue = (nextView.getLeft() - view.getLeft()) * 1f / view.getWidth();
                float yValue = (nextView.getTop() - view.getTop()) * 1f / view.getHeight();
                translateAnimation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, xValue,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, yValue);
                translateAnimation.setInterpolator(new LinearInterpolator());
                translateAnimation.setFillAfter(true);
                translateAnimation.setDuration(300);
                if (i == dragPosition - 1) {
                    translateAnimation.setAnimationListener(animationListener);
                }
                view.startAnimation(translateAnimation);
            }
        } else {
            for (int i = dragPosition + 1; i <= dropPosition; i++) {
                View view = getChildAt(i);
                View prevView = getChildAt(i - 1);
                float xValue = (prevView.getLeft() - view.getLeft()) * 1f / view.getWidth();
                float yValue = (prevView.getTop() - view.getTop()) * 1f / view.getHeight();
                translateAnimation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, xValue,
                        Animation.RELATIVE_TO_SELF, 0f,
                        Animation.RELATIVE_TO_SELF, yValue);
                translateAnimation.setInterpolator(new LinearInterpolator());
                translateAnimation.setFillAfter(true);
                translateAnimation.setDuration(300);
                if (i == dropPosition) {
                    translateAnimation.setAnimationListener(animationListener);
                }
                view.startAnimation(translateAnimation);
            }
        }
        dragPosition = dropPosition;
    }

    /**
     * 手指抬起时，item下落
     */
    private void itemDown() {
        mode = MODE_NORMAL;
        if (dragPosition == originPosition || dragPosition == GridView.INVALID_POSITION) {
            getChildAt(originPosition).setVisibility(VISIBLE);
        } else {
            listChange();
            mineAdapter.moveNotifyDataSetChanged(false, dragPosition);
        }
    }

    /**
     * 更新标签及fragment列表
     */
    private void listChange() {
        String string = listHolder.getMineList().get(originPosition);
        listHolder.getMineList().remove(originPosition);
        listHolder.getMineList().add(dragPosition, string);
    }

    /**
     * 动画监听器
     */
    private class ChannelAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            listChange();
            mineAdapter.moveNotifyDataSetChanged(true, dragPosition);
            originPosition = dragPosition;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
