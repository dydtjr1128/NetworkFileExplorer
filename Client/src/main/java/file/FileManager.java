package file;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    public static FileManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    public List<ClientFile> getListByPath(String path) {
        long t = System.currentTimeMillis();
        final List<ClientFile> result = new ArrayList<>();
        if (path.equals("root")) {
            for (Path p : FileSystems.getDefault().getRootDirectories()) {
                result.add(new ClientFile(p.toString(), true, 0, FileMapper.FOLDER_TYPE, 0));
            }
            return result;
        }
        StringBuilder builder = new StringBuilder();
        Path p = Paths.get(path);
        try {
            DirectoryStream<Path> stream;
            BasicFileAttributes attr;
            stream = Files.newDirectoryStream(p);
            for (Path entry : stream) {
                attr = Files.readAttributes(entry, BasicFileAttributes.class);
                result.add(new ClientFile(entry.getFileName().toString(), attr));
            }
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(System.currentTimeMillis() - t + "@@@");
        return result;
    }

    public boolean changeFileName(String fromPath, String name) {
        File sourceFile = new File(fromPath);

        File destFile = new File(sourceFile.getParent() + "\\" + name);
        System.out.println(sourceFile + " " + destFile);

        if (sourceFile.renameTo(destFile)) {
            System.out.println("File renamed successfully");
            return true;
        } else {
            System.out.println("Failed to rename file");
            return false;
        }
    }

    public boolean deleteFile(String payload) {
        Path path = Paths.get(payload);
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean copyFile(String fromPath, String toPath) {
        int idx = fromPath.lastIndexOf("\\");
        String filename = fromPath.substring(idx + 1);
        Path sourcePath = Paths.get(fromPath);
        Path destinationPath = Paths.get(toPath + "\\" + filename);
        try {
            Files.copy(sourcePath, destinationPath);
        } catch (FileAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean moveFile(String fromPath, String toPath) {
        int idx = fromPath.lastIndexOf("\\");
        String filename = fromPath.substring(idx + 1);
        Path sourcePath = Paths.get(fromPath);
        Path destinationPath = Paths.get(toPath + "\\" + filename);
        try {
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (FileAlreadyExistsException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static class LazyHolder {
        private static final FileManager INSTANCE = new FileManager();
    }

    private FileManager() {
    }
}
