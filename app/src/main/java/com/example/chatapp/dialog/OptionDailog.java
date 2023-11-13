package com.example.chatapp.dialog;

import android.app.Dialog;


import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;


public class OptionDailog extends AppCompatDialogFragment {

    private ImageView imageView;
    private String url;
    private Context context;

    public OptionDailog(Context context, String url) {
        this.url = url;
        this.context =context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.image_pop_up_dialog,null);

        builder.setView(view);

        imageView = view.findViewById(R.id.image_holder_id);

        Glide.with(context).load(url).into(imageView);
        Rect rect = new Rect();
        imageView.setMinimumWidth((int) (rect.width()*1f));

        return builder.create();
    }
}
