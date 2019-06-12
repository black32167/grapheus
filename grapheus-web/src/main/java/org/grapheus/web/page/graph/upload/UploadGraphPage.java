/**
 * 
 */
package org.grapheus.web.page.graph.upload;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.page.base.AbstractGrapheusAuthenticatedPage;
import org.grapheus.web.page.vertices.list.VerticesPage;
import org.grapheus.web.page.vertices.upload.AddArtifactPage;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Slf4j
public class UploadGraphPage extends AbstractGrapheusAuthenticatedPage {
    private static final long serialVersionUID = 1L;
    
    private FileUploadField fileUploadField;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new FeedbackPanel("feedback"));
        
        add(uploadForm("uploadForm").add(fileUploadField = createUploadField("upload")));
    }

    private FileUploadField createUploadField(String uploadFieldId) {
        return new FileUploadField(uploadFieldId);
    }

    private Form<?> uploadForm(String formId) {
        Form<AddArtifactPage> uploadForm = new Form<AddArtifactPage>(formId) {
     
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() {
                FileUpload fileUpload = fileUploadField.getFileUpload();
                if(!"application/zip".equals(fileUpload.getContentType())) {//TODO: move mime type to 'grapheus-common'?
                    error("Unknown file type");
                } else {
                    try {
                        String newGraphId = fileUpload.getClientFileName().replaceAll("\\..*", "");
                        RemoteUtil.graphsAPI().upload(newGraphId, fileUpload.getInputStream());
                        setResponsePage(VerticesPage.class, 
                                new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, newGraphId));
                    } catch (Exception e) {
                        error(e.getMessage() == null ? "Could not upload file:" : e.getMessage());
                        log.error("Could not upload file {}:", fileUpload.getClientFileName(), e);
                    }
                }
                
            }
        };
        
        return uploadForm;
    }
    
}
