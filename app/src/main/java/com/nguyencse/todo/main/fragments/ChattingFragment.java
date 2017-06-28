package com.nguyencse.todo.main.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nguyencse.todo.adapter.MessageAdapter;
import com.nguyencse.todo.custom.EditTextCSE;
import com.nguyencse.todo.main.MainActivity;
import com.nguyencse.todo.objects.Message;
import com.nguyencse.todo.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Putin on 4/12/2017.
 */

public class ChattingFragment extends Fragment {
    private RecyclerView rcvMessage;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditTextCSE edtMessageBody;
    private ImageButton btnSend;

    private DatabaseReference database;

    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatting, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        currentUser = MainActivity.user;
        edtMessageBody = (EditTextCSE) view.findViewById(R.id.edt_message_body);
        rcvMessage = (RecyclerView) view.findViewById(R.id.message_list);
        btnSend = (ImageButton) view.findViewById(R.id.btn_send);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(getContext(), messageList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rcvMessage.setLayoutManager(linearLayoutManager);
        rcvMessage.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
        getAllMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                edtMessageBody.setText("");
                messageAdapter.notifyDataSetChanged();
            }
        });
        return view;
    }

    private void getAllMessages() {
        database.child("chatting").child("global").addChildEventListener(new ChildEventListener() {
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

        String key = database.child("chatting").child("global").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/chatting/global/" + key, message.toMap());
        database.updateChildren(childUpdates);
    }
}
