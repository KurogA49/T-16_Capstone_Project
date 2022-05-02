package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(MainActivity context) {
        super(context, "MainDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE DiaryTable ( Diary TEXT, FaceAnalysResult REAL, FacePhotoPath CHAR(30));");
        db.execSQL("CREATE TABLE StoryTable ( Story TEXT, StoryViewState INTEGER, RecommandMovie CHAR(20), RecommandMusic CHAR(20), RecommandBook CHAR(20));");
        db.execSQL("CREATE TABLE AppSettingTable ( TimeSetting INTEGER, RecordAgreed Boolean);");
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
        writer.execSQL("CREATE TABLE IF NOT EXISTS DiaryTable ( Diary TEXT, FaceAnalysResult REAL, FacePhotoPath CHAR(30));");
        closeDatabase();
    }

    public void createStoryTable() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS StoryTable ( Story TEXT, StoryViewState INTEGER, RecommandMovie CHAR(20), RecommandMusic CHAR(20), RecommandBook CHAR(20));");
        closeDatabase();
    }

    public void createAppSettingTable() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS AppSettingTable ( TimeSetting INTEGER, RecordAgreed Boolean);");
        closeDatabase();
    }

    public void saveDiaryInfo() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO DiaryTable (Diary, FaceAnalysResult, FacePhotoPath) values (?, ?, ?)", new Object[]{"Today's Diary", 20.3, "Photo's Path"});
        closeDatabase();
    }

    public void saveStoryInfo() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO StoryTable (Story, StoryViewState, RecomandMovie, RecommandMusic, RecommandBook) values (?, ?, ?, ?, ?)", new Object[]{"Nice Story", 15, "Very Cool Movie", "Very Graceful Music", "Very Stable Book"});
        closeDatabase();
    }

    public void AppSettingInfo(int time, boolean agreed) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("UPDATE AppSettingTable set TimeSetting = ?, RecordAgreed = ?", new Object[]{time, agreed});
        closeDatabase();
    }

    public Cursor getAllDiary() {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM DiaryTable;", null);

        return cursor;
    }

    public Cursor getConditionalStory(int option) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        String lower = Integer.toString(option - 3);
        String upper = Integer.toString(option + 3);
        Cursor cursor = reader.rawQuery("SELECT * FROM StoryTable WHERE StoryTable >= " + lower + " AND StoryTable <= " + upper, null);

        return cursor;
    }
}
