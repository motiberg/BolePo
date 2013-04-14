package com.bergerlavy.bolepo.shareddata;

public class Misc {

	public static String pumpStrToDoubleCharacters(String str) {
		if (str != null) {
			if (str.length() == 2)
				return str;
			if (str.length() == 1)
				return "" + 0 + str;
			if (str.length() == 0)
				return "" + 0 + "" + 0;
		}
		return null;
	}
}
