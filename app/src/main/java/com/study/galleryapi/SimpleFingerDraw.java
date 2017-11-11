package com.study.galleryapi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class SimpleFingerDraw extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = "SimpleFingerDraw";
    private ImageView mImageView;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private float downx;
    private float downy;
    private float upx;
    private float upy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_finger_draw);
        initView();
        initData();
    }

    private void initData() {
        Display currentDisplay=getWindowManager().getDefaultDisplay();
        float dw=currentDisplay.getWidth();
        float dh=currentDisplay.getHeight();
        mBitmap = Bitmap.createBitmap((int) dw,(int)dh,Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mImageView.setImageBitmap(mBitmap);
    }

    private void initView() {
        mImageView = (ImageView) findViewById(R.id.image_simple_finger_draw);
        mImageView.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG, "onTouch: ");
        int action=event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                downx =event.getX();
                downy =event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                upx=event.getX();
                upy=event.getY();
                mCanvas.drawLine(downx,downy,upx,upy,mPaint);
                mImageView.invalidate();
                downx=upx;
                downy=upy;
                break;
            case MotionEvent.ACTION_UP:
                upx =event.getX();
                upy =event.getY();
                mCanvas.drawLine(downx,downy,upx,upy,mPaint);
                mImageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
               break;
            default:
                break;
        }
        return true;
    }
}
