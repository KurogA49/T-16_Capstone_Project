package com.example.t16_capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    public void onClick(View view) {
        // 화면 전환
        Intent intent = new Intent(this, AnalysisMenu.class);
        // 갈 화면에 줄 인자를 설정한다.
        intent.putExtra("Argv", "MainToDesc");
        startActivity(intent);
        finish(); //액티비티 종료
    }

}