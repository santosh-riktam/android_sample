package com.riktamtech.android.sellabike.io;

import java.util.ArrayList;

import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class InfoParser extends BaseParser {

	public InfoParser(Context ctx) {
		super(ctx);
		
	}

	ArrayList<String> resultArrayList;

	@Override
	public ArrayList<String> parseXml(String xmlString) throws Exception {
		RootElement infoElement = new RootElement(Tags.Info);
		Element statusElement = infoElement.getChild(Tags.Status);
		Element messageHeadingElement = infoElement.getChild(Tags.MessageHeading);
		Element messageBodyElement = infoElement.getChild(Tags.MessageBody);
		resultArrayList = new ArrayList<String>();

		statusElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				resultArrayList.add(body);
			}
		});
		messageHeadingElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				resultArrayList.add(body);
			}
		});

		messageBodyElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				resultArrayList.add(body);
			}
		});

		Xml.parse(xmlString, infoElement.getContentHandler());

		return resultArrayList;

	}

	private interface Tags {
		String Info = "Info", Status = "Status", MessageHeading = "MessageHeading", MessageBody = "MessageBody";
	}

}
