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

public class BikeAdsParser extends BaseParser {
	private BikeDAO bikeAdsDAO;
	private BikesResult bikesResult;
	private Photo photo;
	private String rootElementString;
	private int total = 0;

	public static final String FEATURED_BIKES = "FeaturedBikes",
			SEARCH_BIKES = "SearchBikes";

	public BikeAdsParser(Context context, String rootElementString) {
		super(context);
		this.rootElementString = rootElementString;
	}

	@Override
	public Object parseXml(String xmlString) throws Exception {
		// TODO Auto-generated method stub
		RootElement rootElement = new RootElement(rootElementString);
		Element resultsElement = rootElement.getChild(Tags.RESULTS);
		Element bikeElement = resultsElement.getChild(Tags.BIKE);
		Element bikeNameElement = bikeElement.getChild(Tags.BIKENAME);
		Element makeIDElement = bikeElement.getChild(Tags.MAKEID);
		Element modelIDElement = bikeElement.getChild(Tags.MODELID);
		Element engineIDElement = bikeElement.getChild(Tags.ENGINEID);
		Element engineSizeElement = bikeElement.getChild(Tags.ENGINESIZE);
		Element sellerIDElement = bikeElement.getChild(Tags.SELLERID);
		Element yearElement = bikeElement.getChild(Tags.YEAR);
		Element mileageElement = bikeElement.getChild(Tags.MILEAGE);
		Element regNoElement = bikeElement.getChild(Tags.REGNO);
		Element featuresElement = bikeElement.getChild(Tags.FEATURES);
		Element descriptionElement = bikeElement.getChild(Tags.DESCRIPTION);
		Element priceElement = bikeElement.getChild(Tags.PRICE);
		Element latElement = bikeElement.getChild(Tags.LAT);
		Element lonElement = bikeElement.getChild(Tags.LON);
		Element contactElement = bikeElement.getChild(Tags.CONTACT);
		Element firstNameElement = contactElement.getChild(Tags.FIRSTNAME);
		Element lastNameElement = contactElement.getChild(Tags.LASTNAME);
		Element emailElement = contactElement.getChild(Tags.EMAIL);
		Element telephoneElement = contactElement.getChild(Tags.TELEPHONE);
		Element urlElement = contactElement.getChild(Tags.URL);
		Element photosElement = bikeElement.getChild(Tags.PHOTOS);
		Element photoElement = photosElement.getChild(Tags.PHOTO);
		Element photoUrlElement = photoElement.getChild(Tags.PHOTOURL);
		Element photoCaptionElement = photoElement.getChild(Tags.PHOTOCAPTION);
		Element thumbsElement = bikeElement.getChild(Tags.THUMBS);
		Element thumbElement = thumbsElement.getChild(Tags.THUMB);
		resultsElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				bikesResult = new BikesResult(attributes.getValue(Tags.TOTAL));
			}
		});

		bikeElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				bikeAdsDAO = new BikeDAO(attributes.getValue(Tags.ID),
						attributes.getValue(Tags.GUMTREE), attributes
								.getValue(Tags.PREMIUM));
			}
		});

		bikeNameElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.bikeName = body;

			}
		});

		makeIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.makeID = Integer.parseInt(body);

			}
		});

		modelIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.modelID = Integer.parseInt(body);

			}
		});
		engineSizeElement
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						bikeAdsDAO.engineSize = Integer.parseInt(body);

					}
				});
		engineIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.engineID = Integer.parseInt(body);

			}
		});
		sellerIDElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.sellerId = Integer.parseInt(body);

			}
		});
		yearElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.year = Integer.parseInt(body);

			}
		});
		mileageElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.mileage = Integer.parseInt(body);

			}
		});
		regNoElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.regNo = body;

			}
		});
		featuresElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.features = body;

			}
		});
		descriptionElement
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						bikeAdsDAO.description = body;

					}
				});
		priceElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.price = Integer.parseInt(body);

			}
		});
		latElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.lat = Float.parseFloat(body);

			}
		});
		lonElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.lon = Float.parseFloat(body);

			}
		});
		firstNameElement
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						bikeAdsDAO.firstName = body;

					}
				});
		lastNameElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.lastName = body;

			}
		});
		emailElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.email = body;

			}
		});
		telephoneElement
				.setEndTextElementListener(new EndTextElementListener() {

					@Override
					public void end(String body) {
						bikeAdsDAO.telephone = body;

					}
				});
		urlElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.url = body;

			}
		});

		photosElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				bikeAdsDAO.photos = new ArrayList<Photo>();

			}
		});
		photoElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				photo = new Photo();
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
				bikeAdsDAO.photos.add(photo);
			}
		});
		thumbsElement.setStartElementListener(new StartElementListener() {

			@Override
			public void start(Attributes attributes) {
				bikeAdsDAO.thumbs = new ArrayList<String>();
			}
		});
		thumbElement.setEndTextElementListener(new EndTextElementListener() {

			@Override
			public void end(String body) {
				bikeAdsDAO.thumbs.add(body);
			}
		});

		bikeElement.setEndElementListener(new EndElementListener() {

			@Override
			public void end() {
				bikesResult.bikes.add(bikeAdsDAO);
			}
		});

		Xml.parse(xmlString, rootElement.getContentHandler());

		return bikesResult;

	}

	public class BikesResult {
		public int results;
		public ArrayList<BikeDAO> bikes;

		public BikesResult(String results) {
			super();
			this.results = Integer.parseInt(results);
			this.bikes = new ArrayList<BikeDAO>();
		}

	}

	private interface Tags {
		String FEATUREDBIKES = "FeaturedBikes";
		String RESULTS = "Results";
		String TOTAL = "total";
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
