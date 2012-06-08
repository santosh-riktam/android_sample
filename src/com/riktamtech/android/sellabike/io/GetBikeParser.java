package com.riktamtech.android.sellabike.io;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.dao.Photo;

public class GetBikeParser extends BaseParser {
	public GetBikeParser(Context ctx) {
		super(ctx);

	}

	BikeDAO getBikeDAO;
	ArrayList<Photo> photos;
	ArrayList<String> thumbs;
	Photo photo;

	@Override
	public Object parseXml(String xmlString) {
		RootElement bikeElement = new RootElement(GetBikeTags.BIKE);
		Element bikeNameElement = bikeElement.getChild(GetBikeTags.BIKENAME);
		Element makeIDElement = bikeElement.getChild(GetBikeTags.MAKEID);
		Element modelIDElement = bikeElement.getChild(GetBikeTags.MODELID);
		Element engineIDElement = bikeElement.getChild(GetBikeTags.ENGINEID);
		Element engineSizeElement = bikeElement.getChild(GetBikeTags.ENGINESIZE);
		Element sellerIDElement = bikeElement.getChild(GetBikeTags.SELLERID);
		Element yearElement = bikeElement.getChild(GetBikeTags.YEAR);
		Element mileageElement = bikeElement.getChild(GetBikeTags.MILEAGE);
		Element regNoElement = bikeElement.getChild(GetBikeTags.REGNO);
		Element featuresElement = bikeElement.getChild(GetBikeTags.FEATURES);
		Element descriptionElement = bikeElement.getChild(GetBikeTags.DESCRIPTION);
		Element priceElement = bikeElement.getChild(GetBikeTags.PRICE);
		Element latElement = bikeElement.getChild(GetBikeTags.LAT);
		Element lonElement = bikeElement.getChild(GetBikeTags.LON);
		Element contactElement = bikeElement.getChild(GetBikeTags.CONTACT);
		Element firstNameElement = contactElement.getChild(GetBikeTags.FIRSTNAME);
		Element lastNameElement = contactElement.getChild(GetBikeTags.LASTNAME);
		Element emailElement = contactElement.getChild(GetBikeTags.EMAIL);
		Element telephoneElement = contactElement.getChild(GetBikeTags.TELEPHONE);
		Element urlElement = contactElement.getChild(GetBikeTags.URL);
		Element photoElement = bikeElement.getChild(GetBikeTags.PHOTO);
		//Element photoUrlElement = photoElement.getChild(GetBikeTags.PHOTOURL);
		//Element photoCaptionElement = photoElement.getChild(GetBikeTags.PHOTOCAPTION);
		Element thumbElement = bikeElement.getChild(GetBikeTags.THUMB);
		bikeElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				getBikeDAO = new BikeDAO(attributes.getValue(GetBikeTags.ID), attributes.getValue(GetBikeTags.GUMTREE), attributes.getValue(GetBikeTags.PREMIUM));
			}
		});

		bikeNameElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.bikeName = body;

			}
		});

		makeIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.makeID = Integer.parseInt(body);

			}
		});

		modelIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.modelID = Integer.parseInt(body);

			}
		});
		engineSizeElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.engineSize = Integer.parseInt(body);

			}
		});
		engineIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.engineID = Integer.parseInt(body);

			}
		});
		sellerIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.sellerId = Integer.parseInt(body);

			}
		});
		yearElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.year = Integer.parseInt(body);

			}
		});
		mileageElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.mileage = Integer.parseInt(body);

			}
		});
		regNoElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.regNo = body;

			}
		});
		featuresElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.features = body;

			}
		});
		descriptionElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.description = body;

			}
		});
		priceElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.price = Integer.parseInt(body);

			}
		});
		latElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.lat = Float.parseFloat(body);

			}
		});
		lonElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.lon = Float.parseFloat(body);

			}
		});
		firstNameElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.firstName = body;

			}
		});
		lastNameElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.lastName = body;

			}
		});
		emailElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.email = body;

			}
		});
		telephoneElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.telephone = body;

			}
		});
		urlElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.url = body;

			}
		});

		//		photosElement.setStartElementListener(new StartElementListener() {
		//
		//			@Override
		//			public void start(Attributes attributes) {
		//				getBikeDAO.photos = new ArrayList<Photo>();
		//
		//			}
		//		});
		//		photoElement.setStartElementListener(new StartElementListener() {
		//
		//			@Override
		//			public void start(Attributes attributes) {
		//				photo = new Photo();
		//			}
		//		});
		//		photoUrlElement.setEndTextElementListener(new EndTextElementListener() {
		//
		//			@Override
		//			public void end(String body) {
		//				photo.url = "http://www.sellabike.me/api/images/" + body
		//						+ ".jpg";
		//
		//			}
		//		});
		//		photoCaptionElement
		//				.setEndTextElementListener(new EndTextElementListener() {
		//
		//					@Override
		//					public void end(String body) {
		//						photo.caption = body + "";
		//					}
		//				});
		//
		//		photoElement.setEndElementListener(new EndElementListener() {
		//
		//			@Override
		//			public void end() {
		//				getBikeDAO.photos.add(photo);
		//
		//			}
		//		});

		photoElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.photos = new ArrayList<Photo>();
				photo = new Photo();
				photo.url = "http://www.sellabike.me/api/images/" + body + ".jpg";
				photo.caption="";
				getBikeDAO.photos.add(photo);
			}
		});
		//		thumbsElement.setStartElementListener(new StartElementListener() {
		//
		//			@Override
		//			public void start(Attributes attributes) {
		//				getBikeDAO.thumbs = new ArrayList<String>();
		//			}
		//		});
		thumbElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				getBikeDAO.thumbs = new ArrayList<String>();
				getBikeDAO.thumbs.add(" http://www.sellabike.me/api/images/" + body + ".jpg");
			}
		});
		bikeElement.setEndElementListener(new EndElementListener() {

			@Override
			public void end() {

			}
		});
		try {
			Xml.parse(xmlString, bikeElement.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return getBikeDAO;
	}

	private interface GetBikeTags {
		String BIKE = "Bike";
		String ID = "id";
		String GUMTREE = "gumtree";
		String PREMIUM = "premium";
		String BIKENAME = "BikeName";
		String MAKEID = "MakeID";
		String MODELID = "ModelID";
		String ENGINESIZE = "EngineSize";
		String ENGINEID = "EngineID";
		String SELLERID = "SellerID";
		String YEAR = "Year";
		String MILEAGE = "Mileage";
		String REGNO = "RegNo";
		String FEATURES = "Features";
		String DESCRIPTION = "Description";
		String PRICE = "Price";
		String LAT = "Lat";
		String LON = "Lon";
		String CONTACT = "Contact";
		String FIRSTNAME = "FirstName";
		String LASTNAME = "LastName";
		String EMAIL = "Email";
		String TELEPHONE = "Telephone";
		String URL = "Url";
		String PHOTOS = "Photos";
		String PHOTO = "Photo";
		String PHOTOURL = "Url";
		String PHOTOCAPTION = "Caption";
		String THUMBS = "Thumbs";
		String THUMB = "Thumb";
	}

}
