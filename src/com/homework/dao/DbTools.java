package com.homework.dao;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
/**
 * @author Administrator
 * 
 */
public class DbTools {

	public DbTools() {
		// TODO Auto-generated constructor stub
	}

	// ���ݿ������
	private static Connection connection = null;
	// �������ݿ����һ��ʹ��PreparedStatement ����Statement��
	// ��Ϊǰ��Ч�ʽϸ�
	private static PreparedStatement pstmt = null;
	private static Statement stmt = null;
	private static CallableStatement cs = null;
	// �������������
	private static ResultSet resultSet = null;
	// �������ݿ�Ļ�����Ϣ������Դ�ļ�db.properties�л�ȡ
	private static String username = null;
	private static String password = null;
	private static String url = null;
	private static String driver = null;
	private static Properties pro = null;

	// ��̬�飬������ʼ���������ݿ�����Ҫ����Դ�ļ���
	static {
		pro = new Properties();
		try {
			pro.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("db.properties"));
			username = pro.getProperty("username");
			password = pro.getProperty("password");
			url = pro.getProperty("url");
			driver = pro.getProperty("driver");
			System.out
					.println("the resource file db.properties is load success");
			System.out.println(username + ":" + password + ":" + url + ":"
					+ driver);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ��̬�飬������������������ע�⣺��Ҫ����mysql����������
	static {
		try {
			Class.forName(driver);
			System.out.println("register driver success");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ��ȡ���ݿ�����ӡ�
	public static Connection getConnection() {
		try {
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("the database is connnection success");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

	// �������ݿ�
	/**
	 * @param sql
	 * @param params
	 * @return ִ�ж����ݿ��д��������ӣ��޸ģ�ɾ���� �÷�������ʹ��ռλ���ķ�ʽ����������� ͨ��object[]����װ����
	 */
	public static boolean executeUpdate(String sql, Object[] params) {
		boolean flag = false;
		try {
			// ��ȡprepareStatement
			pstmt = getConnection().prepareStatement(sql);
			checkParams(params);
			// ����ֵΪ�ܵ�Ӱ���������
			int row = pstmt.executeUpdate();
			// �������С������ߵ����㡣��˵������ʧ�ܡ�
			flag = row > 0 ? true : false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// �ر���Դ��
			close(connection, pstmt, null);
		}
		return flag;
	}

	/**
	 * @param sql
	 * @param params
	 * @return ͨ��String[]����װ����
	 */
	public static boolean executeUpdate(String sql, String[] params) {
		boolean flag = false;
		try {
			pstmt = getConnection().prepareStatement(sql);
			int index = 1;
			if (params != null && params.length > 0) {
				for (int i = 0; i < params.length; i++) {
					pstmt.setString(index++, params[i]);
				}
			}
			int row = pstmt.executeUpdate();
			flag = row > 0 ? true : false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// �ر���Դ��
			close(connection, pstmt, null);
		}
		return flag;
	}

	/**
	 * @param sql
	 * @param params
	 * @return ͨ��list��������װҪ���������ݡ�
	 */
	public static boolean executeUpdate(String sql, List<Object> params) {
		boolean flag = false;
		try {
			pstmt = getConnection().prepareStatement(sql);
			int index = 1;
			// params.isEmpty() ��params.size()>0�ȼۡ�
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					pstmt.setObject(index++, params.get(i));
				}
			}

			flag = pstmt.executeUpdate() > 0 ? true : false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// �ر���Դ��
			close(connection, pstmt, null);
		}
		return flag;
	}

	// ������װ��ѯ�Ĳ�����
	/**
	 * @param sql
	 * @param params
	 * @return �����ݿ��ȡһ����¼�����뵽map�У����м�Ϊ������ֵΪ���ݡ�
	 */
	public static Map<String, Object> getUniqueResult(String sql,
			Object[] params) {

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			pstmt = getConnection().prepareStatement(sql);
			checkParams(params);
			// ִ�в�ѯ������
			resultSet = pstmt.executeQuery();
			// ��ȡ�ͽ������ص���Ϣ��������������
			ResultSetMetaData metaData = resultSet.getMetaData();
			// ��ȡ����
			int columnCount = metaData.getColumnCount();
			// ͨ��resultSet.next()�������жϡ����ֻ��һ�С����α�ֹͣ����һ�С�
			while (resultSet.next()) {
				for (int j = 1; j <= columnCount; j++) {
					//
					String columnName = metaData.getColumnName(j);
					Object columnValue = resultSet.getObject(columnName);
					if (columnValue == null) {
						columnValue = "";
					}
					map.put(columnName, columnValue);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(connection, pstmt, stmt);
		}
		return map;
	}

	// ��⴫��ò����Ƿ�Ϊ�ա�
	private static void checkParams(Object[] params) throws SQLException {
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}
		}
	}

	/**
	 * @param sql
	 * @param params
	 * @return
	 * 
	 *         ��ȡ������¼������ŵ�list��
	 */
	public static List<Map<String, Object>> getMoreResults(String sql,
			Object[] params) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		try {
			pstmt = getConnection().prepareStatement(sql);
			checkParams(params);
			resultSet = pstmt.executeQuery();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int colmnCount = metaData.getColumnCount();
			Map<String, Object> map = null;
			while (resultSet.next()) {
				map = new HashMap<String, Object>();
				for (int i = 1; i <= colmnCount; i++) {
					String columnName = metaData.getColumnName(i);
					Object columnValue = resultSet.getObject(i);
					map.put(columnName, columnValue);
				}
				list.add(map);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}

	/**
	 * @param sql
	 * @param parameters
	 * @return ִ�������������������룬�޸ĺ�ɾ����
	 */
	public static boolean executeUpdateBatch(String[] sql, String[][] parameters) {
		boolean result[] = new boolean[sql.length];
		boolean flag = false;
		int row = 0;
		try {
			getConnection().setAutoCommit(false);
			for (int i = 0; i < sql.length; i++) {
				if (null != parameters[i]) {
					pstmt = connection.prepareStatement(sql[i]);
					for (int j = 0; j < parameters[i].length; j++) {
						pstmt.setString(j + 1, parameters[i][j]);
					}
					row = pstmt.executeUpdate();
					result[i] = row > 0 ? true : false;
				}
			}
			for (int m = 0; m < result.length; m++) {
				if (flag == result[m]) {
					break;
				} else {
					flag = true;
				}
			}
			connection.commit();

		} catch (Exception e) {
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e.getMessage());
		} finally {
			close(connection, pstmt, stmt);
		}
		return flag;
	}

	/**
	 * @param sql
	 * @param params
	 * @return ͨ��object[]����װ���� ͨ�����ص�list��ֵ���ж���һ�����Ƕ�����
	 */
	public static List<Object[]> getResults(String sql, String[] params) {
		List<Object[]> list = new ArrayList<Object[]>();
		try {
			pstmt = getConnection().prepareStatement(sql);

			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					pstmt.setString(i + 1, params[i]);
				}
			}
			resultSet = pstmt.executeQuery();
			ResultSetMetaData md = resultSet.getMetaData();
			int columnCount = md.getColumnCount();
			while (resultSet.next()) {
				// Map rowData=new HashMap();
				Object[] rowData = new Object[columnCount];
				for (int i = 1; i <= columnCount; i++) {
					// rowData.put(md.getColumnName(i), rs.getObject(i));
					rowData[i - 1] = resultSet.getObject(i);

				}
				list.add(rowData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			close(connection, pstmt, stmt);
		}
		return list;
	}

	// *************callPro1�洢���̺���1*************
	public static CallableStatement callPro1(String sql, String[] params) {
		try {
			cs = getConnection().prepareCall(sql);
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					cs.setObject(i + 1, params[i]);
				}
			}
			cs.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			close(connection, pstmt, stmt, cs);
		}
		return cs;
	}

	// *******************callpro2�洢����2************************
	public static CallableStatement callPro2(String sql, String[] inparameters,
			Integer[] outparameters) {
		try {
			cs = getConnection().prepareCall(sql);
			if (inparameters != null) {
				for (int i = 0; i < inparameters.length; i++) {
					cs.setObject(i + 1, inparameters[i]);
				}
			}
			// cs.registerOutparameter(2,oracle.jdbc.OracleTypes.CURSOR);
			if (outparameters != null) {
				for (int i = 0; i < outparameters.length; i++) {
					cs.registerOutParameter(inparameters.length + 1 + i,
							outparameters[i]);
				}
			}
			cs.execute();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			close(connection, pstmt, stmt, cs);
		}
		return cs;
	}

	// ��������java�ķ����������ס���ݡ���ȡ��������
	/**
	 * @param sql
	 * @param params
	 * @param clazz
	 * @return
	 * ���ض�Ӧ����Ķ���
	 */
	public static <T> T getObject(String sql, Object[] params, Class<T> clazz)throws SQLException {
		T t = null;
		try {
			pstmt = getConnection().prepareStatement(sql);
			// �������Ƿ�Ϊ�գ�
			checkParams(params);
			resultSet = pstmt.executeQuery();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (resultSet.next()) {
				// ͨ��������ӳ�����
				t = clazz.newInstance();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnName(i);
					Object columnValue = resultSet.getObject(i);
					Field field = clazz.getDeclaredField(columnName);
					if(columnValue==null){
						columnValue="";
					}
					field.setAccessible(true);
					field.set(t, columnValue);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			close(connection, pstmt);
		}
		return t;
	}

	public static <T> List<T> getObjects(String sql,Object[] params,Class<T> clazz) throws SQLException{
//		������Ŷ����list����
		List<T> list=new ArrayList<T>();
		T t=null;
		try {
			pstmt=getConnection().prepareStatement(sql);
			checkParams(params);
			resultSet=pstmt.executeQuery();
//			��ȡ�ͽ������ص����ݿ����Ϣ��
			ResultSetMetaData metaData=resultSet.getMetaData();
//			�������
			int columnCount=metaData.getColumnCount();
			while(resultSet.next()){
//				ͨ���������ɶ���
				t=clazz.newInstance();
				for(int i=1;i<=columnCount;i++){
					String columnName=metaData.getColumnName(i);
					Object columnValue=resultSet.getObject(i);
//					ͨ�����ƻ�ȡ���ԡ�
					Field field=clazz.getDeclaredField(columnName);
//					����˽�����ԣ����Ա�����
					field.setAccessible(true);
//					�Զ�����и�ֵ����ע�⣬����õ�һ������Ϊ�ö��󡣵ڶ���Ϊ���Զ�Ӧ��Ҫ����ֵ
					field.set(t, columnValue);
					
				}
				list.add(t);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			close(connection, pstmt);
		}
		return list;
	}
	
	/**
	 * @param conn
	 * @param preparedStatement
	 * @param satStatement
	 *            ����ͳһ�Ĺر���Դ��
	 */
	public static void close(Connection conn,
			PreparedStatement preparedStatement, Statement satStatement,
			CallableStatement callableStatement) {
		if (callableStatement != null) {
			try {
				callableStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (satStatement != null) {
			try {
				satStatement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param conn
	 * @param preparedStatement
	 * @param satStatement
	 *            ����������Ҫ�洢�Ĳ������Ƚϳ��á�
	 */
	public static void close(Connection conn,
			PreparedStatement preparedStatement, Statement satStatement) {
		close(conn, preparedStatement, satStatement, null);
	}

	/**
	 * @param conn
	 * @param preparedStatement
	 * @param satStatement
	 *            ����������Ҫ�洢�Ĳ������Ƚϳ��á�
	 */
	public static void close(Connection conn,
			PreparedStatement preparedStatement) {
		close(conn, preparedStatement, null, null);
	}

	// ���Դ��롣
	public static void main(String[] args) {
		// getConnection();
		/*
		 * String sql =
		 * "insert into userinfo(username,pswd,realname) values (?,?,?)";
		 * System.out.println(executeUpdate(sql, new Object[] { "wen", "123",
		 * "wen" })); List<Object> params = new ArrayList<Object>();
		 * params.add("wenxiaofei"); params.add("234"); params.add("wagn");
		 * System.out.println(executeUpdate(sql, params));
		 * System.out.println(executeUpdate(sql, new String[] { "123", "123",
		 * "123" }));
		 */
		/*String sql = "select * from userinfo where id=?";
		List<Object[]> list = getResults(sql, new String[] { 1 + "" });
		
		 * for (Object[] obj : list) { System.out.println(Arrays.asList(obj)); }
		 try {
			System.out.println(getObject(sql, new Object[] { 1 + "" },
					UserInfo.class));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String sql = "select * from userinfo";
		/*try {
			for(UserInfo u:getObjects(sql, null, UserInfo.class)){
				
				System.out.println(u);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
