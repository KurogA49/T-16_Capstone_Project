package com.example.t16_capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnalysisMenu extends AppCompatActivity {

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);
    TextView descText;
    ImageButton nextBtn;
    Button[] emoBtnGroup;
    LinearLayout emoBtnGroupLayout;

    int descCursor;
    String[] reAnalysisDesc = {};
    String[] desc = {"메인화면에서 넘어왔어요!", "흐으음", "제가 보기엔 이런 기분이신 것 같네요.", "아니면 사실 다른 기분이신가요?", "알겠어요."};

    // 바인딩 객체 호출
    AnalysisBinding analysisBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_menu_desc);

        descText = findViewById(R.id.descText);
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(nextEvent);

        emoBtnGroup = new Button[8];
        emoBtnGroup[0] = findViewById(R.id.happyBtn);
        emoBtnGroup[1] = findViewById(R.id.normalBtn);
        emoBtnGroup[2] = findViewById(R.id.embarrassBtn);
        emoBtnGroup[3] = findViewById(R.id.annoyedBtn);
        emoBtnGroup[4] = findViewById(R.id.anxiousBtn);
        emoBtnGroup[5] = findViewById(R.id.sadBtn);
        emoBtnGroup[6] = findViewById(R.id.complicateBtn);
        emoBtnGroup[7] = findViewById(R.id.noBtn);
        for(int i=0; i<emoBtnGroup.length; i++)
            emoBtnGroup[i].setOnClickListener(analysWithAnswer);
        emoBtnGroupLayout = findViewById(R.id.emoBtnGroupLayout);

        analysisBinding = new AnalysisBinding();


        Intent intent = getIntent();
        String argv = intent.getStringExtra("Argv");
        switch(argv) {
            case "MainToDesc":
                descCursor = 0;
                break;
            case "CameraToDesc":
                descCursor = 1;
                break;
            default:
                System.out.println("액티비티 인자 값 오류");
                finish();
        }
        descText.setText(desc[descCursor]);
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    public void goCameramenu() {
        Intent intent = new Intent(this, AnalysisCameraMenu.class);
        startActivity(intent);
        finish();
    }

    Button.OnClickListener nextEvent = new Button.OnClickListener() {
        public void onClick(View v)
        {
            if(descCursor == 0) {
                goCameramenu();
                return;
            }
            else if(++descCursor >= desc.length) {
                finish();
                return;
            }
            // 감정 선택 뷰를 나타낸다.
            if(descCursor == 3) {
                emoBtnGroupLayout.setVisibility(View.VISIBLE);
            } else if(descCursor > 3) {
                emoBtnGroupLayout.setVisibility(View.GONE);
            }

            descText.setText(desc[descCursor]);
        }
    };

    Button.OnClickListener analysWithAnswer = new Button.OnClickListener() {
        public void onClick(View v)
        {
            switch (v.getId()) {
                case R.id.happyBtn:
                    break;
                case R.id.normalBtn:
                    break;
                case R.id.embarrassBtn:
                    break;
                case R.id.annoyedBtn:
                    break;
                case R.id.anxiousBtn:
                    break;
                case R.id.sadBtn:
                    break;
                case R.id.complicateBtn:
                    break;
                case R.id.noBtn:
                    break;
                default:
                    System.out.println("감정 선택 오류");
                    finish();
                    break;
            }
        }
    };

}