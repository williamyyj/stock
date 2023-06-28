package org.cc.stock.col;

import java.util.List;

import org.cc.json.JSONObject;

public interface IFNStock {
	double v(List<JSONObject> rows, int idx, String id);
}
