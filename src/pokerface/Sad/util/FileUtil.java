package pokerface.Sad.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.sun.imageio.plugins.common.InputStreamAdapter;

/**
 * 文件操作工具类
 * @author XinYuan
 *
 */
public class FileUtil {
	
	/**
	 * 获取配置文件
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Properties getProperties(String path) throws FileNotFoundException, IOException{
		
		Properties properties = new Properties();
		properties.load(new FileInputStream(path));
		
		return properties;
			
	}
	
	/**
	 * 将字符串写入文件中
	 * @param str
	 * @param path
	 * @throws IOException
	 */
	public static void writeStringToFile(String str,String path,String charset) throws IOException{
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),charset));
		bw.write(str);
		bw.flush();
		bw.close();
	}
	/**
	 * 从文本文件中读取字符串
	 * @param path
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public static String readStringFromFile(String path,String charset) throws IOException{
		BufferedReader br = null;
		if(charset != null)
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path),charset));
		else
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		StringBuffer sb = new StringBuffer();
		String temp = null;
		while((temp = br.readLine()) != null)
		{
			sb.append(temp);
			sb.append("\n");
		}
		return sb.toString();
	}
}
