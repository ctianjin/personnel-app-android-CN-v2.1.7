package com.jieshi.personnel.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.jieshi.personnel.R;
import com.jieshi.personnel.model.PersonnelInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索结果适配器
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    
    private List<PersonnelInfo> dataList;
    private final Context context;
    
    public SearchAdapter(List<PersonnelInfo> dataList) {
        this.dataList = dataList != null ? dataList : new ArrayList<>();
        this.context = null;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonnelInfo info = dataList.get(position);
        holder.tvName.setText(info.getName());
        holder.tvIdentity.setText(info.getIdentityDescription());
        holder.tvPosition.setText(info.getFullPosition());
        
        // 点击查看详情
        holder.cardView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("personnel_id", info.getId());
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    
    /**
     * 更新数据
     */
    public void updateData(List<PersonnelInfo> newData) {
        this.dataList = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvIdentity, tvPosition;
        MaterialCardView cardView;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvIdentity = itemView.findViewById(R.id.tvIdentity);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
