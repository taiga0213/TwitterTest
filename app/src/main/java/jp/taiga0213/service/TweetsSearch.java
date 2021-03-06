package jp.taiga0213.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

import java.util.ArrayList;
import java.util.List;

import jp.taiga0213.twittertest.SearchListActivity;

/**
 * Created by feapar on 2015/01/22.
 */
public class TweetsSearch {
    private long NEWEST_TWEET_ID;
    private long OLDEST_TWEET_ID;
    private static TwitterApiClient twitterApiClient;
    private static SearchService searchService;
    private TweetViewFetchAdapter<CompactTweetView> adapter;
    private List<Long> tweetIds = new ArrayList<Long>();
    private static Context context;
    private static final int SEARCH_COUNT = 30;
    private static final String SEARCH_RESULT_TYPE = "mixed, recent, popular";
    private static String searchWord;

    public TweetsSearch(){

    }

    public TweetsSearch(Context context, String searchWord) {
        TweetsSearch.context = context;
        TweetsSearch.searchWord = searchWord;
        NEWEST_TWEET_ID = Long.MIN_VALUE;
        OLDEST_TWEET_ID = Long.MAX_VALUE;
        adapter = new TweetViewFetchAdapter<CompactTweetView>(TweetsSearch.context);
        twitterApiClient = TwitterCore.getInstance().getApiClient();
        searchService = twitterApiClient.getSearchService();
    }

    /**
     * ツイート検索
     *
     * @return
     */
    public TweetViewFetchAdapter<CompactTweetView> searchResult() {
        searchService.tweets(TweetsSearch.searchWord, null, null, null, SEARCH_RESULT_TYPE,
                SEARCH_COUNT, null, null, null, true,
                new Callback<Search>() {
                    @Override
                    public void success(Result<Search> searchResult) {

                        for (Tweet tweet : searchResult.data.tweets) {

                            tweetIds.add(tweet.id);

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

                    @Override
                    public void failure(TwitterException error) {

                        Toast.makeText(context, "error",
                                Toast.LENGTH_SHORT).show();

                    }
                });

        return adapter;
    }

    /**
     * 検索結果(新規)追加
     *
     * @return
     */
    public TweetViewFetchAdapter<CompactTweetView> addNewSearch() {
        searchService.tweets(TweetsSearch.searchWord, null, null, null, SEARCH_RESULT_TYPE,
                SEARCH_COUNT, null, NEWEST_TWEET_ID, null, true,
                new Callback<Search>() {
                    @Override
                    public void success(Result<Search> searchResult) {


                        for (Tweet tweet : searchResult.data.tweets) {
                            tweetIds.add(0, tweet.id);
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

                    }

                    @Override
                    public void failure(TwitterException error) {

                        Toast.makeText(context, "error",
                                Toast.LENGTH_SHORT).show();

                    }
                });
        return adapter;
    }

    /**
     * 検索結果(古)追加
     *
     * @return
     */
    public TweetViewFetchAdapter<CompactTweetView> addOldSearch() {
        searchService.tweets(TweetsSearch.searchWord, null, null, null, SEARCH_RESULT_TYPE,
                SEARCH_COUNT, null, null, OLDEST_TWEET_ID - 1, true,
                new Callback<Search>() {
                    @Override
                    public void success(Result<Search> searchResult) {

                        for (Tweet tweet : searchResult.data.tweets) {
                            tweetIds.add(tweet.id);

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

                    @Override
                    public void failure(TwitterException error) {

                        Toast.makeText(context, "error",
                                Toast.LENGTH_SHORT).show();

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
        adapter = new TweetViewFetchAdapter<CompactTweetView>(TweetsSearch.context);
        adapter.notifyDataSetChanged();
        return adapter;
    }

    /**
     * 検索画面移動
     *
     * @param activity
     * @param query
     */
    public void executeSearch(Activity activity,String query){
        Intent intent = new Intent(activity, SearchListActivity.class);
        intent.putExtra("searchWord", query);
        activity.startActivity(intent);
    }
}
