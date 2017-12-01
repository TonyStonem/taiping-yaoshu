package xjw.app.hencoder.debug.classloader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by xjw on 2017/12/1 14:24
 * Email 1521975316@qq.com
 * <p>
 * 用来加载存储在文件系统上的 Java 字节代码
 */

public class FileSystemClassLoader extends ClassLoader {

    private final String rootDir;

    public FileSystemClassLoader(String rootDir) {
        this.rootDir = rootDir;
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = getClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        }
        return defineClass(name, classData, 0, classData.length);
    }

    private byte[] getClassData(String className) {
        String path = className2Path(className);
        FileInputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            in = new FileInputStream(path);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 4];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, buffer.length);
            }
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String className2Path(String className) {
        return rootDir + File.separatorChar + className.replace('.'
                , File.separatorChar) + ".class";
    }


}
