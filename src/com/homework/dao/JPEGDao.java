package com.homework.dao;

import java.util.List;

import com.homework.domain.DecodeJPGInfo;
import com.homework.domain.OriImageInfo;
import com.homework.recovery.LoadOriginalImage;

/**
 * ����Ĺ������£���Ҫ������ɶ����ݿ�ķ��ʺͲ���
 * 
 * һ����Ҫע�⣺ 1.������Ϣ�����ݿ�ķ����У�����һ�µ�ԭ���������ص����ݶ�ֻ�������µ�һ����¼
 * 2.������ݿ��е��������У���ʾ�ձ�����Ҫ����������һ����¼ 3.������ζ�Ÿñ����Ѿ����ڼ�¼�� �򱣴�ʱֻ��Ҫ������� �ü�¼���и��¼���
 * 
 * @author Administrator
 * 
 */
public class JPEGDao {

	/**
	 * �÷��������������������ع����ͼƬ����Ϣ�����ݿ�
	 * 
	 * @param oriImageInfo
	 *            ��Ӧ���ع����ͼƬ��ʵ����
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
			System.out.println("jpegͼƬ�Ļ������ݱ���ɹ���������");
		} else {
			sql = "update OriImageInfo set fileName=? , fileType=? , fileDecimion=? , filePath=? , fileSize=?";
			DbTools.executeUpdate(
					sql,
					new Object[] { oriImageInfo.getFileName(),
							oriImageInfo.getFileType(),
							oriImageInfo.getFileDecimion(),
							oriImageInfo.getFilePath(),
							oriImageInfo.getFileSize() });
			System.out.println("jpegͼƬ�Ļ������ݸ��³ɹ���������");
		}
	}

	/**
	 * �÷����Ĺ����Ǳ���jpeg�ļ�����Ϣ�����ݿ�
	 * 
	 * @param imageInfo
	 */
	public void saveDecodeImageInfo(DecodeJPGInfo decodeJPGInfo) {

		List list = getDecodeImageInfos();
		String sql = "";
		if (list.size() == 0) {
			// ��֯�������ݵ�sql���
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

			System.out.println("��������ݱ���ɹ�������");
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
			System.out.println("��������ݸ��³ɹ�������");
		}
	}

	/**
	 * �÷���������ѯImageInfo���е�����
	 * 
	 * @return ����ֵΪ��ѯ���Ľ����
	 */
	public List getDecodeImageInfos() {
		String sqlQuery = "select * from DecodeJPGInfo";
		List list = DbTools.getMoreResults(sqlQuery, null);
		return list;
	}

	/**
	 * �÷���������ѯOriImageInfos���е����ݡ�
	 * 
	 * @return ����ֵ�ǲ�ѯ���Ľ��������һ��list���������
	 */
	public List getOriImageInfos() {
		String sqlQuery = "select * from OriImageInfo";
		List list = DbTools.getMoreResults(sqlQuery, null);
		return list;
	}
}
