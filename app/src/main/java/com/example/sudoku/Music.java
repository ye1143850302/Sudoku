package com.example.sudoku;
import android.content.Context;
import android.media.MediaPlayer;
public class Music {
    private static MediaPlayer mp=null;
    //stop old song and start a new song
    public static void play(Context context,int resource){
        stop(context);
        if(Settings.getMusic(context)){
            mp=MediaPlayer.create(context, resource);
            mp.setLooping(true);
            mp.start();
        }
    }
    //stop the music
    public static void stop(Context context) {
        // TODO Auto-generated method stub
        if(mp!=null){
            mp.stop();
            mp.release();
            mp=null;
        }
    }
}
