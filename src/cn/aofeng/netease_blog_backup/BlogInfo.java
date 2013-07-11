/**
 * 建立时间：2010-6-7
 */
package cn.aofeng.netease_blog_backup;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 博客信息.
 *
 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class BlogInfo {

	public BlogInfo() {
		
	}
	
	public BlogInfo(String hostId, String hostName) {
		this.hostId   = hostId;
		this.hostName = hostName;
	}
	
	/**
	 * 主机ID.
	 */
	private String hostId;
	
	/**
	 * 主机名称.
	 */
	private String hostName;
	
	/**
	 * @return 主机ID.
	 */
	public String getHostId() {
		return hostId;
	}

	/**
	 * @param hostId 主机ID.
	 */
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	/**
	 * @return 主机名称.
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName 主机名称.
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/*
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof BlogInfo)) {
			return false;
		}
		BlogInfo rhs = (BlogInfo) object;
		return new EqualsBuilder().append(this.hostName, rhs.hostName)
				.append(this.hostId, rhs.hostId)
				.isEquals();
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(-1946331009, 323095053)
				.append(this.hostName)
				.append(this.hostId)
				.toHashCode();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("hostId", this.hostId)
				.append("hostName", this.hostName)
				.toString();
	}
	
}
