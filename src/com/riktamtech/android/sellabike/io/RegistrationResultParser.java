package com.riktamtech.android.sellabike.io;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

import com.riktamtech.android.sellabike.dao.RegistrationResultDAO;

public class RegistrationResultParser extends BaseParser {

	public RegistrationResultParser(Context ctx) {
		super(ctx);
		
	}

	RegistrationResultDAO regResultDAO;
	//ArrayList<RegistrationResultDAO> regResult = new ArrayList<RegistrationResultDAO>();

	@Override
	public Object parseXml(String xmlString) {
		RootElement rootElement = new RootElement(RegResultTags.DATASET);
		Element dvlaElement = rootElement.getChild(RegResultTags.DVLA);
		Element vehicleElement = dvlaElement.getChild(RegResultTags.VEHICLE);
		Element makeElement = vehicleElement.getChild(RegResultTags.MAKE);
		Element modelElement = vehicleElement.getChild(RegResultTags.MODEL);
		Element colourElement = vehicleElement.getChild(RegResultTags.COLOUR);
		Element regDateElement = vehicleElement.getChild(RegResultTags.REGDATE);
		Element engineCCElement = vehicleElement
				.getChild(RegResultTags.ENGINECC);
		
		vehicleElement.setStartElementListener(new StartElementListener() {
			
			@Override
			public void start(Attributes attributes) {
				regResultDAO=new RegistrationResultDAO();
			}
		});
		makeElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				regResultDAO.makeName = body;
			}
		});
		modelElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				regResultDAO.modelName = body;
			}
		});
		colourElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				regResultDAO.colour = body;
			}
		});
		regDateElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				regResultDAO.regDate = body;
			}
		});
		engineCCElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				regResultDAO.engineSize = Integer.parseInt(body);
			}
		});
		/*vehicleElement.setEndElementListener(new EndElementListener() {

			@Override
			public void end() {
				regResult.add(regResultDAO);
			}
		});*/

		try {
			Xml.parse(xmlString, rootElement.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return regResultDAO;
	}

	interface RegResultTags {
		String DATASET = "DATASET";
		String DVLA = "DVLA";
		String VEHICLE = "VEHICLE";
		String MAKE = "MAKE";
		String MODEL = "MODEL";
		String REGDATE = "REG_DATE";
		String COLOUR = "COLOUR";
		String ENGINECC = "CC";
	}

}
