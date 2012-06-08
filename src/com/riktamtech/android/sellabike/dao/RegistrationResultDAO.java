package com.riktamtech.android.sellabike.dao;

public class RegistrationResultDAO {
	public String makeName, modelName, colour, regDate;
	public int engineSize;

	public RegistrationResultDAO(String makeName, String modelName, String colour, String regDate, int engineSize) {
		this.makeName = makeName;
		this.modelName = modelName;
		this.colour = colour;
		this.engineSize = engineSize;
		this.regDate = regDate;
	}

	public RegistrationResultDAO() {
	}

	public String getEngineSizeString() {
		String engineSizeString = "";
		if (engineSize >= 0 && engineSize <= 125)
			engineSizeString = "0 - 125cc";
		else if (engineSize >= 126 && engineSize <= 250)
			engineSizeString = "126 - 250cc";
		else if (engineSize >= 251 && engineSize <= 500)
			engineSizeString = "251 - 500cc";
		else if (engineSize >= 501 && engineSize <= 750)
			engineSizeString = "501 - 750cc";
		else if (engineSize >= 751 && engineSize <= 1000)
			engineSizeString = "751 - 1000cc";
		else
			engineSizeString = "Over 1000cc";
		return engineSizeString;
	}

}
