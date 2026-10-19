package com.fyc.accountbook;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 账单列表的自定义 Adapter。
 * 用 BaseAdapter 而不是 ArrayAdapter，是因为每一行要放一个删除按钮，
 * 还要根据"收入/支出"给金额上不同的颜色，得自己控制 getView()。
 */
public class RecordAdapter extends BaseAdapter {

    /** 点了删除按钮之后的回调，交给 Activity 去处理（弹确认框、刷新列表） */
    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    private final Context context;
    private final ArrayList<Record> data;
    private final OnDeleteClickListener listener;

    public RecordAdapter(Context context, ArrayList<Record> data,
                         OnDeleteClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public Object getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // convertView 复用：滑出屏幕的行会被回收再用，避免每次都 new 一个 View
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_record, parent, false);
            holder = new ViewHolder();
            holder.tvCategory = convertView.findViewById(R.id.tvCategory);
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            holder.tvAmount = convertView.findViewById(R.id.tvAmount);
            holder.btnDelete = convertView.findViewById(R.id.btnDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Record r = data.get(position);

        holder.tvCategory.setText(r.getCategory() + (r.isFixed() ? "（固定）" : ""));
        String sub = r.getDate();
        if (r.getNote() != null && !r.getNote().isEmpty()) {
            sub = sub + "  " + r.getNote();
        }
        holder.tvDate.setText(sub);

        holder.tvAmount.setText(r.getAmountText());
        // 支出用红色、收入用绿色，一眼能分出来
        holder.tvAmount.setTextColor(r.isIncome()
                ? Color.parseColor("#1B9C4B")
                : Color.parseColor("#D93025"));

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDelete(position);
                }
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tvCategory;
        TextView tvDate;
        TextView tvAmount;
        ImageButton btnDelete;
    }
}
