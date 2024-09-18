package com.msd.file_compressor.commands;

import com.msd.file_compressor.AnsiConstants;
import com.msd.file_compressor.service.BinFileService;
import com.msd.file_compressor.service.FileGetService;
import com.msd.file_compressor.service.HuffmanDecodeService;
import com.msd.file_compressor.service.HuffmanEncodeService;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
  private final FileGetService fileGetService;

  @ShellMethod(key = "compress")
  public void compress(@ShellOption String file) {
    File fileFromPath = fileGetService.getFile(file);

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


  @ShellMethod(key = "decompress")
  public void decompress(@ShellOption String binFile) {
    File fileFromPath = fileGetService.getFile(binFile);

    if (!fileFromPath.getName().endsWith(".bin")) {
      terminal.writer().println(AnsiConstants.ANSI_RED + "File type invalid: .bin type required");
      return;
    }

    String binFileData;

    try {
      binFileData = binFileService.readBinaryFile(fileFromPath);
    } catch (IOException ex) {
      terminal.writer().println(AnsiConstants.ANSI_RED + ex.getMessage());
      return;
    }

    String[] binFileAsArray = binFileData.split("\n");

    if (binFileAsArray.length != 2) {
      terminal
          .writer()
          .println(
              AnsiConstants.ANSI_RED
                  + "File invalid! Make sure that file is compressed by this tool!");
      return;
    }

    HuffmanDecodeService huffmanDecodeService =
        new HuffmanDecodeService(binFileAsArray[0], binFileAsArray[1]);

    String decodedData = huffmanDecodeService.decode();

    terminal.writer().println(decodedData);

    File decodedFile;

    try {
      decodedFile = binFileService.writeFile(decodedData);
    } catch (IOException ex) {
      terminal.writer().println(AnsiConstants.ANSI_RED + ex.getMessage());
      return;
    }

    terminal.writer().println("Decompressed file path: " + decodedFile.getPath());

    terminal.writer().println("Decompressed file size: " + decodedFile.length());

    terminal.writer().println("Compressed file size: " + fileFromPath.length());
  }
}
