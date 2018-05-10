package com.edu.uni.augsburg.uniatron.domain.converter;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DateConverterTest {

    @Test
    public void fromTimestamp() {
        final Date date = new Date();

        final Date result = DateConverter.fromTimestamp(date.getTime());

        assertThat(result, is(equalTo(date)));
    }

    @Test
    public void dateToTimestamp() {
        final Date date = new Date();

        final Date result = new Date(DateConverter.dateToTimestamp(date));

        assertThat(result, is(equalTo(date)));
    }
}