package ke.co.elmaxdevelopers.eventskenya.fragment.picker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment  implements DatePickerDialog.OnDateSetListener {

	private static TextView timeField;

	public static DatePickerFragment newInstance (TextView timeTextView){
		DatePickerFragment f = new DatePickerFragment();
		timeField = timeTextView;
		return f;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(getActivity(), getTheme(), this, year, month, day);
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		timeField.setText(formatDate(year, monthOfYear, dayOfMonth));
	}
	
	private String formatDate(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(0);
		cal.set(year, month, day);
		Date date = cal.getTime();
		return DateFormat.getDateInstance().format(date);
	}
}
