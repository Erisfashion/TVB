package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.ui.adapter.SelectDialogAdapter;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import java.util.List;

public class SelectDialog extends BaseDialog {
    private TextView tvTitle;
    private TvRecyclerView tvRecyclerView;
    private SelectDialogAdapter adapter;
    private SelectCallback callback;

    public interface SelectCallback {
        void select(int position);
    }

    public SelectDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_select);
        initView();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tvTitle);
        tvRecyclerView = findViewById(R.id.tvRecyclerView);
    }

    public SelectDialog setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    public SelectDialog setData(List<String> data, int select, SelectCallback callback) {
        this.callback = callback;
        adapter = new SelectDialogAdapter(data);
        adapter.setSelect(select);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter a, View view, int position) {
                if (SelectDialog.this.callback != null) {
                    SelectDialog.this.callback.select(position);
                }
                dismiss();
            }
        });
        tvRecyclerView.setAdapter(adapter);
        tvRecyclerView.setSelection(select);
        return this;
    }
}
