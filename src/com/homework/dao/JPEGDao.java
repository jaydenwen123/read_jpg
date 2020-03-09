package com.homework.dao;

import java.util.List;

import com.homework.domain.DecodeJPGInfo;
import com.homework.domain.OriImageInfo;
import com.homework.recovery.LoadOriginalImage;

/**
 * 该类的功能如下，主要用来完成对数据库的访问和操作
 * 
 * 一下需要注意： 1.保存信息到数据库的方法中，尊熊一下的原则，两个保重的数据都只能有最新的一条记录
 * 2.如果数据库中的两个表中，表示空表，则需要对其进行添加一条记录 3.否则，意味着该表中已经存在记录， 则保存时只需要对最近的 该记录进行更新即可
 * 
 * @author Administrator
 * 
 */
public class JPEGDao {

	/**
	 * 该方法的作用是用来保存重构后的图片的信息到数据库
	 * 
	 * @param oriImageInfo
	 *            对应的重构后的图片的实体类
	 */
	public void saveOriImageInfo(OriImageInfo oriImageInfo) {

		List list = getOriImageInfos();
		String sql = "";
		if (list.size() < 1) {
			sql = "insert into OriImageInfo(fileName,fileType,fileDecimion,filePath,fileSize) values (?,?,?,?,?)";
			DbTools.executeUpdate(
					sql,
					new Object[] { oriImageInfo.getFileName(),
							oriImageInfo.getFileType(),
							oriImageInfo.getFileDecimion(),
							oriImageInfo.getFilePath(),
							oriImageInfo.getFileSize() });
			System.out.println("jpeg图片的基本数据保存成功！！！！");
		} else {
			sql = "update OriImageInfo set fileName=? , fileType=? , fileDecimion=? , filePath=? , fileSize=?";
			DbTools.executeUpdate(
					sql,
					new Object[] { oriImageInfo.getFileName(),
							oriImageInfo.getFileType(),
							oriImageInfo.getFileDecimion(),
							oriImageInfo.getFilePath(),
							oriImageInfo.getFileSize() });
			System.out.println("jpeg图片的基本数据更新成功！！！！");
		}
	}

	/**
	 * 该方法的功能是保存jpeg文件的信息到数据库
	 * 
	 * @param imageInfo
	 */
	public void saveDecodeImageInfo(DecodeJPGInfo decodeJPGInfo) {

		List list = getDecodeImageInfos();
		String sql = "";
		if (list.size() == 0) {
			// 组织插入数据的sql语句
			sql = "insert into DecodeJPGInfo(imageWidth,imageHeight,samplePrecision,version,sampleFactor,scanComNum,dhtNum,dqtNum) values (?,?,?,?,?,?,?,?)";
			DbTools.executeUpdate(
					sql,
					new Object[] { decodeJPGInfo.getImageWidth(),
							decodeJPGInfo.getImageHeight(),
							decodeJPGInfo.getSamplePrec(),
							decodeJPGInfo.getVersion(),
							decodeJPGInfo.getSampleFactor(),
							decodeJPGInfo.getScanComNum(),
							decodeJPGInfo.getDhtNum(),
							decodeJPGInfo.getDqtNum()

					});

			System.out.println("解码的数据保存成功！！！");
		} else {
			sql = "update DecodeJPGInfo set imageWidth=? , imageHeight=? , samplePrecision=? , version=? , sampleFactor=? , scanComNum=? , dhtNum=? , dqtNum=?";
			DbTools.executeUpdate(
					sql,
					new Object[] { decodeJPGInfo.getImageWidth(),
							decodeJPGInfo.getImageHeight(),
							decodeJPGInfo.getSamplePrec(),
							decodeJPGInfo.getVersion(),
							decodeJPGInfo.getSampleFactor(),
							decodeJPGInfo.getScanComNum(),
							decodeJPGInfo.getDhtNum(),
							decodeJPGInfo.getDqtNum() });
			System.out.println("解码的数据更新成功！！！");
		}
	}

	/**
	 * 该方法用来查询ImageInfo表中的数据
	 * 
	 * @return 返回值为查询到的结果集
	 */
	public List getDecodeImageInfos() {
		String sqlQuery = "select * from DecodeJPGInfo";
		List list = DbTools.getMoreResults(sqlQuery, null);
		return list;
	}

	/**
	 * 该方法用来查询OriImageInfos表中的数据。
	 * 
	 * @return 返回值是查询到的结果集，用一个list集合来存放
	 */
	public List getOriImageInfos() {
		String sqlQuery = "select * from OriImageInfo";
		List list = DbTools.getMoreResults(sqlQuery, null);
		return list;
	}
}
