package com.game.shooting;

import java.awt.*;
import java.awt.event.*;//KeyListener(Interface)를 사용하려면 필요하다
import java.awt.image.*;
import java.util.ArrayList;
import javax.swing.*; //JFrame사용에 필요한듯
//주석에 멋진말 필요없엉! 이럴거같으면 이렇거 같다고 쓰기!

//GUI를 구현하려면 JFrame을 상속받아야 한다.
//Runnable(Interface)기본적으로 제공되는 인터페이스 //Thread 만들때 implements해야한다.
//KeyListener : 키보드에서 키 눌리는거 처리할 때 사용

/*class What extends Thread{
	implements Runnable 를 사용해도 된다!
}*/
public class Shoot extends JFrame implements Runnable, KeyListener {
	//멤버변수
	private BufferedImage bi = null;
	private ArrayList msList = null; //ArrayList를 담을 (참조)변수 선언
	private ArrayList enList = null; //ArrayList를 담을 (참조)변수 선언
	//방향키, 발사키 정보 담는 변수인듯
	private boolean left = false, right = false, up = false, down = false, fire = false;
	private boolean start = false, end = false;
	private int w = 300, h = 500, x = 130, y = 450, xw = 20, xh = 20; //수치가 나오면 수치를 바꿔보세요
			//w:창 너비 h:창 높이 x:y: 플레이어x,y좌표 xw:xh:객체 크기
	public Shoot() { //Shoot클래스 생성자 //생성자의 역할-> 객체생성초기화 해줘야 할 작업
		bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		msList = new ArrayList(); //ArrayList 객체 생성
		enList = new ArrayList(); //ArrayList 객체 생성
		this.addKeyListener(this); //keylistener 추가
		this.setSize(w, h); //창의 크기 결정 //위에 w,h 변수 값이랑 관련이 있구나!
		this.setTitle("Shooting Game by KJH"); //창 제목
		this.setResizable(false); //창 크기 조절 (true:가능,false:불가)
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //X버튼 누르면 프로그램도 같이 종료
		this.setVisible(true); //화면 보이기(true:보여줌,false:안보임-예외발생-->draw())
	}

	public void run() {
		try {
			int msCnt = 0;
			int enCnt = 0;
			while (true) { //무한루프다!
				Thread.sleep(10); //잠시 정지시키는 메소드, 속도조절!

				if (start) { //start 참이면 아래로 ->
					if (enCnt > 200) { //enCnt가 2000보다 크면 실행
						enCreate(); //뭘까!!!!
						enCnt = 0;
					}
					if (msCnt >= 100) {
						fireMs();
						msCnt = 0;
					}
					msCnt += 10;
					enCnt += 10;
					keyControl();
					crashChk();
				}
				draw();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fireMs() {
		if (fire) {
			if (msList.size() < 100) {
				Ms m = new Ms(this.x, this.y);
				msList.add(m);
			}
		}
	}

	public void enCreate() { //rx,ry 지역변수 로컬변수
		for (int i = 0; i < 9; i++) { //i가 0부터9까지
			double rx = Math.random() * (w - xw); //난수설정 //창 너비-객체 
			double ry = Math.random() * 50;
			Enemy en = new Enemy((int) rx, (int) ry); //적군이다!!!!!적군
			enList.add(en);
		}
	}

	public void crashChk() {
		Graphics g = this.getGraphics();
		Polygon p = null;
		for (int i = 0; i < msList.size(); i++) {
			Ms m = (Ms) msList.get(i);
			for (int j = 0; j < enList.size(); j++) {
				Enemy e = (Enemy) enList.get(j);
				int[] xpoints = { m.x, (m.x + m.w), (m.x + m.w), m.x };
				int[] ypoints = { m.y, m.y, (m.y + m.h), (m.y + m.h) };
				p = new Polygon(xpoints, ypoints, 4);
				if (p.intersects((double) e.x, (double) e.y, (double) e.w, (double) e.h)) {
					msList.remove(i);
					enList.remove(j);
				}
			}
		}
		for (int i = 0; i < enList.size(); i++) {
			Enemy e = (Enemy) enList.get(i);
			int[] xpoints = { x, (x + xw), (x + xw), x };
			int[] ypoints = { y, y, (y + xh), (y + xh) };
			p = new Polygon(xpoints, ypoints, 4);
			if (p.intersects((double) e.x, (double) e.y, (double) e.w, (double) e.h)) {
				enList.remove(i);
				start = false;
				end = true;
			}
		}
	}

	public void draw() {
		Graphics gs = bi.getGraphics();
		gs.setColor(Color.white);
		gs.fillRect(0, 0, w, h);
		gs.setColor(Color.black);
		gs.drawString("Enemy 객체수 : " + enList.size(), 180, 50);
		gs.drawString("Ms 객체수 : " + msList.size(), 180, 70);
		gs.drawString("게임시작 : Enter", 180, 90);

		if (end) {
			gs.drawString("G A M E     O V E R", 100, 250);
		}

		gs.fillRect(x, y, xw, xh);

		for (int i = 0; i < msList.size(); i++) {
			Ms m = (Ms) msList.get(i);
			gs.setColor(Color.blue);
			gs.drawOval(m.x, m.y, m.w, m.h);
			if (m.y < 0)
				msList.remove(i);
			m.moveMs();
		}
		gs.setColor(Color.black);
		for (int i = 0; i < enList.size(); i++) {
			Enemy e = (Enemy) enList.get(i);
			gs.fillRect(e.x, e.y, e.w, e.h);
			if (e.y > h)
				enList.remove(i);
			e.moveEn();
		}

		Graphics ge = this.getGraphics();
		try {
			ge.drawImage(bi, 0, 0, w, h, this);
		} catch (java.lang.NullPointerException e) {
			//java.lang.NullPointerException 예외가 생기면 실행할 코드
			//예외가 발생했다고 프로그램이 아예 종료되면 안된다!
			//예외가 났어도 전에 입력했던 정보가 다 사라지지않고 다시 나타나게!
			System.out.println("java.lang.NullPointerException 이라는 예외 발생");
		}
		
	}

	public void keyControl() {
		if (0 < x) {
			if (left)
				x -= 3;
		}
		if (w > x + xw) {
			if (right)
				x += 3;
		}
		if (25 < y) {
			if (up)
				y -= 3;
		}
		if (h > y + xh) {
			if (down)
				y += 3;
		}
	}

	public void keyPressed(KeyEvent ke) {
		switch (ke.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			left = true;
			break;
		case KeyEvent.VK_RIGHT:
			right = true;
			break;
		case KeyEvent.VK_UP:
			up = true;
			break;
		case KeyEvent.VK_DOWN:
			down = true;
			break;
		case KeyEvent.VK_A:
			fire = true;
			break;
		case KeyEvent.VK_ENTER:
			start = true;
			end = false;
			break;
		}
	}

	public void keyReleased(KeyEvent ke) {
		switch (ke.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			left = false;
			break;
		case KeyEvent.VK_RIGHT:
			right = false;
			break;
		case KeyEvent.VK_UP:
			up = false;
			break;
		case KeyEvent.VK_DOWN:
			down = false;
			break;
		case KeyEvent.VK_A:
			fire = false;
			break;
		}
	}

	public void keyTyped(KeyEvent ke) {
	}

	public static void main(String[] args) { //메인메소드
		Thread t = new Thread(new Shoot()); //쓰레드 하나 생성, Shoot클래스를 가지고 쓰레드 만드는겅가?
		t.start();//쓰레드 실행
	}
}

class Ms {
	int x;
	int y;
	int w = 5;
	int h = 5;

	public Ms(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void moveMs() {
		y--;
	}
}

class Enemy {
	int x;
	int y;
	int w = 10;
	int h = 10;

	public Enemy(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void moveEn() {
		y++;
	}
}