package com.example.sudoku;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class Settings  extends AppCompatActivity {
    private static final String OPT_MUSIC="music";
    private static final boolean OPT_MUSIC_DEF=true;
    private static final String OPT_HINTS="hints";
    private static final boolean OPT_HINTS_DEF=true;
   @Override
   public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
       getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragement()).commit();
  }
    public static class PrefsFragement extends PreferenceFragment {
        public void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
    //get the current music option
    public static boolean getMusic(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_MUSIC,OPT_MUSIC_DEF);
    }
    //get the current music option
    public static boolean getHints(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_HINTS,OPT_HINTS_DEF);
    }
}
