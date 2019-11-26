package file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileMapper {
    private Map<String, String> hashMap;

    public FileMapper() {
        hashMap = new HashMap<>();
        init();
    }

    private void init() {
        hashMap.put("docx", "Microsoft Word 문서");
        hashMap.put("ppt", "Microsoft PowerPoint 문서");
        hashMap.put("pptx", "Microsoft PowerPoint 문서");
        hashMap.put("msi", "Windows Installer 패키지");
        hashMap.put("scr", "화면 보호기");
        hashMap.put("exe", "응용 프로그램");
        hashMap.put("txt", "텍스트 문서");
        hashMap.put("log", "텍스트 문서");
    }

    private String getFileType(String file) {
        int idx = file.lastIndexOf(".");
        String value = file.substring(idx + 1);
        if (hashMap.containsKey(value))
            return hashMap.get(value);
        return value.toUpperCase() + " 파일";
    }

    public String getFileType(Path path) {
        if (Files.isDirectory(path)) return "파일 폴더";
        String file = path.getFileName().toString();
        int idx = file.lastIndexOf(".");
        String value = file.substring(idx + 1);
        if (hashMap.containsKey(value))
            return hashMap.get(value);
        return value.toUpperCase() + " 파일";
    }

    public String getFileType(File file) {
        if (file.isDirectory())
            return "파일 폴더";
        return getFileType(file.getName().toLowerCase());
    }

}
