package com.task.newapp.utils.simplecropview;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.task.newapp.R;

public class BasicActivity extends AppCompatActivity {
    private static final String TAG = BasicActivity.class.getSimpleName();
    static Uri suri;

    public static Intent createIntent(Activity activity, Uri uri) {
        suri = uri;
        Intent intent = new Intent(activity, BasicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("uri", uri);
        return intent;
    }

    // Lifecycle Method ////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_basic);
//        Log.e("BasicActivity uri..",suri+"  ::");

        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().add(R.id.container, BasicFragment.newInstance(suri)).commit();
        }

        // apply custom font
        FontUtils.setFont(findViewById(R.id.root_layout));
        initToolbar();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void initToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar actionBar = getSupportActionBar();
//        FontUtils.setTitle(actionBar, "Basic Sample");
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
    }

    public void startResultActivity(Uri uri) {
        if (isFinishing()) return;
        // Start ResultActivity

        Intent intent = new Intent();
        intent.putExtra("uri", uri.toString());
        setResult(RESULT_OK, intent);
        finish();
//    startActivity(ResultActivity.createIntent(this, uri));
    }
}
