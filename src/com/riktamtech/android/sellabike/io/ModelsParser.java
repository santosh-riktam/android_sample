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

import com.riktamtech.android.sellabike.dao.ModelDAO;

public class ModelsParser extends BaseParser {

	public ModelsParser(Context ctx) {
		super(ctx);

	}

	ModelDAO modelDAO;

	@Override
	public Object parseXml(String xmlString) {
		final ArrayList<ModelDAO> models = new ArrayList<ModelDAO>();
		RootElement rootElement = new RootElement(Tags.MODELS);
		Element modelElement = rootElement.getChild(Tags.MODEL);
		modelElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				modelDAO = new ModelDAO(attributes.getValue(Tags.ID), attributes.getValue(Tags.MAKE_ID), attributes.getValue(Tags.ENGINE_ID), "");
			}
		});
		modelElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				if (!TextUtils.isEmpty(body)) {
					modelDAO.name = body;
					models.add(modelDAO);
				}
			}
		});

		try {
			Xml.parse(xmlString, rootElement.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return models;
	}

	private interface Tags {
		String MODELS = "Models";
		String MODEL = "Model";
		String ID = "id";
		String MAKE_ID = "makeid";
		String ENGINE_ID = "engineid";
	}
}
