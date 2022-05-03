package com.example.t16_capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AnalysisCameraMenu extends AppCompatActivity {

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_menu_camera);
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, AnalysisMenu.class);
        // 돌아갈 화면에 줄 인자를 설정한다.
        intent.putExtra("Argv", "CameraToDesc");
        startActivity(intent);
        finish(); //액티비티 종료
    }

}