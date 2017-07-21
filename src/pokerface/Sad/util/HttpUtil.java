package pokerface.Sad.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


public class HttpUtil {
	/**
	 * get请求获取页面 出现异常返回null
	 * @param url
	 * @param proxy
	 * @return
	 */
	public static String get(String url,String proxyHost,String proxyPort) {  
	    //创建HttpClientBuilder  
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
		//HttpClient  
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
		
		HttpGet httpGet = new HttpGet(url);  
		RequestConfig requestConfig = null;
		
		
		
        if(proxyHost != null && proxyPort != null)
        {
        	
        	HttpHost proxy = new HttpHost(proxyHost, new Integer(proxyPort).intValue());
        	
        	requestConfig = RequestConfig.custom()  
        			.setConnectionRequestTimeout(5000).setConnectTimeout(5000)  
        			.setSocketTimeout(5000).setProxy(proxy).build();
        }else{
        	requestConfig = RequestConfig.custom()  
        			.setConnectionRequestTimeout(1000).setConnectTimeout(1000)  
        			.setSocketTimeout(1000).build();
        }
        httpGet.setConfig(requestConfig);    
		
        HttpResponse httpResponse = null;
        HttpEntity entity = null;
		try {  
		    //执行get请求  
			httpResponse = closeableHttpClient.execute(httpGet);  
		    //获取响应消息实体  
		    entity = httpResponse.getEntity();
		    
		    String html = null;
		    //判断响应实体是否为空  
		    if (entity == null) return null; 
	    	else
		    {
	    		byte[] bytes = new byte[1024*1024]; //最大1M
	    		InputStream is = entity.getContent();
	    		int offset = 0;
	    		int numRead = 0;
	    		while(offset < bytes.length && 
	    				(numRead = is.read(bytes, offset, bytes.length-offset)) != -1) 
	    			offset+=numRead;
	    		html = new String(bytes,0,offset,"utf-8");
	    		String charSet = getCharSet(html);
	    		if(charSet == null || charSet.equals("utf8") || charSet.equals("utf-8"))
	    			//若是未解析到编码方式或编码方式为UTF8 
	    			return html;
	    		else
	    			html = new String(bytes,0,offset,charSet);
	    		return html;
		    }
		} catch (IOException e) {  
		    e.printStackTrace();  
		} finally {  
		    try {  
		    //关闭流并释放资源
		    	if(entity!=null)
		    		EntityUtils.consume(entity);
	            if(closeableHttpClient!= null)
	            	closeableHttpClient.close();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }
		return null;  
	}

	/**
	 * post请求获取页面 出现异常返回null
	 * @param url
	 * @param payload
	 * @param proxy
	 * @return
	 */
	public static String post(String url,List<BasicNameValuePair> payload,String proxyHost,String proxyPort) {

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();  
		HttpResponse response = null;   
		HttpPost httpPost = new HttpPost(url);  
		
		RequestConfig requestConfig = null;
        if(proxyHost != null && proxyPort != null)
        {
        	HttpHost proxy = new HttpHost(proxyHost, new Integer(proxyPort).intValue());
        	requestConfig = RequestConfig.custom()  
        			.setConnectionRequestTimeout(5000).setConnectTimeout(5000)  
        			.setSocketTimeout(5000).setProxy(proxy).build();
        }else{
        	requestConfig = RequestConfig.custom()  
        			.setConnectionRequestTimeout(1000).setConnectTimeout(1000)  
        			.setSocketTimeout(1000).build();
        }
		httpPost.setConfig(requestConfig);
        
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
		
		if(payload != null){
			for(BasicNameValuePair basicNameValuePair:payload)
				formparams.add(basicNameValuePair);  
		}
		UrlEncodedFormEntity entity = null;  
		try {
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");  
	        httpPost.setEntity(entity);  
	
	        response = closeableHttpClient.execute(httpPost);  
	
	        HttpEntity httpEntity = response.getEntity();  
		    String html = null;
		    if (httpEntity == null) return null; 
	    	else
		    {
	    		byte[] bytes = new byte[1024*1024]; //最大1M
	    		InputStream is = httpEntity.getContent();
	    		int offset = 0;
	    		int numRead = 0;
	    		while(offset < bytes.length && 
	    				(numRead = is.read(bytes, offset, bytes.length-offset)) != -1) 
	    			offset+=numRead;
	    		html = new String(bytes,0,offset,"utf-8");
	    		String charSet = getCharSet(html);
	    		if(charSet == null || charSet.equals("utf8") || charSet.equals("utf-8"))
	    			//若是未解析到编码方式或编码方式为UTF8 
	    			return html;
	    		else
	    			html = new String(bytes,0,offset,charSet);
	    		return html;
		    }
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
				try {
					if(entity!=null)
						EntityUtils.consume(entity);
					if(closeableHttpClient!= null)
						closeableHttpClient.close();  
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		    
		return null;
	}
    /** 
     * 正则获取字符编码 
     * @param content 
     * @return 
     */  
    private static String getCharSet(String content){  
        String regex = "charset=\\\"*(.+?)[ \\\">]";  
        Pattern pattern = Pattern.compile(regex);  
        Matcher matcher = pattern.matcher(content);  
        if(matcher.find())  
            return matcher.group(1);  
        else  
            return null;  
    }  
}

