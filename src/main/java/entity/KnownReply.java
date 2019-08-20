package entity;

import lombok.Getter;

public class KnownReply {

	public KnownReply(String knownKey, String reply) {
		this.knownKey = knownKey;
		this.reply = reply;
	}
	@Getter
	String knownKey;
	@Getter
	String reply;


}
