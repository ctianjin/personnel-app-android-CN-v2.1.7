package com.jieshi.personnel.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.jieshi.personnel.R;

import java.util.List;

/**
 * 机构列表适配器
 */
public class InstitutionAdapter extends RecyclerView.Adapter<InstitutionAdapter.ViewHolder> {
    
    private List<InstitutionListActivity.Institution> dataList;
    private Context context;
    
    public InstitutionAdapter(List<InstitutionListActivity.Institution> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_institution, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InstitutionListActivity.Institution institution = dataList.get(position);
        holder.btnInstitution.setText(institution.name);
        
        // 点击跳转到人员列表
        holder.btnInstitution.setOnClickListener(v -> {
            Intent intent = new Intent(context, PersonnelListActivity.class);
            intent.putExtra("type", "institution");
            intent.putExtra("institution_id", institution.id);
            intent.putExtra("institution_name", institution.name);
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialButton btnInstitution;
        
        ViewHolder(View itemView) {
            super(itemView);
            btnInstitution = itemView.findViewById(R.id.btnInstitution);
        }
    }
}
