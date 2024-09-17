package com.msd.file_compressor.commands;

import com.msd.file_compressor.service.BinFileService;
import com.msd.file_compressor.service.HuffmanEncodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@RequiredArgsConstructor
public class CompressorCommand {
  private final HuffmanEncodeService huffmanEncodeService;
  private final BinFileService binFileService;

  @ShellMethod(key = "compress")
  public String compress(@ShellOption String file) {

  }
}
