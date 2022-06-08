package com.yxm.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import com.yxm.library.bean.PieceProcess;
import com.yxm.library.bean.Point;
import com.yxm.library.util.DensityUtils;
import com.yxm.library.util.ExpressionUtil;
import com.yxm.library.util.LogUtils;
import com.yxm.library.util.SharedPrefsUtil;
import com.yxm.library.util.SoundPoolUtils;
import com.yxm.library.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class TileView extends ViewGroup {

    private boolean isTeach = true;
    public boolean isEndTopic = false;// 题库是否结束解答
    private HashMap<String, String> listNum;

    public HashMap<String, String> getListNum() {
        return listNum;
    }

    public void setlistNumClear() {
        this.listNum.clear();
    }

    public void setListNum(HashMap<String, String> listNum) {
        this.listNum = listNum;
        System.out.println(listNum + "tileviewListNum@!!!!!");
    }

    /******************************************************/

    private GestureDetector mGestureDetector = null;
    private ScaleGestureDetector mScaleGestureDetector = null;

    private SoundPoolUtils poolutil;
    private boolean issound;
    public boolean isInTopic = false;// 是否在题库页面
    public int testcount; // 试下前的总手数
    public int testcount1; // 试下前的总手数1
    public int letnum; // 让子数
    public boolean isbtest = false;
    public boolean isCanScale = true;// 是否可以进行缩放
    public boolean is_coord = true;
    private int currentX;
    private int currentY;
    private int isbw;
    private int mtop, mleft, mbottom, mright;
    private Paint markpaint;
    private boolean isScaled = false;
    private int scaleMargin = 0;
    private int screenWidth = 0;
    private int stone_new_w, stone_new_b;
    private Board board;
    private Paint paint;
    protected double xOffset;
    protected double yOffset;
    protected double tileSize;
    public Bitmap bmpBlack;
    public Bitmap bmpWhite;
    public boolean is_judge = true;
    public Bitmap bmpPreBlack;
    public Bitmap bmpPreWhite;
    public Bitmap bmpPreWhite101;
    public int pieces[][];
    public boolean prePutFlag = false;
    private Coordinate prePos;
    public boolean isShowEndNum = true;// 是否显示最后一手手顺
    public boolean isConnecting = true;
    public boolean boardUnlock = true; // 盘面锁
    public boolean showNum = false; // 显示手顺
    public boolean isLet = false;// 判断让子，默认为不让子
    public boolean showWrongPosition=false;//显示错误位置

    private Function mylistener; // 子定义事件接口
    private SharedPreferences sp;
    protected int putType = -1;
    private int topicx;
    private int topicy;
    private int Box_size = 5;
    private int Box_sizepx = 10;
    private Paint paint1;
    private Paint paint2;
    private int numsize = 25; // 初始字体大小
    private double agian = 1; // 缩放的倍数
    private int textSizepx = 25;
    private float textSize = 25;
    private float textSizeNum=40;
    public static final int BlackDead = 4;
    public static final int WhiteDead = 3;

    public static final int BlackNone = 6;
    public static final int WhiteNone = 5;
    Context context;
    public int isErrorStep = 0;

    public int wrongTopicX;//错误棋步的x坐标
    public int wrongTopicY;
    public boolean isDismiss=false;//是否属于消失的情况

    public List<String> errorList;

    public List<Point> WinPoints = new ArrayList<>();// 获取胜利的功能子

    public boolean hotPhoto = false;//显示热点图

    public String hotPhotoInfo=""; // 存储热点图坐标

    public int version=0;// 底层固件版本 >=100 预落子颜色为白色

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    private int boardSize=19;

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);

        findTileView(context);
    }

    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        findTileView(context);
    }

    public TileView(Context context) {
        super(context);

        findTileView(context);
    }

    public void setPutType(int _putType) {
        this.putType = _putType;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
        this.invalidate();
    }

    public void reloadTileview() {
        MarginLayoutParams marginPar = (MarginLayoutParams) getLayoutParams();
        marginPar.setMargins(0, marginPar.topMargin == 0?++marginPar.topMargin:--marginPar.topMargin, 0, 0);
        scrollTo(0, 0);
        currentX = 0;
        currentY = 0;
        setLayoutParams(marginPar);
        onSizeChanged(getWidth(),getHeight(),getWidth(),getHeight());
        this.invalidate();

    }

    private OnScaleGestureListener mOnScaleGestureListener = new OnScaleGestureListener() {

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {


        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {


            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            if (isCanScale) {

                float scale = detector.getScaleFactor();

                if (scale == 1) {
                    return false;

                } else if (scale > 1) {

                    mleft = -200;
                    mtop = -200;
                    mright = -200;
                    mbottom = -200;

                    MarginLayoutParams marginPar = (MarginLayoutParams) getLayoutParams();
                    marginPar.setMargins(mleft, mtop, mright, mbottom);
                    // scrollTo(0, 0);
                    setLayoutParams(marginPar);

                } else {
                    MarginLayoutParams marginPar = (MarginLayoutParams) getLayoutParams();
                    marginPar.setMargins(0, 0, 0, 0);
                    scrollTo(0, 0);
                    currentX = 0;
                    currentY = 0;
                    setLayoutParams(marginPar);
                }
            }
            return false;

        }
    };

    private OnGestureListener mOnGestureListener = new OnGestureListener() {


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.i("mygo", "onSingleTapUp: " + e.toString());

            return false;

        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (isCanScale) {

                if (Math.abs(distanceX) > 5 || Math.abs(distanceY) > 5) {

                    if (isScaled) {
                        currentX += (int) distanceX;
                        currentY += (int) distanceY;
                        scrollBy((int) distanceX, (int) distanceY);
                    }
                }
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
//            if (isCanScale) {
//                if (isScaled) // 已经缩放了，则还原
//                {
//                    MarginLayoutParams marginPar = (MarginLayoutParams) getLayoutParams();
//                    marginPar.setMargins(0, 0, 0, 0);
//                    scrollTo(0, 0);
//                    currentX = 0;
//                    currentY = 0;
//                    setLayoutParams(marginPar);
//
//                    isScaled = false;
//
//                } else {
//
//                    CaculateMargins(e.getX(), e.getY());
//
//                    MarginLayoutParams marginPar = (MarginLayoutParams) getLayoutParams();
//                    marginPar.setMargins(mleft, mtop, mright, mbottom);
//                    // scrollTo(0, 0);
//                    setLayoutParams(marginPar);
//
//                    isScaled = true;
//                }
//            }
        }

        @Override
        public boolean onDown(MotionEvent arg0) {

            return false;
        }

        @Override
        public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
                               float arg3) {

            return false;
        }

        @Override
        public void onShowPress(MotionEvent arg0) {

        }

    };

    private OnDoubleTapListener mDoubleTapListener = new OnDoubleTapListener() {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("LOG_TAG", "onSingleTapConfirmed: " + e.toString());

            if (!boardUnlock)
                return false;

            if (!isConnecting)
                return false;

            float xf = e.getX();
            float yf = e.getY();

            int x = x2Coordinate(xf + currentX);
            int y = y2Coordinate(yf + currentY);

            // 自信模式，直接落子
            if (putType == 0) {
                return doPutPiece(x, y);
            } else if (putType == 2) {

                topicx = x2Coordinate(xf + currentX);
                topicy = y2Coordinate(yf + currentY);
                return TopicBank(topicx, topicy);

            } else {
                // 谨慎模式，预备落子
                prePutPiece(x, y);
                return true;
            }
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.i("LOG_TAG", "onDoubleTapEvent: " + e.toString());

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("LOG_TAG", "onDoubleTap: " + e.toString());
//            if (isCanScale) {
//                if (isScaled) // 已经缩放了，则还原
//                {
//                    MarginLayoutParams marginPar = (MarginLayoutParams) getLayoutParams();
//                    marginPar.setMargins(0, 0, 0, 0);
//                    scrollTo(0, 0);
//                    currentX = 0;
//                    currentY = 0;
//                    setLayoutParams(marginPar);
//
//                    isScaled = false;
//
//                } else {
//                    CaculateMargins(e.getX(), e.getY());
//                    MarginLayoutParams marginPar = (MarginLayoutParams) getLayoutParams();
//                    marginPar.setMargins(mleft, mtop, mright, mbottom);
//                    setLayoutParams(marginPar);
//                    isScaled = true;
//                }
//            }
            // postEnvet();

            return true;
        }
    };
    private int type;
    public Bitmap errorPlot;
    public Bitmap addPlot;
    public Bitmap reducePlot;
    private boolean isShowLattay = true;

    public void prePutPiece(int x, int y) {

        if (board.prePut(x, y)) {
            // 绘制预下棋
            prePutFlag = true;

            prePos = new Coordinate(x, y);

            board.hasPickStone = false;

            invalidate();

            // 告知接口程序，显示确定按钮
            postEnvet("0"); // 表示已预落子

        }

    }

    public boolean TopicBank(int x, int y) {
        Board.hasPickother = false;
        topicx = x;
        topicy = y;
        Log.i("qizi", "落子坐标 " + x + "落子坐标" + y);
        sound();
        invalidate();
        postEnvet("2");

        return true;

    }

    // 落子
    protected boolean doPutPiece(int x, int y) {

        Board.hasPickother = false;
        topicx = x;
        topicy = y;
        Log.i("qizi", "落子坐标doPutPiece " + x + "落子坐标doPutPiece" + y);

        if (board.put(x, y)) {

            boolean T = Board.hasPickother;

            sound();

            invalidate();

            if (T) {
                postEnvet("3"); // 优先知道提子信息，处理相关事件
            }

            postEnvet("1");

            return true;
        }

        return true;
    }

    // 执行真实落子操作。对外公开方法
    public void doPut() {
        doPutPiece(prePos.x, prePos.y);
    }

    // 执行真实落子操作。对外公开方法
    public void doPut(int x, int y) {
        doPutPiece(x, y);
    }

    // 落子
    private void CaculateMargins(float xf, float yf) {

        int x = x2Coordinate(xf + currentX);
        int y = y2Coordinate(yf + currentY);

        int FDivider = 0; // 第一界点
        int SDivider = 0; // 第二界点
        mleft = 0;
        mtop = 0;
        mright = 0;
        mbottom = 0;

        if (boardSize > 9) {
            scaleMargin = (int) (screenWidth - xOffset - tileSize);

            if (boardSize == 19) {
                FDivider = 6;
                SDivider = 12;

            } else if (boardSize == 15) {
                FDivider = 5;
                SDivider = 10;
            } else if (boardSize == 13) {
                FDivider = 4;
                SDivider = 8;
            }

        } else {
            return;
        }

        if (x <= FDivider && y <= FDivider) {
            mleft = 0;
            mtop = -scaleMargin;
            mright = -scaleMargin;
            mbottom = 0;

        } else if (x > FDivider && x <= SDivider && y <= FDivider) {
            mleft = -scaleMargin / 2;
            mtop = -scaleMargin;
            mright = -scaleMargin / 2;
            mbottom = 0;
        } else if (x > SDivider && y <= FDivider) {
            mleft = -scaleMargin;
            mtop = -scaleMargin;
            mright = 0;
            mbottom = 0;
        } else if (x <= FDivider && y > FDivider && y <= SDivider) {
            mleft = 0;
            mtop = -scaleMargin / 2;
            mright = -scaleMargin;
            mbottom = -scaleMargin / 2;
        } else if (x > FDivider && x <= SDivider && y > FDivider && y <= SDivider) {
            mleft = -scaleMargin / 2;
            mtop = -scaleMargin / 2;
            mright = -scaleMargin / 2;
            mbottom = -scaleMargin / 2;
        } else if (x > SDivider && y > FDivider && y <= SDivider) {
            mleft = -scaleMargin;
            mtop = -scaleMargin / 2;
            mright = 0;
            mbottom = -scaleMargin / 2;
        } else if (x <= FDivider && y > SDivider) {
            mleft = 0;
            mtop = 0;
            mright = -scaleMargin;
            mbottom = -scaleMargin;
        } else if (x > FDivider && x <= SDivider && y > SDivider) {

            mleft = -scaleMargin / 2;
            mtop = 0;
            mright = -scaleMargin / 2;
            mbottom = -scaleMargin;

        } else if (x > SDivider && y > SDivider) {
            mleft = -scaleMargin;
            mtop = 0;
            mright = 0;
            mbottom = -scaleMargin;

        }

    }

    /**
     * 落子(题库用放大方法) 李朦利 2016-2-24
     *
     * @param xf    x坐标值
     * @param yf    y坐标值
     * @param width 棋子所占区域宽
     * @param hight 棋子所占区域高
     */
    private void CaculateMarginsInTopic(float xf, float yf, int width, int hight) {

        int x = x2Coordinate(xf + currentX);
        int y = y2Coordinate(yf + currentY);
        int w = x2Coordinate(width + currentX);
        int h = x2Coordinate(hight + currentX);
        int FDivider = 0; // 第一界点
        int SDivider = 0; // 第二界点
        mleft = 0;
        mtop = 0;
        mright = 0;
        mbottom = 0;
        int scaleSGF = Math.max(w, h);
        if (scaleSGF > 12) {
            scaleMargin = screenWidth - Math.max(width, hight) - 225;
        } else if (scaleSGF > 10) {
            scaleMargin = screenWidth - Math.max(width, hight) - 150;
        } else if (scaleSGF > 7) {
            scaleMargin = screenWidth - Math.max(width, hight) - 50;
        } else if (scaleSGF > 4) {
            scaleMargin = screenWidth - Math.max(width, hight) + 100;
        } else {
            scaleMargin = screenWidth - Math.max(width, hight) + 200;
        }

        if (boardSize > 9) {
            if (boardSize == 19) {
                FDivider = 8;
                SDivider = 11;

            } else if (boardSize == 15) {
                FDivider = 7;
                SDivider = 9;
            } else if (boardSize == 13) {
                FDivider = 6;
                SDivider = 7;
            }

        } else {
            return;
        }

        if (x <= FDivider && y <= FDivider) {
            mleft = 0;
            mtop = -scaleMargin;
            mright = -scaleMargin;
            mbottom = 0;

        } else if (x > FDivider && x <= SDivider && y <= FDivider) {
            mleft = -scaleMargin / 2;
            mtop = -scaleMargin;
            mright = -scaleMargin / 2;
            mbottom = 0;
        } else if (x > SDivider && y <= FDivider) {
            mleft = -scaleMargin;
            mtop = -scaleMargin;
            mright = 0;
            mbottom = 0;
        } else if (x <= FDivider && y > FDivider && y <= SDivider) {
            mleft = 0;
            mtop = -scaleMargin / 2;
            mright = -scaleMargin;
            mbottom = -scaleMargin / 2;
        } else if (x > FDivider && x <= SDivider && y > FDivider && y <= SDivider) {
            mleft = -scaleMargin / 2;
            mtop = -scaleMargin / 2;
            mright = -scaleMargin / 2;
            mbottom = -scaleMargin / 2;
        } else if (x > SDivider && y > FDivider && y <= SDivider) {
            mleft = -scaleMargin;
            mtop = -scaleMargin / 2;
            mright = 0;
            mbottom = -scaleMargin / 2;
        } else if (x <= FDivider && y > SDivider) {
            mleft = 0;
            mtop = 0;
            mright = -scaleMargin;
            mbottom = -scaleMargin;
        } else if (x > FDivider && x <= SDivider && y > SDivider) {

            mleft = -scaleMargin / 2;
            mtop = 0;
            mright = -scaleMargin / 2;
            mbottom = -scaleMargin;

        } else if (x > SDivider && y > SDivider) {
            mleft = -scaleMargin;
            mtop = 0;
            mright = 0;
            mbottom = -scaleMargin;

        }

    }

    private void postEnvet(String msgType) {
        Log.i("qizi", "ostEnvet()");
        if (mylistener == null)
            return;
        if ("2".equals(msgType) || "1".equals(msgType)) {
            String Stringxy = "";
            String Stringx = "";
            String Stringy = "";
            if (topicx > 0 && topicx < 10) {
                Stringx = "0" + topicx;
            } else {
                Stringx = String.valueOf(topicx);
            }

            if (topicy > 0 && topicy < 10) {
                Stringy = "0" + topicy;
            } else {
                Stringy = String.valueOf(topicy);
            }
            Stringxy = Stringy + Stringx;
            mylistener.apply(msgType, Stringxy);
        } else {
            mylistener.apply(msgType, "");
        }

    }

    public void setListener(Function listener) {
        this.mylistener = listener;
    }

    // ------------------------------------------------------------------画图

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //抗锯齿设置
        canvas.setDrawFilter
                (new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        // 画棋盘w
        drawLineGrid(canvas);
        drawStar(canvas);

        setCoord();
        // 是否显示坐标
        if (is_coord) {
            drawCoordinate(canvas);
        }

        // 画棋子
        if (is_judge) {
            // 2022-01-19 修改 解决java.lang.ArrayIndexOutOfBoundsException
            try{
                drawPiece(canvas);// 画棋子
            } catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }

        } else {
            // 2022-01-19 修改 解决java.lang.ArrayIndexOutOfBoundsException
            try{
                AkeladrawPiece(canvas);// 教练端局势判断
                markPieces(canvas);// 标记死子
            } catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }

        switch (isErrorStep) {
            case 1:
                drawErrorStep(canvas);
                // drawTextA(canvas);
                break;

            default:
                break;
        }

        if (WinPoints.size() >= 5) {
            drawWinStar(canvas);
        }

        if (showNum) {
            // 显示手顺
            drawNum(canvas);
        } else {
            if (isShowEndNum) {
                // 显示最后落子位置
                drawFlag(canvas);
            }

        }

        if(showWrongPosition){
            if(errorList==null||errorList.size()==0){
                drawWrongPosition(canvas,(float)(x2Screen(wrongTopicX)-tileSize/3) , (float)(y2Screen(wrongTopicY)-tileSize/3),isDismiss);
            }else{
                drawWrongPositionMore(canvas,errorList);
            }
        }

        // if (isTeach)
        // {
        // drawTextA(canvas);
        // }
        // 显示预落子位置

        if (prePutFlag) {
            drawPreFlag(canvas);
            prePutFlag = false;
        }

        // 显示热点图
        if (hotPhoto) {
            drawHotPhoto(canvas);
            hotPhoto = false;
        }
    }


    private void drawHotPhoto(Canvas canvas) {

         paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        float circleSize= (float) (tileSize*2/5);
            String[] str=hotPhotoInfo.substring(1).split("#");

            for(int i=0;i<str.length;i++){

                int y= Integer.parseInt(str[i].substring(1,3).startsWith("0")?str[i].substring(2,3):str[i].substring(1,3));
                int x= Integer.parseInt(str[i].substring(3).startsWith("0")?str[i].substring(4):str[i].substring(3));
                switch (i){

                    case 0:
                        paint1.setColor(getContext().getResources().getColor(R.color.red1));
                        canvas.drawCircle(x2Screen(x), y2Screen(y), circleSize,paint1);
                        break;
                    case 1:
                        paint1.setColor(getContext().getResources().getColor(R.color.red2));
                        canvas.drawCircle(x2Screen(x), y2Screen(y),circleSize, paint1);
                        break;
                    case 2:
                        paint1.setColor(getContext().getResources().getColor(R.color.red3));
                        canvas.drawCircle(x2Screen(x), y2Screen(y), circleSize,paint1);
                        break;
                    case 3:
                        paint1.setColor(getContext().getResources().getColor(R.color.red4));
                        canvas.drawCircle(x2Screen(x), y2Screen(y),circleSize, paint1);
                        break;

                }

        }
    }

    // 标记获取胜利的连珠子
    private void drawWinStar(Canvas canvas) {
        markpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markpaint.setAntiAlias(true);// 设置Paint为无锯齿
        markpaint.setStyle(Paint.Style.FILL);// 设置paint的风格为“实心”
        markpaint.setColor(Color.RED);// 设置画笔颜色

        for (int i = 0; i < WinPoints.size(); i++) {
            float isx = x2Screen(WinPoints.get(i).getX() + 1);
            float isy = y2Screen(WinPoints.get(i).getY() + 1);

            canvas.drawRect(isx - Box_size, isy - Box_size, isx + Box_size, isy
                    + Box_size, markpaint);
        }

    }


    private void drawErrorStep(Canvas canvas) {

        canvas.drawBitmap(errorPlot, x2Screen(topicx) - errorPlot.getWidth()
                / 2, y2Screen(topicy) - errorPlot.getHeight() / 2, null);
    }

    //绘制数子完成后的方块
    private void AkeladrawPiece(Canvas canvas) {
        for (int x = 1; x <= boardSize; x += 1) {
            for (int y = 1; y <= boardSize; y += 1) {
                isbw = pieces[x - 1][y - 1];
                if (isbw != Board.None) {
                    if (isbw == Board.Black) {
                        canvas.drawBitmap(bmpBlack,
                                x2Screen(x) - bmpBlack.getWidth() / 2,
                                y2Screen(y) - bmpBlack.getHeight() / 2, null);

                    } else if (isbw == Board.White) {
                        canvas.drawBitmap(bmpWhite,
                                x2Screen(x) - bmpWhite.getWidth() / 2,
                                y2Screen(y) - bmpWhite.getHeight() / 2, null);

                    }

                    if (isbw == BlackDead) {
                        canvas.drawBitmap(bmpBlack,
                                x2Screen(x) - bmpBlack.getWidth() / 2,
                                y2Screen(y) - bmpBlack.getHeight() / 2, null);

                    } else if (isbw == WhiteDead) {
                        canvas.drawBitmap(bmpWhite,
                                x2Screen(x) - bmpWhite.getWidth() / 2,
                                y2Screen(y) - bmpWhite.getHeight() / 2, null);

                    }

                    if (isbw == BlackNone) {
                    } else if (isbw == WhiteNone) {
                    }
                }
            }
        }
    }

    /**
     * 标记死子的位置
     */
    private void markPieces(Canvas canvas) {
        markpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markpaint.setAntiAlias(true);// 设置Paint为无锯齿
        markpaint.setStyle(Paint.Style.FILL);// 设置paint的风格为“实心”

        int x = 1;
        int y = 1;
        Box_size= (int) (tileSize*1/5);
        for (x = 1; x <= boardSize; x += 1) {
            for (y = 1; y <= boardSize; y += 1) {
                float isx = x2Screen(x);
                float isy = y2Screen(y);
                isbw = pieces[x - 1][y - 1];

                if (isbw == BlackDead) {
                    markpaint.setColor(Color.WHITE);// 设置画笔颜色
                    canvas.drawRect(isx - Box_size, isy - Box_size, isx
                            + Box_size, isy + Box_size, markpaint);
                } else if (isbw == WhiteDead) {
                    markpaint.setColor(Color.BLACK);
                    canvas.drawRect(isx - Box_size, isy - Box_size, isx
                            + Box_size, isy + Box_size, markpaint);
                } else if (isbw == BlackNone) {
                    markpaint.setColor(Color.WHITE);// 设置画笔颜色
                    canvas.drawRect(isx - Box_size, isy - Box_size, isx
                            + Box_size, isy + Box_size, markpaint);
                } else if (isbw == WhiteNone) {
                    markpaint.setColor(Color.BLACK);
                    canvas.drawRect(isx - Box_size, isy - Box_size, isx
                            + Box_size, isy + Box_size, markpaint);
                }
            }
        }
    }

    /******************
     * 画字母标记
     ********************/
    private void drawTextA(Canvas canvas, String str, int x, int y) {
        Paint paint = new Paint();
        // paint.setTextSize(30);
        // paint.setColor(Color.RED);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(Color.RED);

        paint.setTextSize(2 * bmpBlack.getWidth() / 3);

        canvas.drawText(str, x2Screen(x) - 3 * bmpBlack.getWidth() / 14,
                y2Screen(y) + bmpBlack.getHeight() / 4, paint);
    }

    // 画棋子
    private void drawPiece(Canvas canvas) {

        int i = 0;
        for (int x = 1; x <= boardSize; x += 1) {
            for (int y = 1; y <= boardSize; y += 1) {
                int bw = board.getValue(x, y);
                if (bw != Board.None) {

                    // Bitmap bmp;

                    if (bw == Board.Black) {
                        canvas.drawBitmap(bmpBlack,
                                x2Screen(x) - bmpBlack.getWidth() / 2,
                                y2Screen(y) - bmpBlack.getHeight() / 2, null);

                    } else if (bw == Board.White) {
                        canvas.drawBitmap(bmpWhite,
                                x2Screen(x) - bmpWhite.getWidth() / 2,
                                y2Screen(y) - bmpWhite.getHeight() / 2, null);

                    } else if (bw == Board.FLAG) {
                        if (isEndTopic) {
                            String X = x + "";
                            String Y = y + "";
                            if (x < 10) {
                                X = "0" + x;
                            }

                            if (y < 10) {
                                Y = "0" + y;
                            }
                            String position = Y + "" + X;
                            String str = listNum.get(position);
                            if (str == null) {
                                continue;
                            }
                            drawTextA(canvas, str, x, y);
                            i++;
                        }
                    }
                }
            }
        }
    }

    // 画最后落子位置的标记
    private void drawFlag(Canvas canvas) {
        paint.setColor(Color.RED);
        // paint.setTextSize((float) (numsize*agian));
        paint.setTextSize(textSize);

        Coordinate c = board.getLastPosition();
        float x = 0;
        float y = 0;
        if (c != null) {
            x = x2Screen(c.x);
            y = y2Screen(c.y);

            float n1 = paint.measureText("1");
            float n2 = paint.measureText("10");
            float n3 = paint.measureText("100");

            int type;
            type = board.getCount();

            if (isbtest) {

                if (board.getCount() > testcount) {
                    type = board.getCount() - testcount;

                } else {

                    if (isLet) {
                        if (board.getCount() > (letnum * 2 - 1)) {
                            type = board.getCount() - (letnum * 2 - 1);
                        }
                    }
                }

            } else {

                if (isLet) {
                    if (board.getCount() > (letnum * 2 - 1)) {
                        type = board.getCount() - (letnum * 2 - 1);
                    }
                }

            }

            if (type < 10) {
                x = x - n1 / 2;
                y = y + n1 / 2;

            } else if (type < 100) {
                x = x - n2 / 2;
                y = y + n1 / 2;
            } else {
                x = x - n3 / 2;
                y = y + n1 / 2;
            }

            if (isbtest) {
                canvas.drawText(String.valueOf(type), x, y, paint);
            } else {
                if (isLet) {
                    if (board.getCount() > (letnum * 2 - 1)) {
                        // 2020-09-29 增加 解决最后落子为黑棋、手数看不清的问题
                        if(board.getCurBW().equals("-")){
                            paint.setColor(Color.WHITE);
                        }
                        canvas.drawText(String.valueOf(type), x, y, paint);
                    }
                } else {
                    // 2020-09-29 增加 解决最后落子为黑棋、手数看不清的问题
                    if(board.getCurBW().equals("-")){
                        paint.setColor(Color.WHITE);
                    }
                    canvas.drawText(String.valueOf(type), x, y, paint);
                }
            }
        }
    }

    // 画棋盘的星
    private void drawStar(Canvas canvas) {
        paint.setColor(Color.BLACK);

        for (Coordinate c : Utils.createStar()) {
            if (c != null) {
                // canvas.drawCircle(x2Screen(c.x), y2Screen(c.y), 3f, paint);
                canvas.drawCircle(x2Screen(c.x), y2Screen(c.y),
                        DensityUtils.dp2px(context, 2.2f), paint);
            }
        }
    }

    // 画错误的位置
    public void drawWrongPosition(Canvas canvas, float x, float y,boolean isDismiss2){
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);

        if(isDismiss2){
            canvas.drawBitmap(reducePlot,x, y, paint1);
        }else{
            canvas.drawBitmap(errorPlot,x, y, paint1);
        }

    }

    // 画错误的位置（有多个时）
    public void drawWrongPositionMore(Canvas canvas, List<String> errorList){
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        for(String str:errorList){
            String str2[]=str.split("#");

                int x= Integer.parseInt(str2[0]);
                int y= Integer.parseInt(str2[1]);
                if(str2.length==3){
                    int nowColor=Integer.parseInt(str2[2]);
                    if(nowColor!=0){
                        canvas.drawBitmap(addPlot,(float)(x2Screen(x)-tileSize/3) , (float)(y2Screen(y)-tileSize/3), paint1);
                    }else{
                        canvas.drawBitmap(reducePlot,(float)(x2Screen(x)-tileSize/3) , (float)(y2Screen(y)-tileSize/3), paint1);
                    }
                }else{
                    canvas.drawBitmap(errorPlot,(float)(x2Screen(x)-tileSize/3) , (float)(y2Screen(y)-tileSize/3), paint1);
                }

        }
    }

    // 画棋盘网格线
    private void drawLineGrid(Canvas canvas) {

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);//2018-5-3修改
        for (int i = 1; i <= boardSize; i++) {
            drawVLine(canvas, i);
            drawHLine(canvas, i);
        }
    }

    // 垂直线
    private void drawVLine(Canvas canvas, int i) {
        canvas.drawLine(x2Screen(i), y2Screen(1), x2Screen(i),
                y2Screen(boardSize), paint);
    }

    // 水平线
    private void drawHLine(Canvas canvas, int i) {
        canvas.drawLine(x2Screen(1), y2Screen(i), x2Screen(boardSize),
                y2Screen(i), paint);
    }

    // 画坐标轴标记
    private void drawCoordinate(Canvas canvas) {
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint2.setColor(Color.BLACK);

        paint2.setTextSize(textSize);

        paint2.setStrokeWidth(3);

        // canvas.drawColor(Color.BLACK);

        int itileSize = (int) tileSize;

        for (int i = 1; i <= boardSize; i++) {
            // X-轴
            canvas.drawText(getAlpha(i), itileSize * (i - 1) + (int) xOffset,
                    (int) yOffset - itileSize / 2, paint2);
            // Y-轴
            canvas.drawText(String.valueOf(boardSize - i + 1), (int) xOffset
                    - (itileSize / 5 * 4), itileSize * (i - 1) + itileSize / 4
                    + (int) yOffset, paint2);
        }
    }

    private String getAlpha(int i) {
        String list = "ABCDEFGHJKLMNOPQRST";

        char[] charList = list.toCharArray();

        return String.valueOf(charList[i - 1]);
    }

    // 手顺
    private void drawNum(Canvas canvas) {
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint1.setTextSize(textSize);
        paint1.setStrokeWidth(3);
        float n1 = paint1.measureText("1");
        float n2 = paint1.measureText("10");
        float n3 = paint1.measureText("100");

        int[][] a = new int[boardSize][boardSize];

        float x = 0;
        float y = 0;
        if (isInTopic && board.getCount() == testcount) {

        } else {
            for (int i = board.getCount() - 1; i >= 0; i--) {

                type = i + 1;

                if (isbtest) {

                    if (i + 1 > testcount) {
                        type = i + 1 - testcount;

                    } else {

                        if (isLet) {
                            if (i + 1 > (letnum * 2 - 1)) {
                                type = i + 1 - (letnum * 2 - 1);
                            }
                        }
                    }

                } else {

                    if (isLet) {
                        if (i + 1 > (letnum * 2 - 1)) {
                            type = i + 1 - (letnum * 2 - 1);
                        }
                    }

                }

                PieceProcess pc = board.getPieceProcess(i);

                Coordinate c = pc.c;
                x = x2Screen(c.x);
                y = y2Screen(c.y);

                if (c.x == 0 || c.y == 0) {
                    continue;
                }

                if (a[c.x - 1][c.y - 1] == 1) {
                    continue;
                }

                if (type < 10) {
                    x = x - n1 / 2;
                    y = y + n1 / 2;

                } else if (type < 100) {
                    x = x - n2 / 2;
                    y = y + n1 / 2;
                } else {
                    x = x - n3 / 2;
                    y = y + n1 / 2;
                }

                if (pc.bw == Board.Black) {
                    paint1.setColor(Color.WHITE);
                } else if (pc.bw == Board.White) {
                    paint1.setColor(Color.BLACK);
                }

                if (i == board.getCount() - 1) {
                    paint1.setColor(Color.RED);
                }

                if (isbtest) {
                    if (i >= testcount) {
                        canvas.drawText(String.valueOf(type), x, y, paint1);

                    } else {
                        if (board.getCount() - 1 < testcount) {
                            canvas.drawText(String.valueOf(type), x, y, paint1);

                        }
                    }
                } else {
                    if (isLet) {
                        if (i + 1 > letnum * 2 - 1) {
                            canvas.drawText(String.valueOf(type), x, y, paint1);
                        }
                    } else {
                        canvas.drawText(String.valueOf(type), x, y, paint1);
                    }
                }

                a[c.x - 1][c.y - 1] = 1;
            }
        }
    }

    // 预落子位置
    private void drawPreFlag(Canvas canvas) {

        if (board.getCurBW().equals("+")) {
            canvas.drawBitmap(bmpPreBlack,
                    x2Screen(prePos.x) - bmpPreBlack.getWidth() / 2,
                    y2Screen(prePos.y) - bmpPreBlack.getHeight() / 2, null);
        } else {
            canvas.drawBitmap(version>=100?bmpPreWhite101:bmpPreWhite,
                    x2Screen(prePos.x) - bmpPreWhite.getWidth() / 2,
                    y2Screen(prePos.y) - bmpPreWhite.getHeight() / 2, null);
        }

    }

    // ------------------------------------------------------------------坐标变换
    // 虚拟坐标转化为屏幕坐标(虚拟坐标从1开始）
    public float x2Screen(int x) {
        return (float) ((x - 1) * tileSize + xOffset);
    }

    public float y2Screen(int y) {
        // return (float) (y * tileSize + yOffset);
        return (float) ((boardSize - y) * tileSize + yOffset);
    }

    public int x2Coordinate(float x) {

        return (int) Math.round((x - xOffset) / tileSize) + 1;
    }

    public int y2Coordinate(float y) {

        return boardSize - (int) Math.round((y - yOffset) / tileSize);
    }

    public double Coordinate2x(int x) {
        return (x - 1) * tileSize + xOffset;
    }

    public double Coordinate2y(int y) {

        return (boardSize - y) * tileSize + yOffset;
    }

    // 初始化棋子
    public void initStone() {

        setStone();

        Bitmap tempBlack = ExpressionUtil.scaleBitmapFix(context, stone_new_b, 1);

        Bitmap tempWhite = ExpressionUtil.scaleBitmapFix(context, stone_new_w, 1);

        Bitmap tempPreBlack = ExpressionUtil.scaleBitmapFix(context,
                R.mipmap.black_pre, 1);

        Bitmap tempPreWhite = ExpressionUtil.scaleBitmapFix(context,
                R.mipmap.white_pre_new, 1);

        Bitmap tempPreWhite101 = ExpressionUtil.scaleBitmapFix(context,
                R.mipmap.white_pre_101, 1);

        Bitmap errorBitmap = ExpressionUtil.scaleBitmapFix(context,
                R.mipmap.error_step, 1);

        Bitmap addBitmap=ExpressionUtil.scaleBitmapFix(context,
                R.mipmap.add_plot, 1);

        Bitmap reduceBitmap=ExpressionUtil.scaleBitmapFix(context,
                R.mipmap.reduce_plot, 1);

        Matrix matrix = new Matrix();
        matrix.postScale((float) (tileSize / tempBlack.getWidth()),
                (float) (tileSize / tempBlack.getHeight()));

        bmpBlack = Bitmap.createBitmap(tempBlack, 0, 0, tempBlack.getWidth(),
                tempBlack.getHeight(), matrix, true);

        bmpWhite = Bitmap.createBitmap(tempWhite, 0, 0, tempWhite.getWidth(),
                tempWhite.getHeight(), matrix, true);


        errorPlot = Bitmap.createBitmap(errorBitmap, 0, 0,
                    errorBitmap.getWidth(), errorBitmap.getHeight(), matrix, true);


        addPlot=Bitmap.createBitmap(addBitmap, 0, 0,
                addBitmap.getWidth(), addBitmap.getHeight(), matrix, true);

        reducePlot=Bitmap.createBitmap(reduceBitmap, 0, 0,
                reduceBitmap.getWidth(), reduceBitmap.getHeight(), matrix, true);

        // 预落子位置
        Matrix matrix2 = new Matrix();
        matrix2.postScale((float) (tileSize / tempPreBlack.getWidth()),
                (float) (tileSize / tempPreBlack.getHeight()));

        bmpPreBlack = Bitmap.createBitmap(tempPreBlack, 0, 0,
                tempPreBlack.getWidth(), tempPreBlack.getHeight(), matrix2,
                true);

        bmpPreWhite = Bitmap.createBitmap(tempPreWhite, 0, 0,
                tempPreWhite.getWidth(), tempPreWhite.getHeight(), matrix2,
                true);

        bmpPreWhite101 = Bitmap.createBitmap(tempPreWhite101, 0, 0,
                tempPreWhite101.getWidth(), tempPreWhite101.getHeight(), matrix2,
                true);

        agian = tileSize / bmpBlack.getWidth();
        // 字体大小
        String scale = SharedPrefsUtil.getValue(context, "scaledDensity", "2");
        // textSize = DisplayUtil.px2sp((float) (tileSize * 1 / 4),
        // Float.parseFloat(scale)) + 1;

        // 手顺的放大缩小
        textSize = (float) (tileSize * 2 / 5);
        textSizeNum=(float) (tileSize * 3 / 5);

        textSizepx = DensityUtils.sp2px(textSize, Float.parseFloat(scale));
    }

    public void sound() {
        issound = SharedPrefsUtil.getValue(this.getContext(), "sound", true);

        if (Board.hasPickother) {
            if (issound)// ----------落子声音的设置
            {
                poolutil.playSound(1);
            }
            Board.hasPickother = false;
        } else {
            if (issound)// ----------落子声音的设置
            {
                poolutil.playSound(0);
            }
        }

    }

    /**
     * 缩放 李朦利 2016-2-24
     *
     * @param x     坐标值
     * @param y     坐标值
     * @param width 所有棋子所占区域宽
     * @param hight 所有棋子所占区域高
     */

    public void ScaleTile(float x, float y, int width, int hight) {
        if (isCanScale) {
            CaculateMarginsInTopic(x, y, width, hight);
            MarginLayoutParams marginPar = (MarginLayoutParams) getLayoutParams();
            LogUtils.d("m==",mleft+","+mtop+","+mright+","+mbottom);
            marginPar.setMargins(mleft, mtop, mright, mbottom);
            // scrollTo(0, 0);
            setLayoutParams(marginPar);
        }
    }

    /**
     * 找到需要缩放的中心点并进行缩放 李朦利2016-2-24 外部调用时传入值
     *
     * @param xs 所有棋子X坐标集合
     * @param ys 所有棋子Y坐标集合
     */
    public void ScaleCenter(List<Float> xs, List<Float> ys) {
        float xMax = Collections.max(xs);
        float yMax = Collections.max(ys);
        float xMin = Collections.min(xs);
        float yMin = Collections.min(ys);
        int width = (int) (yMax - yMin);
        int hight = (int) (xMax - xMin);
        float x = (xMax + xMin) / 2;
        float y = (yMax + yMin) / 2;
        ScaleTile(x, y, width, hight);
    }

    // ------------------------------------------------------------------事件

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {


        int wh = Math.min(w, h);

        // 是否显示坐标
        if (is_coord) {
            // 根据原tileSize计算
            tileSize = wh / (boardSize + 0.8);
            xOffset = tileSize;
            yOffset = tileSize;
        } else {
            tileSize = wh / (double) boardSize;
            xOffset = tileSize / 2;
            yOffset = tileSize / 2;
        }

        if (screenWidth == 0)
            screenWidth = wh;
        // scaleMargin = (int) (tileSize * (Board.n - 8)); // 放大1倍

        initStone();

        // putType = sp.getInt("putType", 0);
        if (putType == -1) { // 表示尚未被强行指定模式，则取配置文件设定模式
            putType = SharedPrefsUtil.getValue(context.getApplicationContext(),
                    "putType", 0);
        }

        super.onSizeChanged(wh, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mGestureDetector.onTouchEvent(event);

        return mScaleGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int m = Math.max(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(m, m);
    }

    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {

    }

    /**
     * 设置是否显示坐标
     */
    public void setCoord() {

        sp = context.getSharedPreferences("setconfig",
                Context.MODE_PRIVATE);

        String coord = sp.getString("is_coord", null);

        if (coord == null) {
            is_coord = true;
        } else {
            if (coord.equals("true"))// 显示
            {
                is_coord = true;
            } else if (coord.equals("false"))// 隐藏
            {
                is_coord = false;
            }
        }
    }

    public void setStone() {
        sp = context.getSharedPreferences("setconfig",
                Context.MODE_PRIVATE);
        if (sp == null) {
            stone_new_b = R.mipmap.stone_b1;
            stone_new_w = R.mipmap.stone_w1;
        } else {
            String w = sp.getString("cmw", null);
            String b = sp.getString("cmb", null);
            if (b == null & w == null) {
                stone_new_b = R.mipmap.stone_b1;
                stone_new_w = R.mipmap.stone_w1;
            } else {
                int resId1 = getResources().getIdentifier(b, null, null);
                int resId2 = getResources().getIdentifier(w, null, null);
                stone_new_b = resId1;
                stone_new_w = resId2;
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void findTileView(Context context) {
        this.paint = new Paint();
        this.xOffset = 0.5;
        this.yOffset = 0.5;
        this.tileSize = 1;
        this.board = new Board();
        this.context = context;

        if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            poolutil = new SoundPoolUtils(context);
        }
        mGestureDetector = new GestureDetector(mOnGestureListener);
        // 传入双击监听器对象
        mGestureDetector.setOnDoubleTapListener(mDoubleTapListener);

        mScaleGestureDetector = new ScaleGestureDetector(this.getContext(),
                mOnScaleGestureListener);
    }

    @Override
    public void setBackground(Drawable background) {
        if (VERSION.SDK_INT >= 16) {
            super.setBackground(background);
        } else {
            setBackgroundDrawable(background);
        }
    }
}
