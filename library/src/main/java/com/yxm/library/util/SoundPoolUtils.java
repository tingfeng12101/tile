package com.yxm.library.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.yxm.library.R;

import java.util.ArrayList;

/**
 * 音效池  (0:move  1:deadstoneless)
 *
 * @author xm
 */
public class SoundPoolUtils {
    private SoundPool soundPool ;
    private ArrayList<Integer> musicIds;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SoundPoolUtils(Context context) {
        super();
//      soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build();

        musicIds = new ArrayList<>();
        addSound(soundPool.load(context, R.raw.move, 1));
        addSound(soundPool.load(context, R.raw.deadstoneless, 1));
    }

    public void addSound(int musicId){
        musicIds.add(musicId);
    }

    public void stop(int musicId){
        soundPool.stop(musicId);
    }

    public void playSound(int index){
        if(soundPool==null){
            return;
        }
        int id = soundPool.play(musicIds.get(index), 1, 1, 0, 0, 1);
    }
}