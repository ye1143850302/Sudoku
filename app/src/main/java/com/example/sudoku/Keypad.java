package com.example.sudoku;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class Keypad  extends Dialog {//用dialog实现小键盘的显示
    protected static final String TAG="Sudoku";
    private final View keys[]=new View[9];//数组，装载9个数字
    private View keypad;//界面对象
    private final int useds[];//数组用于装载不可用的数字
    private PuzzleView puzzleView;//画图类

    public Keypad(Context context, int useds[], PuzzleView puzzleView){//构造函数
        super(context);
        this.useds=useds;//获取不可用的数字文件
        this.puzzleView=puzzleView;//获取画
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.keypad);//调用keypad布局文件，显示小键盘，并给小键盘设置样式 背景图片等
        findViews();//获得控件
        for (int element : useds) {//查找不可用的数字
            if(element!=0){
                keys[element-1].setVisibility(View.INVISIBLE);//设置小键盘上不可用的数字button对象为不可见
            }
            setListeners();//设置控件的各种监听方法
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//绘制小键盘
        // TODO Auto-generated method stub
        int tile=0;
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_SPACE:tile=0;break;
            case KeyEvent.KEYCODE_1:tile=1;break;
            case KeyEvent.KEYCODE_2:tile=2;break;
            case KeyEvent.KEYCODE_3:tile=3;break;
            case KeyEvent.KEYCODE_4:tile=4;break;
            case KeyEvent.KEYCODE_5:tile=5;break;
            case KeyEvent.KEYCODE_6:tile=6;break;
            case KeyEvent.KEYCODE_7:tile=7;break;
            case KeyEvent.KEYCODE_8:tile=8;break;
            case KeyEvent.KEYCODE_9:tile=9;break;//当该对应的键盘被按下时候
            default:
                return super.onKeyDown(keyCode, event);
        }
        if(isValid(tile)){//如果按下的键是
            returnResult(tile);
        }
        return true;
    }

    private boolean isValid(int tile) {
        // TODO Auto-generated method stub
        for (int t : useds) {
            if(tile==t)
                return false;
        }
        return true;
    }

    private void findViews() {
        // TODO Auto-generated method stub
        keypad=findViewById(R.id.keypad);
        keys[0]=findViewById(R.id.keypad_1);
        keys[1]=findViewById(R.id.keypad_2);
        keys[2]=findViewById(R.id.keypad_3);
        keys[3]=findViewById(R.id.keypad_4);
        keys[4]=findViewById(R.id.keypad_5);
        keys[5]=findViewById(R.id.keypad_6);
        keys[6]=findViewById(R.id.keypad_7);
        keys[7]=findViewById(R.id.keypad_8);
        keys[8]=findViewById(R.id.keypad_9);
    }
    private void setListeners(){//为小键盘设置监听函数
        for(int i=0;i<keys.length;i++){
            final int t=i+1;
            keys[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    returnResult(t);
                }
            });
        }
        keypad.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {//设置监听函数
                // TODO Auto-generated method stub
                returnResult(0);
            }
        });
    }
    private void returnResult(int tile) {//返回结果函数
        // TODO Auto-generated method stub
        puzzleView.setSelectedTile(tile);//绘制用户选择的数字
        dismiss();
    }
}

