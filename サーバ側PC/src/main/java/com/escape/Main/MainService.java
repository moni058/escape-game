package com.escape.Main;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escape.Main.repositories.PlayerRepository;

@Service
public class MainService {
	
	private boolean[] PCUsing = {false,true,false,true};
	private Player nextPlayer; //受付をしているプレイヤー
	private int nextPC;
	private int nowPC;

	@Autowired
	PlayerRepository repository;
	
	// PCの使用可否
	boolean[] gamePcValid = new boolean[4];
	/**
	 *  QRコードリーダーに起動されて、次のプレイヤーを登録するやつ
	 * @param QRId
	 */
	public void entory(String QRId) {
		// データベースとPlayerの色々やるやつ
		Player player = getPlayerData(QRId);
		this.nextPlayer = player;
	}
	
	/**
	 * 
	 * @param QRId QRコードのID
	 * @return Player DBになかったら新しいのを生成、あったらそれをDBから持ってくる
	 */
	public Player getPlayerData(String QRId) {
		Optional<Player> playerOpt = repository.findByQRId(QRId);
		Player player;
		// データがあるかどうか
		if (playerOpt.isPresent()) {
			player = playerOpt.get();
		}else {
			player = new Player(QRId);
		}
		return  player;
	}
	
	/** 受付をしているplayer情報をポーリングしてきたpcに渡す関数
	 * 
	 * @param pcNum リクエストを送ってきたPCのPC番号
	 * @return 受付をしているPlayerのデータ
	 */
	public synchronized  Player getPlayer(int pcNum) {
		if (!(this.nextPlayer == null)) {
			System.out.println(Arrays.toString(PCUsing) + pcNum);
			Player player = new Player(this.nextPlayer);
			if (!PCUsing[pcNum]) {
				PCUsing[pcNum] = true;
				nextPlayer = null;
				nowPC = pcNum;

				return player;
			}
		}
		return null;
	}
	

	
	/**
	 * 成績送信と同時に使用していないに戻す(実行するタイミングは後ほど調整)
	 * @param pcNum
	 */
	public void changePCUsing(int pcNum){
		PCUsing[pcNum] = false;
		return;
	}
	
	/**
	 * 
	 * @return validFlg 使えるPCがあったらtrue
	 */
	public boolean haveValidPC() {
		boolean validFlg = false;
		for (boolean using :PCUsing) {
			if(!using) {
				validFlg = true;
			}
			if(nextPlayer != null) {
				validFlg = false;
			}
		}
		return validFlg;
	}
	
	/**
	 * ゲーム終了時に成績を計算して返却する
	 * @param player ゲームを終了したプレイヤー
	 * @return Score 上から順に全プレイヤーの数、順位、上位何%か
	 */
	public Score calcScore(Player player) {
		List<Player> players = repository.findAll();
		List<Integer> floors = players.stream()
                .map(Player::getFloor)
                .collect(Collectors.toList());
		int reachedFloor = player.getFloor();
		// 降順ソート（重複あり）
		List<Integer> sortedDesc = floors.stream()
		    .sorted(Comparator.reverseOrder())
		    .collect(Collectors.toList());

		// targetの最初に出る位置（0始まり）
		int index = sortedDesc.lastIndexOf(reachedFloor);
		boolean newFlg = false;
		while (index == -1) {
			newFlg = true;
			reachedFloor--;
			index = sortedDesc.lastIndexOf(reachedFloor);
		}

		// 1始まりの順位に変換
		int ranking = index + 1;
		
		// プレイヤーの数
		int totalPlayer = floors.size();
		
		// 上位何%か
		double rate= ((double)ranking / totalPlayer)*100;
		
		String playerRank = "C";
		
		if (rate <= 5) {
			playerRank = "S";
		}else if (rate <= 15) {
			playerRank = "A+";
		}else if (rate <= 25) {
			playerRank = "A";
		}else if (rate <= 50) {
			playerRank = "B";
		}
		Score score = new Score(totalPlayer ,playerRank, ranking ,player.getFloor());
		return score;
		
	}
	
	// ゲームPCに成績を返す際に使用するクラス
	public class Score {
		private int totalPlayer;
		private String playerRank;
		private double ranking;
		private int floor;

		public Score(int totalPlayer, String playerRank, double ranking, int floor) {
			this.totalPlayer = totalPlayer;
			this.playerRank = playerRank;
			this.ranking = ranking;
			this.floor = floor;
		}

		public int getTotalPlayer() {
			return totalPlayer;
		}

		public String getPlayerRank() {
			return playerRank;
		}

		public double getRanking() {
			return ranking;
		}
		public double getFloor() {
			return floor;
		}
	}
}

