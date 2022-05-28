package com.example.t16_capstone;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RecordDiary {
    private byte[] facePhoto;
    private float[] recordAnalysValue;
    private String emotionResult;
    private String[] diaryQuestion;
    private String[] diaryAnswer;
    private DatabaseService dbsvs;

    RecordDiary(Activity menu, Uri facePhotoUri, float[] recordAnalysValue, String emotionResult) {
        dbsvs = new DatabaseService(menu);

        // 이미지 저장
        Bitmap facePhotoBitmap = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                facePhotoBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(menu.getContentResolver(), facePhotoUri));
            } else {
                facePhotoBitmap = MediaStore.Images.Media.getBitmap(menu.getContentResolver(), facePhotoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 원본 이미지 화질, 크기 축소 후 정사각형 모양으로 Crop
        facePhotoBitmap = resizeBitmap(facePhotoBitmap, 1024);
        facePhotoBitmap = cropBitmap(facePhotoBitmap);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        facePhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] photoByteData = outputStream.toByteArray();

        this.facePhoto = photoByteData;
        this.recordAnalysValue = recordAnalysValue;
        this.emotionResult = emotionResult;
    }

    private Bitmap cropBitmap(Bitmap original) {
        Bitmap result = Bitmap.createBitmap(original
                , 0 //X 시작위치
                , original.getHeight() / 4 //Y 시작위치 (원본의 4/1지점)
                , original.getWidth() // 넓이
                , original.getWidth()); // 높이 (가로길이 만큼(1024픽셀))
        if (result != original) {
            original.recycle();
        }
        return result;
    }

    private Bitmap resizeBitmap(Bitmap original, int resizeWidth) {
        double aspectRatio = (double) original.getHeight() / (double) original.getWidth();
        int targetHeight = (int) (resizeWidth * aspectRatio);
        Bitmap result = Bitmap.createScaledBitmap(original, resizeWidth, targetHeight, false);
        if (result != original) {
            original.recycle();
        }
        return result;
    }

    public void saveRecordData() {
        // 답변들 받아오기
        ArrayList<String>[] temp = ((CommunicationMenu)CommunicationMenu.thisContext).receiveStoryDiary();
        diaryQuestion = temp[0].toArray(new String[temp[0].size()]);
        diaryAnswer = temp[1].toArray(new String[temp[1].size()]);

        System.out.println(diaryQuestion.length + " " + diaryAnswer.length);
        for(int i =0; i < diaryQuestion.length; i++)
            System.out.println(diaryQuestion[i]);
        // db 저장.
        // 1개의 개수만 가지는 db
        dbsvs.recordDiaryAndResult(facePhoto, recordAnalysValue, emotionResult);
        // 여러개를 가질 수 있는 db
        if(diaryAnswer.length == 0 && diaryQuestion.length == 0) {
            dbsvs.insertDiaryContentsDB(null, null);
        } else {
            for (int i = 0; i < diaryQuestion.length; i++) {
                dbsvs.insertDiaryContentsDB(diaryQuestion[i], diaryAnswer[i]);
            }
        }
        // contentsDB를 읽어올 때 null임을 판별할 필요가 있음.

        // db 사용. 디버깅 print
        Cursor[] cursors;
        cursors = dbsvs.getResultAndDiaryContentsByKey(1);
        cursors[0].moveToNext();
        cursors[1].moveToNext();
        System.out.println(cursors[0].getFloat(1) + " " + cursors[0].getString(9));
        System.out.println(cursors[1].getString(3));
    }

}
