package com.example.t16_capstone;

import android.app.Activity;

import java.util.ArrayList;

public class RecordDiary {
    private byte[] facePhoto;
    private float[] recordAnalysValue;
    private String emotionResult;
    private String[] diaryQuestion;
    private String[] diaryAnswer;
    private DatabaseService dbsvs;

    RecordDiary(Activity menu, byte[] facePhoto, float[] recordAnalysValue, String emotionResult) {
        dbsvs = new DatabaseService(menu);

        this.facePhoto = facePhoto;
        this.recordAnalysValue = recordAnalysValue;
        this.emotionResult = emotionResult;
    }

    public void saveRecordData() {
        // 답변들 받아오기
        ArrayList<String>[] temp = ((CommunicationMenu)CommunicationMenu.thisContext).receiveStoryDiary();
        diaryQuestion = temp[0].toArray(new String[temp[0].size()]);
        diaryAnswer = temp[1].toArray(new String[temp[1].size()]);

        // db사용. diaryQuestion 없을 때 대비 필요
        System.out.println(facePhoto[facePhoto.length-1]);
        System.out.println(recordAnalysValue[0]);
        System.out.println(emotionResult);
        if(diaryQuestion != null) {
            System.out.println(diaryQuestion[0]);
            System.out.println(diaryAnswer[0]);
        } else System.out.println("diaryQuestion & Answer is null");
    }

}
