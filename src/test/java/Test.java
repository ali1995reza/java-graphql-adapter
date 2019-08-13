import com.sun.org.apache.bcel.internal.Constants;
import grphaqladapter.annotations.GraphqlField;
import org.apache.bcel.Repository;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.BCELifier;

import javax.swing.text.ParagraphView;
import java.io.*;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;

import static org.apache.bcel.Const.*;

public class Test {

    @GraphqlField(nullable = false)
    public Integer maMethod( String sex){
        return 1;
    }


    static ByteBuffer buffer = ByteBuffer.allocate(10000000);


    static enum  Myen{
        SS
    }
    public static void main(String[] args)throws Exception
    {



        BCELifier bceLifier = new BCELifier(Repository.lookupClass(Test.class), new OutputStream() {



            @Override
            public void write(int b) throws IOException {
                System.out.println("Writing ?");
                buffer.put((byte) b);
            }

        });


        buffer.flip();
        System.out.println(new String(buffer.array()));

        ClassGen cg = new ClassGen("HelloWorld", "java.lang.Object",
                "<generated>", ACC_PUBLIC | ACC_SUPER, null);
        ConstantPoolGen cp = cg.getConstantPool(); // cg creates constant pool
        InstructionList il = new InstructionList();

        MethodGen  mg = new MethodGen(ACC_STATIC | ACC_PUBLIC, // access flags
                Type.VOID,               // return type
                new Type[] {             // argument types
                        new ArrayType(Type.STRING, 1) },
                new String[] { "argv" }, // arg names
                "main", "HelloWorld",    // method, class
                il, cp);
        InstructionFactory factory = new InstructionFactory(cg);

        ObjectType i_stream = new ObjectType("java.io.InputStream");
        ObjectType p_stream = new ObjectType("java.io.PrintStream");

        il.append(factory.createNew("java.io.BufferedReader"));
        il.append(InstructionConst.DUP); // Use predefined constant
        il.append(factory.createNew("java.io.InputStreamReader"));
        il.append(InstructionConst.DUP);
        il.append(factory.createFieldAccess("java.lang.System", "in", i_stream, Constants.GETSTATIC));
        il.append(factory.createInvoke("java.io.InputStreamReader", "<init>",
                Type.VOID, new Type[] { i_stream },
                Constants.INVOKESPECIAL));
        il.append(factory.createInvoke("java.io.BufferedReader", "<init>", Type.VOID,
                new Type[] {new ObjectType("java.io.Reader")},
                Constants.INVOKESPECIAL));

        LocalVariableGen lg = mg.addLocalVariable("in",
                new ObjectType("java.io.BufferedReader"), null, null);
        int in = lg.getIndex();
        lg.setStart(il.append(new ASTORE(in))); // "i" valid from here

        lg = mg.addLocalVariable("argumentName", Type.STRING, null, null);
        int name = lg.getIndex();

        il.append(InstructionConst.ACONST_NULL);
        lg.setStart(il.append(new ASTORE(name))); // "argumentName" valid from here

        InstructionHandle try_start =
                il.append(factory.createFieldAccess("java.lang.System", "out", p_stream, Constants.GETSTATIC));

        il.append(new PUSH(cp, "Please enter your argumentName> "));
        il.append(factory.createInvoke("java.io.PrintStream", "print", Type.VOID,
                new Type[] { Type.STRING },
                Constants.INVOKEVIRTUAL));
        il.append(new ALOAD(in));
        il.append(factory.createInvoke("java.io.BufferedReader", "readLine",
                Type.STRING, Type.NO_ARGS,
                Constants.INVOKEVIRTUAL));
        il.append(new ASTORE(name));

        GOTO g = new GOTO(null);
        InstructionHandle try_end = il.append(g);

        InstructionHandle handler = il.append(InstructionConst.RETURN);
        mg.addExceptionHandler(try_start, try_end, handler, new ObjectType("java.io.IOException"));

        InstructionHandle ih =
                il.append(factory.createFieldAccess("java.lang.System", "out", p_stream, Constants.GETSTATIC));
        g.setTarget(ih);

        il.append(factory.createNew(Type.STRINGBUFFER));
        il.append(InstructionConst.DUP);
        il.append(new PUSH(cp, "Hello, "));
        il.append(factory.createInvoke("java.lang.StringBuffer", "<init>",
                Type.VOID, new Type[] { Type.STRING },
                Constants.INVOKESPECIAL));
        il.append(new ALOAD(name));
        il.append(factory.createInvoke("java.lang.StringBuffer", "append",
                Type.STRINGBUFFER, new Type[] { Type.STRING },
                Constants.INVOKEVIRTUAL));
        il.append(factory.createInvoke("java.lang.StringBuffer", "toString",
                Type.STRING, Type.NO_ARGS,
                Constants.INVOKEVIRTUAL));

        il.append(factory.createInvoke("java.io.PrintStream", "println",
                Type.VOID, new Type[] { Type.STRING },
                Constants.INVOKEVIRTUAL));
        il.append(InstructionConst.RETURN);

        mg.setMaxStack();
        cg.addMethod(mg.getMethod());
        il.dispose(); // Allow instruction handles to be reused
        cg.addEmptyConstructor(ACC_PUBLIC);

        cg.getJavaClass().dump("E:\\HelloWorld.class");

    }
}
