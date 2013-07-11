/**
 * 建立时间：2010-5-6
 */
package cn.aofeng.util;

import java.io.Serializable;
import java.util.Properties;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 代理信息.
 *
 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
@SuppressWarnings("serial")
public class Proxy implements Serializable {

	private static Proxy _proxy = new Proxy();
    
    private Proxy() {
        
    }
    
    public static Proxy getInstance() {
        return _proxy;
    }
    
    /**
     * 代理服务器连接地址.
     */
    private String proxyHost;
    
    /**
     * 代理服务器连接端口.
     */
    private int proxyPort;
    
    /**
     * 是否使用代理服务器.
     */
    private boolean useProxy;
    
    /**
     * 不需要经过代理的URL表达式.
     */
    private String notUseProxyExp;

    public void setProxy(Proxy proxy) {
        _proxy = proxy;
    }

    /**
     * @return 代理服务器连接地址.
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * @param proxyHost 代理服务器连接地址.
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * @return 代理服务器连接端口.
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * @param proxyPort 代理服务器连接端口.
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * @return 是否使用代理服务器。使用代理服务器返回true，否则返回false.
     */
    public boolean isUseProxy() {
        return useProxy;
    }

    /**
     * @param useProxy 是否使用代理服务器，可选值：true / false.
     */
    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }
    
    /**
	 * @return 不需要经过代理的URL表达式.
	 */
	public String getNotUseProxyExp() {
		return notUseProxyExp;
	}

	/**
	 * @param notUseProxyExp 不需要经过代理的URL表达式.
	 */
	public void setNotUseProxyExp(String notUseProxyExp) {
		this.notUseProxyExp = notUseProxyExp;
	}

	/*
	 * @see java.lang.Object#equals(Object)
	 */
    @Override
	public boolean equals(Object object) {
		if (!(object instanceof Proxy)) {
			return false;
		}
		Proxy rhs = (Proxy) object;
		return new EqualsBuilder().append(this.useProxy, rhs.useProxy)
				.append(this.proxyHost, rhs.proxyHost)
                .append(this.proxyPort, rhs.proxyPort)
				.append(this.notUseProxyExp, rhs.notUseProxyExp)
				.isEquals();
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
    @Override
	public int hashCode() {
		return new HashCodeBuilder(-1166043331, 1260710309)
				.append(this.useProxy)
				.append(this.proxyHost)
                .append(this.proxyPort)
				.append(this.notUseProxyExp)
				.toHashCode();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
    @Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("useProxy", this.useProxy)
				.append("proxyHost", this.proxyHost)
				.append("proxyPort", this.proxyPort)
                .append("notUseProxyExp", this.notUseProxyExp)
				.toString();
	}
	
	static {
        Properties pros = FileUtils.getClasspathProperties("/proxy.properties");
        if (null != pros && ! pros.isEmpty()) {
            _proxy.proxyHost = pros.getProperty("proxy.host");
            _proxy.proxyPort = Integer.parseInt(pros.getProperty("proxy.port"));
            _proxy.useProxy  = Boolean.parseBoolean(pros.getProperty("proxy.useProxy"));
            _proxy.notUseProxyExp = pros.getProperty("proxy.notUseProxyExp");
        }
    }

}
