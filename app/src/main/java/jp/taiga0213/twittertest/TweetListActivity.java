package jp.taiga0213.twittertest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

import java.util.ArrayList;
import java.util.List;


public class TweetListActivity extends ActionBarActivity implements View.OnClickListener {

    List<Long> tweetIds = new ArrayList<Long>();

    final TweetViewFetchAdapter<CompactTweetView> adapter = new TweetViewFetchAdapter<CompactTweetView>(
            TweetListActivity.this);

    TweetViewFetchAdapter<CompactTweetView> searchAdapter;

    TwitterApiClient twitterApiClient;

    ListView listView;

    private static final int SEARCH_COUNT = 100;
    private static final String SEARCH_RESULT_TYPE = "popular";
    public static final int MENU_LOGOUT = 0;
    public static final int MENU_TWEET = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_list);

        listView = (ListView) findViewById(R.id.listView);
        twitterApiClient = TwitterCore.getInstance().getApiClient();

        Button searchButton = (Button) findViewById(R.id.search);
        searchButton.setOnClickListener(this);




        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView tweetList = (ListView) parent;
                Tweet tweet = (Tweet) tweetList.getItemAtPosition(position);

                Intent intent = new Intent(getApplicationContext(), SingleTweetActivity.class);
                intent.putExtra("id", tweet.id);
                startActivity(intent);

                return false;
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
                return true;
            case MENU_TWEET:
                TweetComposer.Builder builder = new TweetComposer.Builder(this);

                builder.show();
                return true;

        }
        return false;
    }
}
