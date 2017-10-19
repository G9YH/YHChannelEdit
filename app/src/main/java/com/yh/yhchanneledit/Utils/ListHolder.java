package com.yh.yhchanneledit.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YH on 2017/10/14.
 */

public class ListHolder {
    private List<String> mineList = new ArrayList<>();
    private List<String> recommendList = new ArrayList<>();

    private static class Instance {
        private static ListHolder instance = new ListHolder();
    }

    private ListHolder() {
    }

    public static ListHolder getInstance() {
        return Instance.instance;
    }

    public List<String> getMineList() {
        return mineList;
    }

    public void setMineList(List<String> mineList) {
        this.mineList = mineList;
    }

    public List<String> getRecommendList() {
        return recommendList;
    }

    public void setRecommendList(List<String> recommendList) {
        this.recommendList = recommendList;
    }
}
