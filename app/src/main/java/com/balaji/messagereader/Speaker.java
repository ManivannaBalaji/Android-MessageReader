package com.balaji.messagereader;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

public class Speaker extends Service implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean ready = false;
    private boolean allowed = false;
    public Speaker() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Speaker(Context context){
        tts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            tts.setLanguage(Locale.getDefault());
            ready = true;
        }else{
            ready = false;
        }
    }

    public void speak(String text){
        HashMap<String, String> hash = new HashMap<String,String>();
        hash.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    public void pause(int duration){
        tts.playSilence(duration, TextToSpeech.QUEUE_ADD, null);
    }
    public void destroy(){
        tts.shutdown();
    }
}
