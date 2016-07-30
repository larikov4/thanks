package com.komandda.entity.json;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateWithoutTimeZoneJsonDeserializer extends JsonDeserializer<Date> {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private final Logger logger = LoggerFactory.getLogger(DateWithoutTimeZoneJsonDeserializer.class);

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
//        SIMPLE_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return  SIMPLE_DATE_FORMAT.parse(jp.getText());
        } catch (ParseException e) {
            logger.error("Date parse exception: " + jp.getText(), e);
            return null;
        }
    }
}