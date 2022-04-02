package Worker;


import Bean.FileDetail;
import Bean.FileType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Log
public class FileExploreWorker {

    final private String rootPath;
    final private File file;

    @Setter
    @Getter
    private boolean hideRealPath = true;

    /**
     * @param rootPath Basic Path for search.
     */
    public FileExploreWorker(String rootPath) throws FileNotFoundException {
        this(rootPath, false);
    }

    /**
     * @param rootPath     Basic path for search.
     * @param buildPathDir If file non exist,auto make a directory.
     */
    public FileExploreWorker(String rootPath, boolean buildPathDir) throws FileNotFoundException {
        this.rootPath = rootPath;
        this.file = new File(rootPath);

        if (!buildPathDir && !this.file.exists()) {
            throw new
                    FileNotFoundException(String.format("This File Path Not Exist [path : %s]", rootPath));
        } else {
            this.file.mkdirs();
        }

        log.info(String.format("File explore is build,root path is [%s]", rootPath));
    }

    public List<FileDetail> getFileList(String path) throws IllegalAccessException, FileNotFoundException {
        if (Objects.isNull(path) || path.trim().equals("")) {
            path = "";
        }

        File file
                = getFile(path);

        List<FileDetail> files
                = Arrays
                .stream(file.listFiles())
                .map(this::makeFileDetail)
                .collect(Collectors.toList());

        return files;
    }

    protected String concatWithRootPath(String path) throws IllegalAccessException {
        checkFilePathIsOk(path);

        if (!path.contains(rootPath)) {
            path = Path.of(rootPath,path).toString();
        }
        return path;
    }

    protected String hideRealPath(String path) {
        return path.replace(rootPath, "");
    }

    protected File getFile(String path) throws FileNotFoundException, IllegalAccessException {
        String newPath =
                concatWithRootPath(path);

        File file
                = new File(newPath);
        if (!file.exists()) {
            throw new FileNotFoundException("File is not exist");
        }

        return file;
    }

    protected void checkFilePathIsOk(String path) throws IllegalAccessException {
        if (path.contains("..")) {
            throw new IllegalAccessException("Can't use .. command;");
        }
    }

    protected FileDetail makeFileDetail(File file) {
        return new FileDetail(
                file.isFile() ? FileType.FILE : FileType.DIR,
                file.getName(),
                hideRealPath ? hideRealPath(file.getPath()) : file.getPath()
        );
    }

    public List<FileDetail> SearchFile(String path, String fileName) throws IOException, IllegalAccessException {

        return SearchFile(path,fileName,
                (file) -> {
                    String name
                            = file.getName().toLowerCase();

                    if (name.contains(fileName.toLowerCase())) {
                        return true;
                    }
                    return false;
                });
    }

    public List<FileDetail> SearchFile(String path,String fileName,Predicate<File> filter) throws IllegalAccessException, IOException {
        if (Objects.isNull(path)
                || path.trim().equals("")) {
            path = rootPath;
        } else {
            path = concatWithRootPath(path);
        }

        return SearchFile0(Paths.get(path),filter);
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

                if (dirs == null || dirs.length == 0) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
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
                    = makeFileDetail(path.toFile());
            result.add(file);
        }
    }

}
