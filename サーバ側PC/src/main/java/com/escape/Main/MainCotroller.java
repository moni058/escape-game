package com.escape.Main;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.escape.Main.MainService.Score;
import com.escape.Main.repositories.PlayerRepository;

@CrossOrigin(origins = {
		"http://localhost:1011", // 自分のゲーム画面
		"http://localhost:9911", // ゲームテスト用の画面
		"http://localhost:9912",
		"http://localhost:9913",
		"http://localhost:9914",
		"http://localhost:9911", 
		"http://localhost:9912",
		"http://10.26.84.1:9911", //PC1
		"http://10.26.84.2:9911", //PC2
		"http://10.26.84.3:9911", //PC3
		"http://10.26.84.4:9911", //PC4
		"http://10.26.84.5:9910", //サーバ
}) //許可したいクライアントのURL
@Controller
public class MainCotroller {
	
	int nowPC = 5;
	
	@Autowired
	PlayerRepository repository;
	
	@Autowired
	MainService mainService;

	@RequestMapping("/")
	public ModelAndView index(ModelAndView mav) {
		mav.addObject("floor", 1);
		mav.setViewName("game");
		Iterable<Player> list = repository.findAll();
		mav.addObject("data" , list);
		mav.setViewName("main");
		return mav;
	}
	
	// ゲームが終わったプレイヤーの成績を保管
	@ResponseBody
	@PostMapping("/api/player")
	public Player createPlayer(@RequestBody Player player) {
		return repository.save(player);
	}
	
	// リロードが終わったら使用状況をリセット
	@ResponseBody
	@PostMapping("/api/reset/{pcNum}")
	public void reset(@PathVariable int pcNum) {
		mainService.changePCUsing(pcNum);
		return;
	}

	
	/** ゲーム側のjsから呼び出されて、そのpcが空いていればplayer情報を返す
	 * 
	 * @param pcNum pc識別番号
	 * @return player情報 (pcが空いてない場合は空のplayerクラスを返す
	 */
	@ResponseBody
	@GetMapping("/api/PC/{pcNum}")
	public ResponseEntity<Player> assignPC(@PathVariable int pcNum) {
		Player player = mainService.getPlayer(pcNum);
		if (player == null) {
			return ResponseEntity.ok(new Player());
		}else {
			nowPC =pcNum;
			return ResponseEntity.ok(player);
		}
	}
	
	@ResponseBody
    @GetMapping("/api/nowPC")
    public Map<String, Integer> getNowPC() {
        Map<String, Integer> result = new HashMap<>();
        result.put("nowPC", nowPC);
        return result;
    }
	
	// QRコード読み取りで呼び出されて、サーバの次のplayerを登録する処理を起動
	@ResponseBody
	@PostMapping("/api/next/player")
	public void setNextPlayer(@RequestBody Map<String,String> payload) {
		String QRId = payload.get("QRId");
		mainService.entory(QRId);
		return;
	}
	
	@ResponseBody
	@GetMapping("/api/validPC")
	public boolean haveValidPC() {
		return mainService.haveValidPC();
	}
	
	// プレイヤー情報から成績を計算して返却
	@ResponseBody
	@PostMapping("/api/calcScore")
	public Score calcSocre(@RequestBody Player player) {
		Score score = mainService.calcScore(player);
		return score;
	}
	
}
