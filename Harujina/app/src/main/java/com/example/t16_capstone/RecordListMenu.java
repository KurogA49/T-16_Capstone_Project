package com.example.t16_capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordListMenu extends AppCompatActivity {
    DatabaseService dbsvs;
    GridView recordGridView;
    FrameLayout recordPartLayout;
    ListView recordStringView;
    ImageView recordPartImage;
    ImageButton partQuitBtn;
    ImageButton menuQuitBtn;
    TextView emotionResultText;
    TextView angerText;
    TextView contemptText;
    TextView disgustText;
    TextView fearText;
    TextView happinessText;
    TextView neutralText;
    TextView sadnessText;
    TextView surpriseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_list_menu);

        dbsvs = new DatabaseService(this);
        recordGridView = findViewById(R.id.recordGridView);
        recordStringView = findViewById(R.id.recordStringView);
        recordPartLayout = findViewById(R.id.recordPartLayout);
        recordPartImage = findViewById(R.id.recordPartImage);
        partQuitBtn = findViewById(R.id.partQuitBtn);
        menuQuitBtn = findViewById(R.id.menuQuitBtn);
        // 감정 검사 값
        emotionResultText = findViewById(R.id.emotionResultText);
        angerText = findViewById(R.id.angerRatio);
        contemptText = findViewById(R.id.contemptRatio);
        disgustText = findViewById(R.id.disgustRatio);
        fearText = findViewById(R.id.fearRatio);
        happinessText = findViewById(R.id.happinessRatio);
        neutralText = findViewById(R.id.neutralRatio);
        sadnessText = findViewById(R.id.sadnessRatio);
        surpriseText = findViewById(R.id.surpriseRatio);

        partQuitBtn.setOnClickListener(partQuit);
        menuQuitBtn.setOnClickListener(menuQuit);

        // 애니메이션 설정
        AnimationDrawable animationDrawable = (AnimationDrawable) menuQuitBtn.getDrawable();
        animationDrawable.start();

        // 레이아웃 초기 설정
        recordPartLayout.setVisibility(View.GONE);

        RecordListAdapter adapter = new RecordListAdapter();
        Cursor cursor = dbsvs.selectAllDiaryDB();
        ArrayList<RecordPart> recordParts = new ArrayList<>();
        // 역순으로 띄우기 위한 ArrayList 변환
        while(cursor.moveToNext()) {
            recordParts.add(new RecordPart(cursor.getBlob(1), cursor.getString(2), cursor.getInt(0)));
        }
        for(int i = recordParts.size()-1; i >= 0; i--) {
            adapter.addItem(recordParts.get(i));
        }
        recordGridView.setAdapter(adapter);

        recordGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecordPart item = (RecordPart) adapter.getItem(position);
                int diaryKey = item.getDiaryKey();
                showRecordPart(diaryKey, item.getFacePhoto());
                }
            });
    }

    class RecordListAdapter extends BaseAdapter {
        ArrayList<RecordPart> items = new ArrayList<>();

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(RecordPart recordPart) {
            this.items.add(recordPart);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecordPartView recordPartView = null;
            if (convertView == null) {
                recordPartView = new RecordPartView(getApplicationContext());
            } else {
                recordPartView = (RecordPartView) convertView;
            }
            RecordPart recordPart = items.get(position);
            recordPartView.setFacePhoto(recordPart.getFacePhoto());
            recordPartView.setDayString(recordPart.getDayString());

            return recordPartView;
        }
    }

    private void showRecordPart(int diaryKey, Bitmap facePhoto) {
        // 텍스트 설정
        ContentsAdapter adapter = new ContentsAdapter();
        Cursor[] cursors = dbsvs.getResultAndDiaryContentsByKey(diaryKey);

        cursors[0].moveToNext();
        angerText.setText(String.format("%.1f", cursors[0].getFloat(1)*100.0)+"%");
        contemptText.setText(String.format("%.1f", cursors[0].getFloat(2)*100.0)+"%");
        disgustText.setText(String.format("%.1f", cursors[0].getFloat(3)*100.0)+"%");
        fearText.setText(String.format("%.1f", cursors[0].getFloat(4)*100.0)+"%");
        happinessText.setText(String.format("%.1f", cursors[0].getFloat(5)*100.0)+"%");
        neutralText.setText(String.format("%.1f", cursors[0].getFloat(6)*100.0)+"%");
        sadnessText.setText(String.format("%.1f", cursors[0].getFloat(7)*100.0)+"%");
        surpriseText.setText(String.format("%.1f", cursors[0].getFloat(8)*100.0)+"%");
        emotionResultText.setText("이날 나는 "+cursors[0].getString(9)+" 하루였어.");

        cursors[1].moveToNext();
        if(cursors[1].getString(2) == null && cursors[1].getString(2) == null) {
            adapter.addItem(new RecordContent("-", "-"));
        } else {
            do {
                adapter.addItem(new RecordContent(cursors[1].getString(2), cursors[1].getString(3)));
            } while (cursors[1].moveToNext());
        }
        recordStringView.setAdapter(adapter);

        // 이미지 설정
        recordPartImage.setImageBitmap(facePhoto);
        // 레이아웃 설정 후 최종적으로 VISIBLE처리
        recordPartLayout.setVisibility(View.VISIBLE);
    }

    class ContentsAdapter extends BaseAdapter {
        ArrayList<RecordContent> items = new ArrayList<>();

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public RecordContent getItem(int position) {
            return items.get(position);
        }

        public void addItem(RecordContent recordContent) {
            this.items.add(recordContent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecordContentsView recordContentsView = null;
            if (convertView == null) {
                recordContentsView = new RecordContentsView(getApplicationContext());
            } else {
                recordContentsView = (RecordContentsView) convertView;
            }
            RecordContent recordContent = items.get(position);
            recordContentsView.setQuestionText(recordContent.getDiaryQuestion());
            recordContentsView.setAnswerText(recordContent.getDiaryAnswer());

            return recordContentsView;
        }
    }

    Button.OnClickListener partQuit = new Button.OnClickListener() {
        public void onClick(View v)
        {
            recordPartLayout.setVisibility(View.GONE);
        }
    };

    Button.OnClickListener menuQuit = new Button.OnClickListener() {
        public void onClick(View v)
        {
           finish();
        }
    };

}

class RecordPart {
    private Bitmap facePhoto;
    private String dayString;
    private int diaryKey;

    public RecordPart(byte[] facePhotoData, String dayString, int diaryKey) {
        this.facePhoto = BitmapFactory.decodeByteArray(facePhotoData, 0, facePhotoData.length);
        this.dayString = dayString;
        this.diaryKey = diaryKey;
    }

    public Bitmap getFacePhoto() {
        return facePhoto;
    }

    public String getDayString() {
        return dayString;
    }

    public int getDiaryKey() {
        return diaryKey;
    }

}

class RecordPartView extends LinearLayout {
    ImageView partImage;
    TextView partDayString;

    public RecordPartView(Context context) {
        super(context);
        init(context);
    }

    public RecordPartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.record_part, this, true);

        partImage = findViewById(R.id.partImage);
        partDayString = findViewById(R.id.partDayString);
    }

    public void setFacePhoto(Bitmap facephoto) {
        partImage.setImageBitmap(facephoto);
    }

    public void setDayString(String dayString) {
        partDayString.setText(dayString);
    }
}

class RecordContent {
    String diaryQuestion;
    String diaryAnswer;

    public RecordContent(String diaryQuestion, String diaryAnswer) {
        this.diaryQuestion = diaryQuestion;
        this.diaryAnswer = diaryAnswer;
    }

    public String getDiaryQuestion() {
        return diaryQuestion;
    }

    public String getDiaryAnswer() {
        return diaryAnswer;
    }
}

class RecordContentsView extends LinearLayout {
    TextView questionText;
    TextView answerText;

    public RecordContentsView(Context context) {
        super(context);
        init(context);
    }

    public RecordContentsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.record_string_part, this, true);

        questionText = findViewById(R.id.questionText);
        answerText = findViewById(R.id.answerText);
    }

    public void setQuestionText(String questionString) {
        questionText.setText("Q. " + questionString);
    }

    public void setAnswerText(String answerString) {
        answerText.setText("A. " + answerString);
    }
}