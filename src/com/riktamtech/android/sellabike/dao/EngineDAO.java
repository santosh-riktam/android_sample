package com.riktamtech.android.sellabike.dao;

import java.io.Serializable;

public class EngineDAO implements Serializable {
	public String name;
	public int id;

	public EngineDAO(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public EngineDAO(String id, String name) {
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
		return this.id == ((EngineDAO) o).id;
	}
}
