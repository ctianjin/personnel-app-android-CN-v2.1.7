package com.jieshi.personnel.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.jieshi.personnel.R;

import java.util.List;

/**
 * 村社区列表适配器（3 列网格）
 */
public class VillageAdapter extends RecyclerView.Adapter<VillageAdapter.ViewHolder> {
    
    private List<VillageListActivity.Village> dataList;
    private String personnelType;
    private Context context;
    
    public VillageAdapter(List<VillageListActivity.Village> dataList, String personnelType, Context context) {
        this.dataList = dataList;
        this.personnelType = personnelType;
        this.context = context;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_village, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VillageListActivity.Village village = dataList.get(position);
        holder.btnVillage.setText(village.name);
        
        // 点击跳转到人员列表
        holder.btnVillage.setOnClickListener(v -> {
            Intent intent = new Intent(context, PersonnelListActivity.class);
            intent.putExtra("type", "village");
            intent.putExtra("village_id", village.id);
            intent.putExtra("village_name", village.name);
            intent.putExtra("personnel_type", personnelType);
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton btnVillage;
        
        ViewHolder(View itemView) {
            super(itemView);
            btnVillage = itemView.findViewById(R.id.btnVillage);
        }
    }
}
