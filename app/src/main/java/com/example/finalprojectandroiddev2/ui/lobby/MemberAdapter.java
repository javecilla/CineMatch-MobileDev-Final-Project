package com.example.finalprojectandroiddev2.ui.lobby;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroiddev2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for lobby member list. Displays username and host badge.
 */
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    private final List<MemberItem> members = new ArrayList<>();

    public void setMembers(List<MemberItem> items) {
        members.clear();
        if (items != null) {
            members.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MemberItem item = members.get(position);
        holder.textUsername.setText(item.username);
        holder.textHostBadge.setVisibility(item.isHost ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textUsername;
        final TextView textHostBadge;

        ViewHolder(View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.text_username);
            textHostBadge = itemView.findViewById(R.id.text_host_badge);
        }
    }

    public static class MemberItem {
        public final String username;
        public final boolean isHost;

        public MemberItem(String username, boolean isHost) {
            this.username = username;
            this.isHost = isHost;
        }
    }
}
