/**
 * 
 */
let QRwait = document.getElementById("QRwait");
let waiting = document.getElementById("waiting");
let moveGamePoint = document.getElementById("moveGamePoint");
const video = document.getElementById("OP");
const input = document.getElementById("qrid");
moveGamePoint.style.display = "none";
video.style.display = "none";

// 待機画面の切り替えロジック
let pollingInterval = null;
	function waitingPolling(){
		if (pollingInterval !== null)return; //二重起動防止
		pollingInterval = setInterval(async() =>{
			if(!stopFlg){
				const response = await fetch("/api/validPC");
				const useKahi = await response.json();
				if (useKahi){
					waiting.style.display = "none";
					QRwait.style.display= "block";
				}else{
					waiting.style.display = "block";
					QRwait.style.display= "none";
						
				}
			}
		},3000) //3秒ごとに確認
	}
// 動画再生から案内終了までtrue
let stopFlg = false;
// OP再生中はbvideoFlgがtrue
stopFlg = false;

	function startOP(){
		QRwait.style.display = "none";
		waiting.style.display = "none";
		video.style.display = "block";
		
		input.disabled = true;
		video.play();
		stopFlg = true;
	}
video.addEventListener("ended",()=>{
	video.style.display = "none";
	if (nowPC == 0){
		moveGamePoint.src = "案内画像ブース1.jpg";
	}else if (nowPC == 1){
		moveGamePoint.src = "案内画像ブース2.jpg";
	}else if (nowPC == 2){
		moveGamePoint.src = "案内画像ブース3.jpg";
	}else if (nowPC == 3){
		moveGamePoint.src = "案内画像ブース4.jpg";
	}
	moveGamePoint.style.display = "block";
	// 動画の後の案内画像表示時間
	setTimeout(() => {
		moveGamePoint.style.display = "none";
		waiting.style.display = "block";
		stopFlg = false;
		input.disabled = false;
		input.focus();
		waitingPolling();
	},5000);

	
})