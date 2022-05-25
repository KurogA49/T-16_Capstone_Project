package com.example.t16_capstone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

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
        dbsvs.insertDiaryDB(new byte[] {0, 0, 0, 0});
        Cursor cursor = dbsvs.selectAllDiaryDB();
        if(cursor.getColumnCount() == 0)
            System.out.println("조회가능한 컬럼 없음");
        else {
            cursor.moveToNext();
            System.out.println(cursor.getString(2));
        }

        // 권한 엑세스 함수
        cameraAccess();

        // 애니메이션
        characterMain = (ImageView) findViewById(R.id.characterMain);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.floating);
        characterMain.startAnimation(animation);

        speechBubbleMain = findViewById(R.id.speechBubbleMain);
        AnimationDrawable animationDrawable = (AnimationDrawable)speechBubbleMain.getBackground();
        animationDrawable.start();
    }

    public void cameraAccess() {
        //카메라 권한 확인 : https://bottlecok.tistory.com/entry/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EA%B6%8C%ED%95%9C%EC%9A%94%EC%B2%AD-%EA%B6%8C%ED%95%9C%EC%84%A4%EC%A0%95-%ED%8D%BC%EB%AF%B8%EC%85%98%EC%B2%B4%ED%81%AC
        int permssionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permssionCheck != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 승인이 필요합니다", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "000부분 사용을 위해 카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                Toast.makeText(this, "000부분 사용을 위해 카메라 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
        }
        { // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "승인이 허가되어 있습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "아직 승인받지 않았습니다.", Toast.LENGTH_LONG).show();
                cameraAccess();
            }
            return;
        }

    }
}