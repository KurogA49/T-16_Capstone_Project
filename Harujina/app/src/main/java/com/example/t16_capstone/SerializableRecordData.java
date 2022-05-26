package com.example.t16_capstone;

import java.io.Serializable;

public class SerializableRecordData implements Serializable {
    // 참고한 사이트에 적힌 UID사용
    private static final long serialVersionUID = 1209L;

    private byte[] serialPhoto;
    private float[] serialEmotionValue;

    public byte[] getSerialPhoto() {
        return serialPhoto;
    }

    public float[] getSerialEmotionValue() {
        return serialEmotionValue;
    }

    public void setSerialEmotionValue(float[] serialEmotionValue) {
        this.serialEmotionValue = serialEmotionValue;
    }

    public void setSerialPhoto(byte[] serialPhoto) {
        this.serialPhoto = serialPhoto;
    }

    public SerializableRecordData() {

    }
}
