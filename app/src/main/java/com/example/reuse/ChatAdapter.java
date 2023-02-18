package com.example.reuse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<ChatMessage> mChatMessages;

    public ChatAdapter(List<ChatMessage> messages) {
        mChatMessages = messages;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView senderNameTextView;
        public TextView messageTextView;
        public TextView timestampTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            senderNameTextView = itemView.findViewById(R.id.sender_name_text_view);
            messageTextView = itemView.findViewById(R.id.message_text_view);
            timestampTextView = itemView.findViewById(R.id.timestamp_text_view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_chat_window, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage message = mChatMessages.get(position);

        holder.senderNameTextView.setText(message.getSenderName());
        holder.messageTextView.setText(message.getMessageText());
        holder.timestampTextView.setText(formatTimestamp(message.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }
    public void addChatMessage(ChatMessage chatMessage) {
        mChatMessages.add(chatMessage);
        notifyItemInserted(mChatMessages.size() - 1);
    }

    public void clearChatMessages() {
        int size = mChatMessages.size();
        mChatMessages.clear();
        notifyItemRangeRemoved(0, size);
    }
    private String formatTimestamp(long timestamp) {
        DateFormat dateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView mUsernameTextView;
        private TextView mMessageTextView;
        private TextView mTimestampTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            mUsernameTextView = itemView.findViewById(R.id.sender_name_text_view);
            mMessageTextView = itemView.findViewById(R.id.message_text_view);
            mTimestampTextView = itemView.findViewById(R.id.timestamp_text_view);
        }

        public void bind(ChatMessage chatMessage) {
            mUsernameTextView.setText(chatMessage.getSenderName());
            mMessageTextView.setText(chatMessage.getMessageText());
            mTimestampTextView.setText(formatTimestamp(chatMessage.getTimestamp()));
        }

        private String formatTimestamp(long timestamp) {
            // Format timestamp as desired
            return System.currentTimeMillis() - timestamp < 1000 * 60 * 60 * 24
                    ? DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(timestamp))
                    : DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(timestamp));
        }
    }
}

