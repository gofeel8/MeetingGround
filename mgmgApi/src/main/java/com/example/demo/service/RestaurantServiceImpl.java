package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import com.example.demo.dto.Restaurant;
import com.example.demo.repository.RestaurantRepository;

@Service
public class RestaurantServiceImpl implements RestaurantService{
    
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	@Override
	public List<Restaurant> findByLocWithin(Circle circle) {		
		return restaurantRepository.findByLocWithin(circle);
	}

}
