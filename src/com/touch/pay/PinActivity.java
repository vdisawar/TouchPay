package com.touch.pay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PinActivity extends Activity {
	private TextView pinView;
	private TextView tv;

	private static boolean pinSuccess = false;
	private SharedPreferences settings;
	private String correctpin;
	private String price;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finalpincheck);

		pinView = (TextView) findViewById(R.id.pinnum);
		pinView.setInputType(InputType.TYPE_NULL);
		tv = (TextView) findViewById(R.id.amount2);

		price = getIntent().getStringExtra("price");
		tv.setText("$" + price);

		settings = getSharedPreferences("com.touch.pay", MODE_PRIVATE);
		correctpin = settings.getString("pin", "--unavilable--");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pin, menu);
		return true;
	}

	public static boolean getPinSuccess() {
		return pinSuccess;
	}

	private String pin = "";
	private int touchCounter = 0;

	public void numberclick(View view) {
		if (view instanceof Button) {
			Button b = (Button) view;
			
			if (b.getText().equals("OK")) {
				if(pin.equals("")){
					Toast.makeText(this, "Please enter a 4-digit pin", Toast.LENGTH_SHORT).show();
					return;
				}
				if (correctpin.equals(pin)) {
					pinSuccess = true;
					Intent intent = new Intent(this, TransferActivity.class);
					intent.putExtra("price", price);
					startActivity(intent);
				} else {
					Toast.makeText(this, "Wrong pin, please try again.",
							Toast.LENGTH_SHORT).show();
				}
				
			} else if (touchCounter <= 3) {
				pin += b.getText().toString();
				touchCounter++;
				if (touchCounter == 1)
					pinView.setText("*");
				else if (touchCounter == 2)
					pinView.setText("* *");
				else if (touchCounter == 3)
					pinView.setText("* * *");
				else 
					pinView.setText("* * * *");
			}
		} else { //if the user presses the back button reset everything
			pinView.setText("");
			pin = "";
			touchCounter = 0;
		}
	}

	public void send() {
		pinView.setVisibility(View.GONE);
	}

}
