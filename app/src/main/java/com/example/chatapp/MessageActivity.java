package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Fragments.APIService;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.Notification.Client;
import com.example.chatapp.Notification.Data;
import com.example.chatapp.Notification.MyResponse;
import com.example.chatapp.Notification.Sender;
import com.example.chatapp.Notification.Token;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private ImageView select_imageview;
    private static final int REQUEST_IMAGE = 99;
    private TextInputEditText text_sent;
    private RelativeLayout imageWrapperRel;
    private TextView username;
    private ImageButton btn_sent, image_choser, imageRemover;
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference, seenRef, readRef;
    private List<Chat> mchat;
    MessageAdapter messageAdapter;
    Uri imageUri;
    StorageReference storageReference;
    StorageTask uploadtask;
    APIService apiService;
    String userid;
    boolean notify = false, isSending;

    Intent intent;
    ValueEventListener seenListener, readListener;

    SharedPreferences preferences ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
               stopCollectData();
            }
        });
        preferences = getSharedPreferences("virus_recognition", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        text_sent = findViewById(R.id.text_sent);
        btn_sent = findViewById(R.id.btn_sent);
        image_choser = findViewById(R.id.btn_camera);
        select_imageview = findViewById(R.id.image_holder_id);
        imageWrapperRel = findViewById(R.id.image_holder_relative_id);
        imageRemover = findViewById(R.id.image_remover_id);

        intent = getIntent();

        final String userid = intent.getStringExtra("userid");
        editor.putString("virus", userid );
        editor.apply();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("chatimages");

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                if (user.getImageUrl().equals("default")) {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                } else
                    Picasso.with(getApplicationContext()).load(user.getImageUrl()).into(profile_image);
                //Glide.with(MessageActivity.this).load(user.getImageUrl()).into(profile_image);

                readMessage(firebaseUser.getUid(), userid, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        image_choser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
            }
        });


        btn_sent.setOnClickListener(view -> {
            notify = true;
            isSending =true;
            btn_sent.setEnabled(false);
            repeatAnim();
            String message = text_sent.getText().toString();
            if (!message.equals("") || imageUri != null) {
                sendMessage(firebaseUser.getUid(), userid, message, imageUri);
            } else{
                Toast.makeText(MessageActivity.this, "you cant sent empty message", Toast.LENGTH_SHORT).show();
                isSending = false;
                btn_sent.setEnabled(true);
            }

        });

        imageRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUri=null;
                imageWrapperRel.setVisibility(View.GONE);
            }
        });

        //seenMessage(userid);
    }


    public void readMessage(final String sender, final String receiver, final String imgUrl) {

         readRef = FirebaseDatabase.getInstance().getReference("Chats");
        mchat = new ArrayList<>();

        readListener = readRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getSender().equals(sender) && chat.getReceiver().equals(receiver) || chat.getSender().equals(receiver) && chat.getReceiver().equals(sender)) {
                        chat.setChatId(snapshot.getKey());
                        mchat.add(chat);
                    }
                }

                messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imgUrl, getSupportFragmentManager());
                recyclerView.setAdapter(messageAdapter);

               if(receiver.equals(preferences.getString("virus", "t")))
                 modifySeenMsg(sender);  // firebaseUserId
                else{
                   //Toast.makeText(MessageActivity.this, "dont match", Toast.LENGTH_SHORT).show();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> map = new HashMap<>();

        map.put("status", status);
        reference.updateChildren(map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");

        if(readRef!=null && readListener!=null){
            readRef.addValueEventListener(readListener);
        }
        //seenRef.addValueEventListener(seenListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //seenRef.removeEventListener(seenListener);
        status("offline");
        if(readRef!=null && readListener!=null){
            readRef.removeEventListener(readListener);

        }
    }


    private void modifySeenMsg(String sender){
        if(sender==null || mchat.size()==0){
            return;
        }

        for(Chat chat: mchat){
            if (!chat.getIsseen()){
                if(sender.equals(chat.getReceiver())){
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("isseen", true);
                    FirebaseDatabase.getInstance().getReference("Chats").child(chat.getChatId()).updateChildren(map);
                }

            }
        }

    }


    private void seenMessage(final String userid) {

        seenRef = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = seenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;
                    if (firebaseUser.getUid().equals(chat.getReceiver()) && userid.equals(chat.getSender()) && !chat.getIsseen()) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isseen", true);
                        snapshot.getRef().updateChildren(map);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = MessageActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadtask != null) {

            } else {
                select_imageview.setImageURI(imageUri);
                select_imageview.setVisibility(View.VISIBLE);
                imageWrapperRel.setVisibility(View.VISIBLE);
                // uploadFile();
                // Toast.makeText(getContext(), "unable to open file", Toast.LENGTH_SHORT).show();
            }
        }
    }
    

    public void sendMessage(final String sender, final String receiver, final String message, Uri uri) {
        final ProgressDialog pd = new ProgressDialog(MessageActivity.this);
        pd.setMessage("sending");
        btn_sent.setClickable(false);
        pd.show();
        if (uri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(uri));
            uploadtask = fileReference.putFile(uri);
            uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = (Uri) task.getResult();
                        String mUri = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender", sender);
                        hashMap.put("receiver", receiver);
                        hashMap.put("message", message);
                        hashMap.put("imageUrl", mUri);
                        hashMap.put("isseen", false);
                        hashMap.put("publish", new Date().getTime());
                        reference.child("Chats").push().setValue(hashMap);
                        text_sent.setText("");
                        btn_sent.setClickable(true);
                        imageUri = null;
                        select_imageview.setVisibility(View.GONE);
                        imageWrapperRel.setVisibility(View.GONE);
                        select_imageview.setImageURI(imageUri);
                        pd.dismiss();

                    } else {
                        Toast.makeText(MessageActivity.this, "failed to success", Toast.LENGTH_SHORT).show();
                        btn_sent.setClickable(true);
                        pd.dismiss();
                    }
                    isSending =  false;
                    btn_sent.setEnabled(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    btn_sent.setEnabled(true);
                    btn_sent.setClickable(true);
                    pd.dismiss();
                    isSending =  false;
                }
            });
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", message);
            hashMap.put("publish", new Date().getTime());
            hashMap.put("isseen", false);
            reference.child("Chats").push().setValue(hashMap);
            text_sent.setText("");
            btn_sent.setClickable(true);
            btn_sent.setEnabled(true);
            pd.dismiss();
            isSending =  false;
        }

        /*final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (notify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
    }

    private void sendNotification(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "New Message",
                            userid);

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if (response.code() == 200) {
                                        if (response.body().success != 1) {
                                            Toast.makeText(MessageActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void repeatAnim() {
        if (!isSending)
            return;

        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.left_right_sending_animation);


        topAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                repeatAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        btn_sent.startAnimation(topAnim);


    }

    @Override
    public void onBackPressed() {
        stopCollectData();
        super.onBackPressed();
    }

    private void stopCollectData(){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("virus", "t" );
        editor.apply();
    }

}
