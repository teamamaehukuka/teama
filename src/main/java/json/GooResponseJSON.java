package json;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class GooResponseJSON {
	@Getter @Setter
	List<Map<String, Double>> keywords;

	@Getter @Setter
	String request_id;

}
