package Utils;

public class TypeDetect {

    public static String detectFileTypeByName(String fileName){
        int len
                = fileName.length();
        int endIdx
                = fileName.lastIndexOf('.');

        if(len == 0 || !fileName.contains(".") || endIdx == -1){
            return "null";
        }else {
            return fileName.substring(endIdx+1,len);
        }
    };

}
