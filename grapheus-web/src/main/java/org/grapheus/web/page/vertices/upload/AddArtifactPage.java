/**
 * 
 */
package org.grapheus.web.page.vertices.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import org.grapheus.client.UserClient;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.web.RemoteUtil;
import org.grapheus.web.page.base.AbstractGrapheusAuthenticatedPage;
import org.grapheus.web.page.vertices.list.VerticesPage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Slf4j
public class AddArtifactPage extends AbstractGrapheusAuthenticatedPage {
    public static final String PARAM_GRAPH_NAME = "graph";
    private static final Gson gson = new GsonBuilder().create();
    private static final int UPLOAD_THREADS = 10;
    
    private transient final ExecutorService uploadExecutor = Executors.newFixedThreadPool(UPLOAD_THREADS, (r) -> new Thread(r, "Vertex Uploader"));

    private static final long serialVersionUID = 1L;
    protected static final int UPLOAD_VERTEX_BATCH_SIZE = 100;
    private static final int MAX_VERTICES_TO_UPLOAD = 5000;

    private final String graphName;
    
    private final List<FileUpload> uploads = new ArrayList<>();
    
    public AddArtifactPage(PageParameters params) {
        super(params);
        this.graphName = params.get(PARAM_GRAPH_NAME).toString();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new FeedbackPanel("feedback"));
        
        add(uploadForm("uploadForm").add(createUploadField("upload")));
    }
    

    private MultiFileUploadField createUploadField(String id) {
        return new MultiFileUploadField(id, new PropertyModel<>(this, "uploads"), MAX_VERTICES_TO_UPLOAD, true);
    }

    private Form<?>  uploadForm(String id) {
        Form<AddArtifactPage> uploadForm = new Form<AddArtifactPage>(id) {
     
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit()
            {
                int batchStartIdx = 0;
                Collection<Future<?>> uploadFutures = new ArrayList<Future<?>>();
                UserClient remoteClient = RemoteUtil.contextlessUserClient();
                while (batchStartIdx < uploads.size()) {
                    int batchEndIdx = Math.min(uploads.size(), batchStartIdx + UPLOAD_VERTEX_BATCH_SIZE);
                    Collection<FileUpload> uploadsBatch = uploads.subList(batchStartIdx, batchEndIdx);
                    uploadFutures.add(uploadExecutor.submit(() -> {
                        Collection<RVertex> vertices = new ArrayList<>(UPLOAD_VERTEX_BATCH_SIZE);
                        for(FileUpload upload:uploadsBatch) {
                            try {
                                vertices.add(deserializeVertex(upload.getClientFileName(), upload.getInputStream()));
                            } catch(Exception e) {
                                log.error("Cannot read '{}'", upload.getClientFileName(), e);
                            }
                        };
                        remoteClient.vertex().addVertexBatch(graphName, vertices);
                        
                    }));
                    
                    batchStartIdx = batchEndIdx;
                }
                for(Future<?> future:uploadFutures) {
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException e) {
                       log.error("Error uploading batch", e);
                    }
                }
                navigateGraphPage();
            }
        };
        uploadForm.setMultiPart(true);

        // Set maximum size to 100K for demo purposes
        uploadForm.setMaxSize(Bytes.megabytes(100));

        // Set maximum size per file to 90K for demo purposes
        uploadForm.setFileMaxSize(Bytes.megabytes(1));
        return uploadForm;
    }
    
    private RVertex deserializeVertex(String vertexFileName, InputStream vertexJsonStream) throws IOException {
        String artifactName = vertexFileName.replace(".json", "");
    
        try (InputStreamReader freader = new InputStreamReader(vertexJsonStream)) {
            RVertex a = gson.fromJson(freader, RVertex.class);
          
            if(a.getTitle() == null) {
                a.setTitle(artifactName);
            }
            if(a.getDescription() == null) {
                a.setDescription("");
            }
            return a;
        }
    }
    

    private void navigateGraphPage() {
        setResponsePage(VerticesPage.class, new PageParameters().add(VerticesPage.PARAM_SELECTED_GRAPH, graphName));
    }

}
