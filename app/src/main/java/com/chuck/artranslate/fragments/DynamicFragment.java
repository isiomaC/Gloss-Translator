package com.chuck.artranslate.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.chuck.artranslate.R;
import com.chuck.artranslate.adapter.DataAdapter;
import com.chuck.artranslate.utils.ViewsUtil;

import java.util.List;

abstract public class DynamicFragment<D> extends RestoreFragment {

    protected DataAdapter<D, ?> mAdapter;

    protected int mGridColumnsCount = 2,
            mVerticalSpacing = 50,
            mEmptyViewTextRes = 0;

    protected boolean mIsLinearLayout = false,
            mHasFixedSize = false,
            hasLeftItemDecoration = true,
            hasTopItemDecoration = true,
            hasRightItemDecoration = true,
            hasBottomItemDecoration = true,
            clearLastItemDecoration = false,
            clearFirstItemDecoration = false;

    private boolean mIsRefreshing = true;

    public class VerticalSpacingDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

            int position = parent.getChildLayoutPosition(view);
            boolean last = position == mAdapter.getItemCopyCount() - 1;

            if (mIsLinearLayout) {

                if (hasLeftItemDecoration) {
                    outRect.left = mVerticalSpacing;
                }
                if (hasTopItemDecoration && !(clearFirstItemDecoration && position == 0)) {
                    outRect.top = mVerticalSpacing;
                }
                if (hasRightItemDecoration) {
                    outRect.right = mVerticalSpacing;
                }
                if (last && !clearLastItemDecoration) {
                    outRect.bottom = mVerticalSpacing;
                }

            } else {
                boolean even = position % 2 == 0;
                if (position < 2) {
                    outRect.top = mVerticalSpacing;
                }
                if (even) {
                    outRect.left = mVerticalSpacing;
                    outRect.right = mVerticalSpacing/2;
                } else {
                    outRect.right = mVerticalSpacing;
                    outRect.left = mVerticalSpacing/2;
                }
                outRect.bottom = mVerticalSpacing;
            }

        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //Restore the fragment's state here
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mLayoutRes = R.layout.fragment_default_header;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getRecyclerView().setAdapter(null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        TextView emptyView = getEmptyView();
        if (emptyView != null) {
            emptyView.setText(mEmptyViewTextRes);
        }

        if (isReturningFromBackStack()) {
            showProgressBar(true);
        }

        return mRootView;
    }

    protected final ViewGroup getHeader() {
        return mRootView.findViewById(R.id.header_view);
    }

    protected final void showProgressBar(boolean display) {
        if (display) {
            ViewsUtil.makeVisible(getProgressBar());
        } else {
            ViewsUtil.makeInvisible(getProgressBar());
        }
    }

    private View getProgressBar() {
        return mRootView.findViewById(R.id.progressBar);
    }


    private TextView getEmptyView() {
        return mRootView.findViewById(android.R.id.empty);
    }

    @Override
    protected void inBetweenCallbacksCall() {
        getRecyclerView().setAdapter(mAdapter);
    }

    @Override
    protected void restoreInstanceViewState() {
        fetchData();
    }


    protected final void showEmptyView(boolean show) {
        if (show) {
            ViewsUtil.makeVisible(getEmptyView());
            ViewsUtil.makeGone(getRecyclerView());
        } else {
            ViewsUtil.makeInvisible(getEmptyView());
            ViewsUtil.makeVisible(getRecyclerView());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = getRecyclerView();
        NestedScrollView nestedScrollView = getNestedScrollView();
        recyclerView.setLayoutManager(getLayoutManager(nestedScrollView));
        recyclerView.addItemDecoration(new VerticalSpacingDecoration());
        recyclerView.setHasFixedSize(mHasFixedSize);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setItemViewCacheSize(30);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

    }

    protected final RecyclerView getRecyclerView() {
        return mRootView.findViewById(R.id.recycler);
    }

    protected final void setAsLinearLayout() {
        mIsLinearLayout = true;
    }

    protected final NestedScrollView getNestedScrollView() {
        return mRootView.findViewById(R.id.nestedscrollview);
    }

    private LinearLayoutManager getLayoutManager(final NestedScrollView nestedScrollView) {
        if (mIsLinearLayout) {
            return new LinearLayoutManager(mContext) {
                @Override
                public boolean canScrollHorizontally() {
                    return nestedScrollView == null;
                }
                @Override
                public boolean canScrollVertically() {
                    return nestedScrollView == null;
                }
            };
        } else {
            return new GridLayoutManager(mContext, mGridColumnsCount) {
                @Override
                public boolean canScrollHorizontally() {
                    return nestedScrollView == null;
                }
                @Override
                public boolean canScrollVertically() {
                    return nestedScrollView == null;
                }
            };
        }
    }

    @Override
    protected void createInstanceViewState() {
        if (!isReturningFromBackStack()) {
            fetchData();
        }
    }


    protected abstract void fetchData();

    protected abstract List<D> getData();

    protected final void updateListView() {

        onNotifyDataSetChanged(getData());

        showProgressBar(false);
        showEmptyView(mAdapter.getItemCopyCount() == 0);
    }


    protected void onNotifyDataSetChanged(List<D> dataSet) {
        if (isRefreshing()) {
            mAdapter.replaceAll(dataSet);
        } else {
            mAdapter.addAll(dataSet);
        }
    }

    public final boolean isRefreshing() {
        return mIsRefreshing;
    }

}
