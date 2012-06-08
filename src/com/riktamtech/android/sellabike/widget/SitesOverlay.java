package com.riktamtech.android.sellabike.widget;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class SitesOverlay extends ItemizedOverlay<OverlayItem> {
	private List<OverlayItem> items = new ArrayList<OverlayItem>();
	private Drawable marker = null;
	private OverlayItem inDrag = null;
	private ImageView dragImage = null;
	private int xDragImageOffset = 0;
	private int yDragImageOffset = 0;
	private int xDragTouchOffset = 0;
	private int yDragTouchOffset = 0;

	private MapView map;

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		return super.draw(canvas, mapView, false, when);
	}

	private GeoPoint getPoint(double lat, double lon) {

		return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));
	}

	public SitesOverlay(Drawable marker, ImageView dragImage, MapView map) {
		super(marker);
		this.marker = marker;

		this.dragImage = dragImage;
		this.map = map;
		xDragImageOffset = dragImage.getDrawable().getIntrinsicWidth() / 2;
		yDragImageOffset = dragImage.getDrawable().getIntrinsicHeight();

		items.add(new OverlayItem(getPoint(40.748963847316034, -73.96807193756104), "UN", "United Nations"));
		items.add(new OverlayItem(getPoint(40.76866299974387, -73.98268461227417), "Lincoln Center", "Home of Jazz at Lincoln Center"));
		items.add(new OverlayItem(getPoint(40.765136435316755, -73.97989511489868), "Carnegie Hall", "Where you go with practice, practice, practice"));
		items.add(new OverlayItem(getPoint(40.70686417491799, -74.01572942733765), "The Downtown Club", "Original home of the Heisman Trophy"));

		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		return (items.get(i));
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(marker);
	}

	@Override
	public int size() {
		return (items.size());
	}

	public void clearAndAddOverlay(OverlayItem overlay) {
		items.clear();
		items.add(overlay);
		populate();
	}

	public GeoPoint getLocationofTheOverlay() {
		return items.get(0).getPoint();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		final int action = event.getAction();
		final int x = (int) event.getX();
		final int y = (int) event.getY();
		boolean result = false;

		if (action == MotionEvent.ACTION_DOWN) {
			for (OverlayItem item : items) {
				Point p = new Point(0, 0);

				map.getProjection().toPixels(item.getPoint(), p);

				if (hitTest(item, marker, x - p.x, y - p.y)) {
					result = true;
					inDrag = item;
					items.remove(inDrag);
					populate();

					xDragTouchOffset = 0;
					yDragTouchOffset = 0;

					setDragImagePosition(p.x, p.y);
					dragImage.setVisibility(View.VISIBLE);

					xDragTouchOffset = x - p.x;
					yDragTouchOffset = y - p.y;

					break;
				}
			}
		} else if (action == MotionEvent.ACTION_MOVE && inDrag != null) {
			setDragImagePosition(x, y);
			result = true;
		} else if (action == MotionEvent.ACTION_UP && inDrag != null) {
			dragImage.setVisibility(View.GONE);

			GeoPoint pt = map.getProjection().fromPixels(x - xDragTouchOffset, y - yDragTouchOffset);
			OverlayItem toDrop = new OverlayItem(pt, inDrag.getTitle(), inDrag.getSnippet());

			items.add(toDrop);
			populate();

			inDrag = null;
			result = true;
		}

		return (result || super.onTouchEvent(event, mapView));
	}

	private void setDragImagePosition(int x, int y) {
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) dragImage.getLayoutParams();
		lp.setMargins(x - xDragImageOffset - xDragTouchOffset, y - yDragImageOffset - yDragTouchOffset, 0, 0);
		dragImage.setLayoutParams(lp);
	}

}
