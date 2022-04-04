import Bean.PathProvider;
import Worker.FileSaverWorker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

import static java.nio.file.StandardOpenOption.*;

public class test {
    public static void main(String[] args) throws IOException, IllegalAccessException {
        long l1 = getTime();
        PathProvider p = new PathProvider("D:/");
        FileSaverWorker worker = new FileSaverWorker(p);

        for (int i = 0; i < 20; i++) {
            new Thread(new R1(worker)).start();
        }

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
