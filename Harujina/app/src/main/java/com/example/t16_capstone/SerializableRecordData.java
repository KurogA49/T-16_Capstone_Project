package com.example.t16_capstone;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

public class SerializableRecordData implements Serializable {
    // 참고한 사이트에 적힌 UID사용
    private static final long serialVersionUID = 1209L;

    private String serialPhotoUriString;
    private float[] serialEmotionValue;

    public Uri getSerialPhotoUri() {
        return Uri.parse(serialPhotoUriString);
    }

    public float[] getSerialEmotionValue() {
        return serialEmotionValue;
    }

    public void setSerialEmotionValue(float[] serialEmotionValue) {
        this.serialEmotionValue = serialEmotionValue;
    }

    public void setSerialPhotoUri(Uri serialPhotoUri) {
        serialPhotoUriString = serialPhotoUri.toString();
    }

    public SerializableRecordData() {

    }
}
