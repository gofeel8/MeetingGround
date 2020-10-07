package com.example.demo.util;

public class CosineSimilarity {	
	public double calCS(int[] A,int[] B) {
		if (A == null || B == null || A.length == 0 || B.length == 0 || A.length != B.length) {
    		return 2;
    	}

    	double sumProduct = 0;
    	double sumASq = 0;
    	double sumBSq = 0;
    	for (int i = 0; i < A.length; i++) {
    		sumProduct += A[i]*B[i];
    		sumASq += A[i] * A[i];
    		sumBSq += B[i] * B[i];
    	}
    	if (sumASq == 0 && sumBSq == 0) {
    		return 2.0;
    	}
    	
    	return sumProduct / (Math.sqrt(sumASq) * Math.sqrt(sumBSq));
	}
}
