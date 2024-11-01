import java.io.*;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    private static final Scanner in = new Scanner(System.in);

    public static int checkIntLimit(int min, int max) {
        while (true) {
            try {
                int n = Integer.parseInt(in.nextLine());
                if (n < min || n > max) {
                    throw new NumberFormatException();
                }
                return n;
            } catch (NumberFormatException e) {
                System.err.println("Invalid input! Please enter a number between " + min + " and " + max);
            }
        }
    }

    public static String checkString() {
        while (true) {
            String str = in.nextLine().trim();
            
            // Kiểm tra và loại bỏ dấu ngoặc kép đầu và cuối nếu có
            if (str.startsWith("\"") && str.endsWith("\"")) {
                str = str.substring(1, str.length() - 1);
            }
            
            if (!str.isEmpty()) {
                return str;
            }
            System.err.println("Input cannot be empty!");
        }
    }
    

    public static void zipFile() {
        System.out.print("Enter Source Folder: ");
        String pathSrc = checkString();
        System.out.print("Enter Destination Folder: ");
        String pathCompress = checkString();
        System.out.print("Enter Name for Zip File: ");
        String fileZipName = checkString();

        try {
            boolean success = compressTo(pathSrc, fileZipName, pathCompress);
            System.out.println(success ? "Compression successful!" : "Compression failed!");
        } catch (IOException e) {
            System.err.println("Compression error: " + e.getMessage());
        }
    }

    public static boolean compressTo(String pathSrc, String fileZipName, String pathCompress) throws IOException {
        File sourceFile = new File(pathSrc);
        if (!sourceFile.exists()) {
            System.err.println("Source file/folder does not exist.");
            return false;
        }

        File destinationDir = new File(pathCompress);
        if (!destinationDir.exists() && !destinationDir.mkdirs()) {
            System.err.println("Failed to create destination directory.");
            return false;
        }

        try (FileOutputStream fos = new FileOutputStream(new File(destinationDir, fileZipName + ".zip"));
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            File[] filesToZip = sourceFile.isDirectory() ? sourceFile.listFiles() : new File[]{sourceFile};
            if (filesToZip == null || filesToZip.length == 0) {
                System.err.println("No files to compress in the specified folder.");
                return false;
            }
            
            for (File file : filesToZip) {
                if (!file.isDirectory()) { // Ignore subdirectories
                    try (FileInputStream fis = new FileInputStream(file)) {
                        zipOut.putNextEntry(new ZipEntry(file.getName()));
                        byte[] bytes = new byte[1024];
                        int length;
                        while ((length = fis.read(bytes)) >= 0) {
                            zipOut.write(bytes, 0, length);
                        }
                        zipOut.closeEntry();
                    }
                }
            }
            return true;
        }
    }

    public static void unzipFile() {
        System.out.print("Enter Zip File Path: ");
        String pathZipFile = checkString();
        System.out.print("Enter Destination Folder: ");
        String pathExtract = checkString();

        try {
            boolean success = extractTo(pathZipFile, pathExtract);
            System.out.println(success ? "Extraction successful!" : "Extraction failed!");
        } catch (IOException e) {
            System.err.println("Extraction error: " + e.getMessage());
        }
    }

    public static boolean extractTo(String pathZipFile, String pathExtract) throws IOException {
        //Khởi tạo đối tượng File mới đại diện cho thư mục đích (pathExtract).
        File destDir = new File(pathExtract);
        if (!destDir.exists() && !destDir.mkdirs()) {
            System.err.println("Failed to create extraction directory.");
            return false;
        }
        //Đọc nội dung từ file zip
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(pathZipFile))) {
            ZipEntry zipEntry;
            byte[] buffer = new byte[1024];
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, zipEntry.getName());
                
                // Ensure parent directories exist
                File parent = newFile.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    System.err.println("Failed to create directory: " + parent);
                    return false;
                }
                // Ghi dữ liệu từ file zip vào ổ đĩa
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                zis.closeEntry();
            }
            return true;
        }
    }

    public static void menu() {
        while (true) {
            System.out.println("1. Compression");
            System.out.println("2. Extraction");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            int choice = checkIntLimit(1, 3);
            try {
                switch (choice) {
                    case 1 -> zipFile();
                    case 2 -> unzipFile();
                    case 3 -> {
                        System.out.println("Exiting program.");
                        return;
                    }
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            menu();
        } finally {
            in.close();  // Ensure Scanner is closed
        }
    }
}
