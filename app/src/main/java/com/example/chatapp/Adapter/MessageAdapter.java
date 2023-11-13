package com.example.chatapp.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.chatapp.Model.DateUtils;
import com.example.chatapp.R;
import com.example.chatapp.dialog.OptionDailog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.chatapp.Model.Chat;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context mcontext;
    private List<Chat> mchat;
    private String imgUrl;
    FirebaseUser firebaseUser;
    private DateUtils dateUtils;
    private String firebaseUId = FirebaseAuth.getInstance().getUid();
    FragmentManager fragmentManager;
    public MessageAdapter(Context mcontext, List<Chat> chats, String imgUrl, FragmentManager fragmentManager) {

        this.mcontext = mcontext;
        this.mchat = chats;
        this.imgUrl = imgUrl;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser.getUid().equals(mchat.get(position).getSender())) {
            return MSG_TYPE_RIGHT;
        } else
            return MSG_TYPE_LEFT;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Chat chat = mchat.get(i);

        viewHolder.seenImage.setVisibility(View.GONE);



        int n = mchat.size();
        String click = null;
        if (i != 0) {
            Chat beChat = mchat.get(i - 1);

            String disBeMsg = ShowTimeOrNot(beChat.getPublish(), chat.getPublish());
            click = disBeMsg;

            if (disBeMsg.equals("tny")) {

                viewHolder.recent_time.setText(TimeFormat(chat.getPublish()));
                viewHolder.recent_time.setVisibility(View.GONE);
                viewHolder.past_time.setVisibility(View.GONE);

                if (i < n - 1) {

                    Chat afChat = mchat.get(i + 1);
                    String disAfMsg = ShowTimeOrNot(chat.getPublish(), afChat.getPublish());
                    if (afChat.getSender().equals(chat.getSender()) && !disAfMsg.equals("big")) {     // matching two text sender is same
                        viewHolder.profile_image.setVisibility(View.INVISIBLE);

                    } else {
                        viewHolder.profile_image.setVisibility(View.VISIBLE);
                    }

                } else {
                    viewHolder.profile_image.setVisibility(View.VISIBLE);
                }

               /* if (beChat.getSender().equals(chat.getSender())) {
                    viewHolder.sender_name.setVisibility(View.GONE);
                } else {
                    viewHolder.sender_name.setVisibility(View.VISIBLE);
                }*/

            } else if (disBeMsg.equals("mid")) {
                viewHolder.recent_time.setText(TimeFormat(chat.getPublish()));
                viewHolder.recent_time.setVisibility(View.VISIBLE);
                viewHolder.past_time.setVisibility(View.GONE);
                if (i < n - 1) {
                    Chat afChat = mchat.get(i + 1);
                    String disAfMsg = ShowTimeOrNot(chat.getPublish(), afChat.getPublish());
                    if (afChat.getSender().equals(chat.getSender()) && !disAfMsg.equals("big")) {     // matching two text sender is same
                        viewHolder.profile_image.setVisibility(View.INVISIBLE);

                    } else {
                        viewHolder.profile_image.setVisibility(View.VISIBLE);
                    }

                } else {
                    viewHolder.profile_image.setVisibility(View.VISIBLE);
                }

                /*if (beChat.getSender().equals(chat.getSender())) {
                    viewHolder.sender_name.setVisibility(View.GONE);
                } else {
                    viewHolder.sender_name.setVisibility(View.VISIBLE);
                }*/


            } else {

                //viewHolder.sender_name.setVisibility(View.VISIBLE);
                viewHolder.past_time.setVisibility(View.VISIBLE);
                viewHolder.recent_time.setVisibility(View.GONE);
                viewHolder.past_time.setText(TimeFormat(chat.getPublish()));
                viewHolder.profile_image.setVisibility(View.VISIBLE);

                if (i < n - 1) {
                    Chat afChat = mchat.get(i + 1);
                    String disAfMsg = ShowTimeOrNot(chat.getPublish(), afChat.getPublish());
                    if (afChat.getSender().equals(chat.getSender()) && !disAfMsg.equals("big")) {     // matching two text sender is same
                        viewHolder.profile_image.setVisibility(View.INVISIBLE);
                    }
                }

            }
            //  set one pic and one title for one man


        } else {
            //viewHolder.sender_name.setVisibility(View.VISIBLE);
            viewHolder.past_time.setVisibility(View.VISIBLE);
            viewHolder.recent_time.setVisibility(View.GONE);
            viewHolder.past_time.setText(TimeFormat(chat.getPublish()));
            viewHolder.profile_image.setVisibility(View.VISIBLE);
            if (i < n - 1) {
                Chat afChat = mchat.get(i + 1);
                if (afChat.getSender().equals(chat.getSender())) {
                    viewHolder.profile_image.setVisibility(View.INVISIBLE);
                }
            }

        }


        if (!chat.getMessage().equals("")) {
            viewHolder.show_message.setText(chat.getMessage());
            viewHolder.show_message.setVisibility(View.VISIBLE);
        }else viewHolder.show_message.setVisibility(View.GONE);

        if (chat.getImageUrl() != null) {
            viewHolder.show_imgMsg.setVisibility(View.VISIBLE);
            Picasso.with(mcontext).load(chat.getImageUrl()).into(viewHolder.show_imgMsg);

        }else viewHolder.show_imgMsg.setVisibility(View.GONE);

        if (imgUrl.equals("default")) {
            viewHolder.profile_image.setImageResource(R.mipmap.ic_launcher);
        } else Picasso.with(mcontext).load(imgUrl).into(viewHolder.profile_image);


        if (i == mchat.size() - 1) {
            if (chat.getIsseen()) {
                viewHolder.txt_seen.setText("seen");
            } else {
                viewHolder.txt_seen.setText("Delivered");
            }
        } else {
            viewHolder.txt_seen.setVisibility(View.GONE);
        }


        if (i < n - 1) {
            if (chat.getIsseen()) {
                // if its already seen
                if (mchat.get(i + 1).getIsseen()) {
                    viewHolder.seenImage.setVisibility(View.INVISIBLE);

                } else {
                    viewHolder.seenImage.setVisibility(View.VISIBLE);

                    if (!imgUrl.equals("default"))
                        Glide.with(mcontext).load(imgUrl).centerCrop().into(viewHolder.seenImage);
                    else viewHolder.seenImage.setImageResource(R.drawable.button_diabled);
                }

            } else {
                // if its not seen just deliver only
                viewHolder.seenImage.setImageResource(R.drawable.ic_baseline_check_circle_24);
                viewHolder.seenImage.setVisibility(View.VISIBLE);

            }

        } else { //last item so no need to compare
            if (chat.getIsseen()) {
                // if its already seen
                viewHolder.seenImage.setVisibility(View.VISIBLE);

                if (!imgUrl.equals("default"))
                    Glide.with(mcontext).load(imgUrl).centerCrop().into(viewHolder.seenImage);
                else viewHolder.seenImage.setImageResource(R.drawable.button_diabled);
            }else{
                //if its not seen just deliver only
                viewHolder.seenImage.setImageResource(R.drawable.ic_baseline_check_circle_24);
                viewHolder.seenImage.setVisibility(View.VISIBLE);

            }
        }


        // for owner of the inbox profile invisible
        if (chat.getSender().equals(firebaseUId)) {
            viewHolder.profile_image.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mchat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message, past_time, recent_time;
        public TextView txt_seen;
        public ImageView profile_image;
        public ImageView show_imgMsg, seenImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            past_time = itemView.findViewById(R.id.past_time_id);
            recent_time = itemView.findViewById(R.id.recent_time_id);
            profile_image = itemView.findViewById(R.id.profile_image);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            show_imgMsg = itemView.findViewById(R.id.show_imgMsg);
            seenImage = itemView.findViewById(R.id.seen_img);


            show_imgMsg.setOnClickListener(view -> {
                if(RecyclerView.NO_POSITION != getAdapterPosition()){
                    int position = getAdapterPosition();
                    OptionDailog dialog = new OptionDailog(mcontext,mchat.get(position).getImageUrl() );
                    dialog.show(fragmentManager, "picture");
                }
            });
        }
    }


    public String TimeFormat(long times) {
        dateUtils = new DateUtils();
        Calendar calendar = Calendar.getInstance();
        long curentmilis = calendar.getTimeInMillis();
        long h = Math.abs(curentmilis - times);

        if (h < 604800000) {  //use to update 7 days
            if (h < 86400000) {//convert hour \\if 24hour less

                return dateUtils.dayIsYesterday(times);
            } else {
                return dateUtils.WeekFromLong(times);
            }
        } else {  //greatter 7 days
            return dateUtils.dateFromLong(times);
        }

    }


    public String ShowTimeOrNot(long before, long after) {
        dateUtils = new DateUtils();
        Calendar calendar = Calendar.getInstance();
        long curentmilis = calendar.getTimeInMillis();
        long h = Math.abs(before - after);

        //use to update 7 days
        if (h < 3600000) {//convert hour

            if (h < 600000) {//convert hour 10 minute

                return "tny"; //tiny
            } else {
                return "mid";
            }

        } else {
            return "big";
        }


    }

}
