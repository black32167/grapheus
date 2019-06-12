package grapheus.persistence.user.data;

import lombok.Setter;
import grapheus.TimeService;

public class TestTimeService implements TimeService {
    @Setter
    private long currentTime;

    @Override
    public long getMills() {
        return currentTime;
    }
}