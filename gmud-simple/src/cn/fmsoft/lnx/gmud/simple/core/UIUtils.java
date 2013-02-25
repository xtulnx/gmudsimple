package cn.fmsoft.lnx.gmud.simple.core;

/**
 * UI方面通用模块，如列表菜单
 * 
 * @author nxliao
 * 
 */
class UIUtils {

	/**
	 * 竖向菜单
	 * 
	 * @param title
	 *            标题
	 * @param max
	 *            总项数
	 * @param vCount
	 *            可见项数
	 * @param callbackID
	 *            当按 Back 键回调的ID
	 */
	static int ShowMenu(String title[], int count, int vCount, final int x,
			final int y, final int w, final int callbackID) {
		final int lineH = Video.SMALL_LINE_H;
		final int h = lineH * vCount + 2;
		int top = 0, sel = 0;
		boolean update = true;
		Input.ClearKeyStatus();
		int ret = 0;
		while (true) {
			Input.ProcessMsg();
			if ((Input.inputstatus & Input.kKeyUp) != 0) {
				if (sel > 0) {
					sel--;
				} else if (top > 0) {
					top--;
				} else if (count < vCount) {
					top = 0;
					sel = count - 1;
				} else {
					top = count - vCount;
					sel = vCount - 1;
				}
				update = true;
			} else if ((Input.inputstatus & Input.kKeyDown) != 0) {
				if (top + sel >= count - 1) {
					top = sel = 0;
				} else if (sel < vCount - 1) {
					sel++;
				} else {
					top++;
				}
				update = true;
			} else if ((Input.inputstatus & Input.kKeyExit) != 0) {
				break;
			} else if ((Input.inputstatus & Input.kKeyEnt) != 0) {
				ret = UI.onMenuCallBack(callbackID, top + sel);
				if (ret != 0)
					break;
				update = true;
			}

			if (update) {
				Video.VideoClearRect(x, y, w, h);
				Video.VideoDrawRectangle(x, y, w, h);
				final int oy = (lineH - 11) / 2;
				for (int i = 0, ty = y, pos = top; i < vCount && pos < count; i++, pos++, ty += lineH) {
					Video.VideoDrawStringSingleLine(title[pos], x
							+ Video.SMALL_FONT_SIZE, ty);
					if (i == sel)
						UI.DrawCursor(x + 3, ty + oy);
				}
				Video.VideoUpdate();
			}
			Input.ClearKeyStatus();
			Gmud.GmudDelay(50);
		}
		return ret;
	}

}
