package com.example.t16_capstone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnalysisMenu extends AppCompatActivity {

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);
    private TextView descText;
    private ImageButton nextBtn;
    private Button[] emoBtnGroup;
    private Button reCapYesBtn;
    private Button reCapNoBtn;
    private LinearLayout emoBtnGroupLayout;
    private LinearLayout reCaptureLayout;
    private ImageView character;

    private String emotionResult = ""; // 기쁜, 평범한, 당황스런, 기분나쁜, 불안한, 슬픈, 복잡한
    private int descCursor;
    private final int GO_CAMERA_MENU = 100;
    private String[] desc = {"안녕하세요!", "오늘도 사용자님의 얼굴을 보기위해 왔어요.", "오늘의 얼굴을 보여주세요!", // 초기 0 ~ 2
            "흐으음", "제가 보기엔 ", " 하루셨던 것 같네요.",   // 감정 결과 3 ~ 5
            "아니면 사실 다른 기분이신가요?", "알겠어요.", // 기본 흐름 6 ~ 7
            "다시 찍어드릴까요?", "이번엔 제대로 찍어드릴게요!", "복잡한 기분이신가 봐요.."};  // emotionResult == "복잡한" 일때 8 ~ 10

    // 바인딩 객체 호출
    private AnalysisBinding analysisBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_menu_desc);

        descText = findViewById(R.id.descText);
        character = findViewById(R.id.character);
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(nextEvent);

        reCapYesBtn = findViewById(R.id.reCapYesBtn);
        reCapNoBtn = findViewById(R.id.reCapNoBtn);
        reCapYesBtn.setOnClickListener(reCapture);
        reCapNoBtn.setOnClickListener(reCapture);
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
        reCaptureLayout = findViewById(R.id.reCaptureLayout);

        analysisBinding = new AnalysisBinding(this);

        // 선언 끝

        Intent intent = getIntent();
        String argv = intent.getStringExtra("Argv");
        switch(argv) {
            case "MainToDesc":      // "안녕하세요!"
                descCursor = 0;
                break;
            case "CameraToDesc":    // "흐으음"
                descCursor = 3;
                emotionResult = analysisBinding.analysFaceWithAPI();
                break;
            default:
                System.err.println("액티비티 인자 값 오류");
                System.exit(0);
        }

        descText.setText(desc[descCursor++]);
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
            // descCursor에 맞게 desc를 출력하거나 액티비티 호출.
            if(descCursor == GO_CAMERA_MENU) {
                goCameramenu();
                return;
            } else if(descCursor < desc.length)
                descText.setText(desc[descCursor]);
            else analysisBinding.closeMenu();

            // 뷰 설정 확인
            emoBtnGroupLayout.setVisibility(View.GONE);
            reCaptureLayout.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);

            // desc 출력 외의 설정이 필요할 경우
            if(descCursor == 2) { // "오늘의 얼굴을 보여주세요!"
                descCursor = GO_CAMERA_MENU;
                return;
            } else if(descCursor == 6) {    // "아니면 사실 다른 기분이신가요?""
                // 감정 선택 뷰를 나타낸다.
                emoBtnGroupLayout.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.GONE);
            } else if(descCursor == 7) {    // "알겠어요"
                // desc를 끝낸다. 길이보다 높은 인덱스를 줌.
                descCursor = desc.length;
                return;
            } else if(descCursor == 8) {    // "다시 찍어드릴까요?"
                // 재 촬영 선택 뷰를 나타낸다.
                reCaptureLayout.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.GONE);
            } else if (descCursor == 10) {  // "복잡한 기분이신가 봐요.." -> "알겠어요."로 이동.
                descCursor = 7;
                return;
            }

            if(descCursor == 4) {   // "제가 보기엔 ", " 하루셨던 것 같네요."
                descText.append(emotionResult);
                descText.append(desc[++descCursor]);
                if(emotionResult == "복잡한") {
                    descCursor = 8; // "다시 찍어드릴까요?"
                    return;
                }
            }

            descCursor++;
        }
    };

    Button.OnClickListener reCapture = new Button.OnClickListener() {
        public void onClick(View v)
        {
            switch (v.getId()) {
                case R.id.reCapYesBtn:
                    goCameramenu();
                    break;
                case R.id.reCapNoBtn:
                    descCursor = 10; // "복잡한 기분이신가 봐요.."
                    nextEvent.onClick(nextBtn);
                    break;
                default:
                    System.err.println("재 촬영 선택 오류");
                    System.exit(0);
                    break;
            }
        }
    };

    Button.OnClickListener analysWithAnswer = new Button.OnClickListener() {
        public void onClick(View v)
        {
            switch (v.getId()) {
                case R.id.happyBtn:
                case R.id.normalBtn:
                case R.id.embarrassBtn:
                case R.id.annoyedBtn:
                case R.id.anxiousBtn:
                case R.id.sadBtn:
                case R.id.complicateBtn:
                    analysisBinding.analysEmotion(v.getId());
                case R.id.noBtn:
                    nextEvent.onClick(nextBtn);
                    break;
                default:
                    System.err.println("감정 선택 오류");
                    System.exit(0);
                    break;
            }
        }
    };

}