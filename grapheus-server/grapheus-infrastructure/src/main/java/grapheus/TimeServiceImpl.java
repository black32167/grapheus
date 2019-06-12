/**
 * 
 */
package grapheus;

import org.springframework.stereotype.Service;

/**
 * @author black
 *
 */
@Service
public class TimeServiceImpl implements TimeService {

    @Override
    public long getMills() {

        return System.currentTimeMillis();
    }

}
