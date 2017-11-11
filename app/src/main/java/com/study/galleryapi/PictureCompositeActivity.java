package com.study.galleryapi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileNotFoundException;

/**
 * Android SDK，可以首先在Canvas对象绘制一个位图对象，然后在相同Canvas对象中绘制
 * 第二个位图对象来实现合成。唯一区别绘制第二幅图像时，需要在Paint对象制定一个过渡对象(Xfermode)
 * 过度对象模式类集合都继承自Xfermode基类，其中一个包PorterDuffXfermode类，人名得名。
 *
 *在Android的PorterDuff.Mode类中列举Porter和Duff以及更多人指定的规则：
 * android.graphics.PorterDuff.Mode.SRC:只绘制源图像，正在应用此规则的Paint对象
 * android.graphics.PorterDuff.Mode.DST:只显示目标图像，已在画布初始化的对象。
 *
 *紧跟着SRC和DST规则，一起工作规则，确定最终绘制每副图像哪些部分。
 * 这些规则通常适用于图像具有不同大象或者存在透明部分
 *android.graphics.PorterDuff.Mode.DST_OVER:将源图像上绘制目标图像
 * android.graphics.PorterDuff.Mode.DST_IN:仅仅在源图像和目标图像相交的地方绘制目标图像
 * android.graphics.PorterDuff.Mode.DST_OUT:仅仅在源图像和目标图像不想交的地方绘制目标图像
 * android.graphics.PorterDuff.Mode.DST_ATOP:将在目标图像与源图像相交的地方绘制目标图像：再其他地方绘制源图像
 *android.graphics.PorterDuff.Mode.SRC_OVER:在目标图像上绘制源图像
 * android.graphics.PorterDuff.Mode.SRC_IN:在仅仅在目标和源图像相交的地方绘制源图像
 * android.graphics.PorterDuff.Mode.SRC_OUT:在目标和源图像不想交的地方绘制源图像
 * android.graphics.PorterDuff.Mode.SRC_ATOP:相交的地方绘制源图像，不相交的地方绘制目标图像
 *
 *另外4个规则定义了一幅图像放置另一幅图像上如何合成这两幅图像
 * android.graphics.PorterDuff.Mode.LIGHTEN:获得每个位置上两幅图像中最亮的像素并显示
 * android.graphics.PorterDuff.Mode.DARKEN:获得每个位置上两幅图像中最暗像素并显示
 * android.graphics.PorterDuff.Mode.MULTIPLY:将每个位置两个像素相乘，除以255，然后创建一个新的像素显示。
 * 结果颜色=顶部颜色X底部颜色
 * android.graphics.PorterDuff.Mode.SCREEN:反转每个颜色，执行相同操作（* / 255）然后再反转
 * 结果颜色=255-（（(255-顶部颜色)X（255-底部颜色））/255）
 * */
public class PictureCompositeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImageViewComposite;
    private final int PICKED_ONE=1;
    private final int PICKED_TWO=2;
    private  int which=-1;
    private Button mButton1;
    private Button mButton2;
    private Bitmap mBitmapOne;
    private boolean onePicked=false;
    private Bitmap mBitmapTwo;
    private boolean mTwoPicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_composite);
        initView();
    }

    private void initView() {
        mImageViewComposite = (ImageView) findViewById(R.id.composite_picture);
        mButton1 = (Button) findViewById(R.id.get_picture_one);
        mButton2 = (Button) findViewById(R.id.get_picture_two);
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri = data.getData();
        switch (requestCode){
            case PICKED_ONE:
                if(resultCode==RESULT_OK){
                    mBitmapOne = loadBitMap(imageUri);
                    onePicked=true;
                }
                break;
            case PICKED_TWO:
                if(resultCode==RESULT_OK){
                    mBitmapTwo = loadBitMap(imageUri);
                    mTwoPicked = true;
                }
                break;
            default:
                break;
        }
        if(onePicked&&mTwoPicked){
            //在Canvas对象和一个Paint对象，该画布上绘制第一个位图对象(成为目标图像)
            //Paint设置过渡模式，传入操作常量，在Canvas对象上绘制第二个位图对象。
            Bitmap drawBitmap=Bitmap.createBitmap(mBitmapOne.getWidth(),mBitmapOne.getHeight(),mBitmapOne.getConfig());
            Canvas canvas=new Canvas(drawBitmap);
            Paint paint=new Paint();
            canvas.drawBitmap(mBitmapOne,0,0,paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            canvas.drawBitmap(mBitmapTwo,0,0,paint);
            mImageViewComposite.setImageBitmap(drawBitmap);
        }
    }

    private Bitmap loadBitMap(Uri imageUri) {
        Display display=getWindowManager().getDefaultDisplay();
        int dw=display.getWidth();
        int dh=display.getHeight();
        //期望ARGB_4444
        Bitmap returnBitmap=Bitmap.createBitmap(dw,dh,Bitmap.Config.ARGB_4444);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        try {
            returnBitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri),null,options);
            int heightRatio= (int) Math.ceil(options.outHeight/dh);
            int widthRatio= (int) Math.ceil(options.outWidth/dw);
            if(heightRatio>1&&widthRatio>1){
                options.inSampleSize=Math.max(heightRatio,widthRatio);
            }
            options.inSampleSize=4;//just for test
            options.inJustDecodeBounds=false;
            returnBitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri),null,options);

        } catch (FileNotFoundException e) {

        }
        return returnBitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_picture_one:
                which=PICKED_ONE;
                break;
            case R.id.get_picture_two:
                which=PICKED_TWO;
                break;
            default:
                return;
                //break;
        }
        Intent choosePictureIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(choosePictureIntent,which);
    }
}
