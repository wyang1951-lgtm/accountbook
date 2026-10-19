package com.fyc.accountbook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.Locale;

/**
 * 页面 1：记一笔。
 * 用到的备选控件：Spinner（类别）、DatePicker（日期）、RadioGroup（收/支）、CheckBox（固定支出）。
 */
public class MainActivity extends AppCompatActivity {

    // 类别下拉框的数据源
    private static final String[] CATEGORIES = {
            "餐饮", "交通", "购物", "娱乐", "学习", "房租", "医疗", "工资", "其他"
    };

    private EditText etAmount;
    private EditText etNote;
    private Spinner spCategory;
    private DatePicker dpDate;
    private RadioButton rbExpense;
    private RadioButton rbIncome;
    private CheckBox cbFixed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 让内容避开状态栏和导航栏（全面屏适配）
        View root = findViewById(R.id.rootMain);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            androidx.core.graphics.Insets bars =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // 先把上次存的记录读回来（只会在第一次真正读）
        RecordStore.load(this);

        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        spCategory = findViewById(R.id.spCategory);
        dpDate = findViewById(R.id.dpDate);
        rbExpense = findViewById(R.id.rbExpense);
        rbIncome = findViewById(R.id.rbIncome);
        cbFixed = findViewById(R.id.cbFixed);

        // Spinner 需要一个 ArrayAdapter 提供数据
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, CATEGORIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
        spCategory.setSelection(0);

        // 日期初始值设为今天
        Calendar c = Calendar.getInstance();
        dpDate.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveRecord());

        Button btnGoList = findViewById(R.id.btnGoList);
        btnGoList.setOnClickListener(v -> {
            Intent it = new Intent(MainActivity.this, ListActivity.class);
            it.putExtra("hint", "还没有新记录");
            startActivity(it);
        });
    }

    /** 读取界面上的内容，校验通过后存进 RecordStore 并跳转到列表页 */
    private void saveRecord() {
        String amountStr = etAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            toast("请先填写金额");
            etAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            toast("金额必须是数字");
            etAmount.requestFocus();
            return;
        }

        if (amount <= 0) {
            toast("金额要大于 0");
            etAmount.requestFocus();
            return;
        }
        if (amount > 100000000) {
            toast("金额太大了，请检查一下");
            etAmount.requestFocus();
            return;
        }

        String category = (String) spCategory.getSelectedItem();
        Calendar now = Calendar.getInstance();         String date = String.format(Locale.CHINA, "%04d-%02d-%02d %02d:%02d",                 dpDate.getYear(), dpDate.getMonth() + 1, dpDate.getDayOfMonth(),                 now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE));
        boolean income = rbIncome.isChecked();     // 没选收入就是支出
        boolean fixed = cbFixed.isChecked();
        String note = etNote.getText().toString().trim();

        Record r = new Record(category, amount, date, income, fixed, note);
        RecordStore.add(r);
        RecordStore.save(this);                    // 存盘

        // 把刚保存的记录摘要通过 Intent 传给列表页
        String hint = "已保存：" + (income ? "收入 " : "支出 ")
                + String.format(Locale.CHINA, "%.2f 元 · %s", amount, category);

        Intent it = new Intent(MainActivity.this, ListActivity.class);
        it.putExtra("hint", hint);
        it.putExtra("category", category);
        it.putExtra("amount", amount);
        startActivity(it);

        // 清空输入，回到这个页面时是干净的
        etAmount.setText("");
        etNote.setText("");
        cbFixed.setChecked(false);
        rbExpense.setChecked(true);
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /** 回到页面时刷新一下汇总（从列表页返回时用得上） */
    @Override
    protected void onResume() {
        super.onResume();
        RecordStore.load(this);
    }
}
