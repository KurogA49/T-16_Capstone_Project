package com.example.t16_capstone;

import android.app.Activity;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.nio.file.Path;
import java.util.Comparator;

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

    private EmotionList[] faceAnalysResult;
    // anger, contempt, disgust, fear, happiness, neutral, sadness, surprise
    private String emotionResult;
    private Activity menu;

    AnalysisBinding(Activity menu) {
        this.menu = menu;
    }

    String analysFaceWithAPI(String faces) throws JSONException {
        // API에서 받아온 JSON데이터를 처리 후 이용합니다.
        JSONArray jArray = new JSONArray(faces);
        JSONObject inner_json = jArray.getJSONObject(0);
        JSONObject faceObject = inner_json.getJSONObject("faceAttributes");
        JSONObject emotionObject = faceObject.getJSONObject("emotion");

        faceAnalysResult = new EmotionList[]{
                new EmotionList("anger", (float) emotionObject.getDouble("anger")),
                new EmotionList("contempt", (float) emotionObject.getDouble("contempt")),
                new EmotionList("disgust", (float) emotionObject.getDouble("disgust")),
                new EmotionList("fear", (float) emotionObject.getDouble("fear")),
                new EmotionList("happiness", (float) emotionObject.getDouble("happiness")),
                new EmotionList("neutral", (float) emotionObject.getDouble("neutral")),
                new EmotionList("sadness", (float) emotionObject.getDouble("sadness")),
                new EmotionList("surprise", (float) emotionObject.getDouble("surprise"))
        };
        Arrays.sort(faceAnalysResult);
        System.out.println(faces);
        System.out.println(faceAnalysResult[faceAnalysResult.length-1].getEmotion() + " " +faceAnalysResult[faceAnalysResult.length-1].getValue());

        if (faceAnalysResult[faceAnalysResult.length-1].getEmotion() == "neutral") {
            if (faceAnalysResult[faceAnalysResult.length-1].getValue() >= 0.9) {
                return "평범한";
            } else if (faceAnalysResult[faceAnalysResult.length-2].getValue() >= 0.1) {
                switch (faceAnalysResult[faceAnalysResult.length-2].getEmotion()) {
                    case "anger":
                    case "contempt":
                    case "disgust":
                        return "기분나쁜";
                    case "fear":
                        return "불안한";
                    case "happiness":
                        return "기쁜";
                    case "sadness":
                        return "슬픈";
                    case "surprise":
                        return "당황스런";
                    default:
                        System.err.println("감정 결과 오류");
                        System.exit(0);
                        return null;
                }
            } else return "복잡함";
        } else {
            switch (faceAnalysResult[faceAnalysResult.length-1].getEmotion()) {
                case "anger":
                case "contempt":
                case "disgust":
                    return "기분나쁜";
                case "fear":
                    return "불안한";
                case "happiness":
                    return "기쁜";
                case "sadness":
                    return "슬픈";
                case "surprise":
                    return "당황스런";
                default:
                    System.err.println("감정 결과 오류");
                    System.exit(0);
                    return null;
            }
        }
        // 기쁜, 평범한, 당황스런, 기분나쁜, 불안한, 슬픈, 복잡한
    }

    String analysEmotion(int btnId) {
        switch (btnId) {
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

    private Path saveFacePhoto() {
        return null;
    }

    public void openCommModel(String emotionResult) {
        // emotionResult 값을 전달
        Intent intent = new Intent(menu, CommunicationMenu.class);
        intent.putExtra("emotionResult", emotionResult);
        // 화면전환 애니메이션 제거
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        menu.startActivity(intent);
        menu.finish(); //액티비티 종료
    }


}
