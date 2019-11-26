package file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static FileManager INSTANCE = null;
    private FileMapper fileMapper;

    public static FileManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public List<ClientFile> getListByPath(String path) {
        File[] directories;
        if(path.equals("root"))
            directories = File.listRoots();
        else
            directories = new File(path).listFiles();
        List<ClientFile> fileList = new ArrayList<>();
        if (directories != null) {
            for (File directory : directories) {
                fileList.add(new ClientFile(directory, fileMapper.getFileType(directory)));
            }
            return fileList;
        }
        return null;
    }

    private static class LazyHolder {
        private static final FileManager INSTANCE = new FileManager();
    }

    private FileManager() {
        fileMapper = new FileMapper();
    }
}
