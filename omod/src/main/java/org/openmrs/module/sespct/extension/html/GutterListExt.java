package org.openmrs.module.sespct.extension.html;

import org.openmrs.module.Extension.MEDIA_TYPE;
import org.openmrs.module.web.extension.LinkExt;

public class GutterListExt extends LinkExt {
	
	@Override
	public String getLabel() {
		return "CT-Interoperabilidade";
	}
	
	@Override
	public String getUrl() {
		return "module/sespct/sespct.form";
	}
	
	@Override
	public String getRequiredPrivilege() {
		return "App: sespct.app";
	}
	
	@Override
	public MEDIA_TYPE getMediaType() {
		return MEDIA_TYPE.html;
	}
}
