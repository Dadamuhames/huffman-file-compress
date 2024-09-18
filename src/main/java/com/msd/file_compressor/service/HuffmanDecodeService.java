package com.msd.file_compressor.service;

import com.msd.file_compressor.HuffmanNode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class HuffmanDecodeService {
  public String encodedTree;
  public String encodedText;

  public HuffmanNode decodeTree() {
    HuffmanNode node;

    Character currentChar = encodedTree.charAt(0);

    if (currentChar.equals('1')) {
      Character charFromBits = (char) Integer.parseInt(encodedTree.substring(1, 9), 2);
      node = new HuffmanNode(charFromBits);

      encodedTree = encodedTree.substring(9);

    } else {
      node = new HuffmanNode();
      encodedTree = encodedTree.substring(1);

      node.left = decodeTree();
      node.right = decodeTree();
    }



    return node;
  }

  public String decodeText(final HuffmanNode node) {
    StringBuilder result = new StringBuilder();

    HuffmanNode current = node;

    for (Character c : encodedText.toCharArray()) {
      if (c.equals('0')) {
        current = current.left;
      } else {
        current = current.right;
      }

      if (current.character != null) {
        result.append(current.character);
        current = node;
      }
    }

    return result.toString();
  }

  public String decode() {
    HuffmanNode node = decodeTree();

    return decodeText(node);
  }
}
