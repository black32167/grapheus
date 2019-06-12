/**
 * 
 */
package grapheus.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import lombok.RequiredArgsConstructor;

/**
 * @author black
 *
 */
@RequiredArgsConstructor
public class MultimapCollector<T, K, V> implements Collector<T, Map<K, Collection<V>>, Map<K, Collection<V>>> {
    private final Function<T, K> keyMapper;
    private final Function<T, V> valueMapper;

    @Override
    public Supplier<Map<K, Collection<V>>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<K, Collection<V>>, T> accumulator() {
        return (map, item) -> map.computeIfAbsent(keyMapper.apply(item), (k) -> new ArrayList<V>()).add(valueMapper.apply(item));
    }

    @Override
    public BinaryOperator<Map<K, Collection<V>>> combiner() {
        return (m1, m2) -> {
            m2.entrySet().forEach((Map.Entry<K, Collection<V>> e) -> {
                m1.computeIfAbsent(e.getKey(), (k) -> new ArrayList<V>()).addAll(e.getValue());
            });
            return m1;
        };
    }

    @Override
    public Function<Map<K, Collection<V>>, Map<K, Collection<V>>> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.UNORDERED, Characteristics.IDENTITY_FINISH);
    }

}
