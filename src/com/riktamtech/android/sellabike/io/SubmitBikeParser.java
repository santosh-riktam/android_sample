package com.riktamtech.android.sellabike.io;

import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class SubmitBikeParser extends BaseParser {

	public SubmitBikeParser(Context ctx) {
		super(ctx);
	}

	String messageHeading = "";

	@Override
	public Object parseXml(String xmlString) throws Exception {
		RootElement rootElement = new RootElement(Tags.submitBike);
		Element messageHeadingElement = rootElement
				.getChild(Tags.MessageHeading);
		Element messageBodyElement = rootElement.getChild(Tags.MessageBody);

		messageHeadingElement
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						messageHeading = body;
					}
				});
		Xml.parse(xmlString, rootElement.getContentHandler());
		return messageHeading;
	}

	private interface Tags {
		String submitBike = "SubmitBike", MessageHeading = "MessageHeading",
				MessageBody = "MessageBody";
	}
}
