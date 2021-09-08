package com.dengetelekom.telsiz.helpers;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    private String formattedDate;

    public DateConverter(String date) {
        this.format(date);
    }

    private void format(String dateStr) {
        try {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = dt.parse(dateStr);
            SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            formattedDate = dt1.format(date);

        } catch (ParseException e) {
            try {
                SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Date date = dt.parse(dateStr);
                SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                formattedDate = dt1.format(date);
            } catch (ParseException parseException) {
                formattedDate = dateStr;
            }
        }
    }

    public String date() {
        return formattedDate;
    }
}
