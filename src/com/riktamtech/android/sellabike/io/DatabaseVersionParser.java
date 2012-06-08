package com.riktamtech.android.sellabike.io;

import android.content.Context;
import android.os.Bundle;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class DatabaseVersionParser extends BaseParser {
	public DatabaseVersionParser(Context ctx) {
		super(ctx);
		
	}

	Bundle bundle = new Bundle();

	@Override
	public Object parseXml(String xmlString) {
		RootElement rootElement = new RootElement(
				DatabaseVersionTags.DATABASEVERSION);
		Element makeElement = rootElement
				.getChild(DatabaseVersionTags.MAKEVERSION);
		Element modelElement = rootElement
				.getChild(DatabaseVersionTags.MODELVERSION);
		Element engineElement = rootElement
				.getChild(DatabaseVersionTags.ENGINEVERSION);
		Element colourElement = rootElement
				.getChild(DatabaseVersionTags.COLOURVERSION);

		makeElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				bundle.putInt(DatabaseVersionTags.MAKEVERSION,
						Integer.parseInt(body));
			}
		});
		modelElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				bundle.putInt(DatabaseVersionTags.MODELVERSION,
						Integer.parseInt(body));
			}
		});
		engineElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bundle.putInt(DatabaseVersionTags.ENGINEVERSION,
						Integer.parseInt(body));
			}
		});
		colourElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bundle.putInt(DatabaseVersionTags.COLOURVERSION,
						Integer.parseInt(body));
			}
		});
		try {
			Xml.parse(xmlString, rootElement.getContentHandler());
		} catch (Exception e) {
		}
		return bundle;
	}

	public interface DatabaseVersionTags {
		String DATABASEVERSION = "DatabaseVersion";
		String MAKEVERSION = "MakeVersion";
		String MODELVERSION = "ModelVersion";
		String ENGINEVERSION = "EngineVersion";
		String COLOURVERSION = "ColourVersion";

	}
}
