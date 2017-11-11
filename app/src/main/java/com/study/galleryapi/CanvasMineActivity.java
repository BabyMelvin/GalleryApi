package com.study.galleryapi;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CanvasMineActivity extends AppCompatActivity {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas_mine);
    }
    /*
    * 位图创建,需要提供宽高，配置
    * Bitmap.Config常量值：
    * ALPHA_8:作为Alpha蒙版位图，只为Alpha通道配置8位
    * ARGB_4444
    * ARGB_8888
    * RGB_565:几乎和ARGB_8888具有相同质量，但是占用内存空间少
    * */
    private void createBitMap(){
        Bitmap mBitmap=  Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(),getWindowManager().getDefaultDisplay().getHeight(),Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);
        /*
         *颜色：
         * Color.BLACK
         *
         * Color.BLUE
         * Color.RED
         * 样式
         * Paint.Style.STROKE:仅绘制形状轮廓
         * Paint.Style.FILL  :仅填充形状
         * Paint.Style.FILL_AND_STROKE：填充并绘制形状轮廓
         * 笔画宽度
         * 设置为0仍然为1个像素的笔画。为了完全删除笔画应当设置setStyle方法，同时传入Paint.FILL常量。
         **/
        mPaint = new Paint();
        //int myColor=Color.argb(255,128,64,32);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
    }
    private void paintProfile(){
        //1.绘制点,大小取决于画笔的宽度
        mCanvas.drawPoint(199,201,mPaint);
        //2.绘制直线
        int startX=50;
        int startY=100;
        int endX=150;
        int endY=210;
        mCanvas.drawLine(startX,startY,endX,endY,mPaint);
        //3.矩形
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(10);
        float leftX=20;
        float topY=20;
        float rightX=50;
        float bottomY=100;
        mCanvas.drawRect(leftX,topY,rightX,bottomY,mPaint);
        //4.椭圆
        float left=20;
        float top=20;
        float right=150;
        float bottom=100;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCanvas.drawOval(left,top,right,bottom,mPaint);
        }
        //5.圆
        float cX=50;
        float cY=50;
        float radius=20;
        mCanvas.drawCircle(cX,cY,radius,mPaint);
        //6.路径
        Path path=new Path();
        path.moveTo(20,20);//无需画，只是移动到某个点
        path.lineTo(100,200);//绘制一条直线到某个点
        path.lineTo(200,100);
        path.lineTo(250,175);
        path.lineTo(20,20);
        mCanvas.drawPath(path,mPaint);
    }
    private void paintText(){
        /**
         * 通过调用drawText()方法Canvas对象上绘制文本
         * Paint上setTextSize方法设置文本大小
         * 1.内置字体
         * Typeface类：
         * Typeface.MONOSPACE:这种字体每个字母之间有相同间隔
         * Typeface.SANS_SERIF:这是没有衬线（serif）字体
         * Typeface.SERIF:包含衬线（笔锋）
         * Typeface.DEFAULT:默认字体无衬线
         * Typeface.DEFAULT_BOLD:这是无衬线字体的粗体版本
         *
         * Typeface.BOLD
         * Typeface.ITALIC
         * Typeface.NORMAL
         * Typeface.BOLD_ITALIC
         *
         * 2.外部字体
         * TrueType字体是一种标准，可以在任何平台上使用
         * 将chopinscript.ttf放入assert文件中
         * */
        mPaint.setTextSize(40);
        float textX=120;
        float textY=120;
        Typeface serif_italic=Typeface.create(Typeface.SERIF,Typeface.ITALIC);
        mPaint.setTypeface(serif_italic);
        mCanvas.drawText("Hello",textX,textY,mPaint);
        //外部字体
        Typeface fromAsset = Typeface.createFromAsset(getAssets(), "ChopinScript.ttf");
        mPaint.setTypeface(fromAsset);

        //在路径上绘制文本
        mPaint.setTypeface(Typeface.DEFAULT);
        Path myPath=new Path();
        myPath.moveTo(20,20);
        myPath.lineTo(100,150);
        myPath.lineTo(200,220);
        mCanvas.drawTextOnPath("hello this is text on a path",myPath,0,0,mPaint);
    }
}
