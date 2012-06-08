package com.riktamtech.android.sellabike.io;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.riktamtech.android.sellabike.dao.BikeDAO;
import com.riktamtech.android.sellabike.dao.Photo;

import android.content.Context;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

import com.riktamtech.android.sellabike.dao.MyAdsDAO;

public class MyAdsParser extends BaseParser {
	public MyAdsParser(Context ctx) {
		super(ctx);
		
	}

	BikeDAO myAdsDAO;
	ArrayList<BikeDAO> myAds = new ArrayList<BikeDAO>();
	ArrayList<Photo> photos;
	ArrayList<String> thumbs;
	Photo photo;

	@Override
	public Object parseXml(String xmlString) {
		RootElement rootElement = new RootElement(MyAdsTags.BIKES);
		Element bikeElement = rootElement.getChild(MyAdsTags.BIKE);
		Element bikeNameElement = bikeElement.getChild(MyAdsTags.BIKENAME);
		Element makeIDElement = bikeElement.getChild(MyAdsTags.MAKEID);
		Element modelIDElement = bikeElement.getChild(MyAdsTags.MODELID);
		Element engineIDElement = bikeElement.getChild(MyAdsTags.ENGINEID);
		Element engineSizeElement = bikeElement.getChild(MyAdsTags.ENGINESIZE);
		Element sellerIDElement = bikeElement.getChild(MyAdsTags.SELLERID);
		Element yearElement = bikeElement.getChild(MyAdsTags.YEAR);
		Element mileageElement = bikeElement.getChild(MyAdsTags.MILEAGE);
		Element regNoElement = bikeElement.getChild(MyAdsTags.REGNO);
		Element featuresElement = bikeElement.getChild(MyAdsTags.FEATURES);
		Element descriptionElement = bikeElement
				.getChild(MyAdsTags.DESCRIPTION);
		Element priceElement = bikeElement.getChild(MyAdsTags.PRICE);
		Element latElement = bikeElement.getChild(MyAdsTags.LAT);
		Element lonElement = bikeElement.getChild(MyAdsTags.LON);
		Element contactElement = bikeElement.getChild(MyAdsTags.CONTACT);

		Element firstNameElement = contactElement.getChild(MyAdsTags.FIRSTNAME);
		Element lastNameElement = contactElement.getChild(MyAdsTags.LASTNAME);
		Element emailElement = contactElement.getChild(MyAdsTags.EMAIL);
		Element telephoneElement = contactElement.getChild(MyAdsTags.TELEPHONE);
		Element urlElement = contactElement.getChild(MyAdsTags.URL);

		Element photosElement = bikeElement.getChild(MyAdsTags.PHOTOS);

		Element photoElement = photosElement.getChild(MyAdsTags.PHOTO);

		Element photoUrlElement = photoElement.getChild(MyAdsTags.PHOTOURL);
		Element photoCaptionElement = photoElement
				.getChild(MyAdsTags.PHOTOCAPTION);

		Element thumbsElement = bikeElement.getChild(MyAdsTags.THUMBS);

		Element thumbElement = thumbsElement.getChild(MyAdsTags.THUMB);

		Element colorIDElement = bikeElement.getChild(MyAdsTags.COLORID);

		bikeElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				myAdsDAO = new BikeDAO(attributes.getValue(MyAdsTags.ID),
						attributes.getValue(MyAdsTags.GUMTREE), attributes
								.getValue(MyAdsTags.PREMIUM));
			}
		});

		bikeNameElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.bikeName = body;

			}
		});

		makeIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.makeID = Integer.parseInt(body);

			}
		});

		modelIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.modelID = Integer.parseInt(body);

			}
		});
		engineSizeElement
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						myAdsDAO.engineSize = Integer.parseInt(body);

					}
				});
		engineIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.engineID = Integer.parseInt(body);

			}
		});
		sellerIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.sellerId = Integer.parseInt(body);

			}
		});
		yearElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.year = Integer.parseInt(body);

			}
		});
		mileageElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.mileage = Integer.parseInt(body);

			}
		});
		regNoElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.regNo = body;

			}
		});
		featuresElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.features = body;

			}
		});
		descriptionElement
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						myAdsDAO.description = body;

					}
				});
		priceElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.price = Integer.parseInt(body);

			}
		});
		latElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.lat = Float.parseFloat(body);

			}
		});
		lonElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.lon = Float.parseFloat(body);

			}
		});
		firstNameElement
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						myAdsDAO.firstName = body;

					}
				});
		lastNameElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.lastName = body;

			}
		});
		emailElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.email = body;

			}
		});
		telephoneElement
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						myAdsDAO.telephone = body;

					}
				});
		urlElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.url = body;

			}
		});

		photosElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				myAdsDAO.photos = new ArrayList<Photo>();

			}
		});
		photoElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				photo = (Photo) new Photo();
			}
		});
		photoUrlElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				photo.url = body;

			}
		});
		photoCaptionElement
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						photo.caption = body;
					}
				});

		photoElement.setEndElementListener(new EndElementListener() {

			@Override
			public void end() {
				myAdsDAO.photos.add(photo);

			}
		});
		thumbsElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				myAdsDAO.thumbs = new ArrayList<String>();
			}
		});
		thumbElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.thumbs.add(body);
			}
		});
		colorIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				myAdsDAO.colorId = Integer.parseInt(body);

			}
		});
		bikeElement.setEndElementListener(new EndElementListener() {

			@Override
			public void end() {
				myAds.add(myAdsDAO);

			}
		});
		try {
			Xml.parse(xmlString, rootElement.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return myAds;
	}

	private interface MyAdsTags {
		String BIKES = "Bikes";
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
		String COLORID = "colorID";
	}
}
