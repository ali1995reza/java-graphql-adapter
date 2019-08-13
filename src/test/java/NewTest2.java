import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.*;
import java.io.*;

public class NewTest2 {
    private InstructionFactory _factory;
    private ConstantPoolGen    _cp;
    private ClassGen           _cg;

    public NewTest2() {
        _cg = new ClassGen("MyFetcher", "java.lang.Object", "MyFetcher.java", Const.ACC_PUBLIC | Const.ACC_SUPER, new String[] { "graphql.schema.DataFetcher" });

        _cp = _cg.getConstantPool();
        _factory = new InstructionFactory(_cg, _cp);
    }

    public void create(OutputStream out) throws IOException {
        createFields();
        createMethod_0();
        createMethod_1();
        createMethod_2();
        createMethod_3();
        _cg.getJavaClass().dump(out);
    }

    private void createFields() {
        FieldGen field;

        field = new FieldGen(Const.ACC_PUBLIC | Const.ACC_STATIC | Const.ACC_FINAL, new ObjectType("MyFetcher"), "instance", _cp);
        _cg.addField(field.getField());
    }

    private void createMethod_0() {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(Const.ACC_PRIVATE, Type.VOID, Type.NO_ARGS, new String[] {  }, "<init>", "MyFetcher", il, _cp);

        InstructionHandle ih_0 = il.append(_factory.createLoad(Type.OBJECT, 0));
        il.append(_factory.createInvoke("java.lang.Object", "<init>", Type.VOID, Type.NO_ARGS, Const.INVOKESPECIAL));
        InstructionHandle ih_4 = il.append(_factory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        _cg.addMethod(method.getMethod());
        il.dispose();
    }

    private void createMethod_1() {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(Const.ACC_PUBLIC, Type.OBJECT, new Type[] { new ObjectType("graphql.schema.DataFetchingEnvironment") }, new String[] { "arg0" }, "get", "MyFetcher", il, _cp);

        InstructionHandle ih_0 = il.append(_factory.createLoad(Type.OBJECT, 1));
        il.append(_factory.createInvoke("graphql.schema.DataFetchingEnvironment", "getSource", Type.OBJECT, Type.NO_ARGS, Const.INVOKEINTERFACE));
        BranchInstruction ifnonnull_6 = _factory.createBranchInstruction(Const.IFNONNULL, null);
        il.append(ifnonnull_6);
        InstructionHandle ih_9 = il.append(new PUSH(_cp, "HEllo WOLRD"));
        il.append(_factory.createReturn(Type.OBJECT));
        InstructionHandle ih_12 = il.append(_factory.createLoad(Type.OBJECT, 1));
        il.append(_factory.createInvoke("graphql.schema.DataFetchingEnvironment", "getSource", Type.OBJECT, Type.NO_ARGS, Const.INVOKEINTERFACE));
        il.append(new INSTANCEOF(_cp.addClass(Type.STRING)));
        BranchInstruction ifeq_21 = _factory.createBranchInstruction(Const.IFEQ, null);
        il.append(ifeq_21);
        InstructionHandle ih_24 = il.append(new PUSH(_cp, "SHIT"));
        il.append(_factory.createReturn(Type.OBJECT));
        InstructionHandle ih_27 = il.append(new PUSH(_cp, "WTF"));
        il.append(_factory.createReturn(Type.OBJECT));
        ifnonnull_6.setTarget(ih_12);
        ifeq_21.setTarget(ih_27);
        method.setMaxStack();
        method.setMaxLocals();
        _cg.addMethod(method.getMethod());
        il.dispose();
    }

    private void createMethod_2() {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(Const.ACC_PROTECTED, Type.VOID, Type.NO_ARGS, new String[] {  }, "finalize", "MyFetcher", il, _cp);

        InstructionHandle ih_0 = il.append(_factory.createFieldAccess("java.lang.System", "out", new ObjectType("java.io.PrintStream"), Const.GETSTATIC));
        il.append(new PUSH(_cp, "U think true !"));
        il.append(_factory.createInvoke("java.io.PrintStream", "println", Type.VOID, new Type[] { Type.STRING }, Const.INVOKEVIRTUAL));
        InstructionHandle ih_8 = il.append(_factory.createLoad(Type.OBJECT, 0));
        il.append(_factory.createInvoke("java.lang.Object", "finalize", Type.VOID, Type.NO_ARGS, Const.INVOKESPECIAL));
        InstructionHandle ih_12 = il.append(_factory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        _cg.addMethod(method.getMethod());
        il.dispose();
    }

    private void createMethod_3() {
        InstructionList il = new InstructionList();
        MethodGen method = new MethodGen(Const.ACC_STATIC, Type.VOID, Type.NO_ARGS, new String[] {  }, "<clinit>", "MyFetcher", il, _cp);

        InstructionHandle ih_0 = il.append(_factory.createNew("MyFetcher"));
        il.append(InstructionConst.DUP);
        il.append(_factory.createInvoke("MyFetcher", "<init>", Type.VOID, Type.NO_ARGS, Const.INVOKESPECIAL));
        il.append(_factory.createFieldAccess("MyFetcher", "instance", new ObjectType("MyFetcher"), Const.PUTSTATIC));
        il.append(_factory.createReturn(Type.VOID));
        method.setMaxStack();
        method.setMaxLocals();
        _cg.addMethod(method.getMethod());
        il.dispose();
    }



}