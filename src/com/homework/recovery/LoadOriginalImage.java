package com.homework.recovery;

import java.awt.image.MemoryImageSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.homework.decode.JPEGDecoder;
import com.homework.decode.JPEGDecoder.PixelArray;
import com.homework.read.ReadJPG;

public class LoadOriginalImage implements JPEGDecoder.PixelArray {

	// �ó���ָҪ����jpgͼƬ��Ӧ��ʮ���������ݵ��ļ�·��
	private static final String SAVEDATANAME = "C:\\Users\\Administrator\\Desktop\\wen.xls";
	// �ó�����ָҪ�������������jpgͼƬ��Ϣ���ļ�·��
	public static final String JPGFILEINFO = "C:\\Users\\Administrator\\Desktop\\info.txt";
	private int[] pix;
	private static Map<String, Object> params = null;
	private int width, height;
	private static JPEGDecoder jpegDecoder = null;

	// ͼƬ�Ŀ��
	public static int WIDTH_VALUE = 0;
	// ͼƬ�ĸ߶�
	public static int HEIGHT_VALUE = 0;
	// ͼƬ�ľ���
	public static String PRECISION_VALUE = "";
	// ͼƬ�İ汾
	public static String VERSION_VALUE = "";
	// ͼƬ�Ĳ�������
	public static String SAMPLE_PRECISION_VALUE = "";
	// ͼƬ��ɨ���������
	public static int SACN_COMPONENT_NUM_VALUE = 0;
	// ͼƬ��������ĸ���
	public static int DQT_NUM_VALUE = 0;
	// ͼƬ�Ļ�������ĸ���
	public static int DHT_NUM_VALUE = 0;

	// ʵ�ָýӿڵķ���
	@Override
	public void setPixel(int x, int y, int argb) {
		pix[x + y * width] = argb;
	}

	@Override
	public void setSize(int width, int height) {
		// TODO Auto-generated method stub
		this.width = width;
		this.height = height;
		pix = new int[width * height];
	}

	/**
	 * �ڸ÷����н���ִ���ع�ͼƬ�Ĳ���
	 * 
	 * @param filePath
	 * @return
	 */
	public MemoryImageSource loadImage(String filePath) {
		FileInputStream in = null;
		try {
			// ���Ƚ���jpeg�ļ���ʮ�����Ƶ�����д�뵽xls�ļ���
			ReadJPG.OutputJPGToBinaryFile(filePath, SAVEDATANAME);
			// ����jpegDecoder����
			jpegDecoder = new JPEGDecoder();
			in = new FileInputStream(filePath);
			// ��jpeg�ļ����н���
			jpegDecoder.decode(in, this);
			// ����jpegÿһ�ε�����
			jpegDecoder.parse();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (in != null) {

				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		MemoryImageSource mi = new MemoryImageSource(width, height, pix, 0,
				width);
		return mi;
	}

	public static void initParams() {
		// ���jpeg�ļ��Ĺؼ�����
		params = JPEGDecoder.params;
		WIDTH_VALUE = (Integer) params.get("width");
		HEIGHT_VALUE = (Integer) params.get("height");
		SAMPLE_PRECISION_VALUE = (String) params.get("samplePrecision");
		PRECISION_VALUE = (String) params.get("precision");
		DQT_NUM_VALUE = (Integer) params.get("dqtNum");
		DHT_NUM_VALUE = (Integer) params.get("dhtNum");
		SACN_COMPONENT_NUM_VALUE = (Integer) params.get("scanComponentNum");
		VERSION_VALUE = (String) params.get("version");
	}

}
