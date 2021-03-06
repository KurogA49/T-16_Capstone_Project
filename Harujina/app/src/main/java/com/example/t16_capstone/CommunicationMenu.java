package com.example.t16_capstone;

import static java.lang.System.exit;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class CommunicationMenu extends AppCompatActivity {

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);

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
    private Button answerYesBtn;
    private Button answerNoBtn;
    private EditText storyDiaryEdit;
    private ImageButton nextStoryBtn;
    private CommunicationBinding communicationBinding;
    private ImageView characterComm;
    private ImageView speechBubbleComm;
    private Drawable[] drawable;

    // 감정 기록에 전달해줄 값들
    public static Context thisContext;
    private ArrayList<String> diaryQuestion;
    private ArrayList<String> diaryAnswer;

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
        diaryQuestion = new ArrayList<>();
        diaryAnswer = new ArrayList<>();
        thisContext = this;

        storyDescText = findViewById(R.id.storyDescText);
        answerBtnLayout = findViewById(R.id.answerBtnLayout);
        storyDiaryLayout = findViewById(R.id.storyDiaryLayout);
        storyDiaryEdit = findViewById(R.id.storyDiaryEdit);
        answerYesBtn = findViewById(R.id.answerYesBtn);
        answerNoBtn = findViewById(R.id.answerNoBtn);
        nextStoryBtn = findViewById(R.id.nextStoryBtn);

        storyDiaryEdit.setOnEditorActionListener(diaryEditEvent);
        answerYesBtn.setOnClickListener(answerBtnEvent);
        answerNoBtn.setOnClickListener(answerBtnEvent);
        nextStoryBtn.setOnClickListener(nextStoryEvent);

        // 이전 뷰의 인텐트를 받아와 바인딩으로 전달
        Intent intent = getIntent();
        communicationBinding = new CommunicationBinding(this, intent);

        // 애니메이션
        characterComm = findViewById(R.id.characterComm);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.floating);
        characterComm.startAnimation(animation);

        speechBubbleComm = findViewById(R.id.speechBubbleComm);
        AnimationDrawable animationDrawable = (AnimationDrawable)speechBubbleComm.getBackground();
        animationDrawable.start();

        AnimationDrawable animationDrawable2 = (AnimationDrawable)answerYesBtn.getBackground();
        animationDrawable2.start();

        AnimationDrawable animationDrawable3 = (AnimationDrawable)answerNoBtn.getBackground();
        animationDrawable3.start();

        // 캐릭터 이미지 설정
        drawable = new Drawable[13];
        drawable[0] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_0);
        drawable[1] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_1);
        drawable[2] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_2);
        drawable[3] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_3);
        drawable[4] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_4);
        drawable[5] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_5);
        drawable[6] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_6);
        drawable[7] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_7);
        drawable[8] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_8);
        drawable[9] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_9);
        drawable[10] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_10);
        drawable[11] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_11);
        drawable[12] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_12);

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
                    diaryQuestion.remove(diaryQuestion.size()-1);
                    displayStory();
                    break;
                default:
            }
        }
    };

    TextView.OnEditorActionListener diaryEditEvent = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
        {
            boolean handle = false;
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                diaryAnswer.add(storyDiaryEdit.getText().toString());
                displayStory();
            }
            return handle;
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
                    diaryQuestion.add(contents[storyCursor]);
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
            // 캐릭터 이미지 설정
            characterComm.setImageDrawable(drawable[imageStates[storyCursor]]);
            // 대사 설정
            storyDescText.setText(contents[storyCursor]);
            storyCursor++;
        } else {
            communicationBinding.quitComm();
        }
    }

    // 감정 기록을 위한 다이어리 반환
    public ArrayList<String>[] receiveStoryDiary() {
        return new ArrayList[] {diaryQuestion, diaryAnswer};
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }
}

