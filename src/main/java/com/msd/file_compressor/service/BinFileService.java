package com.msd.file_compressor.service;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BinFileService {
  public String readFile(final String filePath) throws IOException {
    try (FileReader fileReader = new FileReader(filePath, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(fileReader)) {

      StringBuilder stringBuilder = new StringBuilder();
      String line;

      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line);
        stringBuilder.append("\n");
      }

      return stringBuilder.toString();
    }
  }

  public String readBinaryFile(final String filePath) throws IOException {
    StringBuilder builder = new StringBuilder();

    try (InputStream inputStream = new FileInputStream(filePath)) {
      int data;

      int treeEndAt = inputStream.read();
      int dataEndAt = inputStream.read();

      byte[] treeLengthInBytes = inputStream.readNBytes(4);
      byte[] dataLengthInBytes = inputStream.readNBytes(4);

      long treeLength = new BigInteger(treeLengthInBytes).longValue() * 8;
      long dataLength = new BigInteger(dataLengthInBytes).longValue() * 8;

      int treeCount = 0;

      while (treeCount < treeLength) {
        treeCount += 8;
        data = inputStream.read();
        String binString = getBinString(data);

        builder.append(binString);

        if (treeCount + 8 >= treeLength && treeEndAt < 8) {
          data = inputStream.read();
          binString = getBinString(data);
          builder.append(binString.substring(treeEndAt));
          break;
        }
      }

      builder.append("\n");

      int dataCount = 0;

      while ((data = inputStream.read()) != -1) {
        dataCount += 8;
        String binString = getBinString(data);
        builder.append(binString);

        if (dataCount + 8 >= dataLength && dataEndAt < 8) {
          data = inputStream.read();
          binString = getBinString(data);
          builder.append(binString.substring(dataEndAt));
          break;
        }
      }
    }

    return builder.toString();
  }

  public String getBinString(final Integer data) {
    String binString = Integer.toBinaryString(data);

    if (binString.length() != 8) {
      binString = "0".repeat(8 - binString.length()) + binString;
    }

    return binString;
  }

  public String writeBinaryFile(final List<Byte> bytesToWrite) throws IOException {
    String now = String.valueOf(System.currentTimeMillis()).replace(".", "");

    String fileName = String.format("%s.bin", now);

    try (OutputStream outputStream = new FileOutputStream(fileName);
        DataOutputStream dos = new DataOutputStream(outputStream)) {

      for (Byte byt : bytesToWrite) {
        dos.writeByte(byt);
      }

      return fileName;
    }
  }
}
