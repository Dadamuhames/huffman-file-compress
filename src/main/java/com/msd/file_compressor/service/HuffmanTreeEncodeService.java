package com.msd.file_compressor.service;

import com.msd.file_compressor.HuffmanNode;
import org.springframework.stereotype.Service;

@Service
public class HuffmanTreeEncodeService {
  private final StringBuilder encodedTree = new StringBuilder();

  public String encodeTree(final HuffmanNode root) {
    encodeTreeRecursive(root);
    return encodedTree.toString();
  }

  public void encodeTreeRecursive(final HuffmanNode node) {
    if (node == null) return;

    if (node.character != null) {
      encodedTree.append("1");

      String binString = Integer.toBinaryString(node.character);

      Integer extraZerosCount = 8 - binString.length();

      if (!extraZerosCount.equals(0)) {
        encodedTree.append("0".repeat(extraZerosCount));
      }

      encodedTree.append(binString);

      return;
    }
    encodedTree.append("0");

    encodeTree(node.left);
    encodeTree(node.right);
  }
}
