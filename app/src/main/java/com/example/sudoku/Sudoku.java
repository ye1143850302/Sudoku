package com.example.sudoku;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class Sudoku extends AppCompatActivity implements OnClickListener{//继承监听接口 实现监听函数
    private static final String TAG = "Sudoku";//设置tag
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);//进入主界面
        View continueButton = this.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(this);//为继续游戏按钮设置监听

        View newButton = this.findViewById(R.id.new_button);
        newButton.setOnClickListener(this);//为新游戏设置监听

        View aboutButton = this.findViewById(R.id.about_button);
        aboutButton.setOnClickListener(this);//为关于设置监听

        View exitButton = this.findViewById(R.id.exit_button);
        exitButton.setOnClickListener(this);//为退出设置监听
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) //监听函数获取button的id
        {
            case R.id.continue_button:
                startGame(Game.DIFFICULTY_CONTINUE);//当点击的是继续按钮，调用开始函数，参数是difficulty_continue=-1
                break;
            case R.id.about_button:
                Intent i = new Intent(this, About.class);//当点击的是关于按钮，调用intent对象获取about活动的intent
                startActivity(i);//调用开始activity函数，开始aboutactivity
                break;
            case R.id.new_button:
                openNewGameDialog();//当点击的是新游戏button 调用对话框函数，跳出新游戏对话框
                break;
            case R.id.exit_button://当点击的是退出按钮
                finish();//调用finish函数，退出程序
                break;
        }
    }
    @Override
    protected void onResume() {//继续函数
        // TODO Auto-generated method stub
        super.onResume();
        Music.play(this, R.raw.nothing_to_lose);//程序开始时，调用music activity 开始播放音乐
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Music.stop(this);//暂停时，退出时，停止播放音乐
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//用于初始化菜单，其中menu参数就是即将要显示的Menu实例。
        // TODO Auto-generated method stub

        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();//创建menunflater对象
        inflater.inflate(R.menu.menu, menu);//调用Activity的getMenuInflater()得到一个MenuInflater，
        //使用inflate方法来把布局文件中的定义的菜单 加载给 第二个参数所对应的menu对象
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//菜单项监听函数，菜单项被点击时调用
        // TODO Auto-generated method stub
        switch (item.getItemId()) {//根据点击的菜单中的item的id进行处理
            case R.id.settings://根据菜单内的item，进入activity
                startActivity(new Intent(this, Settings.class));//如果点击的是设置，则进入setting activity
                return true;
        }
        return false;
    }

    private void openNewGameDialog() {//建立一个处理难度列表的用户界面  用dialog消息机制
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MenuDialog);//创建消息dialog对象
        builder.setTitle(R.string.new_game_title)//设置新游戏的难度
                .setItems(R.array.difficulty,//使用消息对象显示array内容
                        new DialogInterface.OnClickListener() {//为列表设置监听

                            @Override
                            public void onClick(
                                    DialogInterface dialoginterface, int i) {
                                // TODO Auto-generated method stub
                                startGame(i);//根据点击的item的id--难度，调用开始游戏函数，开始对应的界面
                            }
                        }).show();//显示该dialog
    }
    protected void startGame(int i) {//开始游戏函数
        // TODO Auto-generated method stub
        Log.i(TAG, "clicked on"+i);//根据点击的meg 传递的数据及item的id
        Intent intent=new Intent(Sudoku.this,Game.class);//获取游戏activity的
        intent.putExtra(Game.KEY_DIFFICULTY, i);//根据难度的ID打开对应的难度的界面，开始游戏
        startActivity(intent);//根据Game传入的数据，进入game activity开始游戏
    }
}
//AlertDialog的构造方法全部是Protected的，所以不能直接通过new一个AlertDialog来创建出一个AlertDialog。
//要创建一个AlertDialog，就要用到AlertDialog.Builder中的create()方法。
