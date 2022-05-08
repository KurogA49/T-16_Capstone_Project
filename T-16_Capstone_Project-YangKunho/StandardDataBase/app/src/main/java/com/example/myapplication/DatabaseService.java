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
        db.execSQL("CREATE TABLE diarydb ( diaryKey, INTEGER PRIMARY KEY NOT NULL, facePhotoPath TEXT);");
        db.execSQL("CREATE TABLE diarycontentsdb ( contentKey INTEGER PRIMARY KEY NOT NULL, diaryKey INTEGR NOT NULL, diaryContents TEXT, FOREIGN KEY(diaryKey) REFERENCES diarydb(diaryKey));");
        db.execSQL("CREATE TABLE analysisresultdb ( diaryKey INTEGER PRIMARY KEY, anger REAL, contempt REAL, disgust REAL, fear REAL, happiness REAL, neutral REAL, " +
                                                    "sadness REAL, surprise REAL, emotion TEXT NOT NULL, FOREIGN KEY(diaryKey) REFERENCES diarydb(diaryKey));");
        db.execSQL("CREATE TABLE storydb ( storyKey INTEGER PRIMARY KEY NOT NULL, emotionClass TEXT NOT NULL, callCount INTEGER NOT NULL);");
        db.execSQL("CREATE TABLE storycontentsdb ( contentKey INTEGER PRIMARY KEY NOT NULL, storyKey INTEGER NOT NULL, storyContents TEXT, storyViewState INTEGER, FOREIGN KEY(storyKey) REFERENCES storydb(storyKey));");
        db.execSQL(("CREATE TABLE recommendeddb ( recommendKey INTEGER PRIMARY KEY NOT NULL, emotionClass TEXT NOT NULL, content TEXT, callCount INTEGER NOT NULL)"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS diarydb");
        db.execSQL("DROP TABLE IF EXISTS diarycontentsdb");
        db.execSQL("DROP TABLE IF EXISTS analysisresultdb");
        db.execSQL(("DROP TABLE IF EXISTS storydb"));
        db.execSQL(("DROP TABLE IF EXISTS storycontentsdb"));
        db.execSQL(("DROP TABLE IF EXISTS recommendeddb"));
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

    /*-------------테이블 생성 메소드--------------*/

    public void createDiaryDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS diarydb ( diaryKey, INTEGER PRIMARY KEY NOT NULL, facePhotoPath TEXT);");
        closeDatabase();
    }

    public void createDiaryContentsDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS diarycontentsdb ( contentKey INTEGER PRIMARY KEY NOT NULL, diaryKey INTEGR NOT NULL, diaryContents TEXT, FOREIGN KEY(diaryKey) REFERENCES diarydb(diaryKey));");
        closeDatabase();
    }

    public void createAnalysisResultDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS analysisresultdb ( diaryKey INTEGER PRIMARY KEY, anger REAL, contempt REAL, disgust REAL, fear REAL, happiness REAL, neutral REAL, " +
                "sadness REAL, surprise REAL, emotion TEXT NOT NULL, FOREIGN KEY(diaryKey) REFERENCES diarydb(diaryKey));");
        closeDatabase();
    }

    public void createStoryDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS storydb ( storyKey INTEGER PRIMARY KEY NOT NULL, emotionClass TEXT NOT NULL, callCount INTEGER NOT NULL);");
        closeDatabase();
    }

    public void createStoryContentsDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS storycontentsdb ( contentKey INTEGER PRIMARY KEY NOT NULL, storyKey INTEGER NOT NULL, storyContents TEXT, storyViewState INTEGER, FOREIGN KEY(storyKey) REFERENCES storydb(storyKey));");
        closeDatabase();
    }

    public void createRecommendedDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS recommendeddb ( recommendKey INTEGER PRIMARY KEY NOT NULL, emotionClass TEXT NOT NULL, content TEXT, callCount INTEGER NOT NULL);");
        closeDatabase();
    }

    /*-------------테이블 삽입 메소드--------------*/

    public void insertDiaryDB(int dKey, String path) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO diarydb (diaryKey, FacePhotoPath) values (?, ?)", new Object[]{dKey, path});
        closeDatabase();
    }

    public void insertDiaryContentsDB(int cKey, int dKey, String content) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO diarycontentsdb (contentKey, diaryKey, diaryContents) values (?, ?, ?)", new Object[]{cKey, dKey, content});
        closeDatabase();
    }

    public void insertAnalysisResultDB(int dKey, float[] emos, String emotion) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO analysisresultdb (diaryKey, anger, contempt, disgust, fear, happiness, neutral, sadness, surprise, emotion) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{dKey, emos[0], emos[1], emos[2], emos[3], emos[4], emos[5], emos[6], emos[7], emotion});
        closeDatabase();
    }

    public void insertStoryDB(int sKey, String emoClass, int callCount) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO storydb (storyKey, emotionClass, callCount) values (?, ?, ?)", new Object[]{sKey, emoClass, callCount});
        closeDatabase();
    }

    public void insertStoryContentsDB(int cKey, int sKey, String content, int state) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO storycontentdb (contentKey, storyKey, storyContents, storyViewState) values (?, ?, ?, ?)", new Object[]{cKey, sKey, content, state});
        closeDatabase();
    }

    public void insertRecommendedDB(int rKey, String emoClass, String content, int callCount) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO recommendeddb (recommendedKey, emotionClass, content, callCount) values (?, ?, ?, ?)", new Object[]{rKey, emoClass, content, callCount});
        closeDatabase();
    }

    /*-------------테이블 수정 메소드--------------*/


    public void AppSettingInfo(int time, boolean agreed) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("UPDATE AppSettingTable set TimeSetting = ?, RecordAgreed = ?", new Object[]{time, agreed});
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

    /*-------------테이블 조회 메소드--------------*/

    public Cursor selectDiaryDB(int key) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT facePhotoPath FROM diarydb WHERE diarydb.diaryKey = " + Integer.toString(key), null);

        return cursor;
    }

    public Cursor selectDiaryContentsDB(int key) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT diaryContents FROM diarycontentsdb WHERE diarydb.diaryKey = " + Integer.toString(key), null);

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
}
