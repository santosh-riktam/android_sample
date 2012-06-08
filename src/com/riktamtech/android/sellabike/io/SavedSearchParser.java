package com.riktamtech.android.sellabike.io;

import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class SavedSearchParser extends BaseParser {
	public SavedSearchParser(Context ctx) {
		super(ctx);
		
	}

	String savedSearchId = "";

	@Override
	public Object parseXml(String xmlString) throws Exception {
		RootElement rootElement = new RootElement(Tags.savedSearch);
		Element savedSearchElement = rootElement.getChild(Tags.savedSearchID);

		savedSearchElement
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						savedSearchId = body;
					}
				});

		Xml.parse(xmlString, rootElement.getContentHandler());

		return savedSearchId;
	}

	private interface Tags {
		String savedSearch = "savedSearch", MessageHeading = "MessageHeading",
				MessageBody = "MessageBody", savedSearchID = "savedSearchID";
	}
}
