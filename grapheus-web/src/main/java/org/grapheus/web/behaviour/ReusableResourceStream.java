/**
 * 
 */
package org.grapheus.web.behaviour;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.grapheus.web.component.shared.SerializableSupplier;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@NotThreadSafe
@RequiredArgsConstructor
public class ReusableResourceStream extends AbstractResourceStream {
    private static final long serialVersionUID = 1L;
    
    private final String contentType;
    private final SerializableSupplier<InputStream> inputStreamSupplier;
    
    private InputStream cachedInputStream;

    @Override
    public InputStream getInputStream() throws ResourceStreamNotFoundException {
        if(cachedInputStream == null) {
            cachedInputStream = inputStreamSupplier.get();
        }
        return cachedInputStream;
    }
    
    @Override
    public String getContentType() {
            return contentType;
    }
    
    @Override
    public void close() throws IOException {
        cachedInputStream.close();
        cachedInputStream = null;
    }
}
