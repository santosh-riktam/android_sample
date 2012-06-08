package com.riktamtech.android.sellabike.dao;


public class ModelDAO {
	public int id, makeid;

	public String name, engineid;

	public ModelDAO(int id, int makeid, String engineid, String name) {
		super();
		this.id = id;
		this.makeid = makeid;
		this.engineid = engineid;
		this.name = name;
	}

	public ModelDAO(String id, String makeid, String engineid, String name) {
		super();
		this.id = Integer.parseInt(id);
		this.makeid = Integer.parseInt(makeid);
		this.engineid = engineid;
		this.name = name;
	}

	@Override
	public String toString() {
		super.toString();
		return name;
	}
	@Override
	public boolean equals(Object o) {
		super.equals(o);
		return this.id==((ModelDAO)o).id;
	}
}