package kimjang.toolkit.solsol.domain.message;

public class HelloMessage {

	private String chat;

	public HelloMessage() {
	}

	public HelloMessage(String chat) {
		this.chat = chat;
	}

	public String getChat() {
		return chat;
	}

	public void setName(String chat) {
		this.chat = chat;
	}
}