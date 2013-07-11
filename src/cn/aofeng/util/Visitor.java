/**
 * 建立时间：2010-5-6
 */
package cn.aofeng.util;

/**
 * 访问者.
 *
 * @author 聂勇 <a href="mailto:nieyong@asiainfo.com">nieyong@asiainfo.com</a>
 */
public interface Visitor<T extends Object> {

    public void process(T obj);

    public void addVisitor(Visitor<T> visitor);
}
