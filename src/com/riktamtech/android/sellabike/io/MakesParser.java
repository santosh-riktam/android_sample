package com.riktamtech.android.sellabike.io;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.text.TextUtils;
import android.util.Xml;

import com.riktamtech.android.sellabike.dao.MakeDAO;

public class MakesParser extends BaseParser {

	public MakesParser(Context ctx) {
		super(ctx);
		
	}

	MakeDAO makeDAO;

	@Override
	public Object parseXml(String xmlString) {
		final ArrayList<MakeDAO> makes = new ArrayList<MakeDAO>();
		RootElement rootElement = new RootElement(Tags.MAKES);
		Element modelElement = rootElement.getChild(Tags.MAKE);
		modelElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				makeDAO = new MakeDAO(attributes.getValue(Tags.ID), "");
			}
		});
		modelElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				if (!TextUtils.isEmpty(body)) {
					makeDAO.name = body;
					makes.add(makeDAO);
				}
			}
		});

		try {
			Xml.parse(xmlString, rootElement.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return makes;
	}

	private interface Tags {
		String MAKES = "Makes";
		String MAKE = "Make";
		String ID = "id";
	}
}
