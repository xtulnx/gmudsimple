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
	static String menu_title[] =new String[] {
		"普通攻击", "绝招攻击", "使用内力", "使用物品", "调整招式", "逃跑"
	};
//	vector<wstring> desc_words;
	static ArrayList<String> desc_words;

	static int weapon_id[] = new int[2];

//	extern void Gmud.GmudDelay(unsigned int);

	static void DrawFPMenu(int i)
	{
		Video.VideoClearRect(60, 50, 39, 28);
		Video.VideoDrawRectangle(60, 50, 39, 28);
		for (int i3 = 0; i3 < 2; i3++)
		{
			Video.VideoDrawStringSingleLine(UI.fp_menu_words[i3 + 1], 72, 51 + 13 * i3);
			if (i3 == i)
				UI.DrawCursor(60 + 4, 52 + 12 * i3);
		}
	}

	static void DrawPlusFp()
	{
		if (Battle.sBattle.fighter_data[Battle.sBattle.active_id][36] == 255)
		{
//			wstring str("你必须选择你要用的内功心法.");
			String str = "你必须选择你要用的内功心法.";
			UI.DrawStringFromY(str, 660);
			Video.VideoUpdate();
			Gmud.GmudDelay(1500);
			return;
		}
		int i = Gmud.sPlayer.GetPlusFPMax();  //加力上限
		int l;
		int i1 = l = Battle.sBattle.fighter_data[Battle.sBattle.active_id][0];
		boolean flag = false;
		int k1 = 80 - 70 - 4;
		int l1;
		l1 = (l1 = (l1 = 13) + (16 + 2)) + 12 * 5;
		int i2;
		if ((i2 = 80 - l1) > 0)
			i2 /= 2;
		else
			i2 = 0;
		i2 += 13 + 16 + 16 + 16;
		UI.DrawNumberBox(l, i, k1, i2);
		Video.VideoUpdate();
		Input.ClearKeyStatus();
		Gmud.GmudDelay(100);
		while (true)
		{
			if ((Input.inputstatus & Input.kKeyUp)!=0)  //up
			{
				if (l > 0)
					l--;
				UI.DrawNumberBox(l, i, k1, i2);
				Video.VideoUpdate();
				Gmud.GmudDelay(50);
			} else
			if ((Input.inputstatus & Input.kKeyDown)!=0)  //down
			{
				if (l < i)
					l++;
				UI.DrawNumberBox(l, i, k1, i2);
				Video.VideoUpdate();
				Gmud.GmudDelay(50);
			} else
			{
				if ((Input.inputstatus & Input.kKeyEnt)!=0)  //enter
				{
					Battle.sBattle.fighter_data[Battle.sBattle.active_id][0] = l;
					Battle.sBattle.a(Battle.sBattle.active_id, 0, l);
					if (l == i)
					{
//						wstring str("你目前的加力上限为");
//						wchar_t num[6];
//						str += _itow(i, num, 10);
						String str = "你目前的加力上限为"+i;
						UI.DrawStringFromY(str, 660);
						Video.VideoUpdate();
						Input.ClearKeyStatus();
						Gmud.GmudDelay(1500);
						return;
					} else
					{
						Input.ClearKeyStatus();
						Gmud.GmudDelay(50);
						return;
					}
				}
				if ((Input.inputstatus & Input.kKeyExit)!=0)  //Esc
					if (i == i1)
					{
//						wstring str("你目前的加力上限为");
//						wchar_t num[6];
//						str += _itow(i, num, 10);
						String str = "你目前的加力上限为" + i;
						UI.DrawStringFromY(str, 660);
						Video.VideoUpdate();
						Input.ClearKeyStatus();
						Gmud.GmudDelay(1500);
						return;
					} else
					{
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

	static void Main()
	{
		DrawMain();
		Video.VideoUpdate();
		Gmud.GmudDelay(100);
		while (true)
		{
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyLeft)!=0)  //left
			{
				if (menu_id == 0)
					menu_id = 5;
				else
					menu_id--;
				DrawMain();
				Video.VideoUpdate();
				Gmud.GmudDelay(50);
			} else
			if ((Input.inputstatus & Input.kKeyRight)!=0)    //right
			{
				if (menu_id == 5)
					menu_id = 0;
				else
					menu_id++;
				DrawMain();
				Video.VideoUpdate();
				Gmud.GmudDelay(50);
			} else
			if ((Input.inputstatus & Input.kKeyExit)!=0)  //esc
			{
				menu_id = 0;
				DrawMain();
				Video.VideoUpdate();
				Gmud.GmudDelay(50);
			}
			else
			if ((Input.inputstatus & Input.kKeyEnt)!=0)   //enter
			{
				if (menu_id == 0)  //"普通攻击",
				{
					Battle.sBattle.PhyAttack(false);
					Input.ClearKeyStatus();
					return;
				}
				if (menu_id == 1)  // "绝招攻击"
				{
					int i1 = MagicMenu();
					DrawMain();
					Video.VideoUpdate();
					Input.ClearKeyStatus();
					Gmud.GmudDelay(80);
					if (i1 > 0)
						return;
				} else
				if (menu_id == 2)  //"使用内力"
				{
					UseFPMenu();
					DrawMain();
					Video.VideoUpdate();
					Input.ClearKeyStatus();
					Gmud.GmudDelay(50);
				} else
				if (menu_id == 3)  //"使用物品"
				{
					UI.PlayerItem();
					DrawMain();
					Video.VideoUpdate();
					Gmud.GmudDelay(80);
				} else
				if (menu_id == 4)  //"调整招式"
				{
					UI.PlayerSkill();
					DrawMain();
					Video.VideoUpdate();
					Gmud.GmudDelay(80);
				} else
				if (menu_id == 5)  //"逃跑"
				{
					if (Battle.sBattle.CalcActOrder() == 0)
					{
						Battle.sBattle.b(2, 36, 1);
						Input.ClearKeyStatus();
						return;
					}
					Battle.sBattle.d_int_static = 1;
					//Battle.sBattle.b_int_static = -1;
					Input.ClearKeyStatus();
					return;
				}
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(80);
		}
	}

	static void DrawHPRect(int i, int l, int i1, int j1, int k1, boolean flag)
	{
		int l1 = Gmud.WQX_ORG_WIDTH / 4;
		i *= 1000;
		l *= 1000;
		int i2 = (i1 *= 1000) / l1;
		if (i1 % l1 > 0)
			i2++;
		if (i2 <= 0)
			i2 = 1;
		int j2;
		if ((j2 = i / i2) > l1)
			j2 = l1;
		Video.VideoFillRectangle(j1, k1, j2, 5);
		if ((j2 = l / i2) > l1)
			j2 = l1;
		Video.VideoFillRectangle(j1, k1 + 7, j2, 2);
		if(flag)
		{
//			wchar_t num[8];
//			wstring str(_itow(i / 1000, num, 10));
//			str += "/";
//			str += _itow(l / 1000, num, 10);
			String str = String.format("%d/%d", i/1000,l/1000);
			Video.VideoDrawNumberData(str, j1 + l1 + 2, k1 + 2);
		}
	}

	static void DrawMain()
	{
		Video.VideoClear();
		int l;
		int i = l = 12;
		int i1 = Battle.sBattle.player_id==0?1:0;
		l = (l += 16 + 2) + 12 * 5;
		int j1;
		if ((j1 = 80 - l) > 0)
			j1 /= 2;
		else
			j1 = 0;
		int k1 = (160 - 8) / 2 + 30;
		Video.VideoDrawStringSingleLine(Battle.sBattle.player_name/*.c_str()*/, 4, j1);
		Video.VideoDrawStringSingleLine(Battle.sBattle.NPC_name/*.c_str()*/, k1, j1);
		j1 += i + 2;
		if (hit_id == Battle.sBattle.player_id)
			Video.VideoDrawImage(hit_img, 18, j1);    //draw hit
		else
			Video.VideoDrawImage(player_img, 18, j1);
		if (hit_id == i1)
			Video.VideoDrawImage(hit_img, k1 + 10, j1);   //draw hit
		else
			Video.VideoDrawImage(NPC_img, k1 + 10, j1);
		hit_id = -1;
		j1 += 19;
		Video.VideoDrawImage(hp_img, 4, j1);
		int l1 = 4 + 20;
		DrawHPRect(Battle.sBattle.fighter_data[Battle.sBattle.player_id][1], Battle.sBattle.fighter_data[Battle.sBattle.player_id][2], Battle.sBattle.fighter_data[Battle.sBattle.player_id][3], l1, j1 + 4, true);
		DrawHPRect(Battle.sBattle.fighter_data[i1][1], Battle.sBattle.fighter_data[i1][2], Battle.sBattle.fighter_data[i1][3], k1 + 2, j1 + 4, false);
		j1 += 16;
		Video.VideoDrawImage(fp_img, 4, j1);
		DrawHPRect(Battle.sBattle.fighter_data[Battle.sBattle.player_id][4], Battle.sBattle.fighter_data[Battle.sBattle.player_id][5], Battle.sBattle.fighter_data[Battle.sBattle.player_id][5], l1, j1 + 4, true);
		j1 += 16;
		if(Battle.sBattle.fighter_data[Battle.sBattle.player_id][42] != 255)
		{
			Video.VideoDrawImage(mp_img, 4, j1);
			DrawHPRect(Battle.sBattle.fighter_data[Battle.sBattle.player_id][6], Battle.sBattle.fighter_data[Battle.sBattle.player_id][7], Battle.sBattle.fighter_data[Battle.sBattle.player_id][7], l1, j1 + 4, true);
			j1 += 16;
		}
		//u.a(0);
		int i2 = 61;
		int j2 = 50;
		for (int k2 = 0; k2 < 6; k2++)
			if (k2 == menu_id)
			{
				Video.VideoFillRectangle(j2 + k2 * 8, i2, 7, 7);
				Video.VideoDrawStringSingleLine(menu_title[k2], j2 - 2, i2 + 7);
			} else
			{
				Video.VideoDrawRectangle(j2 + k2 * 8, i2, 6, 6);
			}
	}

//	extern wstring ReplaceStr(wstring*, LPCWSTR, LPCWSTR);

	static void PhyAttack(int i, int l)
	{
//		wstring s("");
//		wstring s1("");
		String s="";
		String s1="";
		int i1 = Battle.sBattle.active_id;
		int j1 = Battle.sBattle.player_id;
		int l1;
		if ((l1 = Battle.sBattle.a_int_array1d_static[3]) != -1)
			s += GmudData.hit_point_name[l1];
		switch (i)
		{
		default:
			break;

		case 0: //
			s1 = Skill.GetAttackDesc(l);
			if (i1 == j1)
			{
				s1 = util.ReplaceStr(s1, "N", "你");
				s1 = util.ReplaceStr(s1, "SB", Battle.sBattle.NPC_name/*.c_str()*/);
				s1 = util.ReplaceStr(s1, "SW", Items.item_names[weapon_id[i1]]);
			} else
			{
				s1 = util.ReplaceStr(s1, "N", Battle.sBattle.NPC_name/*.c_str()*/);
				s1 = util.ReplaceStr(s1, "SB", "你");
				s1 = util.ReplaceStr(s1, "SW", Items.item_names[weapon_id[i1]]);
			}
			s1 = util.ReplaceStr(s1, "SP", s/*.c_str()*/);
			break;
		case 1:
			{
				s1 = Magic.GetMagicDesc(l);
				int k2 = Battle.sBattle.a_int_array1d_static[13];
				if (i1 == j1)
				{
					s1 = util.ReplaceStr(s1, "SB", Battle.sBattle.NPC_name/*.c_str()*/);
					s1 = util.ReplaceStr(s1, "SW", Items.item_names[weapon_id[i1]]);
				} else
				{
					s1 = util.ReplaceStr(s1, "你", Battle.sBattle.NPC_name/*.c_str()*/);
					s1 = util.ReplaceStr(s1, "SB", "你");
					s1 = util.ReplaceStr(s1, "SW", Items.item_names[weapon_id[i1]]);
				}
				if (k2 != -1)
					s1 = util.ReplaceStr(s1, "~", Magic.magic_name[k2]);
				break;
			}
		case 2: //hit result
			{
				int j3;
				s1 = Skill.GetHitDesc(j3 = l & 0xff);
				int k3 = l / 256;
//				wstring s2("");
				String s2="";
				if (k3 > 36)
					s2 = Skill.GetHitDesc(k3);
				if (i1 == j1)
				{
					s1 = util.ReplaceStr(s1, "SB", Battle.sBattle.NPC_name/*.c_str()*/);
					if (j3 != 36)
					{
						s1 += "(";
						s1 += Battle.sBattle.NPC_name;
						s1 += s2/*.c_str()*/;
						s1 += ")";
					}
					break;
				}
				s1 = util.ReplaceStr(s1, "你", Battle.sBattle.NPC_name/*.c_str()*/);
				s1 = util.ReplaceStr(s1, "SB", "你");
				if (j3 != 36)
				{
					s1 += "(你";
					s1 += s2/*.c_str()*/;
					s1 += ")";
				}
				break;
			}
		case 3: //？？？
//			s1 = desc_words[l];
			s1 = desc_words.get(l);
			if (i1 == j1)
			{
				s1 = util.ReplaceStr(s1, "N", "你");
				s1 = util.ReplaceStr(s1, "SB", Battle.sBattle.NPC_name/*.c_str()*/);
			} else
			{
				s1 = util.ReplaceStr(s1, "N", Battle.sBattle.NPC_name/*.c_str()*/);
				s1 = util.ReplaceStr(s1, "SB", "你");
			}
			break;

		case 4: //伤害描述
			s1 = Skill.GetHitDesc(l);
			if (i1 == j1)
			{
				s1 = util.ReplaceStr(s1, "SB", Battle.sBattle.NPC_name/*.c_str()*/);
			} else
			{
				s1 = util.ReplaceStr(s1, "你", Battle.sBattle.NPC_name/*.c_str()*/);
				s1 = util.ReplaceStr(s1, "SB", "你");
			}
			break;
		}
		DrawMain();
		UI.DrawStringFromY(s1, 660);
		Video.VideoUpdate();
	}

	static void DrawMagicMenu(int i, int l, int i1)
	{
		int j1;
		if ((j1 = Gmud.WQX_ORG_WIDTH / 2 - 84 - 4) <= 0)
			j1 = 10;
		int k1;
		int l1;
		l1 = (l1 = (l1 = k1 = 12) + (16 + 2)) + 12 * 5;
		int i2;
		if ((i2 = Gmud.WQX_ORG_WIDTH /2 - l1) > 0)
			i2 /= 2;
		else
			i2 = 0;
		i2 += 12 + 16 + 16;
		int j2;
		int k2 = (j2 = 13) * 6 + 8;
		int l2 = k1 + 4;
		if (Battle.sBattle.a_int_array2d_static[i][l + 1] >= 0 && Battle.sBattle.a_int_array2d_static[i][l + 1] < 39)
			l2 += k1;
		Video.VideoClearRect(j1, i2, k2, l2);
		Video.VideoDrawRectangle(j1, i2, k2, l2);
		int j3;
		for (int i3 = 0; i3 < 2 && (j3 = Battle.sBattle.a_int_array2d_static[i][l + i3]) >= 0; i3++)
		{
			if (j3 > 38)
				return;
			Video.VideoDrawStringSingleLine(Magic.magic_name[j3], j1 + 18, i2 + 1 + i3 * (k1 + 1));
			if (i3 == i1)
				UI.DrawCursor(j1 + (j2 - 8) / 2, i2 + i3 * (k1 + 1) + (k1 - 9) / 2);
		}
	}

	static int MagicMenu()
	{
		int l = 0;
		int i1 = 0;
		int j1;
		Magic.Effect(j1 = Battle.sBattle.player_id);
		int k1;
		if ((k1 = Battle.sBattle.g(j1)) < 1)  //get Battle skill number
			return 0;
		DrawMagicMenu(Battle.sBattle.player_id, 0, 0);
		Video.VideoUpdate();
		Input.ClearKeyStatus();
		Gmud.GmudDelay(150);
		while(true)
		{
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyUp)!=0)  //UP
			{	//goto label1;
				if (i1 <= 0)
				{
					if (l > 0)
					{
						l--;
						Input.ClearKeyStatus();
						continue;
					}
					if ((l = k1 - 2) < 0)
						l = 0;
					if (k1 >= 2)
					{
						i1 = 1;
						Input.ClearKeyStatus();
						continue;
					}
				}
				i1 = 0;
				DrawMagicMenu(Battle.sBattle.player_id, l, i1);
				Video.VideoUpdate();
				Input.ClearKeyStatus();
				Gmud.GmudDelay(50);
				continue;
			}
				if ((Input.inputstatus & Input.kKeyDown)!=0)   //down 
				{
					if (i1 < 1 && l + i1 < k1 - 1)
						i1 = 1;
					else
					if (l < k1 - 2)
					{
						l++;
					} else
					{
						l = 0;
						i1 = 0;
					}
					DrawMagicMenu(Battle.sBattle.player_id, l, i1);
					Video.VideoUpdate();
					Gmud.GmudDelay(50);
				} else
				{
					int l1;
					String s;
					if ((Input.inputstatus & Input.kKeyEnt)!=0)  //enter
						if ((s = Magic.UseMagic(l1 = Battle.sBattle.a_int_array2d_static[Battle.sBattle.player_id][l + i1])).length() == 0)
						{
							return 1;
						} else
						{
							UI.DrawStringFromY(s, 660);
							Video.VideoUpdate();
							Gmud.GmudDelay(1200);
							return 0;
						}
					if ((Input.inputstatus & Input.kKeyExit)!=0)  //Esc
						return 0;
				}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(80);
		}
	}
}
