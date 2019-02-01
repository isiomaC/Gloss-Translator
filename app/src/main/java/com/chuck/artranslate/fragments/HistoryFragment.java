package com.chuck.artranslate.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chuck.artranslate.R;
import com.chuck.artranslate.activities.MainActivity;
import com.chuck.artranslate.activities.MapsActivity;
import com.chuck.artranslate.adapter.DBResourcesAdapter;
import com.chuck.artranslate.dbresources.DBresources;
import com.chuck.artranslate.dbresources.MyDatabase;
import com.chuck.artranslate.utils.App;
import com.chuck.artranslate.utils.ViewsUtil;

import java.util.List;


public class HistoryFragment extends DynamicFragment<DBresources>  {

    private EditText edt;
    private MenuItem men;

    @Override
    protected void initializeDefaults() {
        setAsLinearLayout();
        hasBottomItemDecoration = false;
        mBroadcastId = "new";
        hasTopItemDecoration = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(R.string.translation_history);
        view.findViewById(R.id.half_logo).setAlpha(0.4f);

        edt = view.findViewById(R.id.query_string);

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                mAdapter.filter(editable.toString());
            }
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        men = menu.findItem(R.id.action_search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_map:
                startActivity(new Intent(mContext, MapsActivity.class));
                break;

            case R.id.btn_sign_out:
                App.SignOut();
                break;

            case R.id.action_search:
                if (ViewsUtil.isVisible(edt)) {
                    men.setIcon(R.drawable.search);
                    ViewsUtil.makeVisible(mToolBarTitle, mContext, R.anim.fade_in);
                    ViewsUtil.makeGone(edt, mContext, R.anim.fade_out);
                    edt.clearFocus();
                } else {
                    men.setIcon(R.drawable.cancel_blue);
                    ViewsUtil.makeGone(mToolBarTitle, mContext, R.anim.fade_out);
                    ViewsUtil.makeVisible(edt, mContext, R.anim.fade_in);
                    edt.requestFocus();
                }
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }


    @Override
    protected void fetchData() {
        updateListView();
    }

    @Override
    protected void createOneTimeVariables() {

        mAdapter = new DBResourcesAdapter() {
            @Override
            public void onItemClick(final View view, final DBresources data, final int position) {
                if (view.getId() == R.id.delete){

                  ViewsUtil.customDialog(mActivity)
                            .setTitle(R.string.Confirm)
                            .setMessage("Are You Sure")
                            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    data.delete();
                                    mAdapter.remove(position);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }).show();

                } else {

                    DBResourcesViewHolder viewHolder =
                            (DBResourcesViewHolder) getRecyclerView().getChildViewHolder(view);

                    if (ViewsUtil.isVisible(viewHolder.CollapseView)) {
                        ViewsUtil.makeGone(viewHolder.CollapseView, mContext, R.anim.fade_out);
                        ViewsUtil.makeVisible(viewHolder.ExpView, mContext, R.anim.fade_in);
                    } else {
                        ViewsUtil.makeGone(viewHolder.ExpView, mContext, R.anim.fade_out);
                        ViewsUtil.makeVisible(viewHolder.CollapseView, mContext, R.anim.fade_in);
                    }

                }
            }


            @Override
            protected void displayContent(DBResourcesViewHolder holder, final DBresources data, int position) {

                super.displayContent(holder, data, position);
                holder.translated.setText(getString(R.string.translated_from_to, data.getSourceText(), data.getDestText()));

                TextView tt;
                for (String s : data.getTranslation().trim().split(" ")) {
                    tt = (TextView) getLayoutInflater().inflate(R.layout.text, holder.toView, false);
                    s = s.trim();
                    if(!s.isEmpty()){
                        tt.setText(s);
                        tt.setTag(data.getDestText());
                        tt.setOnLongClickListener((MainActivity) mActivity);
                        holder.toView.addView(tt);
                    }
                }

            }
        };

    }

    @Override
    protected void onReceiveBroadcast(String action, Intent intent) {

        if (action.equalsIgnoreCase("new")) {
            mAdapter.add((DBresources) intent.getSerializableExtra("a"), 0);
        }
    }

    @Override
    protected List<DBresources> getData() {
        return MyDatabase.getAll();
    }

}
