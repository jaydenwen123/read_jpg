package com.homework.domain;

public class DecodeJPGInfo {

	// 表的id
	private int id;
	// 图片的宽度
	private int imageWidth;
	// 图片的高度
	private int imageHeight;
	// 图片的采样精度
	private String Precision;
	// 图片的版本号
	private String version;
	// 图片的采样因子
	private String sampleFactor;
	// 扫描组件数
	private int scanComNum;
	// 哈弗曼表的个数
	private int dhtNum;
	// 量化表的个数
	private int dqtNum;

	public DecodeJPGInfo() {
		// TODO Auto-generated constructor stub
	}

	public DecodeJPGInfo(int imageWidth, int imageHeight, String samplePrec,
			String version, String sampleFactor, int scanComNum, int dhtNum,
			int dqtNum) {
		super();
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.Precision = samplePrec;
		this.version = version;
		this.sampleFactor = sampleFactor;
		this.scanComNum = scanComNum;
		this.dhtNum = dhtNum;
		this.dqtNum = dqtNum;
	}

	public DecodeJPGInfo(int id, int imageWidth, int imageHeight,
			String samplePrec, String version, String sampleFactor,
			int scanComNum, int dhtNum, int dqtNum) {
		super();
		this.id = id;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.Precision = samplePrec;
		this.version = version;
		this.sampleFactor = sampleFactor;
		this.scanComNum = scanComNum;
		this.dhtNum = dhtNum;
		this.dqtNum = dqtNum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public String getSamplePrec() {
		return Precision;
	}

	public void setSamplePrec(String samplePrec) {
		this.Precision = samplePrec;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSampleFactor() {
		return sampleFactor;
	}

	public void setSampleFactor(String sampleFactor) {
		this.sampleFactor = sampleFactor;
	}

	public int getScanComNum() {
		return scanComNum;
	}

	public void setScanComNum(int scanComNum) {
		this.scanComNum = scanComNum;
	}

	public int getDhtNum() {
		return dhtNum;
	}

	public void setDhtNum(int dhtNum) {
		this.dhtNum = dhtNum;
	}

	public int getDqtNum() {
		return dqtNum;
	}

	public void setDqtNum(int dqtNum) {
		this.dqtNum = dqtNum;
	}

}
