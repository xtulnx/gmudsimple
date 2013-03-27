package cn.fmsoft.lnx.gmud.simple.core;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class uibattle {

	static int hit_id = -1;
	static int menu_id = 0;
	static Bitmap player_img;
	static Bitmap NPC_img;
	static Bitmap hp_img;
	static Bitmap fp_img;
	static Bitmap mp_img;
	static Bitmap hit_img;
	static final String menu_title[] =new String[] {
		"普通攻击", "绝招攻击", "使用内力", "使用物品", "调整招式", "逃跑"
	};
	
	
//	vector<wstring> desc_words;
	static ArrayList<String> desc_words;

	/** 双方的武器，用于显示招式时输出 */
	static int weapon_id[] = new int[2];

	/**
	 * 绘制内力菜单，只有  {加力} 和 {吸气}
	 * @param sel 当前选择
	 */
	static void DrawFPMenu(int sel) {
		final int x = 58;
		final int y = 48;
		final int w = Video.SMALL_LINE_H * 4;
		final int h = Video.SMALL_LINE_H * 2;
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);
		UI.DrawCursor(x + (Video.SMALL_LINE_H - UI.CURSOR_W) / 2, y
				+ Video.SMALL_LINE_H * sel + (Video.SMALL_LINE_H - UI.CURSOR_H)
				/ 2);
		for (int i = 0; i < 2; i++) {
			Video.VideoDrawStringSingleLine(UI.fp_menu_words[i + 1], x
					+ Video.SMALL_FONT_SIZE, y + Video.SMALL_LINE_H * i);
		}
	}

	/**
	 * 加力菜单
	 */
	static void DrawPlusFp() {
		
		final int id_active = Battle.sBattle.m_active_id;
		
		// 无内功，直接返回
		if (Battle.sBattle.fighter_data[id_active][36] == 255) {
			UI.DrawStringFromY(Res.STR_NO_INNER_KONGFU_STRING, 660);
			Video.VideoUpdate();
			Gmud.GmudDelay(1500);
			return;
		}
		
		// 加力上限
		final int max = Gmud.sPlayer.GetPlusFPMax();
		// 当前加力
		final int cur_base = Battle.sBattle.fighter_data[id_active][0];
		
		int cur = cur_base;
		
		int k1 = 80 - 70 - 4;
		int l1;
		l1 = (l1 = (l1 = 13) + (16 + 2)) + 12 * 5;
		int i2;
		if ((i2 = 80 - l1) > 0)
			i2 /= 2;
		else
			i2 = 0;
		i2 += 13 + 16 + 16 + 16;
		UI.DrawNumberBox(cur, max, k1, i2);
		Video.VideoUpdate();
		Input.ClearKeyStatus();
		Gmud.GmudDelay(100);
		while (true) {
			if ((Input.inputstatus & Input.kKeyUp) != 0) // up
			{
				if (cur > 0)
					cur--;
				UI.DrawNumberBox(cur, max, k1, i2);
				Video.VideoUpdate();
				Gmud.GmudDelay(50);
			} else if ((Input.inputstatus & Input.kKeyDown) != 0) // down
			{
				if (cur < max)
					cur++;
				UI.DrawNumberBox(cur, max, k1, i2);
				Video.VideoUpdate();
				Gmud.GmudDelay(50);
			} else {
				if ((Input.inputstatus & Input.kKeyEnt) != 0) // enter
				{
					Battle.sBattle.fighter_data[id_active][0] = cur;
					Battle.sBattle.stack_fighterdate_set(id_active, 0, cur);
					if (cur == max) {
						String str = String.format(Res.STR_FP_PLUS_LIMIT_STRING, max);
						UI.DrawStringFromY(str, 660);
						Video.VideoUpdate();
						Input.ClearKeyStatus();
						Gmud.GmudDelay(1500);
						return;
					} else {
						Input.ClearKeyStatus();
						Gmud.GmudDelay(50);
						return;
					}
				}
				if ((Input.inputstatus & Input.kKeyExit) != 0) // Esc
					if (max == cur_base) {
						String str = String.format(Res.STR_FP_PLUS_LIMIT_STRING, max);
						UI.DrawStringFromY(str, 660);
						Video.VideoUpdate();
						Input.ClearKeyStatus();
						Gmud.GmudDelay(1500);
						return;
					} else {
						Gmud.GmudDelay(50);
						return;
					}
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(80);
		}
	}

	static void UseFPMenu()
	{
		int l = 0;
		DrawMain();
		DrawFPMenu(0);
		Video.VideoUpdate();
		Input.ClearKeyStatus();
		Gmud.GmudDelay(100);
		while (true)
		{
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyUp)!=0)  //up key
			{
				if (l > 0)
					l--;
				else
					l = 1;
				DrawFPMenu(l);
				Video.VideoUpdate();
				Gmud.GmudDelay(50);
			} else
			if ((Input.inputstatus & Input.kKeyDown)!=0)   //down
			{
				if (l < 1)
					l++;
				else
					l = 0;
				DrawFPMenu(l);
				Video.VideoUpdate();
				Gmud.GmudDelay(50);
			} else
			if ((Input.inputstatus & Input.kKeyEnt)!=0)  //enter
			{
				if (l == 0)
				{
					DrawPlusFp();
					DrawMain();
					DrawFPMenu(l);
					Video.VideoUpdate();
					Gmud.GmudDelay(50);
				} else
				if (l == 1)
				{
//					wstring str(Battle.sBattle.Breath());
					String str = Battle.sBattle.Breath();
					UI.DrawStringFromY(str, 660);
					Video.VideoUpdate();
					Gmud.GmudDelay(1500);
					DrawMain();
					DrawFPMenu(l);
					Video.VideoUpdate();
					Gmud.GmudDelay(50);
				}
			} else
			if ((Input.inputstatus & Input.kKeyExit)!=0)  //esc
			{
				Input.ClearKeyStatus();
				Gmud.GmudDelay(50);
				return;
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(80);
		}
	}

	/** 战斗菜单 */
	static void Main() {
		boolean update = true;
		Input.ClearKeyStatus();
		while (true) {
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyLeft) != 0) {
				if (menu_id > 0)
					menu_id--;
				else 
					menu_id = 5;
				update = true;
			} else if ((Input.inputstatus & Input.kKeyRight) != 0) {
				if (menu_id < 5)
					menu_id++;
				else
					menu_id = 0;
				update = true;
			} else if ((Input.inputstatus & Input.kKeyExit) != 0) {
				menu_id = 0;
				update = true;
			} else if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				if (menu_id == 0) { // "普通攻击",
					Battle.sBattle.PhyAttack(false);
					break;
				} else if (menu_id == 1) { // "绝招攻击"
					int i1 = MagicMenu();
					update = true;
					if (i1 > 0)
						break;
				} else if (menu_id == 2) { // "使用内力"
					UseFPMenu();
					update = true;
				} else if (menu_id == 3) { // "使用物品"
					UI.PlayerItem();
					update = true;
				} else if (menu_id == 4) { // "调整招式"
					UI.PlayerSkill();
					update = true;
				} else if (menu_id == 5) { // "逃跑"
					if (Battle.sBattle.CalcAct()) {
						Battle.sBattle.bEscape = true;
						// Battle.sBattle.b_int_static = -1;
					} else {
						Battle.sBattle.b(2, 36, 1);
					}
					break;
				}
			}
			if (update) {
				update = false;
				DrawMain();
				Video.VideoUpdate();
			}
			Gmud.GmudWaitAnyKey();
		}
		Input.ClearKeyStatus();
	}

	/**
	 * 绘制血条
	 * @param cur 当前血量
	 * @param max 最大血量
	 * @param full 满血
	 * @param x 坐标
	 * @param y 
	 * @param show_percent 是否输出数字，如  99/100
	 */
	static void DrawHPRect(int cur, int max, int full, int x, int y,
			boolean show_percent) {
		final int length = 39;
		int len;
		len = cur * length/ full;
		if (len > length)
			len = length;
		Video.VideoFillRectangle(x, y, len, 3);

		len = max * length/ full;
		if (len > length)
			len = length;
		Video.VideoDrawLine(x, y + 4, x + len, y + 4);
		
		if (show_percent) {
			String str = String.format("%d/%d", cur, max);
			Video.VideoDrawNumberData(str, x + length + 1, y);
		}
	}

	static void DrawMain() {
		Video.VideoClear();

		final int id_player = Battle.sBattle.m_player_id;
		final int id_rival = (id_player == 0) ? 1 : 0;
		final int x = 3;
		final int x_npc = x + 100;
		int y = 0;

		// draw name
		Video.VideoDrawStringSingleLine(Battle.sBattle.m_player_name, x + 5, y);
		Video.VideoDrawStringSingleLine(Battle.sBattle.m_npc_name, x_npc, y);
		y += Video.SMALL_LINE_H + 2;

		// draw image
		Video.VideoDrawImage(hit_id == id_player ? hit_img : player_img, x + 6,
				y);
		Video.VideoDrawImage(hit_id == id_rival ? hit_img : NPC_img, x_npc + 3,
				y);
		hit_id = -1;
		y += 18;

		final int[] data_player = Battle.sBattle.fighter_data[id_player];
		final int[] data_rival = Battle.sBattle.fighter_data[id_rival];
		final int x_r = x + 12;

		// draw HP y:33
		Video.VideoDrawImage(hp_img, x, y);
		DrawHPRect(data_player[1], data_player[2], data_player[3], x_r, y + 2,
				true);
		DrawHPRect(data_rival[1], data_rival[2], data_rival[3], x_npc + 2,
				y + 2, false);
		y += 9;

		// draw FP
		Video.VideoDrawImage(fp_img, x, y);
		DrawHPRect(data_player[4], data_player[5], data_player[5], x_r, y + 2,
				true);
		DrawHPRect(data_rival[4], data_rival[5], data_rival[5], x_npc + 2,
				y + 2, false);
		y += 9;

		// draw MP
		if (data_player[42] != 255) {
			Video.VideoDrawImage(mp_img, x, y);
			DrawHPRect(data_player[6], data_player[7], data_player[7], x_r,
					y + 2, true);
			y += 9;
		}

		// u.a(0);

		final int t_y = y + 1;
		final int t_x = x_r + 36;
		for (int i = 0; i < 6; i++) {
			Video.VideoDrawRectangle(t_x + i * 8, t_y, 6, 6);
			if (i == menu_id) {
				Video.VideoFillRectangle(t_x + i * 8, t_y, 6, 6);
				Video.VideoDrawStringSingleLine(menu_title[i], t_x - 4, t_y + 8);
			}
		}
	}
	
	/**
	 * 描述过招内容
	 * @param attack_type 出招类型 0:物理攻击  1:技能攻击 2:攻击结果 3:??自定义描述文本于desc_words中 4:伤害描述
	 * @param attack_desc 描述文本ID
	 */
	static void PhyAttack(int attack_type, int attack_desc)
	{
		String str_desc;
		int id_active = Battle.sBattle.m_active_id;
		int id_player = Battle.sBattle.m_player_id;
		switch (attack_type)
		{
		case 0: //物理攻击
			str_desc = Skill.GetAttackDesc(attack_desc);
			if (id_active == id_player)
			{
				str_desc = util.ReplaceStr(str_desc, "N", "你");
				str_desc = util.ReplaceStr(str_desc, "SB", Battle.sBattle.m_npc_name);
				str_desc = util.ReplaceStr(str_desc, "SW", Items.item_names[weapon_id[id_active]]);
			} else
			{
				str_desc = util.ReplaceStr(str_desc, "N", Battle.sBattle.m_npc_name);
				str_desc = util.ReplaceStr(str_desc, "SB", "你");
				str_desc = util.ReplaceStr(str_desc, "SW", Items.item_names[weapon_id[id_active]]);
			}
			
			// 攻击部位
			String s;
			int hit_point = Battle.sBattle.a_int_array1d_static[3];
			if (hit_point != -1)
				s = GmudData.hit_point_name[hit_point];
			else {
				s = "";
			}
			str_desc = util.ReplaceStr(str_desc, "SP", s/*.c_str()*/);
			break;
		case 1:
			{
				str_desc = Magic.GetMagicDesc(attack_desc);
				int k2 = Battle.sBattle.a_int_array1d_static[13];
				if (id_active == id_player)
				{
					str_desc = util.ReplaceStr(str_desc, "SB", Battle.sBattle.m_npc_name/*.c_str()*/);
					str_desc = util.ReplaceStr(str_desc, "SW", Items.item_names[weapon_id[id_active]]);
				} else
				{
					str_desc = util.ReplaceStr(str_desc, "你", Battle.sBattle.m_npc_name/*.c_str()*/);
					str_desc = util.ReplaceStr(str_desc, "SB", "你");
					str_desc = util.ReplaceStr(str_desc, "SW", Items.item_names[weapon_id[id_active]]);
				}
				if (k2 != -1)
					str_desc = util.ReplaceStr(str_desc, "~", Magic.MAGIC_NAME[k2]);
				break;
			}
		case 2: //hit result
			{
				int j3 = attack_desc & 0xff;
				str_desc = Skill.GetHitDesc(j3);
				int k3 = attack_desc / 256;
//				wstring s2("");
				String s2="";
				if (k3 > 36)
					s2 = Skill.GetHitDesc(k3);
				if (id_active == id_player)
				{
					str_desc = util.ReplaceStr(str_desc, "SB", Battle.sBattle.m_npc_name/*.c_str()*/);
					if (j3 != 36)
					{
						str_desc += "(";
						str_desc += Battle.sBattle.m_npc_name;
						str_desc += s2/*.c_str()*/;
						str_desc += ")";
					}
					break;
				}
				str_desc = util.ReplaceStr(str_desc, "你", Battle.sBattle.m_npc_name/*.c_str()*/);
				str_desc = util.ReplaceStr(str_desc, "SB", "你");
				if (j3 != 36)
				{
					str_desc += "(你";
					str_desc += s2/*.c_str()*/;
					str_desc += ")";
				}
				break;
			}
		case 3: //？？？
//			s1 = desc_words[l];
			// XXX: 不支持
			Gmud.exit();
			str_desc = desc_words.get(attack_desc);
			if (id_active == id_player)
			{
				str_desc = util.ReplaceStr(str_desc, "N", "你");
				str_desc = util.ReplaceStr(str_desc, "SB", Battle.sBattle.m_npc_name/*.c_str()*/);
			} else
			{
				str_desc = util.ReplaceStr(str_desc, "N", Battle.sBattle.m_npc_name/*.c_str()*/);
				str_desc = util.ReplaceStr(str_desc, "SB", "你");
			}
			break;

		case 4: //伤害描述
			str_desc = Skill.GetHitDesc(attack_desc);
			if (id_active == id_player)
			{
				str_desc = util.ReplaceStr(str_desc, "SB", Battle.sBattle.m_npc_name/*.c_str()*/);
			} else
			{
				str_desc = util.ReplaceStr(str_desc, "你", Battle.sBattle.m_npc_name/*.c_str()*/);
				str_desc = util.ReplaceStr(str_desc, "SB", "你");
			}
			break;
			
		default:
			str_desc = "";
			break;
		}
		DrawMain();
		UI.DrawStringFromY(str_desc, 660);
		Video.VideoUpdate();
	}

	/**
	 * 绘制　大招　列表，最多２行
	 * 
	 * @param id_active
	 * @param top
	 * @param sel
	 */
	static void DrawMagicMenu(int id_active, int top, int sel) {
		final int[] data = Battle.sBattle.a_int_array2d_static[id_active];
		final int lineH = Video.SMALL_LINE_H;
		final int w = lineH * 6 + 2;
		final int h;
		if (data[top + 1] >= 0 && data[top + 1] < 39) {
			h = Video.SMALL_LINE_H * 2;
		} else {
			h = Video.SMALL_LINE_H;
		}
		int x = 36;
		int y = lineH * 3;
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);
		UI.DrawCursor(x + (lineH - UI.CURSOR_W) / 2, y + sel * lineH
				+ (lineH - UI.CURSOR_H) / 2);
		x += lineH;
		for (int i = 0; i < 2; i++) {
			final int magic_id = data[top + i];
			if (magic_id < 0 || magic_id > 38)
				break;
			Video.VideoDrawStringSingleLine(Magic.MAGIC_NAME[magic_id], x, y);
			y += lineH;
		}
	}

	/**
	 * 大招菜单
	 * 
	 * @return 0没有使用任何招式 1已成功使用招式
	 */
	static int MagicMenu() {
		final int id_active = Battle.sBattle.m_player_id;
		Magic.Effect(id_active);
		final int count = Battle.sBattle.CountMagicEffect(id_active);
		if (count < 1)
			return 0;
		int top = 0;
		int sel = 0;
		boolean update = true;
		Input.ClearKeyStatus();
		while (true) {
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyUp) != 0) {
				if (sel > 0)
					sel--;
				else if (top > 0)
					top--;
				else if (count > 2) {
					top = count - 2;
					sel = 1;
				} else {
					top = 0;
					sel = count - 1;
				}
				update = true;
			} else if ((Input.inputstatus & Input.kKeyDown) != 0) {
				if (sel + top + 1 >= count) {
					sel = 0;
					top = 0;
				} else if (sel < 1)
					sel++;
				else
					top++;
				update = true;
			} else if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				int magic_id = Battle.sBattle.a_int_array2d_static[id_active][top
						+ sel];
				String s = Magic.UseMagic(magic_id);
				if (s.length() == 0) {
					return 1;
				} else {
					UI.DrawStringFromY(s, 660);
					Video.VideoUpdate();
					Gmud.GmudDelay(1200);
					break;
				}
			} else if ((Input.inputstatus & Input.kKeyExit) != 0) {
				break;
			}

			if (update) {
				update = false;
				DrawMagicMenu(id_active, top, sel);
				Video.VideoUpdate();
			}
			Gmud.GmudWaitNewKey(Input.kKeyDown | Input.kKeyUp | Input.kKeyEnt
					| Input.kKeyExit);
		}
		return 0;
	}
}
