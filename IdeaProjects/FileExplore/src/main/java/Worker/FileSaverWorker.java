package Worker;

import Bean.PathProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@Log
public class FileSaverWorker {

    @Setter
    @Getter
    private int  work_count_limit = 3;
    private PathProvider pathProvider;
    @Getter
    private Integer work_count = 0;

    public FileSaverWorker(PathProvider pathProvider) {
        this.pathProvider = pathProvider;
        work_count = 0;
        log.info(String.format("File Saver is build root path [%s]", pathProvider.getRootPath()));
    }

    public SaveResult saveNewFile
            (String path_str,
             String fileName,
             boolean auto_create_dir,
             boolean over_write,
             ByteBuffer byteBuffer) throws IOException, IllegalAccessException {

        FileChannel channel = null;
        Path path0
                = Paths.get(pathProvider.getPath(path_str).toString(), fileName);
        SaveResult result
                = newSaveResult(path0);
        try {
            if (canStartWork()) {
                if (!over_write && Files.exists(path0)) {
                    result.addMessage("File already exist [%s]", path0.toString());
                } else if (checkDirIsExist(path0.getParent(), auto_create_dir)) {
                    checkFileIsExist(path0, true, false);
                    channel
                            = FileChannel.open(path0, WRITE, TRUNCATE_EXISTING);
                    saveFile0(channel,byteBuffer);
                    result.setResult(true);
                    result.addMessage("Save file success in [%s]", path0.toString());
                } else {
                    result.addMessage("File path is not exist [%s]", path0.toString());
                }
            } else {
                result.addMessage("File Worker is over load plz wait a minute then retry");

            }
        } catch (OverlappingFileLockException e) {
            result.addMessage("File is write by other thread");
        } finally {
            stopWork();
            if (Objects.nonNull(channel)) {
                channel.close();
            }
        }

        return result;
    }

    private void saveFile0(FileChannel channel,ByteBuffer bf) throws IOException {
        FileLock lock
                = channel.tryLock();
        channel.write(bf);
        lock.release();
    }

    private boolean checkDirIsExist(Path path, boolean autoCreate) throws IOException {
        boolean result;

        out:
        if (Files.exists(path) && Files.isDirectory(path)) {
            result = true;
        } else if (autoCreate) {
            Files.createDirectories(path);
            log.info(
                    String.format("Class : [%s] auto create directory [%s]", this.getClass().getSimpleName(), path)
            );
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    private boolean checkFileIsExist(Path path, boolean autoCreate, boolean overWrite) throws IOException {
        if (Files.exists(path)) {
            if (overWrite) {
                Files.deleteIfExists(path);
                Files.createFile(path);
            }
        } else {
            if (autoCreate) {
                Files.createFile(path);
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean canStartWork() {
        synchronized (work_count) {
            if(work_count <= work_count_limit){
                work_count++;
                return true;
            } else {
                return false;
            }
        }
    }

    private void stopWork() {
        synchronized (work_count) {
            if (work_count > 0)
                work_count--;
        }
    }

    private SaveResult newSaveResult(Path path) {
        return new SaveResult(path);
    }

    @Getter
    @AllArgsConstructor
    public static class SaveResult {
        @Setter
        private boolean result;
        private List<String> messages;
        private Path path;

        public SaveResult(Path path) {
            this.path = path;
            messages = new ArrayList<>();
            result = false;
        }

        public void addMessage(String format, Object... args) {
            String message = "";

            if (Objects.isNull(format)) {
                return;
            }

            try {
                message = String.format(format, args);
            } catch (Exception e) {
                message
                        = String
                        .format("Exception is happen in SaveResult.addMessage() message is : \n[%s]", e.getMessage());
            }

            this.messages.add(message);
        }
    }

}
