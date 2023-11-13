package com.example.chatapp.Adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.LastMsg;
import com.example.chatapp.Model.User;
import com.example.chatapp.dialog.OptionDailog;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isChat;
    private List<LastMsg> last_msg;
    private FragmentManager fragmentManager;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isChat, List<LastMsg> last_msg, FragmentManager fragmentManager) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isChat = isChat;
        this.last_msg = last_msg;
        this.fragmentManager = fragmentManager;
    }

    public UserAdapter(Context mContext, List<User> mUsers , boolean isChat, FragmentManager fragmentManager){
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.isChat = isChat;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_item, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final User user = mUsers.get(i);
        viewHolder.username.setText(user.getUsername());
        if(user.getImageUrl().equals("default")){
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher);
            viewHolder.seenImg.setImageResource(R.mipmap.ic_launcher);
        }else {
            Picasso.with(mContext).load(user.getImageUrl()).into(viewHolder.profile_image);
            Glide.with(mContext).load(user.getImageUrl()).into(viewHolder.seenImg);
        }

        if (isChat){
            if(user.getStatus().equals("online")){
                viewHolder.img_on.setVisibility(View.VISIBLE);
                viewHolder.img_off.setVisibility(View.GONE);
            }else{
                viewHolder.img_off.setVisibility(View.VISIBLE);
                viewHolder.img_on.setVisibility(View.GONE);
            }


        }else {
            viewHolder.img_on.setVisibility(View.GONE);
            viewHolder.img_off.setVisibility(View.GONE);

        }
        if (last_msg!=null){
            if (last_msg.get(i).getMessage()!=null){
                viewHolder.lastMsg.setVisibility(View.VISIBLE);
                viewHolder.seenImg.setVisibility(View.VISIBLE);
                viewHolder.lastMsg.setText(last_msg.get(i).getMessage());
                if (!last_msg.get(i).isIsseen()){
                  viewHolder.lastMsg.setTypeface(null, Typeface.BOLD);
                    viewHolder.seenImg.setImageResource(R.drawable.ic_baseline_check_circle_24);
                } // already set with profile image before

            }else {
                viewHolder.lastMsg.setVisibility(View.GONE);
                viewHolder.seenImg.setVisibility(View.GONE);
            }

        }else{
            viewHolder.seenImg.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,MessageActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
       public TextView username;
       public TextView lastMsg;
       public ImageView profile_image;
       public ImageView img_on;
       public ImageView img_off;
       public ImageView seenImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_image =itemView.findViewById(R.id.profile_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);
            lastMsg = itemView.findViewById(R.id.last_msg);
            seenImg = itemView.findViewById(R.id.seen_img);

            profile_image.setOnClickListener(view -> {
                if(RecyclerView.NO_POSITION != getAdapterPosition()){
                    int position = getAdapterPosition();
                    OptionDailog dialog = new OptionDailog(mContext,mUsers.get(position).getImageUrl() );

                    dialog.show(fragmentManager, "picture");
                }

            });
        }
    }
}
