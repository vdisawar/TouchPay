package com.touch.pay;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PayActivity extends FragmentActivity implements
		PaymentRecieveDialogFragment.OnPaymentAcceptedListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private static TextView tv;
	private static Activity context;

	// list of transactions
	List<String> transactions = new ArrayList<String>();
	private ArrayAdapter<String> arrayAdapter;
	TransactionListFragment transactionFragment = new TransactionListFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		context = this;

		arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, transactions);
		// bind the array adapter to the fragment
		transactionFragment.setListAdapter(arrayAdapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Check to see that the Activity started due to an Android Beam
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processNFC(getIntent());
			showConfirmationDialog();
		}
	}

	private Fragment showConfirmationDialog() {
		DialogFragment dialog = new PaymentRecieveDialogFragment();
		dialog.show(getSupportFragmentManager(), "Pin Dialog");
		return dialog;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// update the intent for getIntent() in processNFC() in onResume()
		this.setIntent(intent);
	}

	private static NdefRecord recievedRecord;

	void processNFC(Intent intent) {

		// get extra messages from the array of data being sent
		Parcelable[] messages = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage recievedMessage = (NdefMessage) messages[0]; // get only one
																	// message
																	// in
																	// this beam
		recievedRecord = recievedMessage.getRecords()[0];
	}

	@Override
	public String getAmount() {
		if (recievedRecord != null) {
			String amount = new String(recievedRecord.getPayload());
			return amount;
		}
		return " No Money ";
	}

	@Override
	public boolean onPaymentAccepted() {
		// TODO execute the transaction
		Toast.makeText(this, "Payment Accepted!", Toast.LENGTH_LONG).show();
		return true;
	}

	private static boolean paymentDeclined = false;

	@Override
	public boolean onPaymentDeclined() {
		Toast.makeText(this, "Payment Declined!", Toast.LENGTH_LONG).show();
		paymentDeclined = true;
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pay, menu);
		return true;
	}

	public void addTransaction(String timestamp, String sender,
			String receiver, String amount) {
		// TODO call this method after the nfc happened and send to the cloud
		final Handler mHandler = new Handler() {

		};
	}

	private void listTransaction() {
		// TODO get the transaction from the database
		final HandlerThread mHandler = new HandlerThread("Thread-List") {

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.HandlerThread#run()
			 */
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				//String list = ClientServerInterface.receiverTransaction();
				// if(list != null)
				// quitSafely();
			}

		};

		updateListUI();
	}

	private void updateListUI() {
		// TODO add in the list the amount recieved from the NFC
	}

	private String tvText;

	public void numberclick(View view) {
		tv = (TextView) findViewById(R.id.amount);
		tvText = tv.getText().toString();
		if (tvText != null && tvText.contains("$"))
			tv.setText("");
		if (view instanceof Button) {
			Button b = (Button) view;
			if (b.getText().equals("OK")) {
				if(tvText.equals("")){
					Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(this, PinActivity.class);
				intent.putExtra("price", tvText);
				startActivity(intent);
			} else
				tv.setText(tvText.toString() + b.getText());
		} else {
			tv.setText("");
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return PaymentFragment.getInstance(position);
			case 1:
				return TransactionListFragment.newInstance(transactionFragment,
						position);
			case 2:
				return DummySectionFragment.getInstance(position);
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Pay!";
			case 1:
				return "Transaction History";
			case 2:
				return "My Money";
			}
			return null;
		}
	}

	public static class PaymentFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public static PaymentFragment getInstance(int num) {
			PaymentFragment fragment = new PaymentFragment();

			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", num);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.activity_amount, container, false);
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		public static DummySectionFragment getInstance(int num) {
			DummySectionFragment fragment = new DummySectionFragment();
			// Supply num input as an argument.
			Bundle args = new Bundle();
			args.putInt("num", num);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_pay_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			if (recievedRecord != null && paymentDeclined == false) // TODO
																	// change to
																	// ListFragment
				dummyTextView.setText("$"+(new String(recievedRecord.getPayload())));
			return rootView;
		}
	}

}
