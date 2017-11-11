package com.study.galleryapi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;

import static android.graphics.Bitmap.createBitmap;

public class ChoosePicture extends AppCompatActivity {
    private static final String TAG = "ChoosePicture";
    private ImageView mImageView;
    private ImageView mBackUpImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_picture);
        initView();
    }

    private void initView() {
        mImageView = (ImageView) findViewById(R.id.image_choose_picture_ay);
        mBackUpImageView = (ImageView) findViewById(R.id.image_back_up_activity);
    }

    public void takePicture(View view) {
        Intent intentChoosePicture=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentChoosePicture,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:
                if(resultCode==RESULT_OK){
                    Uri imageUri = data.getData();
                    Log.i(TAG, "onActivityResult: imageUri="+imageUri);
                    //返回的图片太大，可进行缩减，直接加入内存大
//                    try {
                    //                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    //                        mImageView.setImageBitmap(bitmap);
                    //                    } catch (FileNotFoundException e) {
                    //                        e.printStackTrace();
                    //                    }
                    Display display=getWindowManager().getDefaultDisplay();
                    int wd=display.getWidth();
                    int hd=display.getHeight();
                    Log.i(TAG, "onActivityResult: hw="+ wd+"hd="+hd);
                    try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds=true;
                        Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri),null,options);
                        Log.i(TAG, "onActivityResult: h="+options.outHeight+" w="+options.outWidth);
                        int heightRatio= (int) Math.ceil(options.outHeight/hd);
                        int widthRatio= (int) Math.ceil(options.outWidth/wd);
                        if(widthRatio>1&&heightRatio>1){
                            options.inSampleSize=Math.max(heightRatio,widthRatio);
                        }
                        options.inSampleSize=4;
                        options.inJustDecodeBounds=false;
                        bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri),null,options);
                        mImageView.setImageBitmap(bitmap);
                        showBackUp(bitmap);
                        changeColor(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void changeColor(Bitmap bitmap) {
        Log.i(TAG, "changeColor: ");
        /*
        * 处理像素自身颜色值变化：可以改变对比度、亮度、整体色调等
        * ColorMatrix
        * 操作的值为-(Red,Green,Blue,Alpha)
        * */
        Bitmap alteredBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());
        Canvas canvas=new Canvas(alteredBitmap);
        Paint paint=new Paint();
        ColorMatrix cm=new ColorMatrix();
        /*
        * 改变颜色
        * 2,0,0,0,0
        * 0,1,0,0,0
        * 0,0,1,0,0
        * 0,0,0,1,0
        *2*red+0*green+0*blue+0*Alpha+0
        * 最后一个谁都不乘
        *
        * 改变对比度和亮度
        * 对每个颜色进行加强后，影响亮度和对比度
        * 2,0,0,0,0
        * 0,2,0,0,0
        * 0,0,2,0,0
        * 0,0,0,1,0
        *对比度和亮度是相连的，要只改变对比度需要降低亮度来补偿
        *
        * 改变亮度
        * 利用改变最后的量，无需对原来颜色量进行改变
        * 1,0,0,0,-25
        * 0,1,0,0,-25
        * 0,0,1,0,-25
        * 0,0,0,1,0
        * */
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        /*
        * 改变饱和度,大于1数字将增加饱和度，传入0~1之间数字减少饱和度，0产生一幅灰度图像
        * */
        cm.setSaturation(.5f);
        Matrix matrix=new Matrix();
        canvas.drawBitmap(bitmap,matrix,paint);
        mBackUpImageView.setImageBitmap(alteredBitmap);
    }

    private void showBackUp(Bitmap bitmap) {
        Log.i(TAG, "showBackUp: bitmap="+bitmap);
        /*
        *为了避免被截断的问题，可以开始建立Matrix对象，不是建立空的对象
        *缺点无法再创建Canvas和Paint对象，意味着不能继续改变图像信息.
        * */
//        Matrix matrix=new Matrix();
//        matrix.setRotate(15,bitmap.getWidth()/2,bitmap.getHeight()/2);
//        Bitmap alteredBitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
//        mBackUpImageView.setImageBitmap(alteredBitmap);


        //一个位图不可变对象，不能进行绘制,需要拷贝重绘
        Bitmap alteredBitmap = createBitmap(2*bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        //1.获得画布--》位图上画
        Canvas canvas=new Canvas(alteredBitmap);
        Paint paint = new Paint();
        //画笔将bitmap信息画在画布上(alteredBitMap位图)
        //1.在imageView位置(0,0)开始
        //canvas.drawBitmap(bitmap,0,0,paint);
        //2.矩阵方式显示[](x,y,z),如:x缩小一半
        Matrix matrix=new Matrix();
//        matrix.setValues(new float[]{
//                0.5f,0,0,
//                0,1,0,
//                0,0,1
//        });
        //如果保持原来大小，会被截断需要增加一倍width()*2
//        matrix.setValues(new float[]{
//                1,0.5f,0,
//                0,1,0,
//                0,0,1
//        });
        //1.旋转
        //matrix.setRotate(15);
        //2.缩放
       // matrix.setScale(1.5f,1);
        //3.平移.负号表示向上移动
        //matrix.setTranslate(1.5f,-10);
        //4.镜像
//        matrix.setScale(-1,1);
//        matrix.postTranslate(alteredBitmap.getWidth(),0);
        //5.翻转
//        matrix.setScale(1,-1);
//        matrix.postTranslate(0,alteredBitmap.getHeight());
        canvas.drawBitmap(bitmap,matrix,paint);
        mBackUpImageView.setImageBitmap(alteredBitmap);
    }
}
