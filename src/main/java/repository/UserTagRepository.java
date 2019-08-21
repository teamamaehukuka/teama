package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import entity.UserTag;

public class UserTagRepository {

	static final List<UserTag> list = new ArrayList<UserTag>();

	public static List<UserTag> getList(){
		return list;
	};

	public static List<UserTag> getMatchedList(String content){
		return list.stream().filter(s -> content.equals(s.getTag())).collect(Collectors.toList());
	};

	public static void add(UserTag ut) {
		list.add(ut);
	}

	static {
		list.add(new UserTag("UK94F9EDD","java"));
		list.add(new UserTag("UK94F9EDD","ピカチュウ"));
		list.add(new UserTag("UJVR39QMR","java"));
	}

}
