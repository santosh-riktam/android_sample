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

import com.riktamtech.android.sellabike.dao.NewsDAO;

public class NewsParser extends BaseParser {
	public NewsParser(Context ctx) {
		super(ctx);
		
	}

	NewsDAO newsDAO;
	ArrayList<NewsDAO> news = new ArrayList<NewsDAO>();

	@Override
	public Object parseXml(String xmlString) {
		RootElement rootElement = new RootElement(NewsTags.NEWS);
		Element storyElement = rootElement.getChild(NewsTags.STORY);
		Element thumbElement = storyElement.getChild(NewsTags.THUMB);
		Element titleElement = storyElement.getChild(NewsTags.TITLE);
		Element summaryElement = storyElement.getChild(NewsTags.SUMMARY);
		Element shareSummaryElement = storyElement
				.getChild(NewsTags.SHARE_SUMMARY);
		Element fullTextElement = storyElement.getChild(NewsTags.FULL_TEXT);
		storyElement.setStartElementListener(new StartElementListener() {
			@Override
			public void start(Attributes attributes) {
				newsDAO = new NewsDAO(attributes.getValue(NewsTags.ID));
			}
		});
		thumbElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				newsDAO.thumb = body;

			}
		});
		titleElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				newsDAO.title = body;

			}
		});
		summaryElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				newsDAO.summary = body;

			}
		});
		shareSummaryElement
				.setEndTextElementListener(new EndTextElementListener() {
					@Override
					public void end(String body) {
						newsDAO.shareSummary = body;

					}
				});
		fullTextElement.setEndTextElementListener(new EndTextElementListener() {
			@Override
			public void end(String body) {
				newsDAO.fullText = body;

			}
		});
		storyElement.setEndElementListener(new EndElementListener() {

			@Override
			public void end() {
				// TODO Auto-generated method stub
				news.add(newsDAO);
			}
		});

		try {
			Xml.parse(xmlString, rootElement.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return news;
	}

	interface NewsTags {
		String NEWS = "News";
		String STORY = "Story";
		String THUMB = "Thumb";
		String TITLE = "Title";
		String SUMMARY = "Summary";
		String SHARE_SUMMARY = "ShareSummary";
		String FULL_TEXT = "FullText";
		String ID = "id";
	}
}
