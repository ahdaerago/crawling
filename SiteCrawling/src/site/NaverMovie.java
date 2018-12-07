package site;

// 네이버 영화 정보 및 댓글 수집 프로그램
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import dao.NaverDAO;
import dto.DetailDTO;
import dto.PeopleDTO;
import dto.ReviewDTO;
 

public  class NaverMovie{

	String detail_url = null; // 연결할 상세 정보 페이지 url 
	String page_url = "https://movie.naver.com/movie/sdb/browsing/bmovie.nhn?open=2018&page=";		//페이지 소스(2018년 개봉영화)
	String base_url = "https://movie.naver.com"; // 영화 정보 및 댓글을 크롤링할 때 기준이 되는 url

	int page = 1; // 영화 리스트 페이지 수
	int mvTot = 0; // 크롤링한 영화 총 개수

	public void startCrawling() throws IOException {

		while(true) { // 페이지가 끝이 올 때까지 계속 반복문을 돈다!
			System.out.println(" 페이지 : "+page);
			Document page_doc = Jsoup.connect(page_url+page).get(); 

			Elements pagenavigation = page_doc.select(".pagenavigation td:last-child");
			String last_page = pagenavigation.text(); //해당 페이지네이션의 마지막 요소

			
			Elements mv_link = page_doc.select(".directory_list > li > a"); // 영화 상세페이지로 가는 링크를 가지고 온다.
			detailCrawling(mv_link); // 영화 상세 정보 크롤링

			if(!(last_page).equals("다음")) { // 맨 마지막 요소가 다음이 아니면 끝페이지이므로 종료!
				System.out.println("총 "+page + "페이지"+ mvTot +"개의 영화 수집 완료");
				break;
			}

			page++;

		}

 

	}

	// 영화 상세 정보 크롤링 부분

	public void detailCrawling(Elements mv_link) throws IOException{


		int i = 0;
 

		for (Element element : mv_link) {

			mvTot++;
			detail_url = element.attr("href"); // href 속성을 뽑아옴
			String mv_code = detail_url.substring(detail_url.indexOf("=")+1); // 영화 코드 추출

			Document detail_doc = Jsoup.connect(base_url+detail_url).get(); // 영화 상세 페이지로 연결

			

			Elements article = detail_doc.select(".article"); //영화 상세페이지 안에 있는 모든 내용을 가지고 온다.

			for (Element mv_detail : article) {
				String kor_tit = mv_detail.select(".mv_info_area > .mv_info > .h_movie > a:first-child").text(); // 영화제목 한국어 버전 뽑기
				String eng_tit = mv_detail.select(".mv_info_area > .mv_info > .h_movie2").text(); // 영화제목 영어 버전
				String poster = mv_detail.select(".mv_info_area > .poster img").attr("src"); // 포스터 이미지 링크 
				Elements info_spec = mv_detail.select(".mv_info_area .info_spec");
				
				String genre = ""; // 영화 장르
				String nation = ""; // 국가
				String openDt = ""; // 영화 개봉 날짜
				String director = ""; // 감독
				String lead_role = ""; // 주연
				
	
				for (Element cut_spec : info_spec) {
					genre = cut_spec.select("a[href*=genre]").text(); // 장르 보기
					nation = cut_spec.select("a[href*=nation]").text();
					openDt =(cut_spec.select("a[href*=open]").text()).replaceAll(" ",""); // 2018. 01.01 와 같이 나오는 날짜를 2018.01.01로 바꿔준다.
					director = ((cut_spec.select(".step2").next()).text()); //감독
					lead_role = ((cut_spec.select("a[href*=basic.nhn]").text())); //주연 

					
					
					// 다음 날짜 형식과 같은 형식으로 바꿔주기 위함
					System.out.println("장르 : "+genre);
					System.out.println("국가 : "+nation);
					System.out.println("개봉날짜 : "+openDt);
					System.out.println("감독 : "+director);
					System.out.println("주연 : "+lead_role);
				}
				Elements story = mv_detail.select(".story_area > h5,.story_area > .con_tx"); // 스토리 파싱 부분 
				DaumMovie dm = new DaumMovie();
				boolean exist = dm.searchMoive(kor_tit,openDt,mv_code);
				if(exist == false) { //다음에 영화가 존재  x => 데이터를 저장하지 않고 크롤링문을 빠져나감
					break;
				}else {
					
					//영화 상세정보 저장
					DetailDTO ddto = new DetailDTO(mv_code,kor_tit,eng_tit,poster,info_spec,genre,nation,openDt,director,lead_role);
					NaverDAO nDao = new NaverDAO();
					nDao.insertDetail(ddto);
					//출연 인물 및 감독 정보 크롤링
					madePeople(mv_code);
					reviewCrawling(mv_code); //리뷰 댓글 크롤링
				}

				System.out.println("영화코드 : "+mv_code);
				System.out.println("영화제목 : "+kor_tit);
				System.out.println("영화제목(영) : "+eng_tit);
				System.out.println("포스터 링크 : " +poster);
				System.out.println("영화 스토리 : "+story);	

				
				//System.exit(0);
				//reviewCrawling(mv_code); //리뷰 댓글 크롤링



				

			}

			
			System.out.println(mvTot+"번째 영화 상세페이지 링크 : "+detail_url);

			

		}		

	}

	//리뷰 크롤링 

	public void reviewCrawling(String mv_code) throws IOException {

		int page = 1; // 평점 댓글 페이지 수
		int num  = 0;

		String review_url = "https://movie.naver.com/movie/bi/mi/pointWriteFormList.nhn?code="+mv_code+"&type=after&onlyActualPointYn=N&order=newest"
				+ "&page="; // 개봉 후 평점 부분 url(관람객만 최신 순으로!)

		while(true) { // page가 끝일 때까지 계속 돌아라
			Document review_doc = Jsoup.connect(review_url+page).get(); // 리뷰 url 접속
			String review_last = review_doc.select(".paging > div > a:last-child").text(); // 페이지 네이션의 마지막 버튼 텍스트 출력

			Elements review = review_doc.select(".score_result > ul > li"); // 한 페이지 안의 평점 가져오기 ( 한 페이지 당  최대 10개)

			for (Element detail_review : review) {
				num++;
				String score = detail_review.select(".star_score em").text(); //평점 가져오기
				String cont = detail_review.select(".score_reple > p").text(); //댓글 내용 가져오기
				String content = cont;

				if(cont.length() >= 3) { //댓글 내용이 3글자 보다 길 때(관람객이라는 글자를 지우기 위해)

					content = cont.substring(3); // 맨 앞 관람객이라는 글자 자르기

				}

				String prev_code = detail_review.select(".score_reple > dl > dt > em >a").attr("onclick");

				// onclick 이벤트에 댓글 코드가 있으므로 attr을 통해 onclick 속성을 추출

				String rcode = prev_code.substring(prev_code.indexOf("(")+1,prev_code.indexOf(","));

				// 댓글 번호만 추출하는 코드

						

				String writer = detail_review.select(".score_reple > dl > dt > em >a").text();
				String regdate = detail_review.select(".score_reple > dl > dt > em:nth-child(2)").text();

				

				System.out.println("========"+num+"번째 댓글 수집 시작=======");
				System.out.println("댓글코드 : "+rcode);
				System.out.println("평점 : "+score);
				System.out.println("내용 : "+content);
				System.out.println("작가 : "+writer);
				System.out.println("댓글입력 날짜 : "+regdate);
				
				ReviewDTO rDto = new ReviewDTO(mv_code, rcode, score, content, writer, regdate);
				NaverDAO nDao = new NaverDAO();
				nDao.insertReview(rDto); // mongodb에 저장
				

			}

			

			

			

			if(!review_last.equals("다음") ) { // 마지막 버튼이 다음이 아니면 무한 루프를 종료하라!

				System.out.println(mv_code+"의 "+page+"페이지 댓글 총 "+num+"개 수집 완료!");

				break;

			}

			page++;

		}
		

	}
	//배우정보 크롤링
	public void madePeople(String mv_code) throws IOException {
		String dept = "";
		String detail_url = "https://movie.naver.com/movie/bi/mi/detail.nhn?code="+mv_code; // 출연배우들을 볼 수 있는 url 로 간다.
		
		Document ppl_doc = Jsoup.connect(detail_url).get();
		
		
		Elements act_list = ppl_doc.select(".lst_people > li");
		if(act_list.size()>0) {
			crawlingPeople(act_list,"배우",mv_code);
			
		}
		
		

		/*****감독 정보 수집******/
		Elements dir = ppl_doc.select(".dir_obj");
		if(dir.size()>0) {
			crawlingPeople(dir,"감독",mv_code);
		}
		
	
		
	
	}
	public void crawlingPeople(Elements lst, String dept,String mv_code) {
		String p_code = "";
		String pcode= "";
		String profile = "";
		String pname = ""; // 배우이름
		String eng_name = "" ;// 배우이름(영어)
		String part = ""; // 주연? 조연?
		String role = "";//맡은 배역
		
		for (Element element : lst) {
			Elements p_ele = element.select("img");
			profile = p_ele.attr("src"); // 프로필 경로 가지고 오기
			p_code = element.select(".k_name").attr("href");
			pcode = p_code.substring(p_code.indexOf("=")+1, p_code.length());
			pname = element.select(".p_info .k_name").text(); // 한국이름
			eng_name = element.select(".e_name").text(); // 영어이름
			part = element.select(".p_part").text();
			role = element.select(".pe_cmt").text();
		
			
			System.out.println("==============출연자 정보=============");
			System.out.println("직업 : "+dept);
			System.out.println("인물코드 : "+pcode);
			System.out.println("프로필 : "+p_ele.attr("src"));
			System.out.println("이름 : " + pname);
			System.out.println("영어 이름 : "+eng_name);
			System.out.println("출연 : "+part);
			System.out.println("역할 : "+role);
			
			PeopleDTO pDto = new PeopleDTO(mv_code, dept, pcode, profile, pname, eng_name, part, role);
			NaverDAO nDao = new NaverDAO();
			nDao.insertPeople(pDto);
		}
				
	}

}

