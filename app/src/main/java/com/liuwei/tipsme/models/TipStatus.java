package com.liuwei.tipsme.models;

import com.liuwei.tipsme.R;

/**
 * Created by liuwei on 2016/4/3.
 */
public enum TipStatus {
    TODO,
    DOING,
    DONE,
    GARBAGE;

    public static int getDrawable(TipStatus status) {
        switch (status) {
            case TODO:
                return R.drawable.tips_status_todo;
            case DOING:
                return R.drawable.tips_status_doing;
            case DONE:
                return R.drawable.tips_status_done;
            case GARBAGE:
            default:
                return R.drawable.tips_status_garbage;
        }
    }
}
