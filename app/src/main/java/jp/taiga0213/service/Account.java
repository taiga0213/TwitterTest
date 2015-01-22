package jp.taiga0213.service;

import android.app.Activity;
import android.content.Intent;

import com.twitter.sdk.android.Twitter;

import jp.taiga0213.twittertest.MainActivity;

/**
 * Created by feapar on 2015/01/23.
 */
public class Account {
    public void Logout(Activity activity){
        Twitter.getSessionManager().clearActiveSession();

        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
