/**
 * 建立时间：2010-5-6
 */
package cn.aofeng.netease_blog_backup;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 字节数组数据源实现, 增加了来源URL的设置和获取, 用于在向EML写入附件时设置Header:Content-Location.
 *
 * @author 聂勇 <a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class ByteArrayDataSourceImpl extends ByteArrayDataSource {

    private String url;
    
    public ByteArrayDataSourceImpl(InputStream is, String type) throws IOException {
        super(is, type);
    }

    public ByteArrayDataSourceImpl(byte data[], String type) {
        super(data, type);
    }
    
    public ByteArrayDataSourceImpl(String data, String type) throws IOException {
        super(data, type);
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

	/*
	 * @see java.lang.Object#equals(Object)
	 */
    @Override
	public boolean equals(Object object) {
		if (!(object instanceof ByteArrayDataSourceImpl)) {
			return false;
		}
		ByteArrayDataSourceImpl rhs = (ByteArrayDataSourceImpl) object;
		return new EqualsBuilder()
				.append(this.getContentType(), rhs.getContentType())
				.append(this.url, rhs.url)
				.append(this.getName(), rhs.getName())
				.isEquals();
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
    @Override
	public int hashCode() {
		return new HashCodeBuilder(-1297054903, -955413729)
				.append(this.getContentType())
				.append(this.url)
				.append(this.getName())
				.toHashCode();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
    @Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("contentType", this.getContentType())
				.append("url", this.url)
				.append("name", this.getName())
				.toString();
	}
    
}
