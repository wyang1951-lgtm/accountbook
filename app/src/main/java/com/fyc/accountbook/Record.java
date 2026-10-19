package com.fyc.accountbook;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 一条记账记录。
 * 用 JavaBean 的写法：私有字段 + 公开的 getter/setter，方便 Adapter 读取。
 */
public class Record {

    private String category;   // 类别，如"餐饮"
    private double amount;     // 金额，始终存正数，方向由 income 决定
    private String date;       // 日期，格式 yyyy-MM-dd
    private boolean income;    // true=收入  false=支出
    private boolean fixed;     // 是否标记为固定支出
    private String note;       // 备注

    public Record(String category, double amount, String date,
                  boolean income, boolean fixed, String note) {
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.income = income;
        this.fixed = fixed;
        this.note = note;
    }

    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public boolean isIncome() { return income; }
    public boolean isFixed() { return fixed; }
    public String getNote() { return note; }

    /** 带正负号的金额文本，列表里直接显示 */
    public String getAmountText() {
        return (income ? "+" : "-") + String.format("%.2f", amount);
    }

    // ---------- 下面两个方法用于把记录存进 SharedPreferences ----------

    public JSONObject toJson() throws JSONException {
        JSONObject o = new JSONObject();
        o.put("category", category);
        o.put("amount", amount);
        o.put("date", date);
        o.put("income", income);
        o.put("fixed", fixed);
        o.put("note", note);
        return o;
    }

    public static Record fromJson(JSONObject o) {
        return new Record(
                o.optString("category", "其他"),
                o.optDouble("amount", 0),
                o.optString("date", ""),
                o.optBoolean("income", false),
                o.optBoolean("fixed", false),
                o.optString("note", ""));
    }
}
