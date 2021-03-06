package com.example.liuyueyue.imooc_wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuyueyue on 2017/6/8.
 */

public class WuziqiPanel extends View {
    private String aa="123";
    private int mPanelWidth;
    private float mLineHeight;
    private int MAX_LINE = 10;
    private int MAX_COUNT_IN_LINE = 5;

    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    //白棋先手，或则当前轮到白棋
    private boolean mIsWhite = true;
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;


    public WuziqiPanel(Context context, AttributeSet attrs) {

        super(context, attrs);
        /*setBackgroundColor(0X44FF0000);*/
        init();
    }

    private void init() {
        mPaint.setColor(0X88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2,null);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1,null);

    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width,width);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);

        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth,
                pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth,
                pieceWidth, false);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP){
            if(mIsGameOver) return false;
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point p = getValidPoint(x, y);
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }
            if (mIsWhite) {
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite =! mIsWhite;


        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int) (y / mLineHeight));


    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPieces(canvas);
        drawBoard(canvas);
        checkGameOver();

    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);

        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;

            String text = mIsWhiteWinner? "白棋获胜" : "黑棋获胜";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFiveInLine(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;

            boolean win = checkHorzontal(x, y, points);
            if(win) return true;
            win = checkVertical(x,y,points);
            if(win) return true;
            win = checkLeftDiagonal(x,y,points);
            if(win) return true;
            win = checkRightDiagonal(x,y,points);
            if(win) return true;

        }

        return false;
    }
//判断x,y位置的棋子是否横向，且有相邻的五个一致。
    private boolean checkHorzontal(int x, int y, List<Point> points) {
        int count = 1;
        //左
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if(count == MAX_COUNT_IN_LINE)return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if(count == MAX_COUNT_IN_LINE)return true;
            return false;

        }
//纵向
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        //上下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y-i))) {
                count++;
            } else {
                break;
            }
        }
        if(count == MAX_COUNT_IN_LINE)return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y+i))) {
                count++;
            } else {
                break;
            }
        }
        if(count == MAX_COUNT_IN_LINE)return true;
        return false;

    }
//左斜
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x-i,y+i))) {
                count++;
            } else {
                break;
            }
        }
        if(count == MAX_COUNT_IN_LINE)return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x+i,y-i))) {
                count++;
            } else {
                break;
            }
        }
        if(count == MAX_COUNT_IN_LINE)return true;
        return false;

    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //右斜
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x-i, y-i))) {
                count++;
            } else {
                break;
            }
        }
        if(count == MAX_COUNT_IN_LINE)return true;
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x+i, y+i))) {
                count++;
            } else {
                break;
            }
        }
        if(count == MAX_COUNT_IN_LINE)return true;
        return false;

    }


    private void drawPieces(Canvas canvas) {
        for(int i=0,n=mWhiteArray.size();i< n ;i++){
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    (whitePoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeight,null);
        }
        for(int i=0,n=mBlackArray.size();i< n ;i++){
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeight,
                    (blackPoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeight,null);
        }


    }

    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;
        for(int i=0;i<MAX_LINE;i++){
            int startX = (int) (lineHeight/2);
            int endX = (int)(w-lineHeight/2);
            int y = (int)((0.5 + i)*lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);

        }
    }

    public void start(){
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver=false;
        mIsWhiteWinner=false;
        invalidate();
    }
    private static final  String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER="instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY="instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY="instance_black_array";
    private static final String INSTANCE_ISWHITE="instance_iswhite";



    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        bundle.putBoolean(INSTANCE_ISWHITE,mIsWhite);
        return bundle;
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            mIsWhite=bundle.getBoolean(INSTANCE_ISWHITE);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}




