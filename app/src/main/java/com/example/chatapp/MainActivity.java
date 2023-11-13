 package com.example.chatapp;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.chatapp.Fragments.ChatsFragment;
import com.example.chatapp.Fragments.ProfileFragment;
import com.example.chatapp.Fragments.UsersFragment;
import com.example.chatapp.Model.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

 public class MainActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        profile_image = findViewById(R.id.profile_image);
        username=findViewById(R.id.username);

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageUrl().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Picasso.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                     }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

         viewPagerAdapter.addFragment(new ChatsFragment(getSupportFragmentManager()),"Chats");
         viewPagerAdapter.addFragment(new UsersFragment(getSupportFragmentManager()),"Users");
         viewPagerAdapter.addFragment(new ProfileFragment(), "Profile");
         viewPager.setAdapter(viewPagerAdapter);
         tabLayout.setupWithViewPager(viewPager);
    }


     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
         return super.onCreateOptionsMenu(menu);
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this,StartActivity.class).
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK));
             finish();
        }
         return super.onOptionsItemSelected(item);
     }

     class ViewPagerAdapter extends FragmentPagerAdapter{

          private ArrayList<Fragment> fragments;
           private ArrayList<String> titles;

             public ViewPagerAdapter(FragmentManager fm) {
                 super(fm);
                 this.fragments= new ArrayList<>();
                 this.titles = new ArrayList<>();

             }

             @Override
             public Fragment getItem(int position) {
                 return fragments.get(position);
             }

             @Override
             public int getCount() {
                 return fragments.size();
             }

        public void addFragment(Fragment fragment , String title){
                 fragments.add(fragment);
                 titles.add(title);
        }

        //ctrl + o


         @Nullable
         @Override
         public CharSequence getPageTitle(int position) {
             return titles.get(position);
         }
     }


     private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

         HashMap<String, Object>map = new HashMap<>();

         map.put("status",status);
         reference.updateChildren(map);
     }

     @Override
     protected void onResume() {
         super.onResume();
         status("online");
     }

     @Override
     protected void onPause() {
         super.onPause();
         status("offline");
     }
 }
