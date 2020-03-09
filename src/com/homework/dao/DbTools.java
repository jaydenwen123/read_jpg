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

	// 数据库的连接
	private static Connection connection = null;
	// 操作数据库对象。一般使用PreparedStatement 代替Statement，
	// 因为前者效率较高
	private static PreparedStatement pstmt = null;
	private static Statement stmt = null;
	private static CallableStatement cs = null;
	// 创建结果集对象。
	private static ResultSet resultSet = null;
	// 连接数据库的基本信息。从资源文件db.properties中获取
	private static String username = null;
	private static String password = null;
	private static String url = null;
	private static String driver = null;
	private static Properties pro = null;

	// 静态块，用来初始化连接数据库所需要的资源文件。
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

	// 静态块，用来加载类驱动程序。注意：需要导入mysql的驱动包。
	static {
		try {
			Class.forName(driver);
			System.out.println("register driver success");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 获取数据库的连接。
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

	// 操作数据库
	/**
	 * @param sql
	 * @param params
	 * @return 执行对数据库的写操作（添加，修改，删除） 该方法必须使用占位符的方式来传入参数。 通过object[]来封装对象。
	 */
	public static boolean executeUpdate(String sql, Object[] params) {
		boolean flag = false;
		try {
			// 获取prepareStatement
			pstmt = getConnection().prepareStatement(sql);
			checkParams(params);
			// 返回值为受到影响的行数。
			int row = pstmt.executeUpdate();
			// 如果行数小于零或者等于零。则说明操作失败。
			flag = row > 0 ? true : false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// 关闭资源。
			close(connection, pstmt, null);
		}
		return flag;
	}

	/**
	 * @param sql
	 * @param params
	 * @return 通过String[]来封装对象。
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
			// 关闭资源。
			close(connection, pstmt, null);
		}
		return flag;
	}

	/**
	 * @param sql
	 * @param params
	 * @return 通过list集合来封装要操作的数据。
	 */
	public static boolean executeUpdate(String sql, List<Object> params) {
		boolean flag = false;
		try {
			pstmt = getConnection().prepareStatement(sql);
			int index = 1;
			// params.isEmpty() 和params.size()>0等价。
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
			// 关闭资源。
			close(connection, pstmt, null);
		}
		return flag;
	}

	// 用来封装查询的操作。
	/**
	 * @param sql
	 * @param params
	 * @return 从数据库获取一条记录。放入到map中，其中键为列名，值为数据。
	 */
	public static Map<String, Object> getUniqueResult(String sql,
			Object[] params) {

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			pstmt = getConnection().prepareStatement(sql);
			checkParams(params);
			// 执行查询操作。
			resultSet = pstmt.executeQuery();
			// 获取和结果集相关的信息。包括列名，等
			ResultSetMetaData metaData = resultSet.getMetaData();
			// 获取列数
			int columnCount = metaData.getColumnCount();
			// 通过resultSet.next()方法来判断。如果只有一行。则游标停止在上一行。
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

	// 检测传入得参数是否为空。
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
	 *         获取多条记录，并存放到list中
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
	 * @return 执行批量操作。包括插入，修改和删除。
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
	 * @return 通过object[]来封装数据 通过返回的list的值来判断是一条还是多条。
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

	// *************callPro1存储过程函数1*************
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

	// *******************callpro2存储过程2************************
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

	// 下面利用java的反射机制来封住数据。获取单个对象。
	/**
	 * @param sql
	 * @param params
	 * @param clazz
	 * @return
	 * 返回对应的类的对象。
	 */
	public static <T> T getObject(String sql, Object[] params, Class<T> clazz)throws SQLException {
		T t = null;
		try {
			pstmt = getConnection().prepareStatement(sql);
			// 检查参数是否为空，
			checkParams(params);
			resultSet = pstmt.executeQuery();
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (resultSet.next()) {
				// 通过反射来映射对象
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
//		构建存放对象的list集合
		List<T> list=new ArrayList<T>();
		T t=null;
		try {
			pstmt=getConnection().prepareStatement(sql);
			checkParams(params);
			resultSet=pstmt.executeQuery();
//			获取和结果集相关的数据库的信息。
			ResultSetMetaData metaData=resultSet.getMetaData();
//			获得列数
			int columnCount=metaData.getColumnCount();
			while(resultSet.next()){
//				通过反射生成对象。
				t=clazz.newInstance();
				for(int i=1;i<=columnCount;i++){
					String columnName=metaData.getColumnName(i);
					Object columnValue=resultSet.getObject(i);
//					通过名称获取属性。
					Field field=clazz.getDeclaredField(columnName);
//					设置私有属性，可以被访问
					field.setAccessible(true);
//					对对象进行赋值，需注意，传入得第一个参数为该对象。第二个为属性对应的要赋的值
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
	 *            用来统一的关闭资源。
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
	 *            用来处理不需要存储的操作。比较常用。
	 */
	public static void close(Connection conn,
			PreparedStatement preparedStatement, Statement satStatement) {
		close(conn, preparedStatement, satStatement, null);
	}

	/**
	 * @param conn
	 * @param preparedStatement
	 * @param satStatement
	 *            用来处理不需要存储的操作。比较常用。
	 */
	public static void close(Connection conn,
			PreparedStatement preparedStatement) {
		close(conn, preparedStatement, null, null);
	}

	// 测试代码。
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
