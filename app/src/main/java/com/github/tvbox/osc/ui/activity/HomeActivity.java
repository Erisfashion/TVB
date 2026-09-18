package com.github.tvbox.osc.ui.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.base.BaseLazyFragment;
import com.github.tvbox.osc.bean.AbsSortXml;
import com.github.tvbox.osc.bean.MovieSort;
import com.github.tvbox.osc.bean.SourceBean;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.ui.adapter.HomePageAdapter;
import com.github.tvbox.osc.ui.adapter.SelectDialogAdapter;
import com.github.tvbox.osc.ui.adapter.SortAdapter;
import com.github.tvbox.osc.ui.dialog.SelectDialog;
import com.github.tvbox.osc.ui.dialog.TipDialog;
import com.github.tvbox.osc.ui.fragment.GridFragment;
import com.github.tvbox.osc.ui.fragment.UserFragment;
import com.github.tvbox.osc.ui.tv.widget.DefaultTransformer;
import com.github.tvbox.osc.ui.tv.widget.FixedSpeedScroller;
import com.github.tvbox.osc.ui.tv.widget.NoScrollViewPager;
import com.github.tvbox.osc.ui.tv.widget.ViewObj;
import com.github.tvbox.osc.util.AppManager;
import com.github.tvbox.osc.util.DefaultConfig;
import com.github.tvbox.osc.util.FastClickCheckUtil;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.MD5;
import com.github.tvbox.osc.viewmodel.SourceViewModel;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7GridLayoutManager;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.jessyan.autosize.utils.AutoSizeUtils;

public class HomeActivity extends BaseActivity {
    private LinearLayout topLayout;
    private LinearLayout contentLayout;
    private TextView tvDate;
    private TextView tvName;
    private TvRecyclerView mGridView;
    private NoScrollViewPager mViewPager;
    private SourceViewModel sourceViewModel;
    private SortAdapter sortAdapter;
    private HomePageAdapter pageAdapter;
    private View currentView;
    private final List<BaseLazyFragment> fragments = new ArrayList<>();
    private boolean isDownOrUp = false;
    private boolean sortChange = false;
    private int currentSelected = 0;
    private int sortFocused = 0;
    public View sortFocusView = null;
    private final Handler mHandler = new Handler();
    private long mExitTime = 0;
    private final Runnable mRunnable = new Runnable() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void run() {
            Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            if (tvDate != null) {
                tvDate.setText(timeFormat.format(date));
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    // 针对平板的 5 秒超时保护：防止无接口或网络不通时永久卡死在“大T字”遮罩层
    private final Runnable mTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            if (isLoading()) {
                showSuccess();
                refreshEmpty();
                Toast.makeText(HomeActivity.this, "已进入首页，请点击【设置】配置接口地址", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_home;
    }

    boolean useCacheConfig = false;

    @Override
    protected void init() {
        EventBus.getDefault().register(this);
        ControlManager.get().startServer();
        initView();
        initViewModel();
        useCacheConfig = false;
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            useCacheConfig = bundle.getBoolean("useCache", false);
        }
        initData();
    }

    private void initView() {
        this.topLayout = findViewById(R.id.topLayout);
        this.tvDate = findViewById(R.id.tvDate);
        this.tvName = findViewById(R.id.tvName);
        this.contentLayout = findViewById(R.id.contentLayout);
        this.mGridView = findViewById(R.id.mGridView);
        this.mViewPager = findViewById(R.id.mViewPager);
        this.sortAdapter = new SortAdapter();
        this.mGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 0, false));
        this.mGridView.setSpacingWithMargins(0, AutoSizeUtils.dp2px(this.mContext, 10.0f));
        this.mGridView.setAdapter(this.sortAdapter);
        sortAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mGridView.post(() -> {
                    if (mGridView.getLayoutManager() != null) {
                        View firstChild = mGridView.getLayoutManager().findViewByPosition(0);
                        if (firstChild != null) {
                            mGridView.setSelection(0);
                            firstChild.requestFocus();
                        }
                    }
                });
            }
        });
        this.mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            public void onItemPreSelected(TvRecyclerView tvRecyclerView, View view, int position) {
                if (view != null && !HomeActivity.this.isDownOrUp) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = view.findViewById(R.id.tvTitle);
                            if (textView != null) {
                                textView.getPaint().setFakeBoldText(false);
                                if (sortFocused == p) {
                                    view.animate().scaleX(1.1f).scaleY(1.1f).setInterpolator(new BounceInterpolator()).setDuration(300).start();
                                    textView.setTextColor(HomeActivity.this.getResources().getColor(R.color.color_FFFFFF));
                                } else {
                                    view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                                    textView.setTextColor(HomeActivity.this.getResources().getColor(R.color.color_BBFFFFFF));
                                    View filter = view.findViewById(R.id.tvFilter);
                                    if (filter != null) filter.setVisibility(View.GONE);
                                    View filterColor = view.findViewById(R.id.tvFilterColor);
                                    if (filterColor != null) filterColor.setVisibility(View.GONE);
                                }
                                textView.invalidate();
                            }
                        }

                        public final int p = position;
                    }, 10);
                }
            }

            public void onItemSelected(TvRecyclerView tvRecyclerView, View view, int position) {
                if (view != null) {
                    HomeActivity.this.currentView = view;
                    HomeActivity.this.isDownOrUp = false;
                    HomeActivity.this.sortChange = true;
                    view.animate().scaleX(1.1f).scaleY(1.1f).setInterpolator(new BounceInterpolator()).setDuration(300).start();
                    TextView textView = view.findViewById(R.id.tvTitle);
                    if (textView != null) {
                        textView.getPaint().setFakeBoldText(true);
                        textView.setTextColor(HomeActivity.this.getResources().getColor(R.color.color_FFFFFF));
                        textView.invalidate();
                    }
                    MovieSort.SortData sortData = sortAdapter.getItem(position);
                    if (sortData != null && sortData.filters != null && !sortData.filters.isEmpty()) {
                        showFilterIcon(sortData.filterSelectCount());
                    }
                    HomeActivity.this.sortFocusView = view;
                    HomeActivity.this.sortFocused = position;
                    mHandler.removeCallbacks(mDataRunnable);
                    mHandler.postDelayed(mDataRunnable, 200);
                }
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                if (itemView != null && currentSelected == position) {
                    if (currentSelected >= 0 && currentSelected < fragments.size()) {
                        BaseLazyFragment baseLazyFragment = fragments.get(currentSelected);
                        MovieSort.SortData item = sortAdapter.getItem(position);
                        if ((baseLazyFragment instanceof GridFragment) && item != null && item.filters != null && !item.filters.isEmpty()) {
                            ((GridFragment) baseLazyFragment).showFilter();
                        } else if (baseLazyFragment instanceof UserFragment) {
                            showSiteSwitch();
                        }
                    }
                }
            }
        });

        this.mGridView.setOnInBorderKeyEventListener(new TvRecyclerView.OnInBorderKeyEventListener() {
            public boolean onInBorderKeyEvent(int direction, View view) {
                if (direction == View.FOCUS_UP) {
                    if (sortFocused >= 0 && sortFocused < fragments.size()) {
                        BaseLazyFragment baseLazyFragment = fragments.get(sortFocused);
                        if ((baseLazyFragment instanceof GridFragment)) {
                            ((GridFragment) baseLazyFragment).forceRefresh();
                        }
                    }
                }
                if (direction != View.FOCUS_DOWN) {
                    return false;
                }
                if (sortFocused >= 0 && sortFocused < fragments.size()) {
                    BaseLazyFragment baseLazyFragment = fragments.get(sortFocused);
                    if (!(baseLazyFragment instanceof GridFragment)) {
                        return false;
                    }
                    return !((GridFragment) baseLazyFragment).isLoad();
                }
                return false;
            }
        });

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FastClickCheckUtil.check(v);
                SourceBean homeSource = ApiConfig.get().getHomeSourceBean();
                if (dataInitOk && jarInitOk && homeSource != null && homeSource.getKey() != null && !homeSource.getKey().isEmpty()) {
                    String cspCachePath = FileUtils.getFilePath() + "/csp/";
                    String jar = homeSource.getJar();
                    String jarUrl = (jar != null && !jar.isEmpty()) ? jar : ApiConfig.get().getSpider();
                    if (jarUrl == null) jarUrl = "";
                    File cspCacheDir = new File(cspCachePath + MD5.string2MD5(jarUrl) + ".jar");
                    Toast.makeText(mContext, "jar缓存已清除", Toast.LENGTH_SHORT).show();
                    if (!cspCacheDir.exists()) {
                        refreshHome();
                        return;
                    }
                    new Thread(() -> {
                        try {
                            FileUtils.deleteFile(cspCacheDir);
                            ApiConfig.get().clearJarLoader();
                            mHandler.post(() -> refreshHome());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                } else {
                    Toast.makeText(mContext, "请先在设置中配置接口地址", Toast.LENGTH_SHORT).show();
                    jumpActivity(SettingActivity.class);
                }
            }
        });

        tvName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                jumpActivity(SettingActivity.class);
                return true;
            }
        });
        setLoadSir(this.contentLayout);
    }

    private boolean skipNextUpdate = false;

    private void initViewModel() {
        sourceViewModel = new ViewModelProvider(this).get(SourceViewModel.class);
        sourceViewModel.sortResult.observe(this, new Observer<AbsSortXml>() {
            @Override
            public void onChanged(AbsSortXml absXml) {
                mHandler.removeCallbacks(mTimeoutRunnable);
                if (skipNextUpdate) {
                    skipNextUpdate = false;
                    return;
                }
                showSuccess();
                SourceBean home = ApiConfig.get().getHomeSourceBean();
                String homeKey = home != null ? home.getKey() : "";
                if (absXml != null && absXml.classes != null && absXml.classes.sortList != null && !absXml.classes.sortList.isEmpty()) {
                    sortAdapter.setNewData(DefaultConfig.adjustSort(homeKey, absXml.classes.sortList, true));
                } else {
                    sortAdapter.setNewData(DefaultConfig.adjustSort(homeKey, new ArrayList<>(), true));
                }
                initViewPager(absXml);
                if (home != null && home.getName() != null && !home.getName().isEmpty()) {
                    tvName.setText(home.getName());
                }
                tvName.clearAnimation();
            }
        });
    }

    private boolean dataInitOk = false;
    private boolean jarInitOk = false;

    private void initData() {
        String apiUrl = Hawk.get(HawkConfig.API_URL, "");
        // 全新安装未配置接口，直接展示“我的”设置主页，不进入加载死锁
        if (apiUrl == null || apiUrl.trim().isEmpty()) {
            dataInitOk = true;
            jarInitOk = true;
            showSuccess();
            refreshEmpty();
            Toast.makeText(this, "欢迎使用！请点击【设置】配置数据源接口", Toast.LENGTH_LONG).show();
            return;
        }

        if (dataInitOk && jarInitOk) {
            SourceBean home = ApiConfig.get().getHomeSourceBean();
            if (home != null && home.getKey() != null && !home.getKey().isEmpty()) {
                sourceViewModel.getSort(home.getKey());
            } else {
                showSuccess();
                refreshEmpty();
            }
            if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                LOG.e("有");
            } else {
                LOG.e("无");
            }
            if (!useCacheConfig && Hawk.get(HawkConfig.DEFAULT_LOAD_LIVE, false)) {
                jumpActivity(LivePlayActivity.class);
            }
            return;
        }

        tvNameAnimation();
        showLoading();

        mHandler.removeCallbacks(mTimeoutRunnable);
        mHandler.postDelayed(mTimeoutRunnable, 5000);

        if (dataInitOk && !jarInitOk) {
            if (!ApiConfig.get().getSpider().isEmpty()) {
                ApiConfig.get().loadJar(useCacheConfig, ApiConfig.get().getSpider(), new ApiConfig.LoadConfigCallback() {
                    @Override
                    public void success() {
                        mHandler.removeCallbacks(mTimeoutRunnable);
                        jarInitOk = true;
                        mHandler.postDelayed(() -> initData(), 50);
                    }

                    @Override
                    public void notice(String msg) {
                        mHandler.post(() -> Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void error(String msg) {
                        mHandler.removeCallbacks(mTimeoutRunnable);
                        jarInitOk = true;
                        dataInitOk = true;
                        mHandler.postDelayed(() -> {
                            Toast.makeText(HomeActivity.this, msg + "; 尝试加载最近一次的jar", Toast.LENGTH_SHORT).show();
                            initData();
                        }, 50);
                    }
                });
            } else {
                jarInitOk = true;
                initData();
            }
            return;
        }

        ApiConfig.get().loadConfig(useCacheConfig, new ApiConfig.LoadConfigCallback() {
            TipDialog dialog = null;

            @Override
            public void notice(String msg) {
                mHandler.post(() -> Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void success() {
                mHandler.removeCallbacks(mTimeoutRunnable);
                dataInitOk = true;
                if (ApiConfig.get().getSpider().isEmpty()) {
                    jarInitOk = true;
                }
                mHandler.postDelayed(() -> initData(), 50);
            }

            @Override
            public void error(String msg) {
                mHandler.removeCallbacks(mTimeoutRunnable);
                mHandler.post(() -> {
                    dataInitOk = true;
                    jarInitOk = true;
                    showSuccess();
                    refreshEmpty();
                    Toast.makeText(HomeActivity.this, "加载配置失败，请在【设置】中检查接口", Toast.LENGTH_SHORT).show();
                });
            }
        }, this);
    }

    private void initViewPager(AbsSortXml absXml) {
        fragments.clear();
        if (sortAdapter.getData().size() > 0) {
            for (MovieSort.SortData data : sortAdapter.getData()) {
                if (data.id.equals("my0")) {
                    if (Hawk.get(HawkConfig.HOME_REC, 0) == 1 && absXml != null && absXml.videoList != null && absXml.videoList.size() > 0) {
                        fragments.add(UserFragment.newInstance(absXml.videoList));
                    } else {
                        fragments.add(UserFragment.newInstance(null));
                    }
                } else {
                    fragments.add(GridFragment.newInstance(data));
                }
            }
            pageAdapter = new HomePageAdapter(getSupportFragmentManager(), fragments);
            try {
                Field field = ViewPager.class.getDeclaredField("mScroller");
                field.setAccessible(true);
                FixedSpeedScroller scroller = new FixedSpeedScroller(mContext, new AccelerateInterpolator());
                field.set(mViewPager, scroller);
                scroller.setmDuration(300);
            } catch (Exception ignored) {
            }
            mViewPager.setPageTransformer(true, new DefaultTransformer());
            mViewPager.setAdapter(pageAdapter);
            mViewPager.setCurrentItem(currentSelected, false);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBackPressed() {
        if (isLoading()) {
            refreshEmpty();
            return;
        }
        if (HawkConfig.hotVodDelete) {
            HawkConfig.hotVodDelete = false;
            if (UserFragment.homeHotVodAdapter != null) {
                UserFragment.homeHotVodAdapter.notifyDataSetChanged();
            }
            return;
        }

        if (this.fragments.size() <= 0 || this.sortFocused >= this.fragments.size() || this.sortFocused < 0) {
            doExit();
            return;
        }

        BaseLazyFragment baseLazyFragment = this.fragments.get(this.sortFocused);
        if (baseLazyFragment instanceof GridFragment) {
            GridFragment grid = (GridFragment) baseLazyFragment;
            if (grid.restoreView()) {
                return;
            }
            if (this.sortFocusView != null && !this.sortFocusView.isFocused()) {
                this.sortFocusView.requestFocus();
            } else if (this.sortFocused != 0) {
                this.mGridView.setSelection(0);
            } else {
                doExit();
            }
        } else if (baseLazyFragment instanceof UserFragment && UserFragment.tvHotList != null && UserFragment.tvHotList.canScrollVertically(-1)) {
            UserFragment.tvHotList.scrollToPosition(0);
            this.mGridView.setSelection(0);
        } else {
            doExit();
        }
    }

    private void doExit() {
        if (System.currentTimeMillis() - mExitTime < 2000) {
            AppManager.getInstance().finishAllActivity();
            EventBus.getDefault().unregister(this);
            ControlManager.get().stopServer();
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } else {
            mExitTime = System.currentTimeMillis();
            Toast.makeText(mContext, "再按一次返回键退出应用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.post(mRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_PUSH_URL) {
            if (ApiConfig.get().getSource("push_agent") != null) {
                Intent newIntent = new Intent(mContext, DetailActivity.class);
                newIntent.putExtra("id", (String) event.obj);
                newIntent.putExtra("sourceKey", "push_agent");
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                HomeActivity.this.startActivity(newIntent);
            }
        } else if (event.type == RefreshEvent.TYPE_FILTER_CHANGE) {
            if (currentView != null) {
                showFilterIcon((int) event.obj);
            }
        }
    }

    private void showFilterIcon(int count) {
        if (currentView == null) return;
        boolean visible = count > 0;
        View filterColor = currentView.findViewById(R.id.tvFilterColor);
        if (filterColor != null) {
            filterColor.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        View filter = currentView.findViewById(R.id.tvFilter);
        if (filter != null) {
            filter.setVisibility(visible ? View.GONE : View.VISIBLE);
        }
    }

    private final Runnable mDataRunnable = new Runnable() {
        @Override
        public void run() {
            if (sortChange) {
                sortChange = false;
                if (sortFocused != currentSelected) {
                    currentSelected = sortFocused;
                    mViewPager.setCurrentItem(sortFocused, false);
                    changeTop(sortFocused != 0);
                }
            }
        }
    };

    private long menuKeyDownTime = 0;
    private static final long LONG_PRESS_THRESHOLD = 2000;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (topHide < 0)
            return false;
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                menuKeyDownTime = System.currentTimeMillis();
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                long pressDuration = System.currentTimeMillis() - menuKeyDownTime;
                if (pressDuration >= LONG_PRESS_THRESHOLD) {
                    jumpActivity(SettingActivity.class);
                } else {
                    showSiteSwitch();
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    byte topHide = 0;

    private void changeTop(boolean hide) {
        if (topLayout == null) return;
        ViewObj viewObj = new ViewObj(topLayout, (ViewGroup.MarginLayoutParams) topLayout.getLayoutParams());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                topHide = (byte) (hide ? 1 : 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        if (hide && topHide == 0) {
            animatorSet.playTogether(ObjectAnimator.ofObject(viewObj, "marginTop", new IntEvaluator(),
                            AutoSizeUtils.mm2px(this.mContext, 10.0f),
                            AutoSizeUtils.mm2px(this.mContext, 0.0f)),
                    ObjectAnimator.ofObject(viewObj, "height", new IntEvaluator(),
                            AutoSizeUtils.mm2px(this.mContext, 50.0f),
                            AutoSizeUtils.mm2px(this.mContext, 1.0f)),
                    ObjectAnimator.ofFloat(this.topLayout, "alpha", 1.0f, 0.0f));
            animatorSet.setDuration(200);
            animatorSet.start();
            return;
        }
        if (!hide && topHide == 1) {
            animatorSet.playTogether(ObjectAnimator.ofObject(viewObj, "marginTop", new IntEvaluator(),
                            AutoSizeUtils.mm2px(this.mContext, 0.0f),
                            AutoSizeUtils.mm2px(this.mContext, 10.0f)),
                    ObjectAnimator.ofObject(viewObj, "height", new IntEvaluator(),
                            AutoSizeUtils.mm2px(this.mContext, 1.0f),
                            AutoSizeUtils.mm2px(this.mContext, 50.0f)),
                    ObjectAnimator.ofFloat(this.topLayout, "alpha", 0.0f, 1.0f));
            animatorSet.setDuration(200);
            animatorSet.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        ControlManager.get().stopServer();
        // 移除了进程自毁代码，防止 Activity 重启或配置变更时误退出到桌面
    }

    private SelectDialog<SourceBean> mSiteSwitchDialog;

    void showSiteSwitch() {
        List<SourceBean> sites = ApiConfig.get().getSwitchSourceBeanList();
        if (sites == null || sites.isEmpty()) {
            Toast.makeText(mContext, "暂无可切换的数据源，请先在设置中配置接口", Toast.LENGTH_SHORT).show();
            jumpActivity(SettingActivity.class);
            return;
        }
        int select = sites.indexOf(ApiConfig.get().getHomeSourceBean());
        if (select < 0 || select >= sites.size()) select = 0;
        if (mSiteSwitchDialog == null) {
            mSiteSwitchDialog = new SelectDialog<>(HomeActivity.this);
            TvRecyclerView tvRecyclerView = mSiteSwitchDialog.findViewById(R.id.list);
            int spanCount = (int) Math.floor(sites.size() / 20.0);
            spanCount = Math.min(spanCount, 2);
            if (tvRecyclerView != null) {
                tvRecyclerView.setLayoutManager(new V7GridLayoutManager(mSiteSwitchDialog.getContext(), spanCount + 1));
            }
            ConstraintLayout cl_root = mSiteSwitchDialog.findViewById(R.id.cl_root);
            if (cl_root != null) {
                ViewGroup.LayoutParams clp = cl_root.getLayoutParams();
                clp.width = AutoSizeUtils.mm2px(mSiteSwitchDialog.getContext(), 380 + 200 * spanCount);
            }
            mSiteSwitchDialog.setTip("请选择首页数据源");
        }
        mSiteSwitchDialog.setAdapter(new SelectDialogAdapter.SelectDialogInterface<SourceBean>() {
            @Override
            public void click(SourceBean value, int pos) {
                ApiConfig.get().setSourceBean(value);
                refreshHome();
            }
            @Override
            public String getDisplay(SourceBean val) {
                return val != null ? val.getName() : "";
            }
        }, new DiffUtil.ItemCallback<SourceBean>() {
            @Override
            public boolean areItemsTheSame(@NonNull SourceBean oldItem, @NonNull SourceBean newItem) {
                return oldItem == newItem;
            }
            @Override
            public boolean areContentsTheSame(@NonNull SourceBean oldItem, @NonNull SourceBean newItem) {
                return oldItem.getKey().equals(newItem.getKey());
            }
        }, sites, select);
        mSiteSwitchDialog.show();
    }

    private void refreshHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bundle bundle = new Bundle();
        bundle.putBoolean("useCache", true);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void refreshEmpty() {
        skipNextUpdate = true;
        showSuccess();
        SourceBean home = ApiConfig.get().getHomeSourceBean();
        String homeKey = home != null ? home.getKey() : "";
        sortAdapter.setNewData(DefaultConfig.adjustSort(homeKey, new ArrayList<>(), true));
        initViewPager(null);
        tvName.clearAnimation();
    }

    private void tvNameAnimation() {
        AlphaAnimation blinkAnimation = new AlphaAnimation(0.0f, 1.0f);
        blinkAnimation.setDuration(500);
        blinkAnimation.setStartOffset(20);
        blinkAnimation.setRepeatMode(Animation.REVERSE);
        blinkAnimation.setRepeatCount(Animation.INFINITE);
        tvName.startAnimation(blinkAnimation);
    }
}
