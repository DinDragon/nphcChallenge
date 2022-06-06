package com.nphc.service.Employee.CustomConverter;

import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String dateString) {
        String datePattern1 = "dd-MMM-yy";
        String datePattern2 = "yyyy-MM-dd";
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(datePattern1);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(datePattern2);
        LocalDate parsedDate = null;
        try {

            parsedDate = LocalDate.parse(dateString, formatter1);

        } catch (Exception e){
            try {
                parsedDate = LocalDate.parse(dateString, formatter2);
            }catch(Exception e2){
                //need to return null to process invalid date logic
                //throw new DateTimeParseException("Unable to parse date: " + dateString, dateString, 0);
            }
        }
        return parsedDate;
    }
}
