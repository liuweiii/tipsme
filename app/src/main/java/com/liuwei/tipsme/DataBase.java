package com.liuwei.tipsme;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liuwei.tipsme.models.Tip;
import com.liuwei.tipsme.models.TipStatus;

import java.util.ArrayList;
import java.util.List;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by liuwei on 2016/4/3.
 */
public class DataBase {
    private static SQLiteDatabase db;

    private DataBase() {
    }

    private static void initTestData() {
        Tip tip1 = new Tip("1");
        Tip tip2 = new Tip("2");
        Tip tip3 = new Tip("3");
        Tip tip4 = new Tip("4");

        tip1.setStatus(TipStatus.TODO);
        tip2.setStatus(TipStatus.DOING);
        tip3.setStatus(TipStatus.DONE);
        tip4.setStatus(TipStatus.TODO);

        tip1.setPlanToStartTime("2016-04-05 16:30:00");
        tip2.setPlanToStartTime("2016-04-03 16:30:00");
        tip3.setPlanToStartTime("2016-04-01 16:30:00");
        tip4.setPlanToStartTime("2016-04-06 16:30:00");

        tip1.Create();
        tip2.Create();
        tip3.Create();
        tip4.Create();
    }

    public static void InitDB() {
        CreateDataBase();
        CreateTable();
        initTestData();
//        initTestData();
    }

    private static void CreateDataBase() {
        db = openOrCreateDatabase("/data/data/com.liuwei.tipsme/tipsme.db", null, null);
    }

    private static void CreateTable() {
        db.execSQL("CREATE TABLE IF NOT EXISTS tips (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "status VARCHAR, " +
                "content VARCHAR, " +
                "planToStartTime VARCHAR, " +
                "position INTEGER, " +
                "createTime VARCHAR)");
    }

    private static Integer count(String whereClause, String[] whereArgs) {
        db.beginTransaction();
        int count = 0;
        try {
            Cursor c = db.rawQuery("select count(*) from tips WHERE " + whereClause, whereArgs);
            if (c.moveToFirst()) {
                count = c.getInt(0);
            }
            c.close();
        }finally {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return count;
    }

    private static List<Tip> getList(String whereClause, String[] whereArgs) {
        Cursor c = db.rawQuery("select * from tips WHERE " + whereClause + " ORDER BY position desc", whereArgs);
        List<Tip> tips = new ArrayList<>();

        for (; c.moveToNext(); ) {
            Tip tip = new Tip(c.getString(c.getColumnIndex("content")));
            tip.setId(c.getInt(c.getColumnIndex("id")));
            tip.setStatus(c.getString(c.getColumnIndex("status")));
            tip.setPlanToStartTime(c.getString(c.getColumnIndex("planToStartTime")));
            tip.setCreateTime(c.getString(c.getColumnIndex("createTime")));
            tip.setPosition(c.getInt(c.getColumnIndex("position")));
            tips.add(tip);
        }
        return tips;
    }

    public static List<Tip> GetAll() {
        return getList("1=1", null);
    }

    public static List<Tip> GetList(TipStatus status) {
        return getList("status='" + status.name() + "'", null);
    }

    public static Tip GetByPosition(Integer position) {
        List<Tip> tips = getList(" position=" + position.toString(), null);
        if (tips.size() == 0) {
            return null;
        }
        return tips.get(0);
    }

    public static boolean UpdateTableById(String table, Integer id, String field, String value) {
        db.beginTransaction();
        long result = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(field, value);
            result = db.update(table, values, "id = ?", new String[]{id.toString()});
        }finally {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return 1 == result;
    }

    private static ContentValues makeContentValues(Tip tip) {
        ContentValues values = new ContentValues();
        values.put("status", tip.getStatusString());
        values.put("content", tip.getContent());
        values.put("planToStartTime", tip.getPlanToStartTimeString());
        values.put("createTime", tip.getCreateTimeString());
        Integer position = count("1=1", null) + 1;//guard is already in.
        values.put("position", position);
        return values;
    }

    public static boolean Insert(Tip tip) {
        db.beginTransaction();
        long result =  -1;
        try {
            result = db.insert("tips", null, makeContentValues(tip));
        }finally {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return -1 != result;
    }

    public static boolean Update(Integer id, Tip tip) {
        db.beginTransaction();
        long result = -1;
        try {
            result = db.update("tips", makeContentValues(tip), "id = ?",
                    new String[]{id.toString()});
        }finally {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return 1 == result;
    }

    public static boolean DeleteByPosition(Integer position) {
        db.beginTransaction();
        try {
            if (1 == db.delete("tips", "position=?", new String[]{position.toString()})) {// delete succeed, then decrease bigger positions
                db.execSQL(" update tips set position = position - 1 where position > " + position);
                return true;
            }
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return false;
    }
}
