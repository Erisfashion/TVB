package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.ui.adapter.SelectDialogAdapter;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SelectDialog<T> extends BaseDialog {
    private TextView tvTitle;
    private TvRecyclerView tvRecyclerView;
    private SelectDialogAdapter<T> adapter;

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

    public SelectDialog<T> setAdapter(SelectDialogAdapter.SelectDialogInterface<T> dialogInterface, SelectDialogAdapter.ItemCallback<T> itemCallback, ArrayList<T> data, int select) {
        adapter = new SelectDialogAdapter<>(dialogInterface, itemCallback);
        adapter.setData(data, select);
        if (tvRecyclerView != null) {
            tvRecyclerView.setAdapter(adapter);
            tvRecyclerView.setSelection(select);
        }
        return this;
    }

    public SelectDialog<T> setAdapter(SelectDialogAdapter.SelectDialogInterface<T> dialogInterface, SelectDialogAdapter.ItemCallback<T> itemCallback, List<T> data, int select) {
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
