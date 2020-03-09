package com.homework.util;

/**
 * @author wenxiaofei
 * 
 */
public class Tool {

	public final static byte[] hex = "0123456789ABCDEF".getBytes();

	public static int parse(char c) {
		if (c >= 'a')
			return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
			return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}

	/*
	 * public static void main(String[] args) { System.out.println(hex.length);
	 * for(byte b:hex){
	 * 
	 * System.out.println(b); } }
	 */

	// �ֽ�����תʮ�������ַ���
	/**
	 * @author wenxiaofei
	 * @�������ܣ��ֽ�����תΪ�ַ���
	 * @����ֵ������ת�����ʮ�������ַ���
	 * @param :the byte array
	 */
	public static String Bytes2HexString(byte[] b) {
		byte[] buff = new byte[2 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[2 * i] = hex[b[i] >> 4 & 0x0f];
			buff[2 * i + 1] = hex[b[i] & 0x0f];

		}
		return new String(buff);
	}

	public static String intToHex() {
		return "";
	}

	// ʮ�������ַ���ת�ֽ�����

	/**
	 * @author wenxiaofei
	 * @param: hexstr
	 * @return :byte array value
	 */
	public static byte[] HexString2Bytes(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}
}