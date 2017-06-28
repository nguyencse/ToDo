package com.nguyencse.todo.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nguyencse.todo.adapter.FriendAdapter;
import com.nguyencse.todo.adapter.UserAdapter;
import com.nguyencse.todo.custom.TextViewCSE;
import com.nguyencse.todo.main.MainActivity;
import com.nguyencse.todo.objects.User;
import com.nguyencse.todo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Putin on 5/2/2017.
 */

public class MoreFragment extends Fragment {
    private RecyclerView rcvFriends;
    private RecyclerView rcvAllUsers;
    private LinearLayoutManager lnlManagerFriends;
    private LinearLayoutManager lnlManagerUsers;
    private FriendAdapter friendAdapter;
    private UserAdapter userAdapter;
    private DatabaseReference database;
    private List<User> friendList;
    private List<User> userList;
    private TextViewCSE txtTotalFriends;
    private TextViewCSE txtTotalUsers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        database = FirebaseDatabase.getInstance().getReference();

        txtTotalFriends = (TextViewCSE) view.findViewById(R.id.txt_total_friends);
        txtTotalUsers = (TextViewCSE) view.findViewById(R.id.txt_total_users);

        rcvFriends = (RecyclerView) view.findViewById(R.id.rcv_friends);
        rcvAllUsers = (RecyclerView) view.findViewById(R.id.rcv_all_users);

        friendList = new ArrayList<>();
        lnlManagerFriends = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rcvFriends.setLayoutManager(lnlManagerFriends);
        friendAdapter = new FriendAdapter(getContext(), friendList);
        rcvFriends.setAdapter(friendAdapter);
        friendAdapter.notifyDataSetChanged();
        getAllFriends();

        userList = new ArrayList<>();
        rcvAllUsers = (RecyclerView) view.findViewById(R.id.rcv_all_users);
        lnlManagerUsers = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        lnlManagerUsers.setStackFromEnd(true);
        rcvAllUsers.setLayoutManager(lnlManagerUsers);
        userAdapter = new UserAdapter(getContext(), userList);
        rcvAllUsers.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
        getAllUsers();

        return view;
    }

    private void getAllFriends() {
        database.child("friends").child(MainActivity.user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User friend = dataSnapshot.getValue(User.class);

                friendList.add(friend);
                friendAdapter.notifyDataSetChanged();
                txtTotalFriends.setText(friendList.size() + "");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User friendChanged = dataSnapshot.getValue(User.class);

                int count = userList.size();
                for (int i = 0; i < count; i++) {
                    if (friendList.get(i).getId().equals(friendChanged.getId())) {
                        friendList.get(i).setUsername(friendChanged.getUsername());
                        break;
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User friendDeleted = dataSnapshot.getValue(User.class);
                int count = friendList.size();

                for (int i = 0; i < count; i++) {
                    if (friendList.get(i).getId().equals(friendDeleted.getId())) {
                        friendList.remove(i);
                        friendAdapter.notifyDataSetChanged();
                        break;
                    }
                }

                txtTotalFriends.setText(friendList.size() + "");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAllUsers() {
        database.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);

                userList.add(user);
                userAdapter.notifyDataSetChanged();
                txtTotalUsers.setText(userList.size() + "");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User userChanged = dataSnapshot.getValue(User.class);

                int count = userList.size();
                for (int i = 0; i < count; i++) {
                    if (userList.get(i).getId().equals(userChanged.getId())) {
                        userList.get(i).setUsername(userChanged.getUsername());
                        break;
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}