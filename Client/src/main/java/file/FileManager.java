package file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager {

    private static FileManager INSTANCE = null;
    private FileMapper fileMapper;

    public static FileManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public List<ClientFile> getListByPath(String path) {
        File[] directories;
        if (path.equals("root"))
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

    public List<ClientFile> getListByPath2(String path) throws IOException {
        if (path.equals("root"))
            return getListByPath(path);
        Stream<Path> list = Files.list(Paths.get(path));
        List<ClientFile> result = list.map(p ->
        {
            try {
                return new ClientFile(p, fileMapper.getFileType(p));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        return result;
    }


    private static class LazyHolder {
        private static final FileManager INSTANCE = new FileManager();
    }

    private FileManager() {
        fileMapper = new FileMapper();
    }
}
