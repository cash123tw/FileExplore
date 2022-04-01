package Worker;


import Bean.FileDetail;
import Bean.FileType;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
public class FileExploreWorker {

    final private String rootPath;
    final private File file;

    @Setter
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

    public String concatWithRootPath(String path) {
        if (!path.contains(rootPath)) {
            if (!path.startsWith("/")) {
                path = "/".concat(path);
            }
            path = rootPath.concat(path);
        }
        return path;
    }

    public String hideRealPath(String path) {
        return path.replace(rootPath, "");
    }

    private File getFile(String path) throws FileNotFoundException, IllegalAccessException {
        String newPath =
                concatWithRootPath(path);
        checkFilePathIsOk(path);

        File file
                = new File(newPath);
        if (!file.exists()) {
            throw new FileNotFoundException("File is not exist");
        }

        return file;
    }

    public void checkFilePathIsOk(String path) throws IllegalAccessException {
        if (path.contains("..")) {
            throw new IllegalAccessException("Can't use .. command;");
        }
    }

    public FileDetail makeFileDetail(File file) {
        return new FileDetail(
                file.isFile() ? FileType.FILE : FileType.DIR,
                file.getName(),
                hideRealPath ? hideRealPath(file.getPath()) : file.getPath()
        );
    }

    public List<FileDetail> searchFileByFileName(String fileName) throws Exception {

        overLoad = 0;
        List<FileDetail> result
                = searchFileByFileName0(fileName, new File(rootPath), new ArrayList<>());
        System.out.printf("Search File Runtime Count : [%d]\n", overLoad);

        return result;
    }

    private int overLoad;

    private List<FileDetail> searchFileByFileName0(String fileName, File rootFile, List<FileDetail> result) throws Exception {
        if (overLoad > 10000) {
            throw new Exception("Over Load");
        }

        overLoad++;

        File[] ar
                = rootFile.listFiles();

        if (ar != null) {
            Arrays
                    .stream(ar)
                    .map(file -> {
                        if (file.isDirectory()) {
                            try {
                                searchFileByFileName0(fileName, file, result);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return null;
                            }
                        }
                        return file;
                    })
                    .filter(file -> {
                        if (!Objects.isNull(file) && file.getName().toLowerCase().contains(fileName.toLowerCase())) {
                            return true;
                        } else {
                            return false;
                        }
                    })
                    .map(this::makeFileDetail)
                    .forEach(result::add);
        }
        return result;
    }

    private List<FileDetail> searchFileByFileNameNio0(String fileName, Path rootPath, List<FileDetail> result) {

        Files.walkFileTree()

        return result;
    }

    public class DefineBasicFileVisitor extends SimpleFileVisitor<Path> {
        private String findName;

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (attrs.isDirectory()) {
                String[] dirs = dir
                        .toFile()
                        .list();

                if (dirs == null || dirs.length == 0) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.SKIP_SUBTREE;
        }

    }

}
