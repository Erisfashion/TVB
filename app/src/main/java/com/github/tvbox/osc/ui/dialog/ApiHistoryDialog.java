package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.ui.adapter.ApiHistoryDialogAdapter;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import java.util.List;

public class ApiHistoryDialog extends BaseDialog {
    private TextView tvTitle;
    private TvRecyclerView tvRecyclerView;
    private ApiHistoryDialogAdapter adapter;
    private HistoryCallback callback;

    public interface HistoryCallback {
        void select(int position);
        void delete(int position);
    }

    public ApiHistoryDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_api_history);
        initView();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tvTitle);
        tvRecyclerView = findViewById(R.id.tvRecyclerView);
    }

    public ApiHistoryDialog setData(List<String> data, int select, HistoryCallback callback) {
        this.callback = callback;
        adapter = new ApiHistoryDialogAdapter(data);
        adapter.setSelect(select);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter a, View view, int position) {
                if (ApiHistoryDialog.this.callback != null) {
                    ApiHistoryDialog.this.callback.select(position);
                }
                dismiss();
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter a, View view, int position) {
                if (view.getId() == R.id.ivDelete) {
                    if (ApiHistoryDialog.this.callback != null) {
                        ApiHistoryDialog.this.callback.delete(position);
                    }
                }
            }
        });
        tvRecyclerView.setAdapter(adapter);
        tvRecyclerView.setSelection(select);
        return this;
    }
}
