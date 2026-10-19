package com.fyc.accountbook;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

/**
 * 页面 2：账单列表。
 * 用到的备选控件：ListView（账单列表）、ImageButton（返回、每行删除）。
 * 这一页的提示文字来自页面 1 用 Intent 传过来的数据。
 */
public class ListActivity extends AppCompatActivity {

    private RecordAdapter adapter;
    private TextView tvIncome;
    private TextView tvExpense;
    private TextView tvBalance;
    private TextView tvHint;
    private TextView tvEmpty;
    private ListView lvRecords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        View root = findViewById(R.id.rootList);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            androidx.core.graphics.Insets bars =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        RecordStore.load(this);

        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        tvBalance = findViewById(R.id.tvBalance);
        tvHint = findViewById(R.id.tvHint);
        tvEmpty = findViewById(R.id.tvEmpty);
        lvRecords = findViewById(R.id.lvRecords);

        // ---------- 接收页面 1 传来的数据 ----------
        String hint = getIntent().getStringExtra("hint");
        String category = getIntent().getStringExtra("category");
        double amount = getIntent().getDoubleExtra("amount", 0);
        if (hint != null) {
            tvHint.setText(hint);
        }
        if (category != null) {
            // 把传过来的类别和金额再拼一句，证明数据确实传过来了
            tvHint.setText(hint + String.format(Locale.CHINA, "（%s / %.2f）", category, amount));
        }

        // ---------- 返回按钮 ----------
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());   // finish() 就是关掉当前页面回到上一页

        // ---------- 列表 ----------
        adapter = new RecordAdapter(this, RecordStore.getAll(), position -> confirmDelete(position));
        lvRecords.setAdapter(adapter);
        lvRecords.setEmptyView(tvEmpty);   // 没数据时自动显示这个提示

        refreshSummary();
    }

    /** 弹确认框，确认后删掉这一条 */
    private void confirmDelete(final int position) {
        final Record r = RecordStore.getAll().get(position);
        new AlertDialog.Builder(this)
                .setTitle("删除这条记录？")
                .setMessage(r.getCategory() + "  " + r.getAmountText() + " 元\n" + r.getDate())
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RecordStore.remove(position);
                        RecordStore.save(ListActivity.this);
                        adapter.notifyDataSetChanged();   // 通知列表刷新
                        refreshSummary();
                        Toast.makeText(ListActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /** 刷新顶部的收入/支出/结余 */
    private void refreshSummary() {
        tvIncome.setText(String.format(Locale.CHINA, "%.2f", RecordStore.totalIncome()));
        tvExpense.setText(String.format(Locale.CHINA, "%.2f", RecordStore.totalExpense()));
        tvBalance.setText(String.format(Locale.CHINA, "%.2f", RecordStore.balance()));
        tvEmpty.setVisibility(RecordStore.getAll().isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            refreshSummary();
        }
    }
}
