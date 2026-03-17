package com.jieshi.personnel.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jieshi.personnel.R;
import com.jieshi.personnel.manager.PersonnelDataManager;
import com.jieshi.personnel.model.GovernmentLevel;
import com.jieshi.personnel.model.PersonnelInfo;
import com.jieshi.personnel.model.PersonnelType;
import com.jieshi.personnel.model.VillageLevel;

import java.io.File;
import java.util.Calendar;

/**
 * 新增/编辑人员界面
 */
public class EditActivity extends AppCompatActivity {
    
    private TextInputEditText etName, etGender, etBirthDate, etEthnicity;
    private TextInputEditText etPoliticalStatus, etEducation, etMajor, etWorkStartDate;
    private TextInputEditText etPhone, etIdCard, etAddress;
    private TextInputLayout tilName, tilPhone, tilIdCard;
    
    private AutoCompleteTextView actvPersonnelType;
    
    private PersonnelDataManager dataManager;
    private PersonnelInfo editPersonnel;
    private boolean isEditMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        
        // 初始化视图
        initViews();
        
        // 初始化数据管理器
        initDataManager();
        
        // 检查是否为编辑模式
        checkEditMode();
        
        // 设置日期选择器
        setupDatePickers();
        
        // 设置人员类型下拉框
        setupPersonnelTypeSpinner();
    }
    
    private void initViews() {
        // 工具栏
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? "编辑人员" : "新增人员");
        }
        
        // 基本信息
        etName = findViewById(R.id.etName);
        etGender = findViewById(R.id.etGender);
        etBirthDate = findViewById(R.id.etBirthDate);
        etEthnicity = findViewById(R.id.etEthnicity);
        etPoliticalStatus = findViewById(R.id.etPoliticalStatus);
        etEducation = findViewById(R.id.etEducation);
        etMajor = findViewById(R.id.etMajor);
        etWorkStartDate = findViewById(R.id.etWorkStartDate);
        etPhone = findViewById(R.id.etPhone);
        etIdCard = findViewById(R.id.etIdCard);
        etAddress = findViewById(R.id.etAddress);
        
        // 验证布局
        tilName = findViewById(R.id.tilName);
        tilPhone = findViewById(R.id.tilPhone);
        tilIdCard = findViewById(R.id.tilIdCard);
        
        // 人员类型
        actvPersonnelType = findViewById(R.id.actvPersonnelType);
    }
    
    private void initDataManager() {
        String csvPath = new File(getFilesDir(), "personnel.csv").getAbsolutePath();
        dataManager = new PersonnelDataManager(csvPath);
        dataManager.loadFromCsv();
    }
    
    private void checkEditMode() {
        String personnelId = getIntent().getStringExtra("personnel_id");
        if (personnelId != null) {
            isEditMode = true;
            editPersonnel = findPersonnelById(personnelId);
            if (editPersonnel != null) {
                fillData();
            }
        }
    }
    
    private PersonnelInfo findPersonnelById(String id) {
        for (PersonnelInfo info : dataManager.getAllPersonnel()) {
            if (info.getId().equals(id)) {
                return info;
            }
        }
        return null;
    }
    
    private void fillData() {
        if (editPersonnel == null) return;
        
        etName.setText(editPersonnel.getName());
        etGender.setText(editPersonnel.getGender());
        etBirthDate.setText(editPersonnel.getBirthDate());
        etEthnicity.setText(editPersonnel.getEthnicity());
        etPoliticalStatus.setText(editPersonnel.getPoliticalStatus());
        etEducation.setText(editPersonnel.getEducation());
        etMajor.setText(editPersonnel.getMajor());
        etWorkStartDate.setText(editPersonnel.getWorkStartDate());
        etPhone.setText(editPersonnel.getPhone());
        etIdCard.setText(editPersonnel.getIdCard());
        etAddress.setText(editPersonnel.getAddress());
        
        if (editPersonnel.getPersonnelType() != null) {
            actvPersonnelType.setText(editPersonnel.getPersonnelType().getDisplayName());
        }
    }
    
    private void setupDatePickers() {
        // 出生日期选择器
        etBirthDate.setOnClickListener(v -> showDatePicker(etBirthDate));
        
        // 参加工作时间选择器
        etWorkStartDate.setOnClickListener(v -> showDatePicker(etWorkStartDate));
    }
    
    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                editText.setText(date);
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }
    
    private void setupPersonnelTypeSpinner() {
        String[] types = {"镇府干部", "村两委干部", "网格联防员"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            types
        );
        actvPersonnelType.setAdapter(adapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            savePersonnel();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void savePersonnel() {
        // 验证必填字段
        if (!validate()) {
            return;
        }
        
        PersonnelInfo info;
        if (isEditMode && editPersonnel != null) {
            info = editPersonnel;
        } else {
            info = new PersonnelInfo();
        }
        
        // 填充数据
        info.setName(getText(etName));
        info.setGender(getText(etGender));
        info.setBirthDate(getText(etBirthDate));
        info.setEthnicity(getText(etEthnicity));
        info.setPoliticalStatus(getText(etPoliticalStatus));
        info.setEducation(getText(etEducation));
        info.setMajor(getText(etMajor));
        info.setWorkStartDate(getText(etWorkStartDate));
        info.setPhone(getText(etPhone));
        info.setIdCard(getText(etIdCard));
        info.setAddress(getText(etAddress));
        
        // 设置人员类型
        String typeStr = actvPersonnelType.getText().toString();
        info.setPersonnelType(PersonnelType.fromDisplayName(typeStr));
        
        // 保存
        if (isEditMode) {
            dataManager.updatePersonnel(info);
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        } else {
            dataManager.addPersonnel(info);
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
        }
        
        finish();
    }
    
    private boolean validate() {
        boolean valid = true;
        
        // 姓名验证
        String name = getText(etName);
        if (TextUtils.isEmpty(name)) {
            tilName.setError("姓名不能为空");
            valid = false;
        } else {
            tilName.setError(null);
        }
        
        // 电话验证
        String phone = getText(etPhone);
        if (TextUtils.isEmpty(phone)) {
            tilPhone.setError("电话不能为空");
            valid = false;
        } else if (!phone.matches("1[3-9]\\d{9}")) {
            tilPhone.setError("请输入正确的手机号");
            valid = false;
        } else {
            tilPhone.setError(null);
        }
        
        // 身份证验证
        String idCard = getText(etIdCard);
        if (TextUtils.isEmpty(idCard)) {
            tilIdCard.setError("身份证号不能为空");
            valid = false;
        } else if (!idCard.matches("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)")) {
            tilIdCard.setError("请输入正确的身份证号");
            valid = false;
        } else {
            tilIdCard.setError(null);
        }
        
        return valid;
    }
    
    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }
    
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setTitle("提示")
            .setMessage("确定要放弃修改吗？")
            .setPositiveButton("确定", (dialog, which) -> finish())
            .setNegativeButton("取消", null)
            .show();
    }
}
