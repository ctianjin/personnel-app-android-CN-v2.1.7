package com.jieshi.personnel.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.jieshi.personnel.R;
import com.jieshi.personnel.model.PersonnelInfo;

import java.io.File;
import java.util.List;

/**
 * 人员列表适配器（三级菜单）
 */
public class PersonnelListAdapter extends RecyclerView.Adapter<PersonnelListAdapter.ViewHolder> {
    
    private List<PersonnelInfo> dataList;
    private Context context;
    private String personnelType;
    
    public PersonnelListAdapter(List<PersonnelInfo> dataList, String personnelType, Context context) {
        this.dataList = dataList;
        this.personnelType = personnelType;
        this.context = context;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_personnel_list, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonnelInfo info = dataList.get(position);
        android.util.Log.d("PersonnelAdapter", "onBindViewHolder position: " + position + ", name: " + info.getName());
        
        holder.tvName.setText(info.getName());
        holder.tvPosition.setText(info.getFullPosition());
        
        // 加载头像
        if (info.getAvatarPath() != null && !info.getAvatarPath().isEmpty()) {
            File avatarFile = new File(info.getAvatarPath());
            if (avatarFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(avatarFile.getAbsolutePath());
                if (bitmap != null) {
                    holder.ivAvatar.setImageBitmap(bitmap);
                } else {
                    holder.ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
                }
            } else {
                holder.ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
            }
        } else {
            holder.ivAvatar.setImageResource(R.drawable.ic_avatar_placeholder);
        }
        
        // 点击查看详情
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("personnel_id", info.getId());
            
            // 传递人员类型，用于控制详情页卡片显示
            if (personnelType != null) {
                intent.putExtra("personnel_type", personnelType);
            } else {
                // 根据人员信息推断类型
                if (info.isTownOfficial()) {
                    intent.putExtra("personnel_type", "town_official");
                } else if (info.isVillageCadre()) {
                    intent.putExtra("personnel_type", "village_cadre");
                } else if (info.isGridDefender()) {
                    intent.putExtra("personnel_type", "grid_defender");
                }
            }
            
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        int count = dataList.size();
        android.util.Log.d("PersonnelAdapter", "getItemCount: " + count);
        return count;
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName, tvPosition;
        MaterialCardView cardView;
        
        ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvName);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
