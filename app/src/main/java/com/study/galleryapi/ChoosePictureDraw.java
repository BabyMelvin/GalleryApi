package com.study.galleryapi;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.OutputStream;

public class ChoosePictureDraw extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private static final String TAG = "ChoosePictureDraw";
    private Button mButton;
    private ImageView mImageView;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private float downx;
    private float downy;
    private float upx;
    private float upy;
    private Bitmap mAlteredBitmap;
    private Button mPictureSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picture_draw);
        initView();
        myCheckPermission();
    }

    private void myCheckPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "onRequestPermissionsResult: ");
        }else{
            Toast.makeText(this, "未授权", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        mButton = (Button) findViewById(R.id.picture_draw);
        mImageView = (ImageView) findViewById(R.id.image_view_choose);
        mPictureSave = (Button) findViewById(R.id.picture_save);
        mPictureSave.setOnClickListener(this);
        mButton.setOnClickListener(this);
        mImageView.setOnTouchListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Uri imageUri=data.getData();
            Display display=getWindowManager().getDefaultDisplay();
            float dw=display.getWidth();
            float dh=display.getHeight();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            try {
                mBitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri),null,options);
                int heightRatio= (int) Math.ceil(options.outHeight/dh);
                int widthRatio= (int) Math.ceil(options.outWidth/dw);
                if(heightRatio>1&&widthRatio>1){
                    options.inSampleSize=Math.max(heightRatio,widthRatio);
                }
                options.inJustDecodeBounds=false;
                mBitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri),null,options);
                mAlteredBitmap = Bitmap.createBitmap(mBitmap.getWidth(),mBitmap.getHeight(),mBitmap.getConfig());
                mCanvas = new Canvas(mAlteredBitmap);
                mPaint = new Paint();
                mPaint.setColor(Color.GREEN);
                mPaint.setStrokeWidth(5);
                Matrix matrix = new Matrix();
                mCanvas.drawBitmap(mBitmap,matrix, mPaint);
                mImageView.setImageBitmap(mAlteredBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: 0");
        switch (v.getId()){
            case R.id.picture_draw:
                Log.i(TAG, "onClick: 1");
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,0);
                break;
            case R.id.picture_save:
                Log.i(TAG, "onClick: 2");
                if(mAlteredBitmap!=null){
                    Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                    Uri imageFileUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new ContentValues());
                    Log.i(TAG, "onClick: 2.1");
                    try {OutputStream outputStream=getContentResolver().openOutputStream(imageFileUri);
                        Log.i(TAG, "onClick: 3");

                        mAlteredBitmap.compress(Bitmap.CompressFormat.JPEG,90,outputStream);
                        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downx =event.getX();
                downy =event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                upx =event.getX();
                upy =event.getY();
                mCanvas.drawLine(downx,downy,upx,upy,mPaint);
                mImageView.invalidate();
                downx=upx;
                downy=upy;
                break;
            case MotionEvent.ACTION_UP:
                upx=event.getX();
                upy=event.getY();
                mCanvas.drawLine(downx,downy,upx,upy,mPaint);
                mImageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}
