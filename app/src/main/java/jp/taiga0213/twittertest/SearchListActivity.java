package jp.taiga0213.twittertest;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

import jp.taiga0213.service.Account;
import jp.taiga0213.service.TweetsSearch;


public class SearchListActivity extends ActionBarActivity implements SearchView.OnQueryTextListener{


    private TweetViewFetchAdapter<CompactTweetView> adapter = new TweetViewFetchAdapter<CompactTweetView>(
            SearchListActivity.this);
    private PullToRefreshListView listView;

    private TweetsSearch tweetsSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        listView = (PullToRefreshListView) findViewById(R.id.searchList);
        listView.setEmptyView(findViewById(R.id.searchProgress));

        tweetsSearch = new TweetsSearch(this,getIntent().getStringExtra("searchWord"));

        adapter = tweetsSearch.searchResult();

        listView.setAdapter(adapter);

        listView.setMode(PullToRefreshBase.Mode.BOTH);

        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //リスト引き下げ;
                new NewTweetAdd().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                //リスト引き上げ;
                new OldTweetAdd().execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_list, menu);
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

        TweetsSearch tweetsSearch = new TweetsSearch();
        tweetsSearch.executeSearch(this,query);
        finish();

        return false;
    }


    /**
     * 古い検索結果表示
     */
    private class OldTweetAdd extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            adapter = tweetsSearch.addOldSearch();
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

    /**
     * 新規検索結果表示
     */
    private class NewTweetAdd extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            adapter = tweetsSearch.addNewSearch();
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
