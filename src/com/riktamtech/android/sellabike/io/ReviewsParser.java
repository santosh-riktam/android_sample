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

import com.riktamtech.android.sellabike.dao.ReviewsDAO;

public class ReviewsParser extends BaseParser {
	public ReviewsParser(Context ctx) {
		super(ctx);

	}

	ReviewsDAO reviewsDAO;

	@Override
	public Object parseXml(String xmlString) {
		final ArrayList<ReviewsDAO> reviews = new ArrayList<ReviewsDAO>();
		RootElement rootElement = new RootElement(ReviewsTags.REVIEWS);
		Element storyElement = rootElement.getChild(ReviewsTags.STORY);
		Element thumbElement = storyElement.getChild(ReviewsTags.THUMB);
		Element titleElement = storyElement.getChild(ReviewsTags.TITLE);
		Element summaryElement = storyElement.getChild(ReviewsTags.SUMMARY);
		Element shareSummaryElement = storyElement.getChild(ReviewsTags.SHARE_SUMMARY);
		Element fullTextElement = storyElement.getChild(ReviewsTags.FULL_TEXT);
		storyElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				reviewsDAO = new ReviewsDAO(attributes.getValue(ReviewsTags.ID));
			}
		});
		thumbElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				reviewsDAO.thumb = body;

			}
		});
		titleElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				reviewsDAO.title = body;

			}
		});
		summaryElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				reviewsDAO.summary = body;

			}
		});
		shareSummaryElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				reviewsDAO.shareSummary = body;

			}
		});
		fullTextElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				reviewsDAO.fullText = body;

			}
		});
		storyElement.setEndElementListener(new EndElementListener() {

			@Override
			public void end() {
				// TODO Auto-generated method stub
				reviews.add(reviewsDAO);
			}
		});

		try {
			Xml.parse(xmlString, rootElement.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return reviews;
	}

	private interface ReviewsTags {
		String REVIEWS = "Reviews";
		String STORY = "Story";
		String THUMB = "Thumb";
		String TITLE = "Title";
		String SUMMARY = "Summary";
		String SHARE_SUMMARY = "ShareSummary";
		String FULL_TEXT = "FullText";
		String ID = "id";
	}
}
