package ekgp49.dbc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import ekgp49.dbc.domain.Information;
import ekgp49.dbc.domain.Review;
import ekgp49.dbc.handler.Command;
import ekgp49.dbc.handler.InformationAddCommand;
import ekgp49.dbc.handler.InformationDeleteCommand;
import ekgp49.dbc.handler.InformationListCommand;
import ekgp49.dbc.handler.InformationUpdateCommand;
import ekgp49.dbc.handler.ReviewAddCommand;
import ekgp49.dbc.handler.ReviewDeleteCommand;
import ekgp49.dbc.handler.ReviewListCommand;
import ekgp49.dbc.handler.ReviewSelectCommand;
import ekgp49.dbc.handler.ReviewUpdateCommand;
import ekgp49.dbc.handler.SearchCommand;
import util.Prompt;

public class App {
  static Scanner keyboard = new Scanner(System.in);
  static Deque<String> commandStack = new ArrayDeque<>();
  static Queue<String> commandQueue = new LinkedList<>();

  static List<Information> informationList = new LinkedList<>();
  static List<Review> reviewList = new LinkedList<>();

  public static void main(String[] args) {
    loadInformationData();
    loadReviewData();
    Prompt prompt = new Prompt(keyboard);

    HashMap<String, Command> commandHandler = new HashMap<>();
    commandHandler.put("/search", new SearchCommand(prompt, informationList));
    commandHandler.put("/info/add", new InformationAddCommand(prompt, informationList));
    commandHandler.put("/info/list", new InformationListCommand(informationList));
    commandHandler.put("/info/update", new InformationUpdateCommand(prompt, informationList));
    commandHandler.put("/info/delete", new InformationDeleteCommand(prompt, informationList));
    commandHandler.put("/review/add", new ReviewAddCommand(prompt, reviewList));
    commandHandler.put("/review/list", new ReviewListCommand(reviewList));
    commandHandler.put("/review/update", new ReviewUpdateCommand(prompt, reviewList));
    commandHandler.put("/review/delete", new ReviewDeleteCommand(prompt, reviewList));
    commandHandler.put("/review/star", new ReviewSelectCommand(prompt, reviewList));

    String command;
    while (true) {
      command = prompt.inputString("\n명령> ");
      if (command.length() == 0) {
        continue;
      } else if (command.equals("history")) {
        printCommandHistory(commandStack.iterator());
        continue;
      } else if (command.equals("history2")) {
        printCommandHistory(commandQueue.iterator());
        continue;
      } else if (command.equalsIgnoreCase("quit")) {
        System.out.println("종료합니다.");
        break;
      }

      commandStack.push(command);
      commandQueue.offer(command);

      if (commandHandler.get(command) != null) {
        try {
          commandHandler.get(command).execute();
        } catch (Exception e) {
          System.out.printf("명령어 실행 중 오류 발생: %s\n", e.getMessage());
        }
      } else {
        System.out.println("실행할 수 없는 명령입니다.");
      }
    }
    saveInformationData();
    saveReviewData();
  }

  private static void loadInformationData() {
    File file = new File("./information.csv");
    try (FileReader in = new FileReader(file); Scanner scanData = new Scanner(in);) {
      while (true) {
        try {
          String line = scanData.nextLine();
          String[] data = line.split(",");
          Information info = new Information();
          info.setNo(Integer.parseInt(data[0]));
          info.setCafeName(data[1]);
          info.setCafeAddress(data[2]);
          info.setCafeCall(data[3]);
          info.setCafeWebSite(data[4]);
          info.setOpenTime(data[5]);
          info.setCloseTime(data[6]);
          info.setHolliday(data[7]);
          info.setCafeMenu(data[8]);
          informationList.add(info);
        } catch (Exception e) {
          break;
        }
      }
    } catch (IOException e) {
      System.out.println("파일 읽기 중 오류 발생 - " + e.getMessage());
    }
    System.out.printf("총 %d개의 정보 데이터를 로딩했습니다.\n", informationList.size());
  }

  private static void saveInformationData() {
    File file = new File("./information.csv");
    try (FileWriter out = new FileWriter(file);) {
      for (Information i : informationList) {
        String line = String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s\n", i.getNo(), i.getCafeName(),
            i.getCafeAddress(), i.getCafeCall(), i.getCafeWebSite(), i.getOpenTime(),
            i.getCloseTime(), i.getHolliday(), i.getCafeMenu());
        out.write(line);
      }
      System.out.printf("총 %d개의 정보 데이터를 세이브했습니다.\n", informationList.size());
    } catch (IOException e) {
      System.out.println("파일 쓰기 중 오류 발생 - " + e.getMessage());
    }
  }

  private static void printCommandHistory(Iterator<String> iterator) {
    Iterator<String> commands = iterator;
    System.out.println("명령 목록 출력!");
    int count = 0;
    while (commands.hasNext()) {
      System.out.println(commands.next());
      if (++count % 5 == 0 && commands.hasNext()) {
        System.out.print(":");
        String str = keyboard.nextLine();
        if (str.equalsIgnoreCase("q")) {
          break;
        }
      }
    }
  }

  private static void loadReviewData() {
    File file = new File("./review.csv");
    try (FileReader in = new FileReader(file); Scanner scanData = new Scanner(in);) {
      while (true) {
        try {
          String line = scanData.nextLine();
          String[] data = line.split(",");
          Review r = new Review();
          r.setNo(Integer.parseInt(data[0]));
          r.setCafeName(data[1]);
          r.setCustomer(data[2]);
          r.setStarRate(Integer.parseInt(data[3]));
          r.setContent(data[4]);
          r.setViewCount(Integer.parseInt(data[5]));
          r.setCreatedDate(Date.valueOf(data[6]));
          r.setTimeFormFromToday(data[7]);
          reviewList.add(r);
        } catch (Exception e) {
          break;
        }
      }
    } catch (IOException e) {
      System.out.println("파일 읽기 중 오류 발생 - " + e.getMessage());
    }
    System.out.printf("총 %d개의 리뷰 데이터를 로딩했습니다.\n", reviewList.size());
  }

  private static void saveReviewData() {
    File file = new File("./review.csv");
    try (FileWriter out = new FileWriter(file);) {
      for (Review r : reviewList) {
        String line = String.format("%d,%s,%s,%d,%s,%s,%s,%s\n", r.getNo(), r.getCafeName(),
            r.getCustomer(), r.getStarRate(), r.getContent(), r.getViewCount(), r.getCreatedDate(),
            r.getTimeFormFromToday());
        out.write(line);
      }
      System.out.printf("총 %d개의 리뷰 데이터를 세이브했습니다.\n", reviewList.size());
    } catch (IOException e) {
      System.out.println("파일 쓰기 중 오류 발생 - " + e.getMessage());
    }
  }
}
