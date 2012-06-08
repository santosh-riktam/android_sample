package com.riktamtech.android.sellabike.io;

import org.xml.sax.SAXException;

import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class NominatimParser extends BaseParser {
	public NominatimParser(Context ctx) {
		super(ctx);
		
	}

	String postcode;

	@Override
	public Object parseXml(String xmlString) throws Exception {
		RootElement rootElement = new RootElement(Tags.reversegeocode);
		Element addrElement = rootElement.getChild(Tags.addressparts);
		Element postcodeElement = addrElement.getChild(Tags.postcode);
		postcodeElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				postcode = body;
			}
		});
		try {
			Xml.parse(xmlString, rootElement.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return postcode;

	}

	private interface Tags {
		String reversegeocode = "reversegeocode";
		String addressparts = "addressparts";
		String postcode = "postcode";
	}
}
