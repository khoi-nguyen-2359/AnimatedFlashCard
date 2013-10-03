package khoinguyen.sample.animflashcard;

public class Card {
	private String mTitle;
	private String mContent;
	
	public Card(String title, String content) {
		setTitle(title);
		setContent(content);
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String content) {
		mContent = content;
	}
}
