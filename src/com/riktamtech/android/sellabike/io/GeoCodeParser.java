package com.riktamtech.android.sellabike.io;

import java.util.ArrayList;

import org.xml.sax.SAXException;

import android.content.Context;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

import com.google.android.maps.GeoPoint;

public class GeoCodeParser extends BaseParser {
	int lat, lng;

	ArrayList<GeoPoint> geoPoints;

	public GeoCodeParser(Context ctx) {
		super(ctx);
	}

	@Override
	public Object parseXml(String xmlString) throws Exception {
		RootElement rootElement = new RootElement(GeoCodeTags.GEOCODERESPONSE);
		Element resultElement = rootElement.getChild(GeoCodeTags.RESULT);
		Element geometryElement = resultElement.getChild(GeoCodeTags.GEOMETRY);
		Element locationElement = geometryElement.getChild(GeoCodeTags.LOCATION);
		Element latitudeElement = locationElement.getChild(GeoCodeTags.LAT);
		Element longitudeElement = locationElement.getChild(GeoCodeTags.LON);

		geoPoints = new ArrayList<GeoPoint>();

		latitudeElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				lat = (int) (Double.parseDouble(body) * 1e6);
			}
		});
		longitudeElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				lng = (int) (Double.parseDouble(body) * 1e6);

			}
		});
		locationElement.setEndElementListener(new EndElementListener() {

			@Override
			public void end() {
				geoPoints.add(new GeoPoint(lat, lng));
			}
		});
		try {
			Xml.parse(xmlString, rootElement.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return geoPoints;
	}

	interface GeoCodeTags {
		String GEOCODERESPONSE = "GeocodeResponse";
		String RESULT = "result";
		String GEOMETRY = "geometry";
		String LOCATION = "location";
		String LAT = "lat";
		String LON = "lng";
	}

}
