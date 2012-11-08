package cn.fmsoft.lnx.gmud.simple.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cn.fmsoft.lnx.gmud.simple.OnlineWifi;

public class Online {

	private static DataOutputStream dos;
	private static DataInputStream dis;
	private static OnlineWifi onlineWifi;

	static class OnlineException extends Exception {
		public OnlineException(String msg) {
			super(msg);
		}

		public OnlineException(Exception cause) {
			super(cause);
		}

		public OnlineException(String msg, Exception cause) {
			super(msg, cause);
		}
	}

	static void init() {
		onlineWifi = new OnlineWifi();
		dos = onlineWifi.getDataOutputStream();
		dis = onlineWifi.getDataInputStream();
	}

	static void close() {
		try {
			dos.close();
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dos = null;
		dis = null;
		onlineWifi.clean();
	}

	static void sendPlayData(Battle battle) throws OnlineException {
		try {
			dos.writeUTF(battle.player_name);
			dos.writeInt(Gmud.sPlayer.image_id * 6 + 74 + 4);
			int fighter_data[] = battle.fighter_data[battle.player_id];
			for (int i = 0, c = fighter_data.length; i < c; i++) {
				dos.writeInt(fighter_data[i]);
			}
			dos.flush();
			onlineWifi.send(null, 0, 0);
		} catch (IOException e) {
			e.printStackTrace();
			throw new OnlineException(e);
		}
	}

	static void pk() {

		// 检查联机环境
		// ...

		// 选择主从方式
		int sel = UI.OnlinePkMenu();
		if (sel == -1)
			return;

		cn.fmsoft.lnx.gmud.simple.OnlineWifi onlineWifi = new OnlineWifi();
		if (onlineWifi.start(sel == 0) == 0) {
			Battle battle = new Battle(-1, -1, 1);
			Battle.sBattle = battle;

			battle.CopyPlayerData();
			try {
				DataOutputStream dos = onlineWifi.getDataOutputStream();
				DataInputStream dis = onlineWifi.getDataInputStream();

				dos.writeUTF(battle.player_name);
				dos.writeInt(Gmud.sPlayer.image_id * 6 + 74 + 2);
				int fighter_data[] = battle.fighter_data[battle.player_id];
				for (int i = 0, c = fighter_data.length; i < c; i++) {
					dos.writeInt(fighter_data[i]);
				}
				dos.flush();
				onlineWifi.send(null, 0, 0);

				battle.NPC_name = dis.readUTF();
				battle.NPC_image_id = dis.readInt();
				fighter_data = battle.fighter_data[1 - battle.player_id];
				for (int i = 0, c = fighter_data.length; i < c; i++) {
					fighter_data[i] = dis.readInt();
				}

				// DataOutputStream dos = new DataOutputStream(null);
				// dos.writeByte(val)
				// onlineWifi.send(, 0, 1);
				Battle.sBattle.BattleMain();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}