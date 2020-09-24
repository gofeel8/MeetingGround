package com.example.demo.dto;

import lombok.Data;

@Data
public class Simil {
	private int idx;
	private double similarity;
    public Simil(int idx,double similarity) {
    	this.idx=idx;
    	this.similarity=similarity;
    }
    
}
