package com.example.finalexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
/*
Pramukh Nagendra
id: 801167475
FileName: FinalExam_Pramukh
 */


public class MainActivity extends AppCompatActivity implements SearchFragment.ISearchFragListener, SharedFragment.ISharedFragListener{

    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;
    TabLayout tabLayout;
    FirebaseAuth mAuth;
    final static public String ALBUM_KEY = "ALBUM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        mAuth = FirebaseAuth.getInstance();

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0: tab.setText("Search");
                            break;
                    case 1: tab.setText("Likes");
                            break;
                    case 2: tab.setText("History");
                            break;
                    case 3: tab.setText("Shared");
                            break;
                    default: tab.setText("Search");
                            break;
                }
            }
        }).attach();

    }

    @Override
    public void callDetailAlbumFragment(Album album) {
        Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
        intent.putExtra(ALBUM_KEY, album);
        startActivity(intent);
        finish();
    }

    @Override
    public void callDetailAlbumFromShared(Album album) {
        try {
            Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
            intent.putExtra(ALBUM_KEY, album);
            startActivity(intent);
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public class ViewPagerAdapter extends FragmentStateAdapter{

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            if(position == 0){
                return new SearchFragment();
            }else if(position == 1){
                return new LikesFragment();
            }else if(position ==2){
                return new HistoryFragment();
            }else if(position == 3){
                return new SharedFragment();
            }else{
                return new SearchFragment();
            }

        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.cancel_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logOutId) {
            mAuth.signOut();
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}