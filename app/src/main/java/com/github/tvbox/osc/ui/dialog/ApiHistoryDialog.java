package com.github.tvbox.osc.ui.dialog;

import android.content.Context;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.ui.adapter.ApiHistoryDialogAdapter;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ApiHistoryDialog extends BaseDialog {
    private TextView tvTitle;
    private TvRecyclerView tvRecyclerView;
    private ApiHistoryDialogAdapter adapter;

    public ApiHistoryDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_api_history);
        tvTitle = findViewById(R.id.tvTitle);
        tvRecyclerView = findViewById(R.id.mGridView);
    }

    public ApiHistoryDialog setTip(String tip) {
        if (tvTitle != null) {
            tvTitle.setText(tip);
        }
        return this;
    }

    public ApiHistoryDialog setAdapter(ApiHistoryDialogAdapter.SelectDialogInterface dialogInterface, ArrayList<String> data, int select) {
        adapter = new ApiHistoryDialogAdapter(dialogInterface);
        adapter.setData(data, select);
        if (tvRecyclerView != null) {
            tvRecyclerView.setAdapter(adapter);
            tvRecyclerView.setSelection(select);
        }
        return this;
    }

    public ApiHistoryDialog setAdapter(ApiHistoryDialogAdapter.SelectDialogInterface dialogInterface, List<String> data, int select) {
        ArrayList<String> list = new ArrayList<>();
        if (data != null) {
            list.addAll(data);
        }
        return setAdapter(dialogInterface, list, select);
    }

    public ApiHistoryDialogAdapter getAdapter() {
        return adapter;
    }
}
