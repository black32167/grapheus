/**
 * 
 */
package org.grapheus.web.page.base;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * @author black
 *
 */
abstract public class AbstractGrapheusPage extends WebPage {

    private static final long serialVersionUID = 1L;

    public AbstractGrapheusPage() {
      
    }
    public AbstractGrapheusPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        PackageResourceReference cssFile = new PackageResourceReference(AbstractGrapheusPage.class,
                "style.css");
        CssHeaderItem cssItem = CssHeaderItem.forReference(cssFile);

        response.render(cssItem);
    }
}
