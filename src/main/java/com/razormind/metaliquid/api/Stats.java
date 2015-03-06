package com.razormind.metaliquid.api;

import javax.xml.bind.annotation.XmlRootElement;

public class Stats {
	
	double mean = 0;
	int count = 0;
	double max = Double.MIN_VALUE;
	double min = Double.MAX_VALUE;
	
	public double getMean() {
		return mean;
	}
	public void setMean(double mean) {
		this.mean = mean;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	
	@Override
	public String toString() {
		return "Stats [mean=" + mean + ", count=" + count + ", max=" + max
				+ ", min=" + min + "]";
	}

}