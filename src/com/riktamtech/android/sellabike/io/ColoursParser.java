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

import com.riktamtech.android.sellabike.dao.ColourDAO;

public class ColoursParser extends BaseParser {

	public ColoursParser(Context ctx) {
		super(ctx);
		
	}


	ColourDAO colourDAO;

	@Override
	public Object parseXml(String xmlString) {
		final ArrayList<ColourDAO> colours = new ArrayList<ColourDAO>();
		RootElement rootElement = new RootElement(Tags.COLOURS);
		Element modelElement = rootElement.getChild(Tags.COLOUR);
		modelElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				colourDAO = new ColourDAO(attributes.getValue(Tags.ID), "");
			}
		});
		modelElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				if (!TextUtils.isEmpty(body)) {
				colourDAO.name = body;
				colours.add(colourDAO);
				}
			}
		});

		try {
			Xml.parse(xmlString, rootElement.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return colours;
	}

	
	private interface Tags {
		String COLOURS = "Colours";
		String COLOUR = "Colour";
		String ID = "id";
	}
}
