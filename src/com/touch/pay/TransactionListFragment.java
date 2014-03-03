package com.touch.pay;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TransactionListFragment extends ListFragment {
	
	public static TransactionListFragment newInstance(
			TransactionListFragment fragment, int num) {
		// supply num param as argument
		Bundle args = new Bundle();
		args.putInt("num", num);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.transaction_list_fragment, container, false);
		return view;
	}

}
