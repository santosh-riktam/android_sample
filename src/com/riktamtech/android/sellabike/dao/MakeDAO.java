package com.riktamtech.android.sellabike.dao;

import java.io.Serializable;

public class MakeDAO implements Serializable {
	public String name;
	public int id;

	public MakeDAO(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public MakeDAO(String id, String name) {
		this(Integer.parseInt(id), name);
	}

	@Override
	public String toString() {
		super.toString();
		return name;
	}

	@Override
	public boolean equals(Object o) {
		super.equals(o);
		return this.id == ((MakeDAO) o).id;
	}
}