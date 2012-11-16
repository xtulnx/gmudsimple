package cn.fmsoft.lnx.gmud.simple.core;

public class Items {

	static final String GetItemDesc(int id) {
		if (id < 0 || id > 91) {
			return "";
		}
		if (id == 77) {
			String desc = "这是一件你自制的兵器,靠近手柄处写了一行小字:";
			desc += item_names[77];
			return desc;
		}
		return Res.readtext(0, item_desc[id], item_desc[1 + id]);
	}

	// [93]
	static final int item_desc[] = new int[]{  //item Desc point
			0, 0, 53, 93, 143, 174, 215, 255, 285, 357, 
			440, 520, 575, 658, 697, 777, 877, 907, 977, 1010, 
			1067, 1100, 1130, 1151, 1269, 1340, 1445, 1532, 1622, 1720, 
			1793, 1874, 1967, 2025, 2068, 2121, 2168, 2217, 2307, 2369, 
			2474, 2537, 2567, 2591, 2618, 2698, 2774, 2804, 2834, 2864, 
			2891, 2977, 3032, 3105, 3188, 3243, 3310, 3340, 3404, 3459, 
			3505, 3589, 3632, 3659, 3692, 3775, 3836, 3863, 3915, 4020, 
			4137, 4238, 4335, 4397, 4454, 4484, 4523, 4566, 4578, 4590, 
			4590, 4590, 4590, 4590, 4590, 4590, 4590, 4590, 4590, 4590, 
			4590, 4590, 4590
		};

	/** 0食物，0，+food，+water，0，0，cost */
	final static int ITEM_TYPE_FOOD = 0;
	/** 1药物，type，+effect1，0，0，0，cost */
	final static int ITEM_TYPE_DRUG = 1;
	/** 2武器，type，+attack，+命中，+回避，0，cost  (255=-1;254=-2;253=-3)(weapon type 1刀 6剑 7杖 9鞭) */
	final static int ITEM_TYPE_WEAPON = 2;
	/**
	 * 3装备，type，+defend，+命中，+回避，0，cost (equip type 0饰品 2衣 3背心/披风 5腰带 9钓竿 10鞋)
	 * (defend 原始数据类型为 char，转成了 unsigned char, 所以 >127 的值是负数)
	 */
	final static int ITEM_TYPE_EQUIP = 3;
	/** 4书，0，type，require HP，max_level，require EXP，cost */
	final static int ITEM_TYPE_BOOK = 4;
	/** 5其他，0，0，0，0，0，cost */
	final static int ITEM_TYPE_OTHOR = 5;
	
	// [92][7]
	/**
	 * 原始数量是 8字节 数组<br/>
	 * type，，effect1，，，，cost
	 * 
	 * @see Items#ITEM_TYPE_FOOD
	 * @see Items#ITEM_TYPE_DRUG
	 * @see Items#ITEM_TYPE_WEAPON
	 * @see Items#ITEM_TYPE_EQUIP
	 * @see Items#ITEM_TYPE_BOOK
	 * @see Items#ITEM_TYPE_OTHOR
	 */
	static final int item_attribs[][] = new int[][] {
	  //item effects
		//type，，effect1，，，，cost
		//0食物，0，+food，+water，0，0，cost
		//1药物，type，+effect1，0，0，0，cost
		//2武器，type，+attack，+命中，+回避，0，cost  (255=-1;254=-2;253=-3)(weapon type 1刀 6剑 7杖 9鞭)
		//3装备，type，+defend，+命中，+回避，0，cost  (equip type 0饰品 2衣 3背心/披风 5腰带 9钓竿 10鞋)
		//4书，   0，   type，require HP，max_level，require EXP，cost
		//5其他，0，0，0，0，0，cost
		{0, 0, 0, 0, 0, 0,0},

		{0, 0, 160, 0, 0, 0,30},

		{0, 0, 120, 0, 0, 0,20},

		{0, 0, 180, 200, 0, 0,150},

		{0, 0, 160, 0, 0, 0,55},

		{0, 0, 240, 0, 0, 0,100},

		{0, 0, 60, 0, 0, 0,50},

		{0, 0, 80, 0, 0, 0,35},

		{1, 0, 50, 0, 0, 0,2000},

		{1, 0, 80, 0, 0, 0,3000},

		{1, 1, 1, 0, 0, 0,3000},

		{2, 1, 25, 0, 255, 0,600},

		{2, 1, 17, 0, 0, 0,100},

		{2, 6, 5, 0, 0, 0,50},

		{2, 6, 15, 0, 0, 0,1500},

		{2, 1, 55, 5, 253, 0,30000},

		{2, 1, 25, 0, 0, 0,1000},

		{2, 6, 25, 0, 255, 0,600},

		{2, 6, 10, 0, 0, 0,70},

		{2, 9, 1, 0, 0, 0,10},

		{2, 6, 15, 0, 0, 0,1000},

		{2, 7, 28, 0, 254, 0,700},

		{2, 9, 20, 0, 0, 0,500},

		{2, 1, 35, 3, 0, 0,800},

		{2, 1, 55, 0, 254, 0,20000},

		{2, 9, 70, 0, 254, 0,34464},

		{2, 9, 35, 0, 0, 0,20000},

		{2, 1, 40, 0, 255, 0,20000},

		{2, 9, 55, 0, 255, 0,40000},

		{2, 6, 15, 0, 0, 0,5500},

		{2, 1, 35, 0, 0, 0,9464},

		{2, 1, 55, 0, 254, 0,50000},

		{2, 7, 35, 0, 0, 0,5000},

		{2, 6, 65, 0, 0, 0,7000},

		{2, 7, 20, 0, 0, 0,1000},

		{2, 6, 30, 0, 0, 0,1000},

		{2, 6, 45, 0, 0, 0,10000},

		{2, 6, 60, 0, 0, 0,30000},

		{2, 6, 30, 0, 0, 0,700},

		{3, 2, 45, 0, 3, 0,50000},

		{3, 0, 0, 0, 0, 0,100},

		{3, 0, 0, 0, 0, 0,20},

		{3, 2, 5, 0, 0, 0,8},

		{3, 2, 15, 0, 0, 0,500},

		{3, 3, 15, 0, 255, 0,1000},

		{3, 2, 10, 0, 0, 0,600},

		{3, 0, 0, 0, 0, 0,20},

		{3, 10, 5, 0, 0, 0,300},

		{3, 0, 0, 0, 0, 0,20},

		{3, 0, 0, 0, 0, 0,40},

		{3, 2, 8, 0, 0, 0,100},

		{3, 10, 10, 0, 3, 0,300},

		{3, 2, 0, 0, 10, 0,300},

		{3, 2, 0, 0, 6, 0,14464},

		{3, 2, 10, 0, 5, 0,1500},

		{3, 2, 1, 0, 0, 0,10},

		{3, 0, 3, 3, 0, 0,100},

		{3, 4, 5, 0, 2, 0,2000},

		{3, 2, 5, 5, 0, 0,500},

		{3, 2, 5, 0, 0, 0,300},

		{3, 2, 15, 0, 0, 0,400},

		{3, 2, 4, 0, 0, 0,600},

		{3, 3, 60, 0, 251, 0,10000},

		{3, 3, 10, 0, 5, 0,2000},

		{3, 2, 10, 0, 8, 0,3000},

		{3, 2, 4, 0, 0, 0,600},

		{3, 3, 45, 0, 253, 0,5000},

		{3, 3, 40, 0, 0, 0,5000},

		{4, 0, 1, 20, 60, 3000,500},

		{4, 0, 1, 15, 10, 0,150},

		{4, 0, 3, 20, 50, 1000,150},

		{4, 0, 0, 25, 55, 2000,100},

		{5, 0, 0, 0, 0, 0,50},

		{3, 9, 0, 0, 0, 0,300},

		{0, 0, 250, 0, 0, 0,250},

		{3, 5, 2, 0, 2, 0,5000},

		{5, 0, 0, 0, 0, 0,100},

		{2, 0, 0, 0, 0, 0,100},

		{3, 0, 0, 0, 0, 0,100},

		{5, 0, 0, 0, 0, 0,100},

		{5, 0, 0, 0, 0, 0,100},

		{5, 0, 0, 0, 0, 0,100},

		{5, 0, 0, 0, 0, 0,100},

		{5, 0, 0, 0, 0, 0,100},

		{5, 0, 0, 0, 0, 0,100},

		{5, 0, 0, 0, 0, 0,100},

		{5, 0, 0, 0, 0, 0,100},

		{5, 0, 0, 0, 0, 0,100},

		{1, 1, 1, 0, 0, 0,3000},

		{3, 3, 70, 0, 0, 0,5000},

		{5, 0, 0, 0, 0, 0,100},

		{5, 0, 0, 0, 0, 0,100}
		};

	// [92]
	static final String item_names[] = new String[]{
		"none", "烤鸡腿", "包子", "酥油茶", "翡翠豆腐", "猪肉", "糖葫芦", "白玉豆腐", "金创药", "生肌膏", 
		"海外仙丹", "钢刀", "菜刀", "匕首", "檀香扇", "护手钩", "杀猪刀", "长剑", "缝衣针", "麻绳", 
		"园工剪", "钢杖", "长鞭", "鬼头刀", "紫金刀", "梨花鞭", "拂尘", "柳玉刀", "红拂", "白玉萧", 
		"花碟扇", "河豚毒", "精钢杖", "青锋剑", "铁拐", "铁剑", "楚妃剑", "凝碧剑", "细剑", "黑衣", 
		"老花镜", "绿牡丹", "布衣", "精制布衣", "皮背心", "粉红绸衫", "红杜鹃", "绣花小鞋", "白玫瑰", "茶花", 
		"轻花绸衫", "洒花缎鞋", "宝蓝缎衫", "夜行衣", "绿水罗衣", "轻纱长裙", "黑眼罩", "披风", "武道服", "道袍", 
		"白罗袍", "绸袍", "金锁子甲", "降魔袍", "霓虹羽衣", "雪山白袍", "亮银甲", "豹皮", "拳经", "焦黄纸页", 
		"惊天刀谱", "手抄本", "毛笔", "钓杆", "鲤鱼", "牛皮束带", "鱼篓", "自制武器属性", "自制装备", "青龙坛地图", 
		"地罡坛地图", "朱雀坛地图", "山岚坛地图", "玄武坛地图", "紫煞坛地图", "天微坛地图", "白虎坛地图", "石料", "王蛇胆", "王蛇皮", 
		"兽王令牌", "茅山令牌"
		};

	// 
	/** (size:[92]) 物品是否可叠加 */
	static final int item_repeat[] = new int[]{
		0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
		0, 0
	};
}
