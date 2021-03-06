package com.riktamtech.android.sellabike.dao;

import java.io.Serializable;

public class ColourDAO implements Serializable {
	public String name;
	public int id;

	public ColourDAO(int id, String name) {
		this.id = id;
		this.name = name;
		
	}

	public ColourDAO(String id, String name) {
		this(Integer.parseInt(id), name);
	}

	@Override
	public String toString() {
		super.toString();
		return name;
	}

	@Override
	public boolean equals(Object o) {
		return this.id == ((ColourDAO) o).id;
	}
}
