package com.SmartCity.smartcity;

public class Preference {
	private enum pref {
		noPref,
		dist,
		dur,
		qual
	}
	private pref prefValue;
	Preference(){
		prefValue = pref.noPref;
	}
	
}
