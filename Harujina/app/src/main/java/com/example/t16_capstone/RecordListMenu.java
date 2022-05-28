package com.example.t16_capstone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordListMenu extends AppCompatActivity {
    GridView recordGridView;
    DatabaseService dbsvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_list_menu);

        recordGridView = findViewById(R.id.recordGridView);

        dbsvs = new DatabaseService(this);

        RecordListAdapter adapter = new RecordListAdapter();
        Cursor cursor = dbsvs.selectAllDiaryDB();
        ArrayList<RecordPart> recordParts = new ArrayList<>();
        // 역순으로 띄우기 위한 ArrayList 변환
        while(cursor.moveToNext()) {
            recordParts.add(new RecordPart(cursor.getBlob(1), cursor.getString(2)));
        }
        for(int i = recordParts.size()-1; i >= 0; i--) {
            adapter.addItem(recordParts.get(i));
        }
        recordGridView.setAdapter(adapter);
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
}

class RecordPart {
    private Bitmap facePhoto;
    private String dayString;

    public RecordPart(byte[] facePhotoData, String dayString) {
        this.facePhoto = BitmapFactory.decodeByteArray(facePhotoData, 0, facePhotoData.length);
        this.dayString = dayString;
    }

    public Bitmap getFacePhoto() {
        return facePhoto;
    }

    public String getDayString() {
        return dayString;
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