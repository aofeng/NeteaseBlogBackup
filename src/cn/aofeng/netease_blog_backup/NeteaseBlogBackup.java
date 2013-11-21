/**
 * 建立时间：2010-5-7
 */
package cn.aofeng.netease_blog_backup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;

import cn.aofeng.util.FileUtils;
import cn.aofeng.util.IOUtils;
import cn.aofeng.util.http.HtmlParser;
import cn.aofeng.util.http.HttpUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 网易博客备份工具.
 *
 * @author 聂勇 <a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class NeteaseBlogBackup {
	
	private static final Logger _logger = Logger.getLogger(NeteaseBlogBackup.class);

	private final static ScriptEngine _scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");
	
	/**
	 * 每页的记录数.
	 */
	private int pageSize = 20;
	
	private final static String JAVASCRIPT_FILE = "/cn/aofeng/netease_blog_backup/getObjField.js";
	
	private final static String RETRIEVE_DATA_FUNCTION;
	
	private final static String GET_OBJECT_VALUE_METHOD = "getObjField";
	
	// 抽取JavaScript的正则表达式: 以s后跟一个或多个数字开头并且以;结尾的行或以var开头并且以;结尾的行
	private final static String _SCRIPT_REGEX = "((^s(\\d+\\.)).*(;$))|((^(var)).*(;$))";
	private final static Pattern _SCRIPT_PATTERN = Pattern.compile(_SCRIPT_REGEX, Pattern.MULTILINE);
	
	private StringBuilder find(CharSequence charSequence, Pattern pattern) {
	    if (null == charSequence) {
            return null;
        }
	    
	    StringBuilder buff = new StringBuilder();
	    
		Matcher matcher = pattern.matcher(charSequence);
    	while (matcher.find()) {
    	    buff.append(matcher.group(0));
		}
    	
    	return buff;
	}
	
	/**
	 * 收集博客日志标题和链接.
	 * 
	 * @param blogUrl 博客地址URL.
	 * @param blogLogApiUrl 博客日志内容获取接口，示例：
	 * http://api.blog.163.com/aofengblog/dwr/call/plaincall/BlogBeanNew.getBlogs.dwr?callCount=1&scriptSessionId=${scriptSessionId}187&c0-scriptName=BlogBeanNew&c0-methodName=getBlogs&c0-id=0&c0-param0=number:6317021&c0-param1=number:0&c0-param2=number:20&batchId=306327
	 * @return 收集到的博客日志标题和链接.
	 * @throws XPathExpressionException
	 * @throws IOException
	 */
    public List<BlogLogInfo> collectBlogLogLink(String blogUrl, String blogLogApiUrl, String queryParams) {
        List<BlogLogInfo> result = new ArrayList<BlogLogInfo>();
        
        InputStream ins = null;
        BufferedReader reader = null;
        
        StringBuilder scriptBuff = new StringBuilder();
        
        try {
        	ins = HttpUtils.getInputStream(blogLogApiUrl, "UTF-8", queryParams);
            reader = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
            
            char[] chars = new char[8192];
            int readNum = 0;
            while (-1 != (readNum = reader.read(chars))) {
                scriptBuff.append(chars, 0, readNum);
            }
            
            StringBuilder realNeedScript = find(scriptBuff, _SCRIPT_PATTERN); 
            if (_logger.isDebugEnabled()) {
            	_logger.debug("**************  Filter Content Begin  **************");
                _logger.debug(realNeedScript);
                _logger.debug("**************  Filter Content End    **************");
			}
            
			if (null != realNeedScript && realNeedScript.length() > 0) {
                _scriptEngine.eval(realNeedScript.append(RETRIEVE_DATA_FUNCTION).toString());
                Invocable invocable = (Invocable) _scriptEngine;
                String objName   = null;
                String fieldName = null;
                String title      = null;
                String permalink  = null;
	            for (int i = 0; i < pageSize; i++) {
	                BlogLogInfo blogLogInfo = new BlogLogInfo();
	                
	                objName = "s"+i;
	                fieldName = "title";
	                title = (String) invocable.invokeFunction(GET_OBJECT_VALUE_METHOD, objName, fieldName);
	                if (! StringUtils.isBlank(title)) {
	                    blogLogInfo.setTitle(title);
	                }
	                
	                fieldName = "permalink";
	                permalink = (String) invocable.invokeFunction(GET_OBJECT_VALUE_METHOD, objName, fieldName);
	                if (! StringUtils.isBlank(permalink)) {
	                    if (! blogUrl.endsWith(MhtGenerator.SLASH)) {
	                        blogUrl = blogUrl + MhtGenerator.SLASH;
	                    }
	                    if (permalink.startsWith(MhtGenerator.SLASH)) {
	                        permalink = permalink.substring(MhtGenerator.SLASH.length());
	                    }
	                    blogLogInfo.setLink(blogUrl + permalink);
                    }
	                
	                if (! blogLogInfo.isEmpty()) {
	                    result.add(blogLogInfo); 
                    }
	            }
            }
			
		} catch (UnsupportedEncodingException e) {
			_logger.error("", e);
		} catch (HttpException e) {
			_logger.error("", e);
		} catch (IOException e) {
			_logger.error("", e);
		} catch (ScriptException e) {
			_logger.error("", e);
		} catch (NoSuchMethodException e) {
            _logger.error("", e);
        } finally {
			IOUtils.close(reader);
			IOUtils.close(ins);
		}
        
        return result;
    }

    /**
     * 解析博客的关于我页面，取得其中的JavaScript脚本，解析出hostId和hostName。
     * JavaScript脚本示例：
     * <pre>
     *  window.N = {tm:{'zbtn':'nbtn',
     *  'bdc0':'bdc0','bdc2':'bdc1',
     *  'bgc0':'bgc0','bgc1':'bgc1','bgc2':'bgc2','bgh0':'bgc9',
     *  'fc00':'fc03','fc01':'fc04','fc02':'fc05','fc03':'fc06','fc04':'fc07','fc05':'fc09'}};
     *  Date.servTime = '08/28/2010 16:20:11';
     *  location.api = 'http://api.blog.163.com/';
     *  location.msg = 'http://api.blog.163.com/msg/dwr';
     *  location.dwr = 'http://api.blog.163.com/aofengblog/dwr';
     *  location.vcd = 'http://api.blog.163.com/cap/captcha.jpgx?parentId=6317021&r=';
     *  location.mrt = 'http://b.bst.126.net/newpage/style/mbox/';
     *  location.fce = 'http://os.blog.163.com/common/ava.s?host=';
     *  location.fce2= 'http://os.blog.163.com/common/ava.s?host=';
     *  location.fpr = 'http://b.bst.126.net/common/portrait/face/preview/';
     *  location.f60 = 'http://b.bst.126.net/common/face60.png';
     *  location.f140= 'http://b.bst.126.net/common/face140.png';
     *  location.ept = 'http://b.bst.126.net/common/empty.png';
     *  location.guide_profile_add= 'http://b.bst.126.net/common/guide_profile_add.gif';
     *  location.phtoto_dream = 'http://photo.dream.163.com/blog/writeBlogCallback.do';
     *  window.CF = {
     *  	ca:false
     *  	,cb:''
     *  	,cc:false
     *  	,cd:false
     *  	,ce:'-60'
     *  	,ck:0
     *  	,ci:['api.blog.163.com'
     *  		,'http://photo.163.com/photo/html/crossdomain.html?t=20100205'
     *  		,'q.163.com']
     *  	,cj:[-60]
     *  	,cl:''
     *  	,cm:["","blog/","album/","music/","collection/","friends/","profile/"]
     *  	,cf:0
     *  	,co:{pv:false
     *  		,ti:46249966
     *  		,tn:''
     *  		,tc:0
     *  		,tl:3
     *  		,ut:0
     *  		,un:''
     *  		,um:''
     *  		,ui:0
     *  		,ud:true}
     *  		,cp:{nr:0
     *  		,cr:0
     *  		,vr:-100
     *  		,fr:0}
     *  };
     *  window.UD = {};
     *  	UD.host = {
     *  	userId:6317021,
     *  	userName:'aofengblog',
     *  	nickName:'傲风',
     *  	baseUrl:'http://aofengblog.blog.163.com/',
     *  	gender:'他'
     *  }; 
     * </pre>
     * 
     * @param blogProfileUrl 博客的关于我页面URL.
     * @return 博客的主机ID和主机名称.
     */
    public BlogInfo retriveBlogInfo(String blogProfileUrl) {
    	String hostInfoXPath = "/html/body/script[2]";
        HtmlParser htmlParser = new HtmlParser();
        NodeList scriptNodeList = null;
        try {
			scriptNodeList = htmlParser.parseHtml(blogProfileUrl, hostInfoXPath, "GBK", "UTF-8");
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("blog profile scriptNodeList:" + new HtmlParser().dom2Xml(scriptNodeList, "UTF-8"));
			}
			
			if (scriptNodeList.getLength() > 0) {
				BlogProfileAnalyseVisitor<Node> visitor = new BlogProfileAnalyseVisitor<Node>();
				HtmlParser.domIterate(scriptNodeList, visitor);
				
				String scriptHostInfo = visitor.getScriptString();
				
				if (_logger.isDebugEnabled()) {
					_logger.debug("blog profile scriptHostInfo:" + scriptHostInfo);
				}
				
				if (! StringUtils.isBlank(scriptHostInfo)) {
					String scriptString = RETRIEVE_DATA_FUNCTION + scriptHostInfo;
					_scriptEngine.eval(scriptString);
		             Invocable invocable = (Invocable) _scriptEngine;
		             
		             String objName = "UD";
		             Double hostId = (Double) invocable.invokeFunction(GET_OBJECT_VALUE_METHOD, objName, "host", "userId");
		             String hostName = (String) invocable.invokeFunction(GET_OBJECT_VALUE_METHOD, objName, "host", "userName");
		             
		             BlogInfo blogInfo = new BlogInfo(String.valueOf(hostId.intValue()), hostName);
		             
		             return blogInfo;
				}
			}
		} catch (XPathExpressionException e) {
			_logger.error("", e);
		} catch (IOException e) {
			_logger.error("", e);
		} catch (ScriptException e) {
			_logger.error("", e);
		} catch (NoSuchMethodException e) {
			_logger.error("", e);
		} catch (Exception e) {
			_logger.error("", e);
		}
		
		return null;
    }
    
    public static void main(String[] args) {
    	if (null == args || 4 != args.length) {
			System.err.println("arguments error, enter again. for examples:");
			System.err.println("arguments[0]=http://aofengblog.blog.163.com");
			System.err.println("arguments[1]=http://api.blog.163.com/aofengblog/dwr/call/plaincall/BlogBeanNew.getBlogs.dwr");
			System.err.println("arguments[2]=?callCount=1&scriptSessionId=187&c0-scriptName=BlogBeanNew&c0-methodName=getBlogs&c0-id=0&c0-param0=6317021&c0-param1=0&c0-param2=20&batchId=306327");
			System.err.println("arguments[3]=D:/ftp");
			
			return;
		}
    	
    	NeteaseBlogBackup blogBackUp = new NeteaseBlogBackup();
    	List<BlogLogInfo> blogLogInfos = blogBackUp.collectBlogLogLink(args[0], args[1], args[2]);
    	
    	if (_logger.isInfoEnabled()) {
    		_logger.info("a total of " + blogLogInfos.size() + " log");
		}
    	
    	for (int i = 0; i < blogLogInfos.size(); i++) {
    		BlogLogInfo blogLogInfo = blogLogInfos.get(i);
    		
    		if (_logger.isDebugEnabled()) {
    		    _logger.debug("BlogLogInfo=" + blogLogInfo.toString());
            }
    		
			Runnable job = new MhtGenerator(blogLogInfo, args[3], BackupContentPartComboBoxItem.mapBackupContentPartXPath(BackupContentPartComboBoxItem.CONTENT_BODY));
			job.run();
		}
	}

    static {
    	RETRIEVE_DATA_FUNCTION = FileUtils.readFileContent(
    			NeteaseBlogBackup.class.getResourceAsStream(JAVASCRIPT_FILE));
    	if (StringUtils.isBlank(RETRIEVE_DATA_FUNCTION)) {
			_logger.error("load javascript file " + JAVASCRIPT_FILE + " fail");
		}
    }

}
