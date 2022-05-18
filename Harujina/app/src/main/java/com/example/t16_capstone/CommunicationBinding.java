package com.example.t16_capstone;

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
    private String emotionResult;
    private StoryContents DBStory;
    String[] recommendCounseling = {"끝내기전 저 긴히 할말이 있어요.", "최근에 친구님이 많이 힘드셨다는 걸 느껴요...", "힘드시다는 것이 말할 수 없는 비밀이시라면 숨기셔두돼요.",
                                    "그래도 말해서 나아지는 일이 간혹 있더라구요.", "그것이 적어도 나 자신이어도 좋구요..!", "아니면 혹시 말하고 싶은 곳을 찾고 거라면...",
                                    "상담사를 찾아가 보는 것도 권해요.", "물론 상담사도 사람이니 꺼려질 수도 있고, 맞지 않을수도 있어요.", "어찌됐건 사람들은 서로 공유하고 싶은 사람을 찾는 거겠죠.",
                                    "그것이 고통이라도요.", "만약 상담에 가신다면, 일기장을 보여주는 것도 도움이 될거에요.", "나아질 미래를 응원할게요.", "그리고 언제나 자신을 믿을 수 있길 바래요."};
    int[] recommendCounselingImages = {0, 7, 5, 0, 5, 7, 0, 0, 0, 5, 0, 5, 0};

    public CommunicationBinding(String emotionResult) {
        this.emotionResult = emotionResult;
        DBStory = new StoryContents();
    }

    // StoryContents객체에 add를 통해 내용 구분없이 쭉 붙인다.
    public StoryContents readStoryContents() {
        // 기본 상호작용

        // 추천 상호작용

        // 상담권유 상호작용
        if(checkContinuousEmotion()) {
            for(int i =0; i<recommendCounseling.length; i++)
                DBStory.addStory(recommendCounseling[i], 0, recommendCounselingImages[i]);
        } else {

        }
        return DBStory;
    }

    private boolean checkContinuousEmotion() {
        // 부정적 감정이 5일 이상 지속되는지 확인.
        return false;
    }
}
