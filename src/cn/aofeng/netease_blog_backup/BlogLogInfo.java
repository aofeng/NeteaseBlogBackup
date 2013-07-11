/**
 * 建立时间：2010-5-10
 */
package cn.aofeng.netease_blog_backup;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 博客日志信息.
 *
 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class BlogLogInfo {
	
	/**
	 * 日志标题.
	 */
	private String title;
	
	/**
	 * 日志相对博客地址的链接地址.
	 */
	private String link;
	
	/**
	 * 是否是空对象.
	 */
	public boolean isEmpty() {
	    return (null == title && null == link);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	/*
	 * @see java.lang.Object#equals(Object)
	 */
    @Override
	public boolean equals(Object object) {
		if (!(object instanceof BlogLogInfo)) {
			return false;
		}
		BlogLogInfo rhs = (BlogLogInfo) object;
		return new EqualsBuilder()
				.append(this.title, rhs.title)
				.append(this.link, rhs.link)
				.isEquals();
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
    @Override
	public int hashCode() {
		return new HashCodeBuilder(-1543766055, -605105315)
				.append(this.title)
				.append(this.link)
				.toHashCode();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
    @Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.append("link", this.link)
				.append("title", this.title)
				.toString();
	}

}
