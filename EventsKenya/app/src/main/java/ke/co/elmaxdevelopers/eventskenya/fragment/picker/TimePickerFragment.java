package ke.co.elmaxdevelopers.eventskenya.fragment.picker;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import ke.co.elmaxdevelopers.eventskenya.utils.DateUtils;

public class TimePickerFragment extends DialogFragment implements OnTimeSetListener{
	
	private static TextView setTime;

	public static TimePickerFragment newInstance(TextView editText){
		TimePickerFragment f = new TimePickerFragment();
		setTime = editText;
		return f;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		return new TimePickerDialog(getActivity(), getTheme(), this, hour, minute,false);
	}
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		setTime.setText(DateUtils.formatTime(hourOfDay, minute));

	}
}
