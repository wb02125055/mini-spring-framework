package com.wb.springframework.core.io;

/**
 * @author WangBing
 * @date 2023/6/20 21:24
 */
public class DescriptiveResource extends AbstractResource {

    private final String description;

    public DescriptiveResource(String description) {
        this.description = description;
    }

}
