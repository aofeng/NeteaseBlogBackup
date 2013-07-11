/**
 * 建立时间：2010-6-7
 */
package cn.aofeng.netease_blog_backup;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 备份日志内容下拉框子项.
 *
 * @author 聂勇，<a href="mailto:aofengblog@163.com">aofengblog@163.com</a>
 */
public class BackupContentPartComboBoxItem {

    public BackupContentPartComboBoxItem() {
        
    }

    /**
     * @param text 显示文本.
     * @param value 实际值.
     */
    public BackupContentPartComboBoxItem(String text, String value) {
        this.text  = text;
        this.value = value;
    }
    
    /**
	 * 日志的全部内容.
	 */
	public final static String CONTENT_ALL  = "all";
	
	/**
	 * 日志的正文部分.
	 */
	public final static String CONTENT_BODY = "body";
	
	/**
	 * 映射备份日志内容选择项值对应的XPath.
	 * 
	 * @param backupContentPartComboBoxItemValue 备份日志内容选择项值.
	 * @return XPath字符串.
	 */
	public static String mapBackupContentPartXPath(String backupContentPartComboBoxItemValue) {
		if (CONTENT_BODY.equals(backupContentPartComboBoxItemValue)) {
			return "//div[@class=\"mcnt ztag\"]/div[1]";
		} else if (CONTENT_ALL.equals(backupContentPartComboBoxItemValue)) {
			return "/";
		}
		
		return null;
	}

    /**
     * 显示文本.
     */
    private String text;

    /**
     * 实际值.
     */
    private String value;

	/**
	 * @return 显示文本.
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text 显示文本.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return 实际值.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value 实际值.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/*
	 * @see java.lang.Object#equals(Object)
	 */
    @Override
	public boolean equals(Object object) {
		if (!(object instanceof BackupContentPartComboBoxItem)) {
			return false;
		}
		BackupContentPartComboBoxItem rhs = (BackupContentPartComboBoxItem) object;
		return new EqualsBuilder().append(this.text, rhs.text)
				.append(this.value, rhs.value)
				.isEquals();
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
    @Override
	public int hashCode() {
		return new HashCodeBuilder(-24386027, -1806033395)
				.append(this.text)
				.append(this.value)
				.toHashCode();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
    @Override
	public String toString() {
		return text;
	}

}
