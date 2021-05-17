package com.example.rent.view;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.rent.FireHelper;
import com.example.rent.R;
import com.example.rent.adapter.TimePickerRecyclerAdapter;
import com.example.rent.databinding.ActivityTerrainReservationBinding;
import com.example.rent.databinding.TimePickerCustomLayoutBinding;
import com.example.rent.model.Reservation;
import com.example.rent.model.Terrain;
import com.example.rent.viewmodel.ReservationViewModel;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;

import java.security.SecureRandom;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.rent.ConstantsUtils.sUserGlobalModel;

@RequiresApi(api = Build.VERSION_CODES.N)
public class TerrainReservationActivity extends AppCompatActivity {

    public static final String TIME_FORMAT_RESERVATION_HOUR = "HH:mm";
    private static final String SPACE = " ";
    private static final String EMPTY_SPACE = "";
    private static final String ERROR_MESSAGE = "Error message";
    public static final String TIMESTAMP_FORMAT_DATE = "dd/MM/yyyy";
    public static final String DMY_FORMAT = "%d/%d/%d";
    public static final long DAY = 1000 * 60 * 60 * 24;

    private ActivityTerrainReservationBinding mActivityTerrainReservationBinding;

    private DatePickerDialog mDatePickerDialog;


    public static final String TWO_POINTS = " : ";
    private boolean mIsWeekend = false;

    private boolean mCanReserve = true;

    private boolean mWrongTime = false;

    private TimePickerRecyclerAdapter mHoursAdapter;

    private TimePickerRecyclerAdapter mMinutesAdapter;

    private Date mStartHourTV;

    private Date mEndHourTV;

    private List<Reservation> mReservations;

    private Date mCurrentTime;

    private int mPosition = 0;

    private int mStartHour = 0;

    private int mEndHour = 0;

    private Terrain mTerrain;

    private ReservationViewModel mReservationViewModel;

    private FirebaseAuth mFirebaseAuth;

    private static final String[] HOURS_LIST_START = new String[]{"08", "09", "10", "11", "12", "13", "14",
            "15", "16", "17", "18"};

    private static final String[] MINUTES_LIST = new String[]{"00", "30"};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityTerrainReservationBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_terrain_reservation);
        mReservations = new ArrayList<>();
        mFirebaseAuth = FireHelper.getInstanceOfAuth();
        Intent intent = this.getIntent();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        System.out.println("-----------------> user " + userId);
        if (intent.hasExtra("TERRAIN_OBJECT")) {
            mTerrain = (Terrain) intent.getSerializableExtra("TERRAIN_OBJECT");
            mReservationViewModel = new ViewModelProvider(this).get(ReservationViewModel.class);
            mReservationViewModel.getAllReservationsByTerrainId(mTerrain.getId())
                    .observe(this, reservations -> {
                        mReservations.clear();
                        mReservations.addAll(reservations);
                        setReservationButton();
                    });
        }

        Calendar calendar = Calendar.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerDayPicker(calendar);
        }

        registerStartTimePicker();
        registerEndTimePicker();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registerDayPicker(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mActivityTerrainReservationBinding.buttonCalendarDate.setOnClickListener(v -> {
            mDatePickerDialog = new DatePickerDialog(this,
                    R.style.datepicker, (view, year1, month1, dayOfMonth) -> {
                LocalDate localDate = LocalDate.of(year1, month1 + 1, dayOfMonth);
                DayOfWeek dayOfWeek = DayOfWeek.of(localDate.get(ChronoField.DAY_OF_WEEK));
                mIsWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
                if (mIsWeekend) {
                    Toast.makeText(this, R.string.no_reservations_for_weekend, Toast.LENGTH_SHORT).show();
                    mActivityTerrainReservationBinding.buttonReserve.setEnabled(false);
                    mActivityTerrainReservationBinding.buttonReserve.setVisibility(View.GONE);
                } else {
                    mActivityTerrainReservationBinding.buttonReserve.setEnabled(true);
                    mActivityTerrainReservationBinding.buttonReserve.setVisibility(View.VISIBLE);
                }
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT_DATE);
                String dateStringFormat = String.format(Locale.ENGLISH, DMY_FORMAT,
                        dayOfMonth, month1 + 1, year);
                try {
                    Date date = format.parse(dateStringFormat);
                    if (date != null) {
                        mActivityTerrainReservationBinding.textViewChosenDate.setText(format.format(date));
                    }
                } catch (ParseException e) {
                    if (BuildConfig.DEBUG) {
                        Log.e("TAG", ERROR_MESSAGE + e);
                    }
                }
            }, year, month, day);
            mDatePickerDialog.show();
            long mCurrentTime = System.currentTimeMillis() - 1000;

            mDatePickerDialog.getDatePicker().setMinDate(mCurrentTime);
            mDatePickerDialog.getDatePicker().setMaxDate(mCurrentTime + (DAY * 30));

        });
    }


    @SuppressLint("SetTextI18n")
    public void registerStartTimePicker() {
        mActivityTerrainReservationBinding.buttonHourStart.setOnClickListener(v -> {
            final Dialog timePickerCustomDialog = new Dialog(this);
            final TimePickerCustomLayoutBinding timePickerCustomLayoutBinding = DataBindingUtil
                    .inflate(LayoutInflater.from(
                            timePickerCustomDialog.getContext()), R.layout.time_picker_custom_layout,
                            null, false);
            timePickerCustomDialog.setContentView(timePickerCustomLayoutBinding.getRoot());
            timePickerCustomDialog.show();
            //set up minutes recycler view
            LinearLayoutManager minutesLinearLayout = new LinearLayoutManager(this);
            timePickerCustomLayoutBinding.rvMinutes.setLayoutManager(minutesLinearLayout);
            mMinutesAdapter = new TimePickerRecyclerAdapter(Arrays.asList(MINUTES_LIST));
            timePickerCustomLayoutBinding.rvMinutes.setAdapter(mMinutesAdapter);
            //set up hours recycler view
            LinearLayoutManager hoursLinearLayout = new LinearLayoutManager(this);
            timePickerCustomLayoutBinding.rvHours.setLayoutManager(hoursLinearLayout);
            List<String> startHours = new ArrayList<>(Arrays.asList(HOURS_LIST_START));
            startHours.remove(startHours.size() - 1);
            mHoursAdapter = new TimePickerRecyclerAdapter(startHours);
            timePickerCustomLayoutBinding.rvHours.setAdapter(mHoursAdapter);
            timePickerCustomLayoutBinding.rvHours.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerViewHours, int newState) {
                    super.onScrollStateChanged(recyclerViewHours, newState);
                    timePickerCustomLayoutBinding.chosenHour.setText(Arrays.asList(HOURS_LIST_START)
                            .get(hoursLinearLayout.findLastVisibleItemPosition()));
                }
            });
            timePickerCustomLayoutBinding.rvMinutes.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerViewMinutes, int newState) {
                    super.onScrollStateChanged(recyclerViewMinutes, newState);
                    timePickerCustomLayoutBinding.chosenMinute.setText(Arrays.asList(MINUTES_LIST)
                            .get(minutesLinearLayout.findLastVisibleItemPosition()));
                }
            });
            timePickerCustomDialog.setCancelable(false);
            timePickerCustomLayoutBinding.chooseHourButton.setOnClickListener(view -> {
                String finalStartHour = Integer.parseInt(timePickerCustomLayoutBinding.chosenHour
                        .getText().toString()) + TWO_POINTS + timePickerCustomLayoutBinding.chosenMinute
                        .getText().toString();
                mStartHour = Integer.parseInt(timePickerCustomLayoutBinding.chosenHour.getText().toString());
                if (!mActivityTerrainReservationBinding.endHourTV.getText().toString().equals(EMPTY_SPACE)) {
                    if (mStartHour >= mEndHour) {
                        mActivityTerrainReservationBinding.endHourTV.setText(EMPTY_SPACE);
                        timePickerCustomLayoutBinding.chooseHourButton.performClick();
                    } else {
                        mActivityTerrainReservationBinding.startHourTV.setText(finalStartHour);
                        timePickerCustomDialog.dismiss();
                    }
                } else {
                    mActivityTerrainReservationBinding.startHourTV.setText(finalStartHour);
                    timePickerCustomDialog.dismiss();
                }
            });
            timePickerCustomLayoutBinding.cancelChooseHour.setOnClickListener(view -> timePickerCustomDialog
                    .dismiss());
        });
    }

    @SuppressLint("SetTextI18n")
    public void registerEndTimePicker() {
        mActivityTerrainReservationBinding.buttonHourEnd.setOnClickListener(v -> {
            final Dialog timePickerCustomDialog = new Dialog(this);
            final TimePickerCustomLayoutBinding timePickerCustomLayoutBindingEnd = DataBindingUtil
                    .inflate(LayoutInflater.from(
                            timePickerCustomDialog.getContext()), R.layout.time_picker_custom_layout,
                            null, false);
            timePickerCustomDialog.setContentView(timePickerCustomLayoutBindingEnd.getRoot());
            timePickerCustomDialog.show();
            //set up minutes recycler view
            LinearLayoutManager minutesLinearLayout = new LinearLayoutManager(this);
            timePickerCustomLayoutBindingEnd.rvMinutes.setLayoutManager(minutesLinearLayout);
            mMinutesAdapter = new TimePickerRecyclerAdapter(Arrays.asList(MINUTES_LIST));
            timePickerCustomLayoutBindingEnd.rvMinutes.setAdapter(mMinutesAdapter);
            //set up hours recycler view for the end hour
            for (int i = 0; i < HOURS_LIST_START.length; i++) {
                int value = Integer.parseInt(HOURS_LIST_START[i]);
                if (mStartHour == Integer.parseInt(HOURS_LIST_START[HOURS_LIST_START.length - 1])) {
                    mPosition = 0;
                } else if (value == mStartHour
                        && Integer.parseInt(HOURS_LIST_START[i + 0]) != 1) {
                    mPosition = i + 1;
                }
            }
            LinearLayoutManager hoursLinearLayout = new LinearLayoutManager(this);
            timePickerCustomLayoutBindingEnd.rvHours.setLayoutManager(hoursLinearLayout);
            mHoursAdapter = new TimePickerRecyclerAdapter(Arrays.asList(HOURS_LIST_START)
                    .subList(mPosition, HOURS_LIST_START.length));
            timePickerCustomLayoutBindingEnd.rvHours.setAdapter(mHoursAdapter);
            timePickerCustomLayoutBindingEnd.chosenHour.setText(Arrays.asList(HOURS_LIST_START)
                    .subList(mPosition, HOURS_LIST_START.length).get(0));
            timePickerCustomLayoutBindingEnd.rvHours.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerViewHours, int newState) {
                    super.onScrollStateChanged(recyclerViewHours, newState);
                    timePickerCustomLayoutBindingEnd.chosenHour.setText(Arrays.asList(HOURS_LIST_START)
                            .subList(mPosition, HOURS_LIST_START.length)
                            .get(hoursLinearLayout.findLastVisibleItemPosition()));
                }
            });
            timePickerCustomLayoutBindingEnd.rvMinutes.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerViewMinutes, int newState) {
                    super.onScrollStateChanged(recyclerViewMinutes, newState);
                    timePickerCustomLayoutBindingEnd.chosenMinute.setText(Arrays.asList(MINUTES_LIST)
                            .get(minutesLinearLayout.findLastVisibleItemPosition()));
                }
            });
            timePickerCustomDialog.setCancelable(false);
            timePickerCustomLayoutBindingEnd.chooseHourButton.setOnClickListener(view -> {
                String finalEndHour = Integer.parseInt(timePickerCustomLayoutBindingEnd.chosenHour.getText()
                        .toString()) + TWO_POINTS + timePickerCustomLayoutBindingEnd.chosenMinute
                        .getText().toString();
                mEndHour = Integer.parseInt(timePickerCustomLayoutBindingEnd.chosenHour.getText().toString());
                if (!mActivityTerrainReservationBinding.startHourTV.getText().toString().equals(EMPTY_SPACE)) {
                    if (mActivityTerrainReservationBinding.startHourTV.getText().equals(finalEndHour)) {
                        Toast.makeText(this, R.string.time_restriction,
                                Toast.LENGTH_SHORT).show();
                    } else if (checkIntervalBetweenHours(mActivityTerrainReservationBinding.startHourTV.getText()
                            .toString(), finalEndHour)) {
                        Toast.makeText(this, R.string.reservation_should_start_before_it_ends,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        mActivityTerrainReservationBinding.endHourTV.setText(finalEndHour);
                        timePickerCustomDialog.dismiss();
                    }
                } else {
                    mActivityTerrainReservationBinding.endHourTV.setText(finalEndHour);
                    timePickerCustomDialog.dismiss();
                }
            });
            timePickerCustomLayoutBindingEnd.cancelChooseHour
                    .setOnClickListener(view -> timePickerCustomDialog.dismiss());
        });
    }

    private boolean checkIntervalBetweenHours(String startHour, String endHour) {
        List<String> startHourArray = new ArrayList<>(Arrays
                .asList(startHour.split(TWO_POINTS)));
        List<String> endHourArray = new ArrayList<>(Arrays
                .asList(endHour.split(TWO_POINTS)));
        if (Integer.parseInt(startHourArray.get(0)) > Integer.parseInt(endHourArray.get(0))) {
            return true;
        } else if (Integer.parseInt(startHourArray.get(0)) == Integer.parseInt(endHourArray.get(0))) {
            return Integer.parseInt(startHourArray.get(1)) == 30 && Integer.parseInt(endHourArray.get(1))
                    == 0;
        }
        return false;
    }

    @SuppressLint("SimpleDateFormat")
    private void setReservationButton() {
        mActivityTerrainReservationBinding.buttonReserve.setOnClickListener(view -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT_RESERVATION_HOUR);
            mCanReserve = true;
            mWrongTime = true;
            try {
                mStartHourTV = dateFormat.parse(mActivityTerrainReservationBinding.startHourTV.getText()
                        .toString().replace(SPACE, EMPTY_SPACE));
                mEndHourTV = dateFormat.parse(mActivityTerrainReservationBinding.endHourTV.getText()
                        .toString().replace(SPACE, EMPTY_SPACE));
            } catch (ParseException e) {
                Log.e(ERROR_MESSAGE, getString(R.string.err_parsing_hour));
            }
            if (mActivityTerrainReservationBinding.textViewChosenDate.getText().toString()
                    .equals(EMPTY_SPACE)) {
                Toast.makeText(this, R.string.choose_date_for_reservation, Toast.LENGTH_SHORT).show();
                return;
            }
            for (Reservation reservation : mReservations) {
                if (reservation.getUserId().equals(mFirebaseAuth.getCurrentUser().getUid()) &&
                        reservation.getDate()
                                .equals(mActivityTerrainReservationBinding.textViewChosenDate.getText().toString())) {
                    try {
                        Date startHourBD = dateFormat.parse(reservation.getStartHour().replace(SPACE, EMPTY_SPACE));
                        Date endHourBD = dateFormat.parse(reservation.getEndHour().replace(SPACE, EMPTY_SPACE));

                        if (mStartHourTV != null && mStartHourTV.before(endHourBD) && mStartHourTV.after(startHourBD)) {
                            mCanReserve = false;
                        }
                        if (mStartHourTV != null && mStartHourTV.equals(startHourBD)
                                || mEndHourTV != null && mEndHourTV.equals(endHourBD)) {
                            mCanReserve = false;
                        }
                        if (mEndHourTV != null && mEndHourTV.before(endHourBD) && mEndHourTV.after(startHourBD)) {
                            mCanReserve = false;
                        }
                        if (mStartHourTV != null
                                && mStartHourTV.before(startHourBD)
                                && mEndHourTV != null && mEndHourTV.after(endHourBD)) {
                            mCanReserve = false;
                        }
                    } catch (ParseException e) {
                        Log.e(ERROR_MESSAGE, getString(R.string.err_parsing_hour));
                    }
                }
            }
            String currentDate = new SimpleDateFormat(TIMESTAMP_FORMAT_DATE).format(new Date());
            if (currentDate.equals(mActivityTerrainReservationBinding.textViewChosenDate
                    .getText().toString())) {
                if (mCurrentTime.getHours() > mEndHourTV.getHours()) {
                    mWrongTime = false;
                } else if (mCurrentTime.getHours() == mEndHourTV.getHours() &&
                        mCurrentTime.getMinutes() > mEndHourTV.getMinutes()) {
                    mWrongTime = false;
                }
                if (mCurrentTime.getHours() > mStartHourTV.getHours()) {
                    mWrongTime = false;
                } else if (mCurrentTime.getHours() == mStartHourTV.getHours() &&
                        mCurrentTime.getMinutes() > mStartHourTV.getMinutes()) {
                    mWrongTime = false;
                }
            }
            if (!mWrongTime) {
                Toast.makeText(this, R.string.wrong_time, Toast.LENGTH_SHORT).show();
            } else if (!mCanReserve) {
                Toast.makeText(this, R.string.already_added_Reservation_err_message,
                        Toast.LENGTH_SHORT).show();
            } else if (mActivityTerrainReservationBinding.textViewChosenDate.getText().toString().equals(EMPTY_SPACE)) {
                Toast.makeText(this, R.string.choose_date_for_reservation, Toast.LENGTH_SHORT).show();
            } else if (checkIntervalBetweenHours(mActivityTerrainReservationBinding.startHourTV.getText().toString(),
                    mActivityTerrainReservationBinding.endHourTV.getText().toString())) {
                Toast.makeText(this, R.string.reservation_should_start_before_it_ends,
                        Toast.LENGTH_SHORT).show();
            } else {

                Reservation reservation = new Reservation(
                        mActivityTerrainReservationBinding.textViewChosenDate.getText().toString(),
                        mActivityTerrainReservationBinding.startHourTV.getText().toString(),
                        mActivityTerrainReservationBinding.endHourTV.getText().toString(),
                        mTerrain.getId(), mFirebaseAuth.getCurrentUser().getUid());

                mReservationViewModel.addReservation(reservation);
                Intent intent = new Intent(this, TerrainDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("TERRAIN_ITEM", mTerrain);
                startActivity(intent);
            }
            mCanReserve = true;
        });
    }
}