package com.example.t16_capstone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EmotionCheck extends AppCompatActivity {
    String emotion;
    TextView t1;
    TextView t2;
    Button btn1;
    Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_check);
        emotion = getIntent().getStringExtra("Emotion");
        t1 = findViewById(R.id.textView1);
        t2 = findViewById(R.id.textView2);
        btn1 = findViewById(R.id.btn_emotion_check_retry);
        btn2 = findViewById(R.id.btn_emotion_check_next);

        if (emotion.equals("Happiness") || emotion.equals("행복")) {
            emotion = "행복";

        } else if (emotion.equals("Sadness") || emotion.equals("우울")) {
            emotion = "우울";

        } else if (emotion.equals("Disgust") || emotion.equals("역겨움")) {
            emotion = "역겨움";

        } else if (emotion.equals("Fear") || emotion.equals("두려움")) {
            emotion = "두려움";

        } else if (emotion.equals("Surprise") || emotion.equals("놀람")) {
            emotion = "놀람";

        } else if (emotion.equals("Anger") || emotion.equals("화남")) {
            emotion = "화남";

        } else if (emotion.equals("Contempt") || emotion.equals("경멸")) {
            emotion = "경멸";
        } else if (emotion.equals("Neutral") || emotion.equals("중립")) {
            emotion = "중립";
        }
        t1.setText(emotion);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmotionCheck.this, AnalysisMenu.class);
                startActivity(intent);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmotionCheck.this, AnalysisMenu.class);
                intent.putExtra("Emotion", emotion);
                startActivity(intent);
            }
        });
    }
}
