/*
package com.example.t16_capstone;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class FaceAnalysisAPI extends AppCompatActivity {

    Button process, takePicture;
    ImageView imageView, hidden;

    private FaceServiceClient faceServiceClient;
    Bitmap mBitmap;
    Boolean ready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.analysis_menu_camera2);

        //IMPORTANT!!------------------------------------------------------------------------------
        //Replace the below tags <> with your own endpoint and API Subscription Key.
        //For help with this, read the project's README file.
        faceServiceClient = new FaceServiceRestClient("https://koreacentral.api.cognitive.microsoft.com/face/v1.0", "b0e2b0a6353340b7bb2a99f1fa19c2d7");

        takePicture = findViewById(R.id.takePic);
        imageView = findViewById(R.id.imageView);
        hidden = findViewById(R.id.hidden);
        imageView.setVisibility(View.INVISIBLE);

        process = findViewById(R.id.processClick);
        // 카메라 화면으로 이동
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AnalysisCameraMenu2.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 버전에 따라 MediaStore.ACTION_IMAGE_CAPTURE 인텐트에 셀피 화면을 띄우도록 하는 코드
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                    } else {
                        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    }
                    // onActivityResult에 결과 값을 줌
                    startActivityForResult(intent, 100);
                }
            }
        });

        // 감정 값 화면으로 이동
        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ready) {
                    detectandFrame(mBitmap);
                } else {
                    makeToast("사진을 찍어 주세요");
                }
            }
        });
    }

    // 이동한 화면(startActivityForResult(intent, 100);) 화면에서 결과를 전달 받는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                imageView.setVisibility(View.VISIBLE);
                // data인자 값을 받아들임. 실질적 이미지는 mBitmap 변수에 담겨있음.
                mBitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(mBitmap);
                ready = true;
                hidden.setVisibility(View.INVISIBLE);
            }
        }
    }

    // 감정 값 화면 이동 메소드
    private void detectandFrame(final Bitmap mBitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // mBitmap을 JPEG로 압축
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream((outputStream.toByteArray()));

        // 비동기 작업을 위한 객체
        AsyncTask<InputStream, String, Face[]> detectTask = new AsyncTask<InputStream, String, Face[]>() {
            // 로딩 화면을 띄우기 위한 액티비티 설정
            ProgressDialog pd = new ProgressDialog(AnalysisMenu.this);

            @Override
            protected Face[] doInBackground(InputStream... inputStreams) {

                publishProgress("친구님 표정 관찰 중!");
                //This is where you specify the FaceAttributes to detect. You can change this for your own use.
                FaceServiceClient.FaceAttributeType[] faceAttr = new FaceServiceClient.FaceAttributeType[]{
                        FaceServiceClient.FaceAttributeType.HeadPose,
                        FaceServiceClient.FaceAttributeType.Age,
                        FaceServiceClient.FaceAttributeType.Gender,
                        FaceServiceClient.FaceAttributeType.Emotion,
                        FaceServiceClient.FaceAttributeType.FacialHair
                };

                try {
                    Face[] result = faceServiceClient.detect(inputStreams[0],
                            true,
                            false,
                            faceAttr);

                    if (result == null) {
                        publishProgress("오늘 눈이 잘 안보이네요...");
                    }

                    publishProgress(String.format("Detection Finished. %d face(s) detected", result.length));
                    return result;
                } catch (Exception e) {
                    publishProgress("감지 실패: " + e.getMessage());
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                pd.show();
            }

            @Override
            protected void onProgressUpdate(String... values) {
                pd.setMessage(values[0]);
            }

            @Override
            protected void onPostExecute(Face[] faces) {
                pd.dismiss();
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                Gson gson = new Gson();
                String data = gson.toJson(faces);
                if (faces == null || faces.length == 0) {
                    makeToast("친구님 찾아보는 중...\n사진을 다시 찍어주세요!");
                } else {
                    intent.putExtra("list_faces", data);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    intent.putExtra("image", byteArray);
                    startActivity(intent);
                }

            }
        };
        detectTask.execute(inputStream);
    }
    // 이미지 저장은 인식이 성공적으로 된 마지막 사진만


    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
*/