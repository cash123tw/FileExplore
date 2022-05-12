package Worker;


import Bean.FileDetail;
import Bean.PathProvider;
import lombok.Getter;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log
public class FileExploreWorker {

    @Getter
    private PathProvider pathProvider;

    public FileExploreWorker(PathProvider pathProvider) throws FileNotFoundException {
        this.pathProvider = pathProvider;
        log.info(String.format("File explore is build,root path is [%s]",pathProvider.getRootPath()));
    }

    public FileExploreWorker(String rootPath) throws IllegalAccessException, FileNotFoundException {
        this(new PathProvider(rootPath));
    }

    public FileDetail getFile(String path) throws FileNotFoundException, IllegalAccessException {
        File file
                = pathProvider.getFile(path, false);
        FileDetail fileDetail
                = pathProvider.makeFileDetail(file,false);
        return fileDetail;
    }

    public List<FileDetail> getFileList(String path) throws IllegalAccessException, FileNotFoundException {

        File file
                = pathProvider.getFile(path);

        List<FileDetail> files
                = Arrays
                .stream(file.listFiles())
                .map(pathProvider::makeFileDetail)
                .collect(Collectors.toList());

        return files;
    }

    public List<FileDetail> SearchFile(String path, String fileName) throws IOException, IllegalAccessException {

        return SearchFile(path,
                (file) -> {
                    String name
                            = file.getName().toLowerCase();

                    if (name.contains(fileName.toLowerCase())) {
                        return true;
                    }
                    return false;
                });
    }

    public List<FileDetail> SearchFile(String path,Predicate<File> filter) throws IllegalAccessException, IOException {
        File file
                = pathProvider.getFile(path);

        return SearchFile0(file.toPath(),filter);
    }

    public List<FileDetail> SearchFile0
            (Path rootPath, Predicate<File> predicate) throws IOException {
        if (!rootPath.toFile().exists()) {
            throw new FileNotFoundException(
                    String.format("File path [%s] is not exist.\n", rootPath)
            );
        }

        DefineBasicFileVisitor visitor
                = new DefineBasicFileVisitor(predicate, this);
        Files.walkFileTree(rootPath, visitor);

        return visitor.getResult();
    }

    public class DefineBasicFileVisitor extends SimpleFileVisitor<Path> {
        private Predicate<File> predicate;
        @Getter
        private List<FileDetail> result;
        private FileExploreWorker worker;

        public DefineBasicFileVisitor(Predicate<File> predicate, FileExploreWorker worker) {
            this.predicate = predicate;
            this.worker = worker;
            result = new ArrayList<>();
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

            File file
                    = dir.toFile();

            if (attrs.isDirectory()) {
                String[] dirs
                        = file.list();

//                if (dirs == null || dirs.length == 0) {
//                    return FileVisitResult.SKIP_SUBTREE;
//                }
            }

            if (predicate.test(file)) {
                addFile(dir);
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            File file0
                    = file.toFile();

            if (predicate.test(file0)) {
                addFile(file);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.SKIP_SUBTREE;
        }

        private void addFile(Path path) {
            FileDetail file
                    = pathProvider.makeFileDetail(path.toFile());
            result.add(file);
        }
    }

}
