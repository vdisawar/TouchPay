package com.touch.pay;

import java.nio.charset.Charset;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.Toast;

public class TransferActivity extends Activity implements
		CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	private NfcAdapter nfcAdapter;

	private static final int MESSAGE_SENT = 1;
	private boolean nfcSuccess = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transfer);

		// get the adapter for NFC
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (nfcAdapter == null) {
			Toast.makeText(this, "NFC is unavailable", Toast.LENGTH_LONG)
					.show();
			Intent intent = new Intent(TransferActivity.this, PayActivity.class);
			startActivity(intent);
		} else { // create the callback if NFC is present
			nfcAdapter.setNdefPushMessageCallback(this, this);
			// Register callback to listen for message-sent success
			nfcAdapter.setOnNdefPushCompleteCallback(this, this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent arg0) {
		String payload = getIntent().getStringExtra("price");
		String mimeType = "application/com.touch.pay";
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));

		// create an NdefRecord that holds the data payload to transfer to
		// target phone
		NdefRecord[] record = new NdefRecord[] {
				new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, // the type
																		// of
																		// the
																		// data
						new byte[0], // the ID of the payload
						payload.getBytes()), // the payload in byte array
				// TODO add tokens for extra payload
						
				// create the android application record to go to play store if
				// user doesn't have app
				NdefRecord.createApplicationRecord("com.touch.pay")

		};

		// encapsulate data in message object
		NdefMessage message = new NdefMessage(record);
		return message;
	}

	/** This handler receives a message from onNdefPushComplete */
	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SENT:
				Toast.makeText(TransferActivity.this, "Payment Sent!",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(TransferActivity.this,
						PayActivity.class);
				startActivity(intent);
				break;
			}
		}
	};

	/**
	 * Implementation for the OnNdefPushCompleteCallback interface
	 */
	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		// A handler is needed to send messages to the activity when this
		// callback occurs, because it happens from a binder thread
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.transfer, menu);
		return true;
	}

}