package com.example.t16_capstone;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_PERMISSION = 11;

    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;
    private ImageView characterMain;
    private ImageView speechBubbleMain;

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

        // 애니메이션
        characterMain = (ImageView) findViewById(R.id.characterMain);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating);
        characterMain.startAnimation(animation);

        speechBubbleMain = findViewById(R.id.speechBubbleMain);
        AnimationDrawable animationDrawable = (AnimationDrawable) speechBubbleMain.getBackground();
        animationDrawable.start();

        // 종료 버튼
        ImageButton exitBtn = (ImageButton) findViewById(R.id.exitBtn);
        exitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("정말로 종료하시겠습니까?");
                builder.setTitle("종료 알림창")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("종료 알림창");
                alert.show();
            }
        });

        // 원래는 감정 기록 리스트로 가는 버튼임
        ImageButton nextBtn = (ImageButton) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {

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

        // 원래는 세팅 화면으로 가는 버튼임
        ImageButton settingBtn = (ImageButton) findViewById(R.id.settingBtn);
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