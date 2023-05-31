package com.wb.springframework.context;

import com.wb.springframework.beans.BeansException;

/**
 * @author WangBing
 * @date 2023/5/20 14:31
 */
public interface ConfigurableApplicationContext extends ApplicationContext {

    void refresh() throws BeansException, IllegalStateException;
}
