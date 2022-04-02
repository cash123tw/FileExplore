import Bean.FileDetail;
import Worker.FileExploreWorker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class test {

    public static void main(String[] args) throws IOException, IllegalAccessException {
        FileExploreWorker worker
                = new FileExploreWorker("D:\\");

        worker.setHideRealPath(true);
        List<FileDetail> result = worker
                .SearchFile("/","FileDetail.class");
//        List<FileDetail> result = worker.getFileList("honeywell");
        result.forEach(System.out::println);
    }

}
