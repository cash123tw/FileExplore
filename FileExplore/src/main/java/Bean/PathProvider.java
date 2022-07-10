package Bean;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class PathProvider {

    @Getter
    final private String rootPath;

    @Getter
    @Setter
    private boolean hideRealPath = true;

    public PathProvider(String rootPath) throws IllegalAccessException {
        this.rootPath = checkFilePathIsOk(rootPath);
    }

    protected String concatWithRootPath(String path) throws IllegalAccessException {
        path = checkFilePathIsOk(path);

        if (!path.contains(rootPath)) {
            path = Path.of(rootPath,path).toString();
        }
        return path;
    }

    public String hideRealPath(String path) {
        return path.replace(rootPath, "");
    }

    protected String checkFilePathIsOk(String path) throws IllegalAccessException {
        if(Objects.isNull(path) || path.trim().equals("")){
            path = rootPath;
        } else if (path.contains("..")) {
            throw new IllegalAccessException("Can't use .. command;");
        }
        return Paths.get(path).toString();
    }

    public File getFile(String path) throws FileNotFoundException, IllegalAccessException {
        return getFile(path,false);
    }

    public Path getPath(String path) throws IllegalAccessException {
        String newPath =
                concatWithRootPath(path);
        return Paths.get(newPath);
    }

    public File getFile(String path,boolean Ignore_File_Not_Exist) throws IllegalAccessException, FileNotFoundException {
        File file
                = getPath(path).toFile();
        if (!file.exists() && !Ignore_File_Not_Exist) {
            throw new FileNotFoundException(String.format("File is not exist [%s]",file.toPath()));
        }
        return file;
    }

    public FileDetail makeFileDetail(File file) {
        return makeFileDetail(file,hideRealPath);
    }

    public FileDetail makeFileDetail(File file, boolean hideRealPath){
        return new FileDetail(
                file.isFile() ? FileType.FILE : FileType.DIR,
                file.getName(),
                hideRealPath ? hideRealPath(file.getPath()) : file.getPath()
        );
    }

}
