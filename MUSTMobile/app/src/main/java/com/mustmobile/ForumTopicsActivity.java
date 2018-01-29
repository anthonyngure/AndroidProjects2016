package com.mustmobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.mustmobile.fragment.exchanges.PostTopicFragment;
import com.mustmobile.fragment.exchanges.ForumTopicsFragment;


public class ForumTopicsActivity extends AppCompatActivity {

    private ActionBar actionBar;
    public static String forumName;
    public static String forumCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack);
        actionBar = getSupportActionBar();
        if (getIntent().getExtras() != null){
            forumName = getIntent().getExtras().getString("stack_name");
            forumCode = getIntent().getExtras().getString("stack_code");
        }
        actionBar.setTitle(forumName);
        actionBar.setSubtitle("Topics");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_stack_fragment_container, new ForumTopicsFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_forum_topics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_activity_forum_topics_action_post_topic:
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(ForumTopicsFragment.class.getSimpleName())
                        .replace(R.id.activity_stack_fragment_container,
                                new PostTopicFragment().newInstance(),
                                PostTopicFragment.class.getSimpleName())
                        .commit();
                return true;
            case R.id.menu_activity_forum_topics_action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
            default:
                return false;
        }
    }

}
