package com.msd.file_compressor.service;

import com.msd.file_compressor.utils.HuffmanNode;
import org.springframework.stereotype.Service;


@Service
public class HuffmanTreeEncodeService {
  private StringBuilder encodedTree = new StringBuilder();

  public String encodeTree(final HuffmanNode root) {
    encodedTree = new StringBuilder();
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
