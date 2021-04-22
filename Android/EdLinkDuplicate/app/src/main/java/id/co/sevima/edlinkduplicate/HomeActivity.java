package id.co.sevima.edlinkduplicate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.util.ArrayList;

import id.co.sevima.edlinkduplicate.adapter.PostAdapter;
import id.co.sevima.edlinkduplicate.adapter.ViewPagerAdapter;
import id.co.sevima.edlinkduplicate.fragments.EmptyFragment;
import id.co.sevima.edlinkduplicate.fragments.PostFragment;
import id.co.sevima.edlinkduplicate.fragments.UserFragment;

public class HomeActivity extends BaseActivity {
    ViewPager vp;
    ViewPagerAdapter adapter;
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        vp = findViewById(R.id.vp);
        tabs = findViewById(R.id.tabs);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.add(new PostFragment(), "");
        adapter.add(new EmptyFragment(), "");
        adapter.add(new EmptyFragment(), "");
        adapter.add(new EmptyFragment(), "");
        adapter.add(new UserFragment(), "");
        vp.setAdapter(adapter);
        tabs.setupWithViewPager(vp);
        tabs.getTabAt(0).setIcon(R.drawable.home);
        tabs.getTabAt(1).setIcon(R.drawable.community);
        tabs.getTabAt(2).setIcon(R.drawable.message);
        tabs.getTabAt(3).setIcon(R.drawable.rocket);
        tabs.getTabAt(4).setIcon(R.drawable.user);
    }
}