package com.example.myapplication;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(MainActivity context) {
        super(context, "MainDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE DiaryTable ( Diary TEXT, FaceAnalysResult REAL, FacePhotoPath CHAR(30), SerialId INTEGER PRIMARY KEY);");
        db.execSQL("CREATE TABLE StoryTable ( Story TEXT, StoryViewState INTEGER, RecommandMovie CHAR(20), RecommandMusic CHAR(20), RecommandBook CHAR(20), SerialId INTEGER PRIMARY KEY);");
        db.execSQL("CREATE TABLE AppSettingTable ( TimeSetting INTEGER, RecordAgreed Boolean, SerialId INTEGER PRIMARY KEY);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DiaryTable");
        db.execSQL("DROP TABLE IF EXISTS StoryTable");
        db.execSQL("DROP TABLE IF EXISTS AppSettingTable");
        onCreate(db);
    }
}

public class DatabaseService {
    private DBOpenHelper dbOpenHelper;

    public DatabaseService(MainActivity context) {
        dbOpenHelper = new DBOpenHelper(context);
    }

    public void dropTable(String option) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("DROP TABLE IF EXISTS " + option + ";");
    }

    public void closeDatabase() {
        dbOpenHelper.getWritableDatabase().close();
    }

    public void createDiaryTable() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS DiaryTable ( Diary TEXT, FaceAnalysResult REAL, FacePhotoPath CHAR(30), SerialId INTEGER PRIMARY KEY);");
        closeDatabase();
    }

    public void createStoryTable() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS StoryTable ( Story TEXT, StoryViewState INTEGER, RecommandMovie CHAR(20), RecommandMusic CHAR(20), RecommandBook CHAR(20), SerialId INTEGER PRIMARY KEY);");
        closeDatabase();
    }

    public void createAppSettingTable() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS AppSettingTable ( TimeSetting INTEGER, RecordAgreed Boolean, SerialId INTEGER PRIMARY KEY);");
        closeDatabase();
    }

    public void saveDiaryInfo() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        try {
            writer.execSQL("INSERT INTO DiaryTable (Diary, FaceAnalysResult, FacePhotoPath) values (?, ?, ?, ?)", new Object[]{"Today's Diary", 20.3, "Photo's Path", 1});
        } catch(SQLException e) {
            Log.d("please", e.toString());

        }
        closeDatabase();
    }

    public void saveStoryInfo(int id) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        try {
            writer.execSQL("INSERT INTO StoryTable (Story, StoryViewState, RecommandMovie, RecommandMusic, RecommandBook, SerialId) values (?, ?, ?, ?, ?, ?)", new Object[]{"Nice Story", 14 + (int)(Math.random() * 2), "Very Cool Movie", "Very Graceful Music", "Very Stable Book", id});
        } catch (SQLException e) {
            Log.d("please", e.toString());
        }
        closeDatabase();
    }

    public void FirstAppSettingInfo() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        try {
            writer.execSQL("INSERT INTO AppSettingTable (TimeSetting, RecordAgreed, SerialId) values (?, ?, ?)", new Object[] {1822, true, 1});
        } catch(SQLException e) {
            Log.d("please", e.toString());
        }
        closeDatabase();
    }

    public void updateAppSettingInfo(int newTime, boolean agreed) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        try {
            writer.execSQL("UPDATE AppSettingTable set TimeSetting = ?, RecordAgreed = ? WHERE AppSettingTable.SerialId = 1", new Object[]{newTime, agreed});
        } catch(SQLException e) {
            Log.d("please", e.toString());
        }
        closeDatabase();
    }

    public void DBUpgrade(int oldVersion, int newVersion) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.onUpgrade(writer, oldVersion, newVersion);
    }

    public Cursor getAllDiary() {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM DiaryTable;", null);

        return cursor;
    }

    public Cursor getStoryKeyByValue(int option) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        String lower = Integer.toString(option - 3);
        String upper = Integer.toString(option + 3);
        Cursor cursor = reader.rawQuery("SELECT SerialId FROM StoryTable WHERE StoryTable.StoryViewState >= " + lower + " AND StoryViewState <= " + upper + ";" , null);

        return cursor;
    }

    public Cursor getStoryById(int key) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM StoryTable WHERE StoryTable.SerialId = " + Integer.toString(key) +  ";" , null);

        return cursor;
    }

    public Cursor getTime() {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT TimeSetting FROM AppSettingTable WHERE AppSettingTable.SerialId = 1;", null);

        return cursor;
    }
}
