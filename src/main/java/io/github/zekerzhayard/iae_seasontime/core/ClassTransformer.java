package io.github.zekerzhayard.iae_seasontime.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("sereneseasons.season.SeasonTime".equals(transformedName)) {
            ClassNode cn = new ClassNode();
            new ClassReader(basicClass).accept(cn, 0);
            for (MethodNode mn : cn.methods) {
                if (RemapUtils.checkMethodName(cn.name, mn.name, mn.desc, "<init>") && RemapUtils.checkMethodDesc(mn.desc, "(I)V")) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain.getOpcode() == Opcodes.INVOKESTATIC) {
                            MethodInsnNode min = (MethodInsnNode) ain;
                            if (RemapUtils.checkClassName(min.owner, "com/google/common/base/Preconditions") && RemapUtils.checkMethodName(min.owner, min.name, min.desc, "checkArgument") && RemapUtils.checkMethodDesc(min.desc, "(ZLjava/lang/Object;)V")) {
                                mn.instructions.set(ain, new InsnNode(Opcodes.POP2));
                            }
                        }
                    }
                }
            }
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cn.accept(cw);
            basicClass = cw.toByteArray();
        }
        return basicClass;
    }
}
