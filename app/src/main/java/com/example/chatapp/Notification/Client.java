package com.example.chatapp.Notification;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private  static Retrofit retrofit = null;

    public  static Retrofit getClient(String ur1){

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(ur1)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
