package com.nguyencse.todo.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nguyencse.todo.custom.ButtonCSE;
import com.nguyencse.todo.custom.TextViewCSE;
import com.nguyencse.todo.main.MainActivity;
import com.nguyencse.todo.R;
import com.nguyencse.todo.objects.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Putin on 5/5/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;
    private DatabaseReference database;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        database = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final User user = userList.get(position);

        holder.txtName.setText(user.getUsername());
        holder.txtEmail.setText(user.getEmail());

        if (user.getId().equals(MainActivity.user.getUid())) {
            holder.btnOptions.setVisibility(View.INVISIBLE);
        } else {
            database.child("friends").child(MainActivity.user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user.getId())) {
                        holder.btnOptions.setVisibility(View.INVISIBLE);
                    } else {
                        holder.btnOptions.setVisibility(View.VISIBLE);
                        holder.btnOptions.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_friend_blue));

                        holder.btnOptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.custom_layout_modal_add_friend);
                                dialog.show();

                                ButtonCSE btnYes = (ButtonCSE) dialog.findViewById(R.id.btn_item_modal_add_friend_yes);
                                ButtonCSE btnNo = (ButtonCSE) dialog.findViewById(R.id.btn_item_modal_add_friend_no);

                                btnYes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addFriend(user);
                                        Toast.makeText(context, context.getString(R.string.friend_added), Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                        holder.btnOptions.setVisibility(View.INVISIBLE);
                                    }
                                });

                                btnNo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.cancel();
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        holder.lnlItemUser.setOnClickListener(new View.OnClickListener() {
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

                txtUsername.setText(user.getUsername());
                txtEmail.setText(user.getEmail());
            }
        });
    }

    private void addFriend(User friend) {
        Map<String, Object> newFriendMap = friend.toMap();
        Map<String, Object> childUpdate = new HashMap<>();
        childUpdate.put("friends/" + MainActivity.user.getUid() + "/" + friend.getId(), newFriendMap);
        database.updateChildren(childUpdate);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextViewCSE txtName;
        public TextViewCSE txtEmail;
        public CircleImageView avatar;
        public ImageButton btnOptions;
        public LinearLayout lnlItemUser;

        public ViewHolder(final View itemView) {
            super(itemView);
            txtName = (TextViewCSE) itemView.findViewById(R.id.txt_item_user_name);
            txtEmail = (TextViewCSE) itemView.findViewById(R.id.txt_item_user_email);
            avatar = (CircleImageView) itemView.findViewById(R.id.img_item_user_avatar);
            btnOptions = (ImageButton) itemView.findViewById(R.id.btn_item_user_options);
            lnlItemUser = (LinearLayout) itemView.findViewById(R.id.lnl_item_user);
        }
    }
}