package com.nphc.service.Employee.CSVAbstractBeans;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

        } catch (Exception e){
            try {
                parsedDate = LocalDate.parse(dateString, formatter2);
            }catch(Exception e2){
                //need to return null to process invalid date logic
                parsedDate = null;
                //throw new DateTimeParseException("Unable to parse date: " + dateString, dateString, 0);
            }
        }
        return parsedDate;
    }
}
