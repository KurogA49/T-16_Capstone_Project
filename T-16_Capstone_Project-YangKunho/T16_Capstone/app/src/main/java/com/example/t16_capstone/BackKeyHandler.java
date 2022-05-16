package com.example.t16_capstone;

import android.app.Activity;
import android.widget.Toast;

public class BackKeyHandler {
    //https://dev-imaec.tistory.com/10

    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private Toast toast;
    // 종료시킬 Activity
    private Activity activity;

    /**
     * 생성자
     *
     * @param activity 종료시킬 Activity.
     */
    public BackKeyHandler(Activity activity) {
        this.activity = activity;
    }

    /**
     * Default onBackPressed()
     * 2 seconds
     */
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    /**
     * Default showGuide()
     */
    private void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Toast 메세지 사용자 지정 showGuide(String msg)
     *
     * @param msg Toast makeText()의 2번째 인자에 들어갈 text
     */
    private void showGuide(String msg) {
        toast = Toast.makeText(activity, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
