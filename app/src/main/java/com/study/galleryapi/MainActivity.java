package com.study.galleryapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //启动内置程序
    public void startGallery(View view) {
        startActivity(new Intent(this,ChoosePicture.class));
    }

    public void pictureComposite(View view) {
        startActivity(new Intent(this,PictureCompositeActivity.class));
    }

    public void startCanvas(View view) {
        startActivity(new Intent(this,CanvasMineActivity.class));
    }

    public void drawPath(View view) {
        startActivity(new Intent(this,SimpleFingerDraw.class));
    }

    public void startDrawOnPicture(View view) {
        startActivity(new Intent(this,ChoosePictureDraw.class));
    }
}
