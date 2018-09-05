package co.technisupport.chat4saggic;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

/**
 * Created by manuelthomas on 2/8/18.
 */

public class ChatApplication extends Application {

    @Override
    public void onCreate() {
        EmojiManager.install(new IosEmojiProvider());
        Fresco.initialize(this);
        super.onCreate();
    }

    public static void startChat(Context context){
        Intent chatIntent = new Intent(context,ChatActivity.class);
        context.startActivity(chatIntent);

    }
}
