package com.example.demo.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter {
    public String convertDateFormat(String date){
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate localDate = LocalDate.parse(date, inputFormatter);
        return localDate.format(outputFormatter);
    }
}
