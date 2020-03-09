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
	 * 将图片的二进制数据保存到文件当中 该例子主要是经图片的二进制数据保存到excel表格中
	 * 
	 * @param fileName
	 *            要进行读入的jpg图片的文件路径
	 * @param saveFileName
	 *            要保存的文件的名称
	 * 
	 */
	public static void OutputJPGToBinaryFile(String fileName,
			String saveFileName) {
		// 构建如初输出流
		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(saveFileName));
			// 将字节数组的内容转化为字符串
			String data = Tool.Bytes2HexString(FromJPGToByte(fileName));
			// 将字符串写入到文件中
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
	 * 将一幅jpg的图片文件转换为二进制数据，存放到字节数组中
	 * 
	 * @param fileName
	 *            图片的真实路径
	 * @return 返回的是该图片的二进制数据
	 * 
	 */
	public static byte[] FromJPGToByte(String fileName) {
		InputStream is = null;
		// 构建字节数组输出流，将文件数据读入到字节数组中
		ByteArrayOutputStream arrayOutputStream = null;
		byte[] buffer = null;
		try {
			is = new FileInputStream(new File(fileName));
			arrayOutputStream = new ByteArrayOutputStream();
			int len = 0;
			buffer = new byte[1024];
			// 读出文件到字节数组中
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
