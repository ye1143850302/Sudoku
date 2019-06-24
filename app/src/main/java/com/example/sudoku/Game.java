package com.example.sudoku;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
public class Game extends AppCompatActivity {

    private static final String TAG="Sudoku";
    private static final String PREF_PUZZLE="puzzle";
    protected static final int DIFFICULTY_CONTINUE=-1;

    public static final String KEY_DIFFICULTY="difficulty";
    public static final int DIFFICULTY_EASY=0;
    public static final int DIFFICULTY_MEDIUM=1;
    public static final int DIFFICULTY_HARD=2;//设置ID

    private int puzzle[]=new int[9*9];//设置数组，装数据
    private PuzzleView puzzleView;
    //三种游戏模式
    private static final String easyPuzzle="360000000004230800000004200"+
            "070460003820000014500013010"+
            "001900000007048300000000045";
    private static final String mediumPuzzle="650000070000506000014000005"+
            "007009000002314700000700800"+
            "500000630000201000030000097";
    private static final String hardPuzzle="009000000080605020501078000"+
            "000000700706040102004000000"+
            "000720903090301080000000600";//三种模式的初始化

    private final int used[][][]=new int[9][9][];//用于存储每个单元格已经不可用的数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        int diff=getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);//获取难度 difficulty easy =0  默认为简单
        puzzle=getPuzzle(diff);//获取当前数据
        calculateUsedTiles();//获取当前关的不可用数字数组

        puzzleView=new PuzzleView(this);//获取画图的对象
        setContentView(puzzleView);//设置内容画图
        puzzleView.requestFocus();//画的图，获取焦点
        //if the activity is restarted ,do a continue next time
        getIntent().putExtra(KEY_DIFFICULTY, DIFFICULTY_CONTINUE);
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Music.stop(this);
        //Save the current puzzle
        getPreferences(MODE_PRIVATE).edit().putString(PREF_PUZZLE, toPuzzleString(puzzle)).commit();//保存当前数据的状态 图像状态
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Music.play(this, R.raw.nothing_to_lose);//继续播放音乐
    }



    private void calculateUsedTiles() {////计算所有单元格对应的不可用的数据
        // TODO Auto-generated method stub
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                used[x][y]=calculateUsedTiles(x,y);
            }
        }
    }
    protected int[] getUsedTiles(int x,int y){
        return used[x][y];
    }//取出某一单元格中已经不可用的数据

    private int[] calculateUsedTiles(int x, int y) {//计算某一单元格之中已经不可用的数据
        // TODO Auto-generated method stub
        int c[]=new int[9];//
        //horizontal

        for(int i=0;i<9;i++){//垂直方向
            if(i==y)//如果这是用户点击的格子
                continue;
            int t=getTitle(x, i);
            if(t!=0)
                c[t-1]=t;
        }
        //vertical

        for(int i=0;i<9;i++){
            if(i==x)
                continue;
            int t=getTitle(i, y);
            if(t!=0)
                c[t-1]=t;
        }
        //计算在小的九宫格中有那些数字已经用过了.
        int startx=(x/3)*3;
        int starty=(y/3)*3;
        for(int i=startx;i<startx+3;i++){
            for(int j=starty;j<starty+3;j++){
                if(i==x&&j==y)
                    continue;
                int t=getTitle(i, j);
                if(t!=0)
                    c[t-1]=t;
            }
        }
        // 把c中的0给去掉
        int nused=0;
        for (int t : c) {
            if(t!=0)
                nused++;
        }
        int c1[]=new int[nused];
        nused=0;
        for (int t : c) {
            if(t!=0)
                c1[nused++]=t;
        }
        return c1;
    }
    //give a difficulty level
    private int[] getPuzzle(int diff) {//获取难度
        // TODO Auto-generated method stub
        String puz = null;
        switch (diff) {
            case DIFFICULTY_CONTINUE://-1  为之前游戏的关卡
                puz=getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE, easyPuzzle);
                break;
            case DIFFICULTY_HARD://2 根据设置的难度的id 获取选择的难度
                puz=hardPuzzle;
                break;
            case DIFFICULTY_MEDIUM://1
                puz=mediumPuzzle;
                break;
            case DIFFICULTY_EASY://0
                puz=easyPuzzle;
                break;
        }
        return fromPuzzleString(puz);//返回选择的难度
    }
    //convert an array into a puzzle string
    static private String toPuzzleString(int[] puz){
        StringBuilder buf=new StringBuilder();
        for (int element : puz) {//根据获取的难度
            buf.append(element);//将获取的难度这些字符添加到生成器的末端（进栈）
        }
        return buf.toString();
    }
    //convert a puzzle string to an array
    static protected int[] fromPuzzleString(String string) {//根据一个字符串数据,生成一个整型数组,初始化数据
        // TODO Auto-generated method stub
        int[] puz=new int[string.length()];
        for (int i = 0; i < puz.length; i++) {
            puz[i]=string.charAt(i)-'0';
        }
        return puz;
    }

    public String getTitleString(int x, int y) {//根据x轴坐标和y轴坐标得到这一单元格不可用的数据
        // TODO Auto-generated method stub
        int v=getTitle(x,y);
        if(v==0)
            return "";
        else
            return String.valueOf(v);
    }
    private int getTitle(int x, int y) {//根据九宫格当中的坐标,返回该坐标所应该填写的数字
        // TODO Auto-generated method stub
        return puzzle[y*9+x];
    }
    private void setTitle(int x,int y,int value){
        puzzle[y*9+x]=value;
    }
    //change the tile only if it's a valid move


    protected boolean setTileIfValid(int x, int y, int value) {//设置可用的数字
        // TODO Auto-generated method stub
        int tiles[]=getUsedTiles(x, y);
        if(value!=0){
            for (int tile : tiles) {
                if(tile==value)
                    return false;
            }
        }
        setTitle(x, y, value);//把用户输入的数字添加到九宫格中
        calculateUsedTiles();//更新该单元格可以使用的数字
        return true;

    }
    //open the keypad if there are any valid moves
    protected void showKeypadOrError(int x, int y) {//显示小键盘或者不显示
        // TODO Auto-generated method stub
        int tiles[]=getUsedTiles(x, y);
        if(tiles.length==9){//Toast是一个 View 视图，是一种消息模式，在应用程序上浮动显示信息给用户，它永远不会获得焦点，不影响用户的输入等操作，主要用于一些帮助/提示。
            Toast toast=Toast.makeText(this, R.string.no_moves_label, Toast.LENGTH_SHORT);//创建toast的方法，第一个表示当前的环境，第二个参数表示显示的内容，显示时长
            toast.setGravity(Gravity.BOTTOM, 5, 5);//设置该视图显示的位置，第一个参数表示显示的初始位置，第二，第三个表示偏移量
            toast.show();//显示该视图，提示前面前面有问题 需要修改
        }else{
            Log.d(TAG, "showKeypad:used="+toPuzzleString(tiles));//显示
            Dialog v=new Keypad(this,tiles,puzzleView);//调用dialog对象  显示小键盘
            v.show();
        }
    }

public void youwin(){
    int t=0;
    for (int i=0;i<81;i++){

            if (puzzle[i]!=0){
                t++;
            }
        }
    if (t==81){
        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.MenuDialog);
        builder.setTitle("通关")
                .setMessage("恭喜你 !完成")
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).show();
    }


}
}