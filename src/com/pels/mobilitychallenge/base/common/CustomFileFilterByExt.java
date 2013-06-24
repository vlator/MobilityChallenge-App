package com.pels.mobilitychallenge.base.common;

import java.io.File;
import java.io.FileFilter;

public class CustomFileFilterByExt implements FileFilter {
	private String ext;

	public CustomFileFilterByExt(String ext) {
		this.ext = "." + ext;
	}

	@Override
	public boolean accept(File f) {
		return f.isFile() && !f.isHidden()
				&& f.getName().toLowerCase().endsWith(ext);
	}

}
