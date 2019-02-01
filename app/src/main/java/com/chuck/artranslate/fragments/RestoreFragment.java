package com.chuck.artranslate.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chuck.artranslate.R;
import com.chuck.artranslate.utils.MyBroadcastReceiver;

public abstract class RestoreFragment extends Fragment {

    protected AppCompatActivity mActivity;
    protected Context mContext;
    protected MyBroadcastReceiver mBroadcastReceiver;
    protected String[] mBroadcastIds;
    protected String mBroadcastId;

    protected Toolbar mToolBar;
    protected TextView mToolBarTitle;
    protected ViewGroup mRootView;

    protected boolean mHasOptionsMenu = true,
            mActionBarEnabled = true;

    private boolean mIsReturningFromBackStack = false,
            mReturnedFromBackStack = false;

    protected int mLayoutRes = R.layout.fragment_default,
            mMenuRes = R.menu.menu_main;

    protected LayoutInflater mInflater;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();
        mContext = getContext();
        initializeDefaults();
    }

    protected abstract void initializeDefaults();

    @Override
    public void onResume() {
        super.onResume();
        MyBroadcastReceiver.register(mBroadcastReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MyBroadcastReceiver.unregister(mBroadcastReceiver);
        mIsReturningFromBackStack = true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(mMenuRes, menu);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mInflater = inflater;
        if ((mRootView = (ViewGroup) getView()) == null) {
            mRootView = (ViewGroup) inflater.inflate(mLayoutRes, container, false);
        }

        mToolBar = mRootView.findViewById(R.id.toolbar);
        if (mToolBar != null) {
            mToolBarTitle = mToolBar.findViewById(R.id.toolbar_title);
            if (mActionBarEnabled) {
                mActivity.setSupportActionBar(mToolBar);
            }
        }

        setHasOptionsMenu(mActionBarEnabled && mHasOptionsMenu);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        boolean restoreInstance = savedInstanceState != null;
        if (restoreInstance) {
            onRestoreInstanceState(savedInstanceState);
        }

        if (!isReturningFromBackStack()) {
            createBroadcastReceiver();
            createOneTimeVariables();
            if (restoreInstance) {
                restoreInstanceViewState();
            } else {
                createInstanceViewState();
            }
        }

        inBetweenCallbacksCall();

        mReturnedFromBackStack = mIsReturningFromBackStack;
        if (mIsReturningFromBackStack) {
            onReturnFromBackStack();
            mIsReturningFromBackStack = false;
        }

    }

    private void createBroadcastReceiver() {
        if (mBroadcastIds != null && mBroadcastIds.length > 0) {
            mBroadcastReceiver = new MyBroadcastReceiver(mBroadcastIds) {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction() != null) {
                        onReceiveBroadcast(intent.getAction(), intent);
                    }
                }
            };
        } else if (mBroadcastId != null && !mBroadcastId.isEmpty()) {
            mBroadcastReceiver = new MyBroadcastReceiver(mBroadcastId) {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction() != null) {
                        onReceiveBroadcast(intent.getAction(), intent);
                    }
                }
            };
        }
    }

    protected void createInstanceViewState() { }

    protected void restoreInstanceViewState() { }

    protected void onReturnFromBackStack() { }

    protected abstract void createOneTimeVariables();

    protected void inBetweenCallbacksCall() { }

    protected abstract void onReceiveBroadcast(String action, Intent intent);

    protected void onRestoreInstanceState(Bundle savedInstanceState) { }

    public final boolean isReturningFromBackStack() {
        return mIsReturningFromBackStack || mReturnedFromBackStack;
    }

    protected final void setTitle(@StringRes int title) {
        mToolBarTitle.setText(title);
    }

    protected final void setTitle(String title) {
        mToolBarTitle.setText(title);
    }

}
