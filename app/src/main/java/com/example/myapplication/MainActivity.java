package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

/**
 * 存款利息计算器
 * 输入本金、年利率、存期，计算单利和复利两种方式下的收益。
 */
public class MainActivity extends AppCompatActivity {

    private EditText etPrincipal;   // 本金
    private EditText etRate;        // 年利率
    private EditText etYears;       // 存期
    private TextView tvResult;      // 结果显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. 找到界面上的控件
        etPrincipal = findViewById(R.id.etPrincipal);
        etRate = findViewById(R.id.etRate);
        etYears = findViewById(R.id.etYears);
        tvResult = findViewById(R.id.tvResult);
        Button btnCalc = findViewById(R.id.btnCalc);

        // 2. 给按钮绑定点击事件
        btnCalc.setOnClickListener(v -> calculate());
    }

    /**
     * 读取输入并计算
     */
    private void calculate() {
        String s1 = etPrincipal.getText().toString().trim();
        String s2 = etRate.getText().toString().trim();
        String s3 = etYears.getText().toString().trim();

        // 非空校验
        if (TextUtils.isEmpty(s1) || TextUtils.isEmpty(s2) || TextUtils.isEmpty(s3)) {
            Toast.makeText(this, R.string.err_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // 数字格式校验
        double principal;
        double rate;
        double years;
        try {
            principal = Double.parseDouble(s1);
            rate = Double.parseDouble(s2);
            years = Double.parseDouble(s3);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.err_input, Toast.LENGTH_SHORT).show();
            return;
        }

        // 取值范围校验
        if (principal <= 0 || rate < 0 || years <= 0) {
            Toast.makeText(this, R.string.err_range, Toast.LENGTH_SHORT).show();
            return;
        }

        // 单利：利息 = 本金 × 年利率 × 存期
        double interest = principal * (rate / 100) * years;
        double simpleTotal = principal + interest;

        // 复利：本息合计 = 本金 × (1 + 年利率) ^ 存期
        double compoundTotal = principal * Math.pow(1 + rate / 100, years);

        String text = String.format(Locale.CHINA,
                "本金：%.2f 元\n年利率：%.2f%%\n存期：%.0f 年\n\n"
                        + "【单利】利息 %.2f 元，本息合计 %.2f 元\n"
                        + "【复利】本息合计 %.2f 元\n"
                        + "（复利比单利多 %.2f 元）",
                principal, rate, years,
                interest, simpleTotal, compoundTotal,
                compoundTotal - simpleTotal);

        tvResult.setText(text);
        Toast.makeText(this, R.string.calc_done, Toast.LENGTH_SHORT).show();
    }
}
