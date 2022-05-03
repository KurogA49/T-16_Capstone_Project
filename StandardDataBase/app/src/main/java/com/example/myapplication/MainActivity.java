package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    myDBHelper myDBHelper;
    static DatabaseService dbsvs;
    static MainActivity ma;
    EditText edtName, edtNumber, edtType, edtNameResultm, edtNumberResult, edtTypeResult;
    Button btnInit, btnInsert, btnSelect;
    SQLiteDatabase sqlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("구매 리스트 관리");

        edtName = (EditText) findViewById(R.id.edtName);
        edtNumber = (EditText) findViewById(R.id.edtNumber);
        edtType = (EditText) findViewById(R.id.edtType);
        edtNameResultm = (EditText) findViewById(R.id.edtNameResult);
        edtNumberResult = (EditText) findViewById(R.id.edtNumberResult);
        edtTypeResult = (EditText) findViewById(R.id.edtTypeResult);
        btnInit = (Button) findViewById(R.id.btnInit);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnSelect = (Button) findViewById(R.id.btnSelect);


        myDBHelper = new myDBHelper(this);
        DatabaseService dbsvs = new DatabaseService(this);
        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbsvs.DBUpgrade(1, 2);
                sqlDB = myDBHelper.getWritableDatabase();
                myDBHelper.onUpgrade(sqlDB,1,2);
                sqlDB.close();
                dbsvs.closeDatabase();
            }
        });
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                dbsvs.saveStoryInfo();
                dbsvs.closeDatabase();
                Toast.makeText(getApplicationContext(),"입력됨",0).show();
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlDB = myDBHelper.getReadableDatabase();
                Cursor cursor;
                cursor = dbsvs.getConditionalStory(15);

                String strNames = "목록 리스트"+"\r\n"+"\r\n";
                String strNumbers = "수량"+"\r\n"+"\r\n";
                String strTypes = "종류"+"\r\n"+"\r\n";

                while (cursor.moveToNext()){
                    strNames += cursor.getString(2) + "\r\n";
                    strNumbers += cursor.getString(3) + "\r\n";
                    strTypes += cursor.getString(4) + "\r\n";
                }
                edtNameResultm.setText(strNames);
                edtNumberResult.setText(strNumbers);
                edtTypeResult.setText(strTypes);
                cursor.close();
                dbsvs.closeDatabase();
                sqlDB.close();
            }
        });

    }

    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "groupDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE groupTBL ( gName CHAR(20) PRIMARY KEY, gNumber INTEGER, gType CHAR(10));");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS groupTBL");
            onCreate(db);

        }
    }
}