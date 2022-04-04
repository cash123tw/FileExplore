package Bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.File;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FileDetail {

    private FileType type;
    private String fileName;
    private String filePath;

}
