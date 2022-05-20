package com.example.t16_capstone;

import static java.lang.System.exit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CommunicationMenu extends AppCompatActivity {

    String emotionResult;
    private StoryContents storyContents;
    private String[] contents;
    private int[] viewStates;
    private int[] imageStates;
    private int storyCursor;
    private int storyLength;
    private int yesOrNo; // 0 : yes, 1 : no
    private TextView storyDescText;
    private LinearLayout answerBtnLayout;
    private LinearLayout storyDiaryLayout;
    private Button storyDiaryConfirmBtn;
    private Button answerYesBtn;
    private Button answerNoBtn;
    private EditText storyDiaryEdit;
    private ImageButton nextStoryBtn;
    private CommunicationBinding communicationBinding;

    // 감정 기록에 전달해줄 값들
    private String diaryQuestion;
    private String diaryAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.communication_menu);

        // 변수 초기화
        contents = null;
        viewStates = null;
        imageStates = null;
        diaryQuestion = null;
        diaryAnswer = null;
        storyCursor = 0;
        storyLength = 0;
        yesOrNo = -1;

        storyDescText = findViewById(R.id.storyDescText);
        answerBtnLayout = findViewById(R.id.answerBtnLayout);
        storyDiaryLayout = findViewById(R.id.storyDiaryLayout);
        storyDiaryEdit = findViewById(R.id.storyDiaryEdit);
        storyDiaryConfirmBtn = findViewById(R.id.storyDiaryConfirmBtn);
        answerYesBtn = findViewById(R.id.answerYesBtn);
        answerNoBtn = findViewById(R.id.answerNoBtn);
        nextStoryBtn = findViewById(R.id.nextStoryBtn);

        storyDiaryConfirmBtn.setOnClickListener(diaryBtnEvent);
        answerYesBtn.setOnClickListener(answerBtnEvent);
        answerNoBtn.setOnClickListener(answerBtnEvent);
        nextStoryBtn.setOnClickListener(nextStoryEvent);

        // 이전 뷰의 감정 결과를 받아와 바인딩으로 전달
        Intent intent = getIntent();
        emotionResult = intent.getStringExtra("emotionResult");
        storyDescText.setText(emotionResult);
        communicationBinding = new CommunicationBinding(this, emotionResult);

        // 스토리 진행
        storyContents = new StoryContents();
        receiveStory(); // 스토리들을 DB에서 받아오고, 스토리 관련 변수 값을 초기화합니다.
        displayStory(); // 첫번째 story content를 나타낸다.
    }

    Button.OnClickListener nextStoryEvent = new Button.OnClickListener() {
        public void onClick(View v)
        {
            displayStory();
        }
    };

    Button.OnClickListener answerBtnEvent = new Button.OnClickListener() {
        public void onClick(View v)
        {
            switch (v.getId()) {
                case R.id.answerYesBtn:
                    yesOrNo = 0;
                    displayStory();
                    break;
                case R.id.answerNoBtn:
                    yesOrNo = 1;
                    displayStory();
                    break;
                default:
            }
        }
    };

    Button.OnClickListener diaryBtnEvent = new Button.OnClickListener() {
        public void onClick(View v)
        {
            diaryAnswer = storyDiaryEdit.getText().toString();
            displayStory();
        }
    };

    private void receiveStory() {
        storyContents = communicationBinding.readStoryContents();

        contents = storyContents.getContents();
        viewStates = storyContents.getViewStates();
        imageStates = storyContents.getImageStates();

        storyLength = contents.length;
    }

    // 뷰를 스토리에 맞게 정리한다.
    private void displayStory() {
        if(storyCursor < storyLength) {
            switch(viewStates[storyCursor]/10) {
                case 7:
                    diaryQuestion = contents[storyCursor];
                    break;
                case 8:
                    if(yesOrNo != 0) { // yes가 아니면 스킵
                        storyCursor++;
                        displayStory(); // story도 확인.
                        return;
                    }
                    break;
                case 9:
                    if(yesOrNo != 1) {
                        storyCursor++;
                        displayStory();
                        return;
                    }
                    break;
                default:    // 0일때를 포함
                    break;
            }
            switch(viewStates[storyCursor]%10) {
                case 0:
                    // 뷰 설정
                    nextStoryBtn.setVisibility(View.VISIBLE);
                    storyDiaryLayout.setVisibility(View.GONE);
                    answerBtnLayout.setVisibility(View.GONE);
                    break;
                case 1:
                    // 뷰 설정
                    nextStoryBtn.setVisibility(View.GONE);
                    storyDiaryLayout.setVisibility(View.VISIBLE);
                    answerBtnLayout.setVisibility(View.GONE);
                    break;
                case 2:
                    // 뷰 설정
                    nextStoryBtn.setVisibility(View.GONE);
                    storyDiaryLayout.setVisibility(View.GONE);
                    answerBtnLayout.setVisibility(View.VISIBLE);
                    break;
                default:
                    System.err.println("정의되지 않은 viewState.");
                    break;
            }
            switch(imageStates[storyCursor]) {
                // 캐릭터 이미지 변환
                case 0:     // 기본 자세
                    break;
                case 1:     // 궁금한 자세
                    break;
                case 2:     // 웃는 자세
                    break;
                case 3:     // 일기장 들고 있음
                    break;
                case 4:     // 기분나쁨
                    break;
                case 5:     // 웃픔
                    break;
                case 6:     // 불안함
                    break;
                case 7:     // 우물쭈물
                    break;
                case 8:     // 밝은 표정
                    break;
                case 9:     // 기쁨.
                    break;
                case 10:    // 슬픔.
                    break;
                case 11:    // 당황스러움.
                    break;
                case 12:    // 복잡함.
                    break;
            }
            storyDescText.setText(contents[storyCursor]);
            storyCursor++;
        } else {
            quitComm();
        }
    }

    // 감정 기록을 위한 다이어리 전달
    private String sendStoryDiary() {
        return null;
    }

    private void quitComm() {
        exit(0);
    }
}

