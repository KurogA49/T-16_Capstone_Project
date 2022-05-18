package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    static DatabaseService dbsvs;
    static MainActivity ma;
    EditText edtName, edtNumber, edtType;
    TextView edtNameResultm, edtNumberResult, edtTypeResult;
    Button btnInit, btnInsert, btnSelect;
    SQLiteDatabase sqlDB;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("구매 리스트 관리");

        edtName = (EditText) findViewById(R.id.edtName);
        edtNumber = (EditText) findViewById(R.id.edtNumber);
        edtType = (EditText) findViewById(R.id.edtType);
        edtNameResultm = (TextView) findViewById(R.id.edtNameResult);
        edtNumberResult = (TextView) findViewById(R.id.edtNumberResult);
        edtTypeResult = (TextView) findViewById(R.id.edtTypeResult);
        btnInit = (Button) findViewById(R.id.btnInit);
        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnSelect = (Button) findViewById(R.id.btnSelect);


        DatabaseService dbsvs = new DatabaseService(this);



        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    dbsvs.DBUpgrade(1, 2);
                } catch (SQLException e) {
                    Log.d("please : ", e.toString());
                }
            }
        });
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                try {
                    dbsvs.importRecommendedFile();
                    dbsvs.importStoryContentsFile();
                    dbsvs.importStoryFile();
                } catch (SQLException e) {
                    Log.d("please : ", e.toString());
                }
                Toast.makeText(getApplicationContext(), "입력됨", 0).show();
            }
        });

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor;
                try {
                    cursor = dbsvs.getAllStory();
                    dbsvs.getStoryByEmotionAndRecommend("평범한");

                    String strNames = "목록 리스트" + "\r\n" + "\r\n";
                    String strNumbers = "수량" + "\r\n" + "\r\n";
                    String strTypes = "종류" + "\r\n" + "\r\n";

                    while (cursor.moveToNext()) {
                        strNames += cursor.getInt(0) + "\r\n";
                        strNumbers += cursor.getString(1) + "\r\n";
                        strTypes += cursor.getInt(2) + "\r\n";
                    }

                    edtNameResultm.setText(strNames);
                    edtNumberResult.setText(strNumbers);
                    edtTypeResult.setText(strTypes);
                    cursor.close();
                } catch(SQLException e) {
                    Log.d("please : ", e.toString());
                }
            }
        });

    }

    private class BackRunnable implements  Runnable {
        Context context;
        DatabaseService dbsvs;
        public BackRunnable(Context context) {
            this.context = context;
            dbsvs = new DatabaseService((MainActivity) context);
        }
        public void run() {
            dbsvs.importStoryFile();
            dbsvs.importRecommendedFile();
            dbsvs.importRecommendedFile();
        }
    }
}