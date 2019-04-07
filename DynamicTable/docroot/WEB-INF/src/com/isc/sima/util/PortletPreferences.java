/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.isc.sima.util;

import javax.el.ELResolver;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.*;
import javax.portlet.faces.preference.Preference;
import java.util.Enumeration;
import java.util.Map;

public class PortletPreferences {

	/**
	 * Resets/restores the values in the portletPreferences.xhtml Facelet composition with portlet preference default
	 * values.
	 */
	public void reset() {


	}


	public void submit(String preferenceName) {

        FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		String elExpression = "mutablePortletPreferencesValues";
		ELResolver elResolver = facesContext.getApplication().getELResolver();
		@SuppressWarnings("unchecked")
		Map<String, Preference> mutablePreferenceMap = (Map<String, Preference>) elResolver.getValue(
				facesContext.getELContext(), null, elExpression);

		// Get a list of portlet preference names.
		PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
		javax.portlet.PortletPreferences portletPreferences = portletRequest.getPreferences();
		Enumeration<String> preferenceNames = portletPreferences.getNames();

		try {

			// For each portlet preference name:
			if (preferenceNames.hasMoreElements()) {

				// Get the value specified by the user.

				String preferenceValue = mutablePreferenceMap.get(preferenceName).getValue();

				// Prepare to save the value.
				if (!portletPreferences.isReadOnly(preferenceName)) {
					portletPreferences.setValue(preferenceName, preferenceValue);
				}
			}

			// Save the preference values.
			portletPreferences.store();
			// Report a successful message back to the user as feedback.
			FacesMessageUtil.addGlobalSuccessInfoMessage(facesContext);
		}
		catch (Exception e) {
			FacesMessageUtil.addGlobalUnexpectedErrorMessage(facesContext);
		}
	}
}
