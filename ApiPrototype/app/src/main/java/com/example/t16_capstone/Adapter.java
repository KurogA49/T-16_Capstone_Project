package com.example.t16_capstone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Adapter extends BaseAdapter {

    private Face[] faces;
    private Context context;
    private LayoutInflater inflater;
    private Bitmap orig;

    ArrayList<Double> arrayList = new ArrayList<>();
    TreeMap<Integer, String> rank = new TreeMap<>();

    public Adapter(Face[] faces, Context context, Bitmap orig) {
        this.faces = faces;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.orig = orig;
    }


    @Override
    public int getCount() {
        return faces.length;
    }

    @Override
    public Object getItem(int position) {
        return faces[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.listview_layout, null);
        }

        TextView emotionValue;
        Button btn;
        ImageView imageView;

        emotionValue = view.findViewById(R.id.textSmile); // 감정 값
        btn = view.findViewById(R.id.next);
        imageView = view.findViewById(R.id.imgThumb); // API 가 분석에 사용한 얼굴 이미지

        // 감정 값
        TreeMap<Double, String> treeMap = new TreeMap<>();
        treeMap.put(faces[position].faceAttributes.emotion.happiness, "Happiness");
        treeMap.put(faces[position].faceAttributes.emotion.anger, "Anger");
        treeMap.put(faces[position].faceAttributes.emotion.disgust, "Disgust");
        treeMap.put(faces[position].faceAttributes.emotion.sadness, "Sadness");
        treeMap.put(faces[position].faceAttributes.emotion.surprise, "Surprise");
        treeMap.put(faces[position].faceAttributes.emotion.fear, "Fear");
        treeMap.put(faces[position].faceAttributes.emotion.contempt, "Contempt");
        treeMap.put(faces[position].faceAttributes.emotion.neutral, "Neutral");

        int counter = 0;
        for (Map.Entry<Double, String> entry : treeMap.entrySet()) {
            String key = entry.getValue();
            Double value = entry.getKey();
            rank.put(counter, key);
            counter++;
            arrayList.add(value);
        }

        emotionValue.setText(rank.get(rank.size() - 1) + ": " + 100 * arrayList.get(rank.size() - 1) + "% " + rank.get(rank.size() - 2) + ": " + 100 * arrayList.get(rank.size() - 2) + "%");
        FaceRectangle faceRectangle = faces[position].faceRectangle;
        Bitmap bitmap = Bitmap.createBitmap(orig, faceRectangle.left, faceRectangle.top, faceRectangle.width, faceRectangle.height);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, com.example.t16_capstone.EmotionCheck.class);
                intent.putExtra("Emotion", (String) rank.get(rank.size() - 1));
                context.startActivity(intent);
            }
        });
        imageView.setImageBitmap(bitmap);
        imageView.setImageBitmap(bitmap);
        return view;
    }

}