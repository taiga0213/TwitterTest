package jp.taiga0213.twittertest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

import jp.taiga0213.service.Account;
import jp.taiga0213.service.HomeTimeline;


public class TweetListActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    private TweetViewFetchAdapter<CompactTweetView> adapter;


    PullToRefreshListView listView;

    HomeTimeline homeTimeline = new HomeTimeline(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_list);

        listView = (PullToRefreshListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.progress));


        adapter = new TweetViewFetchAdapter<CompactTweetView>(
                TweetListActivity.this);
        adapter = homeTimeline.createHomeTimeline();

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

        Toast.makeText(this,Twitter.getSessionManager().getActiveSession().getUserName(),Toast.LENGTH_SHORT ).show();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tweet_list, menu);
        MenuItem menuItem = menu.findItem(R.id.search_menu_search_view);
        final SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Account account = new Account();
                account.Logout(this);


                return true;
            case R.id.tweet:
                TweetComposer.Builder builder = new TweetComposer.Builder(this);

                builder.show();
                return true;

        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Intent intent = new Intent(this, SearchListActivity.class);

        intent.putExtra("searchWord", query);
        startActivity(intent);

        return false;
    }


    private class OldTweetAdd extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            adapter = homeTimeline.addOldHomeTimeline();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
            adapter = homeTimeline.addNewHomeTimeline();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listView.onRefreshComplete();//更新アニメーション終了

        }
    }

}
