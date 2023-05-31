package com.wb.springframework.beans;

import com.wb.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author WangBing
 * @date 2023/5/7 10:30
 */
public class MutablePropertyValues implements PropertyValues, Serializable {

    private final List<PropertyValue> propertyValueList;

    private Set<String> processedProperties;

    private volatile boolean converted = false;

    public MutablePropertyValues() {
        this.propertyValueList = new ArrayList<>(0);
    }

    public MutablePropertyValues(PropertyValues original) {
        if (null != original) {
            PropertyValue[] pvs = original.getPropertyValues();
            this.propertyValueList = new ArrayList<>(pvs.length);
            for (PropertyValue pv : pvs) {
                this.propertyValueList.add(new PropertyValue(pv));
            }
        } else {
            this.propertyValueList = new ArrayList<>(0);
        }
    }

    public MutablePropertyValues(Map<?, ?> original) {
        if (null != original) {
            this.propertyValueList = new ArrayList<>(original.size());
            original.forEach((attrName, attrValue) -> this.propertyValueList.add(
                    new PropertyValue(attrName.toString(), attrValue)
            ));
        } else {
            this.propertyValueList = new ArrayList<>(0);
        }
    }

    public MutablePropertyValues(List<PropertyValue> propertyValueList) {
        this.propertyValueList = (propertyValueList != null ? propertyValueList : new ArrayList<>());
    }

    public List<PropertyValue> getPropertyValueList() {
        return this.propertyValueList;
    }

    public int size() {
        return this.propertyValueList.size();
    }

    public MutablePropertyValues addPropertyValues(PropertyValues other) {
        if (other != null) {
            PropertyValue[] pvs = other.getPropertyValues();
            for (PropertyValue pv : pvs) {
                addPropertyValue(new PropertyValue(pv));
            }
        }
        return this;
    }

    public MutablePropertyValues addPropertyValues(Map<?, ?> other) {
        if (null != other) {
           other.forEach((attrName, attrValue) -> addPropertyValue(
                   new PropertyValue(attrName.toString(), attrValue)
           ));
        }
        return this;
    }
    public MutablePropertyValues addPropertyValue(PropertyValue pv) {
        for (int i = 0;i < this.propertyValueList.size(); i++) {
            PropertyValue currentValue = this.propertyValueList.get(i);
            if (currentValue.getName().equals(pv.getName())) {
                mergeIfRequired(pv, currentValue);
                setPropertyValueAt(pv, i);
                return this;
            }
        }
        this.propertyValueList.add(pv);
        return this;
    }

    public void addPropertyValue(String propertyName, Object propertyValue) {
        addPropertyValue(new PropertyValue(propertyName, propertyValue));
    }

    public MutablePropertyValues add(String propertyName, Object propertyValue) {
        addPropertyValue(new PropertyValue(propertyName, propertyValue));
        return this;
    }

    public void setPropertyValueAt(PropertyValue pv, int i) {
        this.propertyValueList.set(i, pv);
    }

    private PropertyValue mergeIfRequired(PropertyValue newPv, PropertyValue currentPv) {
        Object value = newPv.getValue();
        if (value instanceof Mergeable) {
            Mergeable mergeable = (Mergeable) value;
            if (mergeable.isMergeEnabled()) {
                Object merged = mergeable.merge(currentPv.getValue());
                return new PropertyValue(newPv.getName(), merged);
            }
        }
        return newPv;
    }

    public void removePropertyValue(String propertyName) {
        this.propertyValueList.remove(getPropertyValue(propertyName));
    }


    public void removePropertyValue(PropertyValue pv) {
        this.propertyValueList.remove(pv);
    }

    @Override
    public Iterator<PropertyValue> iterator() {
        return Collections.unmodifiableList(this.propertyValueList).iterator();
    }

    @Override
    public Spliterator<PropertyValue> spliterator() {
        return Spliterators.spliterator(this.propertyValueList, 0);
    }

    @Override
    public Stream<PropertyValue> stream() {
        return this.propertyValueList.stream();
    }

    @Override
    public PropertyValue[] getPropertyValues() {
        return this.propertyValueList.toArray(new PropertyValue[0]);
    }

    @Override
    public PropertyValue getPropertyValue(String propertyName) {
        for (PropertyValue pv : this.propertyValueList) {
            if (pv.getName().equals(propertyName)) {
                return pv;
            }
        }
        return null;
    }

    public Object get(String propertyName) {
        PropertyValue pv = getPropertyValue(propertyName);
        return null != pv ? pv.getValue() : null;
    }

    @Override
    public PropertyValues changesSince(PropertyValues old) {
        MutablePropertyValues changes = new MutablePropertyValues();
        if (old == this) {
            return changes;
        }
        for (PropertyValue newPv : this.propertyValueList) {
            PropertyValue pvOld = old.getPropertyValue(newPv.getName());
            if (null == pvOld || !pvOld.equals(newPv)) {
                changes.addPropertyValue(newPv);
            }
        }
        return changes;
    }

    @Override
    public boolean contains(String propertyName) {
        return getPropertyValue(propertyName) != null
                || (this.processedProperties != null && this.processedProperties.contains(propertyName));
    }

    @Override
    public boolean isEmpty() {
        return this.propertyValueList.isEmpty();
    }

    public void registerProcessedProperty(String propertyName) {
        if (this.processedProperties == null) {
            this.processedProperties = new HashSet<>(4);
        }
        this.processedProperties.add(propertyName);
    }

    public void clearProcessedProperty(String propertyName) {
        if (this.processedProperties != null) {
            this.processedProperties.remove(propertyName);
        }
    }
    public void setConverted() {
        this.converted = true;
    }
    public boolean isConverted() {
        return this.converted;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof MutablePropertyValues &&
                this.propertyValueList.equals(((MutablePropertyValues) other).getPropertyValueList()));
    }


    @Override
    public int hashCode() {
        return this.propertyValueList.hashCode();
    }
    @Override
    public String toString() {
        PropertyValue[] pvs = getPropertyValues();
        StringBuilder sb = new StringBuilder("PropertyValues: length=")
                .append(pvs.length);
        if (pvs.length > 0) {
            sb.append("; ").append(StringUtils.arrayToDelimitedString(pvs, "; "));
        }
        return sb.toString();
    }















}
