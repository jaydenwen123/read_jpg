package com.homework.domain;

public class DecodeJPGInfo {

	// ���id
	private int id;
	// ͼƬ�Ŀ��
	private int imageWidth;
	// ͼƬ�ĸ߶�
	private int imageHeight;
	// ͼƬ�Ĳ�������
	private String Precision;
	// ͼƬ�İ汾��
	private String version;
	// ͼƬ�Ĳ�������
	private String sampleFactor;
	// ɨ�������
	private int scanComNum;
	// ��������ĸ���
	private int dhtNum;
	// ������ĸ���
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
