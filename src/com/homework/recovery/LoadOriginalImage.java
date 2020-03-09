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

	// 该常量指要保存jpg图片对应的十六进制数据的文件路径
	private static final String SAVEDATANAME = "C:\\Users\\Administrator\\Desktop\\wen.xls";
	// 该常量是指要保存解析出来的jpg图片信息的文件路径
	public static final String JPGFILEINFO = "C:\\Users\\Administrator\\Desktop\\info.txt";
	private int[] pix;
	private static Map<String, Object> params = null;
	private int width, height;
	private static JPEGDecoder jpegDecoder = null;

	// 图片的宽度
	public static int WIDTH_VALUE = 0;
	// 图片的高度
	public static int HEIGHT_VALUE = 0;
	// 图片的精度
	public static String PRECISION_VALUE = "";
	// 图片的版本
	public static String VERSION_VALUE = "";
	// 图片的采样精度
	public static String SAMPLE_PRECISION_VALUE = "";
	// 图片的扫描组件数量
	public static int SACN_COMPONENT_NUM_VALUE = 0;
	// 图片的量化表的个数
	public static int DQT_NUM_VALUE = 0;
	// 图片的霍夫曼表的个数
	public static int DHT_NUM_VALUE = 0;

	// 实现该接口的方法
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
	 * 在该方法中进行执行重构图片的操作
	 * 
	 * @param filePath
	 * @return
	 */
	public MemoryImageSource loadImage(String filePath) {
		FileInputStream in = null;
		try {
			// 首先将该jpeg文件的十六进制的数据写入到xls文件中
			ReadJPG.OutputJPGToBinaryFile(filePath, SAVEDATANAME);
			// 创建jpegDecoder对象
			jpegDecoder = new JPEGDecoder();
			in = new FileInputStream(filePath);
			// 对jpeg文件进行解码
			jpegDecoder.decode(in, this);
			// 解析jpeg每一段的数据
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
		// 获得jpeg文件的关键数据
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
