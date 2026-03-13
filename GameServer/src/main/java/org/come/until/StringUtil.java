

package org.come.until;

import org.come.server.GameServer;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.UUID;

public class StringUtil {
	public static String str;
	public static final String EMPTY_STRING = "";

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 转换字节数组为16进制字串
	 * @param b 字节数组
	 * @return 16进制字串
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	public static String MD5Encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {
		}
		return resultString;
	}
	private final static BigDecimal b1 = new BigDecimal(10000);
	private final static BigDecimal b2 = new BigDecimal(100000);
	private final static BigDecimal b3 = new BigDecimal(1000000);
	private final static BigDecimal b4 = new BigDecimal(10000000);
	private final static BigDecimal b5 = new BigDecimal(100000000);
	private final static BigDecimal b6 = new BigDecimal(1000000000);
	public static String toMoneyString(BigDecimal money) {
		StringBuffer buffer = new StringBuffer(money.longValue() + "");
		for (int index = buffer.length() - 3; index > 0; index -= 3) {
			buffer.insert(index, ',');
		}
		if (money.compareTo(b1) < 0) {
			buffer.insert(0,"#W");
		} else if (money.compareTo(b2) < 0) {
			buffer.insert(0,"#c24DB76");
		} else if (money.compareTo(b3) < 0) {
			buffer.insert(0,"#cFD44DD");
		} else if (money.compareTo(b4) < 0) {
			buffer.insert(0,"#cFBD932");
		} else if (money.compareTo(b5) < 0) {
			buffer.insert(0,"#c00EFEF");
		} else if (money.compareTo(b6) < 0) {
			buffer.insert(0,"#G");
		} else {
			buffer.insert(0,"#R");
		}
		return buffer.toString();
	}

	public static String generateUniqueString(int length, int numberSize, int index) {
		length -= numberSize;
		int beginIndex = GameServer.random.nextInt(32 - length);
		int endIndex = beginIndex + length;
		String uuid = intToString(index, numberSize) + UUID.randomUUID().toString().replaceAll("-", "").substring(beginIndex, endIndex);
		return uuid;
	}
	private static String intToString(int i,int length) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(i);
		length -= buffer.length();
		for (int j = 0; j < length; j++) buffer.insert(0,"0");
		return buffer.toString();
	}
}
