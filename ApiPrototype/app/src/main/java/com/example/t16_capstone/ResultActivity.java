package com.example.t16_capstone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.microsoft.projectoxford.face.contract.Face;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        String data = getIntent().getStringExtra("list_faces");

        Gson gson = new Gson();
        Face[] faces = gson.fromJson(data, Face[].class);

        ListView myListView = findViewById(R.id.listView);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap orig = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        if (faces == null) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "Face array is null", Toast.LENGTH_LONG).show();
            } else {

            }
        } else {
            try {
                Adapter adapter = new Adapter(faces, this, orig);
                myListView.setAdapter(adapter);
            } catch (Exception e) {
                makeToast(e.getMessage());
            }
        }
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

    }
}
