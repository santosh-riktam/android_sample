package com.riktamtech.android.sellabike.io;

import java.util.ArrayList;
import java.util.HashMap;

import org.xml.sax.Attributes;

import android.content.Context;
import android.sax.Element;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;
import android.util.Xml;

public class NominatimSearchParser extends BaseParser {
	ArrayList<HashMap<String, Double>> points;
	private final String TAG="NominatimSearchParser";
	public NominatimSearchParser(Context ctx) {
		super(ctx);
		points = new ArrayList<HashMap<String, Double>>();
	}

	@Override
	public Object parseXml(String xmlString) throws Exception {
		Log.d(TAG, "xmlString: "+xmlString);
		RootElement rootElement = new RootElement("searchresults");
		Element placeElement = rootElement.getChild("place");
		placeElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				HashMap<String, Double> map = new HashMap<String, Double>();
				map.put("lat", Double.parseDouble(attributes.getValue("lat")));
				map.put("lon", Double.parseDouble(attributes.getValue("lon")));
				points.add(map);
			}
		});

		Xml.parse(xmlString, rootElement.getContentHandler());
		Log.d(TAG, points.toString());
		return points;
	}

}
