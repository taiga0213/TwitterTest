package jp.taiga0213.twittertest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

import java.util.ArrayList;
import java.util.List;


public class TweetListActivity extends ActionBarActivity implements View.OnClickListener {

    List<Long> tweetIds = new ArrayList<Long>();

    final TweetViewFetchAdapter<CompactTweetView> adapter = new TweetViewFetchAdapter<CompactTweetView>(
            TweetListActivity.this);

    TweetViewFetchAdapter<CompactTweetView> searchAdapter;

    TwitterApiClient twitterApiClient;

    PullToRefreshListView listView;

    private static final int SEARCH_COUNT = 100;
    private static final String SEARCH_RESULT_TYPE = "popular";
    public static final int MENU_LOGOUT = 0;
    public static final int MENU_TWEET = 1;

    private static long NEWEST_TWEET_ID;
    private static long OLDEST_TWEET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_list);

        listView = (PullToRefreshListView) findViewById(R.id.listView);
        twitterApiClient = TwitterCore.getInstance().getApiClient();

        Button searchButton = (Button) findViewById(R.id.search);
        searchButton.setOnClickListener(this);

        NEWEST_TWEET_ID = Long.MIN_VALUE;
        OLDEST_TWEET_ID = Long.MAX_VALUE;


        StatusesService statusesService = twitterApiClient.getStatusesService();

        statusesService.homeTimeline(10, null, null, false, false, false,
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
        listView.setAdapter(adapter);

        listView.setMode(PullToRefreshBase.Mode.BOTH);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //引き下げ(上);
                new NewTweetAdd().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //引き上げ(下)
                new OldTweetAdd().execute();
            }
        });

    }

    @Override
    public void onClick(View v) {
        // TODO 自動生成されたメソッド・スタブ
        switch (v.getId()) {
            case R.id.search:

                searchAdapter = new TweetViewFetchAdapter<CompactTweetView>(
                        TweetListActivity.this);

                Toast.makeText(TweetListActivity.this, "search", Toast.LENGTH_SHORT)
                        .show();

                EditText searchWord = (EditText) findViewById(R.id.searchWord);
                String word = searchWord.getText().toString();

                final SearchService service = twitterApiClient.getSearchService();

                service.tweets(word, null, null, null, SEARCH_RESULT_TYPE,
                        SEARCH_COUNT, null, null, null, true,
                        new Callback<Search>() {
                            @Override
                            public void success(Result<Search> searchResult) {

                                final List<Tweet> tweets = searchResult.data.tweets;
                                searchAdapter.getTweets().addAll(tweets);

                                for (Tweet tweet : tweets) {
                                    Log.d("test", tweet.text);
                                }

                                listView.setAdapter(searchAdapter);
                                Toast.makeText(TweetListActivity.this, "OK",
                                        Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void failure(TwitterException error) {

                                Toast.makeText(TweetListActivity.this, "error",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
                break;

            default:
                break;
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(0, MENU_TWEET, 0, "Tweet")
                .setIcon(android.R.drawable.ic_menu_share)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, MENU_LOGOUT, 0, "Logout")
                .setIcon(android.R.drawable.ic_menu_revert)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_LOGOUT:
                Twitter.getSessionManager().clearActiveSession();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                finish();
                return true;
            case MENU_TWEET:
                TweetComposer.Builder builder = new TweetComposer.Builder(this);

                builder.show();
                return true;

        }
        return false;
    }


    private class OldTweetAdd extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            StatusesService statusesService = twitterApiClient.getStatusesService();

            statusesService.homeTimeline(20, null, OLDEST_TWEET_ID + 1, false, false, false,
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

                        }
                    });
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listView.onRefreshComplete();//更新アニメーション終了
        }
    }

    private class NewTweetAdd extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            StatusesService statusesService = twitterApiClient.getStatusesService();

            statusesService.homeTimeline(100, NEWEST_TWEET_ID, null, false, false, false,
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
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listView.onRefreshComplete();//更新アニメーション終了
        }
    }
}
