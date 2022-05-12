import Bean.PathProvider;
import Worker.FileExploreWorker;
import Worker.FileSaverWorker;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.function.Predicate;

import static java.nio.file.StandardOpenOption.*;

public class test {
    public static void main(String[] args) throws IOException, IllegalAccessException {
        long l1 = getTime();
        PathProvider p = new PathProvider("C:\\Mine\\Tomcat\\apache-tomcat-10.0.14-windows-x64\\apache-tomcat-10.0.14\\work\\Catalina");
        FileExploreWorker f = new FileExploreWorker(p);
        f.SearchFile("", new Predicate<File>() {
            @Override
            public boolean test(File file) {
                System.out.println("xxxxx");
                if(file.isDirectory()){
                    Path p1 = Paths.get(file.getPath());
                    Path p2 = Paths.get("C:\\Mine\\Tomcat\\apache-tomcat-10.0.14-windows-x64\\apache-tomcat-10.0.14\\work\\Catalina\\localhost");
                    if(p1.startsWith(p2)){
                        System.out.println();
                    }
                }
                return true;
            }
        });

    }

    public static void listString(List list) {
        list.forEach(System.out::println);
    }

    public static long getTime(){
        return System.currentTimeMillis();
    }

    public static class R1 implements Runnable{
        FileSaverWorker worker;

        public R1(FileSaverWorker worker) {
            this.worker = worker;
        }

        @Override
        public void run() {
            try {
                FileSaverWorker.SaveResult result = worker.saveNewFile("/test1/test2",
                        "test.txt",
                        true,
                        true,
                        ByteBuffer.wrap("Hello.AAA".getBytes(StandardCharsets.UTF_8)));

                listString(result.getMessages());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

}
