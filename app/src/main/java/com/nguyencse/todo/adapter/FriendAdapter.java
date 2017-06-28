package com.nguyencse.todo.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.nguyencse.todo.R;
import com.nguyencse.todo.chatting.PairedRoomActivity;
import com.nguyencse.todo.custom.TextViewCSE;
import com.nguyencse.todo.general.CommonField;
import com.nguyencse.todo.main.MainActivity;
import com.nguyencse.todo.objects.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Putin on 5/5/2017.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private Context context;
    private List<User> friendList;
    private DatabaseReference database;

    public FriendAdapter(Context context, List<User> friendList) {
        this.context = context;
        this.friendList = friendList;
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User friend = friendList.get(position);
        holder.txtUsername.setText(friend.getUsername());
        holder.txtEmail.setText(friend.getEmail());
        holder.lnlItemFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_layout_modal_profile);
                dialog.show();

                Window window = dialog.getWindow();
                if (window != null) {
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }

                TextViewCSE txtUsername = (TextViewCSE) dialog.findViewById(R.id.txt_item_modal_user_name);
                TextViewCSE txtEmail = (TextViewCSE) dialog.findViewById(R.id.txt_item_modal_user_email);
//                        CircleImageView imgAvatar = (CircleImageView) dialog.findViewById(R.id.img_item_modal_user_avatar);

                txtUsername.setText(friend.getUsername());
                txtEmail.setText(friend.getEmail());
            }
        });
        holder.btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_layout_modal_profile_options);
                dialog.show();

                Window window = dialog.getWindow();
                if (window != null) {
                    window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }

                TextViewCSE txtAddFriend = (TextViewCSE) dialog.findViewById(R.id.txt_item_modal_user_add_friend);
                TextViewCSE txtUnfriend = (TextViewCSE) dialog.findViewById(R.id.txt_item_modal_user_unfriend);
                TextViewCSE txtSendMessage = (TextViewCSE) dialog.findViewById(R.id.txt_item_modal_user_message);

                txtAddFriend.setVisibility(View.GONE);

                txtUnfriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        database.child("friends").child(MainActivity.user.getUid()).child(friend.getId()).removeValue();
                        Toast.makeText(context, context.getString(R.string.unfriended), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                txtSendMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPreferences sprfToDo = context.getSharedPreferences(CommonField.SHAREDPREFERENCES, Context.MODE_PRIVATE);
                        sprfToDo.edit().putString(CommonField.FRIEND_FROM, new Gson().toJson(friend)).apply();
                        context.startActivity(new Intent(context, PairedRoomActivity.class));
                        dialog.dismiss();
                    }
                });
            }
        });


        //TODO: set avatar for user
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout lnlItemFriend;
        public TextViewCSE txtUsername;
        public TextViewCSE txtEmail;
        public CircleImageView imgAvatar;
        public ImageButton btnOptions;

        public ViewHolder(View itemView) {
            super(itemView);

            lnlItemFriend = (LinearLayout) itemView.findViewById(R.id.lnl_item_user);
            txtUsername = (TextViewCSE) itemView.findViewById(R.id.txt_item_user_name);
            txtEmail = (TextViewCSE) itemView.findViewById(R.id.txt_item_user_email);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.img_item_user_avatar);
            btnOptions = (ImageButton) itemView.findViewById(R.id.btn_item_user_options);
        }
    }
}
