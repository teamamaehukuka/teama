package entity;

public class UserTag {

	public UserTag(String userid, String tag) {
		this.userid = userid;
		this.tag = tag;
	}

	String userid;
	String tag;

	public String getTag() {
		return tag;
	}
	public String getUserid() {
		return userid;
	}

}
