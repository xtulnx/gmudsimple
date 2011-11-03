package cn.fmsoft.lnx.gmud.simple.core;

public class util {

	static int count_per_millisecond = 1000;

	// 替换字符串target中的obj为data
	static String ReplaceStr(String target, String obj, String data) {
		return target.replaceAll(obj, data);
	}
	/*
	 * wstring aa(L"$o睁大眼睛望着你，显然不知道你在说什??。$o打了个哈哈：今天的天气真是，哈哈。"); WCHAR ss[255];
	 * wcscpy(ss,ReplaceStr(aa,L"$o",L"1010").c_str());
	 */

	
	// 产生不大于max的随机int
	static int RandomInt(int max) {
		if (1 > max)
			return 0;
		// srand((unsigned)time(NULL));
		// return rand() % max;

		return (int) (Math.random() * max);
	}

	static boolean RandomBool(int precent) {
		if (precent <= 0)
			return false;
		if (precent >= 100)
			return true;

		// srand((unsigned)time(NULL));
		// return (rand() % 1000 + 1) < 10 * precent;

		return (Math.random() * 100) < precent;
	}

	static long GetCycleCount() {
		// __asm _emit 0x0F
		// __asm _emit 0x31
		return System.currentTimeMillis();
	}

	static void GetSecondTime() {
		// INT64 start = GetCycleCount();
		// Input::ProcessMsg();
		// Sleep(500);
		// Input::ProcessMsg();
		// Sleep(500);
		// Input::ProcessMsg();
		// Sleep(500);
		// Input::ProcessMsg();
		// count_per_millisecond = (GetCycleCount() - start)/1500;
	}

	// 延时time毫秒
	static void GmudDelay(int time) {
		// TODO: I NEED UPDATA!
		// INT64 t = count_per_millisecond * time + GetCycleCount();
		// while(t > GetCycleCount())
		// {
		// Input::ProcessMsg();
		// if(!Input::Running)
		// exit(0);
		// Sleep(10);
		// }
		// Video::VideoUpdate();
	}
}
