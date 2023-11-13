package com.example.chatapp.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.LastMsg;
import com.example.chatapp.Model.User;
import com.example.chatapp.Notification.Token;


import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<User> mUser,sUser;
    private List<LastMsg> muserlist = new ArrayList<>();
    private List<LastMsg> cuserlist = new ArrayList<>();
    private FirebaseUser firebaseUser;
    private UserAdapter userAdapter;
    DatabaseReference reference;
    FragmentManager fragmentManager;

    public ChatsFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView =view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
           reference.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   mUser.clear();
                   for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                       User user = snapshot.getValue(User.class);
                        mUser.add(user);
                   }
                   readUser();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {

               }
           });

        // updateTokens(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

    public  void readUser(){

        reference = FirebaseDatabase.getInstance().getReference("Chats");
         sUser = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sUser.clear();
                cuserlist.clear();
                muserlist.clear();

                boolean x = true;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                   if(chat.getReceiver().equals(firebaseUser.getUid())){
                       String msg ;
                       if(chat.getMessage()!=null){
                           msg = chat.getMessage();
                       }else{
                           msg = "sent photo";
                       }
                       LastMsg lastMsg = new LastMsg(chat.getSender(),msg,chat.getIsseen());

                       cuserlist.add(lastMsg);
                   }else if(chat.getSender().equals(firebaseUser.getUid())){
                       String msg ;
                       if(chat.getMessage()!=null){
                           msg = chat.getMessage();
                       }else{
                           msg = "sent photo";
                       }
                       LastMsg lastMsg = new LastMsg(chat.getReceiver(),"you: "+msg ,chat.getIsseen());

                       cuserlist.add(lastMsg);
                   }

                }
                   for (int y =cuserlist.size();y>=1;y--)  /// reverse
                   {
                    muserlist.add(cuserlist.get(y-1));
                   }

                   cuserlist.clear();
                for (LastMsg id: muserlist){
                    for (User user :mUser){
                       if( user.getId().equals(id.getId())){
                           if (sUser.size()!=0){
                               for (User user1:sUser){
                                   if (user1.getId().equals(user.getId())){
                                       x=false;
                                       break;
                                   }else{
                                       x=true;
                                   }
                               }if (x==true){
                                   sUser.add(user);
                                   cuserlist.add(id);
                                   //break;
                               }

                           }
                           else{
                           sUser.add(user);
                           cuserlist.add(id);
                           }
                       }
                    }

                }


               userAdapter = new UserAdapter(getContext(),sUser , true, cuserlist, fragmentManager);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateTokens(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token();
        reference.child(firebaseUser.getUid()).setValue(token1);
    }
}
