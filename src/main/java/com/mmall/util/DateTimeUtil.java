package com.mmall.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {
	public static final String NORMAL_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static String dateToStr(Date date) {
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(NORMAL_FORMAT);
	}

	public static Date strToDate(String str) {
		DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern(NORMAL_FORMAT);
		return DateTime.parse(str, dateTimeFormat).toDate();
	}
	
}
