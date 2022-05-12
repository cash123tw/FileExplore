package Bean;

import Utils.TypeDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FileDetail {

    private FileType type;
    private String fileName;
    private String filePath;
    private String typeName;

    public FileDetail(FileType type, String fileName, String filePath) {
        this.type = type;
        this.filePath = filePath;
        setFileName(fileName);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.typeName = TypeDetect.detectFileTypeByName(fileName);
    }

}
