package com.example.finalprojectandroiddev2.ui.lobby;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalprojectandroiddev2.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for lobby member list.
 * Each card shows the default avatar, member username, role (Host / Member),
 * the host badge indicator, and an online status dot.
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

        // Avatar
        holder.imageAvatar.setImageResource(R.drawable.default_user_avatar3);

        // Name line: "Jerome Avecilla (Host)" or "Jerome Avecilla (Member)"
        String roleLabel = item.isHost
                ? holder.itemView.getContext().getString(R.string.label_host)
                : holder.itemView.getContext().getString(R.string.label_member);
        holder.textUsername.setText(item.username + " (" + roleLabel + ")");

        // Subtitle: gender (falls back to role label if gender is missing)
        if (item.gender != null && !item.gender.isEmpty()) {
            holder.textRole.setText(item.gender);
        } else {
            holder.textRole.setText(roleLabel);
        }

        // Current-user highlight: 2dp primary border, none otherwise
        MaterialCardView card = (MaterialCardView) holder.itemView;
        if (item.isCurrentUser) {
            card.setStrokeWidth((int) (2 * holder.itemView.getContext()
                    .getResources().getDisplayMetrics().density));
        } else {
            card.setStrokeWidth(0);
        }

        // Online dot
        holder.viewStatusIndicator.setVisibility(item.isOnline ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageAvatar;
        final TextView  textUsername;
        final TextView  textRole;
        final TextView  textHostBadge;
        final View      viewStatusIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            imageAvatar          = itemView.findViewById(R.id.image_avatar);
            textUsername         = itemView.findViewById(R.id.text_username);
            textRole             = itemView.findViewById(R.id.text_role);
            textHostBadge        = itemView.findViewById(R.id.text_host_badge);
            viewStatusIndicator  = itemView.findViewById(R.id.view_status_indicator);
        }
    }

    public static class MemberItem {
        public final String  username;
        public final String  gender;
        public final boolean isHost;
        public final boolean isOnline;
        public final boolean isCurrentUser;

        public MemberItem(String username, String gender, boolean isHost, boolean isCurrentUser) {
            this(username, gender, isHost, true, isCurrentUser);
        }

        public MemberItem(String username, String gender, boolean isHost, boolean isOnline,
                          boolean isCurrentUser) {
            this.username      = username;
            this.gender        = gender;
            this.isHost        = isHost;
            this.isOnline      = isOnline;
            this.isCurrentUser = isCurrentUser;
        }
    }
}
