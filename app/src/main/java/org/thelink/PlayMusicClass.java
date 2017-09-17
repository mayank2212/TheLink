package org.thelink;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Mayank on 22-02-2017.
 */
public class PlayMusicClass  {
    Context mcontext;

    public PlayMusicClass(Context context){
        mcontext = context;
    }

    public void playMusic(String play_url){

        Uri myUri = Uri.parse(play_url);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(myUri, "audio/*");
        mcontext.startActivity(intent);
    }

}
