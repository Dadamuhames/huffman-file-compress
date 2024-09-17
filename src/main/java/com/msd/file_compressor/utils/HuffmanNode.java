package com.msd.file_compressor.utils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HuffmanNode implements Comparable<HuffmanNode> {
  public Integer value;
  public Character character;
  public HuffmanNode left;
  public HuffmanNode right;

  public HuffmanNode(HuffmanNode left, HuffmanNode right) {
    this.value = left.getValue() + right.getValue();
    this.left = left;
    this.right = right;
  }

  public HuffmanNode(Integer value, Character character) {
    this.value = value;
    this.character = character;
  }

  public HuffmanNode(Character character) {
    this.character = character;
  }

  public Integer getValue() {
    return this.value;
  }

  @Override
  public int compareTo(HuffmanNode n) {
    return Integer.compare(value, n.getValue());
  }
}
