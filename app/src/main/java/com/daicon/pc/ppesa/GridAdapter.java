package com.daicon.pc.ppesa;

import android.app.ProgressDialog;
import android.content.Context;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
public class GridAdapter extends ArrayAdapter {
    private static final String TAG = GridAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<Date> monthlyDates;
    private Calendar currentDate;
    private List<EventObjects> allEvents;
    TextView fechaCelda, factor;
    ProgressDialog progressDialog = new ProgressDialog(getContext());
    public GridAdapter(Context context, List<Date> monthlyDates, Calendar currentDate, List<EventObjects> allEvents) {
        super(context, R.layout.single_cell_layout);
        this.monthlyDates = monthlyDates;
        this.currentDate = currentDate;
        this.allEvents = allEvents;
        mInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        progressDialog.show();
        Date mDate = monthlyDates.get(position);

        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(mDate);
        int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCal.get(Calendar.MONTH) + 1;
        int displayYear = dateCal.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);
        View view = convertView;
        if(view == null){
            view = mInflater.inflate(R.layout.single_cell_layout, parent, false);
        }
        if(displayMonth == currentMonth && displayYear == currentYear){
            view.setBackgroundColor(Color.parseColor("#dcd9d9"));
        }else{
            view.setBackgroundColor(Color.parseColor("#cccccc"));
        }
        //Add day to calendar
        TextView cellNumber = view.findViewById(R.id.calendar_date_id);
        fechaCelda = view.findViewById(R.id.event_fecha);
        cellNumber.setText(String.valueOf(dayValue));
        factor = view.findViewById(R.id.lblfactor);
        //Add events to the calendar
        TextView eventIndicator = view.findViewById(R.id.event_id);
        Calendar eventCalendar = Calendar.getInstance();
        for(int i = 0; i < allEvents.size(); i++){
            try {
                Date date=new SimpleDateFormat("dd/MM/yyyy").parse(allEvents.get(i).getDate());

                eventCalendar.setTime(date);
                int test =  eventCalendar.get(Calendar.DAY_OF_MONTH);
                int test2 = eventCalendar.get(Calendar.MONTH) + 1;
                int test3= eventCalendar.get(Calendar.YEAR);
            if(dayValue == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
                    && displayYear == eventCalendar.get(Calendar.YEAR)){
                eventIndicator.setBackgroundColor(Color.parseColor("#CDDC39"));
                eventIndicator.setText("$"+allEvents.get(i).getCosto());
                //Date date=new SimpleDateFormat("dd/MM/yyyy").parse();
                fechaCelda.setText(allEvents.get(i).getDate().toString());
            }


            } catch (ParseException e) {
                e.printStackTrace();
            }
            factor.setText(String.valueOf(allEvents.get(i).getFactorValor()));

        }
        progressDialog.hide();
        return view;
    }
    @Override
    public int getCount() {
        return monthlyDates.size();
    }
    @Nullable
    @Override
    public Object getItem(int position) {
        return monthlyDates.get(position);
    }
    @Override
    public int getPosition(Object item) {
        return monthlyDates.indexOf(item);
    }
}