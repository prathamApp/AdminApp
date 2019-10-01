package com.pratham.admin.activities.replaceTab;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratham.admin.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class Fragment_ChooseAssignOrReturn extends Fragment {
    private OperationListener operationListener;
    private String LoggedcrlId;
    private String LoggedcrlName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        operationListener = (OperationListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoggedcrlId = getArguments().getString("CRLid");
        LoggedcrlName = getArguments().getString("CRLname");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assign_or_rplace, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.collect)
    public void onCollectClick() {
        operationListener.onOperationSelect(CollectFragment.newInstance(LoggedcrlId, LoggedcrlName));
    }

    @OnClick({R.id.assign})
    public void onAssignClick() {
        operationListener.onOperationSelect(AssignFragment.newInstance(LoggedcrlId, LoggedcrlName));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        operationListener = null;
    }
}

interface OperationListener {
    public void onOperationSelect(Fragment fragment);
}