package jp.taiga0213.service;

import android.content.Context;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taiga on 2015/01/22.
 */
public class HomeTimeline {
    private long NEWEST_TWEET_ID;
    private long OLDEST_TWEET_ID;
    private static TwitterApiClient twitterApiClient;
    private static StatusesService statusesService;
    private TweetViewFetchAdapter<CompactTweetView> adapter;
    private List<Long> tweetIds = new ArrayList<Long>();
    private static Context context;
    private static final int TWEET_COUNT = 30;

    /**
     * @param context
     */
    public HomeTimeline(Context context) {
        HomeTimeline.context = context;
        NEWEST_TWEET_ID = Long.MIN_VALUE;
        OLDEST_TWEET_ID = Long.MAX_VALUE;
        adapter = new TweetViewFetchAdapter<CompactTweetView>(HomeTimeline.context);
        twitterApiClient = TwitterCore.getInstance().getApiClient();
        statusesService = twitterApiClient.getStatusesService();
    }

    /**
     * 初回タイムライン取得
     *
     * @return
     */
    public TweetViewFetchAdapter<CompactTweetView> createHomeTimeline() {
        statusesService.homeTimeline(TWEET_COUNT, null, null, false, false, false,
                false, new Callback<List<Tweet>>() {
                    @Override
                    public void failure(TwitterException exception) {
                        // TODO 自動生成されたメソッド・スタブ
                        Log.e("error", "miss");
                        exception.printStackTrace();
                    }

                    @Override
                    public void success(Result<List<Tweet>> result) {
                        // TODO 自動生成されたメソッド・スタブ
                        for (Tweet tweet : result.data) {
                            tweetIds.add(tweet.id);
                            Log.d("id", String.valueOf(tweet.id));

                            if (NEWEST_TWEET_ID < tweet.id)
                                NEWEST_TWEET_ID = tweet.id;

                            if (OLDEST_TWEET_ID > tweet.id)
                                OLDEST_TWEET_ID = tweet.id;
                        }

                        Log.d("ID:NEW", String.valueOf(NEWEST_TWEET_ID));
                        Log.d("ID:OLD", String.valueOf(OLDEST_TWEET_ID));

                        adapter.setTweetIds(tweetIds,
                                new LoadCallback<List<Tweet>>() {
                                    @Override
                                    public void success(List<Tweet> tweets) {
                                        // my custom actions
                                    }

                                    @Override
                                    public void failure(TwitterException exception) {
                                        // Toast.makeText(...).show();
                                        exception.printStackTrace();
                                    }
                                });
                    }
                });
        return adapter;
    }

    /**
     * 新規タイムライン追加
     *
     * @return
     */
    public TweetViewFetchAdapter<CompactTweetView> addNewHomeTimeline() {
        statusesService.homeTimeline(TWEET_COUNT, NEWEST_TWEET_ID, null, false, false, false,
                false, new Callback<List<Tweet>>() {

                    @Override
                    public void failure(TwitterException exception) {
                        // TODO 自動生成されたメソッド・スタブ
                        Log.e("error", "miss");
                    }

                    @Override
                    public void success(Result<List<Tweet>> result) {
                        // TODO 自動生成されたメソッド・スタブ
                        for (Tweet tweet : result.data) {
                            tweetIds.add(0, tweet.id);
                            Log.d("id", String.valueOf(tweet.id));

                            if (NEWEST_TWEET_ID < tweet.id)
                                NEWEST_TWEET_ID = tweet.id;
                        }

                        Log.d("ID:NEW", String.valueOf(NEWEST_TWEET_ID));
                        Log.d("ID:OLD", String.valueOf(OLDEST_TWEET_ID));

                        adapter.setTweetIds(tweetIds,
                                new LoadCallback<List<Tweet>>() {
                                    @Override
                                    public void success(List<Tweet> tweets) {
                                        // my custom actions
                                    }

                                    @Override
                                    public void failure(TwitterException exception) {
                                        // Toast.makeText(...).show();
                                        exception.printStackTrace();
                                    }
                                });
                        adapter.notifyDataSetChanged();
                    }
                });
        return adapter;
    }

    /**
     * 古いタイムライン追加
     *
     * @return
     */
    public TweetViewFetchAdapter<CompactTweetView> addOldHomeTimeline() {
        statusesService.homeTimeline(TWEET_COUNT, null, OLDEST_TWEET_ID - 1, false, false, false,
                false, new Callback<List<Tweet>>() {

                    @Override
                    public void failure(TwitterException exception) {
                        // TODO 自動生成されたメソッド・スタブ
                        Log.e("error", "miss");

                    }

                    @Override
                    public void success(Result<List<Tweet>> result) {
                        // TODO 自動生成されたメソッド・スタブ
                        for (Tweet tweet : result.data) {
                            tweetIds.add(tweet.id);
                            Log.d("id", String.valueOf(tweet.id));

                            if (OLDEST_TWEET_ID > tweet.id)
                                OLDEST_TWEET_ID = tweet.id;

                        }

                        Log.d("ID:NEW", String.valueOf(NEWEST_TWEET_ID));
                        Log.d("ID:OLD", String.valueOf(OLDEST_TWEET_ID));


                        adapter.setTweetIds(tweetIds,
                                new LoadCallback<List<Tweet>>() {
                                    @Override
                                    public void success(List<Tweet> tweets) {
                                        // my custom actions
                                    }

                                    @Override
                                    public void failure(TwitterException exception) {
                                        // Toast.makeText(...).show();
                                        exception.printStackTrace();
                                    }
                                });
                        adapter.notifyDataSetChanged();

                    }
                });

        return adapter;
    }

    /**
     * アダプタ初期化
     *
     * @return
     */
    public TweetViewFetchAdapter<CompactTweetView> clear() {
        adapter = new TweetViewFetchAdapter<CompactTweetView>(HomeTimeline.context);
        adapter.notifyDataSetChanged();
        return adapter;
    }
}
