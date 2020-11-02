import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyClassLoader extends ClassLoader {

    public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        Class helloXlass =  new MyClassLoader().loadClassByPath("Week_01/Hello.xlass");

        Method helloMethod =  helloXlass.getMethod("hello");
        helloMethod.invoke(helloXlass.newInstance());
    }

    public Class loadClassByPath(String path) throws IOException {

        //read xlass to byte array
        byte[] readedBytes = new byte[2048];
        FileInputStream inputStream = new FileInputStream(path);
        int byteNumber = inputStream.read(readedBytes);

        //get the final bytes
        byte[] finalBytes = new byte[byteNumber];
        System.arraycopy(readedBytes, 0 , finalBytes, 0, byteNumber);

        //decode by add back 255
        for (int i = 0; i < byteNumber; i++) {
            finalBytes[i] = (byte) (255 - finalBytes[i]);
        }

        //need close the resources
        inputStream.close();

        return defineClass("Hello",finalBytes, 0, byteNumber);
    }

}
