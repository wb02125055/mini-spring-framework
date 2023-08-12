package com.wb.springframework.core.type.classreading;

import com.wb.springframework.asm.ClassVisitor;
import com.wb.springframework.asm.SpringAsmInfo;
import com.wb.springframework.core.type.ClassMetadata;

/**
 * @author WangBing
 * @date 2023/7/26 22:46
 */
public class ClassMetadataReadingVisitor extends ClassVisitor implements ClassMetadata {
    public ClassMetadataReadingVisitor() {
        super(SpringAsmInfo.ASM_VERSION);
    }
    public ClassMetadataReadingVisitor(int api) {
        super(api);
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public boolean isInterface() {
        return false;
    }

    @Override
    public String[] getMemberClassNames() {
        return new String[0];
    }
}
