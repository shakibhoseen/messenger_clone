package com.example.chatapp.Fragments;

import com.example.chatapp.Notification.MyResponse;
import com.example.chatapp.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
               "Content-Type:application/json",
                    "Authorization:key=AAAAetGyduk:APA91bG1AI6RoNySsASoE1XDv1KW9xDz2M1RSQ9wR2RTu6x1JqQimLE7wu3fNTdbg809dzYerQE-oeflFDLK846C78fr4tkoB9ySh5ABmIr1INZELLi5Qu8lNVrvJwYhSlLlt_jL1yY1"
            }
    )
    @POST("fc/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
