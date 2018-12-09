package dao;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import dto.DetailDTO;
import dto.PeopleDTO;
import dto.ReviewDTO;

public class NaverDAO {
	//상세정보 저장
	public void insertDetail(DetailDTO dDto) {
		MongoClient mClient = new MongoClient("localhost",27017);
		MongoDatabase trendDB = mClient.getDatabase("movie");
		MongoCollection<Document> collection = trendDB.getCollection("detail");
		Document doc = new Document("movieCd",dDto.getMovieCd())
				.append("kor_tit",dDto.getKor_tit())
				.append("eng_tit", dDto.getEng_tit())
				.append("poster",dDto.getPoster())
				.append("story",dDto.getStory())
				.append("genre",dDto.getGenre())
				.append("nation", dDto.getNation())
				.append("openDt", dDto.getOpenDt())
				.append("director",dDto.getDirector())
				.append("lead_role", dDto.getLead_role());
		collection.insertOne(doc);
		System.out.println("일일"+dDto.toString());
		mClient.close(); //연결해제 자원 반납
		
		
	}
	//사람 정보 저장
	public void insertPeople(PeopleDTO pDto) {

		
	}
	//리뷰 정보 저장
	public void insertReview(ReviewDTO rDto) {
		MongoClient mClient = new MongoClient("localhost",27017);
		MongoDatabase trendDB = mClient.getDatabase("test");
		MongoCollection<Document> collection = trendDB.getCollection("Naverreview2");
		Document doc = new Document("movieCd",rDto.getMovieCd())
				.append("rcode", rDto.getRcode())
				.append("score", rDto.getScore())
				.append("content", rDto.getContent())
				.append("writer", rDto.getWriter())
				.append("regdate", rDto.getRegdate());
		collection.insertOne(doc);
		System.out.println("일일"+rDto.toString());
		mClient.close(); //연결해제 자원 반납
	}
}
