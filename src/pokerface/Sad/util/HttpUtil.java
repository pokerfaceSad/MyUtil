package pokerface.Sad.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;


public class HttpUtil {
	
	public static void main(String[] args) throws IOException {
		String html = HttpUtil.get("https://www.zhihu.com/", null, null);
		System.out.println(html);
		
	}
	
	/**
	 * get请求获取页面 出现异常返回null
	 * @param url
	 * @param proxy
	 * @return
	 * @throws IOException 
	 */
	public static String get(String url,String proxyHost,String proxyPort) throws IOException {  
		CloseableHttpClient closeableHttpClient = null;
		//根据协议类型创建相应client
		if(isHttps(url))
			closeableHttpClient = createSSLClientDefault();
			
		else {
			//创建HttpClientBuilder  
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create(); 
			httpClientBuilder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(5000).build());
			//HttpClient  
			closeableHttpClient = httpClientBuilder.build();  
		}
		
		HttpGet httpGet = new HttpGet(url);  
		RequestConfig requestConfig = null;
		
		//设置请求头
        httpGet.setHeader("Accept", "Accept text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");  
        httpGet.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");  
        httpGet.setHeader("Accept-Encoding", "gzip, deflate");  
        httpGet.setHeader("Accept-Language", "zh-cn,zh;q=0.5");  
        httpGet.setHeader("Connection", "keep-alive");  
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");  
		
		
		
        if(proxyHost != null && proxyPort != null)
        {
        	
        	HttpHost proxy = new HttpHost(proxyHost, new Integer(proxyPort).intValue());
        	
        	requestConfig = RequestConfig.custom()  
        			.setConnectionRequestTimeout(5000).setConnectTimeout(5000)  
        			.setSocketTimeout(5000).setProxy(proxy).build();
        }else{
        	requestConfig = RequestConfig.custom()  
        			.setConnectionRequestTimeout(2000).setConnectTimeout(2000)  
        			.setSocketTimeout(2000).build();
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
		} finally {  
		    try {  
		    	//关闭流并释放资源
		    	if(entity!=null)
		    		EntityUtils.consume(entity);
		    	if(httpGet != null)
		    		httpGet.releaseConnection();
	            if(closeableHttpClient!= null)
	            	closeableHttpClient.close();
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	    }
	}

	/**
	 * post请求获取页面 出现异常返回null
	 * @param url
	 * @param payload
	 * @param proxy
	 * @return
	 */
	public static String post(String url,List<BasicNameValuePair> payload,String proxyHost,String proxyPort) throws IOException{

		CloseableHttpClient closeableHttpClient = null;
		//根据协议类型创建相应client
		if(isHttps(url))
			closeableHttpClient = createSSLClientDefault();
		else {
			//创建HttpClientBuilder  
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();  
			httpClientBuilder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(5000).build());
			//HttpClient  
			closeableHttpClient = httpClientBuilder.build();  
		}
		
		HttpResponse response = null;   
		HttpPost httpPost = new HttpPost(url);  
		
		
		//设置请求头
        httpPost.setHeader("Accept", "Accept text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");  
        httpPost.setHeader("Accept-Charset", "GB2312,utf-8;q=0.7,*;q=0.7");  
        httpPost.setHeader("Accept-Encoding", "gzip, deflate");  
        httpPost.setHeader("Accept-Language", "zh-cn,zh;q=0.5");  
        httpPost.setHeader("Connection", "keep-alive");  
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");  
		
		
		
		
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
		    
	}
	/**
	 * 返回一个SSL连接的CloseableHttpClient实例
	 * @return
	 */
    public static CloseableHttpClient createSSLClientDefault(){
        try {
            SSLContext sslContext=new SSLContextBuilder().loadTrustMaterial(
                    null,new TrustStrategy() {
                        //信任所有
                        public boolean isTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            return true;
                        }
                    }).build();
            SSLConnectionSocketFactory sslsf=new SSLConnectionSocketFactory(sslContext);
            
            return HttpClients.custom().setSSLSocketFactory(sslsf).setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(5000).build()).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }
    /** 
     * 正则获取字符编码 
     * @param content 
     * @return 
     */  
    private static String getCharSet(String content){  
        String regex = "charset=['\\\"]*(.+?)[ '\\\">]";  
        Pattern pattern = Pattern.compile(regex);  
        Matcher matcher = pattern.matcher(content);  
        if(matcher.find())  
            return matcher.group(1);  
        else  
            return null;  
    }  
    
    /**
     * 判断请求URL是否为https请求
     * @param url
     * @return
     */
    public static boolean isHttps(String url){
		boolean result =  false;
    	
		Pattern p = Pattern.compile("https://");
		Matcher m = p.matcher(url);
		if(m.find()) result = true;
		
    	return result;
    }
}

