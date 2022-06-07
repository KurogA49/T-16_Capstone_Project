package com.example.t16_capstone;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FaceAnalysisAPI {


    private FaceServiceClient faceServiceClient;
    private Bitmap mBitmap;
    private Activity menu;
    private String faceAnalysResult;

    FaceAnalysisAPI(Activity menu) {
        //IMPORTANT!!------------------------------------------------------------------------------
        //Replace the below tags <> with your own endpoint and API Subscription Key.
        //For help with this, read the project's README file.
        faceServiceClient = new FaceServiceRestClient("https://koreacentral.api.cognitive.microsoft.com/face/v1.0", "b0e2b0a6353340b7bb2a99f1fa19c2d7");
        this.menu = menu;
    }

    public String getAPIdata() {
        return faceAnalysResult;
    }

    // 감정 속도 향상을 위한 리사이징 메소드
    private Bitmap resizeBitmap(Bitmap original, int resize) {
        double aspectRatio = 0;
        int targetHeight = 0;
        int targetWidth = 0;
        Bitmap result = null;
        if(original.getHeight() >= original.getWidth()) {
            aspectRatio = (double) original.getHeight() / (double) original.getWidth();
            targetHeight = (int) (resize * aspectRatio);
            result = Bitmap.createScaledBitmap(original, resize, targetHeight, false);
        } else {
            aspectRatio = (double) original.getWidth() / (double) original.getHeight();
            targetWidth = (int) (resize * aspectRatio);
            result = Bitmap.createScaledBitmap(original, targetWidth, resize, false);
        }

        if (result != original) {
            original.recycle();
        }
        return result;
    }

    // 감정 값 화면 이동 메소드
    public void faceAnalysis(Uri photoUri) {
        Bitmap mBitmap = null;

        if(photoUri == null) {
            Intent intent = new Intent(menu, AnalysisMenu.class);
            intent.putExtra("Argv", "PhotoURINull");

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            menu.startActivity(intent);
            menu.finish();
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(menu.getContentResolver(), photoUri));
            } else {
                mBitmap = MediaStore.Images.Media.getBitmap(menu.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mBitmap = resizeBitmap(mBitmap, 1024);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // mBitmap을 JPEG로 압축
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] photoByteData = outputStream.toByteArray();

        final ByteArrayInputStream inputStream = new ByteArrayInputStream((photoByteData));

        // 비동기 작업을 위한 메소드. 다른 스레드에서 진행되므로 이 메소드는 순차대로 진행되지 않는다.
        AsyncTask<InputStream, String, Face[]> detectTask = new AsyncTask<InputStream, String, Face[]>() {
            // 로딩 화면을 띄우기 위한 액티비티 설정
            ProgressDialog pd = new ProgressDialog(menu);

            @Override
            protected Face[] doInBackground(InputStream... inputStreams) {
                publishProgress("친구님 표정 관찰 중!");
                //This is where you specify the FaceAttributes to detect. You can change this for your own use.
                FaceServiceClient.FaceAttributeType[] faceAttr = new FaceServiceClient.FaceAttributeType[]{
                        FaceServiceClient.FaceAttributeType.Emotion,
                        FaceServiceClient.FaceAttributeType.Noise,
                        FaceServiceClient.FaceAttributeType.Blur,

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
                // 결과 수행
                pd.dismiss();

                Intent intent = new Intent(menu, AnalysisMenu.class);
                Gson gson = new Gson();
                String data = gson.toJson(faces);
                if (faces == null || faces.length == 0) {
                    // 얼굴 인식이 안되었을 경우 분석 메뉴 대사 수정.
                    ((AnalysisMenu)AnalysisMenu.thisContext).setDescText("얼굴이 잘 안나오셨네요. 다시 찍어드릴게요!");
                } else {
                    // 바인딩 객체에 얼굴 사진 저장
                    SerializableRecordData serializableRecordData = new SerializableRecordData();
                    serializableRecordData.setSerialPhotoUri(photoUri);

                    intent.putExtra("sendPhotoData", serializableRecordData);
                    intent.putExtra("list_faces", data);
                    intent.putExtra("Argv", "CameraToDesc");

                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    menu.startActivity(intent);
                    menu.finish();
                }
            }
        };
        detectTask.execute(inputStream);
    }
    // 이미지 저장은 인식이 성공적으로 된 마지막 사진만

    private void makeToast(String s) {
        Toast.makeText(menu, s, Toast.LENGTH_LONG).show();
    }
}
