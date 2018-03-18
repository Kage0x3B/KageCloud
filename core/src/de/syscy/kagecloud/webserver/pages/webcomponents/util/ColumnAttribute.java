package de.syscy.kagecloud.webserver.pages.webcomponents.util;

public enum ColumnAttribute {
	//@formatter:off
	XS_1, XS_2, XS_3, XS_4, XS_5, XS_6, XS_7, XS_8, XS_9, XS_10, XS_11, XS_12,
	SM_1, SM_2, SM_3, SM_4, SM_5, SM_6, SM_7, SM_8, SM_9, SM_10, SM_11, SM_12,
	MD_1, MD_2, MD_3, MD_4, MD_5, MD_6, MD_7, MD_8, MD_9, MD_10, MD_11, MD_12,
	LG_1, LG_2, LG_3, LG_4, LG_5, LG_6, LG_7, LG_8, LG_9, LG_10, LG_11, LG_12,
	XS_OFFSET_1, XS_OFFSET_2, XS_OFFSET_3, XS_OFFSET_4, XS_OFFSET_5, XS_OFFSET_6,
	XS_OFFSET_7, XS_OFFSET_8, XS_OFFSET_9, XS_OFFSET_10, XS_OFFSET_11, XS_OFFSET_12,
	SM_OFFSET_1, SM_OFFSET_2, SM_OFFSET_3, SM_OFFSET_4, SM_OFFSET_5, SM_OFFSET_6,
	SM_OFFSET_7, SM_OFFSET_8, SM_OFFSET_9, SM_OFFSET_10, SM_OFFSET_11, SM_OFFSET_12,
	MD_OFFSET_1, MD_OFFSET_2, MD_OFFSET_3, MD_OFFSET_4, MD_OFFSET_5, MD_OFFSET_6,
	MD_OFFSET_7, MD_OFFSET_8, MD_OFFSET_9, MD_OFFSET_10, MD_OFFSET_11, MD_OFFSET_12,
	LG_OFFSET_1, LG_OFFSET_2, LG_OFFSET_3, LG_OFFSET_4, LG_OFFSET_5, LG_OFFSET_6,
	LG_OFFSET_7, LG_OFFSET_8, LG_OFFSET_9, LG_OFFSET_10, LG_OFFSET_11, LG_OFFSET_12;
	//@formatter:on

	public String getCSSClass() {
		return "col-" + name().toLowerCase().replaceAll("_", "-");
	}

	@Override
	public String toString() {
		return getCSSClass();
	}
}