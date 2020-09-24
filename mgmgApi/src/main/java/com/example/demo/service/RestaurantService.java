package com.example.demo.service;

import java.util.List;

import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

import com.example.demo.dto.Restaurant;


public interface RestaurantService {
	List<Restaurant> findByLocNear(Point loc,Distance distance);
}
