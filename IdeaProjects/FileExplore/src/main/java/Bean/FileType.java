package Bean;

import lombok.Data;
import lombok.Setter;

public enum FileType {
    FILE("file"),
    DIR("directory"),
    DOCX("docx"),
    PDF("PDF"),
    OTHER("")
    ;

    private String type;

    FileType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
