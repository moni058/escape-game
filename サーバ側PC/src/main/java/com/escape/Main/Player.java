package com.escape.Main;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Player")
public class Player{
	
	@Column
	private long Id;
	
	@Id
	@Column(nullable = false)
	private String QRId;
	
	@Column(nullable = false)
	private int floor;
	
	@Column(nullable = false)
	private int tryCounts;
	
	@Column(nullable = false)
	private String name;
	
	// 初回生成コンストラクタ
	public Player(String QRId) {
		this.QRId = QRId;
		this.floor = 1;
		this.tryCounts = 0;
		// ここは後で入力ロジック作る
		this.name = "dammy";
	}
	
    // コピーコンストラクタ：同じクラスのオブジェクトを受け取ってフィールドをコピー
    public Player(Player other) {
        this.Id = other.Id;
        this.name = other.name;
        this.floor = other.floor;
        this.tryCounts = other.tryCounts;
        this.QRId = other.QRId;
        
    }
    
    public Player() {
    	
    }
	
	public long getId() {
	    return Id;
	}

	public void setId(long id) {
	    this.Id = id;
	}

	public String getQRId() {
	    return QRId;
	}

	public void setQRId(String QRId) {
	    this.QRId = QRId;
	}

	public int getFloor() {
	    return floor;
	}

	public void setFloor(int floor) {
	    this.floor = floor;
	}

	public int getTryCounts() {
	    return tryCounts;
	}

	public void setTryCounts(int tryCounts) {
	    this.tryCounts = tryCounts;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}
	@Override
	public String toString() {
		return this.QRId;
	}
}