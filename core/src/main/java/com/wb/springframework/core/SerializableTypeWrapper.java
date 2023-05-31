package com.wb.springframework.core;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author WangBing
 * @date 2023/5/21 22:02
 */
public final class SerializableTypeWrapper {
    static Type forTypeProvider(TypeProvider typeProvider) {
        Type providedType = typeProvider.getType();
        if (providedType == null || providedType instanceof Serializable) {
            return providedType;
        }
        throw new IllegalArgumentException("Unsupported Type class: " + providedType.getClass().getName());
    }

    interface TypeProvider extends Serializable {
        Type getType();

        default Object getSource() {
            return null;
        }
    }
}
