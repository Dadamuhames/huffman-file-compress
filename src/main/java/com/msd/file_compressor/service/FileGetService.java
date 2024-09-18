package com.msd.file_compressor.service;


import com.msd.file_compressor.AnsiConstants;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileGetService {
    private final Terminal terminal;

    public File getFile(final String path) {
        File fileFromPath = new File(path);

        if (!fileFromPath.exists()) {
            terminal.writer().println(AnsiConstants.ANSI_RED + "File not exists! Try another location!");
            return null;
        }

        return fileFromPath;
    }

}
