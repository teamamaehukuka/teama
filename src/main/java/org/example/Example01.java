package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.riversun.slacklet.Slacklet;
import org.riversun.slacklet.SlackletRequest;
import org.riversun.slacklet.SlackletResponse;
import org.riversun.slacklet.SlackletService;
import org.riversun.xternal.simpleslackapi.SlackSession;
import org.riversun.xternal.simpleslackapi.impl.SlackSessionFactory;

import entity.KnownReply;
import entity.UserTag;
import repository.KnownReplyRepository;
import repository.UserTagRepository;

public class Example01 {

	public static void main(String[] args) throws IOException {

		String botToken = ResourceBundle.getBundle("credentials").getString("slack.bot_api_token");

		SlackletService slackService = new SlackletService(botToken);

		slackService.addSlacklet(new Slacklet() {

			@Override
			public void onDirectMessagePosted(SlackletRequest req, SlackletResponse resp) {
				// BOT宛のダイレクトメッセージがポストされた

				String content = req.getContent();

				// メッセージを送信したユーザーのメンションを取得する
				String mention = req.getUserDisp();

				// ダイレクトメッセージを送信したユーザーに対して返信する
				resp.reply(mention + "さん、ダイレクトメッセージありがとう。\n「" + content + "」って言いましたね");
			}

			@Override
			public void onMentionedMessagePosted(SlackletRequest req, SlackletResponse resp) {
				// あるチャンネルでこのBOTへのメンション付きメッセージがポストされた(例　「@smilebot おはよう」）

				String content = req.getContent();

				String noMentionContent = content.replaceAll("<.*>", "");

				String keyword  = NamedEntityAPI.analyzeByGooTokeyword(noMentionContent);

				String mention = req.getUserDisp();

				System.out.println(req.getRawPostedMessage().getJsonSource());

				List<KnownReply> knownList = KnownReplyRepository.getMatchList(keyword);

				List<UserTag> tagUserlist = UserTagRepository.getMatchList(keyword) ;

				if(knownList.isEmpty() && tagUserlist.isEmpty()) {
					String reply = nativeTalk(noMentionContent);
					sendAsThread(req, resp,reply);

				}
				else {
					sendAsThread(req,resp,"こんにちは、" + mention + "さん。「" + keyword + "」の話ですね");
					if(!knownList.isEmpty()) {

						knownList.stream()
						.forEach(s -> sendAsThread(req,resp,s.getKnownKey() + "は「" + s.getReply() + "」やで。"));
					}
					if(!tagUserlist.isEmpty()) {
						tagUserlist.stream()
						.forEach(s -> sendAsThread(req,resp,"<@" + s.getUserid() + "> さん。" + s.getTag() + "やぞ。"));
					}
				}

			}


		});

		slackService.start();

	}

	/**
	 * unicode文字列を文字列に変換(u\3042 ⇒ あ)
	 *
	 * @param unicode unicode文字列を含む文字列
	 * @return 文字列
	 */
	private static String convertToOiginal(String unicode) {
		String tmp = unicode;
		while (tmp.indexOf("\\u") > 0) {
			String str = tmp.substring(tmp.indexOf("\\u"), tmp.indexOf("\\u") + 6);
			int c = Integer.parseInt(str.substring(2), 16);
			tmp = tmp.replaceFirst("\\" + str, new String(new int[]{c}, 0, 1));
		}
		return tmp;
	}

	static void sendAsThread(SlackletRequest req, SlackletResponse resp, String message) {

		try {
			String botToken = ResourceBundle.getBundle("credentials").getString("slack.bot_api_token");

			SlackSession session = SlackSessionFactory.createWebSocketSlackSession(botToken);

			session.connect();

			Map<String, String> params = new HashMap<>();

			params.put("token",botToken);
			params.put("channel",req.getChannel().getId());
			params.put("text",message);
			params.put("thread_ts",req.getRawPostedMessage().getTimestamp());

			session.postGenericSlackCommand(params, "chat.postMessage");

		}catch(IOException ioe){
			ioe.printStackTrace();
		}

	}

	static String nativeTalk(String content) {
		String reply = "エラーです。"; // 返り値
		try
		{
			URL TestURL
			= new URL("https://api.a3rt.recruit-tech.co.jp/talk/v1/smalltalk");
			URLConnection con = TestURL.openConnection();

			//	送信するよ！指定
			con.setDoOutput(true);

			//--------------------
			//送信する
			//--------------------
			OutputStreamWriter	ow1
			= new OutputStreamWriter(con.getOutputStream());
			BufferedWriter bw1 = new BufferedWriter(ow1);

			//POSTの内容を書き出す
			bw1.write("apikey=DZZ6NlFh2sNTmKhBYilbVyWrBezkk3s0&query=" + content);

			//	クローズ
			bw1.close();
			ow1.close();

			//--------------------
			//受信する
			//--------------------
			InputStreamReader	ir1
			= new InputStreamReader(con.getInputStream());
			BufferedReader	br1 = new BufferedReader(ir1);


			StringBuilder buf = new StringBuilder();
			String line;

			while ((line = br1.readLine()) != null) {
				buf.append(line);
			}

			System.out.println(convertToOiginal(buf.toString()));

			String hensindata = convertToOiginal(buf.toString());
			//replyタグの後ろを取り出す
			int offset = 0;
			offset = hensindata.indexOf("reply");
			if (hensindata.startsWith("reply", offset)) {
				int end = hensindata.indexOf("}", offset);
				reply = hensindata.substring(offset+9, end-1);
			}

			//	クローズ
			br1.close();
			ir1.close();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		// 返信
		return reply;
	}

}
