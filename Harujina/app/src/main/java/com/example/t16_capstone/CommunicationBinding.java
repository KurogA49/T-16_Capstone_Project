package com.example.t16_capstone;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class StoryContents {
    // StoryContentsDB, RecommendedDB, 상담권유내용 들을 각각 받기위한 클래스입니다.
    private ArrayList<String> contents;
    private ArrayList<Integer> viewStates;
    private ArrayList<Integer> imageStates;

    StoryContents() {
        contents = new ArrayList();
        viewStates = new ArrayList();
        imageStates = new ArrayList();
    }

    public void addStory(String s, int views, int images) {
        contents.add(s);
        viewStates.add(views);
        imageStates.add(images);
    }

    public String[] getContents() {
        String[] contentStrs = new String[contents.size()];
        for(int i =0; i<viewStates.size(); i++)
            contentStrs[i] = contents.get(i);
        return contentStrs;
    }
    public int[] getViewStates() {
        int[] views = new int[viewStates.size()];
        for(int i =0; i<viewStates.size(); i++)
            views[i] = viewStates.get(i);
        return views;
    }
    public int[] getImageStates() {
        int[] images = new int[imageStates.size()];
        for(int i =0; i<imageStates.size(); i++)
            images[i] = imageStates.get(i);
        return images;
    }
}

public class CommunicationBinding {

    private Intent intent;
    private SerializableRecordData serializableRecordData;
    private RecordDiary recordDiary;
    private Activity menu;
    private String emotionResult;
    private StoryContents DBStory;
    private Cursor[] cursors;
    private Cursor storyCursor, recommendCursor;
    private DatabaseService dbsvs;

    String[] recommendCounseling = {"끝내기전 저 긴히 할말이 있어요.", "최근에 친구님이 많이 힘드셨다는 걸 느껴요...", "힘드시다는 것이 말할 수 없는 비밀이시라면 숨기셔두돼요.",
            "그래도 말해서 나아지는 일이 간혹 있더라구요.", "그것이 적어도 나 자신이어도 좋구요..!", "아니면 혹시 말하고 싶은 곳을 찾고 거라면...",
            "상담사를 찾아가 보는 것도 권해요.", "물론 상담사도 사람이니 꺼려질 수도 있고, 맞지 않을수도 있어요.", "어찌됐건 사람들은 서로 공유하고 싶은 사람을 찾는 거겠죠.",
            "그것이 고통이라도요.", "만약 상담에 가신다면, 일기장을 보여주는 것도 도움이 될거에요.", "나아질 미래를 응원할게요.", "그리고 언제나 자신을 믿을 수 있길 바래요."};
    int[] recommendCounselingImages = {0, 7, 5, 0, 5, 7, 0, 0, 0, 5, 0, 5, 0};

    public CommunicationBinding(Activity menu, Intent intent) {
        this.intent = intent;

        this.menu = menu;
        this.emotionResult = intent.getStringExtra("emotionResult");;

        dbsvs = new DatabaseService(menu);
        DBStory = new StoryContents();

        cursors = dbsvs.getStoryByEmotionAndRecommend(emotionResult);
        storyCursor = cursors[0];
        recommendCursor = cursors[1];

        serializableRecordData = (SerializableRecordData)intent.getSerializableExtra("sendRecordData");

        // 감정 기록 객체 생성자로 정보 전달.
        recordDiary = new RecordDiary(menu, serializableRecordData.getSerialPhotoUri(), serializableRecordData.getSerialEmotionValue(), emotionResult);
    }

    // StoryContents객체에 add를 통해 내용 구분없이 쭉 붙인다.
    public StoryContents readStoryContents() {
        // 기본 상호작용
        while (storyCursor.moveToNext()){
            DBStory.addStory(storyCursor.getString(2), storyCursor.getInt(3), storyCursor.getInt(4));
        }
        storyCursor.close();
        // 추천 상호작용
        if(recommendCursor == null) {
            // skip
        } else {
            while (recommendCursor.moveToNext()){
                // 전처리 포함
                String[] temp = recommendCursor.getString(2).split("#");
                temp[1] = temp[1] + "(이)라는 " + temp[0] + "에요.";
                temp[0] = "마지막으로 " + temp[0] + "하나 추천드리고 싶네요.";
                temp[2] = temp[2];
                DBStory.addStory(temp[0], 0, 0);
                DBStory.addStory(temp[1], 0, 0);
                DBStory.addStory(temp[2], 0, 0);
                DBStory.addStory("꼭 확인해 보셨음 하네요.", 0, 0);
            }
            recommendCursor.close();
        }
        // 상담권유 상호작용
        if(checkContinuousEmotion()) {
            for(int i =0; i<recommendCounseling.length; i++)
                DBStory.addStory(recommendCounseling[i], 0, recommendCounselingImages[i]);
        } else {
            // skip
        }
        return DBStory;
    }

    private boolean checkContinuousEmotion() {
        // 부정적 감정이 5일 이상 지속되는지 확인.
        return false;
    }

    // 감정 기록 후 메인 화면으로 돌아간다.
    public void quitComm() {
        recordDiary.saveRecordData();
    }
}
