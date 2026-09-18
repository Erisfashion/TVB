package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.ui.adapter.SelectDialogAdapter;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SelectDialog<T> extends BaseDialog {
    private TextView tvTitle;
    private TvRecyclerView tvRecyclerView;
    private SelectDialogAdapter<T> adapter;

    public interface ItemCallback<T> {
        void click(T item);
    }

    public SelectDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_select);
        tvTitle = findViewById(R.id.tvTitle);
        tvRecyclerView = findViewById(R.id.mGridView);
    }

    public SelectDialog<T> setTip(String tip) {
        if (tvTitle != null) {
            tvTitle.setText(tip);
        }
        return this;
    }

    public SelectDialog<T> setTitle(String title) {
        return setTip(title);
    }

    // 支持 ModelSettingFragment 等传入的 DiffUtil.ItemCallback
    public SelectDialog<T> setAdapter(SelectDialogAdapter.SelectDialogInterface<T> dialogInterface, DiffUtil.ItemCallback<T> itemCallback, ArrayList<T> data, int select) {
        adapter = new SelectDialogAdapter<>(dialogInterface, itemCallback);
        adapter.setData(data, select);
        if (tvRecyclerView != null) {
            tvRecyclerView.setAdapter(adapter);
            tvRecyclerView.setSelection(select);
        }
        return this;
    }

    public SelectDialog<T> setAdapter(SelectDialogAdapter.SelectDialogInterface<T> dialogInterface, DiffUtil.ItemCallback<T> itemCallback, List<T> data, int select) {
        ArrayList<T> list = new ArrayList<>();
        if (data != null) {
            list.addAll(data);
        }
        return setAdapter(dialogInterface, itemCallback, list, select);
    }

    // 保留通用 Object 回调重载以应对其他调用方
    public SelectDialog<T> setAdapter(SelectDialogAdapter.SelectDialogInterface<T> dialogInterface, Object itemCallback, ArrayList<T> data, int select) {
        if (itemCallback instanceof DiffUtil.ItemCallback) {
            return setAdapter(dialogInterface, (DiffUtil.ItemCallback<T>) itemCallback, data, select);
        }
        adapter = new SelectDialogAdapter<>(dialogInterface, null);
        adapter.setData(data, select);
        if (tvRecyclerView != null) {
            tvRecyclerView.setAdapter(adapter);
            tvRecyclerView.setSelection(select);
        }
        return this;
    }

    public SelectDialog<T> setAdapter(SelectDialogAdapter.SelectDialogInterface<T> dialogInterface, Object itemCallback, List<T> data, int select) {
        ArrayList<T> list = new ArrayList<>();
        if (data != null) {
            list.addAll(data);
        }
        return setAdapter(dialogInterface, itemCallback, list, select);
    }

    public SelectDialog<T> setAdapter(SelectDialogAdapter<T> adapter) {
        this.adapter = adapter;
        if (tvRecyclerView != null) {
            tvRecyclerView.setAdapter(adapter);
        }
        return this;
    }

    public SelectDialogAdapter<T> getAdapter() {
        return adapter;
    }
}
