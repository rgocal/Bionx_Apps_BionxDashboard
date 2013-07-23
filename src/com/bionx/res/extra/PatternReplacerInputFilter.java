package com.bionx.res.extra;

import android.text.InputFilter;
import android.text.Spanned;

public class PatternReplacerInputFilter implements InputFilter {
	private String pattern;

	public PatternReplacerInputFilter(String pattern) {
		if (pattern == null || pattern.trim().length() == 0) {
			throw new IllegalArgumentException("Invalid input pattern: "
					+ pattern);
		}
		this.pattern = pattern;
	}

	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		return source.subSequence(start, end).toString()
				.replaceAll(pattern, "");
	}
}
