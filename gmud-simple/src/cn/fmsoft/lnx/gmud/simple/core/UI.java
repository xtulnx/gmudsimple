package cn.fmsoft.lnx.gmud.simple.core;

import java.util.ArrayList;

public class UI {

	/**
	 * 是否自动[确认]，用于学习技能
	 */
	static private int m_auto_confirm;

	// [8]
	static final String boss_map_name[] = new String[] { "青龙坛", "地罡坛", "朱雀坛",
			"山岚坛", "玄武坛", "紫煞坛", "天微坛", "白虎坛" };

	static final int boss_map_id[] = new int[] { 23, 73, 59, 79, 31, 54, 64, 44 };

	static final String old_award_words = "你被奖励了：20点实战经验 10点潜能 50金钱";

	/** 主菜单文本 "查看", "物品", "技能", "功能" */
	static final String main_menu_items[] = new String[] { "查看", "物品", "技能",
			"功能" };

	static final String STR_BATTLE_WIN = "战斗胜利，大获全胜!\n战斗获得\n金钱：%d\n物品：%s";

	// [199]
	static final int dialog_point[] = new int[] { 0, 59, 112, 165, 220, 292,
			322, 355, 394, 421, 475, 551, 580, 631, 688, 742, 799, 829, 884,
			926, 977, 1028, 1139, 1202, 1253, 1292, 1337, 1382, 1463, 1505,
			1538, 1595, 1658, 1712, 1781, 1829, 1907, 1952, 2030, 2093, 2129,
			2201, 2256, 2331, 2379, 2442, 2520, 2595, 2676, 2754, 2847, 2901,
			2949, 3003, 3063, 3135, 3180, 3267, 3336, 3429, 3471, 3536, 3605,
			3698, 3731, 3815, 3896, 3941, 4004, 4022, 4067, 4094, 4148, 4178,
			4232, 4265, 4322, 4370, 4412, 4515, 4639, 4767, 4833, 4923, 5113,
			5227, 5272, 5356, 5455, 5488, 5542, 5636, 5714, 5801, 5892, 6003,
			6093, 6129, 6171, 6237, 6294, 6396, 6456, 6515, 6557, 6671, 6773,
			6827, 6854, 6950, 6974, 7061, 7148, 7199, 7325, 7388, 7502, 7559,
			7655, 7730, 7793, 7883, 7952, 8012, 8033, 8054, 8075, 8096, 8117,
			8138, 8159, 8180, 8201, 8222, 8243, 8264, 8300, 8354, 8459, 8534,
			8657, 8687, 8756, 8819, 8951, 9029, 9107, 9167, 9245, 9323, 9383,
			9479, 9545, 9605, 9668, 9716, 9779, 9845, 9908, 9980, 10034, 10103,
			10142, 10217, 10331, 10454, 10511, 10574, 10679, 10778, 10838,
			10931, 10976, 11042, 11117, 11171, 11240, 11297, 11369, 11438,
			11504, 11603, 11651, 11771, 11822, 11885, 11948, 12011, 12074,
			12149, 12263, 12377, 12476, 12524, 12620, 12701, 12748, 12832,
			12898 };

	static final String readDialogText(int id) {
		return Res.readtext(Res.TYPE_DIALOG, dialog_point[id],
				dialog_point[1 + id]);
	}

	static void DrawDead() {
		for (int i = 112; i < 123; i++) {
			UI.ShowDialog2(i);
			Video.VideoUpdate();
			Gmud.GmudDelay(1500);
		}
	}

	/**
	 * 显示战斗胜利的界面
	 * 
	 * @param money
	 *            获得的金钱
	 * @param items
	 *            获得的物品ID表
	 */
	static void BattleWin(int money, short[] items) {
		StringBuilder ib = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			final int item_id = items[i];
			if (item_id == 0)
				break;
			ib.append(Items.item_names[item_id]).append(" ");
		}

		String str = String.format(STR_BATTLE_WIN, money, ib.toString());
		DrawDialog(str);
		Video.VideoUpdate();
		Input.ClearKeyStatus();
		Gmud.GmudDelay(200);
		Gmud.GmudWaitKey(Input.kKeyExit);
	}

	/** 在屏幕下方绘制单行文本，如果有多行，则分多次输出 */
	static void DrawMapTip(String s) {
		final int lineH = Video.SMALL_LINE_H; // 行高
		final int y = Gmud.WQX_ORG_HEIGHT - lineH - 2;
		ArrayList<String> as = Video.SplitString(s, Gmud.WQX_ORG_WIDTH - 3);
		for (int i = 0, c = as.size(); i < c; i++) {
			Video.VideoClearRect(0, y, Gmud.WQX_ORG_WIDTH, lineH + 1);
			Video.VideoDrawRectangle(1, y, Gmud.WQX_ORG_WIDTH - 2, lineH + 1);
			Video.VideoDrawStringSingleLine(as.get(i), 2, y + 1);
			Video.VideoUpdate();
			Gmud.GmudWaitNewKey(Input.kKeyAny);
		}
	}

	/** 加载资源，并显示不超过5行的多行文本框 */
	static void ShowDialog2(final int id) {
		final String s = readDialogText(id);
		final int lineH = Video.SMALL_LINE_H;
		Video.VideoFillRectangle(0, 0, 160, 80, 0);
		ArrayList<String> as = Video.SplitString(s, Gmud.WQX_ORG_WIDTH - 16);
		for (int i = 0, c = as.size(); i < c && i < 5; i++)
			Video.VideoDrawStringSingleLine(as.get(i), 12, 10 + i * lineH, 2);
		Video.VideoUpdate();
		Input.ClearKeyStatus();
	}

	static void ShowDialog(int tip_id) {
		DrawDialog(readDialogText(tip_id));
	}

	/** 在顶端显示最多2行的文本，如果有更多行数，则分批输出 */
	static void DrawDialog(String s) {
		final int lineH = (Video.SMALL_LINE_H);
		final int height = lineH * 2; // 输出区域的高度，2行
		ArrayList<String> as = Video.SplitString(s, Gmud.WQX_ORG_WIDTH);
		for (int i = 0, c = as.size(); i < c;) {
			// 清空输出区域，绘制边框
			Video.VideoClearRect(1, 1, Gmud.WQX_ORG_WIDTH - 2, height);
			Video.VideoDrawRectangle(1, 1, Gmud.WQX_ORG_WIDTH - 2, height);
			Video.VideoDrawStringSingleLine(as.get(i), 1, 2);

			// 如果还有文本，则再输出一行
			if (++i < c) {
				Video.VideoDrawStringSingleLine(as.get(i), 1, lineH + 1);
				i++;
			}
			Video.VideoUpdate();
			if (i < c) {
				// 如果还有文本，就闪光标
				DrawFlashCursor(Gmud.WQX_ORG_WIDTH - 14, height - 10, 8);
			} else {
				// 等待任意键返回
				Gmud.GmudWaitAnyKey();
			}
		}
	}

	/**
	 * 绘制一个向下闪烁的光标
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @return
	 */
	static int DrawFlashCursor(int x, int y, int w) {
		final int h = w / 2 + 1;
		boolean blink = false;
		int count = 0;
		while (true) {
			if (count == 0) {
				if (!blink) {
					Video.VideoDrawArrow(x, y, w, h, 0 + 2 + 0);
				} else {
					Video.VideoDrawArrow(x, y, w, h, 0 + 2 + 4);
				}
				Video.VideoUpdate();
				count = 6;
				blink = !blink;
			} else {
				count--;
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(Gmud.DELAY_WAITKEY);
			Input.ProcessMsg();
			if (Input.inputstatus != 0) {
				break;
			}
		}
		return Input.inputstatus;
	}

	/**
	 * 绘制一个实心向右的箭头，大小(7x11)
	 * 
	 * @param x
	 * @param y
	 */
	static void DrawCursor(int x, int y) {
		Video.VideoDrawArrow(x, y, 7, 11, 1 + 2);
	}

	/** 绘制主菜单 */
	static void DrawMainMenu(int menu_id) {
		final int lineH = Video.SMALL_LINE_H;
		int w = lineH * 2; // 每个菜单项占 2个字 的宽度
		int width = 160 - 16;
		int x = 8;
		if (width < 64 + w * 4) {
			// 如果宽度不够显示
			width = 160 - 2;
			x = 1;
		}
		int item_width = lineH + w; // 菜单项的宽度（含光标区域）
		int height = lineH + 2;
		Video.VideoClearRect(x, 0, width, height);
		Video.VideoDrawRectangle(x, 0, width, height);
		for (int i = 0, xx = x + 11; i < 4; i++) {
			Video.VideoDrawStringSingleLine(main_menu_items[i], xx, 1);
			xx += item_width;
		}
		int cx = x + 2 + menu_id * item_width;
		int cy = (height - 11) / 2;
		if (cy == 0)
			cy = 2;
		DrawCursor(cx, cy);
	}

	static void MainMenu() {
		int menu_id = 0;
		boolean update = true;
		Input.ClearKeyStatus();
		while (Input.Running) {
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyLeft) != 0) {
				if (menu_id > 0)
					menu_id--;
				else
					menu_id = 3;
				update = true;
			} else if ((Input.inputstatus & Input.kKeyRight) != 0) {
				if (menu_id < 3)
					menu_id++;
				else
					menu_id = 0;
				update = true;
			} else if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				if (menu_id == 0) {
					ViewPlayer();
				} else if (menu_id == 1) {
					PlayerItem();
				} else if (menu_id == 2) {
					PlayerSkill();
				} else if (menu_id == 3) {
					Input.ClearKeyStatus();
					if (SystemMenu() != 0) {
						update = true;
						break;
					}
				}
				Gmud.sMap.DrawMap(-1);
				update = true;
			} else if ((Input.inputstatus & Input.kKeyExit) != 0) {
				update = true;
				break;
			}

			if (update) {
				update = false;
				DrawMainMenu(menu_id);
				Video.VideoUpdate();
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(Gmud.DELAY_WAITKEY);
		}

		if (update) {
			Gmud.sMap.DrawMap(-1);
			Video.VideoUpdate();
			Gmud.GmudDelay(Gmud.DELAY_WAITKEY);
		}
	}

	static void Fly() {
		// 检查当前地图是否可飞
		int can_fly = -1;
		final int cur_map_id = Gmud.sMap.GetCurMapID();
		for (int i = 0; i < 19; i++)
			if (GmudData.flyable_map[i] == cur_map_id)
				can_fly = i;
		if (can_fly < 0)
			return;

		// 等级不够
		if (Gmud.sPlayer.GetFlySkillLevel() < 30)
			return;

		// 扣 fp 先
		if (Gmud.sPlayer.fp < 200) {
			UI.DrawDialog("你的内力不足，无法施展轻功。");
			return;
		}
		Gmud.sPlayer.fp -= 200;
		
		// 隐藏未激活的地图:铸剑谷　桃花园
		boolean open_choujiangu = Gmud.sPlayer.lasting_tasks[2] != 0;
		boolean open_taohuayuan = Gmud.sPlayer.lasting_tasks[6] != 0;
		String[] title = GmudData.map_name;
		final int max_count = GmudData.map_name.length;
		int count;
		if (!open_choujiangu || !open_taohuayuan) {
			title = new String[max_count];
			System.arraycopy(GmudData.map_name, 0, title, 0, max_count - 2);
			count = max_count - 2;
			if (open_choujiangu) {
				title[count++] = GmudData.map_name[max_count - 2];
			}
			if (open_taohuayuan) {
				title[count++] = GmudData.map_name[max_count - 1];
			}
		} else {
			count = max_count;
		}

		// 让玩家选择目标地图
		UIUtils.ShowMenu(title, count, 4, 4, 4, Video.SMALL_LINE_H * 4,
				MENUID_FLY);
	}

	static int FlyCallback(int index) {
		// 隐藏未激活的地图:铸剑谷　桃花园
		if (Gmud.sPlayer.lasting_tasks[2] == 0) {
			final int max_count = GmudData.fly_dest_map.length;
			if (index == max_count - 2) {
				index = max_count - 1;
			}
		}
		
		int map_id = GmudData.fly_dest_map[index]; // read map
		Gmud.sMap.LoadMap(map_id);
		// Gmud.sMap.m_stack_pointer = 0;
		Gmud.sMap.SetPlayerLocation(0, 4);
		Gmud.sMap.DrawMap(0);
		Input.ClearKeyStatus();
		return 1;
	}

	static int SystemMenu() {
		final int w = Video.SMALL_LINE_H * 3;
		int x = 8 + 3 * (16 + 40);
		if (x + w + 2 >= Gmud.WQX_ORG_WIDTH)
			x = Gmud.WQX_ORG_WIDTH - w - 2;
		int ret = UIUtils.ShowMenu(sys_menu_words, sys_menu_words.length, 5, x,
				5, w, MENUID_SYSTEM);
		if (ret == 1)
			return 1;
		return 0;
	}

	static int SystemCallback(int menu_id) {
		if (menu_id == 0) {
			FPMenu();
			return 1;
		}
		if (menu_id == 1) {
			MPMenu();
			return 1;
		}
		if (menu_id == 2) {
			PractMenu();
			return 1;
		}
		if (menu_id == 3) {
			SaveMenu();
			return 2;
		}
		if (menu_id == 4) {
			ExitMenu();
			return 2;
		}
		return 0;
	}

	static void ExitMenu() {
		Gmud.GmudDelay(100);
		Input.ClearKeyStatus();
		final int y = Gmud.WQX_ORG_WIDTH / 2 / 2 - 10;
		String str = "您确定要离开游戏吗?\n([输入]确认 [跳出]放弃)";
		int i2 = DialogBx(str, 10, y);
		while (true) {
			if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				if (task.temp_tasks_data[30] > 360) {
					Gmud.GmudDelay(100);
					Input.ClearKeyStatus();
					str = "您已经很久没有存档了,存档吗?\n([输入]确认 [跳出]放弃)";
					i2 = DialogBx(str, 10, y + 10);
					while (true) {
						Input.ProcessMsg();
						if ((i2 & Input.kKeyEnt) != 0) {
							Gmud.WriteSave();
							Gmud.exit();
						} else if ((i2 & Input.kKeyExit) != 0)
							Gmud.exit();
						i2 = Input.inputstatus;
						Input.ClearKeyStatus();
						Gmud.GmudDelay(100);
					}
				}
				Gmud.exit();
			} else if ((Input.inputstatus & Input.kKeyExit) != 0)
				break;
			i2 = Input.inputstatus;
			Input.ClearKeyStatus();
			Gmud.GmudDelay(100);
		}
	}

	static void SaveMenu() {
		if (task.temp_tasks_data[30] >= 100) {
			// save file
			String str = "存档成功!";
			if (Gmud.WriteSave())
				DrawTip(str);
			else {
				str = "存档失败!";
				DrawTip(str);
			}
		} else {
			String str = "请稍后再存";
			DrawTip(str);
		}
		Video.VideoUpdate();
		Gmud.GmudWaitNewKey(Input.kKeyExit);
	}

	static int DialogBx(String s, int x, int y) {
		final int lineH = Video.SMALL_LINE_H;
		final int width = Gmud.WQX_ORG_WIDTH - 8 - x;
		final ArrayList<String> as = Video.SplitString(s, width - 4);
		final int size = as.size();
		final int height = size * lineH + 4;
		Video.VideoClearRect(x, y, width, height);
		Video.VideoDrawRectangle(x, y, width, height);
		for (int i = 0, top = 0; i < size && top < height; i++, top += lineH) {
			Video.VideoDrawStringSingleLine(as.get(i), x + 1, y + 1 + top);
		}
		Video.VideoUpdate();
		Gmud.GmudWaitAnyKey();
		return Input.inputstatus;
	}

	static void DrawTip(String s1) {
		final int lineH = Video.SMALL_LINE_H;
		final int width = s1.length() * lineH;
		final int height = lineH + 4;
		int x = (Gmud.WQX_ORG_WIDTH - width) / 2;
		if (x < 0)
			x = 0;
		int y = (Gmud.WQX_ORG_WIDTH / 2 - height) / 2 - 10;
		if (y < 0)
			y = 0;
		Video.VideoClearRect(x, y, width, height);
		Video.VideoDrawRectangle(x, y, width, height);
		Video.VideoDrawStringSingleLine(s1, x + 2, y + 2);
	}

	static void DrawStringFromY(String s, int i1) {
		final int y = Gmud.WQX_ORG_HEIGHT / 2 + 1;
		Video.VideoClearRect(0, y, Gmud.WQX_ORG_WIDTH, Video.SMALL_LINE_H * 3);
		Video.VideoDrawString(s, 0, y);
	}

	/**
	 * 绘制数字加减对话框，用于 {加力} 等。
	 * 
	 * @param cur
	 * @param max
	 *            最大值，[0-999]
	 * @param x
	 * @param y
	 */
	static void DrawNumberBox(int cur, int max, int x, int y) {
		final int num_w = 27; // 数字区的宽度
		final int nw = 6; // 每个数字的宽度
		final int h = Video.SMALL_LINE_H + 4; // 总高度，假定小号文本高度固定为 13
		final int w = num_w + 4 + 10; // 总宽度
		if (cur > max)
			cur = max;
		if (cur < 0)
			cur = 0;

		// 绘制外边框
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);

		// 分隔线
		Video.VideoDrawLine(x + num_w + 3, y, x + num_w + 3, (y + h));
		Video.VideoDrawLine(x + num_w + 3, y + h / 2, (x + w), y + h / 2);

		// 绘制上下箭头
		final int arrow_w = w - 3 - num_w - 4;
		final int arrow_h = h / 2 - 3;
		Video.VideoDrawArrow(x + num_w + 5, (y + h / 2) - 2, arrow_w, -arrow_h,
				cur > 0 ? 2 : 0);
		Video.VideoDrawArrow(x + num_w + 5, (y + h / 2) + 2, arrow_w, arrow_h,
				cur < max ? 2 : 0);

		// 右对齐绘制数字
		x += 3 + 2 * (nw + 1);
		y += 2;
		while (cur >= 10) {
			Video.VideoDrawStringSingleLine(String.valueOf(cur % 10), x, y);
			cur /= 10;
			x -= nw + 1;
		}
		Video.VideoDrawStringSingleLine(String.valueOf(cur), x, y);
	}

	/**
	 * 绘制进度条
	 * 
	 * @param max
	 *            总值
	 * @param cur
	 *            当前进度值
	 * @param numerator
	 *            分子数值
	 * @param denominator
	 *            分母数值
	 */
	static void DrawProgressBox(int max, int cur, int numerator, int denominator) {
		max *= 1000;
		cur *= 1000;
		int x = 32;
		int h = 8;
		int bar_w = (Gmud.WQX_ORG_WIDTH * 2) / 5; // 进度条的宽度为屏幕的 2/5
		int i3 = 39;
		int width = bar_w + i3 + 12; // 总宽度=进度条+空隙+数字
		if (width > Gmud.WQX_ORG_WIDTH)
			width = Gmud.WQX_ORG_WIDTH;
		if (Gmud.WQX_ORG_WIDTH - width < 32)
			x = Gmud.WQX_ORG_WIDTH - width;
		int y = h / 4;

		// 先绘制空框
		Video.VideoClearRect(x, 0, width, h + 2);
		Video.VideoDrawRectangle(x, y, bar_w, h);
		int pro_w; // 进度值
		if (cur >= max && max > 0) {
			pro_w = bar_w;
		} else if (max <= bar_w) {
			if (max <= 0)
				max = 1;
			pro_w = (bar_w / max) * cur;
		} else {
			int j4 = max / bar_w;
			if (max % bar_w > 0)
				j4++;
			if (j4 <= 0)
				j4 = 1;
			pro_w = cur / j4;
		}
		if (pro_w > bar_w)
			pro_w = bar_w;
		Video.VideoFillRectangle(x, y, pro_w, h);

		String num = String.format("%d/%d", numerator, denominator);
		Video.VideoDrawNumberData(num, x + bar_w + 3, h / 2);
	}

	/**
	 * 绘制 <b>GmudTemp.temp_array_20_2</b> 从start起num 个技能列表
	 * 
	 * @param x
	 *            x坐标起点
	 * @param y
	 *            y坐标起点
	 * @param size
	 *            技能数量
	 * @param start
	 *            技能起点
	 * @param selPos
	 *            当前已选中的序号[0,2]
	 * @param drawLevel
	 *            是否显示技能等级（如 ×180）, 自练技能时不显示 x
	 */
	static void DrawSkillList(int x, int y, int size, int start, int selPos,
			boolean drawLevel) {
		if (size <= 0)
			return;

		final int r = 6; // 圆圈
		final int lineH = Video.SMALL_LINE_H;
		final int w, h;
		if (size >= 3)
			h = lineH * 3 + 6;
		else
			h = size * lineH + 6;
		if (!drawLevel)
			w = lineH * 5 + 16 + 4;
		else
			w = lineH * 7 + 16 + 10;
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);

		final int gap = (lineH - r - r) / 2;
		x += 4 + r;
		y += 2;
		final int data[][] = GmudTemp.temp_array_20_2;
		for (int i = 0; i < size && i < 3; i++, y += lineH + 1) {
			int skill_id = data[start + i][0];
			int skill_level = data[start + i][1];
			if (skill_id == 255)
				continue;
			Video.VideoDrawArc(x, y + gap + r, r);
			if (i == selPos)
				Video.VideoFillArc(x, y + gap + r, 4);
			String s1 = Skill.skill_name[skill_id];
			if (drawLevel) {
				s1 += "×";
				s1 += String.valueOf(skill_level);
			}
			Video.VideoDrawStringSingleLine(s1, x + 10, y);
		}
	}

	// *********** UI-Player ************//
	static final String player_menu_words[] = new String[] { "状态", "描述", "属性",
			"婚姻" };

	static final String item_menu_words[] = new String[] { "食物", "药物", "武器",
			"装备", "其它", "接收" };

	static final String useitem_menu_words[] = new String[] { "使用", "丢弃", "发送" };

	static final String skill_menu_words[] = new String[] { "拳脚", "兵刃", "轻功",
			"内功", "招架", "知识", "法术" };

	static final String sys_menu_words[] = new String[] { "内力", "法力", "练功",
			"存档", "结束" };

	static final String fp_menu_words[] = new String[] { "打坐", "加力", "吸气", "疗伤" };

	static final String mp_menu_words[] = new String[] { "冥思", "法点" };

	static final String player_attrib_menu_words[] = new String[] { "膂力", "敏捷",
			"根骨", "悟性" };

	static void DrawViewPlayer(int menu_id) {
		int width = 160 - 8;
		int height = 80 - 4;
		Video.VideoClearRect(4, 2, width, height);
		Video.VideoDrawRectangle(4, 2, width, height);
		int x = 25, y = 5;
		Video.VideoDrawStringSingleLine(player_menu_words[menu_id], x, y);

		// 绘制方块
		x += Video.SMALL_LINE_H * 5 / 2; // 两个半字的宽度
		y += 1 + (Video.SMALL_FONT_SIZE - 8) / 2;
		for (int j2 = 0; j2 < 4; j2++) {
			Video.VideoDrawRectangle(x, y, 8, 8);
			if (j2 == menu_id) {
				Video.VideoFillRectangle(x, y, 8, 8, 0);
			}
			x += 14;
		}

		switch (menu_id) {
		case 0:
			DrawPlayerStatus();
			break;
		case 1:
			DrawPlayerDesc();
			break;
		case 2:
			DrawPlayerAttrib();
			break;
		case 3:
			DrawPlayerMerry();
			break;
		}
	}

	static void ViewPlayer() {
		/* 按条件打开玩家隐藏属性 */
		if (Gmud.sMap.GetCurMapID() == 1
				&& Gmud.sMap.GetCurOrientation() == Map.CharOrientation.DOWN) {
			Battle.sBattle = new Battle(-1, 0, 1);
			Battle.sBattle.CopyData();
		}

		int menu_id = 0;
		boolean update = true;
		Input.ClearKeyStatus();
		while (Input.Running) {
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyLeft) != 0) {
				if (menu_id > 0)
					menu_id--;
				else
					menu_id = 3;
				update = true;
			} else if ((Input.inputstatus & Input.kKeyRight) != 0) {
				if (menu_id < 3)
					menu_id++;
				else
					menu_id = 0;
				update = true;
			} else if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				Gmud.GmudDelay(100);
				break;
			} else if ((Input.inputstatus & Input.kKeyExit) != 0) {
				Gmud.GmudDelay(100);
				break;
			}
			if (update) {
				update = false;
				DrawViewPlayer(menu_id);
				Video.VideoUpdate();
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(80);
		}

		if (Battle.sBattle != null) {
			Battle.sBattle = null;
		}
	}

	static void DrawPlayerStatus() {
		String as[] = new String[6];
		int i = 0;
		as[i] = String.format("食物:%d/%d", Gmud.sPlayer.food,
				Gmud.sPlayer.GetFoodMax());

		if (Gmud.sPlayer.mp <= 0) {
			i++;
			as[i] = "";
		} else {
			as[i] += "  ";
		}
		as[i] += String.format("饮水:%d/%d", Gmud.sPlayer.water,
				Gmud.sPlayer.GetWaterMax());
		i++;

		int hp = (Gmud.sPlayer.hp_max * 1000)
				/ ((Gmud.sPlayer.hp_full * 1000) / 100);
		if (hp >= 100 && Gmud.sPlayer.hp_max < Gmud.sPlayer.hp_full)
			hp = 99;
		as[i] = String.format("生命:%d/%d(%d%%)", Gmud.sPlayer.hp,
				Gmud.sPlayer.hp_max, hp);
		i++;

		as[i] = String.format("内力:%d/%d(+%d)", Gmud.sPlayer.fp,
				Gmud.sPlayer.fp_level, Gmud.sPlayer.fp_plus);
		i++;

		if (0 >= Gmud.sPlayer.mp) {
			as[i] = String.format("经验:%d 潜能:%d", Gmud.sPlayer.exp,
					Gmud.sPlayer.potential);
			i++;

			as[i] = "";
		} else {
			as[i] = String.format("法力:%d/%d(+%d)", Gmud.sPlayer.mp,
					Gmud.sPlayer.mp_level, Gmud.sPlayer.mp_plus);
			i++;

			as[i] = String.format("经验:%d 潜能:%d ", Gmud.sPlayer.exp,
					Gmud.sPlayer.potential);
			i++;

			as[i] = "";
		}

		for (int k1 = 0; k1 < 6; k1++)
			Video.VideoDrawStringSingleLine(as[k1], 8, 17 + k1 * 12);
	}

	static void DrawPlayerDesc() {
		String as[] = new String[5];
		as[0] = String.format("[%s]%s",
				GmudData.class_name[Gmud.sPlayer.class_id],
				Gmud.sPlayer.player_name);

		int sex = Gmud.sPlayer.sex;
		if (sex != 1)
			sex = 0;

		as[1] = String.format("你是一位%d岁的%s", 14 + Gmud.sPlayer.GetAge(),
				sex != 0 ? "女性" : "男性");

		int facelevel = Gmud.sPlayer.GetFaceLevel();
		as[2] = (facelevel < 0) ? "你一脸稚气" : String.format("你长得%s,%s",
				GmudData.face_level_name[facelevel][sex],
				GmudData.face_level_name[facelevel + 1][sex]);

		as[3] = String.format("武艺看起来%s",
				GmudData.level_name[Gmud.sPlayer.GetPlayerLevel() / 5]);
		as[4] = String.format("出手似乎%s",
				GmudData.attack_level_name[Gmud.sPlayer.GetAttackLevel()]);

		for (int i = 0; i < 5; i++)
			Video.VideoDrawStringSingleLine(as[i], 8, 17 + i * 12);
	}

	static void DrawPlayerAttrib() {
		String as[] = new String[5];
		as[0] = String.format("金钱:%d", Gmud.sPlayer.money);
		as[1] = String.format("膂力  [%d/%d]", Gmud.sPlayer.GetForce(),
				Gmud.sPlayer.pre_force);
		as[2] = String.format("敏捷  [%d/%d]", Gmud.sPlayer.GetAgility(),
				Gmud.sPlayer.pre_agility);
		as[3] = String.format("根骨  [%d/%d]", Gmud.sPlayer.GetAptitude(),
				Gmud.sPlayer.pre_aptitude);
		as[4] = String.format("悟性  [%d/%d]", Gmud.sPlayer.GetSavvy(),
				Gmud.sPlayer.pre_savvy);
		for (int i = 0; i < 5; i++)
			Video.VideoDrawStringSingleLine(as[i], 8, 17 + i * 12);

		Battle battle = Battle.sBattle;
		if (battle != null) {
			as[0] = String.format("攻击  [%d]", battle.CalcAttack(0));
			as[1] = String.format("防御  [%d]", battle.CalcDefenseB(0));
			as[2] = String.format("闪避  [%d]", battle.CalcAvoid(0));
			as[3] = String.format("命中  [%d]", battle.CalcHit(0));
			as[4] = String.format("福缘  [%d]", Gmud.sPlayer.bliss);
			for (int i = 0; i < 5; i++)
				Video.VideoDrawStringSingleLine(as[i], 96, 17 + i * 12);
		}
	}

	static void DrawPlayerMerry() {
		Video.VideoDrawStringSingleLine(Gmud.sPlayer.GetConsortName(), 8, 17);
	}

	private static int DrawDeleteItem() {
		int ret = 0;
		final int x = 16 + 26 + 26 + 6 + 10;
		final int y = 5 + 26 + 16;
		final int w = Video.SMALL_LINE_H * 4;
		final int h = Video.SMALL_LINE_H;
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);
		Video.VideoDrawStringSingleLine("删除吗？", x, y);
		Video.VideoUpdate();
		Gmud.GmudWaitAnyKey();
		if ((Input.getKeyCode() & Input.kKeyEnt) != 0) {
			ret = 1;
		}
		return ret;
	}

	/**
	 * 绘制列表，如 物品或技能，左为分类表，右为分类具体项（使用 {@link GmudTemp#temp_array_32_2} 的数据）
	 * 
	 * @param groups
	 *            分类表标题
	 * @param groupTop
	 *            分类表顶部位置
	 * @param group_sel
	 *            分类表高亮项
	 * @param item_top
	 *            子项顶部
	 * @param item_sel
	 *            子项高亮
	 * @param type
	 *            类别
	 * @param isSkill
	 *            0物品表 1技能表
	 */
	static void DrawList(String groups[], int groupTop, int group_sel,
			int item_top, int item_sel, int type, int isSkill) {
		final int length = (isSkill == 0) ? 6 : 7;
		final int lineH = Video.SMALL_FONT_SIZE;
		final int g_w = Video.SMALL_LINE_H * 2;
		int x = 32 + g_w;
		int y = 4;
		int w = Gmud.WQX_ORG_WIDTH - x * 2;
		int h = lineH * 5;
		final int gap_l = g_w + 6;
		if (w < g_w * 4 + g_w / 2)
			w = g_w * 4 + g_w / 2;
		if ((x = Gmud.WQX_ORG_WIDTH - w - 4) < 0)
			x = 0;
		Video.VideoClearRect(x, y, w, h + 2);
		Video.VideoDrawRectangle(x, y, w, h + 2);
		Video.VideoDrawLine(x + gap_l, y, x + gap_l, y + h + 1);

		// 绘制分类表
		if (groupTop + group_sel <= length - 1) {
			int tx = x + 3;
			int ty = y + 1;
			for (int i = 0, pos = groupTop; i < 5 && pos < length; i++, pos++) {
				if (i == group_sel) {
					Video.VideoFillRectangle(tx, ty, g_w, lineH);
					Video.VideoDrawStringSingleLine(groups[pos], tx, ty, 2);
				} else {
					Video.VideoDrawStringSingleLine(groups[pos], tx, ty);
				}
				ty += lineH;
			}
		}

		// 绘制子项
		int tx = x + gap_l;
		int ty = y + 2;
		final int oy = (lineH - 4) / 2;
		for (int i = 0; i < 5; i++) {
			int index = GmudTemp.temp_array_32_2[i + item_top][0];
			int isSel = GmudTemp.temp_array_32_2[i + item_top][1];
			if (index == 255)
				return;

			Video.VideoDrawRectangle(tx + 4, ty + oy, 5, 5);
			if (type == 1 && item_sel == i)
				Video.VideoFillRectangle(tx + 4, ty + oy, 5, 5);
			if (isSel == 1) {
				Video.VideoDrawStringSingleLine("√", tx + 3, ty - 2);
				/*
				 * Video.VideoDrawLine(i3 + l3 + 4 + 4, j4 * k2 + 5 + 3 + j5 +
				 * 9, i3 + l3 + 8 + 4, j4 * k2 + 5 + 2 + 4 + 9);
				 * Video.VideoDrawLine(i3 + l3 + 4 + 4, j4 * k2 + 5 + 3 + j5 +
				 * 9, i3 + l3 + 8 + 13, j4 * k2 + 5 + 2 + 4);
				 */
			}

			final String title;
			if (isSkill == 0) {
				final int id = Gmud.sPlayer.item_package[index][0];
				final String name = Items.item_names[id];
				if (Items.item_repeat[id] == 1) {
					title = String.format("%s x%d", name,
							Gmud.sPlayer.item_package[index][2]);
				} else {
					title = name;
				}
			} else {
				int id = Gmud.sPlayer.skills[index][0];
				title = Skill.skill_name[id];
			}
			Video.VideoDrawStringSingleLine(title, tx + Video.SMALL_FONT_SIZE,
					ty);

			ty += lineH;
		}
	}

	static void PlayerItem() {
		// 焦点是否在大类列表里面 0是 1不是
		int type = 0;
		int group_top = 0, group_sel = 0;
		int item_top = 0, item_sel = 0, item_count = 0;
		int CW = 12;
		ArrayList<String> item_desc = null;
		int item_desc_count = 0, item_desc_cur = 0;

		boolean update = false;
		boolean update_group = true;
		boolean update_item = false;
		boolean update_desc = false;

		Input.ClearKeyStatus();
		while (Input.Running) {
			Input.ProcessMsg();

			if (Input.inputstatus == 0) {
				Gmud.GmudDelay(60);
				continue;
			}

			if ((Input.inputstatus & Input.kKeyUp) != 0) {
				if (type == 0) {
					if (group_sel > 0)
						group_sel--;
					else if (group_top > 0)
						group_top--;
					else {
						group_top = 1;
						group_sel = 5 - 1;
					}
					update_group = true;
				} else {
					if (item_sel > 0)
						item_sel--;
					else if (item_top > 0)
						item_top--;
					else {
						if (item_count < 5) {
							item_top = 0;
							item_sel = item_count - 1;
						} else {
							item_top = item_count - 5;
							item_sel = 5 - 1;
						}
					}
					update_item = true;
				}
			} else if ((Input.inputstatus & Input.kKeyDown) != 0) {
				if (type == 0) {
					if (group_sel < 4)
						group_sel++;
					else if (group_top < 1)
						group_top++;
					else {
						group_top = 0;
						group_sel = 0;
					}
					update_group = true;
				} else {
					if (item_top + item_sel + 1 >= item_count) {
						item_top = 0;
						item_sel = 0;
					} else if (item_sel < 4)
						item_sel++;
					else
						item_top++;
					update_item = true;
				}
			} else if ((Input.inputstatus & Input.kKeyRight) != 0) {
				if (type != 0 && item_desc_count > 1
						&& item_desc_cur + 1 < item_desc_count) {
					item_desc_cur++;
					update_desc = true;
				}
			} else if ((Input.inputstatus & Input.kKeyExit) != 0) {
				if (type != 0) {
					type = 0;
					update_group = true;
					update_desc = true;
				} else {
					break;
				}
			} else if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				if (type == 0) {
					if (item_count > 0) {
						type = 1;
						update_item = true;
					}
				} else {
					ItemMenu(item_top, item_sel);
					type = 0;
					update_group = true;
					update_desc = true;
				}
			}

			if (update_group) {
				item_count = (Gmud.sPlayer.CopyItemData(-1, group_sel
						+ group_top)) & 0xff;
				item_top = item_sel = 0;
				item_desc_count = item_desc_cur = 0;
			}

			if (update_item) {
				if (type == 1) {
					update_desc = true;
					// 读取物品的描述，并分行
					int index = GmudTemp.temp_array_32_2[item_top + item_sel][0];
					String desc = Items
							.GetItemDesc(Gmud.sPlayer.item_package[index][0]);
					item_desc = Video
							.SplitString(desc, Gmud.WQX_ORG_WIDTH - CW);
					item_desc_count = item_desc.size();
					item_desc_cur = 0;
				}
			}
			if (update_group || update_item) {
				DrawList(item_menu_words, group_top, group_sel, item_top,
						item_sel, type, 0);
				update = true;
			}
			if (update_desc) {
				Video.VideoClearRect(0, Gmud.WQX_ORG_HEIGHT - CW,
						Gmud.WQX_ORG_WIDTH, CW);
				if (item_desc_count > 0) {
					String desc = item_desc.get(item_desc_cur);
					if (item_desc_cur < item_desc_count - 1)
						desc += " >";
					Video.VideoDrawStringSingleLine(desc, 2,
							Gmud.WQX_ORG_HEIGHT - CW);
					update = true;
				}
			}
			if (update) {
				Video.VideoUpdate();
			}
			update = false;
			update_group = false;
			update_item = false;
			update_desc = false;
			Input.ClearKeyStatus();
		}
	}

	/** 物品操作菜单 */
	static void ItemMenu(final int top, final int sel) {
		final int t_w = Video.SMALL_LINE_H * 2; // 文本2个字宽
		final int x = 32 + (t_w + 6) + t_w;
		final int w = t_w + 10;// 文本2个字宽
		final int y = t_w + 5;
		final int ret = UIUtils.ShowMenu(UIUtils.MENU_TYPE_BOX, 10,
				useitem_menu_words, useitem_menu_words.length, 3, x, y, w,
				MENUID_ITEM) - 1;

		final int item_id = GmudTemp.temp_array_32_2[top + sel][0];
		if (ret == 0) {
			// use item
			String s = Gmud.sPlayer.UseItem(item_id);
			if (s.length() > 0) {
				final int lineH = Video.SMALL_LINE_H;
				Video.VideoClearRect(0, Gmud.WQX_ORG_HEIGHT - lineH,
						Gmud.WQX_ORG_WIDTH, lineH);
				Video.VideoDrawStringSingleLine(s, 2, Gmud.WQX_ORG_WIDTH / 2
						- lineH);
				Video.VideoUpdate();
				Gmud.GmudDelay(100);
				Gmud.GmudWaitAnyKey();
			}
		} else if (ret == 1) {
			// delete item
			if (DrawDeleteItem() == 1) {
				Gmud.sPlayer.DeleteOneItem(item_id);
			}
		} else if (ret == 2) {
			// TODO send item
			DrawTip("不能发送!");
			Video.VideoUpdate();
			Gmud.GmudWaitAnyKey();
		}
	}

	static void PlayerSkill() {
		int type = 0;
		int groupTop = 0;
		int groupSel = 0;
		int top = 0;
		int sel = 0;
		int count = 0;
		boolean update_group = true;
		boolean update_item = false;
		Input.ClearKeyStatus();
		while (Input.Running) {
			if ((Input.inputstatus & Input.kKeyUp) != 0) {
				if (type == 0) {
					if (groupSel > 0)
						groupSel--;
					else if (groupTop > 0) {
						groupTop--;
					} else {
						groupTop = 2;
						groupSel = 4;
					}
					update_group = true;
				} else {
					if (sel > 0) {
						sel--;
					} else if (top > 0) {
						top--;
					} else if (count < 5) {
						top = 0;
						sel = count - 1;
					} else {
						top = count - 5;
						sel = 4;
					}
					update_item = true;
				}
			} else if ((Input.inputstatus & Input.kKeyDown) != 0) {
				if (type == 0) {
					if (groupSel < 4)
						groupSel++;
					else if (groupTop < 2)
						groupTop++;
					else
						groupTop = groupSel = 0;
					update_group = true;
				} else {
					if (sel < 4 && top + sel < count - 1)
						sel++;
					else if (top + 5 < count)
						top++;
					else
						top = sel = 0;
					update_item = true;
				}
			} else if ((Input.inputstatus & Input.kKeyExit) != 0) {
				if (type != 0) {
					type = 0;
					update_group = true;
					update_item = true;
				} else {
					Input.ClearKeyStatus();
					Gmud.GmudDelay(200);
					return;
				}
			} else if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				if (type == 0 && count > 0) {
					type = 1;
					update_item = true;
				} else if (type != 0) {
					Gmud.GmudDelay(200);
					final int skillType = groupTop + groupSel;
					final int index = top + sel;
					int id = Gmud.sPlayer.skills[GmudTemp.temp_array_32_2[index][0]][0];
					int isSel = GmudTemp.temp_array_32_2[index][1];
					if (isSel == 0)
						Gmud.sPlayer.SelectSkill(id, skillType);
					else
						Gmud.sPlayer.UnselectSkill(skillType);
					update_group = true;
					update_item = true;
					type = 0;
					if (Battle.sBattle != null)
						Battle.sBattle.CopyPlayerSelectSkills();
				}
			}

			if (update_item) {
				Video.VideoClearRect(0, Gmud.WQX_ORG_HEIGHT
						- Video.SMALL_LINE_H, Gmud.WQX_ORG_WIDTH,
						Video.SMALL_LINE_H);
			}
			if (update_group) {
				count = Gmud.sPlayer.CopySkillData(-1, groupTop + groupSel) & 0xff;
				top = 0;
				sel = 0;
				update_item = true;
			}
			if (update_item) {
				if (count > 0 && type != 0) {
					final int index = GmudTemp.temp_array_32_2[top + sel][0];
					final int point = Gmud.sPlayer.skills[index][2];
					int level = Gmud.sPlayer.skills[index][1];
					if (level > 255)
						level = 255;
					if (level < 0)
						level = 0;
					String s = String.format("%s %d /%d",
							GmudData.level_name[level / 5], level, point);
					Video.VideoDrawStringSingleLine(s, 35, Gmud.WQX_ORG_HEIGHT
							- Video.SMALL_LINE_H);
				}
			}
			if (update_group || update_item) {
				DrawList(skill_menu_words, groupTop, groupSel, top, sel, type,
						1);
				Video.VideoUpdate();
				update_group = false;
				update_item = false;
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(50);
		}
	}

	static void FPMenu() {
		final int w = Video.SMALL_LINE_H * 3;
		int x = 8 + 3 * (16 + 40);
		if (x + w + 3 >= Gmud.WQX_ORG_WIDTH)
			x = Gmud.WQX_ORG_WIDTH - w - 3;
		Gmud.sMap.DrawMap(-1);
		UIUtils.ShowMenu(fp_menu_words, fp_menu_words.length, 4, x, 15, w,
				MENUID_FP);
	}

	static int FPCallback(int sel) {
		if (sel == 0) {
			if (RecoverFP() == 1)
				return 1;
		} else if (sel == 1) {
			if (FPPlusMenu() == 1)
				return 1;
		} else if (sel == 2) {
			String s = Gmud.sPlayer.Breathing();
			DrawStringFromY(s, 660);
			Video.VideoUpdate();
			Gmud.GmudDelay(1500);
		} else if (sel == 3) {
			String s = Gmud.sPlayer.Recovery();
			if (s.substring(0, 3) == "你摧动") {
				String str = "你全身放松，坐下来开始运功疗伤！";
				DrawStringFromY(str, 660);
				Video.VideoUpdate();
				Gmud.GmudDelay(1500);
				DrawStringFromY(s, 660);
				Video.VideoUpdate();
				Gmud.GmudDelay(1500);
			}
			DrawStringFromY(s, 660);
			Video.VideoUpdate();
			Gmud.GmudDelay(1500);
		}
		Gmud.sMap.DrawMap(-1);
		return 0;
	}

	static int RecoverFP() {
		Gmud.GmudDelay(100);
		int j1 = Gmud.sPlayer.GetFPSpeed(); // get speed
		int k1 = 0;
		while (k1 != 3) {
			Input.ProcessMsg();
			while (true) {
				Input.ProcessMsg();
				DrawProgressBox(Gmud.sPlayer.fp_level * 2, Gmud.sPlayer.fp,
						Gmud.sPlayer.fp, Gmud.sPlayer.fp_level);
				Video.VideoUpdate();
				if ((Input.inputstatus & Input.kKeyExit) != 0) {
					Gmud.GmudDelay(150);
					return 0;
				}
				if ((k1 = Gmud.sPlayer.Meditation()) != 0)
					break;
				Input.ClearKeyStatus();
				Gmud.GmudDelay(600 / j1);
			}
			if (k1 == 1) {
				String str = Res.STR_NO_INNER_KONGFU_STRING;
				DrawStringFromY(str, 660);
				Input.ClearKeyStatus();
				Video.VideoUpdate();
				Gmud.GmudDelay(1500);
				return 0;
			}
			if (k1 == 2) {
				String str = "你的内功等级不够";
				DrawStringFromY(str, 660);
				Input.ClearKeyStatus();
				Video.VideoUpdate();
				Gmud.GmudDelay(1500);
				return 0;
			}
		}
		String str = "你的内功等级不够";
		DrawStringFromY(str, 660);
		Input.ClearKeyStatus();
		Video.VideoUpdate();
		Gmud.GmudDelay(1500);
		return 0;
	}

	/** 加力菜单 */
	static int FPPlusMenu() {
		if (255 == Gmud.sPlayer.select_skills[3]) {
			DrawStringFromY(Res.STR_NO_INNER_KONGFU_STRING, 660);
			Video.VideoUpdate();
			Input.ClearKeyStatus();
			Gmud.GmudDelay(1500);
			return 0;
		}
		final int max = Gmud.sPlayer.GetPlusFPMax();
		final int last = Gmud.sPlayer.fp_plus;
		final int x = (8 + 3 * (16 + Video.SMALL_LINE_H * 2))
				- (Video.SMALL_LINE_H * 2) - 14 - 32;

		int cur = last > max ? max : last;
		boolean update = true;

		Input.ClearKeyStatus();
		while (true) {
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyUp) != 0) {
				if (cur > 0) {
					cur--;
					update = true;
				}
			} else if ((Input.inputstatus & Input.kKeyLeft) != 0) {
				if (cur < max) {
					cur += 10;
					if (cur > max)
						cur = max;
					update = true;
				}
			} else if ((Input.inputstatus & Input.kKeyRight) != 0) {
				if (cur > 0) {
					if (cur > 10)
						cur -= 10;
					else
						cur = 0;
					update = true;
				}
			} else if ((Input.inputstatus & Input.kKeyDown) != 0) {
				if (cur < max) {
					cur++;
					update = true;
				}
			}
			if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				Gmud.sPlayer.fp_plus = cur;
				break;
			}
			if ((Input.inputstatus & Input.kKeyExit) != 0) {
				cur = last;
				break;
			}
			if (update) {
				DrawNumberBox(cur, max, x, 60);
				Video.VideoUpdate();
				update = false;
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(80);
		}
		if (cur == max) {
			String str = String.format(Res.STR_FP_PLUS_LIMIT_STRING, max);
			DrawStringFromY(str, 660);
			Input.ClearKeyStatus();
			Video.VideoUpdate();
			Gmud.GmudDelay(1500);
		}
		return 0;
	}

	static int RecoverMP() {
		Gmud.GmudDelay(100);
		int j1 = Gmud.sPlayer.GetMPSpeed(); // get speed
		int k1 = 0;
		while (k1 != 3) {
			Input.ProcessMsg();
			while (true) {
				Input.ProcessMsg();
				DrawProgressBox(Gmud.sPlayer.mp_level * 2, Gmud.sPlayer.mp,
						Gmud.sPlayer.mp, Gmud.sPlayer.mp_level);
				Video.VideoUpdate();
				Input.ProcessMsg();
				if ((Input.inputstatus & Input.kKeyExit) != 0)
					return 0;
				if ((k1 = Gmud.sPlayer.Think()) != 0)
					break;
				Input.ClearKeyStatus();
				Gmud.GmudDelay(600 / j1);
			}
			if (k1 == 1) {
				String str = "你必须选择你要用的法术";
				DrawStringFromY(str, 660);
				Video.VideoUpdate();
				Input.ClearKeyStatus();
				Gmud.GmudDelay(1500);
				return 0;
			}
			if (k1 == 2) {
				String str = "你的法术等级不够";
				DrawStringFromY(str, 660);
				Video.VideoUpdate();
				Input.ClearKeyStatus();
				Gmud.GmudDelay(1500);
				return 0;
			}
		}
		String str = "你的法术等级不够";
		DrawStringFromY(str, 660);
		Video.VideoUpdate();
		Input.ClearKeyStatus();
		Gmud.GmudDelay(1500);
		return 0;
	}

	static int MPPlusMenu() {
		if (255 == Gmud.sPlayer.select_skills[6]) {
			String str = "你必须选择你要用的法术";
			DrawStringFromY(str, 660);
			Video.VideoUpdate();
			Input.ClearKeyStatus();
			Gmud.GmudDelay(1500);
			return 0;
		}
		final int max = Gmud.sPlayer.GetPlusMPMax();
		final int last = Gmud.sPlayer.mp_plus;
		final int x = (8 + 3 * (16 + 26)) - 26 - 14 - 32;
		int cur = last;
		Input.ClearKeyStatus();
		boolean update = true;
		while (Input.Running) {
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyUp) != 0) {
				if (cur > 0) {
					cur--;
					update = true;
				}
			} else if ((Input.inputstatus & Input.kKeyLeft) != 0) {
				if (cur < max) {
					cur += 10;
					if (cur > max)
						cur = max;
					update = true;
				}
			} else if ((Input.inputstatus & Input.kKeyRight) != 0) {
				if (cur > 0) {
					if (cur > 10)
						cur -= 10;
					else
						cur = 0;
					update = true;
				}
			} else if ((Input.inputstatus & Input.kKeyDown) != 0) {
				if (cur < max) {
					cur++;
					update = true;
				}
			} else if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				Gmud.sPlayer.mp_plus = cur;
				break;
			} else if ((Input.inputstatus & Input.kKeyExit) != 0) {
				cur = last;
				break;
			}
			if (update) {
				update = false;
				DrawNumberBox(cur, max, x, 60);
				Video.VideoUpdate();
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(80);
		}
		if (cur == max) {
			String str = String.format("你目前的法点上限为%d", max);
			DrawStringFromY(str, 660);
			Video.VideoUpdate();
			Gmud.GmudDelay(1500);
		} else {
			Gmud.GmudDelay(50);
		}
		return 0;
	}

	static void MPMenu() {
		final int w = Video.SMALL_LINE_H * 3;
		int x = 8 + 3 * (11 + 40);
		if (x + w + w > Gmud.WQX_ORG_WIDTH)
			x = Gmud.WQX_ORG_WIDTH - w - 2;
		Gmud.sMap.DrawMap(-1);
		UIUtils.ShowMenu(mp_menu_words, mp_menu_words.length, 2, x, 22, w,
				MENUID_MP);
	}

	static int MPCallback(int sel) {
		if (sel == 0) {
			if (RecoverMP() == 1)
				return 1;
		} else if (sel == 1)
			if (MPPlusMenu() == 1)
				return 1;
		Gmud.sMap.DrawMap(-1);
		return 0;
	}

	/**
	 * 自练技能菜单
	 */
	static void PractMenu() {
		// 计算可用于练习的技能数量
		final int i1 = Gmud.sPlayer.GetPracticeSkillNumber();
		if (i1 == 0)
			return;

		int j1;
		int k1 = (j1 = 13) * 5 + 20;
		int l1 = Gmud.WQX_ORG_WIDTH - 6 - k1;
		Gmud.GmudDelay(100);
		int i2 = 0;
		int j2 = 0;
		Gmud.sMap.DrawMap(-1);
		DrawSkillList(l1, 40, i1, 0, 0, false);
		Video.VideoUpdate();
		Input.ClearKeyStatus();
		Gmud.GmudDelay(100);
		while (true) {
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyUp) != 0) {
				if (j2 > 0)
					j2--;
				else if (i2 > 0) {
					i2--;
				} else {
					if ((i2 = i1 - 3) < 0)
						i2 = 0;
					if (i1 - 1 < 2)
						j2 = i1 - 1;
					else
						j2 = 2;
				}
				DrawSkillList(l1, 40, i1, i2, j2, false);
				Video.VideoUpdate();
			} else if ((Input.inputstatus & Input.kKeyDown) != 0) {
				if (j2 < 2 && j2 < i1 - 1)
					j2++;
				else if (i2 < i1 - 3) {
					i2++;
				} else {
					i2 = 0;
					j2 = 0;
				}
				DrawSkillList(l1, 40, i1, i2, j2, false);
				Video.VideoUpdate();
			} else if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				Gmud.GmudDelay(100);
				int l2 = 0;
				int j3 = 0;
				l2 = i2 + j2;
				int i3;
				if ((j3 = Gmud.sPlayer
						.SetNewSkill(i3 = GmudTemp.temp_array_20_2[l2][0])) == -1)
					return;
				int k3 = 0;
				m_auto_confirm = 0;
				while (k3 == 0)
					while (true) {
						if ((k3 = DrawPractice(j3)) == 1)
							return;
						if (k3 != 2)
							break;
						Gmud.sMap.DrawMap(-1);
						DrawSkillList(l1, 40, i1, i2, j2, false);
						Input.ClearKeyStatus();
						Video.VideoUpdate();
					}
				Gmud.sMap.DrawMap(-1);
				DrawSkillList(l1, 40, i1, i2, j2, false);
				Video.VideoUpdate();
			} else if ((Input.inputstatus & Input.kKeyExit) != 0) {
				Input.ClearKeyStatus();
				Gmud.GmudDelay(100);
				return;
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(80);
		}
	}

	static int DrawPractice(int i1) {
		Gmud.GmudDelay(100);
		int k1 = Gmud.sPlayer.GetPracticeSpeed(i1);
		int l1 = 0;
		String str;
		// */
		while (l1 != 6) {
			while (true) {
				Input.ProcessMsg();
				DrawProgressBox(Gmud.sPlayer.skills[i1][4],
						Gmud.sPlayer.skills[i1][2], Gmud.sPlayer.skills[i1][2],
						Gmud.sPlayer.skills[i1][1]); // draw
				Video.VideoUpdate();
				if ((Input.inputstatus & Input.kKeyExit) != 0)
					return 1;
				if (m_auto_confirm > 0
						&& (Input.inputstatus & Input.kKeyEnt) != 0) {
					--m_auto_confirm;
				}

				if ((l1 = Gmud.sPlayer.PracticeSkill(i1)) != 0)
					break;
				Gmud.GmudDelay(960 / k1);
			}
			if (l1 == 1) {
				str = "你的功夫很难再有所提高了,还是向师傅请教一下吧";
				DrawStringFromY(str, 660);
				Video.VideoUpdate();
				Input.ClearKeyStatus();
				Gmud.GmudDelay(1500);
				return 3;
			}
			if (l1 == 2) {
				str = "你的内功等级不够";
				DrawStringFromY(str, 660);
				Video.VideoUpdate();
				Input.ClearKeyStatus();
				Gmud.GmudDelay(1500);
				return 3;
			}
			if (l1 == 3) {
				str = "你的内力修为不足,要勤修内功!";
				DrawStringFromY(str, 660);
				Video.VideoUpdate();
				Input.ClearKeyStatus();
				Gmud.GmudDelay(1500);
				return 3;
			}
			if (l1 == 4) {
				str = "趁手的兵器都没有一把,瞎比划什么!";
				DrawStringFromY(str, 660);
				Video.VideoUpdate();
				Input.ClearKeyStatus();
				Gmud.GmudDelay(1500);
				return 3;
			}
			if (l1 == 5) {
				str = "你受伤了,还是先治疗要紧.";
				DrawStringFromY(str, 660);
				Video.VideoUpdate();
				Input.ClearKeyStatus();
				Gmud.GmudDelay(1500);
				return 3;
			}
			if (l1 != 6)
				continue;
			str = "你的功夫进步了!";
			DrawStringFromY(str, 660);
			Video.VideoUpdate();
			Gmud.GmudDelay(1500);
			Gmud.sMap.DrawMap(-1);

			if (m_auto_confirm == 0) {
				return 0;
			}

			int i2;
			str = "继续练功吗？\n([输入]确认 [跳出]放弃)";
			while ((i2 = DialogBx(str, 16, (Gmud.WQX_ORG_WIDTH / 2 / 3) * 2)) != Input.kKeyExit)
				if ((i2 & Input.kKeyEnt) != 0) {
					Input.ClearKeyStatus();
					Gmud.sMap.DrawMap(-1);
					return 0;
				}
			Input.ClearKeyStatus();
			return 3;
		}
		str = "你的武学经验不足,无法领会更深的功夫!";
		DrawStringFromY(str, 660);
		Video.VideoUpdate();
		Input.ClearKeyStatus();
		Gmud.GmudDelay(1500);
		return 3;
	}

	// ********** UI-NPC **********//
	static int npc_id = 0;
	static int npc_image_id = 0;
	static String npc_name;
	static final String npc_menu_words[] = new String[] { "交谈", "查看", "战斗",
			"切磋", "交易", "拜师", "请教" };

	static void TalkWithNPC(int npc_id) {
		task.Talk(npc_id);
	}

	static void EnterBattle(int npc_id) {
		Battle.sBattle = new Battle(npc_id, UI.npc_image_id, 0);
		Battle.sBattle.BattleMain();
		Battle.sBattle = null;

		/*
		 * aa.f = 0; aa.i = o.e; aa.j = id; aa.k = 0; aa.h = 0; aa.d();
		 */
	}

	static void EnterTryBattle(int npc_id) {
		if (NPC.NPC_attrib[npc_id][11] < NPC.NPC_attrib[npc_id][15] / 2) {
			Gmud.sMap.DrawMap(-1);
			Gmud.GmudDelay(120);
			String str = "对方看起来并不想你和切磋";
			UI.DrawDialog(str);
			return;
		} else {
			Battle.sBattle = new Battle(npc_id, UI.npc_image_id, 1);
			Battle.sBattle.BattleMain();
			// delete glpBattle;
			// glpBattle = 0;
			Battle.sBattle = null;
			/*
			 * aa.f = 0; aa.i = o.e; aa.j = i1; aa.k = 1; aa.h = 0; aa.d();
			 */
			return;
		}
	}

	static void Trade(int id) {
		if (NPC.NPC_sell_list[id][0] == 0) // 卖
		{
			int size = Gmud.sPlayer.CopyItemList();
			TradeWithNPC(true, size);
			return;
		} else {
			int k1 = NPC.CopyItemList(id); // 买
			TradeWithNPC(false, k1);
			return;
		}
	}

	/***
	 * 与 NPC 交易
	 * 
	 * @param sell
	 *            false买，true卖
	 * @param size
	 *            物品列表大小，见 {@link GmudTemp#temp_array_32_2}
	 */
	static void TradeWithNPC(boolean sell, int size) {
		if (size <= 0)
			return;
		Gmud.sMap.DrawMap(-1);
		DrawTalk(readDialogText(6 + (sell ? 1 : 0)));
		final int x = 10;
		final int y = 13 + 4;
		int top = 0;
		int sel = 0;
		Input.ClearKeyStatus();
		boolean update = true;
		while (true) {
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyUp) != 0) {
				if (sel > 0) {
					sel--;
				} else if (top > 0) {
					top--;
				} else if (size > 3) {
					top = size - 3;
					sel = 2;
				} else {
					top = 0;
					sel = size - 1;
				}
				update = true;
			} else if ((Input.inputstatus & Input.kKeyDown) != 0) {
				if (top + sel + 1 >= size) {
					top = 0;
					sel = 0;
				} else if (sel < 2)
					sel++;
				else
					top++;
				update = true;
			} else if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				final int pos = top + sel;
				final int id = GmudTemp.temp_array_32_2[pos][0];
				if (!sell) {
					int limit_money = Items.item_attribs[id][6];
					if (Gmud.sPlayer.money >= limit_money
							&& Gmud.sPlayer.GainOneItem(id)) {
						Gmud.sPlayer.money -= Items.item_attribs[id][6];
						break;
					}
				} else {
					// TODO: 用 序号 代替 ID
					// Gmud.sPlayer.LoseItem(i3, 1);
					int index = Gmud.sPlayer.ExistItem(id, 1);
					if (index >= 0) {
						Gmud.sPlayer.LoseItem(index, 1);
						Gmud.sPlayer.money += (Items.item_attribs[id][6] * 7) / 10;
						break;
					}
				}
			} else if ((Input.inputstatus & Input.kKeyExit) != 0) {
				break;
			}
			if (update) {
				update = false;
				DrawItemList(x, y, size, top, sel, sell);
				Video.VideoUpdate();
			}
			Gmud.GmudWaitNewKey(Input.kKeyExit | Input.kKeyEnt | Input.kKeyDown
					| Input.kKeyUp);
		}
		return;
	}

	/**
	 * 绘制物品列表，最多３行
	 * 
	 * @param x
	 * @param y
	 * @param size
	 *            列表数量
	 * @param start
	 *            可见列表中最顶上一个的索引
	 * @param sel
	 * @param sell
	 */
	static void DrawItemList(int x, int y, int size, int start, int sel,
			boolean sell) {
		if (size <= 0)
			return;
		int lineH = 13;
		int h = 0;
		int w = 0;
		int charW = 13;
		if (size >= 3)
			h = lineH * 3 + 6;
		else
			h = size * lineH + 6;
		if (!sell)
			w = charW * 5 + 16 + 4;
		else
			w = charW * 7 + 16 + 10;
		Video.VideoClearRect(x, y, w, h);
		Video.VideoDrawRectangle(x, y, w, h);
		int pad = (lineH - 12) / 2;
		for (int i = 0; i < size && i < 3; i++) {
			int item_id = GmudTemp.temp_array_32_2[start + i][0];
			int item_number = GmudTemp.temp_array_32_2[start + i][1];
			if (item_id == 255)
				continue;
			Video.VideoDrawArc(x + 4 + 6, y + pad + 2 + 6 + i * (lineH + 1), 6);
			if (i == sel) {
				Video.VideoFillArc(x + 4 + 6,
						y + pad + 2 + 6 + i * (lineH + 1), 4);
				int k4 = y + h + 4;
				int cost = Items.item_attribs[item_id][6];
				if (sell)
					cost = (cost * 7) / 10;
				String s2 = String.format("金钱：%d 价格：%d", Gmud.sPlayer.money,
						cost);
				int i5 = Gmud.WQX_ORG_WIDTH - 8 - x;
				Video.VideoClearRect(x, k4, i5, lineH);
				Video.VideoDrawStringSingleLine(s2/* .c_str() */, x, k4);
			}
			String s1 = Items.item_names[item_id];
			if (sell) {
				s1 += String.format("x%d", item_number);
			}
			Video.VideoDrawStringSingleLine(s1, x + 4 + 16, y + 2 + i
					* (lineH + 1));
		}
	}

	/**
	 * 计算技能等级
	 * 
	 * @param i
	 *            门派功夫ID
	 * @param j
	 *            基本功夫ID
	 * @return
	 */
	static int GetSkillTypeLevel(int i, int j) {
		return (Gmud.sPlayer.GetSkillLevel(i) + Gmud.sPlayer.GetSkillLevel(j) / 2);
	}

	// extern wstring ReplaceStr(wstring*, LPCWSTR, LPCWSTR);

	static void ApprenticeWords(int i) {
		// UI.DrawDialog(&ReplaceStr(&ReplaceStr(&gmud_readtext(5,
		// UI.dialog_point[i], UI.dialog_point[1 + i]), "$o",
		// UI.npc_name.c_str()), "m", Gmud.sPlayer.player_name));
		String str = readDialogText(i);
		String s1 = str.replaceAll("\\$o", UI.npc_name);
		UI.DrawDialog(s1.replaceAll("m", Gmud.sPlayer.player_name));
	}

	static void Apprentice(int id) {
		if (Gmud.sPlayer.class_id != 0
				&& Gmud.sPlayer.class_id != NPCINFO.NPC_attribute[id][1]) {
			Gmud.sMap.DrawMap(-1);
			DrawDialog(readDialogText(9));
			return;
		}
		Gmud.sMap.DrawMap(-1);
		switch (id) {
		case 96: // 谷虚道长
			ApprenticeWords(182);
			ApprenticeWords(10);
			Gmud.sPlayer.class_id = 5;
			break;

		case 97: // 古松道长
			ApprenticeWords(182);
			ApprenticeWords(10);
			Gmud.sPlayer.class_id = 5;
			break;

		case 101: // 清虚道长
			if (Gmud.sPlayer.fp_level < 1500) {
				ApprenticeWords(183);
				return;
			}
			if (GetSkillTypeLevel(32, 0) < 180) {
				ApprenticeWords(184);
				return;
			}
			if (GetSkillTypeLevel(31, 1) < 150) {
				ApprenticeWords(185);
				return;
			}
			if (Gmud.sPlayer.GetSavvy() < 28) {
				ApprenticeWords(186);
				return;
			}
			ApprenticeWords(187);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 5;
			break;

		case 122: // 雪山教头
			if (Gmud.sPlayer.GetAgility() < 22) {
				ApprenticeWords(188);
				return;
			}
			ApprenticeWords(189);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 6;
			break;

		case 118: // 封万剑
			if (Gmud.sPlayer.GetAgility() < 23) {
				ApprenticeWords(188);
				return;
			}
			if (GetSkillTypeLevel(36, 0) < 60) {
				ApprenticeWords(193);
				return;
			}
			ApprenticeWords(192);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 6;
			break;

		case 110: // 白瑞德
			if (Gmud.sPlayer.fp_level < 1200) {
				ApprenticeWords(190);
				return;
			}
			if (Gmud.sPlayer.GetAptitude() < 32) {
				ApprenticeWords(191);
				return;
			}
			ApprenticeWords(194);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 6;
			break;

		case 38: // 商宝震
			ApprenticeWords(163);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 1;
			break;

		case 43: // 商剑鸣
			if (Gmud.sPlayer.fp_level < 500) {
				ApprenticeWords(159);
				return;
			}
			if (GetSkillTypeLevel(14, 0) < 75) {
				ApprenticeWords(160);
				return;
			}
			if (GetSkillTypeLevel(11, 3) < 75) {
				ApprenticeWords(161);
				return;
			}
			ApprenticeWords(162);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 1;
			break;

		case 47: // 王维扬
			if (Gmud.sPlayer.fp_level < 800) {
				ApprenticeWords(159);
				return;
			}
			if (GetSkillTypeLevel(14, 0) < 150) {
				ApprenticeWords(160);
				return;
			}
			if (GetSkillTypeLevel(11, 3) < 150) {
				ApprenticeWords(164);
				return;
			}
			ApprenticeWords(165);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 1;
			break;

		case 90: // 腾王丸
			ApprenticeWords(181);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 4;
			break;

		case 87: // 花十郎
			if (GetSkillTypeLevel(27, 1) < 90) {
				ApprenticeWords(180);
				return;
			}
			ApprenticeWords(181);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 4;
			break;

		case 94: // 和仲阳
			if (Gmud.sPlayer.GetForce() < 22) {
				ApprenticeWords(178);
				return;
			}
			if (GetSkillTypeLevel(26, 0) < 180) {
				ApprenticeWords(180);
				return;
			}
			ApprenticeWords(179);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 4;
			break;

		case 73: // 方长老
			ApprenticeWords(174);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 3;
			break;

		case 80: // 余鸿儒
			if (GetSkillTypeLevel(25, 0) < 180) {
				ApprenticeWords(175);
				return;
			}
			if (Gmud.sPlayer.GetForce() < 30) {
				ApprenticeWords(176);
				return;
			}
			ApprenticeWords(174);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 3;
			break;

		case 56: // 平婆婆
			if (Gmud.sPlayer.sex == 0) {
				ApprenticeWords(166);
				return;
			}
			ApprenticeWords(171);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 2;
			break;

		case 57: // 桑轻虹
			if (Gmud.sPlayer.sex == 0) {
				ApprenticeWords(166);
				return;
			}
			if (GetSkillTypeLevel(19, 1) < 90) {
				ApprenticeWords(172);
				return;
			}
			ApprenticeWords(171);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 2;
			break;

		case 66: // 唐晚词
			if (Gmud.sPlayer.sex == 0) {
				ApprenticeWords(166);
				return;
			}
			if (GetSkillTypeLevel(19, 1) < 75) {
				ApprenticeWords(172);
				return;
			}
			ApprenticeWords(171);
			ApprenticeWords(10);
			Gmud.sPlayer.class_id = 2;
			// fall through

		case 58: // 李青照
			if (Gmud.sPlayer.sex == 0) {
				ApprenticeWords(166);
				return;
			}
			if (Gmud.sPlayer.GetSkillLevel(9) < 100) {
				ApprenticeWords(168);
				return;
			}
			if (Gmud.sPlayer.GetFaceLevel() < 9) {
				ApprenticeWords(167);
				return;
			}
			ApprenticeWords(170);
			ApprenticeWords(10);
			Gmud.sPlayer.class_id = 2;
			break;

		case 127: // 华佗
			if (Gmud.sPlayer.pre_savvy > 18) {
				ApprenticeWords(151);
				return;
			}
			ApprenticeWords(150);
			ApprenticeWords(10);
			Gmud.sPlayer.class_id = 7;
			break;

		case 126: // 北海鳄神
			if (Gmud.sPlayer.pre_savvy > 18) {
				ApprenticeWords(151);
				return;
			}
			if (GetSkillTypeLevel(40, 7) < 90) {
				ApprenticeWords(152);
				return;
			}
			ApprenticeWords(149);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 7;
			break;

		case 129: // 娜可露露
			if (Gmud.sPlayer.pre_savvy > 18) {
				ApprenticeWords(151);
				return;
			}
			if (GetSkillTypeLevel(40, 7) < 165) {
				ApprenticeWords(153);
				return;
			}
			if (GetSkillTypeLevel(45, 0) < 165) {
				ApprenticeWords(153);
				return;
			}
			if (GetSkillTypeLevel(41, 3) < 165) {
				ApprenticeWords(153);
				return;
			}
			if (GetSkillTypeLevel(43, 1) < 165) {
				ApprenticeWords(153);
				return;
			}
			ApprenticeWords(148);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 7;
			break;

		case 135: // 葛洪
			if (Gmud.sPlayer.pre_savvy < 28) {
				ApprenticeWords(154);
				return;
			}
			ApprenticeWords(145);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 8;
			break;

		case 138: // 留孙真人
			if (Gmud.sPlayer.GetSavvy() < 28) {
				ApprenticeWords(154);
				return;
			}
			if (GetSkillTypeLevel(49, 9) < 120) {
				ApprenticeWords(155);
				return;
			}
			ApprenticeWords(147);
			ApprenticeWords(10);

			Gmud.sPlayer.class_id = 8;
			break;

		case 141: // 茅盈
			if (Gmud.sPlayer.GetSavvy() < 40) {
				ApprenticeWords(156);
				return;
			}
			if (Gmud.sPlayer.mp_level < 1200) {
				ApprenticeWords(157);
				return;
			}
			ApprenticeWords(146);
			ApprenticeWords(10);
			Gmud.sPlayer.class_id = 8;
			break;

		default:
			return;
		}
		Gmud.sPlayer.teacher_id = id;
	}

	/**
	 * 学技能
	 * 
	 * @see Player#StudySkill(int, int)
	 * @param skill_id
	 * @param skill_level
	 * @return 0正常 1经验不足 2潜能不足 3钱不足 4等级超过 5升级
	 */
	static int DrawConsult(int skill_id, int skill_level) {
		Gmud.GmudDelay(100);
		final int speed = Gmud.sPlayer.GetStudySpeed();
		final int delay = 600 / speed;
		final int[] data = Gmud.sPlayer.skills[skill_id];

		int ret = 0;
		while (ret == 0) {
			Input.ClearKeyStatus();
			Input.ProcessMsg();
			DrawProgressBox(data[4], data[2], data[2], data[1]);
			Video.VideoUpdate();
			Gmud.GmudDelay(delay);

			if ((Input.inputstatus & Input.kKeyExit) != 0)
				break;

			if (m_auto_confirm > 0 && (Input.inputstatus & Input.kKeyEnt) != 0)
				--m_auto_confirm;

			ret = Gmud.sPlayer.StudySkill(skill_id, skill_level);
		}

		if (ret == 0)
			return 1;

		String str;
		if (ret == 1) {
			str = "你的武学经验不足,无法领会更深的功夫";
		} else if (ret == 2) {
			str = "你的潜能已经发挥到极限了,没有办法再成长了";
		} else if (ret == 3) {
			str = "没钱读什么书啊，回去准备够学费再来吧!";
		} else if (ret == 4) {
			str = "你的功夫已经不输为师了，真是可喜可贺呀";
		} else {
			str = "你的功夫进步了！";
		}
		DrawStringFromY(str, 660);
		Video.VideoUpdate();
		Gmud.GmudDelay(1500);

		if (ret != 5)
			return ret;

		Input.ClearKeyStatus();
		Gmud.sMap.DrawMap(-1);

		if (m_auto_confirm == 0)
			return 0;

		str = "继续学习吗？\n([输入]确认 [跳出]放弃)";
		int key = DialogBx(str, 16, (Gmud.WQX_ORG_WIDTH / 2 / 3) * 2);
		while ((Input.inputstatus & Input.kKeyExit) == 0) {
			if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				Gmud.sMap.DrawMap(-1);
				return 0;
			}

			do {
				Input.ClearKeyStatus();
				Gmud.GmudDelay(100);
			} while (Input.inputstatus == 0);
		}
		Gmud.GmudDelay(120);
		return 5;
	}

	static void ConsultWithNPC(int size) {
		Gmud.sMap.DrawMap(-1);
		UI.DrawTalk(readDialogText(8));
		final int x = 10;
		final int y = 13 + 4;
		int top = 0;
		int sel = 0;

		final int max;
		if (size > 3)
			max = size - 3;
		else
			max = 0;

		boolean update = true;
		UI.DrawSkillList(x, y, size, top, sel, true);
		Video.VideoUpdate();
		Gmud.GmudDelay(200);
		while (true) {
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyUp) != 0) {
				if (sel > 0)
					sel--;
				else if (top > 0) {
					top--;
				} else {
					top = max;
					if (max == 0)
						sel = size - 1;
					else
						sel = 2;
				}
				update = true;
			} else if ((Input.inputstatus & Input.kKeyDown) != 0) {
				if (sel < 2 && sel < size - 1)
					sel++;
				else if (top < size - 3) {
					top++;
				} else {
					top = 0;
					sel = 0;
				}
				UI.DrawSkillList(x, y, size, top, sel, true);
				Video.VideoUpdate();
			} else if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				Gmud.GmudDelay(100);
				int index = top + sel;
				int skill_id = GmudTemp.temp_array_20_2[index][0];
				int skill_level = GmudTemp.temp_array_20_2[index][1];
				int skill_index = Gmud.sPlayer.SetNewSkill(skill_id);
				if (skill_index >= 0) {
					m_auto_confirm = 3;
					int k3 = 0;
					while (k3 == 0) {
						// == 2 没有潜能，不能学
						if ((k3 = DrawConsult(skill_index, skill_level)) == 2)
							return;
					}
					Gmud.sMap.DrawMap(-1);
					DrawSkillList(x, y, size, top, sel, true);
					Video.VideoUpdate();
				}
			} else if ((Input.inputstatus & Input.kKeyExit) != 0) {
				Gmud.GmudDelay(100);
				return;
			}

			if (update) {
				update = false;
				DrawSkillList(x, y, size, top, sel, true);
				Video.VideoUpdate();
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(80);
		}
	}

	static void Consult(int id) {
		// 独行大侠，要求 exp > 20W
		if (6 == id && Gmud.sPlayer.exp < 0x30d40) {
			Gmud.sMap.DrawMap(-1);
			String str = "去去去，赚够经验再来学吧！";
			DrawDialog(str);
		} else {
			int size = NPC.CopyNCPSkillList(id);
			if (0 == size) {
			} else {
				ConsultWithNPC(size);
			}
		}
	}

	static void NPCMainMenu(int npc_id, int npc_type, int x) {
		final String[] titles;
		final int count;
		if (npc_type <= 0) {
			count = 4;
			titles = npc_menu_words;
		} else {
			count = 5;
			titles = new String[5];
			System.arraycopy(npc_menu_words, 0, titles, 0, 4);
			titles[4] = npc_menu_words[npc_type];
		}
		final int w = Video.SMALL_LINE_H * 2 + 12;
		if (x < 4)
			x = 4;
		if (x + w + 4 > Gmud.WQX_ORG_WIDTH)
			x = Gmud.WQX_ORG_WIDTH - 4 - w;
		Video.VideoClearRect(4, 4, 26 * 2, 13);
		Video.VideoDrawStringSingleLine(npc_name, 4, 4);
		final int sel = UIUtils.ShowMenu(UIUtils.MENU_TYPE_BOX, 12, titles,
				count, count, x, -1, w, MENUID_NPC) - 1;
		if (sel >= 0) {
			if (sel == 0) {
				TalkWithNPC(npc_id); // 交谈
			} else if (sel == 1) {
				ViewNPC(npc_id); // 查看
			} else if (sel == 2) {
				EnterBattle(npc_id); // 战斗
			} else if (sel == 3) {
				EnterTryBattle(npc_id); // 切磋
			} else if (sel == 4) {
				if (npc_type == 4)
					Trade(npc_id); // 交易
				else if (npc_type == 5)
					Apprentice(npc_id); // 拜师
				else if (npc_type == 6)
					Consult(npc_id); // 请教
			}
			Gmud.GmudDelay(100);
		}
	}

	static void DrawTalk(String tip) {
		final int h = Video.SMALL_LINE_H * 2 + 2;
		ArrayList<String> as = Video.SplitString(tip, Gmud.WQX_ORG_WIDTH - 3);
		final int size = as.size();
		for (int i = 0; i < size;) {
			Video.VideoClearRect(0, 0, Gmud.WQX_ORG_WIDTH - 1, h + 6);
			Video.VideoDrawRectangle(0, 0, Gmud.WQX_ORG_WIDTH - 1, h + 6);
			Video.VideoDrawStringSingleLine(as.get(i), 10, 4);
			if (++i < size) {
				Video.VideoDrawStringSingleLine(as.get(i), 10, 7 + 12);
				i++;
			}
		}
	}

	static void ViewNPC(int npcId) {
		int age = NPCINFO.NPC_attribute[npcId][2];
		if (age < 10)
			age = 10;
		age = (age / 10) * 10;

		Battle.sBattle = new Battle(npcId, 0, 0);
		Battle.sBattle.CopyNPCData(npcId);
		Battle.sBattle.CalcFighterLevel(1);
		int level = Battle.sBattle.fighter_data[1][62] / 5;
		int attack_level = Battle.sBattle.CalcAttackLevel(1);
		Battle.sBattle = null;

		final StringBuilder b = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			int item_id = NPC.NPC_item[npcId][i];
			if ((item_id < 77 || item_id > 86)
					&& (item_id < 88 || item_id > 91)
					&& (item_id < 68 || item_id > 71) && item_id != 10
					&& item_id > 0) {
				b.append(Items.item_names[item_id]).append(" ");
			}
		}
		final String items = b.toString();
		final String desc = NPC.GetNPCDesc(npcId);
		String str = String.format("%s看起来约%d多岁\n武艺看起来%s\n出手似乎%s\n带着:%s\n%s",
				npc_name, age, GmudData.level_name[level],
				GmudData.attack_level_name[attack_level], items, desc);
		final int w = Gmud.WQX_ORG_WIDTH - 4;
		final int h = Gmud.WQX_ORG_WIDTH / 2 - 4;
		final ArrayList<String> as = Video.SplitString(str, w);
		final int size = as.size();
		final int lineH = 13;
		final int max_line = 6;
		int pages = (size + max_line - 1) / max_line;
		for (int i = 0, head = 0; i < pages; head += max_line) {
			Video.VideoClearRect(2, 2, w, h);
			Video.VideoDrawRectangle(2, 2, w, h);
			for (int j = 0, t = head; j < max_line && t < size; j++, t++)
				Video.VideoDrawStringSingleLine(as.get(t), 2, 3 + j * 12);
			Video.VideoUpdate();
			Input.ClearKeyStatus();
			Gmud.GmudDelay(120);
			if (++i < pages) {
				while ((Input.inputstatus & Input.kKeyDown) == 0) {
					UI.DrawFlashCursor(Gmud.WQX_ORG_WIDTH - 16, 5, 8);
					if ((Input.inputstatus & Input.kKeyExit) != 0) {
						Gmud.GmudDelay(Gmud.DELAY_WAITKEY);
						return;
					}
				}
			} else {
				while ((Input.inputstatus & Input.kKeyExit) == 0)
					Gmud.GmudDelay(Gmud.DELAY_WAITKEY);
				Input.ClearKeyStatus();
				Gmud.GmudDelay(100);
				return;
			}
		}
	}

	final static int MENUID_FP = 0;
	final static int MENUID_MP = 1;
	final static int MENUID_PRACT = 2;
	final static int MENUID_SYSTEM = 3;
	final static int MENUID_FLY = 4;
	final static int MENUID_ITEM = 5;
	final static int MENUID_NPC = 6;

	private static int _def_callback_index(int sel) {
		return sel + 1;
	}

	public static int onMenuCallBack(int callbackID, int sel) {
		int ret = 0;
		switch (callbackID) {
		case MENUID_FP:
			return FPCallback(sel);
		case MENUID_MP:
			return MPCallback(sel);
		case MENUID_SYSTEM:
			return SystemCallback(sel);
		case MENUID_FLY:
			return FlyCallback(sel);
		case MENUID_ITEM:
			return _def_callback_index(sel);
		case MENUID_NPC:
			return _def_callback_index(sel);

		default:
			break;
		}
		return ret;
	}

}
