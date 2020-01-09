package ekgp49.dbc.handler;

import java.sql.Date;
import java.util.Arrays;
import ekgp49.dbc.domain.Review;
import util.ArrayList;
import util.Prompt;

public class ReviewHandler {
  ArrayList<Review> reviewList;
  private int no = 1;
  Prompt prompt;

  public ReviewHandler(Prompt prompt) {
    this.prompt = prompt;
    reviewList = new ArrayList<>();
  }

  public ReviewHandler(Prompt prompt, int capacity) {
    this.prompt = prompt;
    reviewList = new ArrayList<>(capacity);
  }

  public void listReview() {
    System.out.println("리뷰");
    Review[] arr = reviewList.toArray(new Review[] {});
    for(Review r : arr) {
      System.out.printf("%d, %s, %s, %s, %s, %s, %s \n%s\n", 
          r.getNo(), r.getCafeName(), r.getCustomer(), r.getStarRate(), r.getCreatedDate(),
          r.getTimeFormFromToday(), r.getViewCount(), r.getContent());
    }
  }

  public void addReview() {
    System.out.println("리뷰");
    Review review = new Review();
    review.setNo(this.no++);
    review.setCafeName(prompt.inputString("카페 상호: "));
    review.setCustomer(prompt.inputString("고객: "));
    review.setStarRate(prompt.inputInt("별점: "));
    review.setContent(prompt.inputString("내용은: "));
    review.setCreatedDate(new Date(System.currentTimeMillis())); 
    review.setTimeFormFromToday(String.format("%1$tp %1$tH:%1$tM:%1$tS ", new java.util.Date()));
    review.setViewCount(0);

    reviewList.add(review);
    System.out.println("저장하였습니다.");
  }

  public void updateReview() {
    int index = indexOfReview(prompt.inputInt("리뷰 번호는? "));
    if (index == -1) {
      System.out.println("해당 번호의 리뷰가 없습니다.");
      return;
    }

    Review old = this.reviewList.get(index);

    String answer = prompt.inputString("리뷰를 변경하시겠습니까? (Y/n)");
    if (!answer.equalsIgnoreCase("y")) {
      System.out.println("리뷰 변경을 취소했습니다.");
      return;
    }
    Review review = new Review();
    review.setNo(this.reviewList.get(index).getNo());
    review.setCafeName(old.getCafeName());
    review.setCustomer(old.getCustomer());
    review.setStarRate(prompt.inputInt(String.format("별점(%s) : ", 
        String.valueOf(this.reviewList.get(index).getStarRate())), 
        this.reviewList.get(index).getStarRate()));
    review.setContent(prompt.inputString(String.format("내용: ", old.getContent())));
    review.setCreatedDate(old.getCreatedDate()); 
    review.setTimeFormFromToday(old.getTimeFormFromToday());
    review.setViewCount(old.getViewCount());

    if (!review.equals(old)) {
      this.reviewList.set(review, index);
      System.out.println("리뷰를 수정했습니다.");
    } else {
      System.out.println("리뷰 변경을 취소했습니다.");
    }
  }

  public void deleteReview() {
    int index = indexOfReview(prompt.inputInt("리뷰 번호는? "));
    if (index == -1) {
      System.out.println("해당 번호의 리뷰가 없습니다.");
      return;
    }

    this.reviewList.remove(index);
    System.out.println("리뷰를 삭제했습니다.");
  }

  public void SelectStarRateReview() {
    int star = prompt.inputInt("별점: ");      
    Review[] arr = new Review[this.reviewList.size()];
    int count = 0;
    for (int i = 0; i < this.reviewList.size(); i++) {
      if (this.reviewList.get(i).getStarRate() == star) {
        Review review = this.reviewList.get(i);
        arr[count++] = review;
      }
    }
    arr = Arrays.copyOf(arr, count);

    for (Review r : arr) {
      System.out.println(r.getCafeName());
    }
  } 

  private int indexOfReview(int no) {
    for (int i = 0; i < this.reviewList.size(); i++) {
      if (this.reviewList.get(i).getNo() == no) {
        return i;
      }
    }
    return -1;
  }
}
