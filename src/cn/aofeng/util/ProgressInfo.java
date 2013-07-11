/**
 * 建立时间：2010-5-10
 */
package cn.aofeng.util;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 文件数字签名摘要处理进度信息.
 *
 * @author 聂勇 <a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class ProgressInfo {

    /**
     * 总大小(单位:bytes).
     */
    private float _totalSize;
    
    /**
     * 已处理数据量(单位:bytes).
     */
    private float _processedSize;

    /**
     * @return 总大小(单位:bytes).
     */
    public float getTotalSize() {
        return _totalSize;
    }

    /**
     * @param totalsize 总大小(单位:bytes).
     */
    public void setTotalSize(float totalsize) {
        this._totalSize = totalsize;
    }

    /**
     * @return 已处理数据量(单位:bytes).
     */
    public float getProcessedSize() {
        return _processedSize;
    }

    /**
     * @param processedsize 已处理数据量(单位:bytes).
     */
    public void setProcessedSize(float processedsize) {
        this._processedSize = processedsize;
    }

    /*
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
        if (!(object instanceof ProgressInfo)) {
            return false;
        }
        ProgressInfo rhs = (ProgressInfo) object;
        return new EqualsBuilder().append(this._processedSize, rhs._processedSize)
                        .append(this._totalSize, rhs._totalSize)
                        .isEquals();
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(658345195, -352940033)
                        .append(this._processedSize)
                        .append(this._totalSize)
                        .toHashCode();
    }

    /*
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                        .append("processedSize", this._processedSize)
                        .append("totalSize", this._totalSize)
                        .toString();
    }

}
