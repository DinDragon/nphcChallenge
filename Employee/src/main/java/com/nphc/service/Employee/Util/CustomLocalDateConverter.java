package com.nphc.service.Employee.Util;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateConverter extends AbstractBeanField {
    String datePattern1 = "dd-MMM-yy";
    String datePattern2 = "yyyy-MM-dd";
    @Override
    protected Object convert(String dateString) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern(datePattern1);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(datePattern2);
        LocalDate parsedDate = null;
        try {

            parsedDate = LocalDate.parse(dateString, formatter1);

        } catch (DateTimeParseException e){
            try {
                parsedDate = LocalDate.parse(dateString, formatter2);
            }catch(DateTimeParseException e2){
                throw new CsvConstraintViolationException("Unable to parse date: " + dateString);
            }
        }
        return parsedDate;
    }
}
