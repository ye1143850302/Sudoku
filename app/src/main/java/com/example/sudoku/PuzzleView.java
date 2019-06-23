package com.example.sudoku;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
@SuppressLint("DrawAllocation")//画图
public class PuzzleView extends View {
    private static final String TAG = "Sudoku";
    private final Game game;

    private float width;
    private float height;
    private int selX;
    private int selY;//设置长宽和坐标
    private final Rect selRect = new Rect();
//Rect类主要用于表示坐标系中的一块矩形区域，
// 并可以对其做一些简单操作。这块矩形区域，
// 需要用左上右下两个坐标点表示（left,top,right,bottom）
// 你也可以获取一个Rect 实例的Width和Height
    private static final String SELX="selX";
    private static final String SELY="selY";
    private static final String VIEW_STATE="viewState";//坐标的字符形式  视图的状态
    private static final int ID=42;//any positive int num

    public PuzzleView(Context context) {//实现构造函数
        super(context);
        this.game = (Game) context;//获取游戏的内容
        setFocusable(true);//控制键盘可以获得这个按钮的焦点
        setFocusableInTouchMode(true);//设置焦点联系方式 获得焦点时 允许碰触
        setId(ID);//设置ID
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//onSizeChanged函数实际上是运行在View的layout方法中，
        // TODO Auto-generated method stub
        width = w / 9f;
        height = h / 9f;//计算单元格的宽和高
        getRect(selX, selY, selRect);
        Log.d(TAG, "onSizeChanged:width" + width + ",height" + height);
        super.onSizeChanged(w, h, oldw, oldh);
    }//当前活动主窗口大小改变时调用

    //实例状态保存在bundle中，保存当前游戏状态
    @Override
    protected Parcelable onSaveInstanceState() {//parcelable是序列化的接口  处理窗口保存事件
        // TODO Auto-generated method stub
        //用户打包需要传输的数据，然后在Binder中传输,用于跨进程传输数据
        Parcelable p=super.onSaveInstanceState();//打包状态
        Log.d(TAG, "onSavedInstanceState");
        Bundle bundle=new Bundle();
        bundle.putInt(SELX, selX);
        bundle.putInt(SELY, selY);//保存当前的坐标
        bundle.putParcelable(VIEW_STATE, p);//保存当前的状态，用bundle传输
        return bundle;
    }
    //恢复已经保存的信息
    @Override
    protected void onRestoreInstanceState(Parcelable state) {//处理窗口还原事件
        // TODO Auto-generated method stub
        Log.d(TAG, "onRestoreInstanceState");
        Bundle bundle=(Bundle) state;
        select(bundle.getInt(SELX),bundle.getInt(SELY));//通过bundle对象获取 保存的坐标
        super.onRestoreInstanceState(bundle.getParcelable(VIEW_STATE));//通过bundle对象获取存放的状态信息
        return;
    }

    @Override
    protected void onDraw(Canvas canvas) {//画图，直接使用canvas对象处理当前的画布
        // TODO Auto-generated method stub
        // draw background
        Paint background = new Paint();//生成用于画背景色的画笔
        background.setColor(getResources().getColor(R.color.puzzle_background));//设置背景的颜色
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);
//设置当前画布的背景颜色为background中定义的颜色，以0,0作为起点，绘制背景色
// 以当前画布的宽度和高度为重点即整块画布来填充，
// 而Paint作为绘画方式的对象可以设置颜色，大小，甚至字体的类型等等。  

        // draw board
        Paint dark = new Paint();
        dark.setColor(getResources().getColor(R.color.puzzle_dark));

        Paint hilite = new Paint();
        hilite.setColor(getResources().getColor(R.color.puzzle_hilite));

        Paint light = new Paint();
        light.setColor(getResources().getColor(R.color.puzzle_light));

        // draw minor grid lines
        for (int i = 0; i < 9; i++) {
            //以下两行代码用户绘制横向的单元格
            //明线和黑线才能合成一条雕刻的线
            canvas.drawLine(0, i * height, getWidth(), i * height, light);
            canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1,
                    hilite);
            //纵向
            canvas.drawLine(i * width, 0, i * width, getHeight(), dark);
            canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(),
                    hilite);
        }
        // draw major grid lines绘制黑线每三格一划分
        for (int i = 0; i < 9; i++) {
            if (i % 3 != 0)
                continue;
            //以下两行代码用户绘制横向的单元格
            // 明线和黑线才能合成一条雕刻的线
            canvas.drawLine(0, i * height, getWidth(), i * height, dark);
            canvas.drawLine(0, i * height + 1, getWidth(), i * height + 1,
                    hilite);
                 //纵向
            canvas.drawLine(i * width, 0, i * width, getHeight(), dark);
            canvas.drawLine(i * width + 1, 0, i * width + 1, getHeight(),
                    hilite);
        }
        // draw numbers 绘制数字
        Paint foreground = new Paint(Paint.ANTI_ALIAS_FLAG);//生成画数字的画笔
        foreground.setColor(getResources().getColor(R.color.puzzle_foreground));//设置字体颜色
        foreground.setStyle(Style.FILL);//
        foreground.setTextSize(height * 0.75f);
        foreground.setTextScaleX(width / height);
        foreground.setTextAlign(Paint.Align.CENTER);//设置数字在框内的布局 居中对齐
        // draw num in the center of the tile
        FontMetrics fm = foreground.getFontMetrics();//定义了字体规格对象，该对象封装了有关在特定屏幕上呈现特定字体的信息。主要是绘制区域
        float x = width / 2;
        float y = height / 2 - (fm.ascent + fm.descent) / 2;//横竖都居中
        //Ascent： 字符顶部到baseLine的距离。
        //Descent： 字符底部到baseLine的距离。
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                canvas.drawText(this.game.getTitleString(i, j), i * width + x,
                        j * height + y, foreground);//画数字
            }
        }
        // draw the selection
        Log.e(TAG, "selRect=" + selRect);
        Paint selected = new Paint();
        selected.setColor(getResources().getColor(R.color.puzzle_selected));//生成选择的对象的画笔 按下变色的颜色
        canvas.drawRect(selRect, selected);

        //draw the hints pick a hint color based on moves left
        //根据每个单元格可填的数目给出不同颜色的提示
        if(Settings.getHints(getContext())){
            Paint hint=new Paint();
            int c[]={getResources().getColor(R.color.puzzle_hint_0),//表示目前填入不合适  需要从新填入
                    getResources().getColor(R.color.puzzle_hint_1),//表示只有一个数字可以填入
                    getResources().getColor(R.color.puzzle_hint_2),};//不同颜色的提醒  表示有两个数字可以填入
            Rect r=new Rect();
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    int movesleft=9-game.getUsedTiles(i, j).length;
                    if(movesleft<c.length){
                        getRect(i, j, r);
                        hint.setColor(c[movesleft]);
                        canvas.drawRect(r, hint);
                    }
                }
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//是接口KeyEvent.Callback中的抽象方法
        // TODO Auto-generated method stub
        // 用来捕捉手机键盘被按下的事件，参数keyCode，该参数为被按下的键值即键盘码，
        // //重写的键盘按下监听 参数event，该参数为按键事件的对象，其中包含了触发事件的详细信息
        Log.d(TAG, "onKeyDown:keycode=" + keyCode + ",event=" + event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                select(selX, selY - 1);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                select(selX, selY + 1);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                select(selX - 1, selY);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                select(selX + 1, selY);
                break;
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_SPACE:
                setSelectedTile(0);
                break;
            case KeyEvent.KEYCODE_1:
                setSelectedTile(1);
                break;
            case KeyEvent.KEYCODE_2:
                setSelectedTile(2);
                break;
            case KeyEvent.KEYCODE_3:
                setSelectedTile(3);
                break;
            case KeyEvent.KEYCODE_4:
                setSelectedTile(4);
                break;
            case KeyEvent.KEYCODE_5:
                setSelectedTile(5);
                break;
            case KeyEvent.KEYCODE_6:
                setSelectedTile(6);
                break;
            case KeyEvent.KEYCODE_7:
                setSelectedTile(7);
                break;
            case KeyEvent.KEYCODE_8:
                setSelectedTile(8);
                break;
            case KeyEvent.KEYCODE_9:
                setSelectedTile(9);
                break;
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                game.showKeypadOrError(selX, selY);//调用函数，显示键盘
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    //显示软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {//手机屏幕事件的处理方法
        // TODO Auto-generated method stub
        //参数event为手机屏幕触摸事件封装类的对象，其中封装了该事件的所有信息，例如触摸的位置、触摸的类型以及触摸的时间等。该对象会在用户触摸手机屏幕时被创建。
          //该方法处理三种情况的事件：屏幕被按下时，屏幕被抬起时，屏幕中拖动
        if(event.getAction()!=MotionEvent.ACTION_DOWN)//如果屏幕没有被按下
            return super.onTouchEvent(event);

        select((int)(event.getX()/width),(int)(event.getY()/height));
        game.showKeypadOrError(selX, selY);
        Log.d(TAG, "onTouchEvent:x"+selX+",y"+selY);
        return true;
    }

    public void setSelectedTile(int tile) {//title为用户选择的数字
        // TODO Auto-generated method stub
        //num is not valid for this tile
        Log.d(TAG, "selectedTile:invalid:"+tile);
   // startAnimation(AnimationUtils.loadAnimation(game, R.aims.shake));
        if (game.setTileIfValid(selX, selY, tile)) {
            invalidate();//该函数的作用是使整个窗口客户区无效。窗口的客户区无效意味着需要重绘
        } else {
            // num is not invalid for this tile
            Log.d(TAG, "setSelectedTile:invalid " + tile);
            //
        }

    }//android.util.Log常用的方法有以下5个：
    // Log.v() Log.d() Log.i() Log.w() 以及 Log.e() 。
    // 根据首字母对应VERBOSE，DEBUG,INFO, WARN，ERROR。
//Log.v 的调试颜色为黑色的，任何消息都会输出，这里的v代表verbose啰嗦的意思，平时使用就是Log.v("","");
    // 首先计算选定区域的x，y坐标，然后再次调用getRect方法计算新的选择矩形
//Log.d的输出颜色是蓝色的，仅输出debug调试的意思，但他会输出上层的信息，过滤起来可以通过DDMS的Logcat标签来选择.
 //Log.i的输出为绿色，一般提示性的消息information，它不会输出Log.v和Log.d的信息，但会显示i、w和e的信息
 //Log.w的意思为橙色，可以看作为warning警告，一般需要我们注意优化Android代码，同时选择它后还会输出Log.e的信息。
    //Log.e为红色，可以想到error错误，这里仅显示红色的错误信息，这些错误就需要我们认真的分析，查看栈的信息了。
   private void select(int x, int y) {
        // TODO Auto-generated method stub
        invalidate(selRect);// 第一次调用通知原选择的区域需要重绘
        selX = Math.min(Math.max(x, 0), 8);
        selY = Math.min(Math.max(y, 0), 8);
        getRect(selX, selY, selRect);
        invalidate(selRect);// 第二次调用通知新选择的区域也需要重绘
    }

    private void getRect(int x, int y, Rect rect) {
        // TODO Auto-generated method stub
        rect.set((int) (x * width), (int) (y * height),
                (int) (x * width + width), (int) (y * height + height));

    }

}


