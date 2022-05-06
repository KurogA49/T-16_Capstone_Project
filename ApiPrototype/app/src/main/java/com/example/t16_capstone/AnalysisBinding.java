package com.example.t16_capstone;

import android.app.Activity;
import android.graphics.Bitmap;
import java.util.Arrays;
import java.nio.file.Path;

class EmotionList implements Comparable<EmotionList> {
    private String emotion;
    private float value;

    EmotionList(String emotion, float value) {
        this.emotion = emotion;
        this.value = value;
    }

    @Override
    public int compareTo(EmotionList emotionList) {
        // TODO Auto-generated method stub
        if (this.value < emotionList.value) {
            return -1;
        } else if (this.value < emotionList.value) {
            return 0;
        } else {
            return 1;
        }
    }

    public float getValue() {
        return value;
    }

    public String getEmotion() {
        return emotion;
    }
}

public class AnalysisBinding {
    // API APIForAnalysis;
    private Bitmap photoForAnalysis;
    private EmotionList[] faceAnalysResult;
    // anger, contempt, disgust, fear, happiness, neutral, sadness, surprise
    private String emotionResult;
    private boolean faceCheck;
    private Activity menu;

    AnalysisBinding(Activity menu) {
        this.menu = menu;
    }

    String analysFaceWithAPI() {
        faceAnalysResult = new EmotionList[]{
                new EmotionList("anger", 0),
                new EmotionList("contempt", 0),
                new EmotionList("disgust", 0),
                new EmotionList("fear", 0),
                new EmotionList("happiness", 0),
                new EmotionList("neutral", 0),
                new EmotionList("sadness", 0),
                new EmotionList("surprise", 0)
        };
        Arrays.sort(faceAnalysResult);

        if(faceAnalysResult[0].getValue() > 0.5) {
            switch(faceAnalysResult[0].getEmotion()) {
                case "anger":
                case "contempt":
                case "disgust":
                    return "기분나쁜";
                case "fear":
                    return "불안한";
                case "happiness":
                    return "기쁜";
                case "neutral":
                    return "평범한";
                case "sadness":
                    return "슬픈";
                case "surprise":
                    return "당황스런";
                default:
                    System.err.println("감정 결과 오류");
                    System.exit(0);
                    return null;
            }
        } else {
            return "복잡한";
        }
        // 기쁜, 평범한, 당황스런, 기분나쁜, 불안한, 슬픈, 복잡한
    }

    String analysEmotion(int btnId) {
        switch(btnId) {
            case R.id.happyBtn:
                emotionResult = "기쁜";
                return "기쁜";
            case R.id.normalBtn:
                emotionResult = "평범한";
                return "평범한";
            case R.id.embarrassBtn:
                emotionResult = "당황스런";
                return "당황스런";
            case R.id.annoyedBtn:
                emotionResult = "기분나쁜";
                return "기분나쁜";
            case R.id.anxiousBtn:
                emotionResult = "불안한";
                return "불안한";
            case R.id.sadBtn:
                emotionResult = "슬픈";
                return "슬픈";
            case R.id.complicateBtn:
                emotionResult = "복잡한";
                return "복잡한";
            default:
                System.err.println("감정 선택 오류");
                return null;
        }
    }

    void closeMenu() {
        menu.finish();
        openCommModel();
    }

    private boolean isFaceChecked() {
        return false;
    }

    private Path saveFacePhoto() {
        return null;
    }

    private void openCommModel() {
        // emotionResult 값을 전달
    }


}
