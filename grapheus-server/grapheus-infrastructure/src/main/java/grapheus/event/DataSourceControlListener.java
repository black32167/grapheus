/**
 * 
 */
package grapheus.event;

/**
 * @author black
 *
 */
public interface DataSourceControlListener {
    void onDatasourceDisabled(String dsLinkId);
    void onDatasourceEnabled(String dsLinkId);

}
