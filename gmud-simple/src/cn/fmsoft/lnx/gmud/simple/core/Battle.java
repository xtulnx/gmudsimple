package cn.fmsoft.lnx.gmud.simple.core;

import cn.fmsoft.lnx.gmud.simple.core.NPC.NPCSKILLINFO;

public class Battle {
	String player_name;
	String NPC_name;
	int a_int_array2d_static[][] = new int[2][10];

	/** (size:2,9) 见 {@link Skill#a}, [8]为描述索引 */
	int c_int_array2d_static[][] = new int[2][9];

	int fighter_data[][] = new int[2][256];
	int is_try;
	int player_id;
	int active_id;
	int NPC_id;
	int NPC_image_id;
	private short NPC_item[] = new short[5];
	/** NPC 的装备， [15]是武器 */
	int NPC_equip[] = new int[16];
	int NPC_select_skill[] = new int[16];

	/** 用作 {@link #b_int_array1d_static} 的副本, [0,10)暂存可用招式ID用于NPC随机出招 */
	int a_int_array1d_static[] = new int[256];
	/** [32,256) 用于记录 {@link #fighter_data} 的变化{id,type,value} */
	int b_int_array1d_static[] = new int[256];

	/** 是否逃脱 */
	boolean bEscape; // end flag ?

	/** 3个数值一组 */
	private int stack_top;

	/** (size:4) [0]描述动作? [3]武器？ */
	private int f_int_array1d_static[] = new int[4];
	private boolean a_boolean_static;

	static Battle sBattle;

	public Battle(int npc, int nimg, int tryflag) {
		NPC_id = npc;
		NPC_image_id = nimg;
		is_try = tryflag;
		player_id = 0;
		active_id = 0;
		for (int i = 0; i < 256; i++) {
			b_int_array1d_static[i] = -1;
			fighter_data[0][i] = fighter_data[1][i] = 0;
		}
		stack_top = 0;
	}

	/**
	 * 计算攻击水平，只有6级。
	 * 
	 * @see GmudData#attack_level_name
	 * @param id
	 *            角色
	 * @return [0,6)
	 */
	int CalcAttackLevel(int id) {
		int[] data = fighter_data[id];
		// 臂力+加力
		int attack = data[8] + data[0];
		// 武器
		int weapon = data[29];
		if (weapon != 0)
			attack += Items.item_attribs[weapon][2];

		attack /= 20;
		if (attack > 5)
			attack = 5;
		return attack;
	}

	/** 统计绝招数量 */
	int CountMagicEffect(int id) {
		final int[] magic_stack = a_int_array2d_static[id];
		int count = 0;
		for (int i = 0; i < 10; i++) {
			int magic = magic_stack[i];
			if (magic < 0 || magic >= 40)
				break;
			count++;
		}
		return count;
	}

	/**
	 * 记录 {@link #fighter_data} 值的变化（伤害结果？）。 先在栈中查找，如果存在则直接替换，否则入栈。如果栈满则不添加。
	 * 
	 * @param id 角色ID
	 * @param type
	 *            {@link #fighter_data} 的元素索引, 0=fpPlus 1=hp 2=hp_max 4=fp
	 * @param val
	 *            新的值
	 */
	void stack_fighterdate_set(int id, int type, int val) {
		int[] fighter_stack = b_int_array1d_static;
		for (int top = 0; top < stack_top; top += 3) {
			// 查找栈，如果(角色ID)及（类型?）相同，则更改成新的（伤害？）
			if (fighter_stack[top + 32] == id
					&& fighter_stack[top + 1 + 32] == type) {
				fighter_stack[top + 2 + 32] = val;
				return;
			}
		}

		// 如果栈空间不足（从[32]开始使用），就不记录，直接返回
		if (stack_top + 3 > 256 - 32)
			return;

		fighter_stack[stack_top + 32] = id;
		fighter_stack[stack_top + 1 + 32] = type;
		fighter_stack[stack_top + 2 + 32] = val;
		stack_top += 3;
	}

	// TODO:
	// Bug: 0级买麻绳与卖花妞战斗，普通攻击的描述变成“闪避”类的文本
	// 相关变量：b_int_array1d_static[5] = 246 c_int_array2d_static[?][8]
	/** 物理攻击，返回伤害值（掉血量） */
	int PhyAttack(boolean flag) {
		int ai[] = new int[2];

		// 随机一个攻击部位
		b_int_array1d_static[3] = util.RandomInt(16);

		ai[0] = ai[1] = 0;
		int id = active_id;
		int id_rival = active_id != 0 ? 0 : 1;
		// int i1;
		// if ((i1 = a(n = jdField_g_of_type_Int == 0 ? 1 : 0, 1)) == 0)
		// return 0;
		int k1 = 50;
		if (active_id == player_id)
			k1 = 60;

		// 经验差距
		int exp_gap = CalcExpLevel(fighter_data[id][64] / 100)
				- CalcExpLevel(fighter_data[id_rival][64] / 100);
		if (exp_gap < -100)
			exp_gap = -100;
		if (exp_gap > 100)
			exp_gap = 100;

		// 武器
		int weapon_id = fighter_data[id][29];
		if (flag && f_int_array1d_static[3] >= 0)
			fighter_data[id][29] = f_int_array1d_static[3]; // 暂时换了武器？

		// 敏捷相差
		int agility_gap = fighter_data[id][9] - fighter_data[id_rival][9];
		int l2 = CalaAvtiveSpeed(id, 2, 4);
		if (l2 > 0 && l2 < 20)
			agility_gap += CalaAvtiveSpeed(id, 1, 0);

		int i2 = 0;
		for (int j3 = 0; j3 < 2; j3++) {
			// 武器
			i2 = fighter_data[j3][29];

			// 空手
			if (i2 == 0) {
				int skill_id = fighter_data[j3][30];
				// 无拳脚技能
				if (skill_id == 255) {
					c_int_array2d_static[j3][8] = ib(j3, 1, 1);
				} else {
					int skill_level = fighter_data[j3][31];
					c_int_array2d_static[j3][8] = ib(j3, skill_id, skill_level);
					ai[j3] = skill_level;
				}
			} else {
				// 武器类型
				int weapon_type = Items.item_attribs[i2][1];
				// 兵刃技能
				int skill_id = fighter_data[j3][32];
				// 兵刃技能有效，并且武器类型相符
				if (skill_id != 255
						&& Skill.skill_weapon_type[skill_id] == weapon_type) {
					int skill_level = fighter_data[j3][33];
					c_int_array2d_static[j3][8] = ib(j3, skill_id, skill_level);
					ai[j3] = skill_level;
				} else { // XXX: 鞭类武器(9)时，描述文本变成闪避
					skill_id = Skill.weapon_to_base_skill[weapon_type];
					c_int_array2d_static[j3][8] = ib(j3, skill_id, 1);
				}
			}
		}

		if (flag && f_int_array1d_static[0] >= 0) {
			// 直接使用 描述动作？
			c_int_array2d_static[id][8] = f_int_array1d_static[0];
			int k3 = f_int_array1d_static[0];
			for (int k4 = 0; k4 < 8; k4++)
				c_int_array2d_static[id][k4] = Skill.a[k3][k4];
		}

		int i3 = 0;
		i3 = 0 + (c_int_array2d_static[id][4] <= 127 ? c_int_array2d_static[id][4]
				: -(256 - c_int_array2d_static[id][4]));
		// 等级差
		int l3 = (fighter_data[id][62] / 5 - fighter_data[id_rival][62] / 5) * 12;
		if (l3 < -60)
			l3 = -60;
		if (l3 > 60)
			l3 = 60;
		k1 += CalcHit(id) + l3 + i3 + agility_gap + exp_gap;
		if (k1 > 99)
			k1 = 99;
		if (k1 < 1)
			k1 = 1;
		int i6 = CalaAvtiveSpeed(id_rival, 4, 4);
		if (i6 > 0 && i6 < 20)
			k1 = 100;
		if (util.RandomBool(k1)) {
			int k6 = CalcAvoid(id_rival); // 回避率
			int j7 = 0;
			int j8 = fighter_data[id_rival][34]; // 轻功
			if (j8 > 0 && j8 < 94)
				j7 = CalcExpLevel(fighter_data[id_rival][35]);
			k6 += j7;
			if (i6 > 0 && i6 < 20)
				k6 = 0;
			if (util.RandomBool(k6)) {
				int l8 = c_int_array2d_static[id][8];
				int k9 = 0;
				b(0, l8, 1);
				if (fighter_data[id_rival][34] != 255) // 如果 对手有轻功，使用他的轻功招式描述
					k9 = ib(id_rival, fighter_data[id_rival][34],
							fighter_data[id_rival][35]);
				else
					k9 = util.RandomInt(5) + 243; // 随机取[243,248)间的招式描述
				d(0, k9, 1);
				fighter_data[id][29] = weapon_id;
				return 0;
			}
			int i9 = 0;
			i9 = 0 + fighter_data[id_rival][8]; // 臂力
			if (fighter_data[id_rival][54] != 255) // 基本招架
				i9 += fighter_data[id_rival][55] / 2;
			if (fighter_data[id_rival][38] != 255) // 招架
				i9 += fighter_data[id_rival][39];
			i9 = CalcExpLevel(i9 += c_int_array2d_static[id_rival][5] <= 127 ? c_int_array2d_static[id_rival][5]
					: -(255 - c_int_array2d_static[id_rival][5]));
			if (i6 > 0 && i6 < 20)
				i9 = 0;
			if (util.RandomBool(i9)) {
				int l9 = c_int_array2d_static[id][8];
				b(0, l9, 1);
				int k10;
				if (i2 == 0)
					k10 = 252;
				else
					k10 = util.RandomInt(4) + 248;
				d(0, k10, 1);
				fighter_data[id][29] = weapon_id;
				return 0;
			}
		} else {
			int l6 = c_int_array2d_static[id][8];
			if (util.RandomBool(65)) { // ? 如果击中了，加入攻击描述
				b(0, l6, 1);
				int k7;
				if (i2 == 0)
					k7 = 252;
				else
					k7 = util.RandomInt(4) + 248;
				d(0, k7, 1);
			} else {
				int l7 = 0;
				b(0, l6, 1);
				if (fighter_data[id_rival][34] != 255) // 如果 对手有轻功
					l7 = ib(id_rival, fighter_data[id_rival][34],
							fighter_data[id_rival][35]);
				else
					l7 = util.RandomInt(5) + 243;
				d(0, l7, 1);
			}
			fighter_data[id][29] = weapon_id; // 恢复武器
			return 0;
		}
		if ((i6 = CalaAvtiveSpeed(id_rival, 5, 4)) > 0 && i6 < 20
				&& util.RandomBool(CalaAvtiveSpeed(id_rival, 5, 0))) {
			b(0, c_int_array2d_static[id][8], 1);
			d(1, CalaAvtiveSpeed(id_rival, 5, 2), 1);
			fighter_data[id][29] = weapon_id;
			return 0;
		}
		int i8 = CalcAttack(id); // 计算攻击
		int i7 = 0 + i8; // 伤害值
		fighter_data[id][29] = weapon_id;
		int k8 = c_int_array2d_static[id][6];
		i7 += c_int_array2d_static[id][7];
		// XXX: 不同？！
		if (fighter_data[id][29] > 800 || fighter_data[id][30] > 800)
			Video.exit(1); // error exit
		int j9 = fighter_data[id][0]; // 加力
		if (fighter_data[id][4] > 0) // 内力 -= 加力
		{
			int i10 = stackB_find(id, 4);
			if (i10 < 0)
				i10 = fighter_data[id][4];
			if (i10 >= j9) {
				i10 -= j9;
			} else {
				j9 = i10;
				i10 = 0;
			}
			stack_fighterdate_set(id, 4, i10);
		} else {
			j9 = 0;
		}
		i7 += i2 == 0 ? j9 : j9 / 2;
		if (flag)
			i7 += f_int_array1d_static[1];
		if ((i7 = (i7 = (i7 -= CalcDefenseB(id_rival))
				- c_int_array2d_static[id_rival][1] * ai[id_rival])
				- fighter_data[id_rival][4] / 10) < 0)
			i7 = 1;
		if (i7 < 8)
			i7 += 8;
		int j10 = 10 + (c_int_array2d_static[id][3] <= 127 ? c_int_array2d_static[id][3]
				: -(256 - c_int_array2d_static[id][3]));
		if (flag)
			j10 += f_int_array1d_static[2];
		if (j10 < 1)
			j10 = 1;
		if (j10 > 70)
			j10 = 70;
		if (util.RandomBool(j10)) {
			i7 += util.RandomInt(fighter_data[id][8]); // 臂力
			if (i2 == 0)
				k8 = i7;
		}
		int l10;
		if (util.RandomBool((l10 = fighter_data[id_rival][11]) * 2)
				&& (i7 -= i7 / 3) > k8)
			k8 = i7;
		int i11;
		if (util.RandomBool(i11 = CalcDefense(id_rival)))
			k8 = 0;
		if (k8 > i7)
			k8 = i7;
		if (i7 <= i8 / 2)
			i7 = i8 / 2 + util.RandomInt(i8 / 2);
		int j11 = c_int_array2d_static[id][8];
		b(0, j11, 1);
		int k11 = 0;
		int l11 = 0;
		if ((i2 = fighter_data[id][29]) == 0) {
			int i12 = 1;
			if (fighter_data[id][30] != 255)
				i12 = fighter_data[id][30];
			k11 = ia(0, i7, fighter_data[id_rival][3], i12);
		} else {
			int j12 = 1;
			if (fighter_data[id][32] != 255)
				j12 = fighter_data[id][32];
			int l12;
			k11 = ia(l12 = Items.item_attribs[i2][1], i7,
					fighter_data[id_rival][3], j12);
		}
		int k12 = stackB_find(id_rival, 1);
		int i13 = stackB_find(id_rival, 2);
		if (k12 < 0)
			k12 = fighter_data[id_rival][1];
		if (i13 < 0)
			i13 = fighter_data[id_rival][2];
		int j13 = k12 - i7; // 扣除 hp
		int k13 = i13 - k8; // 扣除 hp-max
		if (j13 < 0)
			j13 = 0;
		if (k13 < 0)
			k13 = 0;
		stack_fighterdate_set(id_rival, 1, j13);
		stack_fighterdate_set(id_rival, 2, k13);
		if ((l11 = 9 - j13 / (fighter_data[id_rival][3] / 10)) < 0)
			l11 = 0;
		if (i2 != 0) {
			if (l11 == 9 && util.RandomInt(2) == 1)
				l11 = 10;
			l11 += 47;
		} else {
			l11 += 37;
		}
		k11 = l11 * 256 + k11;
		d(2, k11, 1);
		return i7;
	}

	boolean BattleIsEnd() {
		if (bEscape)
			return false; // 逃跑成功

		if (is_try == 0) {// 有一方 hp<=0 战斗结束
			if (fighter_data[0][1] <= 0 || fighter_data[1][1] <= 0)
				return false; // 退出
		} else if (is_try == 1
				&& (fighter_data[0][1] < fighter_data[0][3] / 2 || fighter_data[1][1] < fighter_data[1][3] / 2)) // 切磋
			return false; // 退出
		return true;
	}

	void CopyData() {
		CopyPlayerData();
		CopyNPCData(NPC_id);
	}

	void CopyPlayerData() {
		int id = player_id;
		player_name = Gmud.sPlayer.player_name;
		for (int i = 0; i < 128; i++)
			fighter_data[id][i] = 0;

		fighter_data[id][66] = Gmud.sPlayer.class_id; // class id
		fighter_data[id][0] = Gmud.sPlayer.fp_plus; // 加力
		fighter_data[id][1] = Gmud.sPlayer.hp; // hp
		fighter_data[id][2] = Gmud.sPlayer.hp_max; // hp-max
		fighter_data[id][3] = Gmud.sPlayer.hp_full; // hp_full
		fighter_data[id][4] = Gmud.sPlayer.fp; // fp
		fighter_data[id][5] = Gmud.sPlayer.fp_level; // fp-level
		fighter_data[id][6] = Gmud.sPlayer.mp; // mp
		fighter_data[id][7] = Gmud.sPlayer.mp_level; // mp-level
		fighter_data[id][8] = Gmud.sPlayer.GetForce(); // 后天膂力
		fighter_data[id][9] = Gmud.sPlayer.GetAgility(); // 后天敏捷
		fighter_data[id][10] = Gmud.sPlayer.GetSavvy(); // 后天悟性
		fighter_data[id][11] = Gmud.sPlayer.GetAptitude(); // 后天根骨
		fighter_data[id][12] = Gmud.sPlayer.unknow2; // ?? 命中
		fighter_data[id][13] = Gmud.sPlayer.unknow3; // ?? 闪避

		fighter_data[id][67] = Gmud.sPlayer.money; // money
		fighter_data[id][68] = Gmud.sPlayer.sex; // sex
		fighter_data[id][69] = (int) (Gmud.sPlayer.played_time / Player.AGE_TIME); // age

		// reset skill
		for (int i = 0; i < 8; i++) {
			fighter_data[id][30 + i * 2] = 255;
			fighter_data[id][30 + i * 2 + 1] = 0;
			fighter_data[id][46 + i * 2] = 255;
			fighter_data[id][46 + i * 2 + 1] = 0;
		}

		// copy equip data
		for (int i = 0; i < 16; i++) {
			fighter_data[id][14 + i] = Gmud.sPlayer.equips[i];
		}

		// copy selected skill data
		for (int l1 = 0; l1 < 8; l1++) {
			int skill_id = Gmud.sPlayer.select_skills[l1];
			fighter_data[id][30 + l1 * 2] = skill_id;
			fighter_data[id][30 + l1 * 2 + 1] = Gmud.sPlayer
					.GetSkillLevel(skill_id);
		}

		// 基本拳脚
		fighter_data[id][46] = 1;
		fighter_data[id][47] = Gmud.sPlayer.GetSkillLevel(1);

		// 基本兵刃技能
		int weapon_skill = fighter_data[id][32];
		if (weapon_skill >= 0 && weapon_skill < 54) {
			int weapon_type = Skill.skill_weapon_type[weapon_skill];
			int skill_id = Skill.weapon_to_base_skill[weapon_type];
			fighter_data[id][48] = skill_id;
			fighter_data[id][49] = Gmud.sPlayer.GetSkillLevel(skill_id);
		} else {
			fighter_data[id][48] = 255;
			fighter_data[id][49] = 0;
		}

		// 其他基本技能
		for (int i = 2; i < 8; i++) {
			int skill_id = Skill.base_skill[i];
			fighter_data[id][46 + i * 2] = skill_id;
			fighter_data[id][46 + i * 2 + 1] = Gmud.sPlayer
					.GetSkillLevel(skill_id);
		}

		fighter_data[id][62] = Gmud.sPlayer.GetPlayerLevel(); // global level
		fighter_data[id][63] = Gmud.sPlayer.mp_plus; // 法点
		fighter_data[id][64] = Gmud.sPlayer.exp; // exp
		fighter_data[id][65] = Gmud.sPlayer.unknow1; // ??
		CalcFighterLevel(id);
	}

	void CopyNPCData(int npc_id) {
		if (npc_id < 0 || npc_id > 179)
			return;
		NPC_name = NPC.NPC_names[npc_id]; // npc name

		// clean data
		for (int i = 0; i < 128; i++)
			fighter_data[1][i] = 0;

		// reset npc skill
		for (int i = 0; i < 8; i++) {
			fighter_data[1][30 + i * 2] = 255;
			fighter_data[1][30 + i * 2 + 1] = 0;
			fighter_data[1][46 + i * 2] = 255;
			fighter_data[1][46 + i * 2 + 1] = 0;
		}

		int[] tpdata = NPC.NPC_attrib[npc_id];
		fighter_data[1][66] = tpdata[1]; // class id
		if (fighter_data[1][66] > 8 || fighter_data[1][66] < 0)
			fighter_data[1][66] = 0;
		fighter_data[1][0] = tpdata[4]; // 加力
		fighter_data[1][1] = tpdata[11]; // hp
		fighter_data[1][2] = tpdata[12]; // hp-max
		fighter_data[1][3] = tpdata[15]; // hp-full
		fighter_data[1][4] = tpdata[13]; // fp
		fighter_data[1][5] = tpdata[14]; // fp-level
		fighter_data[1][6] = tpdata[13]; // mp=fp
		fighter_data[1][7] = tpdata[14]; // mp-level=fp
		fighter_data[1][8] = tpdata[5]; // 膂力
		fighter_data[1][9] = tpdata[6]; // 敏捷
		fighter_data[1][10] = tpdata[7]; // 悟性
		fighter_data[1][11] = tpdata[8]; // 根骨
		fighter_data[1][12] = tpdata[9]; // ? 命中
		fighter_data[1][13] = tpdata[10]; // ? 闪避
		fighter_data[1][63] = 0; // 没有法点 mp_plus
		fighter_data[1][64] = tpdata[3]; // 经验值
		fighter_data[1][65] = 0; // ?

		// npc equipt
		NPCEquip(npc_id);
		for (int i = 0; i < 16; i++)
			fighter_data[1][14 + i] = NPC_equip[i];

		// npc select skill
		NPCSetSkill(npc_id);
		for (int i = 0; i < 16; i++)
			fighter_data[1][30 + i] = NPC_select_skill[i];

		// 基本拳脚
		fighter_data[1][46] = 1;
		fighter_data[1][47] = NPC.GetNPCSkillLevel(npc_id, 1);

		// 兵刃的基本功
		int weapon_id = fighter_data[1][29];
		if (weapon_id > 0) {
			int weapon_type = Items.item_attribs[weapon_id][1];
			int base_skill_weapon = Skill.weapon_to_base_skill[weapon_type];
			int l2 = NPC.GetNPCSkillLevel(npc_id, base_skill_weapon);
			fighter_data[1][48] = base_skill_weapon;
			fighter_data[1][49] = l2;
		}

		// 其他类别的基本功
		for (int i = 2; i < 8; i++) {
			int skill_id = Skill.base_skill[i];
			fighter_data[1][46 + i * 2] = skill_id;
			fighter_data[1][46 + i * 2 + 1] = NPC.GetNPCSkillLevel(npc_id,
					skill_id);
		}
	}

	/**
	 * 读取 NPC 的装备，存入 {@link #NPC_equip}
	 * 
	 * @param npc_id
	 */
	private void NPCEquip(int npc_id) {
		if (npc_id < 0 || npc_id > 179)
			return;
		for (int i = 0; i < 16; i++)
			NPC_equip[i] = 0;

		for (int i = 0; i < 5; i++) {
			int item_id = NPC.NPC_item[npc_id][i];
			if (item_id == 0)
				continue;

			if (Items.item_attribs[item_id][0] == 2) {
				// 是武器
				NPC_equip[15] = item_id;
			} else if (Items.item_attribs[item_id][0] == 3) {
				// 装备
				int equip_type = Items.item_attribs[item_id][1];
				NPC_equip[equip_type] = item_id;
			}
		}
	}

	/**
	 * 加载 NPC 的技能列表，存入 {@link #NPC_select_skill}
	 * 
	 * @param npc_id
	 */
	private void NPCSetSkill(int npc_id) {
		if (npc_id < 0 || npc_id > 179)
			return;

		// 技能列表 [0]总数
		final int[] skills;
		if (npc_id == 179) {
			skills = task.bad_man_skill;
		} else {
			// 如果不是“恶人”，则需要读取 NPC 的技能列表
			NPCSKILLINFO nsk = new NPCSKILLINFO();
			NPC.GetNPCSkill(nsk, npc_id);
			skills = nsk.data;
		}

		int size = skills[0];
		if (size < 0) {
			// free(nsk.data);
			return;
		}

		// clear aa.e[]
		for (int k = 0; k < 8; k++) {
			NPC_select_skill[k * 2] = 255;
			NPC_select_skill[k * 2 + 1] = 0;
		}

		// 佩戴的武器ID
		int weapon_id = NPC_equip[15];

		for (int i = 0; i < size; i++) {
			int skill_id = skills[1 + i * 2];
			int skill_level = skills[1 + i * 2 + 1];

			// 不处理 基本功技能
			if (skill_id < 10)
				continue;

			int skill_type = Skill.skill_type[skill_id];
			if (skill_type < 2) {
				if (skill_type == 0) {
					// 拳脚技能
					NPC_select_skill[0] = skill_id;
					NPC_select_skill[1] = skill_level;
					if (weapon_id == 0) {
						// 设置“招架”技能
						NPC_select_skill[8] = skill_id;
						NPC_select_skill[9] = skill_level;
					}
					continue;
				}
				if (skill_type == 1
						&& weapon_id > 0
						&& Items.item_attribs[weapon_id][1] == Skill.skill_weapon_type[skill_id]) {
					// 兵刃技能，并且有装备武器及武器类型符合技能，则保存此技能
					NPC_select_skill[2] = skill_id;
					NPC_select_skill[3] = skill_level;
					NPC_select_skill[8] = skill_id;
					NPC_select_skill[9] = skill_level;
				}
			} else {
				NPC_select_skill[skill_type * 2] = skill_id;
				NPC_select_skill[skill_type * 2 + 1] = skill_level;
			}
		}
		// free(nsk.data);
	}

	/**
	 * 获取角色当前佩戴的武器ID
	 * 
	 * @param id
	 *            角色ID
	 * @return 武器ID
	 */
	private int GetWeaponID(int id) {
		return fighter_data[id][29];
	}

	/**
	 * 获取当前使用的技能等级
	 * 
	 * @param id
	 *            角色ID
	 * @param skill_id
	 *            技能ID
	 * @return 技能等级
	 */
	private int CalcSkillLevel(int id, int skill_id) {
		final int[] data = fighter_data[id];
		if (skill_id < 0 || skill_id > 94)
			return 0;

		if (skill_id < 10) {
			// 这是基本功技能
			for (int i = 0; i < 8; i++) {
				if (data[46 + i * 2] == skill_id) {
					return data[46 + i * 2 + 1];
				}
			}
		} else {
			// 这是已经选择的技能
			for (int i = 0; i < 8; i++) {
				if (data[30 + i * 2] == skill_id) {
					return data[30 + i * 2 + 1];
				}
			}
		}

		return 0;
	}

	/**
	 * 计算攻击等级 [0,255]，整5对齐
	 * 
	 * @param id
	 */
	void CalcFighterLevel(int id) {
		int weapon_id = GetWeaponID(id);
		int parry_type = 0;

		int level = 0;

		// 没有佩戴武器
		if (weapon_id == 0) {
			// 招架类型为拳脚
			parry_type = 0;

			// 拳脚
			level += CalcSkillLevel(id, fighter_data[id][30]) * 2;
			level += CalcSkillLevel(id, 1);
		} else {
			// 招架类型为兵刃
			parry_type = 1;

			// 兵刃
			int weapon_type = Items.item_attribs[weapon_id][1];
			int skill_weapon = fighter_data[id][32];
			if (skill_weapon != 255) {
				// 如果武器类别与兵刃技能类型匹配，才计算技能等级
				if (weapon_type == Skill.skill_weapon_type[skill_weapon])
					level += CalcSkillLevel(id, skill_weapon) * 2;
			}
			int base_skill_weapon = Skill.weapon_to_base_skill[weapon_type];
			level += CalcSkillLevel(id, base_skill_weapon);
		}

		// 招架
		int skill_parry = fighter_data[id][38];
		if (skill_parry != 255 && Skill.skill_type[skill_parry] == parry_type) {
			// 如果招架技能的类别相符，才计算
			level += CalcSkillLevel(id, skill_parry) * 2;
		}
		level += CalcSkillLevel(id, 8);

		// 内功
		level += CalcSkillLevel(id, fighter_data[id][36]) * 2
				+ CalcSkillLevel(id, 0);

		// 轻功
		level += CalcSkillLevel(id, fighter_data[id][34]) * 2
				+ CalcSkillLevel(id, 7);

		level = (level / 3) / 4;

		// 按5对齐（2舍3入）
		if (level % 5 > 2)
			level = (level / 5) * 5 + 5;
		if (level > 255)
			level = 255;
		if (level < 0)
			level = 0;
		fighter_data[id][62] = level;
	}

	/**
	 * 消耗 fp 值
	 * 
	 * @param id
	 *            角色ID
	 * @param fp_cost
	 *            用掉的 fp
	 */
	void CostFP(int id, int fp_cost) {
		int fp = stackB_find(id, 4);
		if (fp < 0)
			fp = fighter_data[id][4];
		fp -= fp_cost;
		if (fp < 0)
			fp = 0;
		stack_fighterdate_set(id, 4, fp);
	}

	/**
	 * 消耗 mp
	 * 
	 * @param id
	 *            角色ID
	 * @param mp_cost
	 *            魔法消耗值
	 */
	void CostMP(int id, int mp_cost) {
		int mp = stackB_find(id, 6);
		if (mp < 0)
			mp = fighter_data[id][6];
		mp -= mp_cost;
		if (mp < 0)
			mp = 0;
		stack_fighterdate_set(id, 6, mp);
	}

	// extern void GmudDelay(unsigned int);
	// extern Image* gmud_loadimage(int);

	void BattleMain() {
		uibattle.weapon_id[0] = uibattle.weapon_id[1] = 0;
		a_boolean_static = false;
		CopyData();
		bEscape = false;
		uibattle.menu_id = 0;
		active_id = CalcActOrder(); // 计算出招先后
		boolean flag = true;

		uibattle.player_img = Res.loadimage(Gmud.sPlayer.image_id * 6 + 74 + 4);
		uibattle.NPC_img = Res.loadimage(NPC_image_id);
		uibattle.hp_img = Res.loadimage(244);
		uibattle.fp_img = Res.loadimage(245);
		uibattle.mp_img = Res.loadimage(246);
		uibattle.hit_img = Res.loadimage(247);

		uibattle.weapon_id[0] = fighter_data[0][29];
		uibattle.weapon_id[1] = fighter_data[1][29];
		uibattle.hit_id = -1;
		uibattle.DrawMain();
		Video.VideoUpdate();
		while (true) {
			Input.ClearKeyStatus();
			if (!flag)
				break;
			PlayerActive();
			if (!(flag = BattleIsEnd()))
				break;
			NPCActive();
			flag = BattleIsEnd();
		}
		;
		BattleEnd(); // win

		// DeleteObject(uibattle.hit_img); //free data
		// DeleteObject(uibattle.mp_img);
		// DeleteObject(uibattle.fp_img);
		// DeleteObject(uibattle.hp_img);
		// DeleteObject(uibattle.NPC_img);
		// DeleteObject(uibattle.player_img);

		player_name = "";
		NPC_name = "";
		Gmud.GmudDelay(200);

	}

	/**
	 * 清除战斗队列
	 */
	void ClearActiveQueue() {
		stack_top = 0;
		for (int i = 0; i < 256; i++) {
			a_int_array1d_static[i] = -1;
			b_int_array1d_static[i] = -1;
		}
	}

	int CalaAvtiveSpeed(int i1, int j1, int k1) {
		if (k1 < 5)
			return fighter_data[i1][70 + j1 * 5 + k1];
		else
			return 0;
	}

	/**
	 * 设置出招的描述文本 ?? 见 {@link #e(int, int, int)}
	 * 
	 * @param attack_type
	 *            攻击类型
	 * @param desc_start
	 *            描述文本的id (Magic.txt)
	 * @param desc_count
	 *            连续描述的数量
	 */
	void b(int attack_type, int desc_start, int desc_count) {
		b_int_array1d_static[4] = attack_type;
		b_int_array1d_static[5] = desc_start;
		b_int_array1d_static[6] = desc_count;
	}

	/** 更新栈头信息 */
	void h() {
		final int[] stack = b_int_array1d_static;
		// 设置栈元素个数
		stack[0] = stack_top;
		// 计算 hash 值
		if (stack_top > 0)
			stack[1] = stack_hash_code(stack);
		else
			stack[1] = 0;
		// 记录当前角色ID
		stack[2] = active_id;
	}

	/**
	 * 描述招式的文本，如果 desc_count>1则表示此招有多个连续描述
	 * 
	 * @param attack_type
	 * @param desc_start
	 *            描述文本ID起点
	 * @param desc_count
	 *            连续文本的数量
	 */
	void e(int attack_type, int desc_start, int desc_count) {
		if (attack_type == -1 || desc_start == -1)
			return;
		int count = desc_count;
		if (count > 5 || count < 0)
			count = 1;
		for (int i = 0; i < count; i++) {
			uibattle.PhyAttack(attack_type, desc_start + i);
			Video.VideoUpdate();
			Gmud.GmudDelay(900);
		}
	}

	/** 处理战斗栈数据，并输出招式描述 */
	private void f() {
		final int[] stack = a_int_array1d_static;
		int hash_code = stack[1];
		if (stack_hash_code(stack) != hash_code)
			return;
		
		// 输出攻击方的出招
		e(stack[4], stack[5], stack[6]);

		// 从栈中取出战斗数据状态 fighter_data
		int stack_top = stack[0];
		if (stack_top > 256 - 32)
			stack_top = 256 - 32;
		for (int k1 = 0; k1 < stack_top; k1 += 3) {
			final int id = stack[k1 + 32];
			final int index = stack[k1 + 1 + 32];
			final int value = stack[k1 + 2 + 32];
			if (value == -123 && index >= 14 && index - 14 < 16) {
				// 解除装备或武器？ 物品索引范围是[14,29]
				int item_id = fighter_data[id][index];
				if (id == player_id) {
					if (index == 29)
						Gmud.sPlayer.UnEquipWeapon();
					else
						Gmud.sPlayer.UnEquipArmor(index - 14);
					int package_index = Gmud.sPlayer.ExistItem(item_id, 1);
					if (package_index >= 0)
						Gmud.sPlayer.DeleteOneItem(package_index);
				}
				// 移除此物品
				fighter_data[id][index] = 0;
			} else {
				fighter_data[id][index] = value;
			}
		}

		int j2 = active_id != 0 ? 0 : 1;
		if (stackA_find(j2, 1) >= 0) // 判断栈中的 hp 值
			uibattle.hit_id = j2;
		e(stack[7], stack[8], stack[9]);
		Video.VideoUpdate();
		e(stack[10], stack[11], stack[12]);
		Video.VideoUpdate();
		int l2 = active_id;
		for (int j3 = 0; j3 < 8; j3++) {
			for (int l3 = 0; l3 < 2; l3++) {
				active_id = l3;
				int j4 = CalaAvtiveSpeed(l3, j3, 4);
				if ((j4) <= 0 || j4 >= 20)
					continue;
				if (j3 == 7) {
					int k4 = CalaAvtiveSpeed(l3, j3, 2);
					if ((k4) >= 0)
						e(1, k4, 1);
					int i5 = CalaAvtiveSpeed(l3, j3, 0);
					fighter_data[l3][1] -= i5;
					if (fighter_data[l3][1] < 0)
						fighter_data[l3][1] = 0;
				}
				int l4;
				if ((--j4) == 0 && (l4 = CalaAvtiveSpeed(l3, j3, 3)) >= 0)
					e(1, l4, 1);
				d(l3, j3, 4, j4);
			}

		}

		active_id = l2;
		uibattle.DrawMain();
		Video.VideoUpdate();
	}

	void i() {
		h();
		for (int i = 0; i < 256; i++)
			a_int_array1d_static[i] = b_int_array1d_static[i];

		f();

		// 清空栈数据
		for (int i = 0; i < 256; i++)
			b_int_array1d_static[i] = -1;

		stack_top = 0;

		a(0, 0, 0, -1);
		uibattle.weapon_id[0] = fighter_data[0][29];
		uibattle.weapon_id[1] = fighter_data[1][29];
		active_id = active_id == 0 ? 1 : 0;
	}

	void a(int i1, int j1, int k1, int l1) {
		f_int_array1d_static[0] = i1;
		f_int_array1d_static[1] = j1;
		f_int_array1d_static[2] = k1;
		f_int_array1d_static[3] = l1;
	}

	void PlayerActive() {
		if (player_id == active_id) {
			ClearActiveQueue();
			int i1 = CalaAvtiveSpeed(active_id, 4, 4);
			if (i1 > 0 && i1 < 20)
				b(1, 99, 1);
			else {
				uibattle.menu_id = 0;
				uibattle.Main();
			}
			i();
		}
	}

	/**
	 * 计算出招先手。与等级差、经验差、敏捷差相关。
	 * 
	 * @return 是否先手
	 */
	boolean CalcAct() {
		final int id_rival = active_id != 0 ? 0 : 1;
		final int[] data = fighter_data[active_id];
		final int[] data_rival = fighter_data[id_rival];

		// 经验等级差
		int exp_gap = CalcExpLevel(data[64] / 100)
				- CalcExpLevel(data_rival[64] / 100);
		if (exp_gap > 50) {
			exp_gap = 50;
		} else if (exp_gap < -50) {
			exp_gap = -50;
		}

		// 敏捷差
		int agility_gap = data[9] - data_rival[9];

		// 等级差
		int level_gap = data[62] - data_rival[62];

		int gap = agility_gap + 50 + level_gap + exp_gap;
		if (gap < 1) {
			gap = 10;
		} else if (gap > 95) {
			gap = 95;
		}
		return util.RandomBool(gap);
	}

	void c(int i1, int j1, int k1) {
		b_int_array1d_static[7] = i1;
		b_int_array1d_static[8] = j1;
		b_int_array1d_static[9] = k1;
	}

	/** 记录出招后的结果，见 {@link #b(int, int, int)} */
	void d(int i1, int j1, int k1) {
		b_int_array1d_static[10] = i1;
		b_int_array1d_static[11] = j1;
		b_int_array1d_static[12] = k1;
	}

	/**
	 * 随机产生在技能等级内的行为描述，包括普通攻击和身法闪避
	 * 
	 * @param id
	 *            角色ID
	 * @param skill_id
	 *            技能ID
	 * @param skill_level
	 *            技能等级
	 * @return 描述表({@link Skill#a})的索引
	 */
	private int ib(int id, int skill_id, int skill_level) {
		for (int i = 0; i < 8; i++)
			c_int_array2d_static[id][i] = 0;

		// 描述类别
		int descGroup = -1;

		// 先遍历攻击的描述检索表
		for (int i = 0; i < 52; i += 2) {
			if (Skill.gongji_desc[i] == skill_id) {
				descGroup = Skill.gongji_desc[i + 1];
				break;
			}
		}

		if (descGroup == -1) {
			// 遍历 身法表
			for (int j3 = 0; j3 < 16; j3 += 2) {
				if (Skill.shenfa_desc[j3] == skill_id) {
					descGroup = Skill.shenfa_desc[j3 + 1];
					break;
				}
			}
		}

		// 没有找到
		if (descGroup == -1)
			return -1;

		// 类别的起点
		int descStart = Skill.e[descGroup];
		// 此类的总数
		int descSize = Skill.e[descGroup + 1] - descStart;
		int k3 = 0;
		do {
			if (k3 >= descSize)
				break;

			int i4 = Skill.a[descStart + k3][0];

			// 如果技能等级符合要求
			if (skill_level < i4)
				break;
			k3++;
		} while (true);

		// 随机取中间的一个
		int i5 = descStart + util.RandomInt(k3);
		for (int k5 = 0; k5 < 8; k5++)
			c_int_array2d_static[id][k5] = Skill.a[i5][k5];

		return i5;
	}

	/**
	 * 计算[命中]
	 * 
	 * @param id
	 * @return
	 */
	int CalcHit(int id) {
		int hit = fighter_data[id][12];

		int k1 = CalaAvtiveSpeed(id, 0, 4);
		if (k1 > 0 && k1 < 20)
			hit += CalaAvtiveSpeed(id, 0, 0);

		for (int i = 0; i < 16; i++) { // 遍历所有装备
			int item_id = fighter_data[id][14 + i];
			if (item_id > 0 && item_id < 92) {
				// 如果是“武器”或“装备”，则加上“命中率”
				if (Items.item_attribs[item_id][0] == 2
						|| Items.item_attribs[item_id][0] == 3) {
					int item_hit = Items.item_attribs[item_id][3];
					if (item_hit <= 127) {
						hit += item_hit;
					} else {
						hit += item_hit - 256;
					}
				}
			}
		}

		return hit;
	}

	/**
	 * 计算[闪避]
	 * 
	 * @param id
	 *            角色ID
	 * @return
	 */
	int CalcAvoid(int id) {
		int avoid = fighter_data[id][13];

		int k1 = CalaAvtiveSpeed(id, 3, 4);
		if (k1 > 0 && k1 < 20)
			avoid += CalaAvtiveSpeed(id, 3, 0);

		// 遍历武器装备
		for (int i = 0; i < 16; i++) {
			int item_id = fighter_data[id][14 + i];
			if (item_id > 0 && item_id < 92) {
				// 如果是武器或者装备
				if (Items.item_attribs[item_id][0] == 2
						|| Items.item_attribs[item_id][0] == 3) {
					int item_avoid = Items.item_attribs[item_id][4];
					if (item_avoid <= 127) {
						avoid += item_avoid;
					} else {
						avoid += item_avoid - 256;
					}
				}
			}
		}

		return avoid;
	}

	/**
	 * 计算装备的防御值
	 * 
	 * @param id
	 *            角色ID
	 * @return
	 */
	int CalcDefense(int id) {
		int defense = 0;

		// 遍历已佩戴的物品，找有防御的装备
		for (int i = 0; i < 16; i++) {
			int item_id = fighter_data[id][14 + i];
			if (item_id > 0 && item_id < 92
					&& Items.item_attribs[item_id][0] == 3) {
				int equip_defense = Items.item_attribs[item_id][2];
				if (equip_defense <= 127) {
					defense += equip_defense;
				} else {
					// 负数在 unsigned char 类型的下
					defense += equip_defense - 256;
				}
			}
		}
		return defense;
	}

	/**
	 * 计算防御？
	 * 
	 * @param id
	 *            角色ID
	 * @return
	 */
	int CalcDefenseB(int id) {
		int defense = 0;
		int k1 = CalaAvtiveSpeed(id, 6, 4);
		if (k1 > 0 && k1 < 20)
			defense = 0 + CalaAvtiveSpeed(id, 6, 0);

		defense += CalcDefense(id);

		return defense;
	}

	/**
	 * 计算攻击力
	 * 
	 * @param id
	 * @return
	 */
	int CalcAttack(int id) {
		final int[] data = fighter_data[id];

		// 臂力+fp/10
		int attack = (0 + data[8]) + data[4] / 10;
		if (attack < 10)
			attack = 10;

		int k1 = CalaAvtiveSpeed(id, 1, 4);
		if (k1 > 0 && k1 < 20)
			attack += CalaAvtiveSpeed(id, 1, 0);

		// 遍历装备(Equip)的属性(武器的攻击)
		for (int i = 0; i < 16; i++) {
			int equip_id = data[14 + i];
			if (equip_id > 0 && equip_id < 92
					&& Items.item_attribs[equip_id][0] == 2) {
				int weapon_attack = Items.item_attribs[equip_id][2];
				if (weapon_attack <= 127) {
					attack += weapon_attack;
				} else {
					attack += weapon_attack - 256;
				}
			}
		}

		// 如果装备了加攻击的自造武器
		if (Gmud.sPlayer.lasting_tasks[9] != 0
				&& Gmud.sPlayer.lasting_tasks[8] / 256 == 0 && data[29] == 77)
			attack += Items.item_attribs[77][4];
		return attack;
	}

	void d(int i1, int j1, int k1, int l1) {
		if (k1 < 5)
			fighter_data[i1][70 + j1 * 5 + k1] = l1;
	}

	/** 记录绝招的特殊效果 */
	boolean a(int id, int type, int k1, int l1, int i2, int j2, int k2) {
		final int[] data = fighter_data[id];
		final int point = 70 + type * 5;
		if (data[point + 4] < 0)
			data[point + 4] = 0;
		if (data[point + 4] != 0)
			return false;
		data[point] = k1;
		data[point + 1] = l1;
		data[point + 2] = i2;
		data[point + 3] = j2;
		data[point + 4] = k2;
		if (k2 >= 20)
			data[point + 4] = 19;

		// ? 已有记录了，还更新一次？
		for (int l2 = 0; l2 < 5; l2++)
			stack_fighterdate_set(id, point + l2, data[point + l2]);

		return true;
	}

	/**
	 * 吸气疗伤
	 * 
	 * @return 描述文本
	 */
	String Breath() {
		int id = active_id;
		int[] data = fighter_data[id];
		int j1 = Gmud.sPlayer.GetSkillLevel(0);
		if (Gmud.sPlayer.select_skills[3] == 255 || j1 == 0)
			return "你必须选择你要用的内功心法.";

		// hp >= hp-max
		if (data[1] >= data[2]) {
			data[1] = data[2];
			return "你现在体力充沛.";
		}
		if (Gmud.sPlayer.fp < 50 || Gmud.sPlayer.fp_level < 50)
			return "你的内力不够.";

		// 补２倍血，浪费一份？
		int fp_cost = (data[2] - data[1]) * 2;
		if (fp_cost > data[4])
			fp_cost = data[4];
		data[4] -= fp_cost;
		data[1] += fp_cost;
		if (data[1] > data[2])
			data[1] = data[2];

		// 设置新的 内力（fp）和 血量（hp）
		stack_fighterdate_set(id, 4, data[4]);
		stack_fighterdate_set(id, 1, data[1]);
		return "你深深吸了几口气,脸色看起来好多了.";
	}

	/**
	 * 在栈中根据 id 和 类型查找属性值
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	int stackA_find(int id, int type) {
		final int[] stack = a_int_array1d_static;
		for (int i = 0; i < stack_top; i += 3)
			if (stack[i + 32] == id && stack[i + 1 + 32] == type)
				return stack[i + 2 + 32];
		return -1;
	}

	/**
	 * 减去 fp 值，并记录在栈中
	 * 
	 * @param id
	 *            角色ID
	 * @param val
	 *            待减去的 fp 值
	 */
	public void stack_minus_fp(int id, int val) {
		// 查找栈中的记录
		int m = stackA_find(id, 4);

		// 如果没有关于 fp 的记录，就取当前的 fp 值
		if (m < 0)
			m = fighter_data[id][4];

		m -= val;
		if (m < 0)
			m = 0;

		// 新的 fp 值
		stack_fighterdate_set(id, 4, m);
	}

	/**
	 * 减去 mp 值，并记录在栈中
	 * 
	 * @param id
	 *            角色ID
	 * @param val
	 *            待减去的 mp 值
	 */
	public void stack_minus_mp(int id, int val) {
		int m = stackA_find(id, 6);
		if (m < 0)
			m = fighter_data[id][6];
		m -= val;
		if (m < 0)
			m = 0;
		stack_fighterdate_set(id, 6, m);
	}

	int stackB_find(int id, int type) {
		int[] stack = b_int_array1d_static;
		for (int t = 0; t < stack_top; t += 3)
			if (stack[t + 32] == id && stack[t + 1 + 32] == type)
				return stack[t + 2 + 32];

		return -1;
	}

	void b(int i1, int j1, int k1, int l1) {
		if (k1 <= 0) {
			d(0, 247, 1);
			return;
		}
		int i2 = 0;
		if ((i2 = stackB_find(l1, 1)) < 0)
			i2 = fighter_data[l1][1];
		if (k1 < 0)
			k1 = 0;
		if ((i2 -= k1) < 0)
			i2 = 0;
		stack_fighterdate_set(l1, 1, i2);
		int j2 = 0;
		if ((j2 = i2 / (fighter_data[l1][3] / j1)) >= j1)
			j2 = j1 - 1;
		if (j2 < 0)
			j2 = 0;
		j2 += i1;
		d(1, j2, 1);
	}

	/** i1武器类别 k1角色ID l1技能ID */
	void c(int i1, int j1, int k1, int l1) {
		if (j1 <= 0) {
			d(0, 247, 1);
			return;
		}
		int i2 = ia(i1, j1, fighter_data[k1][3], l1);
		int j2 = 0;
		int k2 = 0;
		if ((k2 = stackB_find(k1, 1)) < 0)
			k2 = fighter_data[k1][1];
		if ((j2 = 9 - k2 / (fighter_data[k1][3] / 10)) < 0)
			j2 = 0;
		if (j2 > 9)
			j2 = 9;
		if (i1 != 0) {
			if (j2 == 9 && util.RandomInt(2) == 1)
				j2 = 10;
			j2 += 47;
		} else {
			j2 += 37;
		}
		i2 = j2 * 256 + i2;
		d(2, i2, 1);
	}

	/** i1武器类别 k1血量上限hp-full l1技能ID */
	int ia(int i1, int j1, int k1, int l1) {
		j1 *= 1000;
		if ((k1 *= 10) == 0)
			k1 = 10;
		int i2;
		if ((i2 = j1 / k1) > 100)
			i2 = 100;
		if (i2 < 0)
			i2 = 0;
		int l3;
		int i4;
		switch (i1) {
		case 0: // 拳头
		{
			if (l1 == 44) { // 鹰抓功
				int j2;
				if ((j2 = i2 / 12) > 6)
					j2 = 6;
				return j2 + 12;
			}
			if (util.RandomBool(65))
				return 7;
			int k2;
			if ((k2 = i2 / 12) > 6)
				k2 = 6;
			int i3 = k2 != 6 ? k2 : 5;
			int k3;
			if ((k3 = util.RandomInt(4)) == 0)
				return i3;
			if (k3 == 1)
				return 6 + i3;
			if (k3 == 2)
				return 12 + k2;
			if (k3 == 3)
				return 19 + k2;
			else
				return 7;
		}
		case 1: // 刀
		{
			int l2 = i2 / 25;
			return 26 + l2;
		}
		case 6: // 6剑
		{
			int j3 = i2 / 25;
			if (util.RandomBool(85))
				return 30 + j3;
			else
				return 26 + j3;
		}
		case 7: // 7杖
			return l3 = i2 / 16;

		case 9: // 9鞭
			return i4 = i2 / 16;

		case 2: // '\002'
		case 3: // '\003'
		case 4: // '\004'
		case 5: // '\005'
		case 8: // '\b'
		default:
			return 7;
		}
	}

	/***
	 * 重新复制玩家的装备表到战斗数据表中，用于换装时处理
	 */
	void CopyPlayerEquips() {
		// 设置战斗数据的hp到栈
		stack_fighterdate_set(player_id, 2, fighter_data[player_id][2]);
		// 恢复玩家装备到战斗数据中
		for (int i1 = 0; i1 < 16; i1++)
			if (Gmud.sPlayer.equips[i1] != fighter_data[player_id][14 + i1]) {
				fighter_data[player_id][14 + i1] = Gmud.sPlayer.equips[i1];
				stack_fighterdate_set(player_id, 14 + i1,
						Gmud.sPlayer.equips[i1]);
			}
	}

	void CopyPlayerSelectSkills() {
		for (int i1 = 0; i1 < 8; i1++) {
			if (Gmud.sPlayer.select_skills[i1] == fighter_data[player_id][30 + i1 * 2])
				continue;
			fighter_data[player_id][30 + i1 * 2] = Gmud.sPlayer.select_skills[i1];
			fighter_data[player_id][30 + i1 * 2 + 1] = Gmud.sPlayer
					.GetSkillLevel(Gmud.sPlayer.select_skills[i1]);
			stack_fighterdate_set(player_id, 30 + i1 * 2,
					Gmud.sPlayer.select_skills[i1]);
			stack_fighterdate_set(player_id, 30 + i1 * 2 + 1,
					fighter_data[player_id][30 + i1 * 2 + 1]);
			if (i1 == 1) {
				int j1 = player_id;
				int k1;
				if ((k1 = fighter_data[j1][32]) >= 0 && k1 < 36) {
					int l1 = Skill.skill_weapon_type[k1];
					int i2 = Skill.weapon_to_base_skill[l1];
					fighter_data[j1][48] = Skill.weapon_to_base_skill[l1];
					fighter_data[j1][49] = Gmud.sPlayer.GetSkillLevel(i2);
				} else {
					fighter_data[j1][48] = 255;
					fighter_data[j1][49] = 0;
				}
				stack_fighterdate_set(player_id, 48, fighter_data[j1][48]);
				stack_fighterdate_set(player_id, 49, fighter_data[j1][49]);
			}
			CalcFighterLevel(player_id);
			stack_fighterdate_set(player_id, 62, fighter_data[player_id][62]);
		}
	}

	void NPCActive() {
		if (player_id != active_id) {
			int i1 = CalaAvtiveSpeed(active_id, 4, 4);
			if (i1 > 0 && i1 < 20) {
				// "你现在呆若木鸡！"
				b(1, 99, 1);
			} else {
				Magic.Effect(active_id);
				int k1 = CountMagicEffect(active_id); // 使用绝招的概率为 20
				boolean flag = util.RandomBool(20);
				if (k1 > 0 && flag) {// 随机用一个绝招
					int j2 = a_int_array2d_static[active_id][util.RandomInt(k1)];
					String s = Magic.UseMagic(j2);
					if (s.length() > 0)
						PhyAttack(false);
				} else {
					PhyAttack(false);
				}
			}
			i();
		}
	}

	/**
	 * 计算 hash 值，用于校验栈有无变化
	 * 
	 * @param stack
	 *            栈,[32,~)为元素值，[0]为栈顶（元素个数）,
	 * @return
	 */
	int stack_hash_code(int stack[]) {
		int hash = 0;
		int top = stack[0];
		for (int t = 32; t < 32 + top; t++)
			hash += 0xef ^ stack[t];
		return hash;
	}

	/** 计算出招先后 */
	int CalcActOrder() {
		return CalcAct() ? 0 : 1;
	}

	int CalcExpLevel(int exp) {
		int level = 0;
		if (exp < 1)
			return 0;
		for (int m = 0x40000000; m > 0; m >>= 2)
			if (exp >= level + m) {
				exp -= level + m;
				level = (level >>= 1) + m;
			} else {
				level >>= 1;
			}

		return level;
	}

	// extern BOOL WriteSave();

	void BattleEnd() {
		int id = NPC_id;

		// 记录缴获的物品（武器+装备）
		short[] items = NPC_item;
		int top = 0;

		for (int i = 0; i < 5; i++)
			items[i] = 0;

		for (int j2 = 0; j2 < 5; j2++) {
			int item_id = NPC.NPC_item[id][j2];
			if (item_id == 0)
				continue;

			if (Items.item_attribs[item_id][0] == 2) {
				// 如果是武器
				if (fighter_data[1][29] != item_id) {
					// 武器不是开战前的，不处理
					continue;
				}
			} else if (Items.item_attribs[item_id][0] == 3) {
				// 如果是装备，比较同类型的装备
				int type = Items.item_attribs[item_id][1];
				if (fighter_data[1][14 + type] != item_id) {
					// 装备不是开战前的，不处理
					continue;
				}
			}
			items[top++] = (short) item_id;
		}

		int money = NPC.NPC_attrib[id][16];

		// 如果不是“切磋”且不是“逃跑”，刷新战斗结果
		if (is_try == 0 && !bEscape) {
			// player Hp < 0
			if (fighter_data[0][1] <= 0) {
				Gmud.sPlayer.PlayerDead(); // player dead
				Gmud.WriteSave(); // save
				UI.DrawDead();
				Gmud.exit();
			} else {
				TaskEnd(id); // 检查特殊人物
				UI.BattleWin(money, items); // draw win

				// get item
				for (int i = 0; i < 5; i++)
					Gmud.sPlayer.GainOneItem(items[i]);

				Gmud.sPlayer.money += money; // +money
				Map.SetNPCDead(id, (byte) 1); // set npc dead
			}
		}
		RollBackData();
	}

	void TaskEnd(int i1) {
		if (i1 == 179) // 恶人
		{
			/*
			 * //delete map data int j1 = j.a_z_static.a; int k1 =
			 * x.a[j1].length - 1; x.a[j1][k1] = p.b[6]; ac.a[j1][k1] = 255;
			 * //delete event
			 */
			task.bad_man_mapid = -1;
			--Gmud.sMap.map_event_data.size;
			--Gmud.sMap.map_image_data.size;
			task.temp_tasks_data[0] = 0;
			int l3 = task.temp_tasks_data[3];
			int l4 = task.temp_tasks_data[4];
			Gmud.sPlayer.exp += l3;
			Gmud.sPlayer.potential += l4;
			// wstring s1("你被奖励了：\n");
			// wchar_t num[10];
			// s1 += _itow(l3, num, 10);
			// s1 += " 点实战经验\n";
			// s1 += _itow(l4, num, 10);
			// s1 += " 点潜能\n ";
			String s1 = String.format("你被奖励了：\n%d 点实战经验\n%d 点潜能\n ", l3, l4);
			UI.DialogBx(s1, 14, 12);
			return;
		}
		if (task.temp_tasks_data[19] == 1 && task.temp_tasks_data[18] == i1) // 平一指任务
		{
			task.temp_tasks_data[20] = 1;
			return;
		}
		if (i1 >= 148 && i1 <= 178 && i1 != 156 && i1 != 157) // 坛主
		{
			String s = "获得了：\n";
			switch (i1) {
			case 148: {
				int l1;
				if ((l1 = Gmud.sPlayer.ExistItem(79, 1)) >= 0)
					Gmud.sPlayer.LoseItem(l1, 1);
				Gmud.sPlayer.exp += 50000;
				s += "50000点经验\n";
				Gmud.sPlayer.lasting_tasks[1] = 1;
				UI.DialogBx(s, 14, 12);
				return;
			}
			case 155: {
				int i2;
				if ((i2 = Gmud.sPlayer.ExistItem(80, 1)) >= 0)
					Gmud.sPlayer.LoseItem(i2, 1);
				Gmud.sPlayer.exp += 50000;
				Gmud.sPlayer.money += 50000;
				s += "50000点经验\n50000金钱";
				Gmud.sPlayer.lasting_tasks[1] = 2;
				UI.DialogBx(s, 14, 12);
				return;
			}
			case 150: {
				int j2;
				if ((j2 = Gmud.sPlayer.ExistItem(81, 1)) >= 0)
					Gmud.sPlayer.LoseItem(j2, 1);
				Gmud.sPlayer.fp_level += 150;
				s += "150点内力修为\n";
				Gmud.sPlayer.lasting_tasks[1] = 3;
				UI.DialogBx(s, 14, 12);
				return;
			}
			case 154: {
				int k2;
				if ((k2 = Gmud.sPlayer.ExistItem(82, 1)) >= 0)
					Gmud.sPlayer.LoseItem(k2, 1);
				Gmud.sPlayer.fp_level += 200;
				Gmud.sPlayer.money += 60000;
				s += "200点内力修为\n60000金钱\n";
				Gmud.sPlayer.lasting_tasks[1] = 4;
				UI.DialogBx(s, 14, 12);
				return;
			}
			case 151: {
				int l2;
				if ((l2 = Gmud.sPlayer.ExistItem(83, 1)) >= 0)
					Gmud.sPlayer.LoseItem(l2, 1);
				Gmud.sPlayer.fp_level += 200;
				Gmud.sPlayer.exp += 60000;
				s += "200点内力修为\n60000点经验\n";
				Gmud.sPlayer.lasting_tasks[1] = 5;
				UI.DialogBx(s, 14, 12);
				return;
			}
			case 153: {
				int i3;
				if ((i3 = Gmud.sPlayer.ExistItem(84, 1)) >= 0)
					Gmud.sPlayer.LoseItem(i3, 1);
				for (int i4 = 0; i4 < 32; i4++)
					if (Gmud.sPlayer.skills[i4][0] != 255)
						Gmud.sPlayer.skills[i4][1] += 3;

				Gmud.sPlayer.money += 60000;
				s += "武功等级上升3级\n60000金钱\n";
				Gmud.sPlayer.lasting_tasks[1] = 6;
				UI.DialogBx(s, 14, 12);
				return;
			}
			case 152: {
				int j3;
				if ((j3 = Gmud.sPlayer.ExistItem(85, 1)) >= 0)
					Gmud.sPlayer.LoseItem(j3, 1);
				for (int j4 = 0; j4 < 32; j4++)
					if (Gmud.sPlayer.skills[j4][0] != 255)
						Gmud.sPlayer.skills[j4][1] += 3;

				Gmud.sPlayer.exp += 60000;
				s += "武功等级上升3级\n60000点经验\n";
				Gmud.sPlayer.lasting_tasks[1] = 7;
				UI.DialogBx(s, 14, 12);
				return;
			}
			case 149: {
				int k3;
				if ((k3 = Gmud.sPlayer.ExistItem(86, 1)) >= 0)
					Gmud.sPlayer.LoseItem(k3, 1);
				for (int k4 = 0; k4 < 32; k4++)
					if (Gmud.sPlayer.skills[k4][0] != 255)
						Gmud.sPlayer.skills[k4][1] += 3;

				Gmud.sPlayer.fp_level += 200;
				s += "武功等级上升3级\n200点内力修为\n";
				Gmud.sPlayer.lasting_tasks[1] = 8;
				UI.DialogBx(s, 14, 12);
				return;
			}
			}
		}
	}

	void RollBackData() {
		int i1 = player_id;
		int j1 = (i1) != 0 ? 0 : 1;
		for (int k1 = 0; k1 < 16; k1++)
			if (fighter_data[i1][14 + k1] != Gmud.sPlayer.equips[k1])
				Gmud.sPlayer.LoseOneItem(fighter_data[i1][14 + k1]);
		int l1 = NPC_id;
		for (int i2 = 0; i2 < 5; i2++) {
			int j2;
			if ((j2 = NPC.NPC_item[l1][i2]) == 0)
				continue;
			if (Items.item_attribs[j2][0] == 2) {
				// 如果武器不同，是临时的？需要去掉？
				if (fighter_data[1][29] != j2)
					NPC.NPC_item[l1][i2] = 0;
				continue;
			}
			if (Items.item_attribs[j2][0] != 3)
				continue;
			int k2 = Items.item_attribs[j2][1];
			if (fighter_data[1][14 + k2] != j2)
				NPC.NPC_item[l1][i2] = 0;
		}
		NPC.NPC_attrib[l1][11] = fighter_data[j1][1];
		NPC.NPC_attrib[l1][12] = fighter_data[j1][2];

		// TODO: FP 与 MP 共用了 [13]/[14]，暂时只能更新一个，取较为常用的 FP
		// if (fighter_data[j1][6] > 0)
		// NPC.NPC_attrib[l1][13] = fighter_data[j1][6];
		// else
		NPC.NPC_attrib[l1][13] = fighter_data[j1][4];

		Gmud.sPlayer.hp = fighter_data[i1][1];
		Gmud.sPlayer.hp_max = fighter_data[i1][2];
		Gmud.sPlayer.fp = fighter_data[i1][4];
		Gmud.sPlayer.mp = fighter_data[i1][6];
	}
}
