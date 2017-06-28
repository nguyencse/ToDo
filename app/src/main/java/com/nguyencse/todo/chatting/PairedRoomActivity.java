package com.nguyencse.todo.chatting;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.nguyencse.todo.adapter.MessageAdapter;
import com.nguyencse.todo.custom.EditTextCSE;
import com.nguyencse.todo.custom.TextViewCSE;
import com.nguyencse.todo.R;
import com.nguyencse.todo.general.CommonField;
import com.nguyencse.todo.general.CommonMethod;
import com.nguyencse.todo.main.MainActivity;
import com.nguyencse.todo.objects.Message;
import com.nguyencse.todo.objects.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PairedRoomActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rcvMessage;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditTextCSE edtMessageBody;
    private ImageButton btnSend;
    private User fromFriend;
    private TextViewCSE txtScreenTitle;
    private ImageButton btnBack;
    private ImageButton btnLogout;
    private SharedPreferences sprfTodo;

    private DatabaseReference database;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired_room);
        sprfTodo = getSharedPreferences(CommonField.SHAREDPREFERENCES, MODE_PRIVATE);
        fromFriend = new Gson().fromJson(sprfTodo.getString(CommonField.FRIEND_FROM, null), User.class);
        btnBack = (ImageButton) findViewById(R.id.btn_chatting_paired_back);
        btnLogout = (ImageButton) findViewById(R.id.btn_chatting_paired_logout);
        txtScreenTitle = (TextViewCSE) findViewById(R.id.txt_chatting_paired_screen_title);
        txtScreenTitle.setText(fromFriend.getUsername());

        database = FirebaseDatabase.getInstance().getReference();
        currentUser = MainActivity.user;
        edtMessageBody = (EditTextCSE) findViewById(R.id.edt_message_body_private);
        rcvMessage = (RecyclerView) findViewById(R.id.message_list_private);
        btnSend = (ImageButton) findViewById(R.id.btn_send_private);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rcvMessage.setLayoutManager(linearLayoutManager);
        rcvMessage.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
        getAllMessages();

        btnSend.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    private void getAllMessages() {
        database.child("chatting").child("paired").child(MainActivity.user.getUid()).child(fromFriend.getId()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                messageList.add(message);
                messageAdapter.notifyDataSetChanged();
                rcvMessage.smoothScrollToPosition(messageAdapter.getItemCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    private void sendMessage() {
        String messContent = edtMessageBody.getText().toString().trim();

        if (TextUtils.isEmpty(messContent)) {
            edtMessageBody.setError(getString(R.string.error_message_empty));
            return;
        }

        Calendar calendar = Calendar.getInstance();
        Message message = new Message(currentUser.getUid(), messContent, currentUser.getDisplayName(), currentUser.getEmail(), DateFormat.format("HH:mm dd-MM-yyyy", calendar.getTime()).toString(), null);

        String key1 = database.child("chatting").child("paired").child(MainActivity.user.getUid()).child(fromFriend.getId()).push().getKey();
        Map<String, Object> childUpdates1 = new HashMap<>();
        childUpdates1.put("/chatting/paired/" + MainActivity.user.getUid() + "/" + fromFriend.getId() + "/" + key1, message.toMap());
        database.updateChildren(childUpdates1);

        String key2 = database.child("chatting").child("paired").child(fromFriend.getId()).child(MainActivity.user.getUid()).push().getKey();
        Map<String, Object> childUpdates2 = new HashMap<>();
        childUpdates2.put("/chatting/paired/" + fromFriend.getId() + "/" + MainActivity.user.getUid() + "/" + key2, message.toMap());
        database.updateChildren(childUpdates2);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.btn_chatting_paired_back:
                finish();
                break;
            case R.id.btn_chatting_paired_logout:
                CommonMethod.logout(this);
                break;
            case R.id.btn_send_private:
                sendMessage();
                edtMessageBody.setText("");
                messageAdapter.notifyDataSetChanged();
                break;
        }
    }
}
