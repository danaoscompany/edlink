package id.co.sevima.edlinkduplicate;

import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;

import id.co.sevima.edlinkduplicate.adapter.ViewPagerAdapter;
import id.co.sevima.edlinkduplicate.fragments.IntroFragment;

public class IntroActivity extends BaseActivity {
    public static IntroActivity instance;
    ViewPager vp;
    ViewPagerAdapter adapter;
    ViewPagerIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_intro);
        vp = findViewById(R.id.vp);
        indicator = findViewById(R.id.indicator);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.add(new IntroFragment(R.drawable.attendance, R.string.text2, R.string.text3), "");
        adapter.add(new IntroFragment(R.drawable.discussion, R.string.text4, R.string.text5), "");
        adapter.add(new IntroFragment(R.drawable.statistics, R.string.text6, R.string.text7), "");
        adapter.add(new IntroFragment(R.drawable.schedule, R.string.text8, R.string.text9), "");
        vp.setAdapter(adapter);
        indicator.setPageCount(adapter.getCount());
        indicator.setupWithViewPager(vp);
    }

    public void login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void signup(View view) {
        startActivity(new Intent(this, SignupActivity.class));
    }
}