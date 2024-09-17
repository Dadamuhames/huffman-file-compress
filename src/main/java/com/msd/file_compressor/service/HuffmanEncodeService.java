package com.msd.file_compressor.service;

import com.msd.file_compressor.utils.HuffmanNode;
import java.math.BigInteger;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HuffmanEncodeService {
  private final HuffmanTreeEncodeService huffmanTreeEncodeService;

  public List<Byte> encode(final String text) {
    Map<Character, Integer> charFrequencies = fillFrequencies(text);

    return encodeToBytes(text, charFrequencies);
  }

  public Map<Character, Integer> fillFrequencies(final String text) {
    Map<Character, Integer> charFrequencies = new HashMap<>();

    for (char c : text.toCharArray()) {
      Integer count = charFrequencies.get(c);
      charFrequencies.put(c, count != null ? count + 1 : 1);
    }

    return charFrequencies;
  }

  public Map<Character, String> getHuffmanCodes(final HuffmanNode node, final String code) {
    Map<Character, String> huffmanCodes = new HashMap<>();

    getHuffmanCodes(node, code, huffmanCodes);

    return huffmanCodes;
  }

  public void getHuffmanCodes(
      final HuffmanNode node, final String code, final Map<Character, String> huffmanCodes) {
    if (node == null) return;

    if (node.character != null) {
      huffmanCodes.put(node.character, code);
      return;
    }

    getHuffmanCodes(node.left, code.concat("0"), huffmanCodes);
    getHuffmanCodes(node.right, code.concat("1"), huffmanCodes);
  }

  public HuffmanNode getTree(final Map<Character, Integer> charFrequencies) {
    Queue<HuffmanNode> queue = new PriorityQueue<>();

    charFrequencies.forEach((key, value) -> queue.add(new HuffmanNode(value, key)));

    while (queue.size() > 1) {
      HuffmanNode newNode = new HuffmanNode(queue.poll(), queue.poll());
      queue.add(newNode);
    }

    return queue.poll();
  }

  public List<Byte> encodeToBytes(final String text, final Map<Character, Integer> chars) {
    HuffmanNode root = getTree(chars);

    Map<Character, String> huffmanCodes = getHuffmanCodes(root, "");
    String encodedTree = huffmanTreeEncodeService.encodeTree(root);

    String encodedText = getEncodedText(text, huffmanCodes);

    byte[] textAsBytes = getEncodedBytes(encodedText);

    byte[] treeAsBytes = getEncodedBytes(encodedTree);

    Integer treeEndAt = 8 - encodedTree.length() % 8;
    Integer dataEndAt = 8 - encodedText.length() % 8;

    List<Byte> dataInBytes = new ArrayList<>();

    dataInBytes.add(treeEndAt.byteValue());
    dataInBytes.add(dataEndAt.byteValue());

    long treeLength = treeAsBytes.length;
    long dataLength = textAsBytes.length;

    byte[] treeLengthInBytes = BigInteger.valueOf(treeLength).toByteArray();
    byte[] dataLengthInBytes = BigInteger.valueOf(dataLength).toByteArray();

    for (byte b : new byte[4 - treeLengthInBytes.length]) dataInBytes.add(b);

    for (byte b : new byte[4 - dataLengthInBytes.length]) dataInBytes.add(b);

    for (byte b : textAsBytes) dataInBytes.add(b);

    for (byte b : textAsBytes) dataInBytes.add(b);

    return dataInBytes;
  }

  public byte[] getEncodedBytes(final String encodedString) {
    int byteArrayLength = encodedString.length() / 8;

    int remain = encodedString.length() % 8;

    byte[] buffer;

    if (remain == 0) {
      buffer = new byte[byteArrayLength];
    } else {
      buffer = new byte[byteArrayLength + 1];
    }

    for (int i = 0; i < byteArrayLength; i++) {
      String binaryString = encodedString.substring(8 * i, 8 * (i + 1));

      buffer[i] = (byte) Integer.parseInt(binaryString, 2);
    }

    if (remain != 0) {
      String binaryString = encodedString.substring(encodedString.length() - remain);

      buffer[byteArrayLength] = (byte) Integer.parseInt(binaryString, 2);
    }

    return buffer;
  }

  public String getEncodedText(final String text, final Map<Character, String> huffmanCodes) {
    StringBuilder builder = new StringBuilder();

    for (char s : text.toCharArray()) {
      String code = huffmanCodes.get(s);

      builder.append(code);
    }

    return builder.toString();
  }
}
