package com.example.t16_capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AnalysisMenu extends AppCompatActivity {

    public static Context thisContext;
    private BackKeyHandler backKeyHandler = new BackKeyHandler(this);
    private TextView descText;
    private ImageButton nextBtn;
    private Button[] emoBtnGroup;
    private Button reCapYesBtn;
    private Button reCapNoBtn;
    private LinearLayout emoBtnGroupLayout;
    private LinearLayout reCaptureLayout;
    private ImageView characterDesc;
    private ImageView speechBubbleDesc;
    private Animation characterAnim;
    private Animation viewAnim;
    private Drawable[] drawable;

    private String emotionResult = ""; // 기쁜, 평범한, 당황스런, 기분나쁜, 불안한, 슬픈, 복잡한
    private int descCursor;
    private final int GO_CAMERA_MENU = 100;
    private String[] desc = {"안녕하세요!", "오늘도 친구님의 얼굴을 보기위해 왔어요.", "오늘의 얼굴을 보여주세요!", // 초기 0 ~ 2
            "흐으음", "제가 보기엔 ", " 하루셨던 것 같네요.",   // 감정 결과 3 ~ 5
            "아니면 사실 다른 기분이신가요?", "알겠어요.", // 기본 흐름 6 ~ 7
            "다시 찍어드릴까요?", "이번엔 제대로 찍어드릴게요!", "복잡한 기분이신가 봐요.."};  // emotionResult == "복잡한" 일때 8 ~ 10
    private Bitmap facePhotoBitmap;

    // 바인딩 객체 호출
    private AnalysisBinding analysisBinding;
    private FaceAnalysisAPI faceAnalysisAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_menu_desc);

        descText = findViewById(R.id.descText);
        characterDesc = findViewById(R.id.characterDesc);
        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(nextEvent);

        reCapYesBtn = findViewById(R.id.reCapYesBtn);
        reCapNoBtn = findViewById(R.id.reCapNoBtn);
        reCapYesBtn.setOnClickListener(reCapture);
        reCapNoBtn.setOnClickListener(reCapture);
        emoBtnGroup = new Button[8];
        emoBtnGroup[0] = findViewById(R.id.happyBtn);
        emoBtnGroup[1] = findViewById(R.id.normalBtn);
        emoBtnGroup[2] = findViewById(R.id.embarrassBtn);
        emoBtnGroup[3] = findViewById(R.id.annoyedBtn);
        emoBtnGroup[4] = findViewById(R.id.anxiousBtn);
        emoBtnGroup[5] = findViewById(R.id.sadBtn);
        emoBtnGroup[6] = findViewById(R.id.complicateBtn);
        emoBtnGroup[7] = findViewById(R.id.noBtn);
        for (int i = 0; i < emoBtnGroup.length; i++)
            emoBtnGroup[i].setOnClickListener(analysWithAnswer);
        emoBtnGroupLayout = findViewById(R.id.emoBtnGroupLayout);
        reCaptureLayout = findViewById(R.id.reCaptureLayout);

        analysisBinding = new AnalysisBinding(this);
        faceAnalysisAPI = new FaceAnalysisAPI(this);

        thisContext = this;

        // 선언 끝

        // 애니메이션
        characterAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating);
        characterDesc.startAnimation(characterAnim);

        speechBubbleDesc = findViewById(R.id.speechBubbleDesc);
        AnimationDrawable animationDrawable = (AnimationDrawable) speechBubbleDesc.getBackground();
        animationDrawable.start();

        AnimationDrawable[] animationDrawableEmoBtn = new AnimationDrawable[8];
        for (int i = 0; i < emoBtnGroup.length; i++) {
            animationDrawableEmoBtn[i] = (AnimationDrawable) emoBtnGroup[i].getBackground();
            animationDrawableEmoBtn[i].start();
        }

        AnimationDrawable[] animationDrawableReCap = new AnimationDrawable[2];
        animationDrawableReCap[0] = (AnimationDrawable) reCapNoBtn.getBackground();
        animationDrawableReCap[1] = (AnimationDrawable) reCapYesBtn.getBackground();
        animationDrawableReCap[0].start();
        animationDrawableReCap[1].start();

        // 캐릭터 이미지 설정
        drawable = new Drawable[3];
        drawable[0] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_0); // 기본
        drawable[1] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_camera); // 카메라 들고있는
        drawable[2] = ContextCompat.getDrawable(getApplicationContext(), R.drawable.character_ok); // 카메라 확인하는

        // 화면전환 시 수행
        Intent intent = getIntent();
        String argv = intent.getStringExtra("Argv");
        String faces = intent.getStringExtra("list_faces");
        SerializableRecordData serializableRecordData = (SerializableRecordData) intent.getSerializableExtra("sendPhotoData");
        switch (argv) {
            case "MainToDesc":      // "안녕하세요!"
                descCursor = 0;
                break;
            case "CameraToDesc":    // "흐으음"
                // API에게 faces값을 받았으므로 바인딩객체에게 전달.
                try {
                    emotionResult = analysisBinding.analysFaceWithAPI(faces);
                } catch (JSONException e) {
                    System.err.println(e);
                }
                analysisBinding.setFacePhotoUri(serializableRecordData.getSerialPhotoUri());
                descCursor = 3;
                // 캐릭터 이미지 수정
                characterDesc.setImageDrawable(drawable[2]);
                break;
            case "PhotoURINull":      // "찍는 도중 카메라를 돌리면 제가 어지러워요...! \n 다시 한번 사진 부탁드릴게요!"
                descText.setText("찍는 도중 카메라를 돌리면 제가 어지러워요...!\n다시 한번 사진 부탁드릴게요!");
                descCursor = GO_CAMERA_MENU;
                return;// onCreate종료
            default:
                System.err.println("액티비티 인자 값 오류");
                System.exit(0);
        }

        descText.setText(desc[descCursor++]);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        backKeyHandler.onBackPressed();
    }

    // ImageFile의 경로를 가져올 메서드 선언
    private File createImageFile() throws IOException {
        // 파일이름을 세팅 및 저장경로 세팅
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        return image;
    }

    Uri photoUri;

    private void faceAnalysis() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // 버전에 따라 MediaStore.ACTION_IMAGE_CAPTURE 인텐트에 셀피 화면을 띄우도록 하는 코드
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
            } else {
                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            }

            // 원본 이미지 저장 후, 호출하여 원본 해상도의 이미지를 사용
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                System.err.println(ex);
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                // onActivityResult에 결과 값을 줌
                startActivityForResult(intent, 100);
            }
        }
    }

    // 이동한 화면(startActivityForResult(intent, 100);) 화면에서 결과를 전달 받는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100)
            if (resultCode == RESULT_OK) {
                // API에 이미지를 전달함
                faceAnalysisAPI.faceAnalysis(photoUri);
            }
    }

    public void setDescText(String s) {
        descText.setText(s);
    }

    Button.OnClickListener nextEvent = new Button.OnClickListener() {
        public void onClick(View v) {
            // descCursor에 맞게 desc를 출력하거나 액티비티 호출.
            if (descCursor == GO_CAMERA_MENU) {
                faceAnalysis();
                return;
            } else if (descCursor < desc.length)
                descText.setText(desc[descCursor]);
            else analysisBinding.openCommModel(emotionResult);

            // 뷰 설정 확인
            emoBtnGroupLayout.setVisibility(View.GONE);
            reCaptureLayout.setVisibility(View.GONE);
            nextBtn.setVisibility(View.VISIBLE);

            // desc 출력 외의 설정이 필요할 경우
            if (descCursor == 2) { // "오늘의 얼굴을 보여주세요!"
                descCursor = GO_CAMERA_MENU;
                // 애니메이션 수정
                characterAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_stop);
                characterDesc.startAnimation(characterAnim);
                // 캐릭터 이미지 수정
                characterDesc.setImageDrawable(drawable[1]);
                return;
            } else if (descCursor == 6) {    // "아니면 사실 다른 기분이신가요?""
                // 감정 선택 뷰를 나타낸다.
                emoBtnGroupLayout.setVisibility(View.VISIBLE);
                // 뷰 애니메이션 설정
                viewAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_rise);
                emoBtnGroupLayout.startAnimation(viewAnim);

                nextBtn.setVisibility(View.GONE);
            } else if (descCursor == 7) {    // "알겠어요"
                // 애니메이션 수정
                characterAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.floating_stop);
                characterDesc.startAnimation(characterAnim);
                // 캐릭터 이미지 수정
                characterDesc.setImageDrawable(drawable[2]);
                // desc를 끝낸다. 길이보다 높은 인덱스를 줌.
                descCursor = desc.length;
                return;
            } else if (descCursor == 8) {    // "다시 찍어드릴까요?"
                // 재 촬영 선택 뷰를 나타낸다.
                reCaptureLayout.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.GONE);
            } else if (descCursor == 10) {  // "복잡한 기분이신가 봐요.." -> "알겠어요."로 이동.
                descCursor = 7;
                return;
            }

            if (descCursor == 4) {   // "제가 보기엔 ", " 하루셨던 것 같네요."
                // 기본 이미지로 변경.
                characterDesc.setImageDrawable(drawable[0]);
                descText.append(emotionResult);
                descText.append(desc[++descCursor]);
                if (emotionResult == "복잡한") {
                    descCursor = 8; // "다시 찍어드릴까요?"
                    return;
                }
            }

            descCursor++;
        }
    };

    Button.OnClickListener reCapture = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.reCapYesBtn:
                    faceAnalysis();
                    break;
                case R.id.reCapNoBtn:
                    descCursor = 10; // "복잡한 기분이신가 봐요.."
                    nextEvent.onClick(nextBtn);
                    break;
                default:
                    System.err.println("재 촬영 선택 오류");
                    System.exit(0);
                    break;
            }
        }
    };

    Button.OnClickListener analysWithAnswer = new Button.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.happyBtn:
                case R.id.normalBtn:
                case R.id.embarrassBtn:
                case R.id.annoyedBtn:
                case R.id.anxiousBtn:
                case R.id.sadBtn:
                case R.id.complicateBtn:
                    emotionResult = analysisBinding.analysEmotion(v.getId());
                case R.id.noBtn:
                    nextEvent.onClick(nextBtn);
                    break;
                default:
                    System.err.println("감정 선택 오류");
                    System.exit(0);
                    break;
            }
        }
    };

}