package cn.fmsoft.lnx.gmud.simple.core;

public class Skill {

	// size: 54
	static final String skill_name[] = new String[] {
	/* 0 */"基本内功", "基本拳脚", "基本剑术", "基本刀法", "基本法术",
	/* 5 */"基本杖法", "基本鞭法", "基本轻功", "基本招架", "读书写字",
	/* 10 */"驻颜术", "八卦刀", "八卦游身掌", "八阵八卦掌", "混元一气功",
	/* 15 */"游龙身法", "飞蝶身法", "花团鞭法", "柳叶刀法", "一剪梅华手",
	/* 20 */"三花聚顶", "鹤翔身法", "红莲教义", "乱披风杖法", "太祖长拳",
	/* 25 */"普天同济", "扶桑忍术", "无法拳", "无影遁形", "川枫一刀流",
	/* 30 */"太极剑", "太极拳", "太极神功", "万流归一", "玄虚刀法",
	/* 35 */"踏雪无痕", "雪山内功", "入门十三式", "雪山剑法", "雪影擒拿手",
	/* 40 */"五禽戏", "鳄灵刀", "赤尻连拳", "猛虎拳", "鹰抓功",
	/* 45 */"龙象般若功", "灵符身法", "天师掌法", "谷衣心法", "天师正道",
	/* 50 */"万鸦咒", "玄冰咒", "五雷咒", "灵通心诀" };
	
	
	// size: 54
	/**
	 * 技能类别，对应到技能菜单列表，顺序依次为: <br/>
	 * 0拳脚 1兵刃 2轻功 3内功 4招架 5知识 6法术
	 */
	static final int skill_type[] = new int[]{
		3, 0, 1, 1, 6, 1, 1, 2, 4, 5, 
		5, 1, 0, 0, 3, 2, 2, 1, 1, 0, 
		3, 2, 5, 1, 0, 3, 3, 0, 2, 1, 
		1, 0, 3, 2, 1, 2, 3, 1, 1, 0, 
		2, 1, 0, 0, 0, 3, 2, 0, 3, 5, 
		6, 6, 6, 5
	};
	
	/** (size: 54) (兵刃)技能所对应的武器类别 */
	static final int skill_weapon_type[] = new int[] {
		0, 0, 6, 1, 0, 7, 9, 0, 0, 0, 
		0, 1, 0, 0, 0, 0, 0, 9, 1, 0, 
		0, 0, 0, 7, 0, 0, 0, 0, 0, 1, 
		6, 0, 0, 0, 1, 0, 0, 6, 6, 0, 
		0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0
	};
	
	// size: 254
	static final int attack_desc[] = new int[] {
		0, 110, 182, 263, 348, 417, 489, 568, 637, 720, 
		800, 875, 956, 1039, 1114, 1198, 1261, 1341, 1421, 1513, 
		1609, 1713, 1807, 1910, 1975, 2034, 2114, 2191, 2274, 2339, 
		2425, 2481, 2546, 2624, 2689, 2751, 2831, 2911, 2985, 3070, 
		3153, 3248, 3321, 3403, 3482, 3547, 3620, 3682, 3759, 3806, 
		3871, 3933, 4010, 4081, 4137, 4229, 4306, 4365, 4424, 4499, 
		4588, 4671, 4766, 4825, 4892, 4974, 5043, 5108, 5188, 5270, 
		5344, 5414, 5479, 5545, 5630, 5696, 5759, 5822, 5901, 5957, 
		6056, 6140, 6190, 6250, 6315, 6380, 6457, 6550, 6622, 6707, 
		6794, 6878, 6965, 7049, 7151, 7265, 7361, 7460, 7571, 7652, 
		7857, 7964, 8064, 8164, 8270, 8367, 8461, 8555, 8655, 8755, 
		8841, 8951, 9054, 9143, 9257, 9358, 9459, 9553, 9655, 9759, 
		9845, 9946, 10066, 10249, 10339, 10447, 10548, 10647, 10755, 10860, 
		10961, 11066, 11165, 11275, 11392, 11503, 11611, 11718, 11817, 11924, 
		12038, 12149, 12242, 12334, 12409, 12477, 12564, 12659, 12756, 12850, 
		12932, 13040, 13129, 13191, 13268, 13321, 13395, 13457, 13525, 13591, 
		13660, 13739, 13819, 13906, 14001, 14097, 14166, 14246, 14325, 14440, 
		14543, 14643, 14748, 14825, 14919, 15003, 15102, 15179, 15291, 15375, 
		15470, 15534, 15590, 15668, 15739, 15797, 15877, 15939, 16014, 16090, 
		16165, 16246, 16327, 16399, 16476, 16554, 16638, 16730, 16817, 16908, 
		16995, 17082, 17163, 17259, 17327, 17408, 17482, 17551, 17628, 17693, 
		17755, 17851, 17934, 18021, 18105, 18180, 18260, 18355, 18411, 18470, 
		18529, 18585, 18659, 18719, 18778, 18838, 18921, 18984, 19053, 19088, 
		19120, 19143, 19175, 19195, 19235, 19260, 19288, 19313, 19341, 19366, 
		19427, 19470, 19507, 19541, 19573, 19604, 19642, 19689, 19718, 19765, 
		19809, 19844, 19888, 19911
	};
	
	// size: 59
	static final int hit_desc[] = new int[] {
		0, 47, 94, 135, 179, 223, 273, 311, 367, 408, 
		461, 514, 564, 605, 652, 684, 725, 772, 831, 884, 
		919, 951, 992, 1033, 1074, 1115, 1162, 1194, 1238, 1285, 
		1338, 1364, 1390, 1429, 1476, 1499, 1522, 1578, 1617, 1659, 
		1683, 1719, 1761, 1806, 1845, 1875, 1911, 1938, 1977, 2019, 
		2049, 2085, 2127, 2172, 2211, 2253, 2283, 2319, 2355
	};
	
	// size: 17
	/** 与兵刃类型对应的基本功，如基本剑术等 */
	static final int weapon_to_base_skill[] = new int[] {
		255, 3, 255, 255, 255, 255, 2, 5, 255, 6, 
		255, 255, 255, 255, 255, 255, 255
	};
	
	/** (size: 8) 技能类别[0,8)对应的基本功技能ID */
	static final int base_skill[] = new int[] {
		1, 255, 7, 0, 8, 9, 4, 255
	};

	/**
	 *  (size: 253, 9),对应 {@link #e} ([0]技能等级要求, [4] [8]不用) 
	 */
	static final int a[][] = new int[][] {
			{0, 1, 0, 0, 0, 0, 40, 0, 255}, 
			{19, 1, 0, 0, 0, 0, 50, 0, 255}, 
			{29, 1, 0, 5, 0, 0, 60, 0, 255}, 
			{39, 1, 0, 0, 0, 0, 75, 0, 255}, 
			{49, 1, 0, 5, 0, 0, 80, 0, 255}, 
			{59, 1, 0, 10, 0, 0, 90, 0, 255}, 
			{69, 2, 0, 0, 0, 0, 50, 50, 255}, 
			{79, 2, 0, 0, 0, 0, 120, 0, 255}, 
			{0, 4, 0, 5, 0, 0, 0, 80, 255}, 
			{0, 4, 0, 0, 0, 0, 0, 60, 255}, 
			{0, 4, 0, 0, 0, 0, 0, 40, 255}, 
			{0, 0, 0, 15, 10, 10, 0, 100, 255}, 
			{0, 4, 0, 0, 0, 0, 0, 70, 255}, 
			{0, 4, 0, 10, 0, 0, 0, 50, 255}, 
			{0, 4, 0, 5, 0, 0, 0, 80, 255}, 
			{19, 4, 0, 0, 0, 0, 0, 90, 255}, 
			{29, 4, 0, 0, 0, 0, 0, 100, 255}, 
			{39, 0, 0, 5, 0, 0, 0, 110, 255}, 
			{49, 4, 0, 0, 0, 0, 0, 120, 255}, 
			{59, 4, 0, 10, 0, 0, 0, 100, 255}, 
			{69, 4, 0, 5, 0, 0, 0, 150, 255}, 
			{79, 4, 0, 5, 0, 0, 0, 180, 255}, 
			{89, 4, 0, 10, 0, 0, 0, 220, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 3, 0, 0, 0, 0, 40, 0, 255}, 
			{20, 3, 0, 0, 0, 0, 50, 0, 255}, 
			{40, 2, 0, 0, 0, 0, 60, 0, 255}, 
			{50, 3, 0, 5, 0, 0, 60, 0, 255}, 
			{60, 3, 0, 0, 0, 0, 70, 0, 255}, 
			{70, 3, 0, 10, 0, 0, 90, 0, 255}, 
			{80, 2, 0, 0, 0, 0, 100, 0, 255}, 
			{0, 1, 0, 0, 0, 0, 20, 0, 255}, 
			{20, 1, 0, 0, 0, 0, 40, 0, 255}, 
			{40, 2, 0, 5, 0, 0, 40, 0, 255}, 
			{50, 1, 0, 0, 0, 0, 50, 0, 255}, 
			{60, 1, 0, 0, 0, 0, 60, 0, 255}, 
			{70, 1, 0, 0, 0, 0, 65, 0, 255}, 
			{80, 1, 0, 0, 0, 0, 70, 0, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 80, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 60, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 5, 0, 0, 0, 0, 0, 100, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 90, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 150, 255}, 
			{0, 5, 0, 0, 0, 0, 0, 60, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 120, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 3, 0, 0, 0, 0, 40, 40, 255}, 
			{19, 3, 0, 0, 0, 0, 40, 80, 255}, 
			{29, 3, 0, 0, 0, 0, 60, 60, 255}, 
			{39, 3, 0, 0, 0, 0, 80, 40, 255}, 
			{49, 3, 0, 0, 0, 0, 80, 60, 255}, 
			{59, 3, 0, 5, 246, 0, 120, 20, 255}, 
			{69, 2, 0, 10, 0, 0, 80, 0, 255}, 
			{79, 3, 0, 5, 0, 241, 130, 0, 255}, 
			{0, 3, 0, 5, 0, 0, 0, 80, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 60, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 40, 255}, 
			{0, 5, 0, 15, 10, 10, 0, 100, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 70, 255}, 
			{0, 0, 0, 10, 0, 0, 0, 50, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 40, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 40, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 30, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 20, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 60, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 1, 0, 0, 0, 0, 60, 40, 255}, 
			{0, 1, 0, 0, 0, 0, 80, 50, 255}, 
			{0, 1, 0, 0, 0, 0, 90, 60, 255}, 
			{0, 1, 0, 0, 0, 0, 40, 40, 255}, 
			{20, 1, 0, 251, 0, 0, 50, 50, 255}, 
			{30, 1, 0, 0, 0, 0, 50, 60, 255}, 
			{50, 1, 0, 246, 0, 0, 65, 0, 255}, 
			{60, 1, 0, 251, 0, 0, 80, 0, 255}, 
			{70, 2, 0, 5, 0, 0, 50, 0, 255}, 
			{80, 1, 0, 0, 0, 0, 90, 0, 255}, 
			{90, 1, 0, 251, 0, 0, 100, 0, 255}, 
			{0, 2, 0, 5, 0, 0, 150, 0, 255}, 
			{0, 2, 0, 0, 0, 0, 180, 0, 255}, 
			{0, 2, 0, 251, 0, 0, 220, 0, 255}, 
			{0, 2, 0, 251, 0, 0, 20, 0, 255}, 
			{9, 1, 0, 246, 5, 0, 20, 0, 255}, 
			{19, 2, 0, 251, 5, 5, 20, 0, 255}, 
			{29, 1, 0, 0, 0, 10, 30, 0, 255}, 
			{39, 1, 0, 0, 25, 10, 30, 0, 255}, 
			{49, 2, 0, 0, 10, 10, 30, 20, 255}, 
			{59, 2, 0, 0, 0, 15, 40, 0, 255}, 
			{79, 2, 0, 0, 251, 0, 50, 0, 255}, 
			{89, 2, 0, 5, 246, 246, 60, 0, 255}, 
			{99, 1, 0, 5, 0, 0, 50, 0, 255}, 
			{109, 2, 0, 0, 0, 0, 60, 30, 255}, 
			{119, 1, 0, 0, 0, 5, 70, 50, 255}, 
			{129, 1, 0, 0, 5, 5, 70, 80, 255}, 
			{139, 2, 0, 0, 15, 0, 85, 40, 255}, 
			{149, 2, 0, 5, 251, 251, 95, 50, 255}, 
			{159, 2, 0, 0, 251, 251, 115, 0, 255}, 
			{169, 2, 0, 10, 251, 251, 115, 20, 255}, 
			{179, 2, 0, 0, 0, 15, 95, 50, 255}, 
			{189, 2, 0, 5, 10, 0, 125, 80, 255}, 
			{199, 2, 0, 15, 241, 241, 155, 100, 255}, 
			{0, 4, 0, 0, 246, 246, 0, 550, 255}, 
			{0, 4, 0, 0, 246, 246, 0, 550, 255}, 
			{0, 4, 0, 0, 5, 5, 0, 20, 255}, 
			{10, 2, 0, 0, 5, 5, 0, 30, 255}, 
			{20, 2, 0, 0, 5, 5, 0, 30, 255}, 
			{40, 4, 0, 0, 10, 10, 0, 40, 255}, 
			{40, 4, 0, 0, 5, 5, 0, 40, 255}, 
			{60, 4, 0, 0, 15, 15, 0, 50, 255}, 
			{60, 4, 0, 0, 15, 15, 0, 50, 255}, 
			{70, 4, 0, 0, 5, 5, 0, 60, 255}, 
			{80, 4, 0, 0, 10, 10, 0, 60, 255}, 
			{90, 4, 0, 0, 0, 0, 0, 70, 255}, 
			{100, 4, 0, 0, 10, 10, 0, 80, 255}, 
			{110, 4, 0, 0, 0, 0, 0, 130, 255}, 
			{120, 4, 0, 0, 5, 5, 0, 180, 255}, 
			{130, 4, 0, 0, 5, 5, 0, 220, 255}, 
			{140, 4, 0, 0, 10, 10, 0, 250, 255}, 
			{150, 4, 0, 0, 5, 5, 0, 280, 255}, 
			{160, 4, 0, 0, 15, 15, 0, 350, 255}, 
			{170, 4, 0, 0, 10, 10, 0, 380, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 1, 0, 0, 0, 0, 30, 0, 255}, 
			{0, 1, 0, 0, 0, 0, 40, 0, 255}, 
			{0, 1, 0, 5, 0, 0, 30, 0, 255}, 
			{0, 1, 0, 0, 0, 0, 45, 0, 255}, 
			{0, 1, 0, 5, 0, 0, 50, 0, 255}, 
			{0, 1, 0, 10, 0, 0, 40, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 2, 0, 0, 0, 0, 20, 0, 255}, 
			{0, 2, 0, 0, 0, 0, 20, 0, 255}, 
			{0, 1, 0, 0, 0, 0, 25, 0, 255}, 
			{0, 1, 0, 0, 0, 0, 20, 0, 255}, 
			{0, 2, 0, 0, 0, 0, 30, 0, 255}, 
			{30, 2, 0, 2, 0, 0, 40, 0, 255}, 
			{40, 2, 0, 5, 0, 0, 45, 0, 255}, 
			{60, 1, 0, 3, 0, 0, 50, 0, 255}, 
			{70, 2, 0, 5, 0, 0, 50, 0, 255}, 
			{80, 1, 0, 7, 0, 0, 60, 0, 255}, 
			{95, 2, 0, 7, 0, 0, 60, 0, 255}, 
			{90, 2, 0, 5, 0, 0, 80, 0, 255}, 
			{110, 2, 0, 8, 0, 0, 80, 0, 255}, 
			{130, 2, 0, 10, 0, 0, 90, 0, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 110, 255}, 
			{19, 3, 0, 0, 0, 0, 0, 120, 255}, 
			{39, 3, 0, 5, 0, 0, 0, 150, 255}, 
			{60, 1, 0, 10, 0, 0, 0, 150, 255}, 
			{70, 3, 0, 5, 0, 0, 0, 180, 255}, 
			{85, 3, 0, 5, 251, 0, 0, 180, 255}, 
			{95, 3, 0, 0, 0, 0, 0, 200, 255}, 
			{110, 3, 0, 10, 251, 251, 0, 250, 255}, 
			{0, 1, 0, 0, 0, 0, 40, 0, 255}, 
			{0, 1, 0, 0, 0, 0, 50, 0, 255}, 
			{0, 1, 0, 5, 0, 0, 60, 0, 255}, 
			{0, 1, 0, 5, 0, 0, 40, 0, 255}, 
			{0, 1, 0, 5, 0, 0, 50, 0, 255}, 
			{0, 1, 0, 10, 0, 0, 80, 0, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 40, 255}, 
			{0, 5, 0, 5, 5, 5, 0, 40, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 50, 255}, 
			{0, 3, 0, 0, 10, 10, 0, 40, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 90, 255}, 
			{0, 3, 0, 10, 0, 0, 0, 60, 255}, 
			{0, 3, 0, 5, 251, 251, 0, 100, 255}, 
			{19, 3, 0, 5, 0, 0, 0, 120, 255}, 
			{39, 4, 0, 8, 251, 251, 0, 120, 255}, 
			{49, 1, 0, 8, 251, 251, 0, 150, 255}, 
			{69, 3, 0, 5, 246, 246, 0, 180, 255}, 
			{79, 3, 0, 10, 251, 251, 0, 200, 255}, 
			{89, 3, 0, 10, 246, 246, 0, 240, 255}, 
			{99, 4, 0, 2, 251, 251, 0, 240, 255}, 
			{109, 4, 0, 5, 251, 251, 0, 260, 255}, 
			{119, 3, 0, 10, 246, 246, 0, 280, 255}, 
			{129, 3, 0, 15, 244, 244, 0, 320, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 5, 0, 5, 0, 0, 0, 50, 255}, 
			{19, 5, 0, 5, 0, 0, 0, 60, 255}, 
			{39, 5, 0, 0, 0, 0, 0, 80, 255}, 
			{49, 5, 0, 5, 0, 0, 0, 90, 255}, 
			{59, 5, 0, 8, 0, 0, 0, 100, 255}, 
			{69, 3, 0, 8, 0, 0, 0, 120, 255}, 
			{79, 5, 0, 5, 0, 0, 0, 130, 255}, 
			{89, 5, 0, 10, 0, 0, 0, 150, 255}, 
			{99, 4, 0, 15, 0, 0, 0, 200, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 40, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 30, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 40, 255}, 
			{0, 5, 0, 0, 0, 0, 0, 30, 255}, 
			{0, 3, 0, 0, 0, 0, 0, 20, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 50, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}, 
			{0, 0, 0, 0, 0, 0, 0, 0, 255}
	};
	
	/** 40 普通攻击防御的描述索引表，见 {@link #a} ，如248-253为“用兵刃架住”等描述 */
	static final int e[] = new int[] {
		0, 8, 14, 23, 28, 34, 41, 48, 56, 62, 
		70, 76, 81, 87, 90, 98, 101, 121, 123, 141, 
		146, 152, 158, 162, 172, 180, 186, 192, 203, 208, 
		217, 222, 228, 233, 236, 239, 242, 243, 248, 253
	};

	/**
	 * (size:16) 身法的描述检索表（二元数组，[0]技能ID-见 {@link #skill_name} ，[1]描述表的索引-见
	 * {@link #e}）
	 */
	static final int shenfa_desc[] = new int[] {
		15, 3, 16, 4, 21, 8, 28, 12, 33, 19, 
		35, 21, 40, 28, 46, 30
	};

	/**
	 * (size:52) 技能普通攻击的描述检索表（二元数组，[0]技能ID-见 {@link #skill_name} ，[1]描述表的索引-见
	 * {@link #e}）
	 */
	static final int gongji_desc[] = new int[] {
		11, 0, 12, 1, 13, 2, 17, 5, 18, 6, 
		19, 7, 23, 9, 24, 10, 27, 11, 29, 14, 
		30, 16, 31, 18, 34, 20, 37, 22, 38, 23, 
		39, 24, 41, 25, 42, 26, 43, 27, 44, 29, 
		47, 31, 1, 32, 3, 33, 2, 34, 5, 35, 
		6, 36
	};

	
	static String GetAttackDesc(int id) {
		if (id < 0 || id > 252) {
			return "";
		}
		return Res.readtext(2, attack_desc[id], attack_desc[id + 1]);
	}

	static String GetHitDesc(int id) {
		if (id < 0 || id > 57) {
			return "";
		}

		return Res.readtext(4, hit_desc[id], hit_desc[id + 1]);
	}
}
