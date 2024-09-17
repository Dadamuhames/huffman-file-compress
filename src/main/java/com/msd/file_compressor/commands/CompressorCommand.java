package com.msd.file_compressor.commands;

import com.msd.file_compressor.AnsiConstants;
import com.msd.file_compressor.service.BinFileService;
import com.msd.file_compressor.service.HuffmanEncodeService;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jline.terminal.Terminal;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@RequiredArgsConstructor
public class CompressorCommand {
  private final HuffmanEncodeService huffmanEncodeService;
  private final BinFileService binFileService;
  private final Terminal terminal;

  @ShellMethod(key = "compress")
  public void compress(@ShellOption String file) {

    File fileFromPath = new File(file);

    if (!fileFromPath.exists()) {
      terminal.writer().println(AnsiConstants.ANSI_RED + "File not exists! Try another location!");
      return;
    }

    String fileContent;

    try {
      fileContent = binFileService.readFile(fileFromPath);
    } catch (IOException ex) {
      terminal.writer().println(AnsiConstants.ANSI_RED + ex.getMessage());
      return;
    }

    fileContent = fileContent.trim();

    byte[] encodedBytes = huffmanEncodeService.encode(fileContent);

    File compressedFile;

    try {
      compressedFile = binFileService.writeBinaryFile(encodedBytes);
    } catch (IOException ex) {
      terminal.writer().println(AnsiConstants.ANSI_RED + ex.getMessage());
      return;
    }

    terminal.writer().println("Compressed file path: " + compressedFile.getPath());

    terminal.writer().println("Original file size: " + fileFromPath.length());

    terminal.writer().println("Compressed file size: " + compressedFile.length());
  }
}
