package com.touch.pay;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class PaymentRecieveDialogFragment extends DialogFragment {

	private OnPaymentAcceptedListener onPaymentAcceptedListener;
	private OnPaymentAcceptedListener onDeclinePressed;
	private OnPaymentAcceptedListener amount;

	public static PaymentRecieveDialogFragment newInstance(PaymentRecieveDialogFragment fragment,
			int num) {
		// supply num param as argument
		Bundle args = new Bundle();
		args.putInt("num", num);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.payment_recieve_dialog_fragment, null);

		final TextView price = (TextView) view.findViewById(R.id.payment);
		price.append(amount.getAmount() + " dollars.");
		// inflate UI and set the layout for the dialog
		dialog.setView(view)
				.setPositiveButton("Accept",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								onPaymentAcceptedListener.onPaymentAccepted();
							}
						})
				.setNegativeButton("Decline",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								onDeclinePressed.onPaymentDeclined();
								PaymentRecieveDialogFragment.this.getDialog().cancel();//dismisses the dialog
							}
						});

		return dialog.create();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// make sure class that uses this fragment implements the interface
		try {
			onPaymentAcceptedListener = (OnPaymentAcceptedListener) activity;
			onDeclinePressed = (OnPaymentAcceptedListener) activity;
			amount = (OnPaymentAcceptedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnPinEnteredListener");
		}
	}

	// to be called when user enters pin correctly
	public interface OnPaymentAcceptedListener {

		/**
		 * handles the event when the user enters a pin
		 * 
		 * @param pin
		 *            the pin the user entered
		 * @return true if the pin is corrects
		 */
		public boolean onPaymentAccepted();

		/**
		 * handles event when user presses cancel
		 * 
		 * @return
		 */
		public boolean onPaymentDeclined();
		
		public String getAmount();
	}
}
