package com.example.t16_capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSION = 11;

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;
    private ImageButton characterMainBtn;
    private ImageView speechBubbleMain;
    private TextView speechMain;
    private LinearLayout mainSpeechLayout;

    // 데이터베이스 초기화
    private DatabaseService dbsvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // db 첫 초기화
        DatabaseService dbsvs = new DatabaseService(this);

        // 권한 엑세스 함수
        checkPermission();

        // 앱 최초 실행 열부 판단
        SharedPreferences pref = getSharedPreferences("checkFirst", Activity.MODE_PRIVATE);
        boolean checkFirst = pref.getBoolean("checkFirst", false); // 실행 전 false
        if (checkFirst == false) {
            // 튜토리얼 실행
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("checkFirst", true); // 실행 되면 true 로
            editor.commit();

            Intent tutorialIntent = new Intent(MainActivity.this, TutorialActivity.class);
            startActivity(tutorialIntent);
            finish();
        }

        ImageButton settingBtn = (ImageButton) findViewById(R.id.settingBtn);
        ImageButton exitBtn = (ImageButton) findViewById(R.id.exitBtn);
        ImageButton diaryBtn = (ImageButton) findViewById(R.id.diaryBtn);
        mainSpeechLayout = findViewById(R.id.mainSpeechLayout);
        speechMain = findViewById(R.id.speechMain);

        // 하루 판단 메소드. 판단 후 하루 기록이 진행되지 않았으면 말풍선을 띄운다.
        if (!dbsvs.isAnalysedToday()) {
            speechMain.setText("오늘을 일기를 쓰시려면\n저를 눌러주세요!");
            mainSpeechLayout.setVisibility(View.VISIBLE);
        }

        // 애니메이션
        characterMainBtn = findViewById(R.id.characterMainBtn);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating);
        characterMainBtn.startAnimation(animation);

        speechBubbleMain = findViewById(R.id.speechBubbleMain);
        AnimationDrawable animationDrawable = (AnimationDrawable) speechBubbleMain.getBackground();
        animationDrawable.start();

        AnimationDrawable animationDrawable2 = (AnimationDrawable) exitBtn.getDrawable();
        animationDrawable2.start();

        // 디버그 버튼(분석화면 제약없이 이동 가능)
        ImageButton debugBtn = findViewById(R.id.debugBtn);
        debugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AnalysisMenu.class);
                intent.putExtra("Argv", "MainToDesc");
                // 화면전환 애니메이션 제거
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish(); //액티비티 종료
            }
        });

        // 캐릭터를 클릭하면 하루에 한 번 감정 분석 진행 가능
        characterMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dbsvs.isAnalysedToday()) {
                    Intent intent = new Intent(getApplicationContext(), AnalysisMenu.class);
                    intent.putExtra("Argv", "MainToDesc");
                    // 화면전환 애니메이션 제거
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish(); //액티비티 종료
                } else {
                    speechMain.setText("오늘은 일기를 작성하셨네요.\n 일기에서 확인해보세요.");
                    mainSpeechLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // 원래는 종료되는 버튼임
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 프로그램 완전 종료
                if (Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
                System.exit(0);
            }
        });

        // 원래는 감정 기록 리스트로 가는 버튼임
        diaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecordListMenu.class);
                // 화면전환 애니메이션 제거
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                //finish(); //액티비티 종료
            }
        });

        // 원래는 세팅 화면으로 가는 버튼임
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingDialog.class);
                startActivity(intent);
            }
        });
    }

    //권한 요청
    public void checkPermission() {
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA); // 카메라 권한
        int permissionRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE); // 파일 읽어오기 권한
        int permissionWrite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE); // 파일 저장 권한

        // 3가지 권한 중 한개라도 없으면 권한 요청 실행
        if (permissionCamera != PackageManager.PERMISSION_GRANTED
                || permissionRead != PackageManager.PERMISSION_GRANTED
                || permissionWrite != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "이 앱을 실행하기 위해 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // 권한이 취소되면 result 배열은 비어있다.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "권한 확인", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();
                    finish(); //권한이 없으면 앱 종료
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    /*
    public void onClick(View view) {
        // 화면 전환
        Intent intent = new Intent(this, AnalysisMenu.class);
        // 갈 화면에 줄 인자를 설정한다.
        intent.putExtra("Argv", "MainToDesc");
        // 화면전환 애니메이션 제거
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish(); //액티비티 종료
    }
    */

}