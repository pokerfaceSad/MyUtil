package pokerface.Sad.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 
 * @author kid
 *
 *	数据库操作
 */
public class DBUtil {
	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//properties 对象的单例
	private static Properties dbProperties = null;
	/**
	 * 
	 * 获取数据库连接
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static Connection getConn() throws FileNotFoundException, IOException, SQLException  {
		Connection conn = null;
		Properties pro = getDBProperties();
		conn = DriverManager.getConnection(pro.getProperty("url"),pro.getProperty("user"),pro.getProperty("password"));
		return conn;
	}
	/**
	 * 获取根目录下的数据库配置文件
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Properties getDBProperties() throws FileNotFoundException, IOException{
		if(dbProperties == null)
		{
			dbProperties = FileUtil.getProperties("db.properties");
			return dbProperties;
		}else
			return dbProperties;
			
	}
	
	/**
	 * 开启事务
	 * @param conn
	 */
	public static void beginTransaction(Connection conn){
		if(conn!=null){
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	
	/**
	 * 提交事务
	 * @param conn
	 */
	public static void commitTransaction(Connection conn){
		if(conn!=null){
			try {
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	
	/**
	 * 事务回滚
	 * @param conn
	 */
	public static void rollback(Connection conn){
		if(conn!=null){
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	
	/**
	 * 释放资源
	 * @param ps
	 * @param conn
	 * @param rs
	 */
	public static void close(PreparedStatement ps, Connection conn,ResultSet rs) {

		if(rs!=null)
		{
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(ps!=null)
		{
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(conn!=null)
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
