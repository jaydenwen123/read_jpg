package com.homework.read;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.homework.util.Tool;
import com.wxf.util.JpgFile;

public class ReadJPG {

	/**
	 * ��ͼƬ�Ķ��������ݱ��浽�ļ����� ��������Ҫ�Ǿ�ͼƬ�Ķ��������ݱ��浽excel�����
	 * 
	 * @param fileName
	 *            Ҫ���ж����jpgͼƬ���ļ�·��
	 * @param saveFileName
	 *            Ҫ������ļ�������
	 * 
	 */
	public static void OutputJPGToBinaryFile(String fileName,
			String saveFileName) {
		// ������������
		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(saveFileName));
			// ���ֽ����������ת��Ϊ�ַ���
			String data = Tool.Bytes2HexString(FromJPGToByte(fileName));
			// ���ַ���д�뵽�ļ���
			StringBuffer stringBuffer = new StringBuffer();
			for (int i = 0; i < data.length(); i = i + 2) {
				if (i % 32 == 0 && i > 0) {
					// System.out.println();
					stringBuffer.append("\r\n");
				}
				// System.out.print(data.substring(i,i+2)+"   ");
				stringBuffer.append(data.substring(i, i + 2) + "   ");
			}
			os.write(stringBuffer.toString().getBytes());
			os.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ��һ��jpg��ͼƬ�ļ�ת��Ϊ���������ݣ���ŵ��ֽ�������
	 * 
	 * @param fileName
	 *            ͼƬ����ʵ·��
	 * @return ���ص��Ǹ�ͼƬ�Ķ���������
	 * 
	 */
	public static byte[] FromJPGToByte(String fileName) {
		InputStream is = null;
		// �����ֽ���������������ļ����ݶ��뵽�ֽ�������
		ByteArrayOutputStream arrayOutputStream = null;
		byte[] buffer = null;
		try {
			is = new FileInputStream(new File(fileName));
			arrayOutputStream = new ByteArrayOutputStream();
			int len = 0;
			buffer = new byte[1024];
			// �����ļ����ֽ�������
			while ((len = is.read(buffer)) != -1) {
				arrayOutputStream.write(buffer, 0, len);
			}
			arrayOutputStream.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return arrayOutputStream.toByteArray();
	}

	public static void main(String[] args) {

		OutputJPGToBinaryFile(JpgFile.READFILENAME, JpgFile.SAVEFILENAME);
		System.out.println("\nsuccess");
	}

}
