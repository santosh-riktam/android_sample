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

import com.riktamtech.android.sellabike.dao.EngineDAO;

public class EnginesParser extends BaseParser {

	public EnginesParser(Context ctx) {
		super(ctx);
		
	}

	EngineDAO engineDAO;

	@Override
	public Object parseXml(String xmlString) {
		final ArrayList<EngineDAO> engines = new ArrayList<EngineDAO>();
		RootElement rootElement = new RootElement(Tags.ENGINES);
		Element modelElement = rootElement.getChild(Tags.ENGINE);
		modelElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				engineDAO = new EngineDAO(attributes.getValue(Tags.ID), "");
			}
		});
		modelElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				if (!TextUtils.isEmpty(body)) {
					engineDAO.name = body;
					engines.add(engineDAO);
				}
			}
		});

		try {
			Xml.parse(xmlString, rootElement.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return engines;
	}

	private interface Tags {
		String ENGINES = "Engines";
		String ENGINE = "Engine";
		String ID = "id";
	}
}
