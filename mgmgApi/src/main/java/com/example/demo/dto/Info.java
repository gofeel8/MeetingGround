package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Info {
	private float lat;
	private float lon;
	private boolean[] keys;
}
