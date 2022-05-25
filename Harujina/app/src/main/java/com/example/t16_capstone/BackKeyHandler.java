package com.example.t16_capstone;

import android.app.Activity;
import android.os.Build;
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
            // 완전 종료. 백그라운드에도 남지 않음.
            activity.moveTaskToBack(true); // 태스크를 백그라운드로 이동
            if (Build.VERSION.SDK_INT >= 21) {
                activity.finishAndRemoveTask();
            } else {
                activity.finish();
            }
            System.exit(0);
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
}
