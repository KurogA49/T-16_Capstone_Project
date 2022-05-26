package com.example.t16_capstone;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

class DBOpenHelper extends SQLiteOpenHelper {
    // 어플 패치를 할 때 필요시 버전을 올려주세요. 버전을 올리면 스토리 테이블을 삭제 후 다시 적용합니다.
    public DBOpenHelper(Context context) {
        super(context, "MainDB", null, 2);
    }

    //SQLiteOpenHelper는 기존의 DB유무를 확인하고 생성하기 때문에, 덮여쓰일 걱정은 하지않아도 된다.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS diarydb ( diaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, facePhoto BLOB, dayTime DATE);");
        db.execSQL("CREATE TABLE IF NOT EXISTS diarycontentsdb ( contentKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, diaryKey INTEGR NOT NULL, question TEXT, answer TEXT, FOREIGN KEY(diaryKey) REFERENCES diarydb(diaryKey));");
        db.execSQL("CREATE TABLE IF NOT EXISTS analysisresultdb ( diaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, anger REAL, contempt REAL, disgust REAL, fear REAL, happiness REAL, neutral REAL, " +
                "sadness REAL, surprise REAL, emotion TEXT NOT NULL, FOREIGN KEY(diaryKey) REFERENCES diarydb(diaryKey));");
        db.execSQL("CREATE TABLE IF NOT EXISTS storydb ( storyKey INTEGER PRIMARY KEY NOT NULL, emotionClass TEXT NOT NULL, callCount INTEGER NOT NULL);");
        db.execSQL("CREATE TABLE IF NOT EXISTS storycontentsdb ( contentKey INTEGER PRIMARY KEY NOT NULL, storyKey INTEGER NOT NULL, storyContents TEXT, storyViewState INTEGER, storyImageState INTEGER, FOREIGN KEY(storyKey) REFERENCES storydb(storyKey));");
        db.execSQL("CREATE TABLE IF NOT EXISTS recommendeddb ( recommendKey INTEGER PRIMARY KEY NOT NULL, emotionClass TEXT NOT NULL, content TEXT, callCount INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS appsettingdb ( settingKey INTEGER PRIMARY KEY NOT NULL, timeSetting INTEGER, continuousEmotion TEXT, continuousCount INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(("DROP TABLE IF EXISTS storydb"));
        db.execSQL(("DROP TABLE IF EXISTS storycontentsdb"));
        db.execSQL(("DROP TABLE IF EXISTS recommendeddb"));
        onCreate(db);
    }
}

public class DatabaseService {
    private DBOpenHelper dbOpenHelper;
    private Context context;
    private int diaryKey;

    public DatabaseService(Context context) {
        dbOpenHelper = new DBOpenHelper(context);
        this.context = context;
        context.getResources();

        if(!checkStoryExist()) {
            importStoryFile();
            importRecommendedFile();
            importStoryContentsFile();
        } else System.out.println("스토리 존재함");
    }

    public void dropTable(String option) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("DROP TABLE IF EXISTS " + option + ";");
    }

    public void closeDatabase(SQLiteDatabase worker) {
        worker.close();
    }

    /*-------------테이블 생성 메소드--------------*/

    public void createDiaryDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS diarydb ( diaryKey, INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, facePhoto BLOB, dayTime DATE);");
        closeDatabase(writer);
    }

    public void createDiaryContentsDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS diarycontentsdb ( contentKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, diaryKey INTEGR NOT NULL, question TEXT, answer TEXT, FOREIGN KEY(diaryKey) REFERENCES diarydb(diaryKey));");
        closeDatabase(writer);
    }

    public void createAnalysisResultDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS analysisresultdb ( diaryKey INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, anger REAL, contempt REAL, disgust REAL, fear REAL, happiness REAL, neutral REAL, " +
                "sadness REAL, surprise REAL, emotion TEXT NOT NULL, FOREIGN KEY(diaryKey) REFERENCES diarydb(diaryKey));");
        closeDatabase(writer);
    }

    public void createStoryDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS storydb ( storyKey INTEGER PRIMARY KEY NOT NULL, emotionClass TEXT NOT NULL, callCount INTEGER NOT NULL);");
        closeDatabase(writer);
    }

    public void createStoryContentsDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS storycontentsdb ( contentKey INTEGER PRIMARY KEY NOT NULL, storyKey INTEGER NOT NULL, storyContents TEXT, storyViewState INTEGER, storyImageState INTEGER, FOREIGN KEY(storyKey) REFERENCES storydb(storyKey));");
        closeDatabase(writer);
    }

    public void createRecommendedDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE IF NOT EXISTS recommendeddb ( recommendKey INTEGER PRIMARY KEY NOT NULL, emotionClass TEXT NOT NULL, content TEXT, callCount INTEGER NOT NULL);");
        closeDatabase(writer);
    }

    public void createAppSettingDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("CREATE TABLE appsettingdb ( settingKey INTEGER PRIMARY KEY NOT NULL, timeSetting INTEGER, continuousEmotion TEXT, continuousCount INTEGER);");
        closeDatabase(writer);
    }

    /*-------------테이블 삽입 메소드--------------*/

    public void insertDiaryDB(byte[] photo) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        SQLiteStatement p = writer.compileStatement("INSERT INTO diarydb (facePhoto, dayTime) values (?, datetime('now', 'localtime'))");
        p.bindBlob(1, photo);
        p.execute();
        closeDatabase(writer);
    }

    //주의사항 : 지역변수 diaryKey 는 초기화되지 않았으므로 recordDiaryAndResult() 메소드를 먼저 사용한 후에 이 메소드를 사용할 것
    public void insertDiaryContentsDB(String qes, String ans) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO diarycontentsdb (diaryKey, question, answer) values (?, ?, ?)", new Object[]{diaryKey, qes, ans});
        closeDatabase(writer);
    }

    public void insertAnalysisResultDB(float[] emos, String emotion) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO analysisresultdb (anger, contempt, disgust, fear, happiness, neutral, sadness, surprise, emotion) values (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{emos[0], emos[1], emos[2], emos[3], emos[4], emos[5], emos[6], emos[7], emotion});
        closeDatabase(writer);
    }

    // Story관련 DB는 csv로 초기화할 때 사용함.
    public void insertStoryDB(int sKey, String emoClass, int callCount) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO storydb (storyKey, emotionClass, callCount) values (?, ?, ?)", new Object[]{sKey, emoClass, callCount});
        closeDatabase(writer);
    }

    public void insertStoryContentsDB(int cKey, int sKey, String content, int vState, int iState) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO storycontentsdb (contentKey, storyKey, storyContents, storyViewState, storyImageState) values (?, ?, ?, ?, ?)", new Object[]{cKey, sKey, content, vState, iState});
        closeDatabase(writer);
    }

    public void insertRecommendedDB(int rKey, String emoClass, String content, int callCount) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO recommendeddb (recommendKey, emotionClass, content, callCount) values (?, ?, ?, ?)", new Object[]{rKey, emoClass, content, callCount});
        closeDatabase(writer);
    }

    public void insertAppSettingDB() {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("INSERT INTO appsettingdb (settingKey, timeSetting, continuousEmotion, continuousCount) values (0, null, null, null)");
    }

    /*-------------테이블 수정 메소드--------------*/

    public void DBUpgrade(int oldVersion, int newVersion) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        dbOpenHelper.onUpgrade(writer, oldVersion, newVersion);
    }

    public void updateDiaryDB(int key, byte[] photo) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        SQLiteStatement p = writer.compileStatement("UPDATE diarydb set facePhoto = ? WHERE diarydb.diaryKey = " + Integer.toString(key));
        p.bindBlob(1, photo);
        p.execute();
        closeDatabase(writer);
    }

    public void updateDiaryContentsDB(int cKey, int dKey, String qes, String ans) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("UPDATE diarycontentsdb set diaryKey = ?, question = ?, answer = ? WHERE diarycontentsdb.contentKey = " + Integer.toString(cKey), new Object[] {dKey, qes, ans});
        closeDatabase(writer);
    }

    public void updateAnalysisResultDB(int dKey, float[] emos, float emotion) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("UPDATE analysisresultdb set anger = ?, contempt =?, disgust = ?, fear = ?, happiness = ?, neutral = ?, sadness =?, surprise =?, emotion =? WHERE analysisresultdb.diaryKey = " + Integer.toString(dKey),
                new Object[] {emos[0], emos[1], emos[2], emos[3], emos[4], emos[5], emos[6], emos[7], emotion});
        closeDatabase(writer);
    }

    public void updateStoryDB(int sKey, String emoClass) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("UPDATE storydb set emotionClass = ? WHERE storydb.storyKey = " + Integer.toString(sKey), new Object[] {emoClass});
        closeDatabase(writer);
    }

    // 해당 메소드는 StoryDB 호출 시에 사용함.
    public void incrementStoryCallCount(int sKey) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT callCount FROM storydb WHERE storydb.storyKey = " + Integer.toString(sKey), null);
        cursor.moveToNext();
        writer.execSQL("UPDATE storydb set callCount = ? WHERE storydb.storyKey = " + Integer.toString(sKey), new Object[] {cursor.getInt(0) + 1});
    }

    public void updateStoryContentsDB(int cKey, String contents, int vState, int iState) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("UPDATE storycontentsdb set storyContents = ?, storyViewState = ?, storyImageState = ? WHERE storycontentsdb = " + Integer.toString(cKey), new Object[] {contents,vState, iState});
        closeDatabase(writer);
    }

    public void updateRecommendedDB(int rKey, String emoClass, String content) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("UPDATE recommendeddb set emotionClass = ?, content = ? WHERE recommendeddb.recommendKey = " + Integer.toString(rKey), new Object[] {emoClass, content});
        closeDatabase(writer);
    }

    public void incrementRecommendedCallCount(int rKey) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT callCount FROM recommendeddb WHERE recommendeddb.recommendKey = " + Integer.toString(rKey), null);
        cursor.moveToNext();
        writer.execSQL("UPDATE recommendeddb set callCount = ? WHERE recommendeddb.recommendKey = " + Integer.toString(rKey), new Object[] {cursor.getInt(0) + 1});
    }

    public void updateAppSettingDB(int time) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("UPDATE appsettingdb set timeSetting = ? WHERE appsettingdb.settingKey = 0", new Object[] {time});
        closeDatabase(writer);
    }

    public void updateAppSettingDB(String emotion, int count) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        writer.execSQL("UPDATE appsettingdb set continuousEmotion = ?, continuousCount = ? WHERE appsettingdb.settingKey = 0", new Object[] {emotion, count});
        closeDatabase(writer);
    }

    /*-------------테이블 조회 메소드--------------*/

    public Cursor getAllStory() {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM storydb;", null);

        return cursor;
    }

    public Cursor selectAllDiaryDB() {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = reader.rawQuery("SELECT * FROM diarydb", null);
        } catch(CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            cursor = null;
        }
        return cursor;
    }

    public Cursor selectDiaryDB(int key) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM diarydb WHERE diarydb.diaryKey = " + Integer.toString(key), null);

        return cursor;
    }

    public Cursor selectDiaryContentsDB(int key) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM diarycontentsdb WHERE diarydb.diaryKey = " + Integer.toString(key), null);

        return cursor;
    }

    public Cursor selectAnalysisResultDB(int key) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM analysisresultdb WHERE analysisresultdb.diaryKey = " + Integer.toString(key), null);

        return cursor;
    }

    public Cursor selectStoryDB(int key) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM storydb WHERE storydb.storyKey = " + Integer.toString(key), null);

        return cursor;
    }

    public Cursor selectStoryDB(String emoClass) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM storydb WHERE storydb.emotionClass = '" + emoClass + "'", null);

        return cursor;
    }

    public Cursor selectStoryContentsDB(int key) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM storycontentsdb WHERE storycontentsdb.storyKey = " + Integer.toString(key), null);

        return cursor;
    }

    public Cursor selectRecommendedDB(int rKey) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM recommendeddb WHERE recommendeddb.recommendKey = " + Integer.toString(rKey), null);

        return cursor;
    }

    public Cursor selectRecommendedDB(String emoClass) {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM recommendeddb WHERE recommendeddb.emotionClass = '" + emoClass + "'", null);

        return cursor;
    }

    public Cursor selectAppSettingDB() {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM appsettingdb WHERE appsettingdb.settingKey = 0", null);

        return cursor;
    }

    /*-------------CSV파일 import 메소드--------------*/

    private boolean checkStoryExist() {
        SQLiteDatabase reader = dbOpenHelper.getReadableDatabase();
        Cursor cursor = reader.rawQuery("SELECT * FROM storydb" , null);
        int count = cursor.getCount();
        if(count > 0)
            return true;
        else return false;
    }

    public void importStoryFile() {
        AssetManager am = context.getResources().getAssets();
        InputStream is = null;
        String str[];
        BufferedReader br;
        try {
            is = am.open("storydb.csv");
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            br.readLine();

            String sLine = null;
            while((sLine = br.readLine()) != null) {
                str = sLine.split(",");
                insertStoryDB(Integer.parseInt(str[0]), str[1], Integer.parseInt(str[2]));
            }
            is.close();
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void importStoryContentsFile() {
        AssetManager am = context.getResources().getAssets();
        InputStream is = null;
        String str[];
        BufferedReader br;
        try {
            is = am.open("storycontentsdb.csv");
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            br.readLine();

            String sLine = null;
            while((sLine = br.readLine()) != null) {
                str = sLine.split(",");
                insertStoryContentsDB(Integer.parseInt(str[0]), Integer.parseInt(str[1]), str[2], Integer.parseInt(str[3]), Integer.parseInt(str[4]));
            }
            is.close();
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void importRecommendedFile() {
        AssetManager am = context.getResources().getAssets();
        InputStream is = null;
        String str[];
        BufferedReader br;
        try {
            is = am.open("recommendeddb.csv");
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            br.readLine();

            String sLine = null;
            while((sLine = br.readLine()) != null) {
                str = sLine.split(",");
                insertRecommendedDB(Integer.parseInt(str[0]), str[1], str[2], Integer.parseInt(str[3]));
            }
            is.close();
            br.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    /*-------------특수 select 메소드--------------*/

    public Cursor[] getStoryByEmotionAndRecommend(String emoClass) {
        System.out.println("메소드 시작됨");
        Cursor cursor, cursor2, cursor3, cursor4;
        cursor = selectStoryDB(emoClass);

        int foreignKey;
        int max = cursor.getCount();
        max = (int)(Math.random()*max);

        for(int i = 0; i < max+1; i++)
            cursor.moveToNext();

        try {
            foreignKey = cursor.getInt(0);
            incrementStoryCallCount(foreignKey);
            cursor.close();
            cursor2 = selectStoryContentsDB(foreignKey);
        } catch(CursorIndexOutOfBoundsException e) {
            cursor2 = null;
        }

        cursor3 = selectRecommendedDB(emoClass);
        max = cursor3.getCount();
        max = (int)(Math.random()*max);
        for(int i = 0; i < max+1; i++)
            cursor3.moveToNext();
        try {
            cursor4 = selectRecommendedDB(cursor3.getInt(0));
        } catch(CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            cursor4 = null;
        }
        cursor3.close();

        System.out.println("메소드 끝남");
        return new Cursor[] {cursor2, cursor4};
    }

    public Cursor[] getResultAndDiaryContentsByKey(int Key) {
        Cursor cursor, cursor2;
        try {
            cursor = selectAnalysisResultDB(Key);
            cursor2 = selectDiaryContentsDB(Key);
        } catch(CursorIndexOutOfBoundsException e) {
            e.printStackTrace();
            cursor = null;
            cursor2 = null;
        }
        return new Cursor[] {cursor, cursor2};
    }

    /*-------------특수 insert 메소드--------------*/

    public void recordDiaryAndResult(byte[] photo, float[] emo, String emotionList) {
        SQLiteDatabase writer = dbOpenHelper.getWritableDatabase();
        SQLiteStatement p = writer.compileStatement("INSERT INTO diarydb (facePhoto) values(?)");
        p.bindBlob(1, photo);

        Cursor cursor = dbOpenHelper.getReadableDatabase().rawQuery("SELECT diaryKey FROM diarydb", null);
        cursor.moveToLast();
        diaryKey = cursor.getInt(0);

        writer.execSQL("INSERT INTO analysisresultdb (diaryKey, anger, contempt, disgust, fear, happiness, neutral, sadness, surprise, emotion) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[] {diaryKey, emo[0], emo[1], emo[2], emo[3], emo[4], emo[5], emo[6], emo[7], emotionList});
    }
}