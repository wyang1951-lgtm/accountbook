package com.fyc.accountbook;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 记录仓库：所有页面共用同一份数据。
 * 用一个静态 ArrayList 在内存里保存，同时用 SharedPreferences 存成 JSON 字符串，
 * 这样退出 APP 再进来数据还在。
 */
public class RecordStore {

    private static final String PREF_NAME = "account_book";
    private static final String KEY_DATA = "records";

    private static final ArrayList<Record> LIST = new ArrayList<>();
    private static boolean loaded = false;

    /** 全部记录（ListView 直接用它） */
    public static ArrayList<Record> getAll() {
        return LIST;
    }

    public static void add(Record r) {
        LIST.add(0, r);        // 新记录插到最前面，看列表时最近的在上面
    }

    public static void remove(int index) {
        if (index >= 0 && index < LIST.size()) {
            LIST.remove(index);
        }
    }

    public static void clear() {
        LIST.clear();
    }

    /** 总收入 */
    public static double totalIncome() {
        double s = 0;
        for (Record r : LIST) {
            if (r.isIncome()) s += r.getAmount();
        }
        return s;
    }

    /** 总支出 */
    public static double totalExpense() {
        double s = 0;
        for (Record r : LIST) {
            if (!r.isIncome()) s += r.getAmount();
        }
        return s;
    }

    /** 结余 = 收入 - 支出 */
    public static double balance() {
        return totalIncome() - totalExpense();
    }

    // ---------- 持久化 ----------

    /** 把内存里的记录写成 JSON 存起来 */
    public static void save(Context ctx) {
        try {
            JSONArray arr = new JSONArray();
            for (Record r : LIST) {
                arr.put(r.toJson());
            }
            SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            sp.edit().putString(KEY_DATA, arr.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 从 SharedPreferences 读回来，只在 APP 启动后第一次调用时真正执行 */
    public static void load(Context ctx) {
        if (loaded) return;
        loaded = true;
        try {
            SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String s = sp.getString(KEY_DATA, "");
            if (s == null || s.isEmpty()) return;
            JSONArray arr = new JSONArray(s);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                LIST.add(Record.fromJson(o));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
