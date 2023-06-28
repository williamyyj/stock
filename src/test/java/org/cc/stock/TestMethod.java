package org.cc.stock;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class TestMethod {
	@Test
	public void test_divide() {
		int X = 54;
		int m = 5;
		int[] parts = new int[m];
		int quotient = X / m;
		int remainder = X % m;
		Arrays.fill(parts, quotient);
		for (int i = 0; i < remainder; i++) {
			parts[i]++;
		}

		System.out.println(Arrays.toString(parts));
	}
}
