package com.mmall.util;

import java.math.BigDecimal;

public class PriceUtil {
	
	public static BigDecimal add(double v1, double v2) {
		BigDecimal d1 = new BigDecimal(String.valueOf(v1));
		BigDecimal d2 = new BigDecimal(String.valueOf(v2));
		return d1.add(d2);
	}
	
	public static BigDecimal sub(double v1, double v2) {
		BigDecimal d1 = new BigDecimal(String.valueOf(v1));
		BigDecimal d2 = new BigDecimal(String.valueOf(v2));
		return d1.subtract(d2);
	}
	
	public static BigDecimal mul(double v1, double v2) {
		BigDecimal d1 = new BigDecimal(String.valueOf(v1));
		BigDecimal d2 = new BigDecimal(String.valueOf(v2));
		return d1.multiply(d2);
	}
	
	public static BigDecimal div(double v1, double v2) {
		BigDecimal d1 = new BigDecimal(String.valueOf(v1));
		BigDecimal d2 = new BigDecimal(String.valueOf(v2));
		return d1.divide(d2);
	}
}
