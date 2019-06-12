package grapheus.persistence.storage.traverse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Edge {
    String from;
    String to;
    public boolean contains(String v) {
        return v.equals(from) || v.equals(to);
    }
}