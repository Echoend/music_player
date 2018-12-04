package sound;

import java.io.File;
import java.util.Scanner;


//import javazoom.jl.player.advanced.PlaybackListener;


public class MusicManager{
	
	public enum LoopMode{
		SINGLE,
		LIST,
		RAND,
		NONLOOP_SINGLE,
		NONLOOP_LIST,
	}
	
	public enum CMD{
		WAITING,
		PLAY,
		STOP,
		PAUSE,
		CHANGE,
		EXIT,
		
	}
	
	private sound.Player player;
//	private sound.Player last;
	
	private File[] musics;
	private File current;
//	private boolean changing;
	
	private LoopMode mode;
//	private CMD cmd;
	private int index;
	private boolean ended;
	
	public static void main(String[] args) {

		MusicManager manager ;
		//= new MusicManager(new String[] {"*.mp3"});
//		System.out.println(manager.getPlayer());
		
		Scanner scan= new Scanner(System.in);
		
		
		String[] files = new String[6];
		for(int i=0;i<6;++i) {
			files[i]="./music/"+(i+1)+".mp3";
		}
		manager= new MusicManager(files);
		manager.Play();
		
		
		int i=scan.nextInt();
		
		while(i!=0) {
			if(i<7) {
				manager.Change(i-1);
			}
			i=scan.nextInt();
		}
		
		System.out.println("Main end");
	}
	
	public MusicManager(String[] filenames) {
		mode = LoopMode.NONLOOP_LIST;
//		mode=LoopMode.SINGLE;
//		cmd=CMD.PLAY;
		musics=new File[filenames.length];
		for(int i=0;i<filenames.length;i++) {
			musics[i]=new File(filenames[i]);
		}
		current=musics[0];

		player=new MP3Player(musics[0].getAbsolutePath(),new CallBacker() {
			
			@Override
			public void callback() {
				Play();
			}
		});
		index=0;
		ended=false;
	}
	
	public boolean isPlaying() {
		return player.isPlaying();
	}
	
	public boolean hasEnded() {
		return ended;
	}
	
	
	public void Play() {
		System.out.println("\n\nLoopMode is : "+mode);
		
		switch (mode) {
		case NONLOOP_SINGLE:
			player.Stop();
			break;
		case SINGLE:
			if(player.isPlaying()) {
				player.Stop();
				System.out.println("last player has been stopped.");
			}
			player=null;
			player = new MP3Player(current.getAbsolutePath(), new Mycb(this));
			player.Start();
			System.out.println("new player has been started.");
			break;
		case LIST:
		case NONLOOP_LIST:
//			System.out.println("entered play method. step 1");
			index++;
			if(mode==LoopMode.NONLOOP_LIST && index==musics.length) break;

			index=index%musics.length;
			current=musics[index];
			System.out.println("file : "+current.getName()+" index "+index);
			Change(current);
//			System.out.println("entered play method. step 3");

			
			break;

		default:
			break;
		}
//		player.Start();
	}
	
	public void Change(File f) {
//		System.out.println("333");
		System.out.println("isplaying : "+player.isPlaying()); //true. ??
//		player.Pause();
//		System.out.println("PAUSEed");
//		
//		player.Stop();
//		System.out.println("STOPed");
		player=new MP3Player(f.getAbsolutePath(),new CallBacker() {
			@Override
			public void callback() {
				Play();
			}
		});
		player.Start();
	}
	
	public void Change(int i) {
		index=i;
		Change(musics[index]);
	}
	
	
	public void changeMusic() {
		player.Stop();
		index++;
		player=null;
		current=musics[index];
		player=new MP3Player(current.getAbsolutePath(),new Mycb(this));
		player.Start();
	}
	
	public sound.Player getPlayer() {
		return player;
	}
	
	public void Start() {
		player.Start();
		System.out.println("player thread for "+current.getName()+" has started.");
	}
	public void Pause() {
		player.Pause();
	}
	public void Resume() {
		player.Resume();
	}
	public void Stop() {
		player.Stop();
		player=null;
		
	}
	
	public LoopMode getLoopMode() {
		return mode;
	}
	public void setLoopMode(LoopMode loopMode) {
		mode=loopMode;
	}
	
	public void setMusicList(File[] files) {
		musics=files;
		current=files[0];
		System.out.println("setMusicList"+current.getName());
	}

	public File getCurrentMusic() {
		return current;
	}
}

class Mycb implements CallBacker{

	private MusicManager manager;
	public Mycb(MusicManager m) {
		manager=m;
	}
	
	@Override
	public void callback() {
		System.out.println("call back has started.");
		manager.Play();
		System.out.println("call back has ended.");
		// if last thread is stopped, then this object is freed. or so?
	}
	
}
