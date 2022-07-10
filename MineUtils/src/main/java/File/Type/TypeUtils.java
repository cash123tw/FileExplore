package File.Type;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class TypeUtils {

    public static String replaceFileType(String target, String replaceType) {
        String replace = target;
        String type = getType(Paths.get(target));
        if (Objects.nonNull(type)) {
            replace = target.replace("."+type, "."+replaceType);
        }
        return replace;
    }

    /**
     * Give replaceFileType("C://dirA/dirB/test.txt","docx")
     * return "C://dirA/dirB/test.docx"
     *
     * @param target      replace target path
     * @param replaceType replace target type
     * @return replaced type path
     */
    public static Path replaceFileType(Path target, String replaceType) {
        String result = replaceFileType(target.toString(), replaceType);
        return Paths.get(result);
    }

    public static String getType(Path path) {
        int nameCount = path.getNameCount();

        if (nameCount > 0) {
            String name = path.getName(nameCount - 1).toString();
            int lastDot = name.lastIndexOf(".");

            if (lastDot != -1 && name.length() != lastDot + 1) {
                return name.substring(lastDot + 1);
            }
        }

        return null;
    }
}

