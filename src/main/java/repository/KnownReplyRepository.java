package repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import entity.KnownReply;

public class KnownReplyRepository {

	static final List<KnownReply> list = new ArrayList<KnownReply>();

	public static List<KnownReply> getList(){
		return list;
	};

	public static List<KnownReply> getMatchedList(String content){
		return list.stream().filter(s -> content.equals(s.getKnownKey())).collect(Collectors.toList());
	};

	static {
		list.add(new KnownReply("javac","javaをコンパイルすること"));
		list.add(new KnownReply("ピカチュウ","電気ネズミ"));
		list.add(new KnownReply("前泊","実家なら2500円、営業旅館なら10000円"));
		list.add(new KnownReply("SLCハッカソン","第1回優勝チームはAチームです"));
	}

}
