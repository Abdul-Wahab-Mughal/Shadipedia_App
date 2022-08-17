package com.spapp.shadipedia;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.shadipedia.R;

public class Full_Screen_Image extends AppCompatActivity {

    ImageView fullscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_image);

        fullscreen = findViewById(R.id.fullscreenImage);

        if (getIntent().hasExtra("image")) {
            Bitmap _bitmap = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("image"), 0, getIntent().getByteArrayExtra("image").length);
            fullscreen.setImageBitmap(_bitmap);
        }

    }
}