package dto;

public class ReviewDTO {
	private String movieCd;
	private String rcode;
	private String score;
	private String content;
	private String writer;
	private String regdate;
	
	public ReviewDTO() {
		
	}
	public ReviewDTO(String movieCd, String rcode,String score, String content, String writer, String regdate) {
		super();
		this.movieCd = movieCd;
		this.rcode = rcode;
		this.score = score;
		this.content = content;
		this.writer = writer;
		this.regdate = regdate;
	}
	public String getMovieCd() {
		return movieCd;
	}
	public void setMovieCd(String movieCd) {
		this.movieCd = movieCd;
	}
	
	public String getRcode() {
		return rcode;
	}
	public void setRcode(String rcode) {
		this.rcode = rcode;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getRegdate() {
		return regdate;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	@Override
	public String toString() {
		return "ReviewDTO [movieCd=" + movieCd + ", score=" + score + ", content=" + content + ", writer=" + writer
				+ ", regdate=" + regdate + "]";
	}
	
	
}
