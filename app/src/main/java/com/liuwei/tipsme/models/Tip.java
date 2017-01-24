package com.liuwei.tipsme.models;

import com.liuwei.tipsme.DataBase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuwei on 2016/4/3.
 */
public class Tip {
    public static final Integer GuardPosition = 0;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Date getCreateTime() {
        return createTime;
    }

    public String getCreateTimeString() {
        return dateToString(this.createTime);
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setCreateTime(String createTimeString) {
        this.createTime = dateFromString(createTimeString);
    }

    public TipStatus getStatus() {
        return status;
    }

    public String getStatusString() {
        return status.name();
    }

    public void setStatus(TipStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = TipStatus.valueOf(status);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPlanToStartTime() {
        return planToStartTime;
    }

    public String getPlanToStartTimeString() {
        return dateToString(this.planToStartTime);
    }

    public void setPlanToStartTime(Date planToStartTime) {
        this.planToStartTime = planToStartTime;
    }

    public void setPlanToStartTimeFromChinese(String strDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            String ss = strDate.replace("年", "-").replace("月", "-").replace("日", "");
            if (ss.split(":").length == 2) {
                ss = ss.concat(":00");
            }
            date = sdf.parse(ss);
        } catch (Exception e) {
            date = null;
        }
        this.planToStartTime = date;
    }

    public void setPlanToStartTime(String planToStartTimeString) {
        this.planToStartTime = dateFromString(planToStartTimeString);
    }

    private Date dateFromString(String dateString) {
        if (null == dateString || "null" == dateString) {
            return null;
        }
        try {
            return sdf.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }

    private String dateToString(Date date) {
        if (null == date) {
            return "null";
        }
        return sdf.format(date);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    private Integer id;
    private TipStatus status;
    private Integer position;
    private String content;
    private Date planToStartTime;
    private Date createTime;


    public Tip(String content) {
        this.id = null;
        this.content = content;
        this.position = Tip.GuardPosition;
        this.status = TipStatus.TODO;
        this.planToStartTime = null;
        this.createTime = new Date();
    }

    public static boolean SaveStatus(Integer id, TipStatus status) {
        return DataBase.UpdateTableById("tips", id, "status", status.name());
    }

    private static boolean SavePosition(Integer id, Integer position) {
        return DataBase.UpdateTableById("tips", id, "position", position.toString());
    }

    public static boolean SwapPosition(Integer from, Integer to) {
        if (from == to) return true;
        if (from == Tip.GuardPosition || to == Tip.GuardPosition) return true;

        try {
            Tip fromTip = DataBase.GetByPosition(from);
            for (int i = from + 1; i <= to; i++) {
                Tip tip = DataBase.GetByPosition(i);
                Tip.SavePosition(tip.id, from < to ? i - 1 : i + 1);
            }
            return Tip.SavePosition(fromTip.id, to);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean RemoveByPosition(Integer position) {
        try {
            return DataBase.DeleteByPosition(position);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean Create() {
        return DataBase.Insert(this);
    }

}
