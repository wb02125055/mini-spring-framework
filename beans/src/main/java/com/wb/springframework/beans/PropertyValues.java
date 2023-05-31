package com.wb.springframework.beans;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author WangBing
 * @date 2023/5/7 10:39
 */
public interface PropertyValues extends Iterable<PropertyValue> {

    @Override
    default Iterator<PropertyValue> iterator() {
        return Arrays.asList(getPropertyValues()).iterator();
    }

    @Override
    default Spliterator<PropertyValue> spliterator() {
        return Spliterators.spliterator(getPropertyValues(), 0);
    }

    default Stream<PropertyValue> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    PropertyValue[] getPropertyValues();

    PropertyValue getPropertyValue(String propertyName);

    PropertyValues changesSince(PropertyValues old);

    boolean contains(String propertyName);

    boolean isEmpty();
}
