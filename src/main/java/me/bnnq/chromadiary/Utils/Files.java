package me.bnnq.chromadiary.Utils;

import org.springframework.web.multipart.MultipartFile;

public class Files
{
    public static String getFileExtension(MultipartFile file)
    {
        String filename = file.getOriginalFilename();

        assert filename != null;
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) {
            return "unknown";
        }

        return filename.substring(dotIndex + 1);
    }
}
