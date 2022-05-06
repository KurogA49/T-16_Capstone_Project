package com.example.t16_capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AnalysisCameraMenu extends AppCompatActivity {

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

    String[] notice = {"밝은 곳에서 얼굴이 잘 보이게 찍어주세요!"};
    TextView noticeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_menu_camera);

        noticeText = findViewById(R.id.noticeText);

        noticeText.setText(notice[0]);
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