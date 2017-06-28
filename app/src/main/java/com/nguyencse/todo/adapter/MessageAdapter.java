package com.nguyencse.todo.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nguyencse.todo.R;
import com.nguyencse.todo.custom.TextViewCSE;
import com.nguyencse.todo.main.MainActivity;
import com.nguyencse.todo.objects.Message;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Putin on 5/5/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Message message = messageList.get(position);

        if (message.getuID().equals(MainActivity.user.getUid())) {
            holder.lnlItemMessage.setGravity(Gravity.RIGHT);
            holder.cardViewMessage.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.txtMessageBody.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            holder.imgAvatar.setVisibility(View.GONE);
        } else {
            holder.lnlItemMessage.setGravity(Gravity.LEFT);
            holder.cardViewMessage.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
            holder.txtMessageBody.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.imgAvatar.setVisibility(View.VISIBLE);
        }
        holder.txtMessageAuthor.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        holder.txtMessageBody.setText(message.getBody());
        String author = message.getUsername() != null ? message.getUsername() : message.getEmail();
        holder.txtMessageAuthor.setText(author);
        holder.txtMessageTime.setText(message.getTime());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextViewCSE txtMessageBody, txtMessageAuthor, txtMessageTime;
        public LinearLayout lnlItemMessage;
        public CardView cardViewMessage;
        public CircleImageView imgAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            lnlItemMessage = (LinearLayout) itemView.findViewById(R.id.message_item);
            cardViewMessage = (CardView) itemView.findViewById(R.id.card_view_message);
            txtMessageBody = (TextViewCSE) itemView.findViewById(R.id.message_body_in);
            txtMessageAuthor = (TextViewCSE) itemView.findViewById(R.id.message_author_in);
            txtMessageTime = (TextViewCSE) itemView.findViewById(R.id.message_time);
            imgAvatar = (CircleImageView) itemView.findViewById(R.id.message_avatar);
        }
    }
}