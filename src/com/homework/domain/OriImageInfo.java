package com.homework.domain;

public class OriImageInfo {

	// 表的id
	private int id;
	// 图片的名称
	private String fileName;
	// 图片的类型
	private String fileType;
	// 文件的尺寸
	private String fileDecimion;
	// 图片的路径
	private String filePath;
	// 文件的字节大小
	private String fileSize;

	public OriImageInfo() {

	}

	public OriImageInfo(int id, String fileName, String fileType,
			String fileDecimion, String filePath, String fileSize) {
		super();
		this.id = id;
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileDecimion = fileDecimion;
		this.filePath = filePath;
		this.fileSize = fileSize;
	}

	public OriImageInfo(String fileName, String fileType, String fileDecimion,
			String filePath, String fileSize) {
		super();
		this.fileName = fileName;
		this.fileType = fileType;
		this.fileDecimion = fileDecimion;
		this.filePath = filePath;
		this.fileSize = fileSize;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileDecimion() {
		return fileDecimion;
	}

	public void setFileDecimion(String fileDecimion) {
		this.fileDecimion = fileDecimion;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

}
